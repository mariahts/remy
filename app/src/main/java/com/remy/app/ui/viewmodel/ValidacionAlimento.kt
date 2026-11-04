package com.remy.app.ui.viewmodel

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