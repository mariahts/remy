package com.remy.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.remy.app.data.database.RemyDatabase
import com.remy.app.data.model.Alimento
import com.remy.app.data.repository.AlimentoRepository
import com.remy.app.ui.state.EstadoCarga
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class DespensaViewModel(private val repositorio: AlimentoRepository) : ViewModel() {

    private val reintentos = MutableStateFlow(0)

    val estado: StateFlow<EstadoCarga<List<Alimento>>> = reintentos
        .flatMapLatest {
            repositorio.alimentos
                .map<List<Alimento>, EstadoCarga<List<Alimento>>> { EstadoCarga.Datos(it) }
                .onStart { emit(EstadoCarga.Cargando) }
                .catch { emit(EstadoCarga.Error("No se ha podido cargar la despensa.")) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EstadoCarga.Cargando)

    /** Devuelve los errores de validación; si no hay ninguno, guarda. */
    fun guardar(editando: Alimento?, nombre: String, cantidad: String, unidad: String): ErroresAlimento {
        val errores = validarAlimento(nombre, cantidad, unidad)
        if (errores.hayErrores) return errores

        val valor = cantidad.replace(',', '.').toDouble()
        viewModelScope.launch {
            if (editando == null) {
                val limpio = nombre.trim()
                // Si el nombre ya está en el catálogo de recetas, se recupera
                // ese mismo alimento en vez de duplicarlo: así una receta que
                // usa "Arroz" vuelve a completarse al añadirlo otra vez (#34)
                val existente = repositorio.catalogo.first()
                    .firstOrNull { it.nombre.trim().equals(limpio, ignoreCase = true) }
                if (existente != null) {
                    repositorio.actualizar(
                        existente.copy(cantidad = valor, unidad = unidad.trim(), enDespensa = true)
                    )
                } else {
                    repositorio.guardar(Alimento(nombre = limpio, cantidad = valor, unidad = unidad.trim()))
                }
            } else {
                repositorio.actualizar(editando.copy(nombre = nombre.trim(), cantidad = valor, unidad = unidad.trim()))
            }
        }
        return errores
    }

    fun borrar(alimento: Alimento) {
        viewModelScope.launch { repositorio.borrar(alimento) }
    }

    fun reintentar() {
        reintentos.update { it + 1 }
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
