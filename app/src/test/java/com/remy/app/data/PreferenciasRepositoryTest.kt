package com.remy.app.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.remy.app.data.model.FiltrosSugerencias
import com.remy.app.data.repository.PreferenciasRepository
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.job
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PreferenciasRepositoryTest {

    private lateinit var archivo: File
    private lateinit var alcance: CoroutineScope
    private lateinit var repositorio: PreferenciasRepository

    @Before
    fun crear() {
        archivo = File.createTempFile("preferencias", ".preferences_pb")
        archivo.deleteOnExit()
        alcance = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        repositorio = PreferenciasRepository(
            PreferenceDataStoreFactory.create(scope = alcance) { archivo }
        )
    }

    @After
    fun borrar() {
        alcance.cancel()
        archivo.delete()
    }

    @Test
    fun primerArranque_devuelveValoresPorDefecto() = runBlocking {
        val filtros = repositorio.filtros.first()

        assertEquals(FiltrosSugerencias(), filtros)
        assertEquals(3, filtros.maximoFaltantes)
        assertEquals(false, filtros.hayFiltros)
    }

    @Test
    fun guardarYLeer_todosLosFiltros() = runBlocking {
        repositorio.guardarTiposDePlato(setOf("Entrante", "Postre"))
        repositorio.guardarMaximoFaltantes(5)
        repositorio.guardarTiempoMaximo(30)
        repositorio.guardarKcalMaximas(500)
        repositorio.guardarProteinasMinimas(20)
        repositorio.guardarRegion("Mediterráneo")
        repositorio.guardarAltaProteina(true)
        repositorio.guardarBajaHidratos(true)
        repositorio.guardarBajaGrasa(true)

        val filtros = repositorio.filtros.first()
        assertEquals(setOf("Entrante", "Postre"), filtros.tiposDePlato)
        assertEquals(5, filtros.maximoFaltantes)
        assertEquals(30, filtros.tiempoMaximoMinutos)
        assertEquals(500, filtros.kcalMaximas)
        assertEquals(20, filtros.proteinasMinimas)
        assertEquals("Mediterráneo", filtros.region)
        assertEquals(true, filtros.altaProteina)
        assertEquals(true, filtros.bajaHidratos)
        assertEquals(true, filtros.bajaGrasa)
        assertEquals(true, filtros.hayFiltros)
    }

    @Test
    fun maximoFaltantes_seLimitaAlRangoValido() = runBlocking {
        repositorio.guardarMaximoFaltantes(25)
        assertEquals(PreferenciasRepository.MAXIMO_MAXIMO, repositorio.filtros.first().maximoFaltantes)

        repositorio.guardarMaximoFaltantes(-4)
        assertEquals(0, repositorio.filtros.first().maximoFaltantes)
    }

    @Test
    fun alReabrir_seRecuperaLoGuardado() = runBlocking {
        repositorio.guardarTiposDePlato(setOf("Principal"))
        repositorio.guardarMaximoFaltantes(7)
        repositorio.guardarTiempoMaximo(45)
        repositorio.guardarKcalMaximas(700)
        repositorio.guardarProteinasMinimas(30)
        repositorio.guardarRegion("Cantábrico")
        repositorio.guardarAltaProteina(true)
        repositorio.guardarBajaHidratos(true)
        repositorio.guardarBajaGrasa(false)

        // Se cierra la instancia actual y se abre otra sobre el mismo fichero,
        // igual que al reabrir la app
        alcance.cancel()
        alcance.coroutineContext.job.join()

        val reabierta = PreferenciasRepository(
            PreferenceDataStoreFactory.create { archivo }
        )
        val filtros = reabierta.filtros.first()
        assertEquals(setOf("Principal"), filtros.tiposDePlato)
        assertEquals(7, filtros.maximoFaltantes)
        assertEquals(45, filtros.tiempoMaximoMinutos)
        assertEquals(700, filtros.kcalMaximas)
        assertEquals(30, filtros.proteinasMinimas)
        assertEquals("Cantábrico", filtros.region)
        assertEquals(true, filtros.altaProteina)
        assertEquals(true, filtros.bajaHidratos)
        assertEquals(false, filtros.bajaGrasa)
    }

    @Test
    fun limpiarFiltros_vuelveAlEstadoPorDefecto() = runBlocking {
        repositorio.guardarTiposDePlato(setOf("Postre"))
        repositorio.guardarMaximoFaltantes(8)
        repositorio.guardarTiempoMaximo(15)
        repositorio.guardarKcalMaximas(300)
        repositorio.guardarProteinasMinimas(10)
        repositorio.guardarRegion("Europeo")
        repositorio.guardarAltaProteina(true)
        repositorio.guardarBajaHidratos(true)
        repositorio.guardarBajaGrasa(true)

        repositorio.limpiarFiltros()

        assertEquals(FiltrosSugerencias(), repositorio.filtros.first())
    }
}
