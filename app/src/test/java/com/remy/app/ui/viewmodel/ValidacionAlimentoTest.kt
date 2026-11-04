package com.remy.app.ui.viewmodel

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
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
}