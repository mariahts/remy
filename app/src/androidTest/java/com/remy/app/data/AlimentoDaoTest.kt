package com.remy.app.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
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
    fun insertarLeerYBorrar() = runBlocking {
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

        dao.borrar(leido!!)
        assertNull(dao.porId(id))
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
