package com.remy.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.remy.app.data.Alimento
import com.remy.app.data.AlimentoRepository
import com.remy.app.data.RemyDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DespensaViewModel(private val repositorio: AlimentoRepository) : ViewModel() {

    val alimentos: StateFlow<List<Alimento>> = repositorio.alimentos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Devuelve los errores de validación; si no hay ninguno, guarda. */
    fun guardar(editando: Alimento?, nombre: String, cantidad: String, unidad: String): ErroresAlimento {
        val errores = validarAlimento(nombre, cantidad, unidad)
        if (errores.hayErrores) return errores

        val valor = cantidad.replace(',', '.').toDouble()
        viewModelScope.launch {
            if (editando == null) {
                repositorio.guardar(Alimento(nombre = nombre.trim(), cantidad = valor, unidad = unidad.trim()))
            } else {
                repositorio.actualizar(editando.copy(nombre = nombre.trim(), cantidad = valor, unidad = unidad.trim()))
            }
        }
        return errores
    }

    fun borrar(alimento: Alimento) {
        viewModelScope.launch { repositorio.borrar(alimento) }
    }

    companion object {
        /** Crea el ViewModel real de la app, con la base de datos Room. */
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!! as Application
                DespensaViewModel(AlimentoRepository(RemyDatabase.obtener(app).alimentoDao()))
            }
        }
    }
}