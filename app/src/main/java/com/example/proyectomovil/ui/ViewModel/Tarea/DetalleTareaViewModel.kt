package com.example.proyectomovil.ui.ViewModel.Tarea

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomovil.data.Repository.ArchivosMultimediaRepository
import com.example.proyectomovil.data.Repository.TareaRepository
import com.example.proyectomovil.data.model.ArchivosMultimedia
import com.example.proyectomovil.data.model.Tarea
import com.example.proyectomovil.ui.notificaciones.AlarmaScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DetalleTareaViewModel(
    savedStateHandle: SavedStateHandle,
    private val tareaRepository: TareaRepository,
    private val archivosMultimediaRepository: ArchivosMultimediaRepository,
    private val alarmaScheduler: AlarmaScheduler // Inyectado
) : ViewModel() {

    private val tareaId: Int = checkNotNull(savedStateHandle["tareaId"])

    val uiStateDetalle: StateFlow<TareaUiStateDetalle> =
        tareaRepository.obtenerTareaStream(tareaId)
            .filterNotNull()
            .flatMapLatest { tarea ->
                archivosMultimediaRepository.obtenerArchivosPorTarea(tarea.id)
                    .map { archivos ->
                        TareaUiStateDetalle(tarea = tarea, archivos = archivos)
                    }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = TareaUiStateDetalle()
            )

    fun eliminarTarea() {
        viewModelScope.launch {
            uiStateDetalle.value.tarea?.let {
                it.fechasRecordatorio.forEach { fecha ->
                    alarmaScheduler.cancel(it.copy(fechasRecordatorio = listOf(fecha)))
                }
                tareaRepository.eliminarTarea(it)
                archivosMultimediaRepository.eliminarArchivosPorTarea(it.id)
            }
        }
    }
}

data class TareaUiStateDetalle(
    val tarea: Tarea? = null,
    val archivos: List<ArchivosMultimedia> = emptyList()
)
