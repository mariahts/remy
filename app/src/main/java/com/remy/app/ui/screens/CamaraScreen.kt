package com.remy.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

/**
 * Estado del permiso de cámara. Se modela aparte para no quedarnos en un
 * limbo mientras el sistema responde: mientras `Comprobando`, no se pinta
 * ni la cámara ni el mensaje de error.
 */
private sealed interface EstadoPermiso {
    data object Comprobando : EstadoPermiso
    data object Concedido : EstadoPermiso
    data object Denegado : EstadoPermiso
}

/**
 * Prototipo de la función multimedia de la app (#25): pide el permiso de
 * cámara, muestra la vista previa, captura una foto con CameraX y la pinta.
 * Si el permiso se deniega (o el dispositivo no tiene cámara), se ofrece el
 * selector de fotos del sistema como alternativa.
 */
@Composable
fun CamaraScreen() {
    val contexto = LocalContext.current
    var estadoPermiso by remember { mutableStateOf<EstadoPermiso>(EstadoPermiso.Comprobando) }
    var fotoCapturada by remember { mutableStateOf<Bitmap?>(null) }
    var imagenGaleria by remember { mutableStateOf<Uri?>(null) }

    val hayCamara = remember {
        contexto.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

    val lanzadorPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        estadoPermiso = if (concedido) EstadoPermiso.Concedido else EstadoPermiso.Denegado
    }

    val lanzadorGaleria = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> imagenGaleria = uri }

    LaunchedEffect(Unit) {
        val concedido = ContextCompat.checkSelfPermission(
            contexto, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        if (concedido) {
            estadoPermiso = EstadoPermiso.Concedido
        } else {
            lanzadorPermiso.launch(Manifest.permission.CAMERA)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        when (estadoPermiso) {
            EstadoPermiso.Comprobando -> Text(
                text = "Comprobando el permiso de cámara…",
                modifier = Modifier.padding(16.dp)
            )

            EstadoPermiso.Concedido -> if (hayCamara) {
                CamaraConPermiso(alFoto = { fotoCapturada = it }, modifier = Modifier.weight(1f))
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

        fotoCapturada?.let {
            Imagen(bitmap = it, descripcion = "Foto capturada")
        }
        imagenGaleria?.let {
            ImagenDeGaleria(uri = it, descripcion = "Imagen del selector de fotos")
        }
    }
}

/**
 * Vista previa en vivo con CameraX y botón de disparo. La unión entre la
 * cámara y el ciclo de vida de la pantalla la hace `bindToLifecycle`, que
 * libera la cámara sola cuando la pantalla desaparece.
 */
@Composable
private fun CamaraConPermiso(alFoto: (Bitmap) -> Unit, modifier: Modifier = Modifier) {
    val contexto = LocalContext.current
    val cicloDeVida = LocalLifecycleOwner.current
    val captura = remember { ImageCapture.Builder().build() }
    val previewView = remember { PreviewView(contexto) }

    LaunchedEffect(cicloDeVida) {
        val proveedorFuture = ProcessCameraProvider.getInstance(contexto)
        proveedorFuture.addListener({
            val proveedor = proveedorFuture.get()
            val vistaPrevia = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            proveedor.unbindAll()
            proveedor.bindToLifecycle(
                cicloDeVida,
                CameraSelector.DEFAULT_BACK_CAMERA,
                vistaPrevia,
                captura
            )
        }, ContextCompat.getMainExecutor(contexto))
    }

    Column(modifier = modifier) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxWidth().weight(1f))
        Button(
            onClick = {
                captura.takePicture(
                    ContextCompat.getMainExecutor(contexto),
                    object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: androidx.camera.core.ImageProxy) {
                            val bitmap = image.toBitmap()
                            image.close()
                            alFoto(bitmap)
                        }

                        override fun onError(exception: ImageCaptureException) {
                            exception.printStackTrace()
                        }
                    }
                )
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Hacer foto")
        }
    }
}

/**
 * Caso de error del prototipo: sin permiso (o sin cámara física) se explica
 * qué pasa y se ofrece el selector de fotos del sistema, que no necesita
 * ningún permiso.
 */
@Composable
private fun AlternativaGaleria(
    mensaje: String,
    alElegirImagen: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(text = mensaje, style = MaterialTheme.typography.bodyMedium)
        Button(onClick = alElegirImagen, modifier = Modifier.padding(top = 16.dp)) {
            Text("Elegir de la galería")
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
