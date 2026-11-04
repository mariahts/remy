package com.remy.app.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters

/**
 * Catálogo de recetas. Campos del diagrama entidad-relación (#11):
 * Id, Nombre, Cantidad_kcals, Cantidad_proteinas, Region_origen, Tipo_plato.
 * Tiempo y pasos se añaden en la #30 para el detalle de la #32.
 */
@Entity(tableName = "recetas")
@TypeConverters(ConvertidoresReceta::class)
data class Receta(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val kcal: Int = 0,
    val proteinas: Int = 0,
    val regionOrigen: String = "",
    val tipoPlato: String = "",
    @ColumnInfo(defaultValue = "0") val tiempoMinutos: Int = 0,
    @ColumnInfo(defaultValue = "''") val pasos: List<String> = emptyList()
)

/** Guarda los pasos como líneas de texto, uno por línea. */
object ConvertidoresReceta {
    @TypeConverter
    fun dePasos(pasos: List<String>): String = pasos.joinToString("\n")

    @TypeConverter
    fun aPasos(texto: String): List<String> =
        if (texto.isEmpty()) emptyList() else texto.split("\n")
}
