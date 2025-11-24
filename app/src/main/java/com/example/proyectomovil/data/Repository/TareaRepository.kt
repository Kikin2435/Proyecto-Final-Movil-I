package com.example.proyectomovil.data.Repository

import com.example.proyectomovil.data.dao.TareaDao
import com.example.proyectomovil.data.model.Tarea
import kotlinx.coroutines.flow.Flow

interface TareaRepository {
    fun obtenerTodasTareasStream(): Flow<List<Tarea>>
    fun obtenerTareaStream(id: Int): Flow<Tarea?>

    suspend fun insertarTarea(tarea: Tarea): Long // Cambiado para que devuelva el ID
    suspend fun eliminarTarea(tarea: Tarea)
    suspend fun actualizarTarea(tarea: Tarea)
}

class OffLineTareaRepository(private val tareaDao: TareaDao) : TareaRepository {
    override fun obtenerTodasTareasStream(): Flow<List<Tarea>> = tareaDao.obtenerTodas()
    override fun obtenerTareaStream(id: Int): Flow<Tarea?> = tareaDao.obtenerPorId(id)
    override suspend fun insertarTarea(tarea: Tarea): Long = tareaDao.insertar(tarea) // Cambiado
    override suspend fun eliminarTarea(tarea: Tarea) = tareaDao.eliminar(tarea)
    override suspend fun actualizarTarea(tarea: Tarea) = tareaDao.actualizar(tarea)
}