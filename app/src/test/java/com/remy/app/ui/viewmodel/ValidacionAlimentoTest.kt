package com.remy.app.ui.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ValidacionAlimentoTest {
    @Test fun nombreVacio_daError() =
        assertNotNull(validarAlimento("", "5", "g").nombre)

    @Test fun cantidadCero_daError() =
        assertNotNull(validarAlimento("Arroz", "0", "g").cantidad)

    @Test fun cantidadNoNumerica_daError() =
        assertNotNull(validarAlimento("Arroz", "abc", "g").cantidad)

    @Test fun unidadVacia_daError() =
        assertNotNull(validarAlimento("Arroz", "5", " ").unidad)

    @Test fun datosValidos_noDanErrores() =
        assertFalse(validarAlimento("Arroz", "500", "g").hayErrores)

    @Test fun cantidadConComa_seAcepta() =
        assertFalse(validarAlimento("Leche", "1,5", "l").hayErrores)

    @Test fun aviso_nombreExacto_noAvisa() =
        assertNull(avisoNombre("Atún", listOf("Atún", "Tomate")))

    @Test fun aviso_faltaTilde_loIndica() =
        assertEquals("Te falta una tilde: «Atún»", avisoNombre("Atun", listOf("Atún")))

    @Test fun aviso_minuscula_loIndica() =
        assertEquals(
            "Los ingredientes tienen que empezar por mayúsculas: «Tomate»",
            avisoNombre("tomate", listOf("Tomate"))
        )

    @Test fun aviso_todoMayusculas_loIndica() =
        assertEquals(
            "Los ingredientes tienen que empezar por mayúsculas: «Tomate»",
            avisoNombre("TOMATE", listOf("Tomate"))
        )

    @Test fun aviso_sinCoincidencia_noAvisa() =
        assertNull(avisoNombre("Pizza", listOf("Atún", "Tomate")))

    @Test fun aviso_nombreVacio_noAvisa() =
        assertNull(avisoNombre("", listOf("Atún")))

    @Test fun normalizarNombre_quitaTildesYMayusculas() {
        assertEquals(normalizarNombre("Atún"), normalizarNombre("atun"))
        assertEquals(normalizarNombre("Jamón"), normalizarNombre("JAMON"))
        assertFalse(normalizarNombre("Tomate") == normalizarNombre("Lechuga"))
    }
}
