package com.example.proyectomovil.data

import android.content.Context
import com.example.proyectomovil.data.Repository.ArchivosMultimediaRepository
import com.example.proyectomovil.data.Repository.NotaRepository
import com.example.proyectomovil.data.Repository.OffLineNotaRepository
import com.example.proyectomovil.data.Repository.OffLineTareaRepository
import com.example.proyectomovil.data.Repository.TareaRepository
import com.example.proyectomovil.data.Repository.UserPreferencesRepository
import com.example.proyectomovil.ui.notificaciones.AlarmaScheduler
import com.example.proyectomovil.ui.notificaciones.TareaAlarmScheduler

interface AppContainer {
    val notaRepository: NotaRepository
    val tareaRepository: TareaRepository
    val archivosMultimediaRepository: ArchivosMultimediaRepository
    val userPreferencesRepository: UserPreferencesRepository
    val alarmaScheduler: AlarmaScheduler // Agregado
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val notaRepository: NotaRepository by lazy {
        OffLineNotaRepository(ConfigDB.getDatabase(context).notaDao())
    }

    override val tareaRepository: TareaRepository by lazy {
        OffLineTareaRepository(ConfigDB.getDatabase(context).tareaDao())
    }

    override val archivosMultimediaRepository: ArchivosMultimediaRepository by lazy {
        ArchivosMultimediaRepository(ConfigDB.getDatabase(context).archivosMultimediaDao())
    }

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context)
    }

    // Agregado
    override val alarmaScheduler: AlarmaScheduler by lazy {
        TareaAlarmScheduler(context)
    }
}