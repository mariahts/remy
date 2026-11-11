package com.remy.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanDao {

    /** Guarda o reemplaza la comida planificada para un día y momento concretos. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(comida: PlannedMeal): Long

    /** Elimina la planificación de un momento concreto del día. */
    @Query("DELETE FROM comidas_planificadas WHERE fecha = :fecha AND tipoComida = :tipoComida")
    suspend fun borrar(fecha: String, tipoComida: TipoComida)

    /** Comidas planificadas de un día, con su receta e ingredientes. */
    @Transaction
    @Query("SELECT * FROM comidas_planificadas WHERE fecha = :fecha ORDER BY tipoComida")
    fun observarDia(fecha: String): Flow<List<ComidaPlanificadaConReceta>>

    /** Fechas que tienen al menos una comida planificada (para marcar la semana). */
    @Query("SELECT DISTINCT fecha FROM comidas_planificadas")
    fun observarFechasConPlan(): Flow<List<String>>

    /** Elimina toda la planificación del usuario. */
    @Query("DELETE FROM comidas_planificadas")
    suspend fun borrarTodo()
}
