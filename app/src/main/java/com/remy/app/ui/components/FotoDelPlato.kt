package com.remy.app.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.remy.app.ui.theme.Slate500
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Foto guardada en el almacenamiento interno; si falta, muestra un marcador de reserva. */
@Composable
fun FotoDelPlato(
    ruta: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit
) {
    var bitmap by remember(ruta) { mutableStateOf<Bitmap?>(null) }
    var fallo by remember(ruta) { mutableStateOf(false) }

    LaunchedEffect(ruta) {
        if (ruta == null) {
            fallo = true
        } else {
            val resultado = withContext(Dispatchers.IO) { BitmapFactory.decodeFile(ruta) }
            if (resultado == null) fallo = true else bitmap = resultado
        }
    }

    val bmp = bitmap
    when {
        bmp != null -> Image(
            bitmap = bmp.asImageBitmap(),
            contentDescription = "Foto del plato cocinado",
            modifier = modifier,
            contentScale = contentScale
        )

        fallo -> Text(
            if (ruta == null) "Sin foto del plato." else "No se ha podido cargar la foto del plato.",
            style = MaterialTheme.typography.bodySmall,
            color = Slate500,
            modifier = modifier.padding(8.dp)
        )

        else -> Text(
            "Cargando la foto del plato…",
            style = MaterialTheme.typography.bodySmall,
            color = Slate500,
            modifier = modifier.padding(8.dp)
        )
    }
}
