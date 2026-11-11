package com.remy.app.data

import kotlinx.coroutines.flow.Flow

/** Acceso a la planificación semanal de comidas. */
class PlanRepository(private val dao: PlanDao) {

    fun observarDia(fecha: String): Flow<List<ComidaPlanificadaConReceta>> = dao.observarDia(fecha)

    fun observarFechasConPlan(): Flow<List<String>> = dao.observarFechasConPlan()

    suspend fun guardar(fecha: String, tipoComida: TipoComida, recetaId: Long): Long =
        dao.guardar(PlannedMeal(fecha = fecha, tipoComida = tipoComida, recetaId = recetaId))

    suspend fun borrar(fecha: String, tipoComida: TipoComida) = dao.borrar(fecha, tipoComida)
}
