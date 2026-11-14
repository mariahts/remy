package com.remy.app.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.remy.app.data.database.RemyDatabase
import com.remy.app.data.local.PrecargaRecetas
import com.remy.app.data.model.Ingrediente
import com.remy.app.data.model.Receta
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PrecargaRecetasTest {

    private lateinit var db: RemyDatabase

    @Before
    fun crearBase() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemyDatabase::class.java
        ).build()
    }

    @After
    fun cerrarBase() = db.close()

    @Test
    fun precargaDejaLasVeinteRecetas() = runBlocking {
        val contexto = ApplicationProvider.getApplicationContext<Context>()

        PrecargaRecetas.siHaceFalta(db, contexto)
        assertEquals(20, db.recetaDao().contar())

        // Cada ingrediente referencia a un alimento existente (lo garantiza la clave externa)
        val recetas = db.recetaDao().observarTodas().first()
        assertTrue(recetas.all { it.ingredientes.isNotEmpty() })
        // El catálogo completo: los alimentos precargados se insertan fuera de la despensa,
        // así que hay que mirar observarCatalogo() y no observarTodos() (solo despensa).
        val alimentos = db.alimentoDao().observarCatalogo().first().map { it.id }.toSet()
        assertTrue(alimentos.isNotEmpty())
        assertTrue(recetas.all { r -> r.ingredientes.all { it.ingrediente.alimentoId in alimentos } })
    }

    @Test
    fun alReabrir_noDuplicaLasRecetas() = runBlocking {
        val contexto = ApplicationProvider.getApplicationContext<Context>()

        PrecargaRecetas.siHaceFalta(db, contexto)
        PrecargaRecetas.siHaceFalta(db, contexto)

        assertEquals(20, db.recetaDao().contar())
    }

    @Test
    fun retiraLasRecetasQueYaNoEstanEnElCatalogo() = runBlocking {
        val contexto = ApplicationProvider.getApplicationContext<Context>()
        PrecargaRecetas.siHaceFalta(db, contexto)

        // Una receta que no está en assets/recetas.json, como las de una
        // versión anterior del catálogo.
        db.recetaDao().insertarCompleta(
            Receta(nombre = "Receta antigua de prueba", porciones = 2),
            listOf(Ingrediente(recetaId = 0, alimentoId = 1, cantidad = 100.0, unidad = "g"))
        )
        assertEquals(21, db.recetaDao().contar())

        PrecargaRecetas.siHaceFalta(db, contexto)

        assertEquals(20, db.recetaDao().contar())
        assertEquals(null, db.recetaDao().porNombre("Receta antigua de prueba"))
    }

    @Test
    fun lasRecetasTraenTiempoYPasos() = runBlocking {        val contexto = ApplicationProvider.getApplicationContext<Context>()
        PrecargaRecetas.siHaceFalta(db, contexto)

        val recetas = db.recetaDao().observarTodas().first()
        assertTrue(recetas.all { it.receta.tiempoMinutos > 0 })
        assertTrue(recetas.all { it.receta.pasos.isNotEmpty() })
        // Hay recetas de los tres tipos de plato y de varias regiones
        val tipos = recetas.map { it.receta.tipoPlato }.toSet()
        assertEquals(setOf("Entrante", "Principal", "Postre"), tipos)
        assertTrue(recetas.map { it.receta.regionOrigen }.toSet().size >= 3)
    }
}
