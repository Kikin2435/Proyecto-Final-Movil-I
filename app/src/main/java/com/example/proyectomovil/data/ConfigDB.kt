package com.example.proyectomovil.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.proyectomovil.data.model.ArchivoMultimedia
import com.example.proyectomovil.data.model.Nota
import com.example.proyectomovil.data.model.Notificaciones

@Database(
    entities = [Nota::class, Notificaciones::class, ArchivoMultimedia::class],
    version = 1,
     exportSchema = false
)
abstract class ConfigDB : RoomDatabase(){
    abstract fun notaDao(): NotaDao
    // AQUI PODEMOS HACER EL LLAMADO DE LOS DAOS DE NOTIFICACIONES Y ARCHIVOSMULTIMEDIA

    companion object {
        @Volatile
        private var Instance: ConfigDB? = null

        fun getDatabase(context: Context): ConfigDB{
            return Instance ?: synchronized(this){
                Room.databaseBuilder(context, ConfigDB::class.java, "notas_db")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance  = it }
            }
        }
    }

}