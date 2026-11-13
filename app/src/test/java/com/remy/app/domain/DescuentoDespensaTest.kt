package com.remy.app.domain

import com.remy.app.data.Alimento
import com.remy.app.data.Ingrediente
import com.remy.app.data.IngredienteConAlimento
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DescuentoDespensaTest {

    private fun linea(
        alimentoId: Long,
        nombre: String?,
        cantidad: Double,
        unidad: String,
        recetaId: Long = 1L,
        id: Long = 1L
    ) = IngredienteConAlimento(
        ingrediente = Ingrediente(
            id = id,
            recetaId = recetaId,
            alimentoId = alimentoId,
            cantidad = cantidad,
            unidad = unidad
        ),
        alimento = nombre?.let { Alimento(id = alimentoId, nombre = it, cantidad = 0.0, unidad = unidad, enDespensa = false) }
    )

    private fun alimento(
        id: Long,
        nombre: String,
        cantidad: Double,
        unidad: String,
        enDespensa: Boolean = true
    ) = Alimento(id = id, nombre = nombre, cantidad = cantidad, unidad = unidad, enDespensa = enDespensa)

    @Test
    fun descuentoNormal_restaCantidadEnMismaUnidad() {
        val despensa = mapOf(1L to alimento(1, "Arroz", 500.0, "g"))
        val ingredientes = listOf(linea(1, "Arroz", 200.0, "g"))

        val resultado = calcularDescuentoDespensa(ingredientes, 4, 4, despensa)

        assertEquals(1, resultado.actualizaciones.size)
        assertEquals(300.0, resultado.actualizaciones.first().cantidad, 0.0001)
        assertTrue(resultado.noDescontados.isEmpty())
    }

    @Test
    fun cantidadInsuficiente_quedaEnCero() {
        val despensa = mapOf(1L to alimento(1, "Huevo", 1.0, "ud"))
        val ingredientes = listOf(linea(1, "Huevo", 3.0, "ud"))

        val resultado = calcularDescuentoDespensa(ingredientes, 2, 2, despensa)

        assertEquals(1, resultado.actualizaciones.size)
        assertEquals(0.0, resultado.actualizaciones.first().cantidad, 0.0001)
        assertTrue(resultado.noDescontados.isEmpty())
    }

    @Test
    fun mismoAlimentoEnVariasLineas_descuentaElTotal() {
        val despensa = mapOf(1L to alimento(1, "Tomate", 500.0, "g"))
        val ingredientes = listOf(
            linea(1, "Tomate", 100.0, "g", id = 1),
            linea(1, "Tomate", 150.0, "g", id = 2)
        )

        val resultado = calcularDescuentoDespensa(ingredientes, 2, 2, despensa)

        assertEquals(1, resultado.actualizaciones.size)
        assertEquals(250.0, resultado.actualizaciones.first().cantidad, 0.0001)
        assertTrue(resultado.noDescontados.isEmpty())
    }

    @Test
    fun unidadesIncompatibles_seOmiteYSeInforma() {
        val despensa = mapOf(1L to alimento(1, "Leche", 1.0, "l"))
        val ingredientes = listOf(linea(1, "Leche", 200.0, "g"))

        val resultado = calcularDescuentoDespensa(ingredientes, 1, 1, despensa)

        assertTrue(resultado.actualizaciones.isEmpty())
        assertEquals(listOf("Leche"), resultado.noDescontados)
    }

    @Test
    fun escaladoPorRaciones_descuentaSegunRacionesDeseadas() {
        val despensa = mapOf(1L to alimento(1, "Arroz", 1000.0, "g"))
        // 200g para 4 raciones -> 100g para 2 raciones
        val ingredientes = listOf(linea(1, "Arroz", 200.0, "g"))

        val resultado = calcularDescuentoDespensa(ingredientes, porcionesReceta = 4, racionesDeseadas = 2, despensa)

        assertEquals(1, resultado.actualizaciones.size)
        assertEquals(900.0, resultado.actualizaciones.first().cantidad, 0.0001)
        assertTrue(resultado.noDescontados.isEmpty())
    }
}
