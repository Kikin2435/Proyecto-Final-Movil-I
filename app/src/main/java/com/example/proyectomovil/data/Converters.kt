package com.example.proyectomovil.data

import androidx.room.TypeConverter


// CONVERTIDOR DE LA LISTA A STRING PARA LA DB
class Converters {
    @TypeConverter
    fun fromLongList(fechas: List<Long>?): String? {
        return fechas?.joinToString(",")
    }

    @TypeConverter
    fun toLongList(data: String?): List<Long>? {
        return data?.split(",")?.mapNotNull { it.toLongOrNull() }
    }
}
