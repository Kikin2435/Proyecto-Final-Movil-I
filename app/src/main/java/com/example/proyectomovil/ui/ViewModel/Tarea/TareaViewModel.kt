package com.example.proyectomovil.ui.ViewModel.Tarea

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomovil.data.Repository.TareaRepository
import com.example.proyectomovil.data.model.Tarea
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
class TareaViewModel(
    savedStateHandle: SavedStateHandle,
    private val tareaRepository: TareaRepository
) : ViewModel() {

    var tareaUiState by mutableStateOf(TareaUiState())
        private set

    private val tareaId: Int? = savedStateHandle["tareaId"]

    init {
        Log.d("TareaViewModel", "ViewModel creado. ID recibido: $tareaId")
        if (tareaId != null) {
            Log.d("TareaViewModel", "Modo Editar detectado. Cargando Datos")
            viewModelScope.launch {
                tareaUiState = tareaRepository.obtenerTareaStream(tareaId)
                    .filterNotNull()
                    .first()
                    .toTareaUiState()
            }
        }
    }

    fun actualizarUiState(nuevoDetalle: TareaUiState) {
        tareaUiState = nuevoDetalle.copy(
            isEntryValid = nuevoDetalle.titulo.isNotBlank()
        )
    }

    fun guardarTarea() {

        val tareaParaGuardar = tareaUiState.toTarea()
        viewModelScope.launch {
            try {
                if (tareaId != null && tareaId != -1) {

                    tareaRepository.actualizarTarea(tareaParaGuardar)
                } else {

                    tareaRepository.insertarTarea(tareaParaGuardar)
                }

            } catch (e: Exception) {
                Log.e("DEBUG_GUARDAR", "[ViewModel] ¡CRASH! Excepción al guardar en el repositorio: ${e.message}", e)
            }
        }
    }
}


data class TareaUiState(
    val id: Int = 0,
    val titulo: String = "",
    val contenido: String = "",
    val fechaRecordatorio: Long? = null,
    val isEntryValid: Boolean = false
)


private fun Tarea.toTareaUiState(): TareaUiState = TareaUiState(
    id = this.id,
    titulo = this.titulo,
    contenido = this.contenido,
    fechaRecordatorio = this.fechaRecordatorio,
    isEntryValid = this.titulo.isNotBlank()
)


fun TareaUiState.toTarea(): Tarea = Tarea(
    id = this.id,
    titulo = this.titulo,
    contenido = this.contenido,
    fechaRecordatorio = this.fechaRecordatorio
)

