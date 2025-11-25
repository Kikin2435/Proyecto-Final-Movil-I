package com.example.proyectomovil.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyectomovil.ProyectoMovilApplication
import com.example.proyectomovil.ui.ViewModel.Tarea.DetalleTareaViewModel
import com.example.proyectomovil.ui.ViewModel.Tarea.TareaViewModel
import com.example.proyectomovil.ui.ViewModel.home.HomeViewModel
import com.example.proyectomovil.ui.ViewModel.nota.DetalleNotaViewModel
import com.example.proyectomovil.ui.ViewModel.nota.NotaViewModel
import com.example.proyectomovil.ui.ViewModel.settings.SettingsViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {

        initializer {
            HomeViewModel(
                proyectoMovilApplication().container.notaRepository,
                proyectoMovilApplication().container.tareaRepository
            )
        }

        initializer {
            NotaViewModel(
                proyectoMovilApplication(),
                this.createSavedStateHandle(),
                proyectoMovilApplication().container.notaRepository,
                proyectoMovilApplication().container.archivosMultimediaRepository
            )
        }

        // --- CORRECCIÓN ---
        // Le pasamos la instancia de 'Application' al TareaViewModel.
        initializer {
            TareaViewModel(
                proyectoMovilApplication(), // <-- Añadido
                this.createSavedStateHandle(),
                proyectoMovilApplication().container.tareaRepository,
                proyectoMovilApplication().container.archivosMultimediaRepository,
                proyectoMovilApplication().container.alarmaScheduler
            )
        }

        initializer {
            DetalleNotaViewModel(
                this.createSavedStateHandle(),
                proyectoMovilApplication().container.notaRepository,
                proyectoMovilApplication().container.archivosMultimediaRepository
            )
        }

        initializer {
            DetalleTareaViewModel(
                this.createSavedStateHandle(),
                proyectoMovilApplication().container.tareaRepository,
                proyectoMovilApplication().container.archivosMultimediaRepository,
                proyectoMovilApplication().container.alarmaScheduler
            )
        }

        initializer {
            SettingsViewModel(
                proyectoMovilApplication().container.userPreferencesRepository
            )
        }
    }
}

fun CreationExtras.proyectoMovilApplication(): ProyectoMovilApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ProyectoMovilApplication)
