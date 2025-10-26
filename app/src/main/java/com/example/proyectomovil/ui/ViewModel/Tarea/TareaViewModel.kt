package com.example.proyectomovil.ui.ViewModel.Tarea

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomovil.data.Repository.TareaRepository
import com.example.proyectomovil.data.model.Nota
import com.example.proyectomovil.data.model.Tarea
import kotlinx.coroutines.launch

class TareaViewModel(private val tareaRepository: TareaRepository): ViewModel() {

    var tareaUiState by mutableStateOf(TareaUiState())
        private set

    fun actualizarUiState(nuevoDetalle: TareaUiState){
        tareaUiState = nuevoDetalle.copy()
    }

    fun guardarTarea(){
        if(tareaUiState.titulo.isBlank()){
            return
        }

        viewModelScope.launch {
            tareaRepository.insertarTarea(
                Tarea(
                    titulo = tareaUiState.titulo,
                    contenido = tareaUiState.contenido,
                    fechaRecordatorio = tareaUiState.fechaRecordatorio,
                )
            )
        }
    }
}

data class TareaUiState(
    val titulo: String = "",
    val contenido: String = "",
    val fechaRecordatorio: Long? = null,
)