package com.remy.app.ui.screens

import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.remy.app.R
import com.remy.app.ui.components.AlternativaGaleria
import com.remy.app.ui.components.CamaraConPermiso
import com.remy.app.ui.components.EstadoPermiso
import com.remy.app.ui.components.PermisoCamara
import com.remy.app.ui.viewmodel.CamaraViewModel
import java.io.File

/**
 * Prototipo de la función multimedia de la app (#25 y #26): pide el permiso
 * de cámara, muestra la vista previa y captura una foto con CameraX. En
 * paralelo permite elegir una imagen con el selector de fotos del sistema:
 * la imagen se copia al almacenamiento interno, su ruta se guarda en
 * preferencias y se vuelve a mostrar con Coil al reabrir la app (#26).
 * Si el permiso se deniega (o el dispositivo no tiene cámara), se ofrece el
 * selector de fotos del sistema como alternativa.
 *
 * La cámara y el aviso de error viven ahora en `ui/components/CapturaFoto.kt`,
 * compartidos con el flujo de cocción de la #35.
 */
@Composable
fun CamaraScreen(
    camara: CamaraViewModel = viewModel(factory = CamaraViewModel.Factory)
) {
    val contexto = LocalContext.current
    var estadoPermiso by remember { mutableStateOf<EstadoPermiso>(EstadoPermiso.Comprobando) }
    var fotoCapturada by remember { mutableStateOf<Bitmap?>(null) }
    val rutaImagenElegida by camara.rutaImagenElegida.collectAsStateWithLifecycle()
    var mensajeError by remember { mutableStateOf<String?>(null) }

    val hayCamara = remember {
        contexto.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

    val lanzadorGaleria = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) camara.guardarImagenElegida(uri)
    }

    PermisoCamara { estadoPermiso = it }

    Column(modifier = Modifier.fillMaxSize()) {
        when (estadoPermiso) {
            EstadoPermiso.Comprobando -> Text(
                text = "Comprobando el permiso de cámara…",
                modifier = Modifier.padding(16.dp)
            )

            EstadoPermiso.Concedido -> if (hayCamara) {
                CamaraConPermiso(
                    alFoto = { fotoCapturada = it; mensajeError = null },
                    alFallar = { mensajeError = it },
                    alElegirImagen = { lanzadorGaleria.launch("image/*") },
                    modifier = Modifier.weight(1f)
                )
            } else {
                AlternativaGaleria(
                    mensaje = "Este dispositivo no tiene cámara. Elige una imagen de tu galería.",
                    alElegirImagen = { lanzadorGaleria.launch("image/*") },
                    modifier = Modifier.weight(1f)
                )
            }

            EstadoPermiso.Denegado -> AlternativaGaleria(
                mensaje = "Sin permiso de cámara no se pueden hacer fotos. " +
                        "Puedes conceder el permiso en Ajustes o usar una imagen de tu galería.",
                alElegirImagen = { lanzadorGaleria.launch("image/*") },
                modifier = Modifier.weight(1f)
            )
        }

        mensajeError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        fotoCapturada?.let {
            Imagen(bitmap = it, descripcion = "Foto capturada")
        }
        rutaImagenElegida?.let { ruta ->
            ImagenGuardada(ruta = ruta, descripcion = "Imagen elegida en el selector de fotos")
        }
    }
}

/** Muestra la foto que se acaba de capturar con la cámara. */
@Composable
private fun Imagen(bitmap: Bitmap, descripcion: String) {
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = descripcion,
        modifier = Modifier.fillMaxWidth()
    )
}

/**
 * Muestra la imagen elegida en el selector y copiada al almacenamiento
 * interno (#26), cargándola con Coil. El marcador de reserva cubre tanto la
 * carga como un fallo de lectura, así que la pantalla nunca se queda en
 * blanco ni se rompe.
 */
@Composable
private fun ImagenGuardada(ruta: String, descripcion: String) {
    val reserva = painterResource(R.drawable.ic_launcher_foreground)
    AsyncImage(
        model = File(ruta),
        contentDescription = descripcion,
        placeholder = reserva,
        error = reserva,
        fallback = reserva,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 240.dp)
    )
}
