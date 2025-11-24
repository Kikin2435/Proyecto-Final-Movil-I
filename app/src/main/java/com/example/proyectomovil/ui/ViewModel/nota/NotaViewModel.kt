package com.example.proyectomovil.ui.ViewModel.nota

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomovil.data.Repository.ArchivosMultimediaRepository
import com.example.proyectomovil.data.Repository.NotaRepository
import com.example.proyectomovil.data.model.ArchivosMultimedia
import com.example.proyectomovil.data.model.Nota
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NotaViewModel(
    savedStateHandle: SavedStateHandle,
    private val notaRepository: NotaRepository,
    private val archivosMultimediaRepository: ArchivosMultimediaRepository // Inyectado
) : ViewModel() {

    var notaUiState by mutableStateOf(NotaUiState())
        private set

    private val notaId: Int? = savedStateHandle["notaId"]

    init {
        if (notaId != null && notaId != -1) {
            viewModelScope.launch {
                val nota = notaRepository.obtenerNotaStream(notaId).filterNotNull().first()
                val archivos = archivosMultimediaRepository.obtenerArchivosPorNota(notaId).filterNotNull().first()
                notaUiState = nota.toNotaUiState().copy(
                    archivosUri = archivos.map { it.uri }
                )
            }
        }
    }

    fun actualizarUiState(nuevosDetalles: NotaUiState) {
        notaUiState = nuevosDetalles.copy(isEntryValid = nuevosDetalles.titulo.isNotBlank())
    }

    fun removerArchivoUri(uri: String) {
        val currentUris = notaUiState.archivosUri.toMutableList()
        currentUris.remove(uri)
        actualizarUiState(notaUiState.copy(archivosUri = currentUris))
    }

    fun guardarNota() {
        if (!notaUiState.isEntryValid) return

        viewModelScope.launch {
            try {
                val nota = notaUiState.toNota()
                if (nota.id == 0) { // Nota nueva
                    val nuevoId = notaRepository.insertarNota(nota)
                    guardarArchivos(nuevoId.toInt())
                } else { // Nota existente
                    notaRepository.actualizarNota(nota)
                    archivosMultimediaRepository.eliminarArchivosPorNota(nota.id)
                    guardarArchivos(nota.id)
                }
            } catch (e: Exception) {
                Log.e("GUARDAR_VM", "Error al guardar la nota y/o sus archivos", e)
            }
        }
    }

    private suspend fun guardarArchivos(idNota: Int) {
        notaUiState.archivosUri.forEach {
            archivosMultimediaRepository.insertarArchivo(
                ArchivosMultimedia(
                    uri = it,
                    tipo = "archivo",
                    notaIdAsociada = idNota,
                    tareaIdAsociada = null
                )
            )
        }
    }
}

data class NotaUiState(
    val id: Int = 0,
    val titulo: String = "",
    val contenido: String = "",
    val fotoUri: String? = null,
    val archivosUri: List<String> = emptyList(),
    val isEntryValid: Boolean = false
)

fun NotaUiState.toNota(): Nota = Nota(
    id = id,
    titulo = titulo,
    contenido = contenido,
    fotoUri = fotoUri
)

fun Nota.toNotaUiState(): NotaUiState = NotaUiState(
    id = id,
    titulo = titulo,
    contenido = contenido,
    fotoUri = fotoUri,
    isEntryValid = titulo.isNotBlank()
)
