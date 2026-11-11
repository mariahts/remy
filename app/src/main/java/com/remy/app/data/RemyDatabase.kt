package com.remy.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/** Conversores para tipos usados en la planificación semanal. */
object ConvertidoresPlan {
    @TypeConverter
    fun deTipoComida(valor: TipoComida): String = valor.name

    @TypeConverter
    fun aTipoComida(valor: String): TipoComida = TipoComida.valueOf(valor)
}

@Database(
    entities = [Alimento::class, Receta::class, Ingrediente::class, PlannedMeal::class],
    version = 8
)
@TypeConverters(ConvertidoresPlan::class)
abstract class RemyDatabase : RoomDatabase() {
    abstract fun alimentoDao(): AlimentoDao
    abstract fun recetaDao(): RecetaDao
    abstract fun planDao(): PlanDao

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

        /** Separa despensa y catálogo con la columna enDespensa (#34). */
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `alimentos` ADD COLUMN `enDespensa` INTEGER NOT NULL DEFAULT 1")
            }
        }

        /** Añade la marca de cocción (fecha y foto del plato) a las recetas (#35). */
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `recetas` ADD COLUMN `fechaCocinada` INTEGER")
                db.execSQL("ALTER TABLE `recetas` ADD COLUMN `fotoCocinada` TEXT")
            }
        }

        /** Añade el número de raciones de cada receta para mostrar valores por porción. */
        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `recetas` ADD COLUMN `porciones` INTEGER NOT NULL DEFAULT 4")
            }
        }

        /** Crea la tabla de comidas planificadas por día y momento. */
        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS `comidas_planificadas` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `fecha` TEXT NOT NULL,
                        `tipoComida` TEXT NOT NULL,
                        `recetaId` INTEGER NOT NULL,
                        FOREIGN KEY(`recetaId`) REFERENCES `recetas`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )"""
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_comidas_planificadas_fecha` ON `comidas_planificadas` (`fecha`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_comidas_planificadas_recetaId` ON `comidas_planificadas` (`recetaId`)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_comidas_planificadas_fecha_tipoComida` ON `comidas_planificadas` (`fecha`, `tipoComida`)")
            }
        }

        /** Añade hidratos y grasas a las recetas. */
        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `recetas` ADD COLUMN `hidratos` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `recetas` ADD COLUMN `grasas` INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun obtener(contexto: Context): RemyDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    contexto.applicationContext,
                    RemyDatabase::class.java,
                    "remy.db"
                ).addMigrations(
                    MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8
                ).build().also { instancia = it }
            }
    }
}
