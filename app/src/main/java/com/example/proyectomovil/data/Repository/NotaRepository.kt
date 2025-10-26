package com.example.proyectomovil.data.Repository

import com.example.proyectomovil.data.model.Nota
import com.example.proyectomovil.data.dao.NotaDao
import kotlinx.coroutines.flow.Flow

interface NotaRepository {
    fun obtenerTodasStream(): Flow<List<Nota>>
    fun obtenerNotaStream(id: Int): Flow<Nota?>

    suspend fun insertarNota(nota: Nota)
    suspend fun eliminarNota(nota: Nota)
    suspend fun actualizarNota(nota: Nota)
}

class OffLineNotaRepository(private val notaDao: NotaDao) : NotaRepository {
    override fun obtenerTodasStream(): Flow<List<Nota>> = notaDao.obtenerTodas()
    override fun obtenerNotaStream(id: Int): Flow<Nota?> = notaDao.obtenerPorId(id)
    override suspend fun insertarNota(nota: Nota) = notaDao.insertar(nota)
    override suspend fun eliminarNota(nota: Nota) = notaDao.eliminar(nota)
    override suspend fun actualizarNota(nota: Nota) = notaDao.actualizar(nota)
}