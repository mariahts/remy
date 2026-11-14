package com.remy.app.domain.model

import com.remy.app.data.model.Ingrediente

/** Motivo por el que un ingrediente cuenta como faltante. */
enum class MotivoFaltante { AUSENTE, CANTIDAD_INSUFICIENTE }

/** Ingrediente que falta para poder cocinar una receta. */
data class Faltante(
    val alimentoId: Long,
    val motivo: MotivoFaltante = MotivoFaltante.AUSENTE
)

/**
 * Ingrediente que falta, ya resuelto con su nombre y sus cantidades para poder
 * explicarlo en la interfaz.
 *
 * [necesario] y [unidad] son la cantidad que pide la receta. [disponible] es lo
 * que hay en la despensa en este momento, o null si el alimento no está.
 */
data class IngredienteFaltante(
    val alimentoId: Long,
    val nombre: String,
    val necesario: Double,
    val unidad: String,
    val disponible: Double?,
    val motivo: MotivoFaltante
)
