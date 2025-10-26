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

    // Obtener todos los archivos adjuntos a una Nota específica.
    @Query("SELECT * FROM archivos_multimedia WHERE notaIdAsociada = :notaId")
    fun obtenerArchivosPorNota(notaId: Int): Flow<List<ArchivosMultimedia>>

    // Obtener todos los archivos adjuntos a una Tarea específica.
    @Query("SELECT * FROM archivos_multimedia WHERE tareaIdAsociada = :tareaId")
    fun obtenerArchivosPorTarea(tareaId: Int): Flow<List<ArchivosMultimedia>>
}
