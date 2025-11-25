package com.example.proyectomovil.ui.notificaciones

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.proyectomovil.MainActivity
import com.example.proyectomovil.R

class AlarmaReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // --- CORRECCIÓN: Usamos las mismas claves que definimos en TareaAlarmScheduler ---
        val tareaId = intent.getIntExtra("EXTRA_TAREA_ID", -1)
        if (tareaId == -1) return

        val tareaTitulo = intent.getStringExtra("EXTRA_TAREA_TITULO") ?: "Recordatorio de tarea"
        val tareaContenido = intent.getStringExtra("EXTRA_TAREA_CONTENIDO") ?: "No te olvides de completar tu tarea."

        val channelId = "canal_recordatorios"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // --- MEJORA: CREACIÓN DEL CANAL DE NOTIFICACIÓN ---
        // A partir de Android 8.0 (API 26), es obligatorio tener un canal para las notificaciones.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Recordatorios de Tareas",
                NotificationManager.IMPORTANCE_HIGH // Prioridad alta para que la notificación aparezca en la parte superior
            ).apply {
                description = "Canal para los recordatorios de tareas de la aplicación."
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Intent para abrir la app al pulsar la notificación
        val intentAbrirApp = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Pasamos el ID para que la app sepa qué tarea debe mostrar
            putExtra("TAREA_ID_NOTIFICACION", tareaId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            tareaId, // Usamos el ID de la tarea como identificador único
            intentAbrirApp,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // --- MEJORA: DISEÑO DE LA NOTIFICACIÓN CON BIGTEXTSTYLE ---
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Puedes cambiar esto por un icono de notificación personalizado (blanco y transparente)
            .setContentTitle(tareaTitulo)
            .setContentText(tareaContenido)
            // Usamos BigTextStyle para mostrar el contenido completo cuando se expande la notificación.
            .setStyle(NotificationCompat.BigTextStyle().bigText(tareaContenido))
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Prioridad alta para notificaciones "heads-up"
            .setContentIntent(pendingIntent) // Acción al pulsar la notificación
            .setAutoCancel(true) // La notificación se cierra automáticamente al pulsarla
            .build()

        notificationManager.notify(tareaId, notification)
    }
}
