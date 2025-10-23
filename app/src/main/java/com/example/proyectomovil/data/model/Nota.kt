package com.example.proyectomovil.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notas")
data class Nota(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val contenido: String,
    val fechaCreacion: Long = System.currentTimeMillis(),
    val fav: Boolean = false
)

// IMPLEMENTACION PARA LAS NOTIFICACIONES
@Entity(tableName = "notificaciones")
data class Notificaciones(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val notaId: Int,
    val fechaNotificacion: Long
)

// IMPLEMENTACION PARA EL MANEJO DE MULTIMEDIA
@Entity(tableName = "multimedia")
data class ArchivoMultimedia(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val notaId: Int,
    val uri: String,
    val tipo: String, // para posible manejo de variedad de tipos de archivos
)
