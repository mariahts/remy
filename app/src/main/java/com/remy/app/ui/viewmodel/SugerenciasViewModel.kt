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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/** Un ingrediente que falta en una receta, con su nombre para mostrarlo. */
data class FaltanteVista(val nombre: String)

/** Una de las casi cocinables: la receta y exactamente lo que le falta. */
data class CasiCocinableVista(val receta: RecetaConIngredientes, val faltantes: List<FaltanteVista>)

/** Los dos grupos que muestra la pantalla de sugerencias (#34). */
data class GruposSugerencias(
    val completas: List<RecetaConIngredientes> = emptyList(),
    val casiCocinables: List<CasiCocinableVista> = emptyList()
) {
    val hayAlgunaSugerencia: Boolean
        get() = completas.isNotEmpty() || casiCocinables.isNotEmpty()
}

/**
 * Calcula las sugerencias con el algoritmo de #33: junta la despensa, el
 * catálogo de recetas, el máximo de faltantes guardado en preferencias y los
 * filtros de tipo de plato de #29. Toda la lógica vive aquí; el Composable
 * solo pinta lo que llega en [grupos].
 */
class SugerenciasViewModel(
    recetas: RecetaRepository,
    alimentos: AlimentoRepository,
    preferencias: PreferenciasRepository
) : ViewModel() {

    val grupos: StateFlow<GruposSugerencias> = combine(
        recetas.recetas,
        alimentos.alimentos,
        alimentos.catalogo,
        preferencias.maximoFaltantes,
        preferencias.filtrosActivos
    ) { recetas, despensa, catalogo, maximo, filtros ->
        val nombres = catalogo.associate { it.id to it.nombre }
        val filtradas = if (filtros.isEmpty()) recetas
        else recetas.filter { it.receta.tipoPlato in filtros }

        val resultado = clasificarRecetas(
            recetas = filtradas,
            alimentoIdsEnDespensa = despensa.map { it.id }.toSet(),
            maximoFaltantes = maximo
        )

        GruposSugerencias(
            completas = resultado.completas.map { it.receta },
            casiCocinables = resultado.casiCocinables.map { casi ->
                CasiCocinableVista(
                    receta = casi.receta,
                    faltantes = casi.faltantes.map { faltante ->
                        FaltanteVista(nombres[faltante.alimentoId] ?: "Alimento ${faltante.alimentoId}")
                    }
                )
            }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), GruposSugerencias())

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
