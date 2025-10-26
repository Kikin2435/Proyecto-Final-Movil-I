package com.example.proyectomovil.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.proyectomovil.data.dao.ArchivosMultimediaDao
import com.example.proyectomovil.data.dao.NotaDao
import com.example.proyectomovil.data.dao.NotificacionDao
import com.example.proyectomovil.data.dao.TareaDao
import com.example.proyectomovil.data.model.ArchivosMultimedia
import com.example.proyectomovil.data.model.Nota
import com.example.proyectomovil.data.model.Notificacion
import com.example.proyectomovil.data.model.Tarea

@Database(
    entities = [Nota::class, Notificacion::class, ArchivosMultimedia::class, Tarea::class],
    version = 6,
     exportSchema = false
)
abstract class ConfigDB : RoomDatabase(){
    abstract fun notaDao(): NotaDao
    abstract fun notificacionDao(): NotificacionDao
    abstract fun archivosMultimediaDao(): ArchivosMultimediaDao
    abstract fun tareaDao(): TareaDao

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