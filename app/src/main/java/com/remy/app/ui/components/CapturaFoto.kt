package com.remy.app.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner

/**
 * Estado del permiso de cámara. Se modela aparte para no quedarnos en un
 * limbo mientras el sistema responde: mientras `Comprobando`, no se pinta
 * ni la cámara ni el mensaje de error.
 *
 * Compartido por el prototipo de #25 y por el flujo de cocción de #35.
 */
sealed interface EstadoPermiso {
    data object Comprobando : EstadoPermiso
    data object Concedido : EstadoPermiso
    data object Denegado : EstadoPermiso
}

/**
 * Pide el permiso de cámara en tiempo de ejecución y pasa el resultado a
 * `alCambiar`. Si ya estaba concedido, no vuelve a preguntar.
 */
@Composable
fun PermisoCamara(alCambiar: (EstadoPermiso) -> Unit) {
    val contexto = LocalContext.current
    val lanzador = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        alCambiar(if (concedido) EstadoPermiso.Concedido else EstadoPermiso.Denegado)
    }

    LaunchedEffect(Unit) {
        val concedido = ContextCompat.checkSelfPermission(
            contexto, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        if (concedido) {
            alCambiar(EstadoPermiso.Concedido)
        } else {
            lanzador.launch(Manifest.permission.CAMERA)
        }
    }
}

/**
 * Vista previa en vivo con CameraX y botón de disparo (#25). La unión entre la
 * cámara y el ciclo de vida de la pantalla la hace `bindToLifecycle`, que
 * libera la cámara sola cuando la pantalla desaparece. Si la captura falla,
 * se avisa por `alFallar` y la pantalla sigue funcionando.
 */
@Composable
fun CamaraConPermiso(
    alFoto: (Bitmap) -> Unit,
    modifier: Modifier = Modifier,
    alFallar: (String) -> Unit = {},
) {
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
                        override fun onCaptureSuccess(image: ImageProxy) {
                            val bitmap = image.toBitmap()
                            image.close()
                            alFoto(bitmap)
                        }

                        override fun onError(exception: ImageCaptureException) {
                            exception.printStackTrace()
                            alFallar(
                                "No se ha podido hacer la foto (${exception.message}). " +
                                    "Prueba otra vez o elige una imagen de la galería."
                            )
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
 * Caso de error compartido (#25 y #35): sin permiso (o sin cámara física) se
 * explica qué pasa y se ofrece el selector de fotos del sistema, que no
 * necesita ningún permiso. Así el flujo sigue en marcha aunque falle la foto.
 */
@Composable
fun AlternativaGaleria(
    mensaje: String,
    alElegirImagen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(text = mensaje, style = MaterialTheme.typography.bodyMedium)
        Button(onClick = alElegirImagen, modifier = Modifier.padding(top = 16.dp)) {
            Text("Elegir de la galería")
        }
    }
}
