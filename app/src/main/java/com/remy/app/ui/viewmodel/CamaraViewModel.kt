package com.remy.app.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.remy.app.data.PreferenciasRepository
import com.remy.app.logica.CopiaImagen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Prototipo multimedia de #26: recibe la imagen del selector de fotos del
 * sistema, la copia al almacenamiento interno de la app (filesDir) y
 * publica la ruta guardada para que la pantalla la muestre con Coil.
 *
 * La ruta se persiste en DataStore, así que sobrevive a un reinicio; si la
 * copia falla, simplemente no se cambia la ruta y la pantalla sigue
 * funcionando con la imagen anterior (o sin imagen).
 */
class CamaraViewModel(
    private val contexto: Context,
    private val preferencias: PreferenciasRepository
) : ViewModel() {

    /** Ruta absoluta de la última imagen elegida; null mientras no haya ninguna. */
    val rutaImagenElegida: StateFlow<String?> = preferencias.imagenElegida
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    /**
     * Copia la imagen indicada por [uri] a filesDir/imagenes y guarda su
     * ruta. Se hace en IO para no bloquear la interfaz.
     */
    fun guardarImagenElegida(uri: Uri) {
        viewModelScope.launch {
            val ruta = withContext(Dispatchers.IO) {
                val mime = contexto.contentResolver.getType(uri)
                val archivo = File(
                    File(contexto.filesDir, DIRECTORIO_IMAGENES),
                    CopiaImagen.nombreArchivo(mime)
                )
                val entrada = contexto.contentResolver.openInputStream(uri) ?: return@withContext null
                try {
                    CopiaImagen.copiar(entrada, archivo).absolutePath
                } finally {
                    entrada.close()
                }
            } ?: return@launch
            preferencias.guardarImagenElegida(ruta)
        }
    }

    companion object {
        const val DIRECTORIO_IMAGENES = "imagenes"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!!
                CamaraViewModel(app, PreferenciasRepository.delContexto(app))
            }
        }
    }
}
