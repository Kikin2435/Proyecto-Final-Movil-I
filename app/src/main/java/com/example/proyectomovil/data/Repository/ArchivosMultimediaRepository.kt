package com.example.proyectomovil.data.Repository

import com.example.proyectomovil.data.dao.ArchivosMultimediaDao
import com.example.proyectomovil.data.model.ArchivosMultimedia
import kotlinx.coroutines.flow.Flow

class ArchivosMultimediaRepository(private val dao: ArchivosMultimediaDao){
    suspend fun insertarArchivo(archivo: ArchivosMultimedia) = dao.insertarArchivo(archivo)
    suspend fun actualizar(archivo: ArchivosMultimedia) = dao.actualizarArchivo(archivo)
    suspend fun eliminar(archivo: ArchivosMultimedia) = dao.eliminarArchivo(archivo)
    fun obtenerArchivosPorNota(notaId: Int): Flow<List<ArchivosMultimedia>> = dao.obtenerArchivosPorNota(notaId)
    fun obtenerArchivosPorTarea(tareaId: Int) = dao.obtenerArchivosPorTarea(tareaId)

    suspend fun eliminarArchivosPorTarea(tareaId: Int) = dao.eliminarArchivosPorTareaId(tareaId)

    suspend fun eliminarArchivosPorNota(notaId: Int) = dao.eliminarArchivosPorNotaId(notaId)
}