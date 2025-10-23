package com.example.proyectomovil.data

import android.content.Context
import com.example.proyectomovil.data.NotaRepository

interface AppContainer{
    val notaRepository: NotaRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val notaRepository: NotaRepository by lazy {
        OffLineNotaRepository(ConfigDB.getDatabase(context).notaDao())
    }
}