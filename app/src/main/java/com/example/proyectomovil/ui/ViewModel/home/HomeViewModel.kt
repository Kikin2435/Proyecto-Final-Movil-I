package com.example.proyectomovil.ui.ViewModel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomovil.data.Repository.NotaRepository
import com.example.proyectomovil.data.Repository.TareaRepository
import com.example.proyectomovil.data.model.Nota
import com.example.proyectomovil.data.model.Tarea
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val notaRepository: NotaRepository,
    private val tareaRepository: TareaRepository
) : ViewModel() {

    private val notasStream: StateFlow<List<Nota>> = notaRepository.obtenerTodasStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = emptyList()
        )

    private val tareasStream: StateFlow<List<Tarea>> = tareaRepository.obtenerTodas()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = emptyList()
        )

    val homeUiState: StateFlow<HomeUiState> = combine(
        notasStream,
        tareasStream
    ) { listaDeNotas, listaDeTareas ->
        HomeUiState(
            listaDeNotas = listaDeNotas,
            listaDeTareas = listaDeTareas
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = HomeUiState()
    )


    fun eliminarNota(nota: Nota) {
        viewModelScope.launch {
            notaRepository.eliminarNota(nota)
        }
    }

    fun eliminarTarea(tarea: Tarea) {
        viewModelScope.launch {
            tareaRepository.eliminarTarea(tarea)
        }
    }
}

data class HomeUiState(
    val listaDeNotas: List<Nota> = emptyList(),
    val listaDeTareas: List<Tarea> = emptyList()
)
