package com.remy.app.data.model

import kotlin.math.roundToInt

/**
 * Macros de un alimento del catálogo, por 100 g o 100 ml.
 *
 * Es la tabla nutricional que se guarda en `assets/recetas.json`. Las recetas
 * no llevan sus macros estimados a mano: se calculan sumando los macros de sus
 * ingredientes, de forma que el valor que ve el usuario siempre encaja con lo
 * que lleva la receta.
 */
data class MacrosAlimento(
    val kcal: Double,
    val proteinas: Double,
    val hidratos: Double,
    val grasas: Double
)

/** Macros de una receta completa (totales de toda la receta). */
data class MacrosReceta(
    val kcal: Int,
    val proteinas: Int,
    val hidratos: Int,
    val grasas: Int
)

/**
 * Calcula los macros de una receta a partir de sus ingredientes.
 *
 * Cada ingrediente aporta la parte proporcional a su cantidad: un alimento con
 * 35 kcal por 100 g aporta 35 * (cantidad / 100) kcal a la receta. Los
 * ingredientes cuyo alimento no está en el catálogo se ignoran.
 */
fun macrosDeReceta(
    ingredientes: List<Ingrediente>,
    macrosPorAlimento: Map<Long, MacrosAlimento>
): MacrosReceta {
    var kcal = 0.0
    var proteinas = 0.0
    var hidratos = 0.0
    var grasas = 0.0
    for (ingrediente in ingredientes) {
        val macros = macrosPorAlimento[ingrediente.alimentoId] ?: continue
        val factor = ingrediente.cantidad / 100.0
        kcal += macros.kcal * factor
        proteinas += macros.proteinas * factor
        hidratos += macros.hidratos * factor
        grasas += macros.grasas * factor
    }
    return MacrosReceta(
        kcal = kcal.roundToInt(),
        proteinas = proteinas.roundToInt(),
        hidratos = hidratos.roundToInt(),
        grasas = grasas.roundToInt()
    )
}
