package com.remy.app.ui.screens

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.SentimentSatisfied
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.remy.app.R
import com.remy.app.data.Alimento
import com.remy.app.data.Ingrediente
import com.remy.app.data.IngredienteConAlimento
import com.remy.app.data.Receta
import com.remy.app.data.PreferenciasRepository
import com.remy.app.data.RecetaConIngredientes
import com.remy.app.data.RecetaDetalle
import com.remy.app.ui.components.AlternativaGaleria
import com.remy.app.ui.components.CamaraConPermiso
import com.remy.app.ui.components.ContenidoConEstado
import com.remy.app.ui.components.EmojiCircle
import com.remy.app.ui.components.EstadoPermiso
import com.remy.app.ui.components.FotoReceta
import com.remy.app.ui.components.IngredientIcon
import com.remy.app.ui.components.MetaPill
import com.remy.app.ui.components.OutlineButton
import com.remy.app.ui.components.PermisoCamara
import com.remy.app.ui.components.PersonCircle
import com.remy.app.ui.components.PrimaryButton
import com.remy.app.ui.components.RemyCta
import com.remy.app.ui.components.RemyHeaderSombra
import com.remy.app.ui.components.SearchPill
import com.remy.app.ui.components.SectionCard
import com.remy.app.ui.components.StatusBadge
import com.remy.app.ui.components.VarianteBadge
import com.remy.app.ui.components.categoriaDe
import com.remy.app.ui.components.emojiDe
import com.remy.app.ui.components.FotoDelPlato
import com.remy.app.ui.components.RecipePhoto
import com.remy.app.ui.components.RemyCard
import com.remy.app.ui.components.RemyChip
import com.remy.app.ui.components.RemySearchField
import com.remy.app.ui.components.RemyTopBar
import com.remy.app.ui.components.fechaEnEspanol
import com.remy.app.ui.components.formatoCantidad
import com.remy.app.ui.theme.Amber100
import com.remy.app.ui.theme.Amber600
import com.remy.app.ui.theme.AvisoNeutroBg
import com.remy.app.ui.theme.AzulTinta
import com.remy.app.ui.theme.BordeTarjeta
import com.remy.app.ui.theme.DetailDarkPill
import com.remy.app.ui.theme.DetailGreen900
import com.remy.app.ui.theme.DetailGreen950
import com.remy.app.ui.theme.DetailMint
import com.remy.app.ui.theme.DetailOrange700
import com.remy.app.ui.theme.DetailOrange900
import com.remy.app.ui.theme.Emerald50
import com.remy.app.ui.theme.FondoCrema
import com.remy.app.ui.theme.Green050
import com.remy.app.ui.theme.Green100
import com.remy.app.ui.theme.Green200
import com.remy.app.ui.theme.Green400
import com.remy.app.ui.theme.Green500
import com.remy.app.ui.theme.Green600
import com.remy.app.ui.theme.Green900
import com.remy.app.ui.theme.PillShape
import com.remy.app.ui.theme.Red400
import com.remy.app.ui.theme.RemyShapes
import com.remy.app.ui.theme.Slate100
import com.remy.app.ui.theme.Slate200
import com.remy.app.ui.theme.Slate300
import com.remy.app.ui.theme.Slate400
import com.remy.app.ui.theme.Slate500
import com.remy.app.ui.theme.Slate600
import com.remy.app.ui.theme.Slate700
import com.remy.app.ui.theme.Slate900
import com.remy.app.ui.theme.SuperficieBlanca
import com.remy.app.ui.theme.REMYTheme
import com.remy.app.ui.theme.Terracota
import com.remy.app.ui.theme.TextoPrincipal
import com.remy.app.ui.theme.TextoSecundario
import com.remy.app.ui.theme.Terracota100
import com.remy.app.ui.viewmodel.RecetaDetalleViewModel
import com.remy.app.ui.viewmodel.RecetasViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun RecetasScreen(
    onRecetaClick: (Long) -> Unit,
    onPerfilClick: () -> Unit = {},
    viewModel: RecetasViewModel = viewModel(factory = RecetasViewModel.Factory),
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val preferencias = remember { PreferenciasRepository.delContexto(context) }
    val favoritas by preferencias.favoritas.collectAsStateWithLifecycle(initialValue = emptySet())
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().background(FondoCrema)) {
        RemyTopBar(
            titulo = stringResource(R.string.recetas_titulo),
            onAvatarClick = onPerfilClick
        )
        ContenidoConEstado(
            estado = estado,
            onReintentar = viewModel::reintentar,
            mensajeCargando = stringResource(R.string.recetas_cargando)
        ) { recetas ->
            ContenidoRecetas(
                recetas = recetas,
                favoritas = favoritas,
                onToggleFavorita = { id -> scope.launch { preferencias.alternarFavorita(id) } },
                onRecetaClick = onRecetaClick
            )
        }
    }
}

private enum class FiltroRecetas { TODAS, RAPIDAS, FAVORITAS, COCINADAS }

private fun etiquetaFiltro(filtro: FiltroRecetas): Int = when (filtro) {
    FiltroRecetas.TODAS -> R.string.recetas_todas
    FiltroRecetas.RAPIDAS -> R.string.recetas_rapidas
    FiltroRecetas.FAVORITAS -> R.string.recetas_favoritas
    FiltroRecetas.COCINADAS -> R.string.recetas_cocinadas
}

@Composable
private fun ContenidoRecetas(
    recetas: List<RecetaConIngredientes>,
    favoritas: Set<Long>,
    onToggleFavorita: (Long) -> Unit,
    onRecetaClick: (Long) -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    var filtro by rememberSaveable { mutableStateOf(FiltroRecetas.TODAS) }

    val filtradas = remember(recetas, query, filtro, favoritas) {
        recetas.filter { item ->
            val coincideNombre = query.isBlank() ||
                item.receta.nombre.contains(query.trim(), ignoreCase = true)
            val coincideFiltro = when (filtro) {
                FiltroRecetas.TODAS -> true
                FiltroRecetas.RAPIDAS -> item.receta.tiempoMinutos in 1..30
                FiltroRecetas.FAVORITAS -> item.receta.id in favoritas
                FiltroRecetas.COCINADAS -> item.receta.fechaCocinada != null
            }
            coincideNombre && coincideFiltro
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RemySearchField(
                value = query,
                onValueChange = { query = it },
                placeholder = stringResource(R.string.recetas_buscar)
            )
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FiltroRecetas.entries.forEach { opcion ->
                    RemyChip(
                        text = stringResource(etiquetaFiltro(opcion)),
                        selected = filtro == opcion,
                        onClick = { filtro = opcion }
                    )
                }
            }
        }

        if (filtradas.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Outlined.MenuBook,
                    contentDescription = null,
                    tint = TextoSecundario,
                    modifier = Modifier.size(40.dp)
                )
                Text(
                    text = when {
                        recetas.isEmpty() -> stringResource(R.string.recetas_vacia)
                        query.isNotBlank() -> stringResource(R.string.recetas_sin_resultados, query.trim())
                        else -> stringResource(R.string.recetas_sin_resultados_filtro)
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextoSecundario,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 96.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtradas, key = { it.receta.id }) { item ->
                    TarjetaRecetaGrid(
                        item = item,
                        esFavorita = item.receta.id in favoritas,
                        onToggleFavorita = { onToggleFavorita(item.receta.id) },
                        onClick = { onRecetaClick(item.receta.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaRecetaGrid(
    item: RecetaConIngredientes,
    esFavorita: Boolean,
    onToggleFavorita: () -> Unit,
    onClick: () -> Unit
) {
    val receta = item.receta

    val porciones = receta.porciones.coerceAtLeast(1)
    val kcalPorRacion = kotlin.math.round(receta.kcal.toFloat() / porciones).toInt()
    val proteinasPorRacion = kotlin.math.round(receta.proteinas.toFloat() / porciones).toInt()

    RemyCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        padding = 0.dp
    ) {
        Box(modifier = Modifier.fillMaxWidth().aspectRatio(4f / 3f)) {
            RecipePhoto(
                model = receta.fotoCocinada,
                nombre = receta.nombre,
                modifier = Modifier.fillMaxSize()
            )
            if (receta.tiempoMinutos > 0) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .clip(PillShape)
                        .background(Color.White.copy(alpha = 0.92f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = TextoPrincipal,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        stringResource(R.string.comun_minutos, receta.tiempoMinutos),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextoPrincipal
                    )
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.92f))
                    .clickable(onClick = onToggleFavorita),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (esFavorita) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = stringResource(
                        if (esFavorita) R.string.recetas_quitar else R.string.recetas_guardar
                    ),
                    tint = if (esFavorita) Terracota else TextoSecundario,
                    modifier = Modifier.size(19.dp)
                )
            }
        }
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                receta.nombre,
                style = MaterialTheme.typography.titleSmall,
                color = TextoPrincipal,
                maxLines = 2,
                minLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                stringResource(R.string.recetas_kcal_proteina, kcalPorRacion, proteinasPorRacion),
                style = MaterialTheme.typography.bodySmall,
                color = TextoSecundario,
                maxLines = 2
            )
            receta.fechaCocinada?.let { fecha ->
                Text(
                    stringResource(R.string.recetas_cocinada_el, fechaEnEspanol(fecha)),
                    style = MaterialTheme.typography.labelSmall,
                    color = Terracota,
                    maxLines = 1
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFBF5)
@Composable
private fun RecetasScreenPreview() {
    REMYTheme {
        ContenidoRecetas(
            recetas = listOf(
                RecetaConIngredientes(
                    receta = Receta(id = 1, nombre = "Arroz con verduras", kcal = 420, proteinas = 14, regionOrigen = "MediterrÃ¡nea", tipoPlato = "Principal", tiempoMinutos = 25, porciones = 4),
                    ingredientes = emptyList()
                ),
                RecetaConIngredientes(
                    receta = Receta(id = 2, nombre = "Tortilla de patatas", kcal = 380, proteinas = 22, regionOrigen = "EspaÃ±a", tipoPlato = "Principal", tiempoMinutos = 40, porciones = 4),
                    ingredientes = emptyList()
                )
            ),
            favoritas = setOf(1),
            onToggleFavorita = {},
            onRecetaClick = {}
        )
    }
}

@Composable
private fun PillContador(n: Int) {
    Row(
        modifier = Modifier
            .shadow(2.dp, PillShape, ambientColor = Color.Black.copy(alpha = 0.04f), spotColor = Color.Black.copy(alpha = 0.06f))
            .clip(PillShape)
            .background(Green100)
            .border(1.dp, Green200, PillShape)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "$n ${if (n == 1) "receta guardada" else "recetas guardadas"}",
            style = MaterialTheme.typography.labelMedium,
            color = Green900
        )
    }
}

@Composable
private fun PieRecetas() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Filled.Verified, contentDescription = null, tint = Green600, modifier = Modifier.size(24.dp))
        Text(
            "Tus recetas se sincronizan automÃ¡ticamente con tu despensa REMY",
            style = MaterialTheme.typography.labelSmall,
            color = Slate600.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp).width(260.dp)
        )
    }
}

/** Chip blanco con punto de color de categorÃ­a (chips de ingredientes Â§2.7). */
@Composable
private fun ChipIngrediente(nombre: String) {
    val cat = categoriaDe(nombre)
    Row(
        modifier = Modifier
            .clip(PillShape)
            .background(Color.White)
            .border(1.dp, Slate400.copy(alpha = 0.3f), PillShape)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier.size(16.dp).clip(CircleShape).background(cat.colors.bg),
            contentAlignment = Alignment.Center
        ) {
            Box(Modifier.size(7.dp).clip(CircleShape).background(cat.colors.fg))
        }
        Text(nombre, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Slate600, maxLines = 1)
    }
}

@Composable
private fun TarjetaReceta(
    item: RecetaConIngredientes,
    esFavorita: Boolean = false,
    onToggleFavorita: () -> Unit = {},
    onClick: () -> Unit
) {
    val receta = item.receta

    SectionCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RemyShapes.extraLarge,
        border = null,
        glow = false,
        padding = PaddingValues(0.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(176.dp)) {
            // Si la receta se cocinÃ³ y tiene foto, mostrarla; si no, placeholder.
            if (receta.fotoCocinada != null) {
                FotoDelPlato(
                    ruta = receta.fotoCocinada,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                FotoReceta(receta.nombre, Modifier.fillMaxSize())
            }
            if (receta.tiempoMinutos > 0) {
                MetaPill(
                    text = "${receta.tiempoMinutos} min",
                    modifier = Modifier.align(Alignment.TopStart).padding(12.dp),
                    icon = Icons.Outlined.Schedule,
                    tint = Green600,
                    bg = Color.White.copy(alpha = 0.92f),
                    fg = Slate900
                )
            }
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.92f))
                    .clickable { onToggleFavorita() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Bookmark,
                    contentDescription = "Guardar receta",
                    tint = if (esFavorita) Green600 else Slate500,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(receta.nombre, style = MaterialTheme.typography.titleLarge, color = Slate900)
            receta.fechaCocinada?.let {
                Text(
                    "Cocinada el ${fechaEnEspanol(it)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Green600,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (receta.kcal > 0 || receta.proteinas > 0) {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (receta.kcal > 0) {
                        MetaPill("${receta.kcal} kcal", icon = Icons.Outlined.LocalFireDepartment, tint = Amber600, bg = Slate100, fg = Slate900)
                    }
                    if (receta.proteinas > 0) {
                        MetaPill("${receta.proteinas}g ProteÃ­na", icon = Icons.Outlined.FitnessCenter, tint = Green600, bg = Slate100, fg = Slate900)
                    }
                }
            }

            val nombres = item.ingredientes.mapNotNull { it.alimento?.nombre }.distinct().take(4)
            if (nombres.isNotEmpty()) {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    nombres.forEach { ChipIngrediente(it) }
                }
            }

            RemyCta(
                text = "Ver receta",
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                height = 44.dp,
                iconAfter = Icons.Filled.ArrowForward,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun RecetaDetalleScreen(
    id: Long,
    onVolver: () -> Unit,
    onPerfilClick: () -> Unit = {},
    viewModel: RecetaDetalleViewModel = viewModel(factory = RecetaDetalleViewModel.Factory),
) {
    LaunchedEffect(id) { viewModel.cargar(id) }
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val estadoCoccion by viewModel.estadoCoccion.collectAsStateWithLifecycle()
    var capturando by remember { mutableStateOf(false) }

    // Al confirmar, el registro ya estÃ¡ guardado: se vuelve al detalle para
    // ver la receta con su fecha y su foto.
    LaunchedEffect(estadoCoccion) {
        if (estadoCoccion is RecetaDetalleViewModel.EstadoCoccion.Guardado) {
            capturando = false
        }
    }

    when (val actual = estado) {
        RecetaDetalleViewModel.Estado.Cargando -> {
            Box(modifier = Modifier.fillMaxSize().background(FondoCrema), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Terracota, strokeWidth = 4.dp)
            }
        }

        RecetaDetalleViewModel.Estado.NoEncontrada -> {
            Column(
                modifier = Modifier.fillMaxSize().background(FondoCrema).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.detalle_no_encontrada),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextoPrincipal,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                OutlineButton(
                    text = stringResource(R.string.detalle_volver_listado),
                    onClick = onVolver,
                    icon = Icons.AutoMirrored.Outlined.ArrowBack
                )
            }
        }

        is RecetaDetalleViewModel.Estado.Encontrada -> if (capturando) {
            Column(modifier = Modifier.fillMaxSize().background(FondoCrema)) {
                RemyTopBar(
                    titulo = stringResource(R.string.detalle_marcar_cocinada),
                    onVolver = {
                        capturando = false
                        viewModel.limpiarCoccion()
                    }
                )
                CapturaPlato(
                    estadoCoccion = estadoCoccion,
                    alConfirmar = { foto, uri -> viewModel.marcarComoCocinada(id, foto, uri) },
                    alCancelar = {
                        capturando = false
                        viewModel.limpiarCoccion()
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            DetalleReceta(
                detalle = actual.detalle,
                estadoCoccion = estadoCoccion,
                onVolver = onVolver,
                onEmpezarCoccion = { capturando = true },
                onLimpiarAviso = { viewModel.limpiarCoccion() }
            )
        }
    }
}

/** Envuelve el detalle con las favoritas de DataStore. */
@Composable
private fun DetalleReceta(
    detalle: RecetaDetalle,
    estadoCoccion: RecetaDetalleViewModel.EstadoCoccion,
    onVolver: () -> Unit,
    onEmpezarCoccion: () -> Unit,
    onLimpiarAviso: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val preferencias = remember { PreferenciasRepository.delContexto(context) }
    val favoritas by preferencias.favoritas.collectAsStateWithLifecycle(initialValue = emptySet())
    val esFavorita = detalle.receta.id in favoritas

    ContenidoDetalle(
        detalle = detalle,
        esFavorita = esFavorita,
        onToggleFavorita = { scope.launch { preferencias.alternarFavorita(detalle.receta.id) } },
        estadoCoccion = estadoCoccion,
        onVolver = onVolver,
        onEmpezarCoccion = onEmpezarCoccion,
        onLimpiarAviso = onLimpiarAviso
    )
}

@Composable
private fun ContenidoDetalle(
    detalle: RecetaDetalle,
    esFavorita: Boolean,
    onToggleFavorita: () -> Unit,
    estadoCoccion: RecetaDetalleViewModel.EstadoCoccion,
    onVolver: () -> Unit,
    onEmpezarCoccion: () -> Unit,
    onLimpiarAviso: () -> Unit
) {
    val receta = detalle.receta
    val porciones = receta.porciones.coerceAtLeast(1)
    val total = detalle.ingredientes.size
    val disponibles = detalle.ingredientes.count { it.alimento != null }
    val faltan = (total - disponibles).coerceAtLeast(0)

    Scaffold(
        containerColor = FondoCrema,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            ColumnaAccionDetalle(
                yaCocinada = receta.fechaCocinada != null,
                estadoCoccion = estadoCoccion,
                onEmpezarCoccion = onEmpezarCoccion,
                onLimpiarAviso = onLimpiarAviso
            )
        }
    ) { pad ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = pad.calculateBottomPadding() + 24.dp)
        ) {
            item {
                HeroDetalle(
                    receta = receta,
                    esFavorita = esFavorita,
                    onToggleFavorita = onToggleFavorita,
                    onVolver = onVolver
                )
            }
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = receta.nombre,
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextoPrincipal
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DatoPildora(
                            Icons.Outlined.People,
                            if (porciones == 1) {
                                stringResource(R.string.comun_una_racion)
                            } else {
                                stringResource(R.string.comun_raciones, porciones)
                            }
                        )
                        if (receta.tiempoMinutos > 0) {
                            DatoPildora(Icons.Outlined.Schedule, stringResource(R.string.comun_minutos, receta.tiempoMinutos))
                        }
                        if (receta.kcal > 0) {
                            DatoPildora(
                                Icons.Outlined.LocalFireDepartment,
                                stringResource(R.string.comun_kcal_racion, kotlin.math.round(receta.kcal.toFloat() / porciones).toInt())
                            )
                        }
                        if (receta.proteinas > 0) {
                            DatoPildora(
                                Icons.Outlined.FitnessCenter,
                                stringResource(R.string.detalle_proteina, kotlin.math.round(receta.proteinas.toFloat() / porciones).toInt())
                            )
                        }
                        if (receta.tipoPlato.isNotBlank()) {
                            DatoPildora(Icons.Outlined.Restaurant, receta.tipoPlato)
                        }
                        if (receta.regionOrigen.isNotBlank()) {
                            DatoPildora(Icons.Outlined.Public, receta.regionOrigen)
                        }
                    }
                }
            }
            item {
                RemyCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.detalle_ingredientes),
                            style = MaterialTheme.typography.titleLarge,
                            color = TextoPrincipal
                        )
                        if (faltan == 0 && total > 0) {
                            StatusBadge(
                                text = stringResource(R.string.sugerencias_badge_lista),
                                variante = VarianteBadge.LISTA
                            )
                        } else if (faltan > 0) {
                            StatusBadge(
                                text = stringResource(R.string.sugerencias_badge_faltan, faltan),
                                variante = VarianteBadge.FALTAN
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    detalle.ingredientes.forEachIndexed { indice, par ->
                        if (indice > 0) {
                            HorizontalDivider(color = BordeTarjeta, thickness = 1.dp)
                        }
                        FilaIngredienteDetalle(par = par, porciones = porciones)
                    }
                    if (total == 0) {
                        Text(
                            text = stringResource(R.string.detalle_sin_ingredientes),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextoSecundario
                        )
                    }
                }
            }
            if (receta.pasos.isNotEmpty()) {
                item {
                    RemyCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)) {
                        Text(
                            text = stringResource(R.string.detalle_pasos),
                            style = MaterialTheme.typography.titleLarge,
                            color = TextoPrincipal
                        )
                        Spacer(Modifier.height(4.dp))
                        receta.pasos.forEachIndexed { indice, paso ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(AzulTinta),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${indice + 1}",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = Color.White
                                    )
                                }
                                Text(
                                    text = paso,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextoSecundario,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
            if (receta.fechaCocinada != null) {
                item {
                    RemyCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Restaurant,
                                contentDescription = null,
                                tint = Terracota,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = stringResource(R.string.detalle_cocinada_el, fechaEnEspanol(receta.fechaCocinada!!)),
                                style = MaterialTheme.typography.titleMedium,
                                color = TextoPrincipal
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        FotoDelPlato(
                            ruta = receta.fotoCocinada,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 220.dp)
                                .clip(RemyShapes.small),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroDetalle(
    receta: Receta,
    esFavorita: Boolean,
    onToggleFavorita: () -> Unit,
    onVolver: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth().height(320.dp)) {
        RecipePhoto(
            model = receta.fotoCocinada,
            nombre = receta.nombre,
            modifier = Modifier.fillMaxSize(),
            shape = RectangleShape
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.35f), Color.Transparent)))
        )
        BotonRedondo(
            icon = Icons.AutoMirrored.Outlined.ArrowBack,
            descripcion = stringResource(R.string.comun_volver),
            onClick = onVolver,
            modifier = Modifier.align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(start = 16.dp, top = 12.dp)
        )
        BotonRedondo(
            icon = if (esFavorita) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
            descripcion = stringResource(
                if (esFavorita) R.string.recetas_quitar else R.string.recetas_guardar
            ),
            onClick = onToggleFavorita,
            tint = if (esFavorita) Terracota else TextoPrincipal,
            modifier = Modifier.align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(end = 16.dp, top = 12.dp)
        )
    }
}

@Composable
private fun BotonRedondo(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    descripcion: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = TextoPrincipal
) {
    Box(
        modifier = modifier
            .size(44.dp)
            .shadow(3.dp, CircleShape, ambientColor = Color.Black.copy(alpha = 0.12f), spotColor = Color.Black.copy(alpha = 0.16f))
            .clip(CircleShape)
            .background(Color.White)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = descripcion, tint = tint, modifier = Modifier.size(22.dp))
    }
}

@Composable
private fun DatoPildora(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    texto: String
) {
    Row(
        modifier = Modifier
            .clip(PillShape)
            .background(AvisoNeutroBg)
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(icon, contentDescription = null, tint = TextoPrincipal, modifier = Modifier.size(16.dp))
        Text(text = texto, style = MaterialTheme.typography.labelLarge, color = TextoPrincipal)
    }
}

@Composable
private fun FilaIngredienteDetalle(par: IngredienteConAlimento, porciones: Int = 1) {
    val nombre = par.alimento?.nombre ?: stringResource(R.string.detalle_ingrediente)
    val tiene = par.alimento != null
    val porRacion = (par.ingrediente.cantidad / porciones.coerceAtLeast(1))
    val cantidad = stringResource(R.string.detalle_cantidad_racion, porRacion.formatoCantidad(), par.ingrediente.unidad)

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        IngredientIcon(nombre = nombre)
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = nombre,
                style = MaterialTheme.typography.titleSmall,
                color = TextoPrincipal
            )
            Text(
                text = cantidad,
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario
            )
        }
        if (tiene) {
            Box(
                modifier = Modifier.size(24.dp).clip(CircleShape).background(AzulTinta),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = stringResource(R.string.detalle_en_despensa),
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        } else {
            StatusBadge(
                text = stringResource(R.string.detalle_falta),
                variante = VarianteBadge.FALTAN
            )
        }
    }
}

@Composable
private fun ColumnaAccionDetalle(
    yaCocinada: Boolean,
    estadoCoccion: RecetaDetalleViewModel.EstadoCoccion,
    onEmpezarCoccion: () -> Unit,
    onLimpiarAviso: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SuperficieBlanca)
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val aviso = (estadoCoccion as? RecetaDetalleViewModel.EstadoCoccion.Guardado)?.aviso
        if (aviso != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = aviso,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
                TextButton(onClick = onLimpiarAviso) {
                    Text(stringResource(R.string.comun_entendido), color = Terracota)
                }
            }
        }
        PrimaryButton(
            text = stringResource(
                if (yaCocinada) R.string.detalle_volver_cocinar else R.string.detalle_marcar_cocinada
            ),
            onClick = onEmpezarCoccion,
            modifier = Modifier.fillMaxWidth(),
            enabled = estadoCoccion !is RecetaDetalleViewModel.EstadoCoccion.Guardando,
            icon = Icons.Outlined.Restaurant
        )
    }
}

/**
 * Formulario para marcar la receta como cocinada: se elige la foto con la
 * cÃ¡mara (#25) o con el selector del sistema, y al confirmar se guarda la
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
        modifier = modifier.padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.detalle_captura_titulo),
            style = MaterialTheme.typography.headlineSmall,
            color = TextoPrincipal
        )
        Text(
            text = stringResource(R.string.detalle_captura_texto),
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSecundario
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
                        Text(stringResource(R.string.detalle_elegir_otra), color = Terracota)
                    }
                }
            } else {
                when (estadoPermiso) {
                    EstadoPermiso.Comprobando -> Text(
                        text = stringResource(R.string.detalle_comprobando_permiso),
                        color = TextoSecundario
                    )

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
                            mensaje = stringResource(R.string.detalle_sin_camara),
                            alElegirImagen = { lanzadorGaleria.launch("image/*") },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    EstadoPermiso.Denegado -> AlternativaGaleria(
                        mensaje = stringResource(R.string.detalle_sin_permiso),
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
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlineButton(
                text = stringResource(R.string.detalle_cancelar),
                onClick = alCancelar,
                modifier = Modifier.weight(1f)
            )
            PrimaryButton(
                text = stringResource(
                    if (hayFoto) R.string.detalle_confirmar else R.string.detalle_confirmar_sin_foto
                ),
                onClick = { alConfirmar(fotoBitmap, fotoUri) },
                modifier = Modifier.weight(1f),
                enabled = estadoCoccion !is RecetaDetalleViewModel.EstadoCoccion.Guardando
            )
        }
    }
}

/** Foto ya elegida: la de la cÃ¡mara en bitmap o la del selector por su uri. */
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

    when {
        bitmap != null -> Image(
            bitmap = bitmap!!.asImageBitmap(),
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

@Preview(showBackground = true, backgroundColor = 0xFFFFFBF5)
@Composable
private fun RecetaDetallePreview() {
    val receta = Receta(
        id = 1,
        nombre = "Lentejas con verduras",
        kcal = 420,
        proteinas = 22,
        regionOrigen = "Castilla y LeÃ³n",
        tipoPlato = "Principal",
        tiempoMinutos = 35,
        porciones = 4,
        pasos = listOf(
            "SofrÃ­e la cebolla y la zanahoria picadas.",
            "AÃ±ade las lentejas y el caldo, y cocina 25 minutos."
        )
    )
    val ingredientes = listOf(
        IngredienteConAlimento(
            ingrediente = Ingrediente(id = 1, recetaId = 1, alimentoId = 1, cantidad = 200.0, unidad = "g"),
            alimento = Alimento(
                id = 1,
                nombre = "Lentejas",
                cantidad = 500.0,
                unidad = "g"
            )
        ),
        IngredienteConAlimento(
            ingrediente = Ingrediente(id = 2, recetaId = 1, alimentoId = 2, cantidad = 1.0, unidad = "ud"),
            alimento = null
        )
    )

    REMYTheme {
        ContenidoDetalle(
            detalle = RecetaDetalle(receta, ingredientes),
            esFavorita = true,
            onToggleFavorita = {},
            estadoCoccion = RecetaDetalleViewModel.EstadoCoccion.Normal,
            onVolver = {},
            onEmpezarCoccion = {},
            onLimpiarAviso = {}
        )
    }
}
