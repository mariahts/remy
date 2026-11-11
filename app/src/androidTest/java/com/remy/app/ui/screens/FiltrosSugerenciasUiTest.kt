package com.remy.app.ui.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.remy.app.data.FiltrosSugerencias
import com.remy.app.ui.theme.REMYTheme
import com.remy.app.ui.viewmodel.GruposSugerencias
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** Pruebas de interfaz de la pantalla de filtros (#37 y #38). */
class FiltrosSugerenciasUiTest {

    @get:Rule
    val compose = createComposeRule()

    private val grupos = GruposSugerencias()
    private val regiones = listOf("Asiático", "Cantábrico", "Europeo", "Mediterráneo", "Tropical")

    private fun mostrar(
        filtros: FiltrosSugerencias = FiltrosSugerencias(),
        onTipo: (String) -> Unit = {},
        onTiempo: (Int) -> Unit = {},
        onLimpiar: () -> Unit = {}
    ) {
        compose.setContent {
            REMYTheme {
                PantallaFiltros(
                    filtros = filtros,
                    grupos = grupos,
                    regiones = regiones,
                    onAlternarTipo = onTipo,
                    onCambiarMaximo = {},
                    onCambiarTiempo = onTiempo,
                    onCambiarCalorias = {},
                    onCambiarProteinas = {},
                    onCambiarRegion = {},
                    onLimpiarFiltros = onLimpiar,
                    onMostrar = {}
                )
            }
        }
    }

    @Test
    fun muestraTodosLosFiltrosYLaCuentaDeRecetas() {
        mostrar()

        compose.onNodeWithText("Tipo de plato").assertExists()
        compose.onNodeWithText("Máx. faltantes").assertExists()
        compose.onNodeWithText("Tiempo máximo").assertExists()
        compose.onNodeWithText("Calorías máximas").assertExists()
        compose.onNodeWithText("Proteínas mínimas").assertExists()
        compose.onNodeWithText("Región").assertExists()
        compose.onNodeWithText("0 recetas encajan con estos filtros").assertExists()
    }

    @Test
    fun alPulsarUnTipoDePlato_avisaAlViewModel() {
        var elegido = ""
        mostrar(onTipo = { elegido = it })

        compose.onNodeWithText("Postre").performScrollTo().performClick()

        assertEquals("Postre", elegido)
    }

    @Test
    fun alExpandirElTiempo_sePuedeElegirUnLimite() {
        var minutos = -1
        mostrar(onTiempo = { minutos = it })

        compose.onNodeWithText("Tiempo máximo").performScrollTo().performClick()
        compose.onNodeWithText("15 min").performScrollTo().performClick()

        assertEquals(15, minutos)
    }

    @Test
    fun conFiltrosActivos_sePuedeLimpiar() {
        var limpiado = false
        mostrar(
            filtros = FiltrosSugerencias(tiposDePlato = setOf("Postre")),
            onLimpiar = { limpiado = true }
        )

        compose.onNodeWithText("Limpiar filtros").performScrollTo().performClick()

        assertEquals(true, limpiado)
    }

    @Test
    fun sinFiltros_noSeMuestraElBotonDeLimpiar() {
        mostrar()

        compose.onNodeWithText("Limpiar filtros").assertDoesNotExist()
    }
}
