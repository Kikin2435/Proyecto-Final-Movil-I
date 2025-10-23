package com.example.proyectomovil.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyectomovil.ProyectoMovilApplication
import com.example.proyectomovil.ui.screens.home.HomeViewModel
import com.example.proyectomovil.ui.screens.nota.NotaViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            HomeViewModel(ProyectoMovilApplication().container.notaRepository)
        }

        initializer {
            NotaViewModel(ProyectoMovilApplication().container.notaRepository)
        }

    }
}

fun CreationExtras.ProyectoMovilApplication(): ProyectoMovilApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ProyectoMovilApplication)
