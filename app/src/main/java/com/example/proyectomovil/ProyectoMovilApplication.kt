package com.example.proyectomovil

import android.app.Application
import com.example.proyectomovil.data.AppContainer // Asegúrate de que la ruta del import sea correcta
import com.example.proyectomovil.data.AppDataContainer // Asegúrate de que la ruta del import sea correcta

class ProyectoMovilApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}


