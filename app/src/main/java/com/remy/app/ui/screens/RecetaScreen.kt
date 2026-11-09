package com.remy.app.ui.screens

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.remy.app.data.Receta
import com.remy.app.data.RecetaConIngredientes
import com.remy.app.data.RecetaDetalle
import com.remy.app.ui.components.AlternativaGaleria
import com.remy.app.ui.components.CamaraConPermiso
import com.remy.app.ui.components.EstadoPermiso
import com.remy.app.ui.components.PermisoCamara
import com.remy.app.ui.components.fechaEnEspanol
import com.remy.app.ui.components.formatoCantidad
import com.remy.app.ui.viewmodel.RecetaDetalleViewModel
import com.remy.app.ui.viewmodel.RecetasViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun RecetasScreen(
    onRecetaClick: (Long) -> Unit,
    viewModel: RecetasViewModel = viewModel(factory = RecetasViewModel.Factory),
) {
    val recetas by viewModel.recetas.collectAsStateWithLifecycle()

    if (recetas.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Aún no hay recetas", style = MaterialTheme.typography.titleMedium)
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(recetas, key = { it.receta.id }) { item ->
                TarjetaReceta(item) { onRecetaClick(item.receta.id) }
            }
        }
    }
}

@Composable
private fun TarjetaReceta(item: RecetaConIngredientes, onClick: () -> Unit) {
    val receta = item.receta
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp).clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(receta.nombre, style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("${receta.tiempoMinutos} min", style = MaterialTheme.typography.bodyMedium)
                Text("·", style = MaterialTheme.typography.bodyMedium)
                Text(receta.tipoPlato, style = MaterialTheme.typography.bodyMedium)
            }
            receta.fechaCocinada?.let {
                Text(
                    "Cocinada el ${fechaEnEspanol(it)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/** Detalle de receta: ingredientes, pasos, tiempo, nutrición y cocción (#35). */
@Composable
fun RecetaDetalleScreen(
    id: Long,
    onVolver: () -> Unit,
    viewModel: RecetaDetalleViewModel = viewModel(factory = RecetaDetalleViewModel.Factory),
) {
    LaunchedEffect(id) { viewModel.cargar(id) }
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val estadoCoccion by viewModel.estadoCoccion.collectAsStateWithLifecycle()
    var capturando by remember { mutableStateOf(false) }

    // Al confirmar, el registro ya está guardado: se vuelve al detalle para
    // ver la receta con su fecha y su foto.
    LaunchedEffect(estadoCoccion) {
        if (estadoCoccion is RecetaDetalleViewModel.EstadoCoccion.Guardado) {
            capturando = false
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TextButton(onClick = onVolver, modifier = Modifier.padding(start = 8.dp, top = 8.dp)) {
            Text("← Volver al listado")
        }

        when (val actual = estado) {
            RecetaDetalleViewModel.Estado.Cargando -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Cargando…", style = MaterialTheme.typography.bodyLarge)
                }
            }

            RecetaDetalleViewModel.Estado.NoEncontrada -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se encontró esta receta", style = MaterialTheme.typography.titleMedium)
                }
            }

            is RecetaDetalleViewModel.Estado.Encontrada -> if (capturando) {
                CapturaPlato(
                    estadoCoccion = estadoCoccion,
                    alConfirmar = { foto, uri -> viewModel.marcarComoCocinada(id, foto, uri) },
                    alCancelar = {
                        capturando = false
                        viewModel.limpiarCoccion()
                    },
                    modifier = Modifier.weight(1f)
                )
            } else {
                ContenidoDetalle(
                    detalle = actual.detalle,
                    estadoCoccion = estadoCoccion,
                    alEmpezarCoccion = { capturando = true },
                    alLimpiarAviso = { viewModel.limpiarCoccion() },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * Formulario para marcar la receta como cocinada: se elige la foto con la
 * cámara (#25) o con el selector del sistema, y al confirmar se guarda la
 * fecha y la foto. Si el permiso se deniega o la captura falla, se avisa y
 * se ofrece el selector para que el flujo siga sin romperse.
 */
@Composable
private fun CapturaPlato(
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

    val hayCamara = remember {
        contexto.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

    val lanzadorGaleria = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            fotoUri = uri
            fotoBitmap = null
            mensajeCaptura = null
        }
    }

    PermisoCamara { estadoPermiso = it }

    val hayFoto = fotoBitmap != null || fotoUri != null

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Marcar como cocinada", style = MaterialTheme.typography.titleMedium)
        Text(
            "Haz o elige una foto del plato. Al confirmar se guarda la fecha de hoy " +
                "junto a la receta y a la fotografía.",
            style = MaterialTheme.typography.bodyMedium
        )

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            if (hayFoto) {
                Column(modifier = Modifier.fillMaxSize()) {
                    FotoElegida(
                        bitmap = fotoBitmap,
                        uri = fotoUri,
                        modifier = Modifier.weight(1f).fillMaxWidth()
                    )
                    TextButton(onClick = {
                        fotoBitmap = null
                        fotoUri = null
                        mensajeCaptura = null
                    }) {
                        Text("Elegir otra foto")
                    }
                }
            } else {
                when (estadoPermiso) {
                    EstadoPermiso.Comprobando -> Text("Comprobando el permiso de cámara…")

                    EstadoPermiso.Concedido -> if (hayCamara) {
                        CamaraConPermiso(
                            alFoto = {
                                fotoBitmap = it
                                fotoUri = null
                                mensajeCaptura = null
                            },
                            alFallar = { mensajeCaptura = it },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        AlternativaGaleria(
                            mensaje = "Este dispositivo no tiene cámara. Elige una imagen de tu galería.",
                            alElegirImagen = { lanzadorGaleria.launch("image/*") },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    EstadoPermiso.Denegado -> AlternativaGaleria(
                        mensaje = "Sin permiso de cámara no se pueden hacer fotos. " +
                            "Puedes conceder el permiso en Ajustes o usar una imagen de tu galería.",
                        alElegirImagen = { lanzadorGaleria.launch("image/*") },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        mensajeCaptura?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
        }

        when (val coccion = estadoCoccion) {
            is RecetaDetalleViewModel.EstadoCoccion.Error -> Text(
                coccion.mensaje,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )

            else -> Unit
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = alCancelar) {
                Text("Cancelar")
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { alConfirmar(fotoBitmap, fotoUri) },
                enabled = estadoCoccion !is RecetaDetalleViewModel.EstadoCoccion.Guardando
            ) {
                Text(if (hayFoto) "Confirmar" else "Confirmar sin foto")
            }
        }
    }
}

/** Foto ya elegida: la de la cámara en bitmap o la del selector por su uri. */
@Composable
private fun FotoElegida(bitmap: Bitmap?, uri: Uri?, modifier: Modifier = Modifier) {
    when {
        bitmap != null -> Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Foto elegida del plato",
            modifier = modifier,
            contentScale = ContentScale.Fit
        )

        uri != null -> FotoDeGaleria(uri = uri, modifier = modifier)

        else -> Text("Elige una foto del plato para continuar.", modifier = modifier)
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

    when {
        bitmap != null -> Image(
            bitmap = bitmap!!.asImageBitmap(),
            contentDescription = "Foto elegida del plato",
            modifier = modifier,
            contentScale = ContentScale.Fit
        )

        fallo -> Text(
            "No se ha podido cargar la imagen. Elige otra.",
            modifier = modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.error
        )

        else -> Text("Cargando la imagen…", modifier = modifier.padding(16.dp))
    }
}

/** Contenido del detalle: receta, cocción y avisos, con desplazamiento vertical. */
@Composable
private fun ContenidoDetalle(
    detalle: RecetaDetalle,
    estadoCoccion: RecetaDetalleViewModel.EstadoCoccion,
    alEmpezarCoccion: () -> Unit,
    alLimpiarAviso: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val receta = detalle.receta
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(receta.nombre, style = MaterialTheme.typography.headlineSmall)
        Text(
            "${receta.tiempoMinutos} min · ${receta.tipoPlato} · ${receta.regionOrigen}",
            style = MaterialTheme.typography.bodyMedium
        )

        Text("Nutrición", style = MaterialTheme.typography.titleMedium)
        Text(
            "${receta.kcal} kcal · ${receta.proteinas} g de proteínas",
            style = MaterialTheme.typography.bodyLarge
        )

        Text("Ingredientes", style = MaterialTheme.typography.titleMedium)
        if (detalle.ingredientes.isEmpty()) {
            Text("Sin ingredientes registrados", style = MaterialTheme.typography.bodyMedium)
        } else {
            detalle.ingredientes.forEach { item ->
                val alimento = item.alimento?.nombre ?: "Alimento ${item.ingrediente.alimentoId}"
                Text(
                    "· ${item.ingrediente.cantidad.formatoCantidad()} ${item.ingrediente.unidad} de $alimento",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Text("Pasos", style = MaterialTheme.typography.titleMedium)
        if (receta.pasos.isEmpty()) {
            Text("Sin pasos registrados", style = MaterialTheme.typography.bodyMedium)
        } else {
            receta.pasos.forEachIndexed { indice, paso ->
                Text("${indice + 1}. $paso", style = MaterialTheme.typography.bodyMedium)
            }
        }

        Text("Cocción", style = MaterialTheme.typography.titleMedium)
        if (receta.fechaCocinada != null) {
            TarjetaCocinada(receta = receta)
        } else {
            Text("Todavía no has cocinado esta receta.", style = MaterialTheme.typography.bodyMedium)
            Button(onClick = alEmpezarCoccion, modifier = Modifier.fillMaxWidth()) {
                Text("Marcar como cocinada")
            }
        }

        when (val coccion = estadoCoccion) {
            is RecetaDetalleViewModel.EstadoCoccion.Guardado -> coccion.aviso?.let { aviso ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        aviso,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    TextButton(onClick = alLimpiarAviso) {
                        Text("Entendido")
                    }
                }
            }

            else -> Unit
        }
    }
}

/** Receta ya cocinada: muestra la fecha y la foto del plato (#35). */
@Composable
private fun TarjetaCocinada(receta: Receta) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Cocinada el ${fechaEnEspanol(receta.fechaCocinada ?: 0L)}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            FotoDelPlato(ruta = receta.fotoCocinada)
        }
    }
}

/** Foto guardada en el almacenamiento interno; si falta, muestra un marcador de reserva. */
@Composable
private fun FotoDelPlato(ruta: String?, modifier: Modifier = Modifier) {
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

    when {
        bitmap != null -> Image(
            bitmap = bitmap!!.asImageBitmap(),
            contentDescription = "Foto del plato cocinado",
            modifier = modifier.fillMaxWidth().heightIn(max = 220.dp),
            contentScale = ContentScale.Fit
        )

        fallo -> Text(
            if (ruta == null) "Sin foto del plato." else "No se ha podido cargar la foto del plato.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        else -> Text("Cargando la foto del plato…", style = MaterialTheme.typography.bodySmall)
    }
}
