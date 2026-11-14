package com.remy.app.domain.model

/**
 * Disponibilidad de una receta respecto a la despensa actual.
 *
 * Cuando faltan ingredientes se guardan ya resueltos (nombre y cantidades) para
 * que la interfaz pueda avisar de qué falta exactamente antes de planificar.
 */
sealed interface DisponibilidadReceta {
    data object Lista : DisponibilidadReceta

    data class Faltan(val faltantes: List<IngredienteFaltante>) : DisponibilidadReceta {
        /** Número de ingredientes distintos que faltan. */
        val cantidad: Int get() = faltantes.size
    }
}
