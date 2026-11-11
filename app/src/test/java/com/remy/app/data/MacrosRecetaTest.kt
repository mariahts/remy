package com.remy.app.data

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Los macros de una receta no se escriben a mano: se calculan sumando los
 * macros de cada ingrediente segun la cantidad que lleva. Estos tests fijan
 * ese comportamiento, que es el que garantiza que los valores nutritionales
 * que ve el usuario encajen con la receta.
 */
class MacrosRecetaTest {

    private val catalogo = mapOf(
        1L to MacrosAlimento(kcal = 350.0, proteinas = 7.0, hidratos = 78.0, grasas = 1.0),
        2L to MacrosAlimento(kcal = 165.0, proteinas = 31.0, hidratos = 0.0, grasas = 4.0),
        6L to MacrosAlimento(kcal = 884.0, proteinas = 0.0, hidratos = 0.0, grasas = 100.0)
    )

    private fun ingrediente(id: Long, cantidad: Double, unidad: String = "g") =
        Ingrediente(recetaId = 0, alimentoId = id, cantidad = cantidad, unidad = unidad)

    @Test
    fun sumaLosMacrosDeCadaIngredienteProporcionalALaCantidad() {
        val macros = macrosDeReceta(
            listOf(ingrediente(1, 200.0), ingrediente(2, 300.0)),
            catalogo
        )

        assertEquals(1195, macros.kcal)      // 350 x 2 + 165 x 3
        assertEquals(107, macros.proteinas)  // 7 x 2 + 31 x 3
        assertEquals(156, macros.hidratos)   // 78 x 2
        assertEquals(14, macros.grasas)      // 1 x 2 + 4 x 3
    }

    @Test
    fun elAceiteAportaSoloGrasas() {
        val macros = macrosDeReceta(listOf(ingrediente(6, 15.0, "ml")), catalogo)

        assertEquals(133, macros.kcal)       // 884 x 0,15
        assertEquals(0, macros.proteinas)
        assertEquals(0, macros.hidratos)
        assertEquals(15, macros.grasas)      // 100 x 0,15
    }

    @Test
    fun unaRecetaSinIngredientesNoTieneMacros() {
        val macros = macrosDeReceta(emptyList(), catalogo)

        assertEquals(0, macros.kcal)
        assertEquals(0, macros.proteinas)
        assertEquals(0, macros.hidratos)
        assertEquals(0, macros.grasas)
    }

    @Test
    fun ignoraLosIngredientesQueNoEstanEnElCatalogo() {
        val macros = macrosDeReceta(
            listOf(ingrediente(999, 500.0), ingrediente(1, 100.0)),
            catalogo
        )

        assertEquals(350, macros.kcal)
        assertEquals(7, macros.proteinas)
        assertEquals(78, macros.hidratos)
        assertEquals(1, macros.grasas)
    }

    @Test
    fun losMacrosRedondeadosSiguenCuadrandoConElCalculo() {
        // 165 x 0,5 = 82,5 -> 83
        val macros = macrosDeReceta(listOf(ingrediente(2, 50.0)), catalogo)

        assertEquals(83, macros.kcal)
        assertEquals(16, macros.proteinas)   // 31 x 0,5 = 15,5 -> 16
        assertEquals(0, macros.hidratos)
        assertEquals(2, macros.grasas)       // 4 x 0,5 = 2
    }
}
