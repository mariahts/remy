package com.remy.app.domain

import com.remy.app.data.model.Alimento
import com.remy.app.data.model.Ingrediente
import com.remy.app.data.model.IngredienteConAlimento
import com.remy.app.data.model.Receta
import com.remy.app.data.model.RecetaConIngredientes
import com.remy.app.domain.model.Faltante
import com.remy.app.domain.model.MotivoFaltante
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AlgoritmoSugerenciasTest {

    /** Despensa de prueba conocida: arroz (1) y huevo (2). */
    private val despensaIds = setOf(1L, 2L)

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

    private fun linea(
        id: Long,
        recetaId: Long,
        alimentoId: Long,
        nombre: String,
        cantidad: Double,
        unidad: String
    ) = IngredienteConAlimento(
        ingrediente = Ingrediente(id = id, recetaId = recetaId, alimentoId = alimentoId, cantidad = cantidad, unidad = unidad),
        alimento = Alimento(id = alimentoId, nombre = nombre, cantidad = 0.0, unidad = unidad, enDespensa = false)
    )

    private fun recetaConLineas(id: Long, nombre: String, ingredientes: List<IngredienteConAlimento>) =
        RecetaConIngredientes(receta = Receta(id = id, nombre = nombre), ingredientes = ingredientes)

    private fun despensa(vararg alimentos: Alimento): List<Alimento> = alimentos.toList()

    // ---------------------------------------------------------------------
    // ingredienteDisponible (badge «Lista» y checks del detalle de receta)
    // ---------------------------------------------------------------------

    private fun lineaConAlimento(
        nombre: String,
        cantidadNecesaria: Double,
        unidadNecesaria: String,
        enDespensa: Boolean,
        cantidadDespensa: Double,
        unidadDespensa: String
    ) = IngredienteConAlimento(
        ingrediente = Ingrediente(id = 1, recetaId = 1, alimentoId = 1L, cantidad = cantidadNecesaria, unidad = unidadNecesaria),
        alimento = Alimento(id = 1L, nombre = nombre, cantidad = cantidadDespensa, unidad = unidadDespensa, enDespensa = enDespensa)
    )

    @Test
    fun ingredienteDisponible_faltaSiNoEstaEnLaDespensa() {
        // La fila del catálogo existe (alimento != null) pero no está en la despensa
        val linea = lineaConAlimento("Tomate", 200.0, "g", enDespensa = false, cantidadDespensa = 0.0, unidadDespensa = "g")

        assertTrue(!ingredienteDisponible(linea))
    }

    @Test
    fun ingredienteDisponible_sinAlimentoEnElCatalogoNoCuenta() {
        val linea = IngredienteConAlimento(
            ingrediente = Ingrediente(id = 1, recetaId = 1, alimentoId = 99L, cantidad = 1.0, unidad = "ud"),
            alimento = null
        )

        assertTrue(!ingredienteDisponible(linea))
    }

    @Test
    fun ingredienteDisponible_losBasicosSiempreCuentan() {
        val sal = lineaConAlimento("Sal", 1.0, "cdta", enDespensa = false, cantidadDespensa = 0.0, unidadDespensa = "cdta")
        val agua = lineaConAlimento("Agua", 200.0, "ml", enDespensa = false, cantidadDespensa = 0.0, unidadDespensa = "ml")

        assertTrue(ingredienteDisponible(sal))
        assertTrue(ingredienteDisponible(agua))
    }

    @Test
    fun ingredienteDisponible_compruebaCantidadSuficiente() {
        val suficiente = lineaConAlimento("Arroz", 100.0, "g", enDespensa = true, cantidadDespensa = 250.0, unidadDespensa = "g")
        val insuficiente = lineaConAlimento("Arroz", 300.0, "g", enDespensa = true, cantidadDespensa = 250.0, unidadDespensa = "g")
        val otraUnidad = lineaConAlimento("Arroz", 1.0, "kg", enDespensa = true, cantidadDespensa = 250.0, unidadDespensa = "g")

        assertTrue(ingredienteDisponible(suficiente))
        assertTrue(!ingredienteDisponible(insuficiente))
        assertTrue(!ingredienteDisponible(otraUnidad))
    }

    @Test
    fun ingredienteDisponible_equiparaGramosYMililitros() {
        // La receta pide5 g de aceite y la despensa lo guarda en ml (como en la demo)
        val suficiente = lineaConAlimento("Aceite de oliva", 5.0, "g", enDespensa = true, cantidadDespensa = 100.0, unidadDespensa = "ml")
        val insuficiente = lineaConAlimento("Aceite de oliva", 5.0, "g", enDespensa = true, cantidadDespensa = 2.0, unidadDespensa = "ml")

        assertTrue(ingredienteDisponible(suficiente))
        assertTrue(!ingredienteDisponible(insuficiente))
    }

    @Test
    fun faltantesDeReceta_aceiteEnGConStockEnMlNoFalta() {
        // Caso del wrap de pollo:5 g pedidos y500 ml en la despensa → sin faltantes
        val recetaWrap = recetaConLineas(
            1, "Wrap de pollo",
            listOf(linea(1, 1, 6, "Aceite de oliva", 5.0, "g"))
        )
        val despensaAceite = despensa(
            Alimento(id = 6, nombre = "Aceite de oliva", cantidad = 500.0, unidad = "ml", enDespensa = true)
        )

        assertTrue(faltantesDeReceta(recetaWrap, despensaAceite).isEmpty())
    }

    @Test
    fun casoPrincipal_completaCasiCocinableYExcluida() {
        val completa = receta(10, "Arroz con huevo", 1L, 2L)      // todo en la despensa
        val casi = receta(11, "Tortilla", 1L, 3L)                  // falta el huevo (3)
        val excluida = receta(12, "Guiso", 4L, 5L, 6L)             // faltan tres, con máximo 1

        val resultado = clasificarRecetas(listOf(completa, casi, excluida), despensaIds, maximoFaltantes = 1)

        assertEquals(listOf(10L), resultado.completas.map { it.receta.receta.id })
        assertEquals(listOf(11L), resultado.casiCocinables.map { it.receta.receta.id })
        assertEquals(listOf(Faltante(3L)), resultado.casiCocinables.first().faltantes)
        assertTrue(resultado.completas.isNotEmpty())
    }

    @Test
    fun laComparacionEsPorIdentificadorNoPorNombre() {
        // El alimento 99 se llama igual que el 1 ("Arroz") pero no está en la despensa
        val conIdDesconocido = receta(20, "Receta rara", 99L)

        val resultado = clasificarRecetas(listOf(conIdDesconocido), despensaIds, maximoFaltantes = 1)

        assertTrue(resultado.completas.isEmpty())
        assertEquals(listOf(Faltante(99L)), resultado.casiCocinables.first().faltantes)
    }

    @Test
    fun elMaximoDeFaltantesEsConfigurable() {
        val receta = receta(30, "Con dos faltantes", 7L, 8L)

        val conMaximo1 = clasificarRecetas(listOf(receta), despensaIds, maximoFaltantes = 1)
        assertTrue(conMaximo1.casiCocinables.isEmpty())
        assertTrue(conMaximo1.completas.isEmpty())

        val conMaximo2 = clasificarRecetas(listOf(receta), despensaIds, maximoFaltantes = 2)
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

        val resultado = clasificarRecetas(listOf(conRepetido), despensaIds, maximoFaltantes = 0)
        assertEquals(1, resultado.completas.size)
        assertTrue(resultado.casiCocinables.isEmpty())
    }

    @Test
    fun cantidadInsuficiente_marcaFaltantePorCantidad() {
        val receta = recetaConLineas(
            50, "Poca harina",
            listOf(linea(1, 50, 1, "Harina", 500.0, "g"))
        )
        val resultado = clasificarRecetas(
            listOf(receta),
            despensa(Alimento(id = 1, nombre = "Harina", cantidad = 200.0, unidad = "g")),
            maximoFaltantes = 1
        )

        assertTrue(resultado.completas.isEmpty())
        assertEquals(1, resultado.casiCocinables.size)
        assertEquals(
            listOf(Faltante(1, MotivoFaltante.CANTIDAD_INSUFICIENTE)),
            resultado.casiCocinables.first().faltantes
        )
    }

    @Test
    fun conversionMasa_gYkg_seConsideraDisponible() {
        val receta = recetaConLineas(
            51, "Con arroz",
            listOf(linea(1, 51, 1, "Arroz", 500.0, "g"))
        )
        val resultado = clasificarRecetas(
            listOf(receta),
            despensa(Alimento(id = 1, nombre = "Arroz", cantidad = 1.0, unidad = "kg")),
            maximoFaltantes = 0
        )

        assertEquals(1, resultado.completas.size)
        assertTrue(resultado.casiCocinables.isEmpty())
    }

    @Test
    fun conversionVolumen_mlYl_seConsideraDisponible() {
        val receta = recetaConLineas(
            52, "Con leche",
            listOf(linea(1, 52, 2, "Leche", 500.0, "ml"))
        )
        val resultado = clasificarRecetas(
            listOf(receta),
            despensa(Alimento(id = 2, nombre = "Leche", cantidad = 1.0, unidad = "l")),
            maximoFaltantes = 0
        )

        assertEquals(1, resultado.completas.size)
        assertTrue(resultado.casiCocinables.isEmpty())
    }

    @Test
    fun basicosSalAguaPimienta_noCuentanComoFaltantes() {
        val receta = recetaConLineas(
            53, "Sencilla",
            listOf(
                linea(1, 53, 1, "Arroz", 100.0, "g"),
                linea(2, 53, 10, "Sal", 5.0, "g"),
                linea(3, 53, 11, "Agua", 200.0, "ml"),
                linea(4, 53, 12, "Pimienta", 2.0, "g")
            )
        )
        val resultado = clasificarRecetas(
            listOf(receta),
            despensa(Alimento(id = 1, nombre = "Arroz", cantidad = 200.0, unidad = "g")),
            maximoFaltantes = 0
        )

        assertEquals(1, resultado.completas.size)
        assertTrue(resultado.casiCocinables.isEmpty())
    }
}
