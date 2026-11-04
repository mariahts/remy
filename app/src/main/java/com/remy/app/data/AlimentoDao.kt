package com.remy.app.data

import androidx.room.Dao
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

    /** Los alimentos de la despensa del usuario (enDespensa = 1). */
    @Query("SELECT * FROM alimentos WHERE enDespensa = 1 ORDER BY nombre")
    fun observarTodos(): Flow<List<Alimento>>

    /** El catálogo completo, incluidos los ocultos de la despensa (#34). */
    @Query("SELECT * FROM alimentos ORDER BY nombre")
    fun observarCatalogo(): Flow<List<Alimento>>

    @Query("SELECT * FROM alimentos WHERE id = :id")
    suspend fun porId(id: Long): Alimento?

    @Update
    suspend fun actualizar(alimento: Alimento)

    /**
     * Quita el alimento de la despensa sin borrar la fila: las recetas que lo
     * usan como ingrediente siguen intactas y el nombre sigue disponible.
     */
    @Query("UPDATE alimentos SET enDespensa = 0 WHERE id = :id")
    suspend fun ocultar(id: Long)
}
