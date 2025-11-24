package com.example.proyectomovil.ui.ViewModel.ArchivosMultimedia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomovil.data.Repository.ArchivosMultimediaRepository
import com.example.proyectomovil.data.model.ArchivosMultimedia
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

class ArchivosMultimediaViewModel(
    private val repository: ArchivosMultimediaRepository
) : ViewModel() {

    // IDs dinámicos
    private val notaId = MutableStateFlow<Int?>(null)
    private val tareaId = MutableStateFlow<Int?>(null)

    // Stream para los archivos multimedia (usa los métodos que devuelven Flow)
    private val archivosStream: StateFlow<List<ArchivosMultimedia>> =
        notaId.flatMapLatest { idNota ->
            tareaId.flatMapLatest { idTarea ->
                when {
                    idNota != null -> repository.obtenerArchivosPorNota(idNota)
                    idTarea != null -> repository.obtenerArchivosPorTarea(idTarea)
                    else -> flow { emit(emptyList()) }
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = emptyList()
        )

    // UI state combinado
    val archivosMultimediaUiState: StateFlow<ArchivosMultimediaUiState> =
        combine(
            archivosStream,
            notaId,
            tareaId
        ) { lista, idNota, idTarea ->
            ArchivosMultimediaUiState(
                archivos = lista,
                notaId = idNota,
                tareaId = idTarea
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ArchivosMultimediaUiState() // ahora válido porque la data class tiene defaults
        )

    // Funciones para seleccionar origen (nota o tarea)
    fun cargarArchivosParaNota(id: Int) {
        notaId.value = id
        tareaId.value = null
    }

    fun cargarArchivosParaTarea(id: Int) {
        tareaId.value = id
        notaId.value = null
    }

    // CRUD
    fun insertarArchivo(archivo: ArchivosMultimedia) {
        viewModelScope.launch {
            repository.insertarArchivo(archivo)
        }
    }

    fun eliminarArchivo(archivo: ArchivosMultimedia) {
        viewModelScope.launch {
            repository.eliminar(archivo)
        }
    }

    // Metodo utilitario para detalle: obtener un archivo por id desde el estado actual
    fun obtenerArchivoPorId(id: Int): ArchivosMultimedia? {
        return archivosMultimediaUiState.value.archivos.find { it.id == id }
    }
}

// Data class con valores por defecto
data class ArchivosMultimediaUiState(
    val archivos: List<ArchivosMultimedia> = emptyList(),
    val notaId: Int? = null,
    val tareaId: Int? = null,
)
