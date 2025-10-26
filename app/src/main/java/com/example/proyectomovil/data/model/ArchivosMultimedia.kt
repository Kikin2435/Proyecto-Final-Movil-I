package com.example.proyectomovil.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "multimedia")
data class ArchivosMultimedia(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val notaId: Int,
    val uri: String,
    val tipo: String,
)