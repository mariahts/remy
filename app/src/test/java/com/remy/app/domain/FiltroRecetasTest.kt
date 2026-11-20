package com.remy.app.domain

import com.remy.app.data.model.FiltrosSugerencias
import com.remy.app.data.model.Receta
import com.remy.app.data.model.RecetaConIngredientes
import org.junit.Assert.assertEquals
import org.junit.Test

class FiltroRecetasTest {

    private fun receta(
        id: Long,
        tipo: String = "Principal",
        region: String = "Mediterráneo",
        tiempo: Int = 30,
        kcal: Int = 400,
        proteinas: Int = 20,
        hidratos: Int = 0,
        grasas: Int = 0,
        porciones: Int = 4
    ) = RecetaConIngredientes(
        receta = Receta(
            id = id,
            nombre = "Receta $id",
            tipoPlato = tipo,
            regionOrigen = region,
            tiempoMinutos = tiempo,
            kcal = kcal,
            proteinas = proteinas,
            hidratos = hidratos,
            grasas = grasas,
            porciones = porciones
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

    @Test
    fun filtrosDeMacrosPorRacion() {
        // proteínas: 100 g / 4 = 25 por ración (pasa) y 80 / 4 = 20 (no pasa);
        // hidratos: 120 / 4 = 30 (pasa) y 124 / 4 = 31 (no pasa);
        // grasas: 60 / 4 = 15 (pasa) y 64 / 4 = 16 (no pasa)
        val base = receta(1, proteinas = 100, hidratos = 120, grasas = 60)
        val noPasa = receta(2, proteinas = 80, hidratos = 124, grasas = 64)
        val recetas = listOf(base, noPasa)

        assertEquals(
            listOf(1L),
            filtrarRecetas(recetas, FiltrosSugerencias(altaProteina = true)).map { it.receta.id }
        )
        assertEquals(
            listOf(1L),
            filtrarRecetas(recetas, FiltrosSugerencias(bajaHidratos = true)).map { it.receta.id }
        )
        assertEquals(
            listOf(1L),
            filtrarRecetas(recetas, FiltrosSugerencias(bajaGrasa = true)).map { it.receta.id }
        )
    }
}
