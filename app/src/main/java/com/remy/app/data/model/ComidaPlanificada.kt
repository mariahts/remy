package com.remy.app.data.model

import androidx.room.Embedded
import androidx.room.Relation

/** Comida planificada con la receta e ingredientes resueltos para la interfaz. */
data class ComidaPlanificadaConReceta(
    @Embedded val plan: PlannedMeal,
    @Relation(
        parentColumn = "recetaId",
        entity = Receta::class,
        entityColumn = "id"
    )
    val receta: RecetaConIngredientes
)
