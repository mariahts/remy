package com.remy.app.data

import android.content.Context
import androidx.room.withTransaction
import org.json.JSONObject

/**
 * Precarga el catálogo propio de recetas (#30) desde el JSON de assets.
 *
 * Solo se ejecuta si la tabla de recetas está vacía (primer arranque o datos
 * borrados), así que nunca duplica recetas al reiniciar la app. Los alimentos
 * del catálogo se insertan primero para que cada ingrediente pueda referenciarlos
 * por identificador.
 */
object PrecargaRecetas {

    suspend fun siHaceFalta(db: RemyDatabase, contexto: Context) {
        if (db.recetaDao().contar() > 0) return

        val json = contexto.assets.open("recetas.json")
            .bufferedReader(Charsets.UTF_8).use { it.readText() }
        val raiz = JSONObject(json)

        db.withTransaction {
            val alimentoDao = db.alimentoDao()
            val recetaDao = db.recetaDao()

            val alimentos = raiz.getJSONArray("alimentos")
            for (i in 0 until alimentos.length()) {
                val a = alimentos.getJSONObject(i)
                alimentoDao.insertarSiFalta(
                    Alimento(
                        id = a.getLong("id"),
                        nombre = a.getString("nombre"),
                        kcal = a.getInt("kcal"),
                        proteinas = a.getInt("proteinas"),
                        regionOrigen = a.getString("regionOrigen"),
                        tipoPlato = a.getString("tipoPlato"),
                        cantidad = a.getDouble("cantidad"),
                        unidad = a.getString("unidad"),
                        enDespensa = false
                    )
                )
            }

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
                val pasosJson = r.getJSONArray("pasos")
                recetaDao.insertarCompleta(
                    Receta(
                        nombre = r.getString("nombre"),
                        kcal = r.getInt("kcal"),
                        proteinas = r.getInt("proteinas"),
                        hidratos = r.optInt("hidratos", 0),
                        grasas = r.optInt("grasas", 0),
                        regionOrigen = r.getString("regionOrigen"),
                        tipoPlato = r.getString("tipoPlato"),
                        tiempoMinutos = r.getInt("tiempoMinutos"),
                        pasos = (0 until pasosJson.length()).map { pasosJson.getString(it) },
                        porciones = r.optInt("porciones", 1).coerceAtLeast(1)
                    ),
                    ingredientes
                )
            }
        }
    }
}
