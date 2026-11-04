package com.remy.app.logica

import com.remy.app.data.RecetaConIngredientes

/** Ingrediente que falta para poder cocinar una receta. */
data class Faltante(val alimentoId: Long)

/** Receta que ya se puede cocinar: tiene todos los ingredientes en la despensa. */
data class RecetaCompleta(val receta: RecetaConIngredientes)

/** Receta a la que le faltan ingredientes, pero dentro del máximo permitido. */
data class RecetaCasiCocinable(val receta: RecetaConIngredientes, val faltantes: List<Faltante>)

/** Los dos grupos que devuelve el algoritmo de sugerencias. */
data class ResultadoSugerencias(
    val completas: List<RecetaCompleta>,
    val casiCocinables: List<RecetaCasiCocinable>
)

/**
 * Clasifica las recetas según el contenido de la despensa (#33).
 *
 * La comparación se hace **solo por identificador de [Alimento][com.remy.app.data.Alimento]**,
 * nunca por nombre de texto. Una receta es:
 * - completa si todos sus ingredientes están en la despensa;
 * - casi cocinable si le faltan como mucho [maximoFaltantes] ingredientes;
 * - en otro caso, no se sugiere.
 *
 * Solo se mira la presencia del alimento en la despensa, no la cantidad.
 */
fun clasificarRecetas(
    recetas: List<RecetaConIngredientes>,
    alimentoIdsEnDespensa: Set<Long>,
    maximoFaltantes: Int
): ResultadoSugerencias {
    val completas = mutableListOf<RecetaCompleta>()
    val casiCocinables = mutableListOf<RecetaCasiCocinable>()

    for (item in recetas) {
        val faltantes = item.ingredientes
            .filter { it.alimentoId !in alimentoIdsEnDespensa }
            .map { Faltante(it.alimentoId) }

        when {
            faltantes.isEmpty() -> completas += RecetaCompleta(item)
            faltantes.size <= maximoFaltantes -> casiCocinables += RecetaCasiCocinable(item, faltantes)
        }
    }

    return ResultadoSugerencias(completas, casiCocinables)
}
