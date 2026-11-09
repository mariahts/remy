package com.remy.app.data

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Una receta con su lista de ingredientes, resuelta por Room.
 * Relación anidada hasta el nombre del alimento (para mostrar los chips
 * de ingredientes en las tarjetas del mockup §2.7 sin tocar el esquema).
 */
data class RecetaConIngredientes(
    @Embedded val receta: Receta,
    @Relation(parentColumn = "id", entity = Ingrediente::class, entityColumn = "recetaId")
    val ingredientes: List<IngredienteConAlimento>
)
