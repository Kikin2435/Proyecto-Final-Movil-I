package com.example.proyectomovil.ui.notificaciones

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.proyectomovil.data.model.Tarea

/**
 * Implementación de [AlarmaScheduler] que utiliza el [AlarmManager] de Android para las tareas.
 */
class TareaAlarmScheduler(private val context: Context) : AlarmaScheduler {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun schedule(tarea: Tarea) {
        // Si la tarea no tiene fecha de recordatorio, no hay nada que programar.
        val fechaRecordatorio = tarea.fechaRecordatorio ?: return

        val intent = Intent(context, AlarmaReceiver::class.java).apply {
            // Pasamos los datos que la notificación necesitará para mostrarse correctamente.
            putExtra("EXTRA_TAREA_ID", tarea.id)
            putExtra("EXTRA_TAREA_TITULO", tarea.titulo)
            putExtra("EXTRA_TAREA_CONTENIDO", tarea.contenido)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            tarea.id, // Usamos el ID de la tarea como código de petición único para poder actualizarla o cancelarla.
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            // Programamos la alarma para que se despierte incluso si el dispositivo está en modo de bajo consumo.
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                fechaRecordatorio,
                pendingIntent
            )
        } catch (e: SecurityException) {
            // Este error puede ocurrir si no se tiene el permiso SCHEDULE_EXACT_ALARM.
            // Ya lo gestionamos en la pantalla, pero es buena práctica tener un control aquí.
            e.printStackTrace()
        }
    }

    override fun cancel(tarea: Tarea) {
        // Para cancelar una alarma, debemos crear un PendingIntent que sea idéntico al que usamos para programarla.
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            tarea.id,
            Intent(context, AlarmaReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
