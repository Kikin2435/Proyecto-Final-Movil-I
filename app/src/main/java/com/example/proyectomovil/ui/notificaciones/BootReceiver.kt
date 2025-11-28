package com.example.proyectomovil.ui.notificaciones

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.proyectomovil.ProyectoMovilApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Dispositivo reiniciado. Reagendando alarmas...")

            val application = context.applicationContext as ProyectoMovilApplication
            val container = application.container
            val tareaRepository = container.tareaRepository
            val alarmaScheduler: TareaAlarmScheduler = TareaAlarmScheduler(context)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val tareas = tareaRepository.obtenerTodas().first()
                    Log.d("BootReceiver", "Se encontraron ${tareas.size} tareas en la base de datos.")

                    tareas.forEach { tarea ->
                        val recordatoriosFuturos = tarea.fechasRecordatorio.filter { it > System.currentTimeMillis() }
                        if (recordatoriosFuturos.isNotEmpty()) {
                            Log.d("BootReceiver", "Reagendando tarea: '${tarea.titulo}' con ${recordatoriosFuturos.size} recordatorios.")
                            alarmaScheduler.schedule(tarea.copy(fechasRecordatorio = recordatoriosFuturos))
                        }
                    }
                    Log.d("BootReceiver", "Proceso de reagendamiento finalizado.")
                } catch (e: Exception) {
                    Log.e("BootReceiver", "Error al reagendar las tareas.", e)
                }
            }
        }
    }
}