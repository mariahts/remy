package com.remy.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Catálogo de recetas. Campos del diagrama entidad-relación (#11):
 * Id, Nombre, Cantidad_kcals, Cantidad_proteinas, Region_origen, Tipo_plato.
 */
@Entity(tableName = "recetas")
data class Receta(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val kcal: Int = 0,
    val proteinas: Int = 0,
    val regionOrigen: String = "",
    val tipoPlato: String = ""
)
