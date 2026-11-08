package com.remy.app.logica

import java.io.File
import java.io.InputStream

/**
 * Copia de la imagen elegida en el selector de fotos al almacenamiento
 * interno de la app (#26).
 *
 * Lógica pura (solo java.io) para poder probarla sin dispositivo: el
 * contenido no se reencodea, se traslada tal cual, así que el nombre del
 * archivo conserva la extensión que corresponde al tipo MIME.
 */
object CopiaImagen {

    const val NOMBRE_BASE = "imagen_elegida"
    const val EXTENSION_POR_DEFECTO = "jpg"

    /**
     * Nombre del archivo de destino a partir del tipo MIME de la imagen
     * elegida. Tipos desconocidos o ausentes van a `.jpg`, que es lo que
     * esperan la mayoría de galerías.
     */
    fun nombreArchivo(tipoMime: String?): String {
        val subtipo = tipoMime?.substringAfterLast('/')?.lowercase()?.trim().orEmpty()
        val extension = if (subtipo.matches(Regex("[a-z0-9]+")) && subtipo.isNotEmpty()) {
            subtipo
        } else {
            EXTENSION_POR_DEFECTO
        }
        // «image/jpeg» → «jpg» en lugar de «jpeg», para no dejar dos nombres distintos
        val normalizada = if (extension == "jpeg") "jpg" else extension
        return "$NOMBRE_BASE.$normalizada"
    }

    /**
     * Copia el flujo de origen al archivo de destino creando la carpeta si
     * hace falta y devolviendo el archivo ya escrito.
     */
    fun copiar(entrada: InputStream, destino: File): File {
        destino.parentFile?.mkdirs()
        destino.outputStream().use { salida ->
            entrada.copyTo(salida)
        }
        return destino
    }
}
