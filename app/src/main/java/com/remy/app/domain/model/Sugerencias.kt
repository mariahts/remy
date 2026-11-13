package com.remy.app.domain.model

import com.remy.app.data.RecetaConIngredientes

/** Receta que ya se puede cocinar: tiene todos los ingredientes en la despensa. */
data class RecetaCompleta(val receta: RecetaConIngredientes)

/** Receta a la que le faltan ingredientes, pero dentro del máximo permitido. */
data class RecetaCasiCocinable(val receta: RecetaConIngredientes, val faltantes: List<Faltante>)

/** Los dos grupos que devuelve el algoritmo de sugerencias. */
data class ResultadoSugerencias(
    val completas: List<RecetaCompleta>,
    val casiCocinables: List<RecetaCasiCocinable>
)
