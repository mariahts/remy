package com.remy.app.logica

import com.remy.app.data.Ingrediente
import com.remy.app.data.IngredienteConAlimento
import com.remy.app.data.Receta
import com.remy.app.data.RecetaConIngredientes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AlgoritmoSugerenciasTest {

    /** Despensa de prueba conocida: arroz (1) y huevo (2). */
    private val despensa = setOf(1L, 2L)

    private fun receta(id: Long, nombre: String, vararg alimentoIds: Long): RecetaConIngredientes =
        RecetaConIngredientes(
            receta = Receta(id = id, nombre = nombre),
            ingredientes = alimentoIds.mapIndexed { indice, alimentoId ->
                IngredienteConAlimento(
                    ingrediente = Ingrediente(
                        id = (indice + 1).toLong(),
                        recetaId = id,
                        alimentoId = alimentoId,
                        cantidad = 100.0,
                        unidad = "g"
                    ),
                    alimento = null
                )
            }
        )

    @Test
    fun casoPrincipal_completaCasiCocinableYExcluida() {
        val completa = receta(10, "Arroz con huevo", 1L, 2L)      // todo en la despensa
        val casi = receta(11, "Tortilla", 1L, 3L)                  // falta el huevo (3)
        val excluida = receta(12, "Guiso", 4L, 5L, 6L)             // faltan tres, con máximo 1

        val resultado = clasificarRecetas(listOf(completa, casi, excluida), despensa, maximoFaltantes = 1)

        assertEquals(listOf(10L), resultado.completas.map { it.receta.receta.id })
        assertEquals(listOf(11L), resultado.casiCocinables.map { it.receta.receta.id })
        assertEquals(listOf(Faltante(3L)), resultado.casiCocinables.first().faltantes)
        assertTrue(resultado.completas.isNotEmpty())
    }

    @Test
    fun laComparacionEsPorIdentificadorNoPorNombre() {
        // El alimento 99 se llama igual que el 1 ("Arroz") pero no está en la despensa
        val conIdDesconocido = receta(20, "Receta rara", 99L)

        val resultado = clasificarRecetas(listOf(conIdDesconocido), despensa, maximoFaltantes = 1)

        assertTrue(resultado.completas.isEmpty())
        assertEquals(listOf(Faltante(99L)), resultado.casiCocinables.first().faltantes)
    }

    @Test
    fun elMaximoDeFaltantesEsConfigurable() {
        val receta = receta(30, "Con dos faltantes", 7L, 8L)

        val conMaximo1 = clasificarRecetas(listOf(receta), despensa, maximoFaltantes = 1)
        assertTrue(conMaximo1.casiCocinables.isEmpty())
        assertTrue(conMaximo1.completas.isEmpty())

        val conMaximo2 = clasificarRecetas(listOf(receta), despensa, maximoFaltantes = 2)
        assertEquals(2, conMaximo2.casiCocinables.first().faltantes.size)
        assertTrue(conMaximo2.completas.isEmpty())
    }

    @Test
    fun losIngredientesDeLaDespensaNoSeMarcanComoFaltantes() {
        // Ingredientes repetidos del mismo alimento: cuenta una sola vez si no está
        val conRepetido = RecetaConIngredientes(
            receta = Receta(id = 40, nombre = "Doble"),
            ingredientes = listOf(
                IngredienteConAlimento(
                    Ingrediente(id = 1, recetaId = 40, alimentoId = 1L, cantidad = 50.0, unidad = "g"),
                    alimento = null
                ),
                IngredienteConAlimento(
                    Ingrediente(id = 2, recetaId = 40, alimentoId = 1L, cantidad = 50.0, unidad = "g"),
                    alimento = null
                )
            )
        )

        val resultado = clasificarRecetas(listOf(conRepetido), despensa, maximoFaltantes = 0)
        assertEquals(1, resultado.completas.size)
        assertTrue(resultado.casiCocinables.isEmpty())
    }
}
