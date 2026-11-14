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
    fun filtraAltaEnProteinaPorRacion() {
        // 100g / 4 raciones = 25g por ración (pasa), 80g / 4 = 20g (no pasa)
        val recetas = listOf(
            receta(1, proteinas = 100, porciones = 4),
            receta(2, proteinas = 80, porciones = 4)
        )

        val resultado = filtrarRecetas(recetas, FiltrosSugerencias(altaProteina = true))

        assertEquals(listOf(1L), resultado.map { it.receta.id })
    }

    @Test
    fun filtraBajaEnHidratosPorRacion() {
        // 120g / 4 = 30g por ración (pasa), 124g / 4 = 31g (no pasa)
        val recetas = listOf(
            receta(1, hidratos = 120, porciones = 4),
            receta(2, hidratos = 124, porciones = 4)
        )

        val resultado = filtrarRecetas(recetas, FiltrosSugerencias(bajaHidratos = true))

        assertEquals(listOf(1L), resultado.map { it.receta.id })
    }

    @Test
    fun filtraBajaEnGrasaPorRacion() {
        // 60g / 4 = 15g por ración (pasa), 64g / 4 = 16g (no pasa)
        val recetas = listOf(
            receta(1, grasas = 60, porciones = 4),
            receta(2, grasas = 64, porciones = 4)
        )

        val resultado = filtrarRecetas(recetas, FiltrosSugerencias(bajaGrasa = true))

        assertEquals(listOf(1L), resultado.map { it.receta.id })
    }
}
