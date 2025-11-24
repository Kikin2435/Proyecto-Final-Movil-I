package com.example.proyectomovil.ui.ViewModel.Tarea

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomovil.data.Repository.ArchivosMultimediaRepository
import com.example.proyectomovil.data.Repository.TareaRepository
import com.example.proyectomovil.data.model.ArchivosMultimedia
import com.example.proyectomovil.data.model.Tarea
import com.example.proyectomovil.ui.notificaciones.AlarmaScheduler
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TareaViewModel(
    savedStateHandle: SavedStateHandle,
    private val tareaRepository: TareaRepository,
    private val archivosMultimediaRepository: ArchivosMultimediaRepository,
    private val alarmaScheduler: AlarmaScheduler // Inyectado
) : ViewModel() {

    var tareaUiState by mutableStateOf(TareaUiState())
        private set

    private val tareaId: Int? = savedStateHandle["tareaId"]

    init {
        if (tareaId != null && tareaId != -1) {
            viewModelScope.launch {
                val tarea = tareaRepository.obtenerTareaStream(tareaId).filterNotNull().first()
                val archivos = archivosMultimediaRepository.obtenerArchivosPorTarea(tareaId).filterNotNull().first()
                tareaUiState = tarea.toTareaUiState().copy(
                    archivosUri = archivos.map { it.uri }
                )
            }
        }
    }

    fun actualizarUiState(nuevoDetalle: TareaUiState) {
        tareaUiState = nuevoDetalle.copy(
            isEntryValid = nuevoDetalle.titulo.isNotBlank()
        )
    }

    fun removerArchivoUri(uri: String) {
        val currentUris = tareaUiState.archivosUri.toMutableList()
        currentUris.remove(uri)
        actualizarUiState(tareaUiState.copy(archivosUri = currentUris))
    }

    fun guardarTarea() {
        if (!tareaUiState.isEntryValid) return

        viewModelScope.launch {
            try {
                val tarea = tareaUiState.toTarea()
                var tareaGuardada: Tarea

                if (tarea.id == 0) { // Tarea nueva
                    val nuevoId = tareaRepository.insertarTarea(tarea)
                    tareaGuardada = tarea.copy(id = nuevoId.toInt())
                    guardarArchivos(nuevoId.toInt())
                } else { // Tarea existente
                    // Primero cancelamos cualquier alarma anterior
                    alarmaScheduler.cancel(tarea)
                    tareaRepository.actualizarTarea(tarea)
                    tareaGuardada = tarea
                    archivosMultimediaRepository.eliminarArchivosPorTarea(tarea.id)
                    guardarArchivos(tarea.id)
                }

                // Programamos la nueva alarma si es necesario
                alarmaScheduler.schedule(tareaGuardada)

            } catch (e: Exception) {
                Log.e("GUARDAR_VM", "Error al guardar la tarea y/o sus archivos", e)
            }
        }
    }

    private suspend fun guardarArchivos(idTarea: Int) {
        tareaUiState.archivosUri.forEach {
            archivosMultimediaRepository.insertarArchivo(
                ArchivosMultimedia(
                    uri = it,
                    tipo = "archivo",
                    tareaIdAsociada = idTarea,
                    notaIdAsociada = null
                )
            )
        }
    }
}

data class TareaUiState(
    val id: Int = 0,
    val titulo: String = "",
    val contenido: String = "",
    val fechaRecordatorio: Long? = null,
    val fotoUri: String? = null,
    val archivosUri: List<String> = emptyList(),
    val isEntryValid: Boolean = false
)

fun TareaUiState.toTarea(): Tarea = Tarea(
    id = id,
    titulo = titulo,
    contenido = contenido,
    fechaRecordatorio = fechaRecordatorio,
    fotoUri = fotoUri
)

fun Tarea.toTareaUiState(): TareaUiState = TareaUiState(
    id = id,
    titulo = titulo,
    contenido = contenido,
    fechaRecordatorio = fechaRecordatorio,
    fotoUri = fotoUri,
    isEntryValid = titulo.isNotBlank()
)
