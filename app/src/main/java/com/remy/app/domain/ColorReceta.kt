package com.remy.app.domain

/**
 * Número de tonos pasteles disponibles para las miniaturas de receta.
 * Los colores en sí viven en el tema (`ui/theme/Color.kt`).
 */
const val TONOS_PASTEL = 6

/**
 * Elige el tono pastel de la miniatura de una receta a partir de su nombre.
 *
 * El resultado depende solo del nombre, así que una receta conserva siempre
 * el mismo color entre reinicios y la lista se ve estable al abrirla.
 *
 * @param nombre nombre de la receta.
 * @return índice de tono entre 0 y [TONOS_PASTEL] - 1.
 */
fun indiceTonoPastel(nombre: String): Int {
    if (nombre.isBlank()) return 0
    var suma = 0
    for (caracter in nombre) {
        suma = (suma * 31 + caracter.code) % (TONOS_PASTEL * 997)
    }
    return suma % TONOS_PASTEL
}
