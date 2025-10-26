package com.example.proyectomovil.data

import android.content.Context
import com.example.proyectomovil.data.Repository.NotaRepository
import com.example.proyectomovil.data.Repository.OffLineNotaRepository
import com.example.proyectomovil.data.Repository.OffLineTareaRepository
import com.example.proyectomovil.data.Repository.TareaRepository

interface AppContainer{
    val notaRepository: NotaRepository
    val tareaRepository: TareaRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val notaRepository: NotaRepository by lazy {
        OffLineNotaRepository(ConfigDB.getDatabase(context).notaDao())
    }
    override val tareaRepository: TareaRepository by lazy {
        OffLineTareaRepository(ConfigDB.getDatabase(context).tareaDao())
    }
}