package com.example.proyectomovil.ui.ViewModel.nota

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.proyectomovil.data.Repository.ArchivosMultimediaRepository
import com.example.proyectomovil.data.Repository.NotaRepository
import com.example.proyectomovil.data.model.ArchivosMultimedia
import com.example.proyectomovil.data.model.Nota
import com.example.proyectomovil.util.AudioRecorder
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NotaViewModel(
    application: Application, 
    savedStateHandle: SavedStateHandle,
    private val notaRepository: NotaRepository,
    private val archivosMultimediaRepository: ArchivosMultimediaRepository,
) : AndroidViewModel(application) { 

    var notaUiState by mutableStateOf(NotaUiState())
        private set

    val audioRecorder: AudioRecorder = AudioRecorder(application)

    private val notaId: Int? = savedStateHandle["notaId"]

    init {
        if (notaId != null && notaId != -1) {
            viewModelScope.launch {
                val nota = notaRepository.obtenerNotaStream(notaId).filterNotNull().first()
                val archivos = archivosMultimediaRepository.obtenerArchivosPorNota(notaId).filterNotNull().first()
                notaUiState = nota.toNotaUiState(archivos)
            }
        }
    }

    fun actualizarUiState(nuevoDetalle: NotaUiState) {
        notaUiState = nuevoDetalle.copy(
            isEntryValid = nuevoDetalle.titulo.isNotBlank()
        )
    }

    fun removerArchivo(archivo: ArchivosMultimedia) {
        val archivosActualizados = notaUiState.archivos.toMutableList()
        archivosActualizados.remove(archivo)
        actualizarUiState(notaUiState.copy(archivos = archivosActualizados))
    }

    fun guardarNota() {
        if (!notaUiState.isEntryValid) return

        viewModelScope.launch {
            try {
                val nota = notaUiState.toNota()
                if (nota.id == 0) {
                    val nuevoId = notaRepository.insertarNota(nota)
                    guardarArchivos(nuevoId.toInt())
                } else {
                    notaRepository.actualizarNota(nota)
                    archivosMultimediaRepository.eliminarArchivosPorNota(nota.id)
                    guardarArchivos(nota.id)
                }
            } catch (e: Exception) {
                Log.e("GUARDAR_NOTA_VM", "Error al guardar la nota y/o sus archivos", e)
            }
        }
    }

    private suspend fun guardarArchivos(idNota: Int) {
        notaUiState.archivos.forEach { archivo ->
            archivosMultimediaRepository.insertarArchivo(archivo.copy(notaIdAsociada = idNota))
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioRecorder.stop()
    }
}

data class NotaUiState(
    val id: Int = 0,
    val titulo: String = "",
    val contenido: String = "",
    val fotoUri: String? = null,
    val archivos: List<ArchivosMultimedia> = emptyList(),
    val isEntryValid: Boolean = false
)

fun Nota.toNotaUiState(archivos: List<ArchivosMultimedia> = emptyList()): NotaUiState = NotaUiState(
    id = id,
    titulo = titulo,
    contenido = contenido,
    fotoUri = fotoUri,
    archivos = archivos,
    isEntryValid = titulo.isNotBlank()
)

fun NotaUiState.toNota(): Nota = Nota(
    id = id,
    titulo = titulo,
    contenido = contenido,
    fotoUri = fotoUri,
)