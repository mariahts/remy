package com.remy.app.data

import androidx.room.Embedded
import androidx.room.Relation

/** Una receta con su lista de ingredientes, resuelta por Room. */
data class RecetaConIngredientes(
    @Embedded val receta: Receta,
    @Relation(parentColumn = "id", entityColumn = "recetaId")
    val ingredientes: List<Ingrediente>
)
