package com.remy.app.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.remy.app.data.PreferenciasRepository
import com.remy.app.data.Receta
import com.remy.app.data.RecetaConIngredientes
import com.remy.app.ui.components.ContenidoConEstado
import com.remy.app.ui.components.OutlineButton
import com.remy.app.ui.components.PrimaryButton
import com.remy.app.ui.components.RecipePhoto
import com.remy.app.ui.components.RemyCard
import com.remy.app.ui.components.RemyChip
import com.remy.app.ui.components.RemyLogo
import com.remy.app.ui.components.RemySearchField
import com.remy.app.ui.components.RemyTopBar
import com.remy.app.ui.components.fechaEnEspanol
import com.remy.app.ui.theme.FondoCrema
import com.remy.app.ui.theme.PillShape
import com.remy.app.ui.theme.REMYTheme
import com.remy.app.ui.theme.Terracota
import com.remy.app.ui.theme.TextoPrincipal
import com.remy.app.ui.theme.TextoSecundario
import com.remy.app.ui.viewmodel.RecetasViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun RecetasScreen(
    onRecetaClick: (Long) -> Unit,
    onPerfilClick: () -> Unit = {},
    onVerSugerencias: () -> Unit = {},
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
                onRecetaClick = onRecetaClick,
                onVerSugerencias = onVerSugerencias
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
    onRecetaClick: (Long) -> Unit,
    onVerSugerencias: () -> Unit = {}
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
        BarraBusquedaYFiltros(
            query = query,
            onQueryChange = { query = it },
            filtro = filtro,
            onFiltroChange = { filtro = it }
        )

        if (filtradas.isEmpty()) {
            EstadoVacioRecetas(
                sinDatos = recetas.isEmpty(),
                query = query,
                onVerSugerencias = onVerSugerencias,
                onLimpiar = { query = ""; filtro = FiltroRecetas.TODAS }
            )
        } else {
            CuadriculaRecetas(
                recetas = filtradas,
                favoritas = favoritas,
                onToggleFavorita = onToggleFavorita,
                onRecetaClick = onRecetaClick
            )
        }
    }
}

@Composable
private fun BarraBusquedaYFiltros(
    query: String,
    onQueryChange: (String) -> Unit,
    filtro: FiltroRecetas,
    onFiltroChange: (FiltroRecetas) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        RemySearchField(
            value = query,
            onValueChange = onQueryChange,
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
                    onClick = { onFiltroChange(opcion) }
                )
            }
        }
    }
}

@Composable
private fun EstadoVacioRecetas(
    sinDatos: Boolean,
    query: String,
    onVerSugerencias: () -> Unit,
    onLimpiar: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        RemyLogo(size = 96.dp, conBorde = true)
        Spacer(Modifier.height(16.dp))
        Text(
            text = when {
                sinDatos -> stringResource(R.string.recetas_vacia)
                query.isNotBlank() -> stringResource(R.string.recetas_sin_resultados, query.trim())
                else -> stringResource(R.string.recetas_sin_resultados_filtro)
            },
            style = MaterialTheme.typography.titleLarge,
            color = TextoPrincipal,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = when {
                sinDatos -> stringResource(R.string.recetas_vacia_texto)
                else -> stringResource(R.string.recetas_sin_resultados_filtro_texto)
            },
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSecundario,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        if (sinDatos) {
            PrimaryButton(text = stringResource(R.string.inicio_ver_sugerencias), onClick = onVerSugerencias)
        } else {
            OutlineButton(text = stringResource(R.string.sugerencias_revisar), onClick = onLimpiar)
        }
    }
}

@Composable
private fun CuadriculaRecetas(
    recetas: List<RecetaConIngredientes>,
    favoritas: Set<Long>,
    onToggleFavorita: (Long) -> Unit,
    onRecetaClick: (Long) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 96.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(recetas, key = { it.receta.id }) { item ->
            TarjetaRecetaGrid(
                item = item,
                esFavorita = item.receta.id in favoritas,
                onToggleFavorita = { onToggleFavorita(item.receta.id) },
                onClick = { onRecetaClick(item.receta.id) }
            )
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
    val kcalPorRacion = (receta.kcal.toFloat() / porciones).roundToInt()
    val proteinasPorRacion = (receta.proteinas.toFloat() / porciones).roundToInt()

    RemyCard(modifier = Modifier.fillMaxWidth(), onClick = onClick, padding = 0.dp) {
        FotoTarjetaReceta(
            receta = receta,
            esFavorita = esFavorita,
            onToggleFavorita = onToggleFavorita
        )
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
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

@Composable
private fun FotoTarjetaReceta(
    receta: Receta,
    esFavorita: Boolean,
    onToggleFavorita: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth().aspectRatio(4f / 3f)) {
        RecipePhoto(model = receta.fotoCocinada, nombre = receta.nombre, modifier = Modifier.fillMaxSize())
        if (receta.tiempoMinutos > 0) {
            EtiquetaTiempoReceta(
                minutos = receta.tiempoMinutos,
                modifier = Modifier.align(Alignment.BottomStart).padding(8.dp)
            )
        }
        BotonFavoritaTarjeta(
            esFavorita = esFavorita,
            onClick = onToggleFavorita,
            modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
        )
    }
}

@Composable
private fun EtiquetaTiempoReceta(minutos: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(PillShape)
            .background(Color.White.copy(alpha = 0.92f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(Icons.Outlined.Schedule, contentDescription = null, tint = TextoPrincipal, modifier = Modifier.size(13.dp))
        Text(
            stringResource(R.string.comun_minutos, minutos),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextoPrincipal
        )
    }
}

@Composable
private fun BotonFavoritaTarjeta(esFavorita: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.92f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            if (esFavorita) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
            contentDescription = stringResource(if (esFavorita) R.string.recetas_quitar else R.string.recetas_guardar),
            tint = if (esFavorita) Terracota else TextoSecundario,
            modifier = Modifier.size(19.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFBF5)
@Composable
private fun RecetasScreenPreview() {
    REMYTheme {
        ContenidoRecetas(
            recetas = listOf(
                RecetaConIngredientes(
                    receta = Receta(id = 1, nombre = "Arroz con verduras", kcal = 420, proteinas = 14, regionOrigen = "Mediterránea", tipoPlato = "Principal", tiempoMinutos = 25, porciones = 4),
                    ingredientes = emptyList()
                ),
                RecetaConIngredientes(
                    receta = Receta(id = 2, nombre = "Tortilla de patatas", kcal = 380, proteinas = 22, regionOrigen = "España", tipoPlato = "Principal", tiempoMinutos = 40, porciones = 4),
                    ingredientes = emptyList()
                )
            ),
            favoritas = setOf(1),
            onToggleFavorita = {},
            onRecetaClick = {}
        )
    }
}
