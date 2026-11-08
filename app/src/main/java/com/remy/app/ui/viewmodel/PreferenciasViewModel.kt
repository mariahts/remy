package com.remy.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.remy.app.data.PreferenciasRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PreferenciasViewModel(private val repositorio: PreferenciasRepository) : ViewModel() {

    val filtrosActivos: StateFlow<Set<String>> = repositorio.filtrosActivos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    val maximoFaltantes: StateFlow<Int> = repositorio.maximoFaltantes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PreferenciasRepository.MAXIMO_POR_DEFECTO)

    fun alternarFiltro(filtro: String) {
        viewModelScope.launch {
            val actual = repositorio.filtrosActivos.first()
            repositorio.guardarFiltros(if (filtro in actual) actual - filtro else actual + filtro)
        }
    }

    fun cambiarMaximoFaltantes(valor: Int) {
        viewModelScope.launch { repositorio.guardarMaximoFaltantes(valor) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!! as Application
                PreferenciasViewModel(PreferenciasRepository.delContexto(app))
            }
        }
    }
}
