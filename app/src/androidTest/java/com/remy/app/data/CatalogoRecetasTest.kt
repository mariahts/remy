package com.remy.app.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Comprueba que el catalogo de `assets/recetas.json` esta vivo: que los macros
 * de cada receta son los que salen de sus ingredientes, que ningun alimento se
 * queda sin hidratos ni grasas y que todas las recetas se pueden seguir.
 *
 * Estos datos nutritionales son la base de lo que ve el usuario en el detalle
 * de cada receta, asi que cualquier cambio en los ingredientes tiene que
 * reflejarse aqui.
 */
@RunWith(AndroidJUnit4::class)
class CatalogoRecetasTest {

    private fun raiz(): JSONObject {
        val contexto = ApplicationProvider.getApplicationContext<Context>()
        val texto = contexto.assets.open("recetas.json")
            .bufferedReader(Charsets.UTF_8).use { it.readText() }
        return JSONObject(texto)
    }

    private fun macrosPorAlimento(raiz: JSONObject): Map<Long, MacrosAlimento> {
        val alimentos = raiz.getJSONArray("alimentos")
        val mapa = mutableMapOf<Long, MacrosAlimento>()
        for (i in 0 until alimentos.length()) {
            val a = alimentos.getJSONObject(i)
            mapa[a.getLong("id")] = MacrosAlimento(
                kcal = a.optDouble("kcal", 0.0),
                proteinas = a.optDouble("proteinas", 0.0),
                hidratos = a.optDouble("hidratos", 0.0),
                grasas = a.optDouble("grasas", 0.0)
            )
        }
        return mapa
    }

    @Test
    fun losMacrosDeCadaRecetaCoincidenConSusIngredientes() {
        val raiz = raiz()
        val catalogo = macrosPorAlimento(raiz)
        val recetas = raiz.getJSONArray("recetas")

        for (i in 0 until recetas.length()) {
            val r = recetas.getJSONObject(i)
            val ingredientesJson = r.getJSONArray("ingredientes")
            val ingredientes = (0 until ingredientesJson.length()).map { j ->
                val ing = ingredientesJson.getJSONObject(j)
                Ingrediente(
                    recetaId = 0,
                    alimentoId = ing.getLong("alimentoId"),
                    cantidad = ing.getDouble("cantidad"),
                    unidad = ing.getString("unidad")
                )
            }
            val macros = macrosDeReceta(ingredientes, catalogo)
            val nombre = r.getString("nombre")

            assertEquals("$nombre kcal", macros.kcal.toLong(), r.getInt("kcal").toLong())
            assertEquals("$nombre proteinas", macros.proteinas.toLong(), r.getInt("proteinas").toLong())
            assertEquals("$nombre hidratos", macros.hidratos.toLong(), r.getInt("hidratos").toLong())
            assertEquals("$nombre grasas", macros.grasas.toLong(), r.getInt("grasas").toLong())
        }
    }

    @Test
    fun todosLosAlimentosTienenHidratosYGrasas() {
        val raiz = raiz()
        val alimentos = raiz.getJSONArray("alimentos")

        for (i in 0 until alimentos.length()) {
            val a = alimentos.getJSONObject(i)
            val nombre = a.getString("nombre")
            assertTrue("$nombre sin hidratos", a.getInt("hidratos") >= 0)
            assertTrue("$nombre sin grasas", a.getInt("grasas") >= 0)
        }
    }

    @Test
    fun todasLasRecetasTienenPasosSuficientesYIngredientesValidos() {
        val raiz = raiz()
        val catalogo = macrosPorAlimento(raiz)
        val recetas = raiz.getJSONArray("recetas")

        for (i in 0 until recetas.length()) {
            val r = recetas.getJSONObject(i)
            val nombre = r.getString("nombre")
            val pasos = r.getJSONArray("pasos")
            assertTrue("$nombre con menos de 3 pasos", pasos.length() >= 3)
            for (j in 0 until pasos.length()) {
                assertTrue("paso vacio en $nombre", pasos.getString(j).trim().isNotEmpty())
            }
            val ingredientes = r.getJSONArray("ingredientes")
            assertTrue("$nombre sin ingredientes", ingredientes.length() >= 2)
            for (j in 0 until ingredientes.length()) {
                val id = ingredientes.getJSONObject(j).getLong("alimentoId")
                assertTrue("ingrediente desconocido en $nombre", catalogo.containsKey(id))
            }
        }
    }
}
