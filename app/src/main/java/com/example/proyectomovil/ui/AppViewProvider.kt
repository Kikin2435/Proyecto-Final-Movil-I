package com.example.proyectomovil.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyectomovil.ProyectoMovilApplication
import com.example.proyectomovil.ui.ViewModel.Tarea.TareaViewModel
import com.example.proyectomovil.ui.ViewModel.home.HomeViewModel
import com.example.proyectomovil.ui.ViewModel.nota.NotaViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            HomeViewModel(ProyectoMovilApplication().container.notaRepository, ProyectoMovilApplication().container.tareaRepository)
        }

        initializer {
            NotaViewModel(ProyectoMovilApplication().container.notaRepository)
        }

        // Falta inicializar el viewModel de tarea
        initializer {
            TareaViewModel(ProyectoMovilApplication().container.tareaRepository)
        }

    }
}

fun CreationExtras.ProyectoMovilApplication(): ProyectoMovilApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ProyectoMovilApplication)
