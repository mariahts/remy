package com.remy.app.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Línea de ingrediente de una receta: la relación N:M Receta–Alimento del
 * diagrama (#11), con los atributos Unidades_alimento y Peso_alimento.
 *
 * Referencia a [Alimento] por identificador y nunca por nombre, para que la
 * comparación con la despensa se haga por id.
 */
@Entity(
    tableName = "ingredientes",
    foreignKeys = [
        ForeignKey(
            entity = Receta::class,
            parentColumns = ["id"],
            childColumns = ["recetaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Alimento::class,
            parentColumns = ["id"],
            childColumns = ["alimentoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("recetaId"), Index("alimentoId")]
)
data class Ingrediente(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recetaId: Long,
    val alimentoId: Long,
    val cantidad: Double,
    val unidad: String
)
