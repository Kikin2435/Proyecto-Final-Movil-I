package com.example.proyectomovil.ui.ViewModel.Tarea

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomovil.data.Repository.TareaRepository
import com.example.proyectomovil.data.model.Tarea
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DetalleTareaViewModel (
    savedStateHandle: SavedStateHandle,
    tareaRepository: TareaRepository
) : ViewModel() {
    private val tareaId: Int = checkNotNull(savedStateHandle["tareaId"])

    val uiStateDetalle: StateFlow<TareaUiStateDetalle> =
        tareaRepository.obtenerTareaStream(tareaId)
            .filterNotNull()
            .map{ tarea ->
                TareaUiStateDetalle(tarea = tarea)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = TareaUiStateDetalle()
            )
}

data class TareaUiStateDetalle (
    val tarea: Tarea? = null,
)