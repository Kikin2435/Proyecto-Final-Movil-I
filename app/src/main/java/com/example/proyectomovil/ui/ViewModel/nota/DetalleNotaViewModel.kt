package com.example.proyectomovil.ui.ViewModel.nota

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomovil.data.Repository.NotaRepository
import com.example.proyectomovil.data.model.Nota
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DetalleNotaViewModel(
    savedStateHandle: SavedStateHandle,
    notaRepository: NotaRepository
) : ViewModel() {

    private val notaId: Int = checkNotNull(savedStateHandle["notaId"])

    val uiStateDetalle: StateFlow<NotaUiStateDetalle> =
        notaRepository.obtenerNotaStream(notaId)
            .filterNotNull()
            .map { nota ->
                NotaUiStateDetalle(nota = nota)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = NotaUiStateDetalle()
            )
}

data class NotaUiStateDetalle(
    val nota: Nota? = null
)
