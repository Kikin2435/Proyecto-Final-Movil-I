package com.example.proyectomovil.ui.notificaciones

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.proyectomovil.data.model.Tarea

// Interfaz para facilitar pruebas y futuras implementaciones
interface AlarmaScheduler {
    fun schedule(tarea: Tarea)
    fun cancel(tarea: Tarea)
}

class TareaAlarmScheduler(
    private val context: Context
) : AlarmaScheduler {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(tarea: Tarea) {
        val reminderTime = tarea.fechaRecordatorio ?: return
        // No programar alarmas para fechas pasadas
        if (reminderTime <= System.currentTimeMillis()) return

        val intent = Intent(context, AlarmaReceiver::class.java).apply {
            putExtra("TAREA_ID", tarea.id)
            putExtra("TAREA_TITULO", tarea.titulo)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            tarea.id, // Usamos el ID de la tarea como identificador único
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Usamos setExactAndAllowWhileIdle para asegurar que la alarma suene incluso en modo Doze
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            reminderTime,
            pendingIntent
        )
    }

    override fun cancel(tarea: Tarea) {
        val intent = Intent(context, AlarmaReceiver::class.java).apply {
            putExtra("TAREA_ID", tarea.id)
            putExtra("TAREA_TITULO", tarea.titulo)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            tarea.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }
}