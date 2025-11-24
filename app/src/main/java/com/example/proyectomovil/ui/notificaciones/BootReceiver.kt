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
            // Obtenemos el contenedor de dependencias de la aplicación
            val application = context.applicationContext as ProyectoMovilApplication
            val container = application.container
            val tareaRepository = container.tareaRepository
            val alarmaScheduler = container.alarmaScheduler

            // Lanzamos una corrutina para hacer el trabajo en segundo plano
            CoroutineScope(Dispatchers.IO).launch {
                // Obtenemos todas las tareas
                val tareas = tareaRepository.obtenerTodasTareasStream().first()
                
                // Volvemos a programar las alarmas para las tareas con recordatorios futuros
                tareas.forEach { tarea ->
                    tarea.fechaRecordatorio?.let {
                        if (it > System.currentTimeMillis()) {
                            alarmaScheduler.schedule(tarea)
                        }
                    }
                }
            }
        }
    }
}