package com.remy.app.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters

/**
 * Catálogo de recetas. Campos del diagrama entidad-relación (#11):
 * Id, Nombre, Cantidad_kcals, Cantidad_proteinas, Region_origen, Tipo_plato.
 * Tiempo y pasos se añaden en la #30 para el detalle de la #32.
 * Fecha y foto de cocción se añaden en la #35: mientras `fechaCocinada` sea
 * null, la receta sigue sin cocinar.
 * `kcal`, `proteinas`, `hidratos` y `grasas` son totales de la receta;
 * `porciones` indica para cuántas raciones, de modo que la interfaz pueda
 * mostrarlos por porción.
 */
@Entity(tableName = "recetas")
@TypeConverters(ConvertidoresReceta::class)
data class Receta(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val kcal: Int = 0,
    val proteinas: Int = 0,
    @ColumnInfo(defaultValue = "0") val hidratos: Int = 0,
    @ColumnInfo(defaultValue = "0") val grasas: Int = 0,
    val regionOrigen: String = "",
    val tipoPlato: String = "",
    @ColumnInfo(defaultValue = "0") val tiempoMinutos: Int = 0,
    @ColumnInfo(defaultValue = "''") val pasos: List<String> = emptyList(),
    /** Número de raciones que rinde la receta (mínimo 1). */
    @ColumnInfo(defaultValue = "4") val porciones: Int = 4,
    /** Instante (en milisegundos) en que se cocinó por última vez; null si nunca. */
    val fechaCocinada: Long? = null,
    /** Ruta absoluta de la foto del plato dentro del almacenamiento interno. */
    val fotoCocinada: String? = null
)

/** Guarda los pasos como líneas de texto, uno por línea. */
object ConvertidoresReceta {
    @TypeConverter
    fun dePasos(pasos: List<String>): String = pasos.joinToString("\n")

    @TypeConverter
    fun aPasos(texto: String): List<String> =
        if (texto.isEmpty()) emptyList() else texto.split("\n")
}
