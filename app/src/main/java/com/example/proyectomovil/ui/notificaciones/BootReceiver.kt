package com.example.proyectomovil.ui.notificaciones

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.proyectomovil.ProyectoMovilApplication
import com.example.proyectomovil.R
import com.example.proyectomovil.data.model.Tarea
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    // Se activa al reiniciar el dispositivo para reagendar las alarmas perdidas.
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
                            // Muestra una notificación para confirmar visualmente el reagendamiento.
                            showRescheduleNotification(context, tarea)
                        }
                    }
                    Log.d("BootReceiver", "Proceso de reagendamiento finalizado.")
                } catch (e: Exception) {
                    Log.e("BootReceiver", "Error al reagendar las tareas.", e)
                }
            }
        }
    }

    // Muestra una notificación para confirmar que una tarea ha sido reagendada.
    private fun showRescheduleNotification(context: Context, tarea: Tarea) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "reagendado_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Recordatorios Reagendados",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones para confirmar que un recordatorio se ha vuelto a programar."
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Recordatorio Reagendado")
            .setContentText("Se ha vuelto a programar la tarea: ${tarea.titulo}")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        // Se usa el ID de la tarea para la notificación, para que cada una sea única.
        notificationManager.notify(tarea.id, notification)
    }
}