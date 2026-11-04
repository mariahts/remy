package com.remy.app.data

import kotlinx.coroutines.flow.Flow

class RecetaRepository(private val dao: RecetaDao) {
    val recetas: Flow<List<RecetaConIngredientes>> = dao.observarTodas()

    suspend fun guardar(receta: Receta, ingredientes: List<Ingrediente>): Long =
        dao.insertarCompleta(receta, ingredientes)

    suspend fun porId(id: Long): RecetaConIngredientes? = dao.porId(id)

    suspend fun actualizar(receta: Receta) = dao.actualizar(receta)

    suspend fun borrar(receta: Receta) = dao.borrar(receta)
}
