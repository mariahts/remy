package com.remy.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStorePreferencias by preferencesDataStore(name = "preferencias")

/**
 * Preferencias de filtrado de la app (#29): filtros activos de tipo de plato y
 * número máximo de ingredientes faltantes para las sugerencias.
 *
 * Primer arranque sin preferencias: se devuelven los valores por defecto sin error.
 * DataStore realiza la escritura en Dispatchers.IO, fuera del hilo de interfaz.
 */
class PreferenciasRepository(private val dataStore: DataStore<Preferences>) {

    val filtrosActivos: Flow<Set<String>> =
        dataStore.data.map { it[CLAVE_FILTROS] ?: emptySet() }

    val maximoFaltantes: Flow<Int> =
        dataStore.data.map { it[CLAVE_MAXIMO_FALTANTES] ?: MAXIMO_POR_DEFECTO }

    suspend fun guardarFiltros(filtros: Set<String>) {
        dataStore.edit { it[CLAVE_FILTROS] = filtros }
    }

    suspend fun guardarMaximoFaltantes(valor: Int) {
        dataStore.edit { it[CLAVE_MAXIMO_FALTANTES] = valor }
    }

    companion object {
        val CLAVE_FILTROS = stringSetPreferencesKey("filtros_activos")
        val CLAVE_MAXIMO_FALTANTES = intPreferencesKey("maximo_faltantes")
        const val MAXIMO_POR_DEFECTO = 3

        fun delContexto(contexto: Context): PreferenciasRepository =
            PreferenciasRepository(contexto.applicationContext.dataStorePreferencias)
    }
}
