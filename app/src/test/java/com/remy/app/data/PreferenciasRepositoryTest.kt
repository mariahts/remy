package com.remy.app.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
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
import java.io.File

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
        assertEquals(emptySet<String>(), repositorio.filtrosActivos.first())
        assertEquals(3, repositorio.maximoFaltantes.first())
    }

    @Test
    fun guardarYLeer_filtrosYMaximo() = runBlocking {
        repositorio.guardarFiltros(setOf("Entrante", "Postre"))
        repositorio.guardarMaximoFaltantes(5)

        assertEquals(setOf("Entrante", "Postre"), repositorio.filtrosActivos.first())
        assertEquals(5, repositorio.maximoFaltantes.first())
    }

    @Test
    fun alReabrir_seRecuperaLoGuardado() = runBlocking {
        repositorio.guardarFiltros(setOf("Principal"))
        repositorio.guardarMaximoFaltantes(7)

        // Se cierra la instancia actual y se abre otra sobre el mismo fichero,
        // igual que al reabrir la app
        alcance.cancel()
        alcance.coroutineContext.job.join()

        val reabierta = PreferenciasRepository(
            PreferenceDataStoreFactory.create { archivo }
        )
        assertEquals(setOf("Principal"), reabierta.filtrosActivos.first())
        assertEquals(7, reabierta.maximoFaltantes.first())
    }

    @Test
    fun primerArranque_noHayImagenElegida() = runBlocking {
        assertEquals(null, repositorio.imagenElegida.first())
    }

    @Test
    fun alReabrir_seRecuperaLaRutaDeLaImagenElegida() = runBlocking {
        val ruta = File.createTempFile("imagen_elegida", ".jpg").absolutePath
        repositorio.guardarImagenElegida(ruta)

        alcance.cancel()
        alcance.coroutineContext.job.join()

        val reabierta = PreferenciasRepository(
            PreferenceDataStoreFactory.create { archivo }
        )
        assertEquals(ruta, reabierta.imagenElegida.first())
    }
}
