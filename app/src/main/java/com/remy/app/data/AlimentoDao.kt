package com.remy.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AlimentoDao {
    @Insert
    suspend fun insertar(alimento: Alimento): Long

    /** Inserta solo si no existe ya con ese identificador (precarga, #30). */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarSiFalta(alimento: Alimento): Long

    @Query("SELECT * FROM alimentos ORDER BY nombre")
    fun observarTodos(): Flow<List<Alimento>>

    @Query("SELECT * FROM alimentos WHERE id = :id")
    suspend fun porId(id: Long): Alimento?

    @Update
    suspend fun actualizar(alimento: Alimento)

    @Delete
    suspend fun borrar(alimento: Alimento)
}
