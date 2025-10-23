package com.example.proyectomovil.ui.screens.nota

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomovil.data.NotaRepository
import com.example.proyectomovil.data.model.Nota
import kotlinx.coroutines.launch

class NotaViewModel(private val notaRepository: NotaRepository): ViewModel(){

    var notaUiState by mutableStateOf(NotaUiState())
        private set

    fun actualizarUiState(nuevoDetalles: NotaUiState){
        notaUiState=  nuevoDetalles.copy()
    }

    fun guardarNota(){
        if(notaUiState.titulo.isBlank()){
            return
        }

        viewModelScope.launch {
            notaRepository.insertarNota(
                Nota(
                    titulo = notaUiState.titulo,
                    contenido = notaUiState.contenido
                )
            )
        }
    }
}

data class NotaUiState(
    val titulo: String = "",
    val contenido: String = ""
)