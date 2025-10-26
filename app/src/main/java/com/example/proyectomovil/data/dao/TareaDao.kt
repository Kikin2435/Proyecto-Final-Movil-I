package com.example.proyectomovil.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.proyectomovil.data.model.Tarea
import kotlinx.coroutines.flow.Flow

@Dao
interface TareaDao{
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertar(tarea: Tarea)

    @Update
    suspend fun actualizar(tarea: Tarea)

    @Delete
    suspend fun eliminar(tarea: Tarea)

    @Query("SELECT * FROM tareas ORDER BY fechaRecordatorio DESC")
    fun obtenerTodas(): Flow<List<Tarea>>

    @Query("SELECT * FROM tareas WHERE id = :id")
    fun obtenerPorId(id: Int): Flow<Tarea?>

    @Update
    suspend fun actualizarTarea(tarea: Tarea)
}