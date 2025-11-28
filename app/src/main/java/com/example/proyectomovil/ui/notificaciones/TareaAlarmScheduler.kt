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
        // Asumimos que este método se llama para cada recordatorio individualmente,
        // por lo que la lista `fechasRecordatorio` contiene un solo elemento.
        val fechaRecordatorio = tarea.fechasRecordatorio.firstOrNull() ?: return

        val intent = Intent(context, AlarmaReceiver::class.java).apply {
            // Pasamos los datos que la notificación necesitará para mostrarse correctamente.
            putExtra("EXTRA_TAREA_ID", tarea.id)
            putExtra("EXTRA_TAREA_TITULO", tarea.titulo)
            putExtra("EXTRA_TAREA_CONTENIDO", tarea.contenido)
        }

        // Para que cada alarma de una misma tarea sea única, el PendingIntent debe ser único.
        // Usamos el ID de la tarea y la fecha del recordatorio para crear un 'requestCode' único.
        val requestCode = (tarea.id.toString() + fechaRecordatorio.toString()).hashCode()

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode, // Usamos el requestCode único
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            // Programamos la alarma para que se despierte incluso si el dispositivo está en modo de bajo consumo.
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                fechaRecordatorio, // Usamos la fecha única
                pendingIntent
            )
        } catch (e: SecurityException) {
            // Este error puede ocurrir si no se tiene el permiso SCHEDULE_EXACT_ALARM.
            e.printStackTrace()
        }
    }

    override fun cancel(tarea: Tarea) {
        // Para cancelar una alarma, debemos crear un PendingIntent que sea idéntico al que usamos para programarla.
        val fechaRecordatorio = tarea.fechasRecordatorio.firstOrNull() ?: return
        
        // Recreamos el mismo requestCode único que usamos al programar.
        val requestCode = (tarea.id.toString() + fechaRecordatorio.toString()).hashCode()

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode, // Usamos el mismo requestCode
            Intent(context, AlarmaReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        alarmManager.cancel(pendingIntent)
    }
}