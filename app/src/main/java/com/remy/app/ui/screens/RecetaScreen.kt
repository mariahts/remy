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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.MonitorHeart
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.remy.app.ui.components.MetaPill
import com.remy.app.ui.components.PermisoCamara
import com.remy.app.ui.components.PersonCircle
import com.remy.app.ui.components.RemyCta
import com.remy.app.ui.components.RemyHeaderSombra
import com.remy.app.ui.components.SearchPill
import com.remy.app.ui.components.SectionCard
import com.remy.app.ui.components.categoriaDe
import com.remy.app.ui.components.emojiDe
import com.remy.app.ui.components.fechaEnEspanol
import com.remy.app.ui.components.FotoDelPlato
import com.remy.app.ui.components.formatoCantidad
import com.remy.app.ui.theme.Amber100
import com.remy.app.ui.theme.Amber600
import com.remy.app.ui.theme.DetailDarkPill
import com.remy.app.ui.theme.DetailGreen900
import com.remy.app.ui.theme.DetailGreen950
import com.remy.app.ui.theme.DetailMint
import com.remy.app.ui.theme.DetailOrange700
import com.remy.app.ui.theme.DetailOrange900
import com.remy.app.ui.theme.Emerald50
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

    Column(modifier = Modifier.fillMaxSize().background(Green100)) {
        RemyHeaderSombra(
            titulo = "Recetas Guardadas",
            bg = Green100,
            logoSize = 40.dp,
            trailing = { PersonCircle(36.dp, onClick = onPerfilClick) }
        )

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            ContenidoConEstado(
                estado = estado,
                onReintentar = viewModel::reintentar,
                mensajeCargando = "Cargando tus recetas…"
            ) { recetas ->
                var query by rememberSaveable { mutableStateOf("") }
                val filtradas = remember(recetas, query) {
                    if (query.isBlank()) recetas
                    else recetas.filter { it.receta.nombre.contains(query.trim(), ignoreCase = true) }
                }

                if (recetas.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Aún no hay recetas", style = MaterialTheme.typography.titleLarge, color = Slate600)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item(key = "contador") { PillContador(filtradas.size) }
                        item(key = "buscador") {
                            SearchPill(
                                value = query,
                                onValueChange = { query = it },
                                placeholder = "Buscar en tus recetas...",
                                leadingIcon = Icons.Filled.Search,
                                iconTint = Green600,
                                trailingIcon = if (query.isNotEmpty()) Icons.Outlined.Close else Icons.Outlined.Tune,
                                onTrailingClick = if (query.isNotEmpty()) ({ query = "" }) else null
                            )
                        }
                        if (filtradas.isEmpty()) {
                            item(key = "sin-resultados") {
                                Text(
                                    "No encontramos recetas con «$query»",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Slate600,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
                                )
                            }
                        } else {
                            item(key = "tarjetas") {
                                Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                                    filtradas.forEach { item ->
                                        TarjetaReceta(
                                            item = item,
                                            esFavorita = item.receta.id in favoritas,
                                            onToggleFavorita = { scope.launch { preferencias.alternarFavorita(item.receta.id) } },
                                            onClick = { onRecetaClick(item.receta.id) }
                                        )
                                    }
                                }
                            }
                        }
                        item(key = "pie") { PieRecetas() }
                    }
                }
            }
        }
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
            "Tus recetas se sincronizan automáticamente con tu despensa REMY",
            style = MaterialTheme.typography.labelSmall,
            color = Slate600.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp).width(260.dp)
        )
    }
}

/** Chip blanco con punto de color de categoría (chips de ingredientes §2.7). */
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
            // Si la receta se cocinó y tiene foto, mostrarla; si no, placeholder.
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
                        MetaPill("${receta.proteinas}g Proteína", icon = Icons.Outlined.FitnessCenter, tint = Green600, bg = Slate100, fg = Slate900)
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

    // Al confirmar, el registro ya está guardado: se vuelve al detalle para
    // ver la receta con su fecha y su foto.
    LaunchedEffect(estadoCoccion) {
        if (estadoCoccion is RecetaDetalleViewModel.EstadoCoccion.Guardado) {
            capturando = false
        }
    }

    when (val actual = estado) {
        RecetaDetalleViewModel.Estado.Cargando -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    CircularProgressIndicator(color = Green600, strokeWidth = 4.dp)
                    Text("Cargando…", style = MaterialTheme.typography.bodyLarge, color = Slate600)
                }
            }
        }

        RecetaDetalleViewModel.Estado.NoEncontrada -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("No se encontró esta receta", style = MaterialTheme.typography.titleLarge, color = Slate900)
                    TextButton(onClick = onVolver) {
                        Text("← Volver al listado", color = Green600)
                    }
                }
            }
        }

        is RecetaDetalleViewModel.Estado.Encontrada -> if (capturando) {
            Column(modifier = Modifier.fillMaxSize().background(Green050)) {
                DetalleTopBar(
                    fav = false,
                    onFav = {},
                    onVolver = {
                        capturando = false
                        viewModel.limpiarCoccion()
                    },
                    onCompartir = {},
                    onPerfilClick = onPerfilClick
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
                onLimpiarAviso = { viewModel.limpiarCoccion() },
                onPerfilClick = onPerfilClick,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun DetalleReceta(
    detalle: RecetaDetalle,
    estadoCoccion: RecetaDetalleViewModel.EstadoCoccion,
    onVolver: () -> Unit,
    onEmpezarCoccion: () -> Unit,
    onLimpiarAviso: () -> Unit,
    onPerfilClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val receta = detalle.receta
    val scroll = rememberScrollState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val preferencias = remember { PreferenciasRepository.delContexto(context) }
    val favoritas by preferencias.favoritas.collectAsStateWithLifecycle(initialValue = emptySet())
    val fav = receta.id in favoritas
    val onFav: () -> Unit = { scope.launch { preferencias.alternarFavorita(receta.id) } }
    var porciones by rememberSaveable(receta.id) { mutableStateOf(1) }
    val checks = remember { mutableStateMapOf<Long, Boolean>() }
    val pasosHechos = remember { mutableStateMapOf<Int, Boolean>() }

    val compartir: () -> Unit = {
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "¡Mira esta receta de REMY: ${receta.nombre}!")
        }
        context.startActivity(Intent.createChooser(send, null))
    }

    Scaffold(
        modifier = modifier,
        containerColor = Green050,
        topBar = {
            DetalleTopBar(
                fav = fav,
                onFav = onFav,
                onVolver = onVolver,
                onCompartir = compartir,
                onPerfilClick = onPerfilClick
            )
        },
        bottomBar = {
            DetalleBottomBar(
                tiempo = receta.tiempoMinutos,
                onEmpezar = { scope.launch { scroll.animateScrollTo(scroll.maxValue) } }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            HeroReceta(
                receta = receta,
                totalIngredientes = detalle.ingredientes.size,
                enDespensa = detalle.ingredientes.count { it.alimento != null },
                fav = fav,
                onFav = onFav,
                onCompartir = compartir
            )
            TitleCard(receta)
            NutriCard(receta)
            ChefTip(receta)
            IngCard(detalle, porciones, { if (porciones > 1) porciones-- }, { if (porciones < 8) porciones++ }, checks)
            StepsCard(receta, pasosHechos)
            CoccionCard(
                receta = receta,
                estadoCoccion = estadoCoccion,
                onEmpezarCoccion = onEmpezarCoccion,
                onLimpiarAviso = onLimpiarAviso
            )
            Spacer(Modifier.height(4.dp))
        }
    }
}

@Composable
private fun DetalleTopBar(
    fav: Boolean,
    onFav: () -> Unit,
    onVolver: () -> Unit,
    onCompartir: () -> Unit,
    onPerfilClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RectangleShape, ambientColor = Color.Black.copy(alpha = 0.04f), spotColor = Color.Black.copy(alpha = 0.06f))
            .background(Green050.copy(alpha = 0.92f))
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onVolver, modifier = Modifier.size(44.dp)) {
            Icon(Icons.Outlined.ArrowBack, contentDescription = "Volver", tint = Slate900, modifier = Modifier.size(24.dp))
        }
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(com.remy.app.R.drawable.remy_logo),
            contentDescription = null,
            modifier = Modifier.size(32.dp).clip(CircleShape)
        )
        Text(
            "Detalle de receta",
            style = MaterialTheme.typography.titleLarge,
            color = Slate900,
            maxLines = 1,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
        )
        IconButton(onClick = onFav, modifier = Modifier.size(44.dp)) {
            Icon(
                if (fav) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = "Favorito",
                tint = if (fav) Red400 else Slate600,
                modifier = Modifier.size(22.dp)
            )
        }
        IconButton(onClick = onCompartir, modifier = Modifier.size(44.dp)) {
            Icon(Icons.Outlined.Share, contentDescription = "Compartir", tint = Slate700, modifier = Modifier.size(22.dp))
        }
        PersonCircle(32.dp, onClick = onPerfilClick)
    }
}

@Composable
private fun HeroReceta(
    receta: Receta,
    totalIngredientes: Int,
    enDespensa: Int,
    fav: Boolean,
    onFav: () -> Unit,
    onCompartir: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(4f / 3f)
            .heightIn(max = 320.dp)
            .clip(RemyShapes.large)
    ) {
        if (receta.fotoCocinada != null) {
            FotoDelPlato(ruta = receta.fotoCocinada, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        } else {
            FotoReceta(receta.nombre, Modifier.fillMaxSize())
        }
        Box(
            modifier = Modifier.matchParentSize().background(
                Brush.verticalGradient(
                    listOf(
                        Color.Black.copy(alpha = 0.30f),
                        Color.Transparent,
                        Green050
                    )
                )
            )
        )
        Row(
            modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter).padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .clip(PillShape)
                    .background(Color.White.copy(alpha = 0.9f))
                    .padding(horizontal = 12.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🌿 100% Match Despensa", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Green600)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.9f)).clickable(onClick = onFav),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (fav) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (fav) Red400 else Slate600,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.9f)).clickable(onClick = onCompartir),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Share, contentDescription = "Compartir", tint = Slate700, modifier = Modifier.size(18.dp))
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomStart).padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (receta.tiempoMinutos > 0) {
                PillOscuro("${receta.tiempoMinutos} min", Icons.Outlined.Schedule)
            }
            PillOscuro("Fácil", Icons.Outlined.SentimentSatisfied)
            if (totalIngredientes > 0) {
                Row(
                    modifier = Modifier.clip(PillShape).background(DetailMint).padding(horizontal = 12.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Filled.Inventory, contentDescription = null, tint = DetailGreen950, modifier = Modifier.size(15.dp))
                    Text(
                        "$enDespensa/$totalIngredientes en despensa",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DetailGreen950
                    )
                }
            }
        }
    }
}

@Composable
private fun PillOscuro(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        modifier = Modifier
            .clip(PillShape)
            .background(DetailDarkPill.copy(alpha = 0.85f))
            .padding(horizontal = 12.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(icon, contentDescription = null, tint = Green400, modifier = Modifier.size(15.dp))
        Text(text, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
    }
}

@Composable
private fun TitleCard(receta: Receta) {
    SectionCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RemyShapes.medium,
        border = null,
        glow = true,
        padding = PaddingValues(20.dp)
    ) {
        val tags = buildList {
            if (receta.regionOrigen.isNotBlank()) add(Triple("#${receta.regionOrigen}", Emerald50, DetailGreen950))
            if (receta.tipoPlato.isNotBlank()) {
                add(
                    Triple(
                        "#${receta.tipoPlato.replaceFirstChar { it.uppercase() }}",
                        Slate100,
                        Green600
                    )
                )
            }
            if (receta.proteinas >= 30) add(Triple("#AltaProteína", Terracota100, DetailOrange900))
        }
        if (tags.isNotEmpty()) {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                tags.forEach { (texto, bg, fg) ->
                    Row(
                        modifier = Modifier.clip(PillShape).background(bg).padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(texto, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = fg, maxLines = 1)
                    }
                }
            }
        }
        Text(
            receta.nombre,
            style = MaterialTheme.typography.headlineSmall,
            color = Slate900,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            descripcionDe(receta),
            style = MaterialTheme.typography.bodyLarge,
            color = Slate600,
            modifier = Modifier.padding(top = 4.dp)
        )
        Row(
            modifier = Modifier.padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Filled.Verified, contentDescription = null, tint = Green600, modifier = Modifier.size(18.dp))
            Text("Validado por Chef Botánico REMY", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Green600)
        }
    }
}

private fun descripcionDe(r: Receta): String {
    val base = when {
        r.tipoPlato.isNotBlank() && r.regionOrigen.isNotBlank() ->
            "${r.tipoPlato.replaceFirstChar { it.uppercase() }} de tradición ${r.regionOrigen.lowercase()} preparado al punto"
        r.tipoPlato.isNotBlank() -> r.tipoPlato.replaceFirstChar { it.uppercase() }
        else -> "Receta casera"
    }
    return "$base con ${r.kcal} kcal y ${r.proteinas} g de proteínas por ración. " +
        "Aprovecha al 100% los ingredientes de tu despensa sin generar desperdicios."
}

@Composable
private fun NutriCard(receta: Receta) {
    var desgloseAbierto by remember { mutableStateOf(false) }
    val rotacion by animateFloatAsState(if (desgloseAbierto) 180f else 0f, label = "chevron")

    SectionCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RemyShapes.medium,
        border = null,
        glow = true,
        padding = PaddingValues(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Outlined.MonitorHeart, contentDescription = null, tint = Green600, modifier = Modifier.size(22.dp))
                    Text("Valores Nutricionales", style = MaterialTheme.typography.titleLarge, color = Slate900)
                }
                MetaPill("Por ración", bg = Slate100, fg = Slate600)
            }

            val pctKcal = if (receta.kcal > 0) receta.kcal * 100 / 2000 else 0
            val pctProt = if (receta.proteinas > 0) receta.proteinas * 100 / 50 else 0
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MacroCard("Calorías", "${receta.kcal} kcal", "$pctKcal% VDR", Green600, Slate500, Modifier.weight(1f))
                    MacroCard("Proteínas", "${receta.proteinas}g", "$pctProt% VDR", DetailOrange700, Amber600, Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MacroCard("Carbohidratos", "—", "No registrado", Slate900, Slate500, Modifier.weight(1f))
                    MacroCard("Grasas", "—", "No registrado", Slate900, Slate500, Modifier.weight(1f))
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RemyShapes.small)
                    .background(Slate100)
                    .clickable { desgloseAbierto = !desgloseAbierto }
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Outlined.BarChart, contentDescription = null, tint = Green600, modifier = Modifier.size(18.dp))
                        Text("Desglose nutricional completo", style = MaterialTheme.typography.labelLarge, color = Green600)
                    }
                    Icon(
                        Icons.Outlined.ExpandMore,
                        contentDescription = null,
                        tint = Slate600,
                        modifier = Modifier.size(20.dp).graphicsLayer { rotationZ = rotacion }
                    )
                }
                if (desgloseAbierto) {
                    Column(modifier = Modifier.padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        DesgloseRow("Calorías totales", "${receta.kcal} kcal")
                        DesgloseRow("Proteínas", "${receta.proteinas} g")
                        DesgloseRow("Carbohidratos", "No registrados")
                        DesgloseRow("Grasas", "No registradas")
                    }
                }
            }
        }
    }
}

@Composable
private fun MacroCard(
    label: String,
    valor: String,
    nota: String,
    valorColor: Color,
    notaColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RemyShapes.small)
            .background(Green050)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = Slate600)
        Text(valor, style = MaterialTheme.typography.titleLarge, color = valorColor)
        Text(nota, style = MaterialTheme.typography.bodySmall, color = notaColor)
    }
}

@Composable
private fun DesgloseRow(label: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RemyShapes.extraSmall)
            .background(Green050)
            .padding(horizontal = 8.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Slate600)
        Text(valor, style = MaterialTheme.typography.bodyMedium, color = Slate900)
    }
}

@Composable
private fun ChefTip(receta: Receta) {
    SectionCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RemyShapes.medium,
        bg = Green100,
        border = null,
        padding = PaddingValues(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(DetailMint),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Restaurant, contentDescription = null, tint = DetailGreen950, modifier = Modifier.size(26.dp))
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Consejo del Chef Remy", style = MaterialTheme.typography.titleLarge, color = DetailGreen950)
                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = Green600, modifier = Modifier.size(16.dp))
                }
                Text(
                    consejoDe(receta),
                    style = MaterialTheme.typography.bodyMedium,
                    color = DetailGreen900
                )
            }
        }
    }
}

private fun consejoDe(r: Receta): String {
    val s = (r.nombre + " " + r.regionOrigen + " " + r.tipoPlato).lowercase()
    return when {
        listOf("mediterr", "españa", "espan", "arroz", "paella").any { s.contains(it) } ->
            "«Raspa suavemente el fondo de la sartén durante los últimos 2 minutos de cocción a fuego vivo para conseguir un ligero y crujiente socarrat aromático lleno de sabor tostado.»"
        r.tipoPlato.equals("Postre", true) ->
            "«Deja enfriar unos minutos antes de servir: los sabores se asientan y la textura gana cuerpo sin perder frescura.»"
        r.tipoPlato.equals("Entrante", true) ->
            "«Un chorrito de limón o vinagre al final despierta todos los sabores de tu despensa y equilibra el plato.»"
        else ->
            "«Sazona por capas: añade sal en cada paso, no solo al final, y termina con un hilo de aceite de oliva para potenciar el aroma de tus ingredientes.»"
    }
}

@Composable
private fun IngCard(
    detalle: RecetaDetalle,
    porciones: Int,
    onMenos: () -> Unit,
    onMas: () -> Unit,
    checks: MutableMap<Long, Boolean>
) {
    SectionCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RemyShapes.medium,
        border = null,
        glow = false,
        padding = PaddingValues(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Ingredientes", style = MaterialTheme.typography.titleLarge, color = Slate900)
                Row(
                    modifier = Modifier.clip(PillShape).background(Slate100).padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    MiniCircleBtn(Icons.Outlined.Remove, Slate900, onMenos)
                    Text(
                        "$porciones porc.",
                        style = MaterialTheme.typography.labelLarge,
                        color = Slate900,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(56.dp)
                    )
                    MiniCircleBtn(Icons.Outlined.Add, Green600, onMas)
                }
            }

            val total = detalle.ingredientes.size
            val enDespensa = detalle.ingredientes.count { it.alimento != null }
            val todos = total > 0 && enDespensa == total
            Row(
                modifier = Modifier
                    .clip(PillShape)
                    .background(if (todos) Green600.copy(alpha = 0.1f) else Amber100)
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    if (todos) Icons.Filled.CheckCircle else Icons.Outlined.Warning,
                    contentDescription = null,
                    tint = if (todos) Green600 else Amber600,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    if (todos) "¡Tienes todos los ingredientes! ($enDespensa de $total)"
                    else "Te faltan ${total - enDespensa} ingredientes ($enDespensa de $total)",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (todos) Green600 else Amber600,
                    maxLines = 1
                )
            }

            detalle.ingredientes.forEach { par ->
                val nombre = par.alimento?.nombre ?: "Ingrediente"
                FilaIngrediente(
                    nombre = nombre,
                    cantidadTexto = "${(par.ingrediente.cantidad * porciones).formatoCantidad()} ${par.ingrediente.unidad}",
                    enDespensa = par.alimento != null,
                    subtitulo = if (par.alimento != null) {
                        "En despensa (${par.alimento.cantidad.formatoCantidad()} ${par.alimento.unidad})"
                    } else {
                        "No está en tu despensa"
                    },
                    marcado = checks[par.ingrediente.id] == true,
                    onToggle = {
                        val actual = checks[par.ingrediente.id] ?: false
                        checks[par.ingrediente.id] = !actual
                    }
                )
            }
        }
    }
}

@Composable
private fun MiniCircleBtn(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(Color.White)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
    }
}

@Composable
private fun FilaIngrediente(
    nombre: String,
    cantidadTexto: String,
    enDespensa: Boolean,
    subtitulo: String,
    marcado: Boolean,
    onToggle: () -> Unit
) {
    val cat = categoriaDe(nombre)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RemyShapes.small)
            .background(Green050)
            .clickable(onClick = onToggle)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            EmojiCircle(emojiDe(nombre), size = 28.dp, bg = if (enDespensa) cat.colors.bg else Slate100)
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    "$cantidadTexto $nombre",
                    style = MaterialTheme.typography.labelLarge,
                    color = Slate900,
                    maxLines = 1
                )
                Text(
                    subtitulo,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (enDespensa) Green500 else Amber600,
                    maxLines = 1
                )
            }
        }
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(if (marcado) Green600 else Color.Transparent)
                .border(1.5.dp, if (marcado) Green600 else Slate300, CircleShape)
                .clickable(onClick = onToggle),
            contentAlignment = Alignment.Center
        ) {
            if (marcado) {
                Icon(Icons.Outlined.Check, contentDescription = "Listo", tint = Color.White, modifier = Modifier.size(14.dp))
            }
        }
    }
}

@Composable
private fun StepsCard(receta: Receta, pasosHechos: MutableMap<Int, Boolean>) {
    val total = receta.pasos.size
    val primerPendiente = (0 until total).firstOrNull { pasosHechos[it] != true } ?: -1
    val pasoMin = if (receta.tiempoMinutos > 0 && total > 0) maxOf(1, receta.tiempoMinutos / total) else 0

    SectionCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RemyShapes.medium,
        border = null,
        glow = false,
        padding = PaddingValues(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text("Pasos de preparación", style = MaterialTheme.typography.titleLarge, color = Slate900)
                    Text(
                        "Toca cada paso conforme completes tu cocción",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate600
                    )
                }
                MetaPill(
                    "${pasosHechos.size.coerceAtMost(total)} de $total listos",
                    bg = Green100,
                    fg = Green900
                )
            }

            if (total == 0) {
                Text("Sin pasos registrados", style = MaterialTheme.typography.bodyMedium, color = Slate600)
            }

            receta.pasos.forEachIndexed { indice, paso ->
                val hecho = pasosHechos[indice] == true
                val (titulo, desc) = partirPaso(paso)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RemyShapes.small)
                        .background(if (hecho) Green100 else Green050)
                        .clickable {
                            pasosHechos[indice] = !hecho
                            if (!hecho) pasosHechos[indice] = true else pasosHechos.remove(indice)
                        }
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f).padding(end = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (hecho || indice == primerPendiente) Green600 else Slate100
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "${indice + 1}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (hecho || indice == primerPendiente) Color.White else Slate900
                                )
                            }
                            Text(titulo, style = MaterialTheme.typography.titleLarge, color = Slate900, maxLines = 2)
                        }
                        if (desc.isNotEmpty()) {
                            Text(
                                desc,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Slate600,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        if (pasoMin > 0) {
                            Row(
                                modifier = Modifier.padding(start = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Outlined.Timer, contentDescription = null, tint = Green600, modifier = Modifier.size(14.dp))
                                Text("≈ $pasoMin min", style = MaterialTheme.typography.labelSmall, color = Green600)
                            }
                        }
                    }
                    Icon(
                        if (hecho) Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                        contentDescription = if (hecho) "Paso completado" else "Marcar paso",
                        tint = if (hecho) Green600 else Slate500,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

/** Divide un paso libre en título y descripción (la BD guarda texto libre). */
private fun partirPaso(paso: String): Pair<String, String> {
    val s = paso.trim()
    val dosPuntos = s.indexOf(": ")
    if (dosPuntos in 1..60) return s.substring(0, dosPuntos) to s.substring(dosPuntos + 2)
    val punto = s.indexOf(". ")
    if (punto in 1..60) return s.substring(0, punto) to s.substring(punto + 2)
    if (s.length <= 60) return s to ""
    return s.take(57) + "…" to ""
}

@Composable
private fun CoccionCard(
    receta: Receta,
    estadoCoccion: RecetaDetalleViewModel.EstadoCoccion,
    onEmpezarCoccion: () -> Unit,
    onLimpiarAviso: () -> Unit
) {
    SectionCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RemyShapes.medium,
        border = null,
        glow = false,
        padding = PaddingValues(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Outlined.Restaurant, contentDescription = null, tint = Green600, modifier = Modifier.size(22.dp))
                Text("Cocción", style = MaterialTheme.typography.titleLarge, color = Slate900)
            }
            if (receta.fechaCocinada != null) {
                Text(
                    "Cocinada el ${fechaEnEspanol(receta.fechaCocinada)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Green600
                )
                FotoDelPlato(
                    ruta = receta.fotoCocinada,
                    modifier = Modifier.fillMaxWidth().heightIn(max = 220.dp).clip(RemyShapes.small),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    "Todavía no has cocinado esta receta.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Slate600
                )
                RemyCta(
                    text = "Marcar como cocinada",
                    onClick = onEmpezarCoccion,
                    modifier = Modifier.fillMaxWidth(),
                    height = 44.dp,
                    icon = Icons.Outlined.Restaurant,
                    fontSize = 14.sp
                )
            }

            if (estadoCoccion is RecetaDetalleViewModel.EstadoCoccion.Guardado) {
                estadoCoccion.aviso?.let { aviso ->
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
                        TextButton(onClick = onLimpiarAviso) { Text("Entendido", color = Green600) }
                    }
                }
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
        Text("Marcar como cocinada", style = MaterialTheme.typography.headlineSmall, color = Slate900)
        Text(
            "Haz o elige una foto del plato. Al confirmar se guarda la fecha de hoy junto a la receta y a la fotografía.",
            style = MaterialTheme.typography.bodyMedium,
            color = Slate600
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
                        Text("Elegir otra foto", color = Green600)
                    }
                }
            } else {
                when (estadoPermiso) {
                    EstadoPermiso.Comprobando -> Text("Comprobando el permiso de cámara…", color = Slate600)

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
                        mensaje = "Sin permiso de cámara no se pueden hacer fotos. Puedes conceder el permiso en Ajustes o usar una imagen de tu galería.",
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
                Text("Cancelar", color = Slate600)
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

        else -> Text("Cargando la imagen…", modifier = modifier.padding(16.dp), color = Slate600)
    }
}

@Composable
private fun DetalleBottomBar(tiempo: Int, onEmpezar: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, RectangleShape, ambientColor = Green600.copy(alpha = 0.08f), spotColor = Green600.copy(alpha = 0.12f))
            .background(Green050.copy(alpha = 0.95f))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (tiempo > 0) {
            Row(
                modifier = Modifier.clip(PillShape).background(Slate100).padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Outlined.Timer, contentDescription = null, tint = Green600, modifier = Modifier.size(20.dp))
                Text("$tiempo min", style = MaterialTheme.typography.titleLarge, color = Slate900)
            }
        }
        RemyCta(
            text = "Empezar a cocinar",
            onClick = onEmpezar,
            modifier = Modifier.weight(1f),
            height = 48.dp,
            icon = Icons.Outlined.Restaurant,
            fontSize = 14.sp
        )
    }
}
