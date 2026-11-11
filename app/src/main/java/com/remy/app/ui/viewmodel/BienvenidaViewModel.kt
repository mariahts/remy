package com.remy.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.remy.app.data.Alimento
import com.remy.app.data.AlimentoRepository
import com.remy.app.data.PreferenciasRepository
import com.remy.app.data.RemyDatabase
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

    val ingredientesComunes: List<IngredienteComun> = listOf(
        IngredienteComun("Aceite de oliva", 500.0, "ml"),
        IngredienteComun("Arroz", 500.0, "g"),
        IngredienteComun("Pasta", 500.0, "g"),
        IngredienteComun("Huevo", 6.0, "ud"),
        IngredienteComun("Leche", 1.0, "l"),
        IngredienteComun("Cebolla", 2.0, "ud"),
        IngredienteComun("Ajo", 1.0, "ud"),
        IngredienteComun("Tomate", 4.0, "ud"),
        IngredienteComun("Patata", 4.0, "ud"),
        IngredienteComun("Pechuga de pollo", 500.0, "g"),
        IngredienteComun("Atún", 2.0, "ud"),
        IngredienteComun("Harina", 500.0, "g"),
        IngredienteComun("Azúcar", 500.0, "g"),
        IngredienteComun("Queso", 200.0, "g")
    )

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
