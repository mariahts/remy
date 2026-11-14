package com.remy.app.data.local

import com.remy.app.data.database.RemyDatabase
import com.remy.app.data.repository.PreferenciasRepository
import kotlinx.coroutines.flow.first

object DatosDemo {
    const val CORREO_DEMO = "demo@remy.app"

    /**
     * Versión del conjunto de datos de la despensa de demostración.
     *
     * Se incrementa cuando cambian las cantidades o las unidades para que las
     * instalaciones antiguas vuelvan a sembrar la despensa con los datos nuevos.
     */
    const val VERSION_DESPENSA_DEMO = 4

    /**
     * Alimentos del catálogo que se precargan en la despensa de la cuenta demo.
     *
     * Las cantidades y unidades son las mismas que usan las recetas (gramos y
     * mililitros), porque el algoritmo de sugerencias compara ambos. Con
     * unidades como "ud" ninguna receta llegaba a considerarse completa.
     *
     * Se dejan fuera el bacalao, el camarón y el queso de sobra para que la
     * pantalla de sugerencias muestre también recetas a las que les falta algo
     * y se pueda ver el aviso al añadirlas al plan.
     */
    internal val ALIMENTOS_DEMO = listOf(
        "Arroz" to (1000.0 to "g"),
        "Pechuga de pollo" to (500.0 to "g"),
        "Tomate" to (1000.0 to "g"),
        "Cebolla" to (1000.0 to "g"),
        "Ajo" to (100.0 to "g"),
        "Aceite de oliva" to (500.0 to "ml"),
        "Huevo" to (300.0 to "g"),
        "Leche" to (1000.0 to "ml"),
        "Queso" to (300.0 to "g"),
        "Pan" to (400.0 to "g"),
        "Atún" to (400.0 to "g"),
        "Garbanzos cocidos" to (500.0 to "g"),
        "Lentejas cocidas" to (500.0 to "g"),
        "Zanahoria" to (500.0 to "g"),
        "Pimiento" to (500.0 to "g"),
        "Patata" to (1000.0 to "g"),
        "Nata" to (500.0 to "ml"),
        "Azúcar" to (500.0 to "g"),
        "Harina" to (1000.0 to "g"),
        "Mantequilla" to (250.0 to "g"),
        "Plátano" to (500.0 to "g"),
        "Fresa" to (500.0 to "g"),
        "Chocolate negro" to (200.0 to "g"),
        "Champiñón" to (500.0 to "g"),
        "Espinacas" to (300.0 to "g"),
        "Lechuga" to (300.0 to "g"),
        "Sal" to (500.0 to "g"),
        "Pasta" to (500.0 to "g")
    )

    fun esCuentaDemo(correo: String?): Boolean =
        correo?.trim()?.equals(CORREO_DEMO, ignoreCase = true) == true

    /**
     * Precarga la despensa de la cuenta demo si todavía no se ha sembrado con
     * los datos actuales.
     */
    suspend fun sembrarDespensaSiHaceFalta(
        db: RemyDatabase,
        preferencias: PreferenciasRepository
    ) {
        if (preferencias.versionDespensaDemo.first() >= VERSION_DESPENSA_DEMO) return
        val dao = db.alimentoDao()
        val catalogo = dao.observarCatalogo().first()
        ALIMENTOS_DEMO.forEach { (nombre, cantidadUnidad) ->
            val (cantidad, unidad) = cantidadUnidad
            val alimento = catalogo.firstOrNull { it.nombre.trim().equals(nombre, ignoreCase = true) } ?: return@forEach
            dao.actualizar(alimento.copy(cantidad = cantidad, unidad = unidad, enDespensa = true))
        }
        preferencias.guardarVersionDespensaDemo(VERSION_DESPENSA_DEMO)
    }

    /** Restablece los datos locales del usuario para empezar desde cero. */
    suspend fun resetearDatosUsuario(db: RemyDatabase, preferencias: PreferenciasRepository) {
        db.alimentoDao().vaciarDespensa()
        db.planDao().borrarTodo()
        db.recetaDao().limpiarCocinadas()
        preferencias.limpiarPreferenciasUsuario()
    }
}
