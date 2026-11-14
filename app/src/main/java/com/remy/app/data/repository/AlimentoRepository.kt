package com.remy.app.data.repository

import com.remy.app.data.dao.AlimentoDao
import com.remy.app.data.model.Alimento
import kotlinx.coroutines.flow.Flow

class AlimentoRepository(private val dao: AlimentoDao) {
    /** La despensa del usuario: solo los alimentos marcados como tales. */
    val alimentos: Flow<List<Alimento>> = dao.observarTodos()

    /** El catálogo completo, para resolver los nombres de los faltantes (#34). */
    val catalogo: Flow<List<Alimento>> = dao.observarCatalogo()

    suspend fun guardar(alimento: Alimento): Long = dao.insertar(alimento)

    suspend fun actualizar(alimento: Alimento) = dao.actualizar(alimento)

    /** Quita de la despensa sin destruir las recetas que usan el alimento. */
    suspend fun borrar(alimento: Alimento) = dao.ocultar(alimento.id)
}
