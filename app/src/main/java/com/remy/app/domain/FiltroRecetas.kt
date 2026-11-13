package com.remy.app.domain

import com.remy.app.data.FiltrosSugerencias
import com.remy.app.data.RecetaConIngredientes

/**
 * Aplica los filtros de #37 al catálogo de recetas antes de clasificarlas.
 *
 * Los valores a cero no limitan y una región vacía acepta todas. Cuando hay un
 * límite activo, las recetas sin ese dato (tiempo o calorías a cero) se
 * descartan: no se puede asegurar que lo cumplan. Los filtros de macros se
 * evalúan por ración.
 */
fun filtrarRecetas(
    recetas: List<RecetaConIngredientes>,
    filtros: FiltrosSugerencias
): List<RecetaConIngredientes> = recetas.filter { item ->
    val receta = item.receta
    val porciones = receta.porciones.coerceAtLeast(1)
    val proteinaRacion = receta.proteinas.toDouble() / porciones
    val hidratosRacion = receta.hidratos.toDouble() / porciones
    val grasasRacion = receta.grasas.toDouble() / porciones

    val tipoCorrecto = filtros.tiposDePlato.isEmpty() || receta.tipoPlato in filtros.tiposDePlato
    val regionCorrecta = filtros.region.isBlank() ||
        receta.regionOrigen.equals(filtros.region, ignoreCase = true)
    val tiempoCorrecto = filtros.tiempoMaximoMinutos <= 0 ||
        (receta.tiempoMinutos > 0 && receta.tiempoMinutos <= filtros.tiempoMaximoMinutos)
    val kcalCorrectas = filtros.kcalMaximas <= 0 ||
        (receta.kcal > 0 && receta.kcal <= filtros.kcalMaximas)
    val proteinasCorrectas = filtros.proteinasMinimas <= 0 ||
        receta.proteinas >= filtros.proteinasMinimas
    val altaProteinaCorrecta = !filtros.altaProteina ||
        proteinaRacion >= FiltrosSugerencias.UMBRAL_ALTA_PROTEINA
    val bajaHidratosCorrecta = !filtros.bajaHidratos ||
        hidratosRacion <= FiltrosSugerencias.UMBRAL_BAJA_HIDRATOS
    val bajaGrasaCorrecta = !filtros.bajaGrasa ||
        grasasRacion <= FiltrosSugerencias.UMBRAL_BAJA_GRASA

    tipoCorrecto && regionCorrecta && tiempoCorrecto && kcalCorrectas &&
        proteinasCorrectas && altaProteinaCorrecta && bajaHidratosCorrecta && bajaGrasaCorrecta
}
