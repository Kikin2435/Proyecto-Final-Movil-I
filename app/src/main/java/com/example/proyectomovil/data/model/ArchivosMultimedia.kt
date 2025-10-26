package com.example.proyectomovil.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
@Entity(
    tableName = "archivos_multimedia",
    // Definimos dos claves foráneas, una para Nota y otra para Tarea.
    foreignKeys = [
        ForeignKey(
            entity = Nota::class,
            parentColumns = ["id"],
            childColumns = ["notaIdAsociada"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Tarea::class,
            parentColumns = ["id"],
            childColumns = ["tareaIdAsociada"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["notaIdAsociada"]), Index(value = ["tareaIdAsociada"])]
)
data class ArchivosMultimedia(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val uri: String,
    val tipo: String,
    val notaIdAsociada: Int?,
    val tareaIdAsociada: Int?
)