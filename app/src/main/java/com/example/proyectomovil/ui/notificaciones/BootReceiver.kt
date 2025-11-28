package com.example.proyectomovil.ui.notificaciones

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.proyectomovil.ProyectoMovilApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val application = context.applicationContext as ProyectoMovilApplication
            val container = application.container
            val tareaRepository = container.tareaRepository
            val alarmaScheduler = container.alarmaScheduler

            CoroutineScope(Dispatchers.IO).launch {
                val tareas = tareaRepository.obtenerTodas().first()
                
                tareas.forEach { tarea ->
                    tarea.fechasRecordatorio.forEach { fecha ->
                        if (fecha > System.currentTimeMillis()) {
                            alarmaScheduler.schedule(tarea.copy(fechasRecordatorio = listOf(fecha)))
                        }
                    }
                }
            }
        }
    }
}