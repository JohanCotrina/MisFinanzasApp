package edu.pe.cibertec.MisFinanzas.repository

import edu.pe.cibertec.MisFinanzas.data.TranasaccionDao
import edu.pe.cibertec.MisFinanzas.data.Transaccion
import kotlinx.coroutines.flow.Flow

class TransaccionRepository(private val dao: TranasaccionDao) {
    fun getAllTransacctions(): Flow<List<Transaccion>> = dao.getAll()

    suspend fun insertTransaccion(transaccion: Transaccion) = dao.insert(transaccion)

    suspend fun deleteTransaccion(transaccion: Transaccion) = dao.delete(transaccion)
}