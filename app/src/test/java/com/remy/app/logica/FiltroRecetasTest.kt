package com.remy.app.logica

import com.remy.app.data.FiltrosSugerencias
import com.remy.app.data.Receta
import com.remy.app.data.RecetaConIngredientes
import org.junit.Assert.assertEquals
import org.junit.Test

class FiltroRecetasTest {

    private fun receta(
        id: Long,
        tipo: String = "Principal",
        region: String = "Mediterráneo",
        tiempo: Int = 30,
        kcal: Int = 400,
        proteinas: Int = 20
    ) = RecetaConIngredientes(
        receta = Receta(
            id = id,
            nombre = "Receta $id",
            tipoPlato = tipo,
            regionOrigen = region,
            tiempoMinutos = tiempo,
            kcal = kcal,
            proteinas = proteinas
        ),
        ingredientes = emptyList()
    )

    @Test
    fun sinFiltros_devuelveTodasLasRecetas() {
        val recetas = listOf(receta(1), receta(2, tipo = "Postre"))

        val resultado = filtrarRecetas(recetas, FiltrosSugerencias())

        assertEquals(recetas, resultado)
    }

    @Test
    fun filtraPorTipoDePlato() {
        val recetas = listOf(receta(1, tipo = "Entrante"), receta(2, tipo = "Postre"))

        val resultado = filtrarRecetas(recetas, FiltrosSugerencias(tiposDePlato = setOf("Postre")))

        assertEquals(listOf(2L), resultado.map { it.receta.id })
    }

    @Test
    fun filtraPorRegionSinDistinguirMayusculas() {
        val recetas = listOf(receta(1, region = "Mediterráneo"), receta(2, region = "Asiático"))

        val resultado = filtrarRecetas(recetas, FiltrosSugerencias(region = "mediterráneo"))

        assertEquals(listOf(1L), resultado.map { it.receta.id })
    }

    @Test
    fun filtraPorTiempoMaximoYDescartaLasQueNoLoTienen() {
        val recetas = listOf(
            receta(1, tiempo = 15),
            receta(2, tiempo = 45),
            receta(3, tiempo = 0)
        )

        val resultado = filtrarRecetas(recetas, FiltrosSugerencias(tiempoMaximoMinutos = 30))

        assertEquals(listOf(1L), resultado.map { it.receta.id })
    }

    @Test
    fun filtraPorCaloriasMaximasYProteinasMinimas() {
        val recetas = listOf(
            receta(1, kcal = 250, proteinas = 25),
            receta(2, kcal = 600, proteinas = 30),
            receta(3, kcal = 250, proteinas = 5)
        )

        val resultado = filtrarRecetas(
            recetas,
            FiltrosSugerencias(kcalMaximas = 300, proteinasMinimas = 20)
        )

        assertEquals(listOf(1L), resultado.map { it.receta.id })
    }

    @Test
    fun combinaTodosLosFiltros() {
        val recetas = listOf(
            receta(1, tipo = "Postre", region = "Europeo", tiempo = 20, kcal = 300, proteinas = 15),
            receta(2, tipo = "Postre", region = "Europeo", tiempo = 50, kcal = 300, proteinas = 15),
            receta(3, tipo = "Entrante", region = "Europeo", tiempo = 20, kcal = 300, proteinas = 15)
        )

        val resultado = filtrarRecetas(
            recetas,
            FiltrosSugerencias(
                tiposDePlato = setOf("Postre"),
                region = "Europeo",
                tiempoMaximoMinutos = 30,
                kcalMaximas = 400,
                proteinasMinimas = 10
            )
        )

        assertEquals(listOf(1L), resultado.map { it.receta.id })
    }

    @Test
    fun losLimitesACeroNoFiltran() {
        val recetas = listOf(receta(1, tiempo = 0, kcal = 0, proteinas = 0))

        val resultado = filtrarRecetas(recetas, FiltrosSugerencias())

        assertEquals(listOf(1L), resultado.map { it.receta.id })
    }
}
