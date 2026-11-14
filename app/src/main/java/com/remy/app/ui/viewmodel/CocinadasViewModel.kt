package com.remy.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.remy.app.data.database.RemyDatabase
import com.remy.app.data.model.RecetaConIngredientes
import com.remy.app.data.repository.RecetaRepository
import com.remy.app.ui.state.EstadoCarga
import java.time.LocalDate
import kotlin.random.Random
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

/** Datos de la pantalla de inicio: historial cocinado y una receta sugerida. */
data class InicioDatos(
    val cocinadas: List<RecetaConIngredientes>,
    val sugerencia: RecetaConIngredientes?
)

/** Historial de recetas cocinadas con foto y fecha (#44). */
@OptIn(ExperimentalCoroutinesApi::class)
class CocinadasViewModel(private val repositorio: RecetaRepository) : ViewModel() {

    private val reintentos = MutableStateFlow(0)

    val estado: StateFlow<EstadoCarga<InicioDatos>> = reintentos
        .flatMapLatest {
            combine(repositorio.cocinadas, repositorio.recetas) { cocinadas, todas ->
                val sugerencia = if (todas.isEmpty()) null else {
                    val semilla = LocalDate.now().toEpochDay().toInt()
                    todas[Random(semilla).nextInt(todas.size)]
                }
                EstadoCarga.Datos(InicioDatos(cocinadas = cocinadas, sugerencia = sugerencia)) as EstadoCarga<InicioDatos>
            }.onStart {
                emit(EstadoCarga.Cargando)
            }.catch { error ->
                emit(EstadoCarga.Error(error.message ?: "No se pudo cargar el inicio"))
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = EstadoCarga.Cargando
        )

    fun reintentar() {
        reintentos.update { it + 1 }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                val db = RemyDatabase.obtener(app)
                CocinadasViewModel(RecetaRepository(db.recetaDao()))
            }
        }
    }
}
