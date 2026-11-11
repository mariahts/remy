package com.remy.app.ui.screens

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.remy.app.R
import com.remy.app.ui.components.AlternativaGaleria
import com.remy.app.ui.components.CamaraConPermiso
import com.remy.app.ui.components.EstadoPermiso
import com.remy.app.ui.components.OutlineButton
import com.remy.app.ui.components.PermisoCamara
import com.remy.app.ui.components.PrimaryButton
import com.remy.app.ui.theme.Terracota
import com.remy.app.ui.theme.TextoPrincipal
import com.remy.app.ui.theme.TextoSecundario
import com.remy.app.ui.viewmodel.RecetaDetalleViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Formulario para marcar la receta como cocinada: se elige la foto con la
 * cámara o con el selector del sistema, y al confirmar se guarda la fecha y la
 * foto. Si el permiso se deniega o la captura falla, se avisa y se ofrece el
 * selector para que el flujo siga sin romperse.
 */
@Composable
fun CapturaPlato(
    estadoCoccion: RecetaDetalleViewModel.EstadoCoccion,
    alConfirmar: (Bitmap?, Uri?) -> Unit,
    alCancelar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val contexto = LocalContext.current
    var estadoPermiso by remember { mutableStateOf<EstadoPermiso>(EstadoPermiso.Comprobando) }
    var fotoBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var fotoUri by remember { mutableStateOf<Uri?>(null) }
    var mensajeCaptura by remember { mutableStateOf<String?>(null) }

    val hayCamara = remember { contexto.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) }
    val lanzadorGaleria = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            fotoUri = uri
            fotoBitmap = null
            mensajeCaptura = null
        }
    }

    PermisoCamara { estadoPermiso = it }
    val hayFoto = fotoBitmap != null || fotoUri != null

    Column(
        modifier = modifier.padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = stringResource(R.string.detalle_captura_titulo), style = MaterialTheme.typography.headlineSmall, color = TextoPrincipal)
        Text(text = stringResource(R.string.detalle_captura_texto), style = MaterialTheme.typography.bodyMedium, color = TextoSecundario)

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            if (hayFoto) {
                VistaFotoElegida(
                    bitmap = fotoBitmap,
                    uri = fotoUri,
                    onElegirOtra = {
                        fotoBitmap = null
                        fotoUri = null
                        mensajeCaptura = null
                    }
                )
            } else {
                SelectorFuenteFoto(
                    estadoPermiso = estadoPermiso,
                    hayCamara = hayCamara,
                    onFoto = {
                        fotoBitmap = it
                        fotoUri = null
                        mensajeCaptura = null
                    },
                    onFallar = { mensajeCaptura = it },
                    onElegirGaleria = { lanzadorGaleria.launch("image/*") }
                )
            }
        }

        mensajeCaptura?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium) }
        val error = (estadoCoccion as? RecetaDetalleViewModel.EstadoCoccion.Error)?.mensaje
        if (error != null) Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)

        AccionesCaptura(
            hayFoto = hayFoto,
            guardando = estadoCoccion is RecetaDetalleViewModel.EstadoCoccion.Guardando,
            onCancelar = alCancelar,
            onConfirmar = { alConfirmar(fotoBitmap, fotoUri) }
        )
    }
}

@Composable
private fun VistaFotoElegida(bitmap: Bitmap?, uri: Uri?, onElegirOtra: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        FotoElegida(bitmap = bitmap, uri = uri, modifier = Modifier.weight(1f).fillMaxWidth())
        TextButton(onClick = onElegirOtra) { Text(stringResource(R.string.detalle_elegir_otra), color = Terracota) }
    }
}

@Composable
private fun SelectorFuenteFoto(
    estadoPermiso: EstadoPermiso,
    hayCamara: Boolean,
    onFoto: (Bitmap) -> Unit,
    onFallar: (String) -> Unit,
    onElegirGaleria: () -> Unit
) {
    when (estadoPermiso) {
        EstadoPermiso.Comprobando -> Text(text = stringResource(R.string.detalle_comprobando_permiso), color = TextoSecundario)
        EstadoPermiso.Concedido -> if (hayCamara) {
            CamaraConPermiso(alFoto = onFoto, alFallar = onFallar, modifier = Modifier.fillMaxSize())
        } else {
            AlternativaGaleria(
                mensaje = stringResource(R.string.detalle_sin_camara),
                alElegirImagen = onElegirGaleria,
                modifier = Modifier.fillMaxSize()
            )
        }
        EstadoPermiso.Denegado -> AlternativaGaleria(
            mensaje = stringResource(R.string.detalle_sin_permiso),
            alElegirImagen = onElegirGaleria,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun AccionesCaptura(hayFoto: Boolean, guardando: Boolean, onCancelar: () -> Unit, onConfirmar: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlineButton(text = stringResource(R.string.detalle_cancelar), onClick = onCancelar, modifier = Modifier.weight(1f))
        PrimaryButton(
            text = stringResource(if (hayFoto) R.string.detalle_confirmar else R.string.detalle_confirmar_sin_foto),
            onClick = onConfirmar,
            modifier = Modifier.weight(1f),
            enabled = !guardando
        )
    }
}

/** Foto ya elegida: la de la cámara en bitmap o la del selector por su uri. */
@Composable
private fun FotoElegida(bitmap: Bitmap?, uri: Uri?, modifier: Modifier = Modifier) {
    when {
        bitmap != null -> Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = stringResource(R.string.detalle_foto_plato),
            modifier = modifier,
            contentScale = ContentScale.Fit
        )
        uri != null -> FotoDeGaleria(uri = uri, modifier = modifier)
        else -> Text(stringResource(R.string.detalle_elige_foto), modifier = modifier)
    }
}

/** Carga la imagen del selector de fotos; si no se puede, avisa sin romper la pantalla. */
@Composable
private fun FotoDeGaleria(uri: Uri, modifier: Modifier = Modifier) {
    val contexto = LocalContext.current
    var bitmap by remember(uri) { mutableStateOf<Bitmap?>(null) }
    var fallo by remember(uri) { mutableStateOf(false) }

    LaunchedEffect(uri) {
        val resultado = withContext(Dispatchers.IO) {
            contexto.contentResolver.openInputStream(uri)?.use { entrada ->
                BitmapFactory.decodeStream(entrada)
            }
        }
        if (resultado == null) fallo = true else bitmap = resultado
    }

    val bmp = bitmap
    when {
        bmp != null -> Image(
            bitmap = bmp.asImageBitmap(),
            contentDescription = stringResource(R.string.detalle_foto_plato),
            modifier = modifier,
            contentScale = ContentScale.Fit
        )
        fallo -> Text(
            text = stringResource(R.string.detalle_foto_error),
            color = MaterialTheme.colorScheme.error,
            modifier = modifier
        )
        else -> Box(modifier = modifier, contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Terracota)
        }
    }
}
