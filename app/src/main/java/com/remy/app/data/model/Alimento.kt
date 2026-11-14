package com.remy.app.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Alimento de la despensa y del catálogo de recetas.
 *
 * [enDespensa] distingue la despensa del usuario (true) del catálogo que las
 * recetas usan como ingredientes (false): borrar de la despensa oculta el
 * alimento sin destruir las recetas que lo referencian, y el nombre sigue
 * disponible para mostrar los ingredientes que faltan en las sugerencias (#34).
 */
@Entity(tableName = "alimentos")
data class Alimento(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val kcal: Int = 0,
    val proteinas: Int = 0,
    val regionOrigen: String = "",
    val tipoPlato: String = "",
    val cantidad: Double,
    val unidad: String,
    @ColumnInfo(defaultValue = "1") val enDespensa: Boolean = true
)
