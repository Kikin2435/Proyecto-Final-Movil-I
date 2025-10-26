package com.example.proyectomovil.ui.ViewModel.nota

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomovil.data.Repository.NotaRepository
import com.example.proyectomovil.data.model.Nota
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NotaViewModel(
    savedStateHandle: SavedStateHandle,
    private val notaRepository: NotaRepository
) : ViewModel() {


    var notaUiState by mutableStateOf(NotaUiState())
        private set

    private val notaId: Int? = savedStateHandle["notaId"]

    init {
        if (notaId != null && notaId != -1) {
            viewModelScope.launch {
                notaUiState = notaRepository.obtenerNotaStream(notaId)
                    .filterNotNull()
                    .first()
                    .toNotaUiState()
            }
        } else {
            Log.d("NotaViewModel", "Modo Crear detectado.")
        }
    }


    fun actualizarUiState(nuevosDetalles: NotaUiState) {
        notaUiState = nuevosDetalles.copy(

            isEntryValid = nuevosDetalles.titulo.isNotBlank()
        )
    }


    fun guardarNota() {
        if (!notaUiState.isEntryValid) {
            return
        }

        viewModelScope.launch {

            if (notaId != null && notaId != -1) {
                notaRepository.actualizarNota(notaUiState.toNota())
            } else {

                notaRepository.insertarNota(notaUiState.toNota())
            }
        }
    }
}


data class NotaUiState(
    val id: Int = 0,
    val titulo: String = "",
    val contenido: String = "",
    val isEntryValid: Boolean = false
)



private fun Nota.toNotaUiState(): NotaUiState = NotaUiState(
    id = this.id,
    titulo = this.titulo,
    contenido = this.contenido,
    isEntryValid = this.titulo.isNotBlank()
)

fun NotaUiState.toNota(): Nota = Nota(
    id = this.id,
    titulo = this.titulo,
    contenido = this.contenido
)
