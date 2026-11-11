package com.remy.app.data

import kotlinx.coroutines.flow.first

object DatosDemo {
    const val CORREO_DEMO = "demo@remy.app"

    /** Nombres de alimentos del catálogo que se precargan en la despensa de la cuenta demo. */
    private val ALIMENTOS_DEMO = listOf(
        "Arroz" to (1000.0 to "g"),
        "Pechuga de pollo" to (500.0 to "g"),
        "Tomate" to (500.0 to "g"),
        "Cebolla" to (3.0 to "ud"),
        "Ajo" to (2.0 to "ud"),
        "Aceite de oliva" to (500.0 to "ml"),
        "Huevo" to (6.0 to "ud"),
        "Leche" to (1.0 to "l"),
        "Patata" to (4.0 to "ud"),
        "Pasta" to (500.0 to "g"),
        "Queso" to (200.0 to "g"),
        "Sal" to (500.0 to "g")
    )

    fun esCuentaDemo(correo: String?): Boolean =
        correo?.trim()?.equals(CORREO_DEMO, ignoreCase = true) == true

    /** Precarga la despensa de la cuenta demo si está vacía. */
    suspend fun sembrarDespensaSiHaceFalta(db: RemyDatabase) {
        val dao = db.alimentoDao()
        if (dao.observarTodos().first().isNotEmpty()) return
        val catalogo = dao.observarCatalogo().first()
        ALIMENTOS_DEMO.forEach { (nombre, cantidadUnidad) ->
            val (cantidad, unidad) = cantidadUnidad
            val alimento = catalogo.firstOrNull { it.nombre.trim().equals(nombre, ignoreCase = true) } ?: return@forEach
            dao.actualizar(alimento.copy(cantidad = cantidad, unidad = unidad, enDespensa = true))
        }
    }

    /** Restablece los datos locales del usuario para empezar desde cero. */
    suspend fun resetearDatosUsuario(db: RemyDatabase, preferencias: PreferenciasRepository) {
        db.alimentoDao().vaciarDespensa()
        db.planDao().borrarTodo()
        db.recetaDao().limpiarCocinadas()
        preferencias.limpiarPreferenciasUsuario()
    }
}
