package com.remy.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.remy.app.data.AlimentoRepository
import com.remy.app.data.PreferenciasRepository
import com.remy.app.data.RecetaConIngredientes
import com.remy.app.data.RecetaRepository
import com.remy.app.data.RemyDatabase
import com.remy.app.logica.clasificarRecetas
import com.remy.app.logica.filtrarRecetas
import com.remy.app.ui.EstadoCarga
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

/** Un ingrediente que falta en una receta, con su nombre para mostrarlo. */
data class FaltanteVista(
    val alimentoId: Long,
    val nombre: String,
    val insuficiente: Boolean = false
)

/** Una de las casi cocinables: la receta y exactamente lo que le falta. */
data class CasiCocinableVista(val receta: RecetaConIngredientes, val faltantes: List<FaltanteVista>)

/** Los dos grupos que muestra la pantalla de sugerencias (#34). */
data class GruposSugerencias(
    val completas: List<RecetaConIngredientes> = emptyList(),
    val casiCocinables: List<CasiCocinableVista> = emptyList(),
    /** Faltantes detectados (ausentes o con cantidad insuficiente) para poder elegirlos. */
    val faltantesDisponibles: List<FaltanteVista> = emptyList()
) {
    val hayAlgunaSugerencia: Boolean
        get() = completas.isNotEmpty() || casiCocinables.isNotEmpty()
}

/**
 * Calcula las sugerencias con el algoritmo de #33 y los filtros de #37: junta
 * la despensa, el catálogo, el máximo de faltantes y el resto de filtros
 * guardados en DataStore. La pantalla solo pinta lo que llega en [estado].
 */
class SugerenciasViewModel(
    private val recetas: RecetaRepository,
    alimentos: AlimentoRepository,
    preferencias: PreferenciasRepository
) : ViewModel() {

    private val reintentos = MutableStateFlow(0)

    val regiones: StateFlow<List<String>> = recetas.recetas
        .map { lista ->
            lista.map { it.receta.regionOrigen.trim() }
                .filter { it.isNotEmpty() }
                .distinct()
                .sorted()
        }
        .catch { emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val estado: StateFlow<EstadoCarga<GruposSugerencias>> = reintentos
        .flatMapLatest {
            combine(
                recetas.recetas,
                alimentos.alimentos,
                alimentos.catalogo,
                preferencias.filtros
            ) { recetas, despensa, catalogo, filtros ->
                val nombres = catalogo.associate { it.id to it.nombre }
                val filtradas = filtrarRecetas(recetas, filtros)
                val resultado = clasificarRecetas(
                    recetas = filtradas,
                    despensa = despensa,
                    maximoFaltantes = filtros.maximoFaltantes,
                    faltantesPermitidos = filtros.faltantesPermitidos
                )
                val candidatos = clasificarRecetas(
                    recetas = filtradas,
                    despensa = despensa,
                    maximoFaltantes = Int.MAX_VALUE,
                    faltantesPermitidos = emptySet()
                )
                val faltantesDisponibles = candidatos.casiCocinables
                    .flatMap { it.faltantes }
                    .distinctBy { it.alimentoId }
                    .map { f ->
                        FaltanteVista(
                            alimentoId = f.alimentoId,
                            nombre = nombres[f.alimentoId] ?: "Alimento ${f.alimentoId}",
                            insuficiente = f.motivo == com.remy.app.logica.MotivoFaltante.CANTIDAD_INSUFICIENTE
                        )
                    }
                    .sortedBy { it.nombre.lowercase() }

                GruposSugerencias(
                    completas = resultado.completas.map { it.receta },
                    casiCocinables = resultado.casiCocinables.map { casi ->
                        CasiCocinableVista(
                            receta = casi.receta,
                            faltantes = casi.faltantes.map { faltante ->
                                FaltanteVista(
                                    alimentoId = faltante.alimentoId,
                                    nombre = nombres[faltante.alimentoId] ?: "Alimento ${faltante.alimentoId}",
                                    insuficiente = faltante.motivo == com.remy.app.logica.MotivoFaltante.CANTIDAD_INSUFICIENTE
                                )
                            }
                        )
                    },
                    faltantesDisponibles = faltantesDisponibles
                )
            }
                .map<GruposSugerencias, EstadoCarga<GruposSugerencias>> { EstadoCarga.Datos(it) }
                .onStart { emit(EstadoCarga.Cargando) }
                .catch { emit(EstadoCarga.Error("No se han podido calcular las sugerencias.")) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EstadoCarga.Cargando)

    fun reintentar() {
        reintentos.update { it + 1 }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!! as Application
                val db = RemyDatabase.obtener(app)
                SugerenciasViewModel(
                    recetas = RecetaRepository(db.recetaDao()),
                    alimentos = AlimentoRepository(db.alimentoDao()),
                    preferencias = PreferenciasRepository.delContexto(app)
                )
            }
        }
    }
}
