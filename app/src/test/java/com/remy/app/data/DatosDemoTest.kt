package com.remy.app.data

import com.remy.app.domain.clasificarRecetas
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Datos de la despensa de la cuenta de demostración.
 *
 * Antes se sembraba con unidades como "ud" y con "Pasta", que no existe en el
 * catálogo, así que ninguna receta llegaba a considerarse completa.
 */
class DatosDemoTest {

    /** Convierte la lista de demostración en alimentos con los identificadores del catálogo. */
    private fun despensaDemo(): List<Alimento> =
        DatosDemo.ALIMENTOS_DEMO.mapIndexed { indice, (nombre, cantidadUnidad) ->
            val (cantidad, unidad) = cantidadUnidad
            Alimento(
                id = indice + 1L,
                nombre = nombre,
                cantidad = cantidad,
                unidad = unidad,
                enDespensa = true
            )
        }

    private fun linea(
        id: Long,
        recetaId: Long,
        alimentoId: Long,
        nombre: String,
        cantidad: Double,
        unidad: String
    ) = com.remy.app.data.IngredienteConAlimento(
        ingrediente = Ingrediente(
            id = id,
            recetaId = recetaId,
            alimentoId = alimentoId,
            cantidad = cantidad,
            unidad = unidad
        ),
        alimento = Alimento(id = alimentoId, nombre = nombre, cantidad = 0.0, unidad = unidad, enDespensa = false)
    )

    @Test
    fun lasUnidadesDeLaDespensaDemoSonLasMismasQueUsanLasRecetas() {
        val unidades = DatosDemo.ALIMENTOS_DEMO.map { it.second.second }.toSet()
        assertEquals(setOf("g", "ml"), unidades)
    }

    @Test
    fun todosLosAlimentosDeLaDespensaDemoTienenCantidadPositiva() {
        assertTrue(DatosDemo.ALIMENTOS_DEMO.isNotEmpty())
        DatosDemo.ALIMENTOS_DEMO.forEach { (nombre, cantidadUnidad) ->
            assertTrue("La cantidad de $nombre debe ser mayor que cero", cantidadUnidad.first > 0.0)
        }
    }

    @Test
    fun conLaDespensaDemoSePuedeCocinarUnaRecetaCompleta() {
        // "Pollo al curry": pechuga de pollo, cebolla, ajo y aceite de oliva.
        val receta = RecetaConIngredientes(
            receta = Receta(id = 1L, nombre = "Pollo al curry"),
            ingredientes = listOf(
                linea(1L, 1L, 2L, "Pechuga de pollo", 400.0, "g"),
                linea(2L, 1L, 4L, "Cebolla", 80.0, "g"),
                linea(3L, 1L, 5L, "Ajo", 10.0, "g"),
                linea(4L, 1L, 6L, "Aceite de oliva", 40.0, "ml")
            )
        )

        val resultado = clasificarRecetas(listOf(receta), despensaDemo(), maximoFaltantes = 2)

        assertEquals(listOf(1L), resultado.completas.map { it.receta.receta.id })
    }
}
