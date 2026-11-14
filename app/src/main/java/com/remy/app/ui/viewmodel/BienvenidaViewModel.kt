package com.remy.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.remy.app.data.database.RemyDatabase
import com.remy.app.data.model.Alimento
import com.remy.app.data.model.Ingrediente
import com.remy.app.data.repository.AlimentoRepository
import com.remy.app.data.repository.PreferenciasRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/** Ingrediente común sugerido en la bienvenida, con cantidad y unidad por defecto. */
data class IngredienteComun(
    val nombre: String,
    val cantidad: Double,
    val unidad: String
)

class BienvenidaViewModel(
    private val alimentoRepository: AlimentoRepository,
    private val preferenciasRepository: PreferenciasRepository
) : ViewModel() {

    /**
     * Ingredientes básicos que se sugieren en la bienvenida.
     *
     * Las unidades son las mismas que usan las recetas (gramos y mililitros)
     * para que el algoritmo de sugerencias pueda compararlas.
     */
    val ingredientesComunes: List<IngredienteComun> = listOf(
        IngredienteComun("Aceite de oliva", 500.0, "ml"),
        IngredienteComun("Arroz", 500.0, "g"),
        IngredienteComun("Harina", 500.0, "g"),
        IngredienteComun("Huevo", 200.0, "g"),
        IngredienteComun("Leche", 1000.0, "ml"),
        IngredienteComun("Cebolla", 200.0, "g"),
        IngredienteComun("Ajo", 50.0, "g"),
        IngredienteComun("Tomate", 400.0, "g"),
        IngredienteComun("Patata", 500.0, "g"),
        IngredienteComun("Pechuga de pollo", 500.0, "g"),
        IngredienteComun("Atún", 400.0, "g"),
        IngredienteComun("Azúcar", 500.0, "g"),
        IngredienteComun("Queso", 300.0, "g")
    )

    /** Inserta los ingredientes seleccionados en la despensa sin marcar la bienvenida. */
    fun insertarSeleccion(seleccionados: Set<String>, alTerminar: () -> Unit = {}) {
        viewModelScope.launch {
            val catalogo = alimentoRepository.catalogo.first()
            seleccionados.forEach { nombre ->
                val comun = ingredientesComunes.firstOrNull { it.nombre == nombre } ?: return@forEach
                val existente = catalogo.firstOrNull { it.nombre.trim().equals(comun.nombre, ignoreCase = true) }
                    ?: catalogo.firstOrNull { coincideNombre(it.nombre, comun.nombre) }
                if (existente != null) {
                    alimentoRepository.actualizar(
                        existente.copy(
                            cantidad = comun.cantidad,
                            unidad = comun.unidad,
                            enDespensa = true
                        )
                    )
                } else {
                    alimentoRepository.guardar(
                        Alimento(
                            nombre = comun.nombre,
                            cantidad = comun.cantidad,
                            unidad = comun.unidad,
                            enDespensa = true
                        )
                    )
                }
            }
            alTerminar()
        }
    }

    /** Inserta los ingredientes seleccionados en la despensa y marca la bienvenida como completada. */
    fun completarConSeleccion(seleccionados: Set<String>, alTerminar: () -> Unit) {
        viewModelScope.launch {
            val catalogo = alimentoRepository.catalogo.first()
            seleccionados.forEach { nombre ->
                val comun = ingredientesComunes.firstOrNull { it.nombre == nombre } ?: return@forEach
                val existente = catalogo.firstOrNull { it.nombre.trim().equals(comun.nombre, ignoreCase = true) }
                    ?: catalogo.firstOrNull { coincideNombre(it.nombre, comun.nombre) }
                if (existente != null) {
                    alimentoRepository.actualizar(
                        existente.copy(
                            cantidad = comun.cantidad,
                            unidad = comun.unidad,
                            enDespensa = true
                        )
                    )
                } else {
                    alimentoRepository.guardar(
                        Alimento(
                            nombre = comun.nombre,
                            cantidad = comun.cantidad,
                            unidad = comun.unidad,
                            enDespensa = true
                        )
                    )
                }
            }
            preferenciasRepository.marcarBienvenidaCompletada()
            alTerminar()
        }
    }

    /** Marca la bienvenida como completada sin añadir ingredientes. */
    fun omitir(alTerminar: () -> Unit) {
        viewModelScope.launch {
            preferenciasRepository.marcarBienvenidaCompletada()
            alTerminar()
        }
    }

    private fun coincideNombre(catalogo: String, buscado: String): Boolean {
        val a = catalogo.trim().lowercase()
        val b = buscado.trim().lowercase()
        return a == b || a.contains(b) || b.contains(a)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!! as Application
                val db = RemyDatabase.obtener(app)
                BienvenidaViewModel(
                    alimentoRepository = AlimentoRepository(db.alimentoDao()),
                    preferenciasRepository = PreferenciasRepository.delContexto(app)
                )
            }
        }
    }
}
