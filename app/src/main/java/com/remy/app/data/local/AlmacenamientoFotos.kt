package com.remy.app.data.local

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Persiste la foto del plato en el almacenamiento interno de la app (#35).
 * La carpeta es privada, sobrevive a los reinicios y se borra si la usuaria
 * desinstala la app. La ruta absoluta del archivo es la que se guarda en la
 * receta; si algún día faltara, la pantalla pinta un marcador de reserva.
 *
 * El selector de fotos devuelve un `Uri` temporal: se copian sus bytes tal
 * cual (sin decodificar, para no cargar la imagen entera en memoria) y la
 * cámara entrega un `Bitmap`, que se comprime en JPEG.
 */
object AlmacenamientoFotos {

    /** Carpeta interna donde viven las fotos de los platos. */
    const val CARPETA = "fotos_plato"

    /** Calidad del JPEG al guardar una foto hecha con la cámara. */
    private const val CALIDAD_JPEG = 90

    /**
     * Nombre del archivo: `receta_<id>_<fecha>.jpg`. Es una función pura para
     * poder testearla sin Android: cada cocción tiene su propio nombre.
     */
    fun nombrePara(recetaId: Long, fecha: Long): String = "receta_${recetaId}_$fecha.jpg"

    /** Copia la imagen elegida en el selector a la carpeta interna. */
    suspend fun guardarImagen(contexto: Context, uri: Uri, nombre: String): String =
        withContext(Dispatchers.IO) {
            val origen = contexto.contentResolver.openInputStream(uri)
                ?: throw IOException("No se pudo abrir la imagen elegida")
            val destino = archivo(contexto, nombre)
            origen.use { entrada ->
                FileOutputStream(destino).use { salida -> entrada.copyTo(salida) }
            }
            destino.absolutePath
        }

    /** Comprime la foto capturada con la cámara en JPEG dentro de la carpeta interna. */
    suspend fun guardarBitmap(contexto: Context, bitmap: Bitmap, nombre: String): String =
        withContext(Dispatchers.IO) {
            val destino = archivo(contexto, nombre)
            FileOutputStream(destino).use { salida ->
                if (!bitmap.compress(Bitmap.CompressFormat.JPEG, CALIDAD_JPEG, salida)) {
                    throw IOException("No se pudo comprimir la foto del plato")
                }
            }
            destino.absolutePath
        }

    /** Archivo de destino dentro de la carpeta interna, creándola si no existe. */
    private fun archivo(contexto: Context, nombre: String): File {
        val carpeta = File(contexto.filesDir, CARPETA)
        if (!carpeta.exists() && !carpeta.mkdirs()) {
            throw IOException("No se pudo crear la carpeta de fotos")
        }
        return File(carpeta, nombre)
    }
}
