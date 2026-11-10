package com.remy.app.logica

import com.remy.app.data.Alimento
import com.remy.app.data.RecetaConIngredientes
import java.util.Locale
import kotlin.math.abs

/** Motivo por el que un ingrediente cuenta como faltante. */
enum class MotivoFaltante { AUSENTE, CANTIDAD_INSUFICIENTE }

/** Ingrediente que falta para poder cocinar una receta. */
data class Faltante(val alimentoId: Long, val motivo: MotivoFaltante = MotivoFaltante.AUSENTE)

/** Receta que ya se puede cocinar: tiene todos los ingredientes en la despensa. */
data class RecetaCompleta(val receta: RecetaConIngredientes)

/** Receta a la que le faltan ingredientes, pero dentro del máximo permitido. */
data class RecetaCasiCocinable(val receta: RecetaConIngredientes, val faltantes: List<Faltante>)

/** Los dos grupos que devuelve el algoritmo de sugerencias. */
data class ResultadoSugerencias(
    val completas: List<RecetaCompleta>,
    val casiCocinables: List<RecetaCasiCocinable>
)

/**
 * Clasifica las recetas según el contenido de la despensa.
 *
 * La comparación se hace por identificador de alimento. Un ingrediente cuenta como
 * faltante si no está en la despensa o si no hay cantidad suficiente. Una receta es
 * casi cocinable si todos sus faltantes están permitidos (cuando se indican) y no
 * superan [maximoFaltantes].
 */
fun clasificarRecetas(
    recetas: List<RecetaConIngredientes>,
    despensa: List<Alimento>,
    maximoFaltantes: Int,
    faltantesPermitidos: Set<Long> = emptySet()
): ResultadoSugerencias {
    val enDespensa = despensa.associateBy { it.id }
    val completas = mutableListOf<RecetaCompleta>()
    val casiCocinables = mutableListOf<RecetaCasiCocinable>()

    for (item in recetas) {
        val faltantes = linkedMapOf<Long, Faltante>()
        for (ing in item.ingredientes) {
            val id = ing.ingrediente.alimentoId
            if (id in faltantes) continue
            val disponible = enDespensa[id]
            val motivo = when {
                disponible == null -> MotivoFaltante.AUSENTE
                !cantidadSuficiente(ing.ingrediente.cantidad, ing.ingrediente.unidad, disponible.cantidad, disponible.unidad) ->
                    MotivoFaltante.CANTIDAD_INSUFICIENTE
                else -> null
            }
            if (motivo != null) faltantes[id] = Faltante(id, motivo)
        }
        val lista = faltantes.values.toList()
        val permitidosOk = faltantesPermitidos.isEmpty() || lista.all { it.alimentoId in faltantesPermitidos }
        when {
            lista.isEmpty() -> completas += RecetaCompleta(item)
            lista.size <= maximoFaltantes && permitidosOk -> casiCocinables += RecetaCasiCocinable(item, lista)
        }
    }

    return ResultadoSugerencias(completas, casiCocinables)
}

/** Sobrecarga compatible: solo presencia por identificador, sin cantidad. */
fun clasificarRecetas(
    recetas: List<RecetaConIngredientes>,
    alimentoIdsEnDespensa: Set<Long>,
    maximoFaltantes: Int
): ResultadoSugerencias {
    val completas = mutableListOf<RecetaCompleta>()
    val casiCocinables = mutableListOf<RecetaCasiCocinable>()

    for (item in recetas) {
        val faltantes = item.ingredientes
            .filter { it.ingrediente.alimentoId !in alimentoIdsEnDespensa }
            .distinctBy { it.ingrediente.alimentoId }
            .map { Faltante(it.ingrediente.alimentoId) }

        when {
            faltantes.isEmpty() -> completas += RecetaCompleta(item)
            faltantes.size <= maximoFaltantes -> casiCocinables += RecetaCasiCocinable(item, faltantes)
        }
    }

    return ResultadoSugerencias(completas, casiCocinables)
}

private fun cantidadSuficiente(necesaria: Double, unidadNecesaria: String, disponible: Double, unidadDisponible: String): Boolean {
    val n = normalizarCantidad(necesaria, unidadNecesaria)
    val d = normalizarCantidad(disponible, unidadDisponible)
    return when {
        n == null || d == null -> unidadNecesaria.trim().equals(unidadDisponible.trim(), ignoreCase = true) && disponible + 1e-9 >= necesaria
        n.second != d.second -> false
        else -> d.first + 1e-9 >= n.first
    }
}

private fun normalizarCantidad(cantidad: Double, unidad: String): Pair<Double, String>? {
    val u = unidad.trim().lowercase(Locale.getDefault())
    return when (u) {
        "kg", "kilo", "kilos" -> cantidad * 1000.0 to "masa"
        "g", "gr", "gramo", "gramos" -> cantidad to "masa"
        "l", "litro", "litros" -> cantidad * 1000.0 to "volumen"
        "ml", "mililitro", "mililitros" -> cantidad to "volumen"
        "ud", "uds", "unidad", "unidades", "u" -> cantidad to "unidad"
        else -> null
    }
}
