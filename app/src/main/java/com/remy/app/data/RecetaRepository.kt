package com.remy.app.data

import kotlinx.coroutines.flow.Flow

class RecetaRepository(private val dao: RecetaDao) {
    val recetas: Flow<List<RecetaConIngredientes>> = dao.observarTodas()

    suspend fun guardar(receta: Receta, ingredientes: List<Ingrediente>): Long =
        dao.insertarCompleta(receta, ingredientes)

    suspend fun porId(id: Long): RecetaConIngredientes? = dao.porId(id)

    /** Detalle con los ingredientes resueltos hasta el nombre del alimento. */
    suspend fun detalle(id: Long): RecetaDetalle? = dao.detallePorId(id)

    suspend fun actualizar(receta: Receta) = dao.actualizar(receta)

    /**
     * Registra la cocción de una receta (#35): guarda la fecha y la ruta de la
     * foto del plato. Devuelve false si la receta ya no existe, para que la
     * pantalla pueda avisar en lugar de fallar en silencio.
     */
    suspend fun marcarComoCocinada(id: Long, fecha: Long, rutaFoto: String?): Boolean {
        val actual = dao.porId(id)?.receta ?: return false
        dao.actualizar(actual.copy(fechaCocinada = fecha, fotoCocinada = rutaFoto))
        return true
    }

    suspend fun borrar(receta: Receta) = dao.borrar(receta)
}
