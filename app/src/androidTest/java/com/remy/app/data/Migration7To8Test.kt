package com.remy.app.data

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.remy.app.data.database.RemyDatabase
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Migration7To8Test {

    private val nombreDb = "migration-7-8-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        RemyDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun migraDe7a8_conservaDatosYAnadeMacros() {
        helper.createDatabase(nombreDb, 7).apply {
            execSQL(
                """
                INSERT INTO recetas (id, nombre, kcal, proteinas, regionOrigen, tipoPlato, tiempoMinutos, pasos, porciones, fechaCocinada, fotoCocinada)
                VALUES (1, 'Prueba', 300, 20, 'Test', 'Principal', 15, 'Paso 1', 2, 12345, '/tmp/foto.jpg')
                """.trimIndent()
            )
            close()
        }

        val db = helper.runMigrationsAndValidate(nombreDb, 8, true, RemyDatabase.MIGRATION_7_8)
        db.query("SELECT nombre, kcal, proteinas, hidratos, grasas, porciones, fechaCocinada, fotoCocinada FROM recetas WHERE id = 1").use { cursor ->
            cursor.moveToFirst()
            assertEquals("Prueba", cursor.getString(0))
            assertEquals(300, cursor.getInt(1))
            assertEquals(20, cursor.getInt(2))
            assertEquals(0, cursor.getInt(3))
            assertEquals(0, cursor.getInt(4))
            assertEquals(2, cursor.getInt(5))
            assertEquals(12345L, cursor.getLong(6))
            assertEquals("/tmp/foto.jpg", cursor.getString(7))
        }
        db.close()
    }
}
