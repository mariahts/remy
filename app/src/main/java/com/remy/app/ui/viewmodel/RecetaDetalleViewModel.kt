package com.remy.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.remy.app.data.RecetaDetalle
import com.remy.app.data.RecetaRepository
import com.remy.app.data.RemyDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecetaDetalleViewModel(private val repositorio: RecetaRepository) : ViewModel() {

    sealed interface Estado {
        data object Cargando : Estado
        data class Encontrada(val detalle: RecetaDetalle) : Estado
        data object NoEncontrada : Estado
    }

    private val _estado = MutableStateFlow<Estado>(Estado.Cargando)
    val estado: StateFlow<Estado> = _estado

    /** Carga la receta por su identificador; si no existe, queda en NoEncontrada. */
    fun cargar(id: Long) {
        _estado.value = Estado.Cargando
        viewModelScope.launch {
            val detalle = repositorio.detalle(id)
            _estado.value = if (detalle != null) Estado.Encontrada(detalle) else Estado.NoEncontrada
        }
    }

    companion object {
        /** Crea el ViewModel real de la app, con la base de datos Room. */
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!! as Application
                RecetaDetalleViewModel(RecetaRepository(RemyDatabase.obtener(app).recetaDao()))
            }
        }
    }
}
