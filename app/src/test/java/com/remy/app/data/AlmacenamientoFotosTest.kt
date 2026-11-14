package com.remy.app.data

import com.remy.app.data.local.AlmacenamientoFotos
import com.remy.app.data.model.Receta
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AlmacenamientoFotosTest {

    @Test
    fun nombrePara_unaRecetaCocinadaDosVecesTieneDosArchivos() {
        val primera = AlmacenamientoFotos.nombrePara(recetaId = 7, fecha = 1_000L)
        val segunda = AlmacenamientoFotos.nombrePara(recetaId = 7, fecha = 2_000L)

        assertEquals("receta_7_1000.jpg", primera)
        assertEquals("receta_7_2000.jpg", segunda)
    }

    @Test
    fun nombrePara_seDiferenciaEntreRecetas() {
        val deUna = AlmacenamientoFotos.nombrePara(recetaId = 7, fecha = 1_000L)
        val deOtra = AlmacenamientoFotos.nombrePara(recetaId = 8, fecha = 1_000L)

        assertEquals("receta_7_1000.jpg", deUna)
        assertEquals("receta_8_1000.jpg", deOtra)
    }

    @Test
    fun recetaNueva_empiezaSinCocinar() {
        val receta = Receta(nombre = "Lentejas", tipoPlato = "Principal")

        assertNull(receta.fechaCocinada)
        assertNull(receta.fotoCocinada)
    }
}
