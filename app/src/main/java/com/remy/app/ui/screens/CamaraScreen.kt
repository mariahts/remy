package com.remy.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil3.compose.AsyncImage
import com.remy.app.R
import com.remy.app.ui.viewmodel.CamaraViewModel
import java.io.File
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
 * Prototipo de la función multimedia de la app (#25 y #26): pide el permiso
 * de cámara, muestra la vista previa y captura una foto con CameraX. En
 * paralelo permite elegir una imagen con el selector de fotos del sistema:
 * la imagen se copia al almacenamiento interno, su ruta se guarda en
 * preferencias y se vuelve a mostrar con Coil al reabrir la app (#26).
 * Si el permiso se deniega (o el dispositivo no tiene cámara), se ofrece el
 * selector de fotos como alternativa.
 */
@Composable
fun CamaraScreen(
    camara: CamaraViewModel = viewModel(factory = CamaraViewModel.Factory)
) {
    val contexto = LocalContext.current
    var estadoPermiso by remember { mutableStateOf<EstadoPermiso>(EstadoPermiso.Comprobando) }
    var fotoCapturada by remember { mutableStateOf<Bitmap?>(null) }
    val rutaImagenElegida by camara.rutaImagenElegida.collectAsStateWithLifecycle()

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
    ) { uri ->
        if (uri != null) camara.guardarImagenElegida(uri)
    }

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
                CamaraConPermiso(
                    alFoto = { fotoCapturada = it },
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

        fotoCapturada?.let {
            Imagen(bitmap = it, descripcion = "Foto capturada")
        }
        rutaImagenElegida?.let { ruta ->
            ImagenGuardada(ruta = ruta, descripcion = "Imagen elegida en el selector de fotos")
        }
    }
}

/**
 * Vista previa en vivo con CameraX y botón de disparo. La unión entre la
 * cámara y el ciclo de vida de la pantalla la hace `bindToLifecycle`, que
 * libera la cámara sola cuando la pantalla desaparece. Se añade también un
 * acceso directo al selector de fotos (#26), que no necesita permisos.
 */
@Composable
private fun CamaraConPermiso(
    alFoto: (Bitmap) -> Unit,
    alElegirImagen: () -> Unit,
    modifier: Modifier = Modifier
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
        Button(onClick = alElegirImagen, modifier = Modifier.padding(bottom = 16.dp)) {
            Text("Elegir imagen de la galería")
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
