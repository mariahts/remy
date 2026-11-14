package com.remy.app.ui.components

import java.time.Month
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FechaEspanolTest {

    /**
     * 15 de junio de 2026 a mediodía UTC: en cualquier huso horario del planeta
     * la fecha sigue cayendo en junio de 2026, así que el año es comprobable.
     */
    private val instante = 1_781_524_800_000L

    @Test
    fun fechaLarga_llevaDiaMesEnEspanolYAnio() {
        val texto = fechaEnEspanol(instante)
        assertTrue(
            "Formato inesperado: $texto",
            Regex("""\d{1,2} de [a-záéíóúñ]+ de \d{4}""").containsMatchIn(texto)
        )
    }

    @Test
    fun fechaLarga_noMezclaConLaHora() {
        assertFalse("La fecha no debe llevar hora: ${fechaEnEspanol(instante)}", fechaEnEspanol(instante).contains(":"))
    }

    @Test
    fun fechaLarga_noSaleDelAnioEsperado() {
        assertTrue("Debe terminar en 2026: ${fechaEnEspanol(instante)}", fechaEnEspanol(instante).endsWith("de 2026"))
    }

    @Test
    fun nombreDelMes_saleEnEspanol() {
        assertEquals("noviembre", mesEnEspanol(Month.NOVEMBER))
        assertEquals("enero", mesEnEspanol(Month.JANUARY))
    }

    @Test
    fun nombreDelMes_noEmpiezaPorMayuscula() {
        assertEquals("julio", mesEnEspanol(Month.JULY))
    }
}
