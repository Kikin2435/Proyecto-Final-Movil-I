package com.example.proyectomovil.data.dao

import androidx.room.Dao
import androidx.room.*
import com.example.proyectomovil.data.model.ArchivosMultimedia
import kotlinx.coroutines.flow.Flow

@Dao
interface ArchivosMultimediaDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarArchivo(archivo: ArchivosMultimedia)

    @Delete
    suspend fun eliminarArchivo(archivo: ArchivosMultimedia)

    @Update
    suspend fun actualizarArchivo(archivo: ArchivosMultimedia)

    @Query("SELECT * FROM archivos_multimedia WHERE notaIdAsociada = :notaId")
    fun obtenerArchivosPorNota(notaId: Int): Flow<List<ArchivosMultimedia>>

    @Query("SELECT * FROM archivos_multimedia WHERE tareaIdAsociada = :tareaId")
    fun obtenerArchivosPorTarea(tareaId: Int): Flow<List<ArchivosMultimedia>>

    @Query("DELETE FROM archivos_multimedia WHERE tareaIdAsociada = :tareaId")
    suspend fun eliminarArchivosPorTareaId(tareaId: Int)

    // Nuevo método para eliminar por ID de nota
    @Query("DELETE FROM archivos_multimedia WHERE notaIdAsociada = :notaId")
    suspend fun eliminarArchivosPorNotaId(notaId: Int)
}
