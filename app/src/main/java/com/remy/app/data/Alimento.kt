package com.remy.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alimentos")
data class Alimento(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val kcal: Int,
    val proteinas: Int,
    val regionOrigen: String,
    val tipoPlato: String,
    val cantidad: Double,
    val unidad: String // "g", "u", "ml"...
)
