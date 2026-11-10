package com.remy.app.ui.navigation

object Routes {
    const val DESPENSA = "despensa"
    const val RECETAS = "recetas"
    const val COCINADAS = "cocinadas"
    const val SUGERENCIAS = "sugerencias"
    const val RECETA_DETALLE = "receta/{id}"

    /** Ruta del detalle de una receta concreta. */
    fun detalleReceta(id: Long) = "receta/$id"
}


