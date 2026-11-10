package com.remy.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.dataStorePreferencias by preferencesDataStore(name = "preferencias")

/**
 * Preferencias de filtrado de las sugerencias (#29 y #37): tipo de plato,
 * máximo de ingredientes faltantes, tiempo, calorías, proteínas y región.
 *
 * Primer arranque sin preferencias: se devuelven los valores por defecto sin
 * error. DataStore escribe en Dispatchers.IO, fuera del hilo de interfaz.
 */
class PreferenciasRepository(private val dataStore: DataStore<Preferences>) {

    val filtros: Flow<FiltrosSugerencias> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferencias ->
            FiltrosSugerencias(
                tiposDePlato = preferencias[CLAVE_FILTROS] ?: emptySet(),
                maximoFaltantes = (preferencias[CLAVE_MAXIMO_FALTANTES] ?: MAXIMO_POR_DEFECTO)
                    .coerceIn(0, MAXIMO_MAXIMO),
                tiempoMaximoMinutos = (preferencias[CLAVE_TIEMPO_MAXIMO] ?: 0).coerceAtLeast(0),
                kcalMaximas = (preferencias[CLAVE_KCAL_MAXIMAS] ?: 0).coerceAtLeast(0),
                proteinasMinimas = (preferencias[CLAVE_PROTEINAS_MINIMAS] ?: 0).coerceAtLeast(0),
                region = preferencias[CLAVE_REGION] ?: "",
                faltantesPermitidos = (preferencias[CLAVE_FALTANTES_PERMITIDOS] ?: emptySet())
                    .mapNotNull { it.toLongOrNull() }
                    .toSet()
            )
        }

    /** Identificadores de las recetas marcadas como favoritas. */
    val favoritas: Flow<Set<Long>> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferencias ->
            (preferencias[CLAVE_FAVORITAS] ?: emptySet())
                .mapNotNull { it.toLongOrNull() }
                .toSet()
        }

    suspend fun guardarTiposDePlato(tipos: Set<String>) {
        dataStore.edit { it[CLAVE_FILTROS] = tipos }
    }

    suspend fun guardarMaximoFaltantes(valor: Int) {
        dataStore.edit { it[CLAVE_MAXIMO_FALTANTES] = valor.coerceIn(0, MAXIMO_MAXIMO) }
    }

    suspend fun guardarTiempoMaximo(minutos: Int) {
        dataStore.edit { it[CLAVE_TIEMPO_MAXIMO] = minutos.coerceAtLeast(0) }
    }

    suspend fun guardarKcalMaximas(kcal: Int) {
        dataStore.edit { it[CLAVE_KCAL_MAXIMAS] = kcal.coerceAtLeast(0) }
    }

    suspend fun guardarProteinasMinimas(gramos: Int) {
        dataStore.edit { it[CLAVE_PROTEINAS_MINIMAS] = gramos.coerceAtLeast(0) }
    }

    suspend fun guardarRegion(region: String) {
        dataStore.edit { it[CLAVE_REGION] = region.trim() }
    }

    suspend fun guardarFaltantesPermitidos(ids: Set<Long>) {
        dataStore.edit { it[CLAVE_FALTANTES_PERMITIDOS] = ids.map { id -> id.toString() }.toSet() }
    }

    suspend fun limpiarFiltros() {
        dataStore.edit { preferencias ->
            preferencias.remove(CLAVE_FILTROS)
            preferencias.remove(CLAVE_MAXIMO_FALTANTES)
            preferencias.remove(CLAVE_TIEMPO_MAXIMO)
            preferencias.remove(CLAVE_KCAL_MAXIMAS)
            preferencias.remove(CLAVE_PROTEINAS_MINIMAS)
            preferencias.remove(CLAVE_REGION)
            preferencias.remove(CLAVE_FALTANTES_PERMITIDOS)
        }
    }

    suspend fun alternarFavorita(id: Long) {
        dataStore.edit { preferencias ->
            val actual = preferencias[CLAVE_FAVORITAS] ?: emptySet()
            val clave = id.toString()
            preferencias[CLAVE_FAVORITAS] = if (clave in actual) actual - clave else actual + clave
        }
    }

    companion object {
        val CLAVE_FILTROS = stringSetPreferencesKey("filtros_activos")
        val CLAVE_MAXIMO_FALTANTES = intPreferencesKey("maximo_faltantes")
        val CLAVE_TIEMPO_MAXIMO = intPreferencesKey("tiempo_maximo_minutos")
        val CLAVE_KCAL_MAXIMAS = intPreferencesKey("kcal_maximas")
        val CLAVE_PROTEINAS_MINIMAS = intPreferencesKey("proteinas_minimas")
        val CLAVE_REGION = stringPreferencesKey("region")
        val CLAVE_FALTANTES_PERMITIDOS = stringSetPreferencesKey("faltantes_permitidos")
        val CLAVE_FAVORITAS = stringSetPreferencesKey("recetas_favoritas")

        const val MAXIMO_POR_DEFECTO = 3
        const val MAXIMO_MAXIMO = 20

        fun delContexto(contexto: Context): PreferenciasRepository =
            PreferenciasRepository(contexto.applicationContext.dataStorePreferencias)
    }
}
