package com.remy.app.logica

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.File
import java.nio.file.Files

class CopiaImagenTest {

    @Test
    fun nombreArchivo_usaElSubtipoDelMime() {
        assertEquals("imagen_elegida.png", CopiaImagen.nombreArchivo("image/png"))
        assertEquals("imagen_elegida.webp", CopiaImagen.nombreArchivo("image/webp"))
    }

    @Test
    fun nombreArchivo_jpegSeNormalizaAJpg() {
        assertEquals("imagen_elegida.jpg", CopiaImagen.nombreArchivo("image/jpeg"))
    }

    @Test
    fun nombreArchivo_sonidoDesconocidoOCamposIncorrectosUsanJpg() {
        assertEquals("imagen_elegida.jpg", CopiaImagen.nombreArchivo(null))
        assertEquals("imagen_elegida.jpg", CopiaImagen.nombreArchivo(""))
        assertEquals("imagen_elegida.jpg", CopiaImagen.nombreArchivo("application/octet-stream"))
        assertEquals("imagen_elegida.jpg", CopiaImagen.nombreArchivo("image/"))
    }

    @Test
    fun copiar_transladaElContenidoYCreaLaCarpeta() {
        val carpeta = Files.createTempDirectory("imagenes").toFile()
        val destino = File(carpeta, "subcarpeta/imagen_elegida.jpg")
        val bytes = byteArrayOf(1, 2, 3, 4, 5)

        val resultado = CopiaImagen.copiar(ByteArrayInputStream(bytes), destino)

        assertTrue(resultado.exists())
        assertTrue(resultado.parentFile!!.isDirectory)
        assertTrue(resultado.readBytes().contentEquals(bytes))
    }
}
