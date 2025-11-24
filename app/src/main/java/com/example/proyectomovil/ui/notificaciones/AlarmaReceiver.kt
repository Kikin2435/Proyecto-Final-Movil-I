package com.example.proyectomovil.ui.notificaciones

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.proyectomovil.MainActivity
import com.example.proyectomovil.R

class AlarmaReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val tareaId = intent.getIntExtra("TAREA_ID", -1)
        val tareaTitulo = intent.getStringExtra("TAREA_TITULO") ?: "Recordatorio de tarea"

        if (tareaId == -1) return

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Intent para abrir la app al pulsar la notificación
        val intentAbrirApp = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("TAREA_ID_NOTIFICACION", tareaId) // Pasamos el ID para la navegación
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 
            tareaId, // Usamos el ID de la tarea como identificador único
            intentAbrirApp, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "canal_recordatorios")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Asegúrate de tener este drawable
            .setContentTitle("Recordatorio: Tarea Pendiente")
            .setContentText(tareaTitulo)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(tareaId, notification)
    }
}