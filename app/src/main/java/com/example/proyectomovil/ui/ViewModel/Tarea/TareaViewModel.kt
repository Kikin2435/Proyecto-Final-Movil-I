package com.example.proyectomovil.ui.ViewModel.Tarea

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.proyectomovil.data.Repository.ArchivosMultimediaRepository
import com.example.proyectomovil.data.Repository.TareaRepository
import com.example.proyectomovil.data.model.ArchivosMultimedia
import com.example.proyectomovil.data.model.Tarea
import com.example.proyectomovil.ui.notificaciones.AlarmaScheduler
import com.example.proyectomovil.util.AudioRecorder
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TareaViewModel(
    application: Application, // <-- Añadido
    savedStateHandle: SavedStateHandle,
    private val tareaRepository: TareaRepository,
    private val archivosMultimediaRepository: ArchivosMultimediaRepository,
    private val alarmaScheduler: AlarmaScheduler
) : AndroidViewModel(application) { // <-- Cambiado

    var tareaUiState by mutableStateOf(TareaUiState())
        private set

    val audioRecorder: AudioRecorder = AudioRecorder(application) // <-- Añadido

    private val tareaId: Int? = savedStateHandle["tareaId"]

    init {
        if (tareaId != null && tareaId != -1) {
            viewModelScope.launch {
                val tarea = tareaRepository.obtenerTareaStream(tareaId).filterNotNull().first()
                val archivos = archivosMultimediaRepository.obtenerArchivosPorTarea(tareaId).filterNotNull().first()
                tareaUiState = tarea.toTareaUiState(archivos)
            }
        }
    }

    fun actualizarUiState(nuevoDetalle: TareaUiState) {
        tareaUiState = nuevoDetalle.copy(
            isEntryValid = nuevoDetalle.titulo.isNotBlank()
        )
    }

    fun removerArchivo(archivo: ArchivosMultimedia) {
        val archivosActualizados = tareaUiState.archivos.toMutableList()
        archivosActualizados.remove(archivo)
        actualizarUiState(tareaUiState.copy(archivos = archivosActualizados))
    }

    fun guardarTarea() {
        if (!tareaUiState.isEntryValid) return

        viewModelScope.launch {
            try {
                val tarea = tareaUiState.toTarea()
                var tareaGuardada: Tarea

                if (tarea.id == 0) { 
                    val nuevoId = tareaRepository.insertarTarea(tarea)
                    tareaGuardada = tarea.copy(id = nuevoId.toInt())
                    guardarArchivos(nuevoId.toInt())
                } else { 
                    alarmaScheduler.cancel(tarea)
                    tareaRepository.actualizarTarea(tarea)
                    tareaGuardada = tarea
                    archivosMultimediaRepository.eliminarArchivosPorTarea(tarea.id)
                    guardarArchivos(tarea.id)
                }

                if (tareaGuardada.fechaRecordatorio != null) {
                    alarmaScheduler.schedule(tareaGuardada)
                }

            } catch (e: Exception) {
                Log.e("GUARDAR_TAREA_VM", "Error al guardar la tarea y/o sus archivos", e)
            }
        }
    }

    private suspend fun guardarArchivos(idTarea: Int) {
        tareaUiState.archivos.forEach { archivo ->
            archivosMultimediaRepository.insertarArchivo(archivo.copy(tareaIdAsociada = idTarea))
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioRecorder.stop()
    }
}

data class TareaUiState(
    val id: Int = 0,
    val titulo: String = "",
    val contenido: String = "",
    val fechaRecordatorio: Long? = null,
    val fotoUri: String? = null,
    val archivos: List<ArchivosMultimedia> = emptyList(),
    val isEntryValid: Boolean = false
)

fun Tarea.toTareaUiState(archivos: List<ArchivosMultimedia> = emptyList()): TareaUiState = TareaUiState(
    id = id,
    titulo = titulo,
    contenido = contenido,
    fechaRecordatorio = fechaRecordatorio,
    fotoUri = fotoUri,
    archivos = archivos,
    isEntryValid = titulo.isNotBlank()
)

fun TareaUiState.toTarea(): Tarea = Tarea(
    id = id,
    titulo = titulo,
    contenido = contenido,
    fechaRecordatorio = fechaRecordatorio,
    fotoUri = fotoUri
)
