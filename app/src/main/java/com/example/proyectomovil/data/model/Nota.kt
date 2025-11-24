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
    val fotoUri: String? = null, // Agregado
    val fav: Boolean = false
)
