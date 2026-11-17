package com.remy.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.remy.app.data.database.RemyDatabase
import com.remy.app.data.model.RecetaConIngredientes
import com.remy.app.data.repository.AlimentoRepository
import com.remy.app.data.repository.PreferenciasRepository
import com.remy.app.data.repository.RecetaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/** Cifras resumidas que muestra la pantalla de perfil. */
data class CifrasPerfil(
    val guardadas: Int = 0,
    val favoritas: Int = 0,
    val despensa: Int = 0
)

class PerfilViewModel(
    recetas: RecetaRepository,
    preferencias: PreferenciasRepository,
    alimentos: AlimentoRepository
) : ViewModel() {

    val favoritas: StateFlow<List<RecetaConIngredientes>> = combine(
        recetas.recetas,
        preferencias.favoritas
    ) { lista, ids ->
        lista.filter { it.receta.id in ids }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val cifras: StateFlow<CifrasPerfil> = combine(
        recetas.recetas,
        preferencias.favoritas,
        alimentos.alimentos
    ) { lista, ids, despensa ->
        CifrasPerfil(
            guardadas = lista.size,
            favoritas = lista.count { it.receta.id in ids },
            despensa = despensa.count { it.enDespensa }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CifrasPerfil())

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!! as Application
                val db = RemyDatabase.obtener(app)
                PerfilViewModel(
                    recetas = RecetaRepository(db.recetaDao()),
                    preferencias = PreferenciasRepository.delContexto(app),
                    alimentos = AlimentoRepository(db.alimentoDao())
                )
            }
        }
    }
}
