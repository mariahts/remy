package com.remy.app.logica

import com.remy.app.data.FiltrosSugerencias
import com.remy.app.data.RecetaConIngredientes

/**
 * Aplica los filtros de #37 al catálogo de recetas antes de clasificarlas.
 *
 * Los valores a cero no limitan y una región vacía acepta todas. Cuando hay un
 * límite activo, las recetas sin ese dato (tiempo o calorías a cero) se
 * descartan: no se puede asegurar que lo cumplan.
 */
fun filtrarRecetas(
    recetas: List<RecetaConIngredientes>,
    filtros: FiltrosSugerencias
): List<RecetaConIngredientes> = recetas.filter { item ->
    val receta = item.receta
    val tipoCorrecto = filtros.tiposDePlato.isEmpty() || receta.tipoPlato in filtros.tiposDePlato
    val regionCorrecta = filtros.region.isBlank() ||
        receta.regionOrigen.equals(filtros.region, ignoreCase = true)
    val tiempoCorrecto = filtros.tiempoMaximoMinutos <= 0 ||
        (receta.tiempoMinutos > 0 && receta.tiempoMinutos <= filtros.tiempoMaximoMinutos)
    val kcalCorrectas = filtros.kcalMaximas <= 0 ||
        (receta.kcal > 0 && receta.kcal <= filtros.kcalMaximas)
    val proteinasCorrectas = filtros.proteinasMinimas <= 0 ||
        receta.proteinas >= filtros.proteinasMinimas

    tipoCorrecto && regionCorrecta && tiempoCorrecto && kcalCorrectas && proteinasCorrectas
}
