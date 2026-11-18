package com.remy.app.ui.components

import kotlin.math.roundToLong

/** Muestra la cantidad sin decimales cuando es entera (p. ej. 2.0 → "2"). */
internal fun Double.formatoCantidad(): String =
    if (this % 1.0 == 0.0) toLong().toString() else toString()

/** Cantidad redondeada al entero más cercano para mostrar ingredientes en recetas. */
internal fun Double.formatoCantidadReceta(): String = roundToLong().toString()

/**
 * Presenta una cantidad con la unidad más cómoda: cuando llega a 1 kg o 1 l se muestra
 * como kg o l en lugar de g o ml (p. ej. 1500 g → 1.5 kg, 2000 ml → 2 l).
 * Devuelve la cantidad y la unidad tal como deben presentarse.
 */
internal fun cantidadPresentacion(cantidad: Double, unidad: String): Pair<Double, String> =
    when (unidad.trim().lowercase()) {
        "g" -> if (cantidad >= 1000.0) cantidad / 1000.0 to "kg" else cantidad to unidad
        "ml" -> if (cantidad >= 1000.0) cantidad / 1000.0 to "l" else cantidad to unidad
        else -> cantidad to unidad
    }
