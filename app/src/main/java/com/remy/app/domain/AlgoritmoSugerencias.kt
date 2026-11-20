package com.remy.app.domain

import com.remy.app.data.model.Alimento
import com.remy.app.data.model.IngredienteConAlimento
import com.remy.app.data.model.RecetaConIngredientes
import com.remy.app.domain.model.Faltante
import com.remy.app.domain.model.IngredienteFaltante
import com.remy.app.domain.model.MotivoFaltante
import com.remy.app.domain.model.RecetaCasiCocinable
import com.remy.app.domain.model.RecetaCompleta
import com.remy.app.domain.model.ResultadoSugerencias
import java.text.Normalizer
import java.util.Locale

private val BASICOS = setOf("sal", "agua", "pimienta")

private fun esBasico(nombre: String?): Boolean {
    if (nombre == null) return false
    val normalizado = Normalizer.normalize(nombre.trim().lowercase(Locale.getDefault()), Normalizer.Form.NFD)
        .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
    return normalizado in BASICOS
}

/**
 * Ingredientes que le faltan a [receta] con la [despensa] actual, ya resueltos
 * con su nombre y sus cantidades.
 *
 * Un ingrediente falta si no está en la despensa o si no hay cantidad
 * suficiente. Sal, agua y pimienta no cuentan. Los ingredientes repetidos se
 * devuelven una sola vez.
 */
fun faltantesDeReceta(
    receta: RecetaConIngredientes,
    despensa: List<Alimento>
): List<IngredienteFaltante> {
    val enDespensa = despensa.associateBy { it.id }
    val vistos = mutableSetOf<Long>()
    val faltantes = mutableListOf<IngredienteFaltante>()

    for (ing in receta.ingredientes) {
        val id = ing.ingrediente.alimentoId
        if (!vistos.add(id)) continue
        if (esBasico(ing.alimento?.nombre)) continue

        val disponible = enDespensa[id]
        val motivo = when {
            disponible == null -> MotivoFaltante.AUSENTE
            !cantidadSuficiente(
                ing.ingrediente.cantidad,
                ing.ingrediente.unidad,
                disponible.cantidad,
                disponible.unidad
            ) -> MotivoFaltante.CANTIDAD_INSUFICIENTE
            else -> null
        } ?: continue

        faltantes += IngredienteFaltante(
            alimentoId = id,
            nombre = ing.alimento?.nombre ?: "Alimento $id",
            necesario = ing.ingrediente.cantidad,
            unidad = ing.ingrediente.unidad,
            disponible = disponible?.let {
                convertirAUnidad(it.cantidad, it.unidad, ing.ingrediente.unidad)
            },
            motivo = motivo
        )
    }

    return faltantes
}

/**
 * ¿Está disponible el [ingrediente] en la despensa actual?
 *
 * Cuenta lo mismo que [faltantesDeReceta]: los básicos (sal, agua y
 * pimienta) siempre, y el resto si la fila del alimento está en la
 * despensa ([Alimento.enDespensa]) con la cantidad suficiente. La fila
 * del catálogo existe aunque el alimento no esté en la despensa, así que
 * comprobar solo `alimento != null` no sirve para saber qué falta.
 */
fun ingredienteDisponible(ingrediente: IngredienteConAlimento): Boolean {
    val alimento = ingrediente.alimento ?: return false
    if (esBasico(alimento.nombre)) return true
    if (!alimento.enDespensa) return false
    return cantidadSuficiente(
        ingrediente.ingrediente.cantidad,
        ingrediente.ingrediente.unidad,
        alimento.cantidad,
        alimento.unidad
    )
}

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
    val completas = mutableListOf<RecetaCompleta>()
    val casiCocinables = mutableListOf<RecetaCasiCocinable>()

    for (item in recetas) {
        val faltantes = faltantesDeReceta(item, despensa)
            .map { Faltante(it.alimentoId, it.motivo) }
        val permitidosOk = faltantesPermitidos.isEmpty() || faltantes.all { it.alimentoId in faltantesPermitidos }
        when {
            faltantes.isEmpty() -> completas += RecetaCompleta(item)
            faltantes.size <= maximoFaltantes && permitidosOk -> casiCocinables += RecetaCasiCocinable(item, faltantes)
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
        !familiasComparables(n.second, d.second) -> false
        else -> d.first + 1e-9 >= n.first
    }
}

private val FAMILIAS_CANTIDAD = setOf("masa", "volumen")

/**
 * Las familias «masa» y «volumen» se equiparan (1 ml ≈ 1 g, aproximación de
 * cocina): una receta que pide aceite en gramos se considera cubierta si la
 * despensa lo guarda en mililitros, y al revés. El resto de familias solo se
 * comparan consigo mismas.
 */
internal fun familiasComparables(a: String, b: String): Boolean =
    a == b || (a in FAMILIAS_CANTIDAD && b in FAMILIAS_CANTIDAD)

/**
 * Expresa [cantidad] (guardada en [unidad]) en [unidadObjetivo] cuando las
 * familias son comparables. Si no hay conversión posible, la devuelve tal cual.
 */
private fun convertirAUnidad(cantidad: Double, unidad: String, unidadObjetivo: String): Double {
    val origen = normalizarCantidad(cantidad, unidad) ?: return cantidad
    val destino = normalizarCantidad(cantidad, unidadObjetivo) ?: return cantidad
    if (!familiasComparables(origen.second, destino.second)) return cantidad
    return when (unidadObjetivo.trim().lowercase(Locale.getDefault())) {
        "kg", "kilo", "kilos", "l", "litro", "litros" -> origen.first / 1000.0
        else -> origen.first
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
