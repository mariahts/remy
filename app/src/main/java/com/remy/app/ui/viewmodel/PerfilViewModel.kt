package com.remy.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.remy.app.data.PreferenciasRepository
import com.remy.app.data.RecetaConIngredientes
import com.remy.app.data.RecetaRepository
import com.remy.app.data.RemyDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class PerfilViewModel(
    recetas: RecetaRepository,
    preferencias: PreferenciasRepository
) : ViewModel() {

    val favoritas: StateFlow<List<RecetaConIngredientes>> = combine(
        recetas.recetas,
        preferencias.favoritas
    ) { lista, ids ->
        lista.filter { it.receta.id in ids }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!! as Application
                val db = RemyDatabase.obtener(app)
                PerfilViewModel(
                    recetas = RecetaRepository(db.recetaDao()),
                    preferencias = PreferenciasRepository.delContexto(app)
                )
            }
        }
    }
}