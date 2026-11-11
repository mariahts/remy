package com.remy.app.ui

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.remy.app.ui.components.ContenidoConEstado
import com.remy.app.ui.components.PantallaCargando
import com.remy.app.ui.components.PantallaError
import com.remy.app.ui.theme.REMYTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** Pruebas de interfaz de los estados de carga y error (#38 y #46). */
class EstadoCargaUiTest {

    @get:Rule
    val compose = createComposeRule()

    @Test
    fun cargando_muestraElMensaje() {
        compose.setContent {
            REMYTheme { PantallaCargando("Cargando tus recetas…") }
        }

        compose.onNodeWithText("Cargando tus recetas…").assertIsDisplayed()
    }

    @Test
    fun error_muestraMensajeYPermiteReintentar() {
        var reintentos = 0
        compose.setContent {
            REMYTheme {
                PantallaError("No se ha podido leer Room.", onReintentar = { reintentos++ })
            }
        }

        compose.onNodeWithText("No se han podido cargar los datos").assertIsDisplayed()
        compose.onNodeWithText("No se ha podido leer Room.").assertIsDisplayed()
        compose.onNodeWithText("Reintentar").performClick()

        assertEquals(1, reintentos)
    }

    @Test
    fun datos_muestraElContenidoReal() {
        compose.setContent {
            REMYTheme {
                ContenidoConEstado(
                    estado = EstadoCarga.Datos(listOf("Arroz")),
                    onReintentar = {}
                ) { alimentos ->
                    Text(alimentos.first())
                }
            }
        }

        compose.onNodeWithText("Arroz").assertIsDisplayed()
    }
}
