package com.remy.app.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.remy.app.data.dao.AlimentoDao
import com.remy.app.data.database.RemyDatabase
import com.remy.app.data.model.Alimento
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AlimentoDaoTest {

    private lateinit var db: RemyDatabase
    private lateinit var dao: AlimentoDao

    @Before
    fun crearBase() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemyDatabase::class.java
        ).build()
        dao = db.alimentoDao()
    }

    @After
    fun cerrarBase() = db.close()

    @Test
    fun insertarLeerYOCultar() = runBlocking {
        val id = dao.insertar(
            Alimento(
                nombre = "Tomate",
                kcal = 18,
                proteinas = 1,
                regionOrigen = "Mediterráneo",
                tipoPlato = "Verdura",
                cantidad = 500.0,
                unidad = "g"
            )
        )

        val leido = dao.porId(id)
        assertEquals("Tomate", leido?.nombre)

        // Borrar de la despensa oculta el alimento pero conserva la fila:
        // las recetas que lo usan como ingrediente no se rompen (#34)
        dao.ocultar(id)
        assertTrue(dao.observarTodos().first().none { it.id == id })
        assertEquals(false, dao.porId(id)?.enDespensa)
    }

    @Test
    fun observarTodosDevuelveLosInsertados() = runBlocking {
        dao.insertar(
            Alimento(
                nombre = "Arroz",
                kcal = 130,
                proteinas = 3,
                regionOrigen = "Mediterráneo",
                tipoPlato = "Cereal",
                cantidad = 1.0,
                unidad = "u"
            )
        )

        val lista = dao.observarTodos().first()
        assertEquals(1, lista.size)
        assertEquals("Arroz", lista.first().nombre)
    }
}
