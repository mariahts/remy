package com.remy.app.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.remy.app.data.AlimentoDao
import com.remy.app.data.AlmacenamientoFotos
import com.remy.app.data.RecetaDetalle
import com.remy.app.data.RecetaRepository
import com.remy.app.data.RemyDatabase
import java.util.Locale
import kotlin.math.max
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecetaDetalleViewModel(
    private val repositorio: RecetaRepository,
    private val alimentoDao: AlimentoDao,
    private val contexto: Application,
) : ViewModel() {

    sealed interface Estado {
        data object Cargando : Estado
        data class Encontrada(val detalle: RecetaDetalle) : Estado
        data object NoEncontrada : Estado
    }

    /** Avance del registro de cocción de la #35. */
    sealed interface EstadoCoccion {
        /** Nada pendiente: o aún no se ha cocinado o el aviso ya se ha mostrado. */
        data object Normal : EstadoCoccion

        data object Guardando : EstadoCoccion

        /** Registro guardado; `aviso` avisa si la foto no se pudo escribir. */
        data class Guardado(val fecha: Long, val rutaFoto: String?, val aviso: String?) : EstadoCoccion

        /** Algo ha fallado y se puede reintentar sin romper el flujo. */
        data class Error(val mensaje: String) : EstadoCoccion
    }

    private val _estado = MutableStateFlow<Estado>(Estado.Cargando)
    val estado: StateFlow<Estado> = _estado

    private val _estadoCoccion = MutableStateFlow<EstadoCoccion>(EstadoCoccion.Normal)
    val estadoCoccion: StateFlow<EstadoCoccion> = _estadoCoccion

    /** Carga la receta por su identificador; si no existe, queda en NoEncontrada. */
    fun cargar(id: Long) {
        _estado.value = Estado.Cargando
        viewModelScope.launch {
            val detalle = repositorio.detalle(id)
            _estado.value = if (detalle != null) Estado.Encontrada(detalle) else Estado.NoEncontrada
        }
    }

    /**
     * Marca la receta como cocinada: guarda la foto/fecha y descuenta de la
     * despensa los ingredientes escalados a [racionesDeseadas].
     * Si la foto no se puede guardar, el registro sigue adelante sin ella y se
     * deja constancia en `aviso`; solo un fallo de la base de datos pasa a Error.
     */
    fun marcarComoCocinada(id: Long, foto: Bitmap?, uriFoto: Uri?, racionesDeseadas: Int = 1) {
        if (_estadoCoccion.value is EstadoCoccion.Guardando) return
        _estadoCoccion.value = EstadoCoccion.Guardando

        viewModelScope.launch {
            val fecha = System.currentTimeMillis()
            var aviso: String? = null
            var rutaFoto: String? = null

            val hayFoto = foto != null || uriFoto != null
            try {
                rutaFoto = when {
                    foto != null -> AlmacenamientoFotos.guardarBitmap(
                        contexto, foto, AlmacenamientoFotos.nombrePara(id, fecha)
                    )
                    uriFoto != null -> AlmacenamientoFotos.guardarImagen(
                        contexto, uriFoto, AlmacenamientoFotos.nombrePara(id, fecha)
                    )
                    else -> null
                }
            } catch (e: Exception) {
                rutaFoto = null
                aviso = if (hayFoto) {
                    "No se ha podido guardar la foto del plato, pero el registro se ha guardado sin ella."
                } else {
                    null
                }
            }

            if (!hayFoto) {
                aviso = "Has confirmado sin foto: el registro queda sin fotografía del plato."
            }

            val guardado = try {
                repositorio.marcarComoCocinada(id, fecha, rutaFoto)
            } catch (e: Exception) {
                _estadoCoccion.value = EstadoCoccion.Error(
                    "No se ha podido guardar el registro de cocción. Inténtalo de nuevo."
                )
                return@launch
            }

            if (guardado) {
                try {
                    descontarIngredientes(id, racionesDeseadas)
                } catch (_: Exception) {
                    // El registro de cocción ya se guardó; no bloqueamos por el descuento.
                }
                cargar(id)
                _estadoCoccion.value = EstadoCoccion.Guardado(fecha, rutaFoto, aviso)
            } else {
                _estadoCoccion.value = EstadoCoccion.Error(
                    "Esta receta ya no existe: no se ha podido marcar como cocinada."
                )
            }
        }
    }

    /** Descuenta de la despensa las cantidades necesarias para [racionesDeseadas]. */
    private suspend fun descontarIngredientes(recetaId: Long, racionesDeseadas: Int) {
        val detalle = repositorio.detalle(recetaId) ?: return
        val porciones = detalle.receta.porciones.coerceAtLeast(1)
        val raciones = racionesDeseadas.coerceAtLeast(1)
        for (ing in detalle.ingredientes) {
            val necesario = ing.ingrediente.cantidad * raciones / porciones.toDouble()
            if (necesario <= 0.0) continue
            val alimento = alimentoDao.porId(ing.ingrediente.alimentoId) ?: continue
            if (!alimento.enDespensa) continue
            val restante = restarCantidad(
                disponible = alimento.cantidad,
                unidadDisponible = alimento.unidad,
                necesario = necesario,
                unidadNecesaria = ing.ingrediente.unidad
            ) ?: continue
            alimentoDao.actualizar(alimento.copy(cantidad = max(0.0, restante)))
        }
    }

    /**
     * Resta [necesario] de [disponible] si las unidades son compatibles.
     * Devuelve la cantidad restante en la unidad de la despensa, o null si no se puede convertir.
     */
    private fun restarCantidad(
        disponible: Double,
        unidadDisponible: String,
        necesario: Double,
        unidadNecesaria: String
    ): Double? {
        val disp = normalizar(disponible, unidadDisponible)
        val nec = normalizar(necesario, unidadNecesaria)
        return if (disp != null && nec != null && disp.second == nec.second) {
            val restanteBase = disp.first - nec.first
            convertirDesdeBase(restanteBase, disp.second, unidadDisponible)
        } else if (unidadDisponible.trim().equals(unidadNecesaria.trim(), ignoreCase = true)) {
            disponible - necesario
        } else {
            null
        }
    }

    private fun normalizar(cantidad: Double, unidad: String): Pair<Double, String>? {
        val u = unidad.trim().lowercase(Locale.getDefault())
        return when (u) {
            "kg", "kilo", "kilos" -> cantidad * 1000.0 to "masa"
            "g", "gr", "gramo", "gramos" -> cantidad to "masa"
            "l", "litro", "litros" -> cantidad * 1000.0 to "volumen"
            "ml", "mililitro", "mililitros" -> cantidad to "volumen"
            "ud", "uds", "unidad", "unidades", "u" -> cantidad to "unidad"
            else -> null
        }
    }

    private fun convertirDesdeBase(cantidadBase: Double, familia: String, unidadDestino: String): Double {
        val u = unidadDestino.trim().lowercase(Locale.getDefault())
        return when (familia) {
            "masa" -> if (u == "kg" || u == "kilo" || u == "kilos") cantidadBase / 1000.0 else cantidadBase
            "volumen" -> if (u == "l" || u == "litro" || u == "litros") cantidadBase / 1000.0 else cantidadBase
            else -> cantidadBase
        }
    }

    /** Descarta el aviso mostrado tras guardar. */
    fun limpiarCoccion() {
        _estadoCoccion.value = EstadoCoccion.Normal
    }

    companion object {
        /** Crea el ViewModel real de la app, con la base de datos Room. */
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!! as Application
                val db = RemyDatabase.obtener(app)
                RecetaDetalleViewModel(
                    RecetaRepository(db.recetaDao()),
                    db.alimentoDao(),
                    app,
                )
            }
        }
    }
}
