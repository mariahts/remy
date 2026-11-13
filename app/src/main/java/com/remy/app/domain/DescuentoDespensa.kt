package com.remy.app.domain

import com.remy.app.data.Alimento
import com.remy.app.data.IngredienteConAlimento
import com.remy.app.domain.model.ResultadoDescuento
import java.util.Locale
import kotlin.math.max

/**
 * Calcula el descuento de los [ingredientes] de una receta sobre la [despensa].
 *
 * Escala las cantidades según [racionesDeseadas] respecto a [porcionesReceta].
 * No modifica la despensa original: devuelve copias actualizadas y la lista de
 * ingredientes que no se pudieron descontar (ausentes, sin cantidad suficiente en
 * términos de disponibilidad, unidades incompatibles o fuera de la despensa).
 */
fun calcularDescuentoDespensa(
    ingredientes: List<IngredienteConAlimento>,
    porcionesReceta: Int,
    racionesDeseadas: Int,
    despensa: Map<Long, Alimento>
): ResultadoDescuento {
    val porciones = porcionesReceta.coerceAtLeast(1)
    val raciones = racionesDeseadas.coerceAtLeast(1)
    val cantidades = despensa.mapValues { it.value.cantidad }.toMutableMap()
    val actualizados = linkedSetOf<Long>()
    val noDescontados = mutableListOf<String>()

    for (ing in ingredientes) {
        val necesario = ing.ingrediente.cantidad * raciones / porciones.toDouble()
        if (necesario <= 0.0) continue
        val nombre = ing.alimento?.nombre ?: "Ingrediente"
        val alimento = despensa[ing.ingrediente.alimentoId]
        if (alimento == null || !alimento.enDespensa) {
            noDescontados += nombre
            continue
        }
        val disponibleActual = cantidades[alimento.id] ?: alimento.cantidad
        val restante = restarCantidad(
            disponible = disponibleActual,
            unidadDisponible = alimento.unidad,
            necesario = necesario,
            unidadNecesaria = ing.ingrediente.unidad
        )
        if (restante == null) {
            noDescontados += nombre
            continue
        }
        cantidades[alimento.id] = max(0.0, restante)
        actualizados += alimento.id
    }

    val actualizaciones = actualizados.mapNotNull { id ->
        val original = despensa[id] ?: return@mapNotNull null
        val nueva = cantidades[id] ?: original.cantidad
        if (nueva == original.cantidad) null else original.copy(cantidad = nueva)
    }

    return ResultadoDescuento(actualizaciones = actualizaciones, noDescontados = noDescontados)
}

/**
 * Resta [necesario] de [disponible] si las unidades son compatibles.
 * Devuelve la cantidad restante en la unidad de la despensa, o null si no se puede convertir.
 */
internal fun restarCantidad(
    disponible: Double,
    unidadDisponible: String,
    necesario: Double,
    unidadNecesaria: String
): Double? {
    val disp = normalizarCantidadDescuento(disponible, unidadDisponible)
    val nec = normalizarCantidadDescuento(necesario, unidadNecesaria)
    return if (disp != null && nec != null && disp.second == nec.second) {
        val restanteBase = disp.first - nec.first
        convertirDesdeBase(restanteBase, disp.second, unidadDisponible)
    } else if (unidadDisponible.trim().equals(unidadNecesaria.trim(), ignoreCase = true)) {
        disponible - necesario
    } else {
        null
    }
}

private fun normalizarCantidadDescuento(cantidad: Double, unidad: String): Pair<Double, String>? {
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

private fun convertirDesdeBase(cantidadBase: Double, familia: String, unidadDestino: String): Double {
    val u = unidadDestino.trim().lowercase(Locale.getDefault())
    return when (familia) {
        "masa" -> if (u == "kg" || u == "kilo" || u == "kilos") cantidadBase / 1000.0 else cantidadBase
        "volumen" -> if (u == "l" || u == "litro" || u == "litros") cantidadBase / 1000.0 else cantidadBase
        else -> cantidadBase
    }
}
