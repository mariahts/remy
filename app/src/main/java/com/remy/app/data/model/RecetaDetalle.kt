package com.remy.app.data.model

import androidx.room.Embedded
import androidx.room.Relation

/** Un ingrediente con el alimento al que apunta, para mostrar su nombre en el detalle. */
data class IngredienteConAlimento(
    @Embedded val ingrediente: Ingrediente,
    @Relation(parentColumn = "alimentoId", entityColumn = "id")
    val alimento: Alimento?
)

/**
 * Detalle de una receta: la receta con sus ingredientes ya resueltos
 * hasta el nombre del alimento.
 */
data class RecetaDetalle(
    @Embedded val receta: Receta,
    @Relation(parentColumn = "id", entity = Ingrediente::class, entityColumn = "recetaId")
    val ingredientes: List<IngredienteConAlimento>
)
