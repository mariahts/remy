package com.remy.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.remy.app.data.model.Ingrediente
import com.remy.app.data.model.Receta
import com.remy.app.data.model.RecetaConIngredientes
import com.remy.app.data.model.RecetaDetalle
import kotlinx.coroutines.flow.Flow

@Dao
interface RecetaDao {
    @Insert
    suspend fun insertar(receta: Receta): Long

    @Insert
    suspend fun insertarIngredientes(ingredientes: List<Ingrediente>)

    /** Inserta la receta y sus ingredientes en una sola transacción. */
    @Transaction
    suspend fun insertarCompleta(receta: Receta, ingredientes: List<Ingrediente>): Long {
        val recetaId = insertar(receta)
        insertarIngredientes(ingredientes.map { it.copy(recetaId = recetaId) })
        return recetaId
    }

    @Transaction
    @Query("SELECT * FROM recetas ORDER BY nombre")
    fun observarTodas(): Flow<List<RecetaConIngredientes>>

    /** Recetas ya cocinadas, de la más reciente a la más antigua (#44). */
    @Transaction
    @Query("SELECT * FROM recetas WHERE fechaCocinada IS NOT NULL ORDER BY fechaCocinada DESC")
    fun observarCocinadas(): Flow<List<RecetaConIngredientes>>

    @Query("SELECT COUNT(*) FROM recetas")
    suspend fun contar(): Int

    @Transaction
    @Query("SELECT * FROM recetas WHERE id = :id")
    suspend fun porId(id: Long): RecetaConIngredientes?

    /** Detalle con los ingredientes resueltos hasta el nombre del alimento. */
    @Transaction
    @Query("SELECT * FROM recetas WHERE id = :id")
    suspend fun detallePorId(id: Long): RecetaDetalle?

    @Update
    suspend fun actualizar(receta: Receta)

    @Query("SELECT * FROM recetas WHERE nombre = :nombre LIMIT 1")
    suspend fun porNombre(nombre: String): Receta?

    /** Borra las recetas que ya no forman parte del catálogo de assets. */
    @Query("DELETE FROM recetas WHERE nombre NOT IN (:nombres)")
    suspend fun borrarFueraDelCatalogo(nombres: List<String>)

    @Query("DELETE FROM ingredientes WHERE recetaId = :recetaId")
    suspend fun borrarIngredientesDe(recetaId: Long)

    /** Sustituye los ingredientes de una receta del catálogo. */
    @Transaction
    suspend fun reemplazarIngredientes(recetaId: Long, ingredientes: List<Ingrediente>) {
        borrarIngredientesDe(recetaId)
        insertarIngredientes(ingredientes.map { it.copy(recetaId = recetaId) })
    }

    /** Limpia las marcas de cocinado para dejar el historial del usuario vacío. */
    @Query("UPDATE recetas SET fechaCocinada = NULL, fotoCocinada = NULL")
    suspend fun limpiarCocinadas()

    /** Borra la receta; sus ingredientes caen por la clave externa (CASCADE). */
    @Delete
    suspend fun borrar(receta: Receta)

    @Update
    suspend fun actualizarIngrediente(ingrediente: Ingrediente)

    @Delete
    suspend fun borrarIngrediente(ingrediente: Ingrediente)
}
