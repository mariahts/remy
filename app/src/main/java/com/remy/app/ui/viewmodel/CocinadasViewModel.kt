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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/** Historial de recetas cocinadas con foto y fecha (#44). */
class CocinadasViewModel(private val repositorio: RecetaRepository) : ViewModel() {

    val cocinadas: StateFlow<List<RecetaConIngredientes>> = repositorio.cocinadas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    companion object {
        /** Crea el ViewModel real de la app, con la base de datos Room. */
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!! as Application
                CocinadasViewModel(RecetaRepository(RemyDatabase.obtener(app).recetaDao()))
            }
        }
    }
}
