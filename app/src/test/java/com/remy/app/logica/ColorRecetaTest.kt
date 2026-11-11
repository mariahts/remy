package com.remy.app.logica

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pruebas del reparto de tonos pasteles de las miniaturas de receta.
 */
class ColorRecetaTest {

    @Test
    fun `la misma receta siempre recibe el mismo tono`() {
        assertEquals(
            indiceTonoPastel("Pollo al curry"),
            indiceTonoPastel("Pollo al curry")
        )
    }

    @Test
    fun `el tono siempre esta dentro del rango de colores`() {
        val nombres = listOf(
            "Arroz con leche", "Arroz con pollo", "Pollo al curry", "Bacalao al ajillo",
            "Tortilla de patatas", "Pasta con tomate", "Lentejas", "Sopa de verduras",
            "Ensalada", "Merluza al horno"
        )
        nombres.forEach { nombre ->
            val indice = indiceTonoPastel(nombre)
            assertTrue(
                "El tono de $nombre debe estar entre 0 y ${TONOS_PASTEL - 1}",
                indice in 0 until TONOS_PASTEL
            )
        }
    }

    @Test
    fun `un nombre vacio usa el primer tono`() {
        assertEquals(0, indiceTonoPastel(""))
        assertEquals(0, indiceTonoPastel("   "))
    }

    @Test
    fun `el reparto usa varios tonos y no concentrates todas las recetas`() {
        val nombres = listOf(
            "Arroz con leche", "Arroz con pollo", "Pollo al curry", "Bacalao al ajillo",
            "Tortilla de patatas", "Pasta con tomate", "Lentejas", "Sopa de verduras",
            "Ensalada", "Merluza al horno", "Pollo al curry con arroz", "Tortilla de arroz"
        )
        val tonos = nombres.map { indiceTonoPastel(it) }.toSet()
        assertTrue("El reparto debe usar al menos tres tonos distintos", tonos.size >= 3)
    }
}
