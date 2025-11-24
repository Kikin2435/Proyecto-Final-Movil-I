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
                this.createSavedStateHandle(),
                proyectoMovilApplication().container.notaRepository,
                proyectoMovilApplication().container.archivosMultimediaRepository
            )
        }

        initializer {
            TareaViewModel(
                this.createSavedStateHandle(),
                proyectoMovilApplication().container.tareaRepository,
                proyectoMovilApplication().container.archivosMultimediaRepository,
                proyectoMovilApplication().container.alarmaScheduler // Añadido
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
                proyectoMovilApplication().container.alarmaScheduler // Añadido
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
