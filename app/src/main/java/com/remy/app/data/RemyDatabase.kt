package com.remy.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Alimento::class], version = 1)
abstract class RemyDatabase : RoomDatabase() {
    abstract fun alimentoDao(): AlimentoDao

    companion object {
        @Volatile
        private var instancia: RemyDatabase? = null

        fun obtener(contexto: Context): RemyDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    contexto.applicationContext,
                    RemyDatabase::class.java,
                    "remy.db"
                ).build().also { instancia = it }
            }
    }
}
