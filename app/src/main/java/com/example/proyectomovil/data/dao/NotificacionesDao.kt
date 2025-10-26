package com.example.proyectomovil.data.dao

import androidx.room.Dao
import androidx.room.*
import com.example.proyectomovil.data.model.Notificacion
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificacionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarNotificacion(notificacion: Notificacion)

    @Delete
    suspend fun eliminarNotificacion(notificacion: Notificacion)

    @Query("SELECT * FROM notificaciones WHERE tareaId = :tareaId ORDER BY triggerTimestamp ASC")
    fun obtenerNotificacionesPorTarea(tareaId: Int): Flow<List<Notificacion>>


    @Query("SELECT * FROM notificaciones WHERE triggerTimestamp > :ahora AND fueDisparada = 0")
    fun obtenerNotificacionesPendientes(ahora: Long): Flow<List<Notificacion>>

    // Marcar una notificación como "disparada".
    @Query("UPDATE notificaciones SET fueDisparada = 1 WHERE id = :notificacionId")
    suspend fun marcarComoDisparada(notificacionId: Int)
}
