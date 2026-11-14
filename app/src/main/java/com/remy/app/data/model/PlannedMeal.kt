package com.remy.app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Momento del día para el que se planifica una comida. */
enum class TipoComida {
    DESAYUNO,
    COMIDA,
    CENA
}

/**
 * Comida planificada para un día concreto del calendario.
 * `fecha` se guarda como texto ISO "yyyy-MM-dd" para facilitar las consultas por día.
 */
@Entity(
    tableName = "comidas_planificadas",
    foreignKeys = [
        ForeignKey(
            entity = Receta::class,
            parentColumns = ["id"],
            childColumns = ["recetaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("fecha"), Index("recetaId"), Index(value = ["fecha", "tipoComida"], unique = true)]
)
data class PlannedMeal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fecha: String,
    val tipoComida: TipoComida,
    val recetaId: Long
)
