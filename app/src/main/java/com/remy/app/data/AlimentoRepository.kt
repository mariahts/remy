package com.remy.app.data

import kotlinx.coroutines.flow.Flow

class AlimentoRepository(private val dao: AlimentoDao) {
    val alimentos: Flow<List<Alimento>> = dao.observarTodos()

    suspend fun guardar(alimento: Alimento): Long = dao.insertar(alimento)

    suspend fun actualizar(alimento: Alimento) = dao.actualizar(alimento)

    suspend fun borrar(alimento: Alimento) = dao.borrar(alimento)
}
