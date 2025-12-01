package com.example.proyectomovil.ui.notificaciones

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.proyectomovil.data.model.Tarea

class TareaAlarmScheduler(private val context: Context) : AlarmaScheduler {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // Programa una alarma para un recordatorio de tarea específico.
    override fun schedule(tarea: Tarea) {
        val fechaRecordatorio = tarea.fechasRecordatorio.firstOrNull() ?: return

        val intent = Intent(context, AlarmaReceiver::class.java).apply {
            putExtra("EXTRA_TAREA_ID", tarea.id)
            putExtra("EXTRA_TAREA_TITULO", tarea.titulo)
            putExtra("EXTRA_TAREA_CONTENIDO", tarea.contenido)
        }

        val requestCode = (tarea.id.toString() + fechaRecordatorio.toString()).hashCode()

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                fechaRecordatorio,
                pendingIntent
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    // Cancela una alarma previamente programada para un recordatorio.
    override fun cancel(tarea: Tarea) {
        val fechaRecordatorio = tarea.fechasRecordatorio.firstOrNull() ?: return
        
        val requestCode = (tarea.id.toString() + fechaRecordatorio.toString()).hashCode()

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            Intent(context, AlarmaReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        alarmManager.cancel(pendingIntent)
    }
}