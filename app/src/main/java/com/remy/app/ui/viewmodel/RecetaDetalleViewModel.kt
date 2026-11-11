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
import com.remy.app.logica.calcularDescuentoDespensa
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
    private suspend fun descontarIngredientes(recetaId: Long, racionesDeseadas: Int): List<String> {
        val detalle = repositorio.detalle(recetaId) ?: return emptyList()
        val ids = detalle.ingredientes.map { it.ingrediente.alimentoId }.distinct()
        val despensa = ids.mapNotNull { id -> alimentoDao.porId(id)?.let { id to it } }.toMap()
        val resultado = calcularDescuentoDespensa(
            ingredientes = detalle.ingredientes,
            porcionesReceta = detalle.receta.porciones,
            racionesDeseadas = racionesDeseadas,
            despensa = despensa
        )
        for (alimento in resultado.actualizaciones) {
            alimentoDao.actualizar(alimento)
        }
        return resultado.noDescontados
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
