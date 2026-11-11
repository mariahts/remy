package com.remy.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.remy.app.data.RecetaConIngredientes
import com.remy.app.data.RecetaRepository
import com.remy.app.data.RemyDatabase
import com.remy.app.ui.EstadoCarga
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class)
class RecetasViewModel(private val repositorio: RecetaRepository) : ViewModel() {

    private val reintentos = MutableStateFlow(0)

    val estado: StateFlow<EstadoCarga<List<RecetaConIngredientes>>> = reintentos
        .flatMapLatest {
            repositorio.recetas
                .map<List<RecetaConIngredientes>, EstadoCarga<List<RecetaConIngredientes>>> {
                    EstadoCarga.Datos(it)
                }
                .onStart { emit(EstadoCarga.Cargando) }
                .catch { emit(EstadoCarga.Error("No se han podido cargar las recetas.")) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EstadoCarga.Cargando)

    fun reintentar() {
        reintentos.update { it + 1 }
    }

    companion object {
        /** Crea el ViewModel real de la app, con la base de datos Room. */
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!! as Application
                RecetasViewModel(RecetaRepository(RemyDatabase.obtener(app).recetaDao()))
            }
        }
    }
}
