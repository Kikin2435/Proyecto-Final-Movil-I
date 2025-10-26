package com.example.proyectomovil.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.proyectomovil.data.model.Nota
import kotlinx.coroutines.flow.Flow

@Dao
interface NotaDao{
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertar(nota: Nota)

    @Update
    suspend fun actualizar(nota: Nota)

    @Delete
    suspend fun eliminar(nota: Nota)

    @Query("SELECT * FROM notas ORDER BY fechaCreacion DESC")
    fun obtenerTodas(): Flow<List<Nota>>

    @Query("SELECT * FROM notas WHERE id = :id")
    fun obtenerPorId(id: Int): Flow<Nota?>

}