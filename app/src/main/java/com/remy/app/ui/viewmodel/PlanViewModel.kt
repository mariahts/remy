package com.remy.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.remy.app.data.AlimentoRepository
import com.remy.app.data.ComidaPlanificadaConReceta
import com.remy.app.data.PlanRepository
import com.remy.app.data.PreferenciasRepository
import com.remy.app.data.RecetaRepository
import com.remy.app.data.RemyDatabase
import com.remy.app.data.TipoComida
import com.remy.app.domain.faltantesDeReceta
import com.remy.app.domain.filtrarRecetas
import com.remy.app.domain.model.DisponibilidadReceta
import com.remy.app.ui.EstadoCarga
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Comida planificada lista para pintar, con su disponibilidad. */
data class ComidaPlanificadaVista(
    val plan: ComidaPlanificadaConReceta,
    val disponibilidad: DisponibilidadReceta
)

/** Datos expuestos para el día seleccionado y la semana. */
data class PlanDia(
    val fecha: LocalDate = LocalDate.now(),
    val comidas: Map<TipoComida, ComidaPlanificadaVista> = emptyMap(),
    /** Comidas planificadas para hoy, para la tarjeta destacada. */
    val comidasHoy: Map<TipoComida, ComidaPlanificadaVista> = emptyMap(),
    val diasConPlan: Set<LocalDate> = emptySet(),
    /** Recetas disponibles para elegir en el sheet, con disponibilidad y ordenadas (listas primero). */
    val recetasParaElegir: List<Pair<com.remy.app.data.RecetaConIngredientes, DisponibilidadReceta>> = emptyList(),
    /** Número de alimentos que hay ahora mismo en la despensa. */
    val alimentosEnDespensa: Int = 0
) {
    fun comidaDe(tipo: TipoComida): ComidaPlanificadaVista? = comidas[tipo]

    val listasParaCocinar: Int
        get() = recetasParaElegir.count { it.second is DisponibilidadReceta.Lista }
}

/** Gestiona la planificación semanal y calcula la disponibilidad con la despensa. */
@OptIn(ExperimentalCoroutinesApi::class)
class PlanViewModel(
    private val planRepositorio: PlanRepository,
    recetasRepositorio: RecetaRepository,
    alimentosRepositorio: AlimentoRepository,
    preferenciasRepositorio: PreferenciasRepository
) : ViewModel() {

    private val fechaSeleccionada = MutableStateFlow(LocalDate.now())
    val fechaActual: StateFlow<LocalDate> = fechaSeleccionada

    private val formatoFecha = DateTimeFormatter.ISO_LOCAL_DATE

    val estado: StateFlow<EstadoCarga<PlanDia>> = fechaSeleccionada
        .flatMapLatest { fecha ->
            val clave = fecha.format(formatoFecha)
            val claveHoy = LocalDate.now().format(formatoFecha)
            combine(
                planRepositorio.observarDia(clave),
                planRepositorio.observarDia(claveHoy),
                planRepositorio.observarFechasConPlan(),
                recetasRepositorio.recetas,
                alimentosRepositorio.alimentos,
                preferenciasRepositorio.filtros
            ) { valores ->
                @Suppress("UNCHECKED_CAST")
                val comidasDia = valores[0] as List<com.remy.app.data.ComidaPlanificadaConReceta>
                @Suppress("UNCHECKED_CAST")
                val comidasHoy = valores[1] as List<com.remy.app.data.ComidaPlanificadaConReceta>
                @Suppress("UNCHECKED_CAST")
                val fechasConPlan = valores[2] as List<String>
                @Suppress("UNCHECKED_CAST")
                val recetas = valores[3] as List<com.remy.app.data.RecetaConIngredientes>
                @Suppress("UNCHECKED_CAST")
                val despensa = valores[4] as List<com.remy.app.data.Alimento>
                val filtros = valores[5] as com.remy.app.data.FiltrosSugerencias

                val filtradas = filtrarRecetas(recetas, filtros)
                val faltantesPorReceta = recetas.associate { receta ->
                    receta.receta.id to faltantesDeReceta(receta, despensa)
                }

                fun disponibilidadDe(recetaId: Long): DisponibilidadReceta {
                    val faltantes = faltantesPorReceta[recetaId]
                    return if (faltantes.isNullOrEmpty()) DisponibilidadReceta.Lista
                    else DisponibilidadReceta.Faltan(faltantes)
                }

                fun aVista(lista: List<com.remy.app.data.ComidaPlanificadaConReceta>) =
                    lista.associate { comida ->
                        comida.plan.tipoComida to
                            ComidaPlanificadaVista(comida, disponibilidadDe(comida.receta.receta.id))
                    }

                val dias = fechasConPlan.mapNotNull { runCatching { LocalDate.parse(it) }.getOrNull() }.toSet()
                val recetasElegir = filtradas.map { receta ->
                    receta to disponibilidadDe(receta.receta.id)
                }.sortedWith(compareBy<Pair<com.remy.app.data.RecetaConIngredientes, DisponibilidadReceta>> {
                    when (it.second) { is DisponibilidadReceta.Lista -> 0; is DisponibilidadReceta.Faltan -> 1 }
                }.thenBy { (it.second as? DisponibilidadReceta.Faltan)?.cantidad ?: 0 }
                 .thenBy { it.first.receta.nombre.lowercase() })

                PlanDia(
                    fecha = fecha,
                    comidas = aVista(comidasDia),
                    comidasHoy = aVista(comidasHoy),
                    diasConPlan = dias,
                    recetasParaElegir = recetasElegir,
                    alimentosEnDespensa = despensa.size
                )
            }
                .map<PlanDia, EstadoCarga<PlanDia>> { EstadoCarga.Datos(it) }
                // Sin emitir "Cargando" al cambiar de dia: se mantiene el dia anterior
                // en pantalla hasta que llegan los datos nuevos, para que la pantalla no
                // parpadee ni pierda la posicion del scroll.
                .catch { emit(EstadoCarga.Error("No se ha podido cargar el plan.")) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EstadoCarga.Cargando)

    fun seleccionarDia(fecha: LocalDate) {
        fechaSeleccionada.value = fecha
    }

    fun planificar(tipoComida: TipoComida, recetaId: Long) {
        val fecha = fechaSeleccionada.value.format(formatoFecha)
        viewModelScope.launch { planRepositorio.guardar(fecha, tipoComida, recetaId) }
    }

    fun quitar(tipoComida: TipoComida) {
        val fecha = fechaSeleccionada.value.format(formatoFecha)
        viewModelScope.launch { planRepositorio.borrar(fecha, tipoComida) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!! as Application
                val db = RemyDatabase.obtener(app)
                PlanViewModel(
                    planRepositorio = PlanRepository(db.planDao()),
                    recetasRepositorio = RecetaRepository(db.recetaDao()),
                    alimentosRepositorio = AlimentoRepository(db.alimentoDao()),
                    preferenciasRepositorio = PreferenciasRepository.delContexto(app)
                )
            }
        }
    }
}
