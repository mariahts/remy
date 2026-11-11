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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Kitchen
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.remy.app.data.FiltrosSugerencias
import com.remy.app.data.Receta
import com.remy.app.data.RecetaConIngredientes
import com.remy.app.ui.components.ContenidoConEstado
import com.remy.app.ui.components.IngredientIcon
import com.remy.app.ui.components.OutlineButton
import com.remy.app.ui.components.PrimaryButton
import com.remy.app.ui.components.RecipePhoto
import com.remy.app.ui.components.RemyCard
import com.remy.app.ui.components.RemyChip
import com.remy.app.ui.components.RemyLogo
import com.remy.app.ui.components.RemyTopBar
import com.remy.app.ui.components.StatusBadge
import com.remy.app.ui.components.VarianteBadge
import com.remy.app.ui.theme.AzulTinta
import com.remy.app.ui.theme.BordeTarjeta
import com.remy.app.ui.theme.FondoCrema
import com.remy.app.ui.theme.PillShape
import com.remy.app.ui.theme.REMYTheme
import com.remy.app.ui.theme.Terracota
import com.remy.app.ui.theme.TextoPrincipal
import com.remy.app.ui.theme.TextoSecundario
import com.remy.app.ui.viewmodel.CasiCocinableVista
import com.remy.app.ui.viewmodel.FaltanteVista
import com.remy.app.ui.viewmodel.GruposSugerencias
import com.remy.app.ui.viewmodel.PreferenciasViewModel
import com.remy.app.ui.viewmodel.SugerenciasViewModel

private val TIPOS_DE_PLATO = listOf("Entrante", "Principal", "Postre")

/**
 * Sugerencias: recetas que puedes cocinar ya y recetas casi listas, con los
 * filtros guardados en DataStore. Los filtros viven en un ModalBottomSheet y
 * la pantalla se abre desde el atajo de Inicio (no es una pestaña).
 */
@Composable
fun SugerenciasScreen(
    onRecetaClick: (Long) -> Unit = {},
    onIrADespensa: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    onVolver: () -> Unit = {},
    preferencias: PreferenciasViewModel = viewModel(factory = PreferenciasViewModel.Factory),
    sugerencias: SugerenciasViewModel = viewModel(factory = SugerenciasViewModel.Factory)
) {
    val filtros by preferencias.filtros.collectAsStateWithLifecycle()
    val estado by sugerencias.estado.collectAsStateWithLifecycle()
    val regiones by sugerencias.regiones.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().background(FondoCrema)) {
        RemyTopBar(
            titulo = stringResource(R.string.sugerencias_titulo),
            onVolver = onVolver
        )
        ContenidoConEstado(
            estado = estado,
            onReintentar = sugerencias::reintentar,
            mensajeCargando = stringResource(R.string.sugerencias_cargando)
        ) { grupos ->
            ContenidoSugerencias(
                filtros = filtros,
                grupos = grupos,
                regiones = regiones,
                onAlternarTipo = preferencias::alternarTipoDePlato,
                onCambiarMaximo = preferencias::cambiarMaximoFaltantes,
                onAlternarFaltante = preferencias::alternarFaltantePermitido,
                onCambiarTiempo = preferencias::cambiarTiempoMaximo,
                onCambiarCalorias = preferencias::cambiarKcalMaximas,
                onCambiarProteinas = preferencias::cambiarProteinasMinimas,
                onAlternarAltaProteina = preferencias::alternarAltaProteina,
                onAlternarBajaHidratos = preferencias::alternarBajaHidratos,
                onAlternarBajaGrasa = preferencias::alternarBajaGrasa,
                onCambiarRegion = preferencias::cambiarRegion,
                onLimpiarFiltros = preferencias::limpiarFiltros,
                onRecetaClick = onRecetaClick,
                onIrADespensa = onIrADespensa
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContenidoSugerencias(
    filtros: FiltrosSugerencias,
    grupos: GruposSugerencias,
    regiones: List<String>,
    onAlternarTipo: (String) -> Unit,
    onCambiarMaximo: (Int) -> Unit,
    onAlternarFaltante: (Long) -> Unit,
    onCambiarTiempo: (Int) -> Unit,
    onCambiarCalorias: (Int) -> Unit,
    onCambiarProteinas: (Int) -> Unit,
    onAlternarAltaProteina: () -> Unit,
    onAlternarBajaHidratos: () -> Unit,
    onAlternarBajaGrasa: () -> Unit,
    onCambiarRegion: (String) -> Unit,
    onLimpiarFiltros: () -> Unit,
    onRecetaClick: (Long) -> Unit,
    onIrADespensa: () -> Unit
) {
    var mostrarFiltros by rememberSaveable { mutableStateOf(false) }
    val total = grupos.completas.size

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item(key = "frase") {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = if (total == 1) {
                        stringResource(R.string.sugerencias_frase_una)
                    } else {
                        stringResource(R.string.sugerencias_frase, total)
                    },
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextoPrincipal
                )
                Text(
                    stringResource(R.string.sugerencias_subtitulo),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario
                )
            }
        }

        item(key = "chips") {
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RemyChip(
                    text = stringResource(R.string.sugerencias_todos),
                    selected = filtros.tiposDePlato.isEmpty(),
                    onClick = { filtros.tiposDePlato.forEach(onAlternarTipo) }
                )
                TIPOS_DE_PLATO.forEach { tipo ->
                    RemyChip(
                        text = tipo,
                        selected = tipo in filtros.tiposDePlato,
                        onClick = { onAlternarTipo(tipo) }
                    )
                }
                RemyChip(
                    text = stringResource(R.string.sugerencias_filtros),
                    selected = filtros.hayFiltros,
                    icon = Icons.Outlined.Tune,
                    onClick = { mostrarFiltros = true }
                )
            }
        }

        if (!grupos.hayAlgunaSugerencia) {
            item(key = "vacio") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    RemyLogo(size = 96.dp, conBorde = true)
                    Text(
                        stringResource(R.string.sugerencias_vacio_titulo),
                        style = MaterialTheme.typography.titleLarge,
                        color = TextoPrincipal,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        stringResource(R.string.sugerencias_vacio_texto),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextoSecundario,
                        textAlign = TextAlign.Center
                    )
                    PrimaryButton(
                        text = stringResource(R.string.sugerencias_revisar),
                        onClick = { mostrarFiltros = true }
                    )
                    OutlineButton(
                        text = stringResource(R.string.sugerencias_ir_despensa),
                        onClick = onIrADespensa,
                        icon = Icons.Outlined.Kitchen
                    )
                }
            }
        }

        if (grupos.completas.isNotEmpty()) {
            item(key = "cabecera-listas") {
                CabeceraSeccion(
                    titulo = stringResource(R.string.sugerencias_listas),
                    total = grupos.completas.size
                )
            }
            items(grupos.completas, key = { "lista-${it.receta.id}" }) { receta ->
                TarjetaSugerencia(
                    receta = receta,
                    esCompleta = true,
                    faltantes = emptyList(),
                    onRecetaClick = onRecetaClick
                )
            }
        }

        if (grupos.casiCocinables.isNotEmpty()) {
            item(key = "cabecera-casi") {
                CabeceraSeccion(
                    titulo = stringResource(R.string.sugerencias_casi),
                    total = grupos.casiCocinables.size
                )
            }
            items(grupos.casiCocinables, key = { "casi-${it.receta.receta.id}" }) { casi ->
                TarjetaSugerencia(
                    receta = casi.receta,
                    esCompleta = false,
                    faltantes = casi.faltantes,
                    onRecetaClick = onRecetaClick
                )
            }
            item(key = "ir-despensa") {
                OutlineButton(
                    text = stringResource(R.string.sugerencias_anadir_despensa),
                    onClick = onIrADespensa,
                    icon = Icons.Outlined.Kitchen
                )
            }
        }
    }

    if (mostrarFiltros) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { mostrarFiltros = false },
            sheetState = sheetState,
            containerColor = FondoCrema
        ) {
            PantallaFiltros(
                filtros = filtros,
                grupos = grupos,
                regiones = regiones,
                onAlternarTipo = onAlternarTipo,
                onCambiarMaximo = onCambiarMaximo,
                onCambiarTiempo = onCambiarTiempo,
                onCambiarCalorias = onCambiarCalorias,
                onCambiarProteinas = onCambiarProteinas,
                onAlternarAltaProteina = onAlternarAltaProteina,
                onAlternarBajaHidratos = onAlternarBajaHidratos,
                onAlternarBajaGrasa = onAlternarBajaGrasa,
                onCambiarRegion = onCambiarRegion,
                onLimpiarFiltros = onLimpiarFiltros,
                onMostrar = { mostrarFiltros = false },
                onAlternarFaltante = onAlternarFaltante,
                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.92f)
            )
        }
    }
}

@Composable
private fun CabeceraSeccion(titulo: String, total: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            titulo,
            style = MaterialTheme.typography.titleLarge,
            color = TextoPrincipal,
            modifier = Modifier.weight(1f)
        )
        StatusBadge(
            text = total.toString(),
            variante = VarianteBadge.NEUTRO
        )
    }
}

@Composable
private fun TarjetaSugerencia(
    receta: RecetaConIngredientes,
    esCompleta: Boolean,
    faltantes: List<FaltanteVista>,
    onRecetaClick: (Long) -> Unit
) {
    val datos = receta.receta
    RemyCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onRecetaClick(datos.id) }
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            RecipePhoto(
                model = datos.fotoCocinada,
                nombre = datos.nombre,
                modifier = Modifier.size(88.dp).clip(RoundedCornerShape(16.dp))
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    datos.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextoPrincipal,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (datos.tiempoMinutos > 0) {
                        Text(
                            stringResource(R.string.comun_minutos, datos.tiempoMinutos),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextoSecundario
                        )
                        Text("·", style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
                    }
                    val porciones = datos.porciones.coerceAtLeast(1)
                    Text(
                        stringResource(R.string.comun_kcal_racion, kotlin.math.round(datos.kcal.toFloat() / porciones).toInt()),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextoSecundario
                    )
                }
                StatusBadge(
                    text = if (esCompleta) {
                        stringResource(R.string.sugerencias_badge_lista)
                    } else {
                        stringResource(R.string.sugerencias_badge_faltan, faltantes.size)
                    },
                    variante = if (esCompleta) VarianteBadge.LISTA else VarianteBadge.FALTAN
                )
            }
        }

        if (!esCompleta && faltantes.isNotEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                faltantes.forEach { faltante ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IngredientIcon(nombre = faltante.nombre, size = 30.dp)
                        Text(
                            text = if (faltante.insuficiente) {
                                stringResource(R.string.sugerencias_insuficiente, faltante.nombre)
                            } else {
                                stringResource(R.string.sugerencias_falta, faltante.nombre)
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = TextoSecundario,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Filtros (ModalBottomSheet)
// ---------------------------------------------------------------------------

@Composable
internal fun PantallaFiltros(
    filtros: FiltrosSugerencias,
    grupos: GruposSugerencias,
    regiones: List<String>,
    onAlternarTipo: (String) -> Unit,
    onCambiarMaximo: (Int) -> Unit,
    onCambiarTiempo: (Int) -> Unit,
    onCambiarCalorias: (Int) -> Unit,
    onCambiarProteinas: (Int) -> Unit,
    onAlternarAltaProteina: () -> Unit,
    onAlternarBajaHidratos: () -> Unit,
    onAlternarBajaGrasa: () -> Unit,
    onCambiarRegion: (String) -> Unit,
    onLimpiarFiltros: () -> Unit,
    onMostrar: () -> Unit,
    onPerfilClick: () -> Unit = {},
    onAlternarFaltante: (Long) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val total = grupos.completas.size + grupos.casiCocinables.size

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                stringResource(R.string.sugerencias_ajustar_titulo),
                style = MaterialTheme.typography.headlineSmall,
                color = TextoPrincipal
            )
            Text(
                stringResource(R.string.sugerencias_ajustar_texto),
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario
            )
        }

        SeccionFiltro(titulo = stringResource(R.string.sugerencias_tipo)) {
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TIPOS_DE_PLATO.forEach { tipo ->
                    RemyChip(
                        text = tipo,
                        selected = tipo in filtros.tiposDePlato,
                        onClick = { onAlternarTipo(tipo) }
                    )
                }
            }
        }

        SeccionFiltro(titulo = stringResource(R.string.sugerencias_max_faltantes)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StepperBoton(
                    icono = Icons.Outlined.Remove,
                    enabled = filtros.maximoFaltantes > 0,
                    onClick = { onCambiarMaximo((filtros.maximoFaltantes - 1).coerceAtLeast(0)) }
                )
                Text(
                    "${filtros.maximoFaltantes}",
                    modifier = Modifier.width(40.dp),
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextoPrincipal,
                    textAlign = TextAlign.Center
                )
                StepperBoton(
                    icono = Icons.Outlined.Add,
                    enabled = filtros.maximoFaltantes < 20,
                    onClick = { onCambiarMaximo((filtros.maximoFaltantes + 1).coerceAtMost(20)) }
                )
                Text(
                    stringResource(R.string.sugerencias_de_20),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextoSecundario
                )
            }
        }

        SeccionFiltro(titulo = stringResource(R.string.sugerencias_tiempo)) {
            FilaOpciones(
                opciones = listOf(
                    stringResource(R.string.sugerencias_sin_limite) to 0,
                    "15 min" to 15,
                    "30 min" to 30,
                    "45 min" to 45,
                    "60 min" to 60
                ),
                seleccionado = filtros.tiempoMaximoMinutos,
                onSeleccionar = onCambiarTiempo
            )
        }

        SeccionFiltro(titulo = stringResource(R.string.sugerencias_kcal)) {
            FilaOpciones(
                opciones = listOf(
                    stringResource(R.string.sugerencias_sin_limite) to 0,
                    "300 kcal" to 300,
                    "500 kcal" to 500,
                    "700 kcal" to 700
                ),
                seleccionado = filtros.kcalMaximas,
                onSeleccionar = onCambiarCalorias
            )
        }

        SeccionFiltro(titulo = stringResource(R.string.sugerencias_proteinas)) {
            FilaOpciones(
                opciones = listOf(
                    stringResource(R.string.sugerencias_sin_limite) to 0,
                    "10 g" to 10,
                    "20 g" to 20,
                    "30 g" to 30
                ),
                seleccionado = filtros.proteinasMinimas,
                onSeleccionar = onCambiarProteinas
            )
        }

        SeccionFiltro(titulo = stringResource(R.string.sugerencias_macros)) {
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RemyChip(
                    text = stringResource(R.string.sugerencias_alta_proteina),
                    selected = filtros.altaProteina,
                    onClick = onAlternarAltaProteina
                )
                RemyChip(
                    text = stringResource(R.string.sugerencias_baja_hidratos),
                    selected = filtros.bajaHidratos,
                    onClick = onAlternarBajaHidratos
                )
                RemyChip(
                    text = stringResource(R.string.sugerencias_baja_grasa),
                    selected = filtros.bajaGrasa,
                    onClick = onAlternarBajaGrasa
                )
            }
        }

        SeccionFiltro(titulo = stringResource(R.string.sugerencias_region)) {
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RemyChip(
                    text = stringResource(R.string.sugerencias_todas),
                    selected = filtros.region.isBlank(),
                    onClick = { onCambiarRegion("") }
                )
                regiones.forEach { region ->
                    RemyChip(
                        text = region,
                        selected = filtros.region == region,
                        onClick = { onCambiarRegion(region) }
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(AzulTinta.copy(alpha = 0.05f))
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Outlined.Restaurant,
                contentDescription = null,
                tint = Terracota,
                modifier = Modifier.size(20.dp)
            )
            Text(
                stringResource(R.string.sugerencias_encajan, total),
                style = MaterialTheme.typography.titleSmall,
                color = TextoPrincipal
            )
        }

        if (filtros.hayFiltros) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(PillShape)
                    .clickable(onClick = onLimpiarFiltros)
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    stringResource(R.string.sugerencias_limpiar),
                    style = MaterialTheme.typography.titleSmall,
                    color = Terracota
                )
            }
        }

        PrimaryButton(
            text = stringResource(R.string.sugerencias_ver),
            onClick = onMostrar
        )
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun SeccionFiltro(titulo: String, contenido: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            titulo,
            style = MaterialTheme.typography.titleMedium,
            color = TextoPrincipal
        )
        contenido()
    }
}

@Composable
private fun FilaOpciones(
    opciones: List<Pair<String, Int>>,
    seleccionado: Int,
    onSeleccionar: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        opciones.forEach { (etiqueta, valor) ->
            RemyChip(
                text = etiqueta,
                selected = valor == seleccionado,
                onClick = { onSeleccionar(valor) }
            )
        }
    }
}

@Composable
private fun StepperBoton(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(if (enabled) androidx.compose.ui.graphics.Color.White else BordeTarjeta)
    ) {
        Icon(
            icono,
            contentDescription = null,
            tint = if (enabled) AzulTinta else TextoSecundario,
            modifier = Modifier.size(20.dp)
        )
    }
}

// ---------------------------------------------------------------------------
// Preview
// ---------------------------------------------------------------------------

private fun recetaDemo(id: Long, nombre: String, minutos: Int, kcal: Int, porciones: Int = 4) = RecetaConIngredientes(
    receta = Receta(
        id = id,
        nombre = nombre,
        kcal = kcal,
        proteinas = 18,
        regionOrigen = "Mediterránea",
        tipoPlato = "Principal",
        tiempoMinutos = minutos,
        porciones = porciones
    ),
    ingredientes = emptyList()
)

@Preview(showBackground = true, backgroundColor = 0xFFFFFBF5, heightDp = 900)
@Composable
private fun SugerenciasScreenPreview() {
    REMYTheme {
        ContenidoSugerencias(
            filtros = FiltrosSugerencias(),
            grupos = GruposSugerencias(
                completas = listOf(
                    recetaDemo(1, "Arroz con verduras", 25, 420),
                    recetaDemo(2, "Tortilla de patatas", 40, 380)
                ),
                casiCocinables = listOf(
                    CasiCocinableVista(
                        receta = recetaDemo(3, "Pasta con tomate", 20, 450),
                        faltantes = listOf(
                            FaltanteVista(alimentoId = 1, nombre = "Tomate"),
                            FaltanteVista(alimentoId = 2, nombre = "Albahaca", insuficiente = true)
                        )
                    )
                )
            ),
            regiones = listOf("Mediterránea", "Europea"),
            onAlternarTipo = {},
            onCambiarMaximo = {},
            onAlternarFaltante = {},
            onCambiarTiempo = {},
            onCambiarCalorias = {},
            onCambiarProteinas = {},
            onAlternarAltaProteina = {},
            onAlternarBajaHidratos = {},
            onAlternarBajaGrasa = {},
            onCambiarRegion = {},
            onLimpiarFiltros = {},
            onRecetaClick = {},
            onIrADespensa = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFBF5, heightDp = 900)
@Composable
private fun PantallaFiltrosPreview() {
    REMYTheme {
        PantallaFiltros(
            filtros = FiltrosSugerencias(tiposDePlato = setOf("Principal")),
            grupos = GruposSugerencias(completas = listOf(recetaDemo(1, "Arroz con verduras", 25, 420))),
            regiones = listOf("Mediterránea", "Europea"),
            onAlternarTipo = {},
            onCambiarMaximo = {},
            onCambiarTiempo = {},
            onCambiarCalorias = {},
            onCambiarProteinas = {},
            onAlternarAltaProteina = {},
            onAlternarBajaHidratos = {},
            onAlternarBajaGrasa = {},
            onCambiarRegion = {},
            onLimpiarFiltros = {},
            onMostrar = {}
        )
    }
}
