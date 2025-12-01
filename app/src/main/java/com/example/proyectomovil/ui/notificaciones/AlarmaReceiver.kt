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

    // Se activa al recibir una alarma para crear y mostrar una notificación.
    override fun onReceive(context: Context, intent: Intent) {
        val tareaId = intent.getIntExtra("EXTRA_TAREA_ID", -1)
        if (tareaId == -1) return

        val tareaTitulo = intent.getStringExtra("EXTRA_TAREA_TITULO") ?: "Recordatorio de tarea"
        val tareaContenido = intent.getStringExtra("EXTRA_TAREA_CONTENIDO") ?: "No te olvides de completar tu tarea."

        val channelId = "canal_recordatorios"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Recordatorios de Tareas",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal para los recordatorios de tareas de la aplicación."
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intentAbrirApp = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("TAREA_ID_NOTIFICACION", tareaId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            tareaId,
            intentAbrirApp,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(tareaTitulo)
            .setContentText(tareaContenido)
            .setStyle(NotificationCompat.BigTextStyle().bigText(tareaContenido))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(tareaId, notification)
    }
}
