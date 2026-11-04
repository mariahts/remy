package com.remy.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Alimento::class, Receta::class, Ingrediente::class],
    version = 3
)
abstract class RemyDatabase : RoomDatabase() {
    abstract fun alimentoDao(): AlimentoDao
    abstract fun recetaDao(): RecetaDao

    companion object {
        @Volatile
        private var instancia: RemyDatabase? = null

        /** Añade las tablas recetas e ingredientes sin tocar los alimentos existentes. */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS `recetas` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `nombre` TEXT NOT NULL,
                        `kcal` INTEGER NOT NULL,
                        `proteinas` INTEGER NOT NULL,
                        `regionOrigen` TEXT NOT NULL,
                        `tipoPlato` TEXT NOT NULL
                    )"""
                )
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS `ingredientes` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `recetaId` INTEGER NOT NULL,
                        `alimentoId` INTEGER NOT NULL,
                        `cantidad` REAL NOT NULL,
                        `unidad` TEXT NOT NULL,
                        FOREIGN KEY(`recetaId`) REFERENCES `recetas`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
                        FOREIGN KEY(`alimentoId`) REFERENCES `alimentos`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )"""
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_ingredientes_recetaId` ON `ingredientes` (`recetaId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_ingredientes_alimentoId` ON `ingredientes` (`alimentoId`)")
            }
        }

        /** Añade tiempo y pasos a las recetas existentes (#30). */
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `recetas` ADD COLUMN `tiempoMinutos` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `recetas` ADD COLUMN `pasos` TEXT NOT NULL DEFAULT ''")
            }
        }

        fun obtener(contexto: Context): RemyDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    contexto.applicationContext,
                    RemyDatabase::class.java,
                    "remy.db"
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3).build().also { instancia = it }
            }
    }
}
