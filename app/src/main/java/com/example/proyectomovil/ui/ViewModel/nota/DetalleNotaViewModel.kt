package com.example.proyectomovil.ui.ViewModel.nota

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomovil.data.Repository.ArchivosMultimediaRepository
import com.example.proyectomovil.data.Repository.NotaRepository
import com.example.proyectomovil.data.model.ArchivosMultimedia
import com.example.proyectomovil.data.model.Nota
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DetalleNotaViewModel(
    savedStateHandle: SavedStateHandle,
    private val notaRepository: NotaRepository,
    private val archivosMultimediaRepository: ArchivosMultimediaRepository // Inyectado
) : ViewModel() {

    private val notaId: Int = checkNotNull(savedStateHandle["notaId"])

    val uiStateDetalle: StateFlow<NotaUiStateDetalle> =
        notaRepository.obtenerNotaStream(notaId)
            .filterNotNull()
            .flatMapLatest { nota ->
                archivosMultimediaRepository.obtenerArchivosPorNota(nota.id)
                    .map { archivos ->
                        NotaUiStateDetalle(nota = nota, archivos = archivos)
                    }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = NotaUiStateDetalle()
            )

    fun eliminarNota() {
        viewModelScope.launch {
            uiStateDetalle.value.nota?.let {
                notaRepository.eliminarNota(it)
                archivosMultimediaRepository.eliminarArchivosPorNota(it.id)
            }
        }
    }
}

data class NotaUiStateDetalle(
    val nota: Nota? = null,
    val archivos: List<ArchivosMultimedia> = emptyList()
)
