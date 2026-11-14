package com.remy.app.data.local

import android.content.Context
import androidx.room.withTransaction
import com.remy.app.data.database.RemyDatabase
import com.remy.app.data.model.Alimento
import com.remy.app.data.model.Ingrediente
import com.remy.app.data.model.MacrosAlimento
import com.remy.app.data.model.Receta
import com.remy.app.data.model.macrosDeReceta
import org.json.JSONObject

/**
 * Precarga y sincroniza el catálogo propio de recetas desde el JSON de assets.
 *
 * En instalaciones nuevas inserta el catálogo completo. Si ya existen recetas,
 * actualiza las del catálogo por nombre (macros, datos y pasos), añade las que
 * sean nuevas y borra las que hayan salido del catálogo, sin tocar la despensa.
 * La fecha y la foto de cocción de las recetas que se conservan no se pierden.
 */
object PrecargaRecetas {

    suspend fun siHaceFalta(db: RemyDatabase, contexto: Context) {
        val json = contexto.assets.open("recetas.json")
            .bufferedReader(Charsets.UTF_8).use { it.readText() }
        val raiz = JSONObject(json)

        db.withTransaction {
            val alimentoDao = db.alimentoDao()
            val recetaDao = db.recetaDao()

            val alimentos = raiz.getJSONArray("alimentos")
            val macrosPorAlimento = mutableMapOf<Long, MacrosAlimento>()
            for (i in 0 until alimentos.length()) {
                val a = alimentos.getJSONObject(i)
                val id = a.getLong("id")
                macrosPorAlimento[id] = MacrosAlimento(
                    kcal = a.optDouble("kcal", 0.0),
                    proteinas = a.optDouble("proteinas", 0.0),
                    hidratos = a.optDouble("hidratos", 0.0),
                    grasas = a.optDouble("grasas", 0.0)
                )
                alimentoDao.insertarSiFalta(
                    Alimento(
                        id = id,
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
            val nombresDelCatalogo = mutableListOf<String>()
            for (i in 0 until recetas.length()) {
                val r = recetas.getJSONObject(i)
                val pasosJson = r.getJSONArray("pasos")
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
                // Si el JSON trae macros definidos se usan tal cual (valores
                // revisados). Si no, se calculan a partir de los ingredientes
                // del catálogo para no desincronizarlos.
                val kcalJson = r.optInt("kcal", 0)
                val macrosCalculados = macrosDeReceta(ingredientes, macrosPorAlimento)
                val kcalReceta = if (kcalJson > 0) r.optInt("kcal", 0) else macrosCalculados.kcal
                val protReceta = if (kcalJson > 0) r.optInt("proteinas", 0) else macrosCalculados.proteinas
                val hidrReceta = if (kcalJson > 0) r.optInt("hidratos", 0) else macrosCalculados.hidratos
                val grasReceta = if (kcalJson > 0) r.optInt("grasas", 0) else macrosCalculados.grasas
                val recetaCatalogo = Receta(
                    nombre = r.getString("nombre"),
                    kcal = kcalReceta,
                    proteinas = protReceta,
                    hidratos = hidrReceta,
                    grasas = grasReceta,
                    regionOrigen = r.getString("regionOrigen"),
                    tipoPlato = r.getString("tipoPlato"),
                    tiempoMinutos = r.getInt("tiempoMinutos"),
                    pasos = (0 until pasosJson.length()).map { pasosJson.getString(it) },
                    porciones = r.optInt("porciones", 1).coerceAtLeast(1)
                )

                nombresDelCatalogo += recetaCatalogo.nombre
                val existente = recetaDao.porNombre(recetaCatalogo.nombre)
                if (existente == null) {
                    recetaDao.insertarCompleta(recetaCatalogo, ingredientes)
                } else {
                    recetaDao.actualizar(
                        existente.copy(
                            kcal = recetaCatalogo.kcal,
                            proteinas = recetaCatalogo.proteinas,
                            hidratos = recetaCatalogo.hidratos,
                            grasas = recetaCatalogo.grasas,
                            regionOrigen = recetaCatalogo.regionOrigen,
                            tipoPlato = recetaCatalogo.tipoPlato,
                            tiempoMinutos = recetaCatalogo.tiempoMinutos,
                            pasos = recetaCatalogo.pasos,
                            porciones = recetaCatalogo.porciones
                        )
                    )
                    recetaDao.reemplazarIngredientes(existente.id, ingredientes)
                }
            }

            // Las recetas que ya no están en el catálogo se retiran, con sus
            // ingredientes y sus comidas planificadas (clave externa CASCADE).
            recetaDao.borrarFueraDelCatalogo(nombresDelCatalogo)
        }
    }
}
