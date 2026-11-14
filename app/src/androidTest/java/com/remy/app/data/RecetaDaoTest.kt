package com.remy.app.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.remy.app.data.dao.AlimentoDao
import com.remy.app.data.dao.RecetaDao
import com.remy.app.data.database.RemyDatabase
import com.remy.app.data.model.Alimento
import com.remy.app.data.model.Ingrediente
import com.remy.app.data.model.Receta
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecetaDaoTest {

    private lateinit var db: RemyDatabase
    private lateinit var dao: RecetaDao
    private lateinit var alimentoDao: AlimentoDao

    @Before
    fun crearBase() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemyDatabase::class.java
        ).build()
        dao = db.recetaDao()
        alimentoDao = db.alimentoDao()
    }

    @After
    fun cerrarBase() = db.close()

    @Test
    fun insertarRecetaConIngredientesYLecturaCompleta() = runBlocking {
        val idTomate = alimentoDao.insertar(
            Alimento(
                nombre = "Tomate", kcal = 18, proteinas = 1,
                regionOrigen = "Mediterráneo", tipoPlato = "Verdura",
                cantidad = 500.0, unidad = "g"
            )
        )

        val idReceta = dao.insertarCompleta(
            Receta(
                nombre = "Salsa de tomate",
                kcal = 120,
                proteinas = 4,
                regionOrigen = "Mediterráneo",
                tipoPlato = "Entrante"
            ),
            listOf(
                Ingrediente(recetaId = 0, alimentoId = idTomate, cantidad = 500.0, unidad = "g")
            )
        )

        val leida = dao.porId(idReceta)
        assertNotNull(leida)
        assertEquals("Salsa de tomate", leida!!.receta.nombre)
        assertEquals(1, leida.ingredientes.size)
        // El ingrediente referencia al alimento por identificador, no por nombre
        assertEquals(idTomate, leida.ingredientes.first().ingrediente.alimentoId)
        assertEquals(500.0, leida.ingredientes.first().ingrediente.cantidad, 0.0)
    }

    @Test
    fun editarYBorrarReceta() = runBlocking {
        val id = dao.insertarCompleta(
            Receta(nombre = "Ensalada", kcal = 80, proteinas = 2, tipoPlato = "Entrante"),
            emptyList()
        )

        val receta = dao.porId(id)!!.receta
        dao.actualizar(receta.copy(nombre = "Ensalada mixta"))
        assertEquals("Ensalada mixta", dao.porId(id)!!.receta.nombre)

        dao.borrar(dao.porId(id)!!.receta)
        assertNull(dao.porId(id))
    }

    @Test
    fun observarTodasDevuelveLasInsertadas() = runBlocking {
        dao.insertarCompleta(Receta(nombre = "Lentejas", tipoPlato = "Principal"), emptyList())

        val lista = dao.observarTodas().first()
        assertEquals(1, lista.size)
        assertEquals("Lentejas", lista.first().receta.nombre)
    }
}
