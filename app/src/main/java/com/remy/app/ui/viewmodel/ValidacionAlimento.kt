package com.remy.app.ui.viewmodel

import java.text.Normalizer
import java.util.Locale

data class ErroresAlimento(
    val nombre: String? = null,
    val cantidad: String? = null,
    val unidad: String? = null,
) {
    val hayErrores: Boolean get() = nombre != null || cantidad != null || unidad != null
}

fun validarAlimento(nombre: String, cantidad: String, unidad: String): ErroresAlimento {
    val valor = cantidad.replace(',', '.').toDoubleOrNull()
    return ErroresAlimento(
        nombre = if (nombre.isBlank()) "El nombre es obligatorio" else null,
        cantidad = if (valor == null || valor <= 0.0) "La cantidad debe ser mayor que cero" else null,
        unidad = if (unidad.isBlank()) "La unidad es obligatoria" else null,
    )
}

/** Quita tildes y pasa a minúsculas: «Atún» y «atun» se comparan igual. */
private fun normalizarNombre(texto: String): String =
    Normalizer.normalize(texto.trim().lowercase(Locale.getDefault()), Normalizer.Form.NFD)
        .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")

/**
 * Aviso de corrección cuando el nombre escrito coincide con uno del catálogo
 * pero no exactamente (letras, tildes o mayúsculas): la búsqueda sigue siendo
 * sensible a la escritura y aquí se indica cómo corregirla.
 *
 * Devuelve `null` si el nombre ya es exacto o si no se parece a ningún
 * alimento del catálogo (entonces se guarda tal cual, sin aviso).
 */
fun avisoNombre(escrito: String, catalogo: List<String>): String? {
    val buscado = escrito.trim()
    if (buscado.isEmpty()) return null
    val candidato = catalogo.firstOrNull { normalizarNombre(it) == normalizarNombre(buscado) } ?: return null
    if (candidato.trim() == buscado) return null
    return if (buscado.lowercase(Locale.getDefault()) != candidato.lowercase(Locale.getDefault())) {
        "Te falta una tilde: «$candidato»"
    } else {
        "Los ingredientes tienen que empezar por mayúsculas: «$candidato»"
    }
}
