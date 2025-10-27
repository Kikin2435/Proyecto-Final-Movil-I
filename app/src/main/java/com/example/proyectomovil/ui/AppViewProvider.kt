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
            HomeViewModel(ProyectoMovilApplication().container.notaRepository, ProyectoMovilApplication().container.tareaRepository)
        }

        initializer {
            NotaViewModel(this.createSavedStateHandle(),ProyectoMovilApplication().container.notaRepository)
        }

        // Falta inicializar el viewModel de tarea
        initializer {
            TareaViewModel(this.createSavedStateHandle(), ProyectoMovilApplication().container.tareaRepository)
        }

        initializer {
            DetalleTareaViewModel(this.createSavedStateHandle(),ProyectoMovilApplication().container.tareaRepository)
        }

        initializer {
            DetalleNotaViewModel(this.createSavedStateHandle(), ProyectoMovilApplication().container.notaRepository)
        }

        initializer {
            SettingsViewModel(
                ProyectoMovilApplication().container.userPreferencesRepository
            )
        }

    }
}

fun CreationExtras.ProyectoMovilApplication(): ProyectoMovilApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ProyectoMovilApplication)
