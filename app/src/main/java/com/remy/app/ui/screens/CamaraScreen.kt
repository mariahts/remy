package com.remy.app.ui.screens

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.remy.app.ui.components.AlternativaGaleria
import com.remy.app.ui.components.CamaraConPermiso
import com.remy.app.ui.components.EstadoPermiso
import com.remy.app.ui.components.PermisoCamara
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Prototipo de la función multimedia de la app (#25): pide el permiso de
 * cámara, muestra la vista previa, captura una foto con CameraX y la pinta.
 * Si el permiso se deniega (o el dispositivo no tiene cámara), se ofrece el
 * selector de fotos del sistema como alternativa.
 *
 * La cámara y el aviso de error viven ahora en `ui/components/CapturaFoto.kt`,
 * compartidos con el flujo de cocción de la #35.
 */
@Composable
fun CamaraScreen() {
    val contexto = LocalContext.current
    var estadoPermiso by remember { mutableStateOf<EstadoPermiso>(EstadoPermiso.Comprobando) }
    var fotoCapturada by remember { mutableStateOf<Bitmap?>(null) }
    var imagenGaleria by remember { mutableStateOf<Uri?>(null) }
    var mensajeError by remember { mutableStateOf<String?>(null) }

    val hayCamara = remember {
        contexto.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

    val lanzadorGaleria = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> imagenGaleria = uri }

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
        imagenGaleria?.let {
            ImagenDeGaleria(uri = it, descripcion = "Imagen del selector de fotos")
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

/** Decodifica la imagen elegida en el selector de fotos y la muestra. */
@Composable
private fun ImagenDeGaleria(uri: Uri, descripcion: String) {
    val contexto = LocalContext.current
    var bitmap by remember(uri) { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(uri) {
        bitmap = withContext(Dispatchers.IO) {
            contexto.contentResolver.openInputStream(uri)?.use { entrada ->
                BitmapFactory.decodeStream(entrada)
            }
        }
    }

    bitmap?.let {
        Imagen(bitmap = it, descripcion = descripcion)
    }
}
