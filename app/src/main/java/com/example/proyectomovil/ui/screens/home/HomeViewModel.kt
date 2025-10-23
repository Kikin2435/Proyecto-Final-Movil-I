package com.example.proyectomovil.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomovil.data.NotaRepository
import com.example.proyectomovil.data.model.Nota
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val notaRepository: NotaRepository): ViewModel() {
    val homeUiState: StateFlow<HomeUiState> = notaRepository.obtenerTodasStream()
        .map { HomeUiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = HomeUiState()
        )

    fun eliminarNota(nota: Nota){
        viewModelScope.launch {
            notaRepository.eliminarNota(nota)
        }
    }
}

data class HomeUiState(
    val listaDeNotas: List<Nota> = emptyList()
)