package com.remy.app.ui.components

/** Muestra la cantidad sin decimales cuando es entera (p. ej. 2.0 → "2"). */
internal fun Double.formatoCantidad(): String =
    if (this % 1.0 == 0.0) toLong().toString() else toString()
