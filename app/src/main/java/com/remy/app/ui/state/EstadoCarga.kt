package com.remy.app.ui.state

/**
 * Estado de una pantalla que lee datos de forma asíncrona (#46).
 *
 * [Cargando] se muestra mientras Room devuelve la primera lectura, [Error]
 * permite explicar el fallo y ofrecer un reintento, y [Datos] envuelve lo que
 * se pinta cuando la consulta ha ido bien.
 */
sealed interface EstadoCarga<out T> {
    data object Cargando : EstadoCarga<Nothing>
    data class Error(val mensaje: String) : EstadoCarga<Nothing>
    data class Datos<T>(val datos: T) : EstadoCarga<T>
}
