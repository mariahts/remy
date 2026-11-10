package com.remy.app.ui.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.remy.app.R
import com.remy.app.data.FiltrosSugerencias
import com.remy.app.data.RecetaConIngredientes
import com.remy.app.ui.EstadoCarga
import com.remy.app.ui.components.FotoReceta
import com.remy.app.ui.components.MetaPill
import com.remy.app.ui.components.PantallaCargando
import com.remy.app.ui.components.PantallaError
import com.remy.app.ui.components.PersonCircle
import com.remy.app.ui.components.RemyCta
import com.remy.app.ui.components.RemyHeaderSombra
import com.remy.app.ui.components.SectionCard
import com.remy.app.ui.components.categoriaDe
import com.remy.app.ui.components.emojiDe
import com.remy.app.ui.theme.PillShape
import com.remy.app.ui.theme.RemyShapes
import com.remy.app.ui.theme.Slate300
import com.remy.app.ui.viewmodel.CasiCocinableVista
import com.remy.app.ui.viewmodel.FaltanteVista
import com.remy.app.ui.viewmodel.GruposSugerencias
import com.remy.app.ui.viewmodel.PreferenciasViewModel
import com.remy.app.ui.viewmodel.SugerenciasViewModel

private val TIPOS_DE_PLATO = listOf("Entrante", "Principal", "Postre")

/**
 * Pantalla de sugerencias (#34): estado de configuración con filtros
 * persistentes de #29 (tipo de plato, máximo de faltantes) y, al pulsar
 * "Mostrar sugerencias", los resultados del algoritmo de #33 agrupados
 * en "se puede cocinar ya" y "casi cocinable" con sus faltantes.
 *
 * §2.6 (filtros) y §2.8 (resultados) del SPEC son dos vistas de la misma
 * pestaña; el botón "Mostrar sugerencias" / "Volver a filtros" alterna.
 */
@Composable
fun SugerenciasScreen(
    onRecetaClick: (Long) -> Unit = {},
    onIrADespensa: () -> Unit = {},
    onPerfilClick: () -> Unit = {},
    preferencias: PreferenciasViewModel = viewModel(factory = PreferenciasViewModel.Factory),
    sugerencias: SugerenciasViewModel = viewModel(factory = SugerenciasViewModel.Factory)
) {
    val filtros by preferencias.filtros.collectAsStateWithLifecycle()
    val estado by sugerencias.estado.collectAsStateWithLifecycle()
    val regiones by sugerencias.regiones.collectAsStateWithLifecycle()
    var viendoResultados by rememberSaveable { mutableStateOf(false) }

    when (val actual = estado) {
        is EstadoCarga.Cargando,
        is EstadoCarga.Error -> PantallaEstadoSugerencias(actual, sugerencias::reintentar, onPerfilClick)

        is EstadoCarga.Datos -> {
            if (viendoResultados) {
                PantallaResultados(
                    filtros = filtros,
                    grupos = actual.datos,
                    onVolverFiltros = { viendoResultados = false },
                    onRecetaClick = onRecetaClick,
                    onIrADespensa = onIrADespensa,
                    onPerfilClick = onPerfilClick
                )
            } else {
                PantallaFiltros(
                    filtros = filtros,
                    grupos = actual.datos,
                    regiones = regiones,
                    onAlternarTipo = preferencias::alternarTipoDePlato,
                    onCambiarMaximo = preferencias::cambiarMaximoFaltantes,
                    onAlternarFaltante = preferencias::alternarFaltantePermitido,
                    onCambiarTiempo = preferencias::cambiarTiempoMaximo,
                    onCambiarCalorias = preferencias::cambiarKcalMaximas,
                    onCambiarProteinas = preferencias::cambiarProteinasMinimas,
                    onCambiarRegion = preferencias::cambiarRegion,
                    onLimpiarFiltros = preferencias::limpiarFiltros,
                    onMostrar = { viendoResultados = true },
                    onPerfilClick = onPerfilClick
                )
            }
        }
    }
}

@Composable
private fun PantallaEstadoSugerencias(
    estado: EstadoCarga<*>,
    onReintentar: () -> Unit,
    onPerfilClick: () -> Unit = {}
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            RemyHeaderSombra(
                titulo = "Sugerencias",
                bg = MaterialTheme.colorScheme.background,
                logoSize = 32.dp,
                trailing = { PersonCircle(size = 32.dp, onClick = onPerfilClick) }
            )
        }
    ) { inner ->
        when (estado) {
            is EstadoCarga.Cargando -> PantallaCargando(
                mensaje = "Calculando tus sugerencias…",
                modifier = Modifier.padding(inner)
            )

            is EstadoCarga.Error -> PantallaError(
                mensaje = estado.mensaje,
                onReintentar = onReintentar,
                modifier = Modifier.padding(inner)
            )

            is EstadoCarga.Datos -> Unit
        }
    }
}

// ---------------------------------------------------------------------------
// §2.6 — Configuración de filtros
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
    onCambiarRegion: (String) -> Unit,
    onLimpiarFiltros: () -> Unit,
    onMostrar: () -> Unit,
    onPerfilClick: () -> Unit = {},
    onAlternarFaltante: (Long) -> Unit = {}
) {
    val total = grupos.completas.size + grupos.casiCocinables.size

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            RemyHeaderSombra(
                titulo = "Sugerencias",
                bg = MaterialTheme.colorScheme.background,
                logoSize = 32.dp,
                trailing = { PersonCircle(size = 32.dp, onClick = onPerfilClick) }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HeroPreferencias()

            if (filtros.hayFiltros) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Row(
                        modifier = Modifier
                            .clip(PillShape)
                            .clickable(onClick = onLimpiarFiltros)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.Icon(
                            Icons.Outlined.RestartAlt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            "Limpiar filtros",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            FiltroTipoDePlato(filtrosActivos = filtros.tiposDePlato, alternar = onAlternarTipo)

            FiltroMaxFaltantes(
                maximoFaltantes = filtros.maximoFaltantes,
                faltantesDisponibles = grupos.faltantesDisponibles,
                faltantesPermitidos = filtros.faltantesPermitidos,
                cambiarMaximo = onCambiarMaximo,
                alternarFaltante = onAlternarFaltante
            )

            FiltroOpciones(
                icono = Icons.Outlined.Schedule,
                fondoIcono = Color(0xFFF3E8FF),
                colorIcono = Color(0xFF8B5CF6),
                etiqueta = "Tiempo de preparación",
                titulo = "Tiempo máximo",
                seleccionado = filtros.tiempoMaximoMinutos,
                opciones = listOf(
                    "Sin límite" to 0,
                    "15 min" to 15,
                    "30 min" to 30,
                    "45 min" to 45,
                    "60 min" to 60
                ),
                resumen = if (filtros.tiempoMaximoMinutos > 0) "Hasta ${filtros.tiempoMaximoMinutos} min" else "Sin límite",
                onSeleccionar = onCambiarTiempo
            )

            FiltroOpciones(
                icono = Icons.Outlined.LocalFireDepartment,
                fondoIcono = Color(0xFFFFF7ED),
                colorIcono = Color(0xFFEA580C),
                etiqueta = "Aporte energético",
                titulo = "Calorías máximas",
                seleccionado = filtros.kcalMaximas,
                opciones = listOf(
                    "Sin límite" to 0,
                    "300 kcal" to 300,
                    "500 kcal" to 500,
                    "700 kcal" to 700
                ),
                resumen = if (filtros.kcalMaximas > 0) "Hasta ${filtros.kcalMaximas} kcal" else "Sin límite",
                onSeleccionar = onCambiarCalorias
            )

            FiltroOpciones(
                icono = Icons.Outlined.FitnessCenter,
                fondoIcono = Color(0xFFDCFCE7),
                colorIcono = Color(0xFF16A34A),
                etiqueta = "Macronutrientes",
                titulo = "Proteínas mínimas",
                seleccionado = filtros.proteinasMinimas,
                opciones = listOf(
                    "Sin límite" to 0,
                    "10 g" to 10,
                    "20 g" to 20,
                    "30 g" to 30
                ),
                resumen = if (filtros.proteinasMinimas > 0) "Desde ${filtros.proteinasMinimas} g" else "Sin límite",
                onSeleccionar = onCambiarProteinas
            )

            FiltroOpciones(
                icono = Icons.Outlined.Public,
                fondoIcono = Color(0xFFF0F9FF),
                colorIcono = Color(0xFF0284C7),
                etiqueta = "Procedencia",
                titulo = "Región",
                seleccionado = filtros.region,
                opciones = listOf("Todas" to "") + regiones.map { it to it },
                resumen = filtros.region.ifBlank { "Todas" },
                onSeleccionar = onCambiarRegion
            )

            // ChefMotivation (§2.6): cuenta real de recetas encajando
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RemyShapes.extraLarge)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.Icon(
                        Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        "$total recetas encajan con estos filtros",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "Aprovecharás tus ingredientes frescos hoy.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // CTA §2.6: h56 r16 con flecha
            RemyCta(
                text = "Mostrar sugerencias",
                onClick = onMostrar,
                modifier = Modifier.fillMaxWidth(),
                height = 56.dp,
                iconAfter = Icons.Filled.ArrowForward,
                fontSize = 14.sp
            )
        }
    }
}

/** HeroCard §2.6: píldora "INSPIRACIÓN A MEDIDA" + titular + bajada. */
@Composable
private fun HeroPreferencias() {
    SectionCard(
        shape = RemyShapes.large,
        bg = MaterialTheme.colorScheme.surfaceVariant,
        border = null,
        glow = true,
        padding = PaddingValues(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // círculo decorativo esquina inferior derecha (mockup: blur 24px)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(96.dp)
                    .padding(0.dp)
                    .offset(x = 34.dp, y = 34.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f))
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .clip(PillShape)
                        .background(Color.White)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material3.Icon(
                        Icons.Outlined.Tune,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        "INSPIRACIÓN A MEDIDA",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "Filtra tus recetas ideales",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    "Configura tus preferencias antes de descubrir qué cocinar hoy con tu despensa.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

/** Card "Tipo de plato" (§2.6 fila 2) con chips funcionales de #29. */
@Composable
private fun FiltroTipoDePlato(
    filtrosActivos: Set<String>,
    alternar: (String) -> Unit
) {
    var expandido by rememberSaveable { mutableStateOf(true) }

    SectionCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RemyShapes.extraLarge,
        bg = Color.White,
        border = null,
        padding = PaddingValues(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CuadroIcono(44.dp, Color(0xFFFFF7ED), Icons.Outlined.Restaurant, Color(0xFFEA580C))
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            "Categoría culinaria",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text("Tipo de plato", style = MaterialTheme.typography.titleMedium)
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (filtrosActivos.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .clip(PillShape)
                                .background(MaterialTheme.colorScheme.background)
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            androidx.compose.material3.Icon(
                                Icons.Outlined.Restaurant,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (filtrosActivos.size == 1) filtrosActivos.first()
                                else "${filtrosActivos.size} activos",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                    BotonMini(
                        icon = if (expandido) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                        onClick = { expandido = !expandido }
                    )
                }
            }

            if (expandido) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RemyShapes.medium)
                        .background(MaterialTheme.colorScheme.background)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Los filtros quedan guardados: se recuperan al volver a abrir la app.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TIPOS_DE_PLATO.forEach { tipo ->
                            FiltroChip(
                                label = tipo,
                                active = tipo in filtrosActivos,
                                onClick = { alternar(tipo) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Ingredientes que pueden faltar o no tener cantidad suficiente. */
@Composable
private fun FiltroMaxFaltantes(
    maximoFaltantes: Int,
    faltantesDisponibles: List<FaltanteVista>,
    faltantesPermitidos: Set<Long>,
    cambiarMaximo: (Int) -> Unit,
    alternarFaltante: (Long) -> Unit
) {
    var expandido by rememberSaveable { mutableStateOf(true) }

    SectionCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RemyShapes.extraLarge,
        bg = Color.White,
        border = null,
        padding = PaddingValues(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CuadroIcono(44.dp, Color(0xFFF0F9FF), Icons.Outlined.Tune, Color(0xFF0284C7))
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            "Ingredientes prescindibles",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text("Máx. faltantes", style = MaterialTheme.typography.titleMedium)
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .clip(PillShape)
                            .background(MaterialTheme.colorScheme.background)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            "Máx $maximoFaltantes",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    BotonMini(
                        icon = if (expandido) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                        onClick = { expandido = !expandido }
                    )
                }
            }

            if (expandido) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RemyShapes.medium)
                        .background(MaterialTheme.colorScheme.background)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BotonMini(
                            icon = Icons.Outlined.Remove,
                            enabled = maximoFaltantes > 0,
                            onClick = { cambiarMaximo((maximoFaltantes - 1).coerceAtLeast(0)) }
                        )
                        Text(
                            "$maximoFaltantes",
                            modifier = Modifier.width(48.dp),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        BotonMini(
                            icon = Icons.Outlined.Add,
                            enabled = maximoFaltantes < 10,
                            onClick = { cambiarMaximo((maximoFaltantes + 1).coerceAtMost(10)) }
                        )
                        Text(
                            "de 10",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        "Elige qué ingredientes pueden faltar o no tener la cantidad exacta. Si no eliges ninguno, se permiten todos hasta el máximo indicado.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (faltantesDisponibles.isEmpty()) {
                        Text(
                            "Ahora mismo no hay ingredientes que falten o estén incompletos para las recetas disponibles.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            faltantesDisponibles.forEach { faltante ->
                                val activo = faltante.alimentoId in faltantesPermitidos
                                val texto = if (faltante.insuficiente) {
                                    "${faltante.nombre} · cantidad insuficiente"
                                } else {
                                    "${faltante.nombre} · te falta"
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RemyShapes.small)
                                        .background(if (activo) MaterialTheme.colorScheme.primaryContainer else Color.White)
                                        .border(
                                            1.dp,
                                            if (activo) MaterialTheme.colorScheme.primary else Color(0xFFE2E8F0),
                                            RemyShapes.small
                                        )
                                        .clickable { alternarFaltante(faltante.alimentoId) }
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        texto,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (activo) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(if (activo) MaterialTheme.colorScheme.primary else Color.Transparent)
                                            .border(
                                                1.5.dp,
                                                if (activo) MaterialTheme.colorScheme.primary else Slate300,
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (activo) {
                                            Icon(
                                                Icons.Outlined.Check,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Card genérica de un filtro de opción única (#37), plegada por defecto. */
@Composable
private fun <T> FiltroOpciones(
    icono: ImageVector,
    fondoIcono: Color,
    colorIcono: Color,
    etiqueta: String,
    titulo: String,
    seleccionado: T,
    opciones: List<Pair<String, T>>,
    resumen: String,
    onSeleccionar: (T) -> Unit
) {
    var expandido by rememberSaveable { mutableStateOf(false) }

    SectionCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RemyShapes.extraLarge,
        bg = Color.White,
        border = null,
        padding = PaddingValues(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandido = !expandido },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CuadroIcono(44.dp, fondoIcono, icono, colorIcono)
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            etiqueta,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(titulo, style = MaterialTheme.typography.titleMedium)
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .clip(PillShape)
                            .background(MaterialTheme.colorScheme.background)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            resumen,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                    BotonMini(
                        icon = if (expandido) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                        onClick = { expandido = !expandido }
                    )
                }
            }

            if (expandido) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RemyShapes.medium)
                        .background(MaterialTheme.colorScheme.background)
                        .horizontalScroll(rememberScrollState())
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    opciones.forEach { (label, valor) ->
                        FiltroChip(
                            label = label,
                            active = valor == seleccionado,
                            onClick = { onSeleccionar(valor) }
                        )
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// §2.8 — Resultados
// ---------------------------------------------------------------------------
@Composable
private fun PantallaResultados(
    filtros: FiltrosSugerencias,
    grupos: GruposSugerencias,
    onVolverFiltros: () -> Unit,
    onRecetaClick: (Long) -> Unit,
    onIrADespensa: () -> Unit,
    onPerfilClick: () -> Unit = {}
) {
    // "Completo" = lista completa (100% match + casi listas);
    // "Incompleto" = solo las recetas a las que les falta algo.
    var verCompleto by rememberSaveable { mutableStateOf(true) }

    val completasVisibles = if (verCompleto) grupos.completas else emptyList()
    val casiVisibles = grupos.casiCocinables
    val mostradas = completasVisibles.size + casiVisibles.size
    val etiquetas = buildList {
        filtros.tiposDePlato.sorted().forEach { add(it to Icons.Outlined.Restaurant) }
        if (filtros.tiempoMaximoMinutos > 0) {
            add("Hasta ${filtros.tiempoMaximoMinutos} min" to Icons.Outlined.Schedule)
        }
        if (filtros.kcalMaximas > 0) {
            add("Hasta ${filtros.kcalMaximas} kcal" to Icons.Outlined.LocalFireDepartment)
        }
        if (filtros.proteinasMinimas > 0) {
            add("Desde ${filtros.proteinasMinimas} g de proteína" to Icons.Outlined.FitnessCenter)
        }
        if (filtros.region.isNotBlank()) add(filtros.region to Icons.Outlined.Public)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        topBar = {
            TopBarResultados(onVolverFiltros = onVolverFiltros, onPerfilClick = onPerfilClick)
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            // Filtros activos (§2.8) + botón "Ajustar"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (etiquetas.isEmpty()) {
                        MetaPill(
                            text = "Sin filtros",
                            bg = Color(0xFFF1F5F9),
                            fg = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        etiquetas.forEach { (texto, icono) ->
                            MetaPill(
                                text = texto,
                                icon = icono,
                                tint = MaterialTheme.colorScheme.primary,
                                bg = Color(0xFFF1F5F9),
                                fg = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    MetaPill(
                        text = "Máx ${filtros.maximoFaltantes} faltantes",
                        icon = Icons.Filled.Bolt,
                        tint = MaterialTheme.colorScheme.primary,
                        bg = MaterialTheme.colorScheme.primaryContainer,
                        fg = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Row(
                    modifier = Modifier
                        .clip(PillShape)
                        .clickable(onClick = onVolverFiltros)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material3.Icon(
                        Icons.Outlined.Tune,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        "Ajustar",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Segmentado Completo / Incompleto
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Segmento(
                    label = "Completo",
                    active = verCompleto,
                    modifier = Modifier.weight(1f),
                    onClick = { verCompleto = true }
                )
                Segmento(
                    label = "Incompleto",
                    active = !verCompleto,
                    modifier = Modifier.weight(1f),
                    onClick = { verCompleto = false }
                )
            }

            if (!grupos.hayAlgunaSugerencia) {
                EstadoVacioResultados(onVolverFiltros = onVolverFiltros, onIrADespensa = onIrADespensa)
                return@Column
            }

            // Contador
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.Icon(
                        Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    "$mostradas recetas sugeridas",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            // Sección "100% MATCH" (título implícito en el mockup)
            if (completasVisibles.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                    completasVisibles.forEach { receta ->
                        TarjetaSugerida(
                            receta = receta,
                            esCompleta = true,
                            faltantes = emptyList(),
                            onRecetaClick = onRecetaClick
                        )
                    }
                }
            }

            // Sección "Casi listas"
            if (casiVisibles.isNotEmpty()) {
                EncabezadoCasi(total = casiVisibles.size)

                Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                    casiVisibles.forEach { casi ->
                        TarjetaSugerida(
                            receta = casi.receta,
                            esCompleta = false,
                            faltantes = casi.faltantes,
                            onRecetaClick = onRecetaClick
                        )
                    }
                }

                if (!verCompleto && completasVisibles.isEmpty()) {
                    Text(
                        "Si quitas ingredientes faltantes de tu despensa, aparecerán aquí recetas 100% listas.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                ChefTipResultados()
            }

            if (!verCompleto && casiVisibles.isEmpty()) {
                Text(
                    "Todas las sugerencias actuales están completas: puedes cocinarlas ya.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // CTA final (§2.8): añadir alimentos → pestaña Despensa
            if (casiVisibles.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier
                            .clip(PillShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable(onClick = onIrADespensa)
                            .padding(horizontal = 24.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.Icon(
                            Icons.Filled.AddCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "Añadir alimentos a tu despensa (+${casiVisibles.size} recetas)",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

/** TopBar de resultados (§2.8): volver a filtros + logo/REMY + persona. */
@Composable
private fun TopBarResultados(onVolverFiltros: () -> Unit, onPerfilClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .height(40.dp)
                .clip(PillShape)
                .background(Color(0xFFF1F5F9))
                .clickable(onClick = onVolverFiltros)
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.Icon(
                Icons.Filled.KeyboardArrowLeft,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                "Volver a filtros",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }
        Image(
            painter = painterResource(R.drawable.remy_logo),
            contentDescription = "Logotipo de REMY",
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
        )
        Column {
            Text(
                "REMY",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF22C55E),
                letterSpacing = 1.6.sp
            )
            Text("Sugerencias", style = MaterialTheme.typography.titleLarge)
        }
        Spacer(Modifier.weight(1f))
        PersonCircle(size = 32.dp, onClick = onPerfilClick)
    }
}

/** Encabezado "Casi listas (Ingredientes prescindibles)" (§2.8). */
@Composable
private fun EncabezadoCasi(total: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF1F5F9)),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.Icon(
                        Icons.Outlined.Info,
                        contentDescription = null,
                        tint = Color(0xFFD97706),
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    "Casi listas (Ingredientes prescindibles)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            MetaPill(
                text = "Falta opcional ($total)",
                bg = Color(0xFFF1F5F9),
                fg = Color(0xFFD97706),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Text(
            "Recetas cuyo sabor principal y estructura están cubiertos por tu despensa; solo faltan toppings, hierbas frescas o aderezos secundarios que puedes omitir o sustituir fácilmente.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 17.sp
        )
    }
}

/** Tarjeta de receta sugerida (§2.8): foto con gradiente, macros, chips, CTA. */
@Composable
private fun TarjetaSugerida(
    receta: RecetaConIngredientes,
    esCompleta: Boolean,
    faltantes: List<FaltanteVista>,
    onRecetaClick: (Long) -> Unit
) {
    val datos = receta.receta
    var marcada by rememberSaveable(datos.id) { mutableStateOf(false) }
    val ingredientes = receta.ingredientes

    SectionCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RemyShapes.medium,
        bg = Color.White,
        border = null,
        padding = PaddingValues.Zero
    ) {
        // Foto + gradiente overlay + badges (§2.8)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(176.dp)
                .clip(RemyShapes.medium)
        ) {
            FotoReceta(nombre = datos.nombre, modifier = Modifier.fillMaxSize())
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.20f),
                                Color.Black.copy(alpha = 0.70f)
                            )
                        )
                    )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (datos.tiempoMinutos > 0) {
                    MetaPill(
                        text = "${datos.tiempoMinutos} min",
                        icon = Icons.Outlined.Schedule,
                        tint = MaterialTheme.colorScheme.primary,
                        bg = Color.White.copy(alpha = 0.9f),
                        fg = MaterialTheme.colorScheme.onSurface
                    )
                } else {
                    Spacer(Modifier.size(1.dp))
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            if (marcada) MaterialTheme.colorScheme.primary
                            else Color.White.copy(alpha = 0.9f)
                        )
                        .clickable { marcada = !marcada },
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.Icon(
                        Icons.Filled.Bookmark,
                        contentDescription = "Guardar receta",
                        tint = if (marcada) Color.White else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(datos.nombre, style = MaterialTheme.typography.titleLarge, lineHeight = 20.sp)

            // Macros
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetaPill(
                    text = "${datos.kcal} kcal",
                    icon = Icons.Outlined.LocalFireDepartment,
                    tint = Color(0xFFD97706),
                    bg = Color(0xFFF1F5F9),
                    fg = MaterialTheme.colorScheme.onSurface
                )
                MetaPill(
                    text = "${datos.proteinas}g Proteína",
                    icon = Icons.Outlined.FitnessCenter,
                    tint = MaterialTheme.colorScheme.primary,
                    bg = Color(0xFFF1F5F9),
                    fg = MaterialTheme.colorScheme.onSurface
                )
                if (!esCompleta && datos.tiempoMinutos > 0) {
                    MetaPill(
                        text = "Listo en ${datos.tiempoMinutos} min",
                        icon = Icons.Filled.Bolt,
                        tint = MaterialTheme.colorScheme.primary,
                        bg = MaterialTheme.colorScheme.primaryContainer,
                        fg = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            if (ingredientes.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ingredientes.take(4).forEach { ing ->
                        ChipResultado(nombre = ing.alimento?.nombre ?: "Ingrediente")
                    }
                }
            }

            if (!esCompleta && faltantes.isNotEmpty()) {
                AvisoFaltantes(faltantes)
            }

            RemyCta(
                text = "Ver receta",
                onClick = { onRecetaClick(datos.id) },
                modifier = Modifier.fillMaxWidth(),
                height = 44.dp,
                iconAfter = Icons.Filled.ArrowForward,
                fontSize = 13.sp
            )
        }
    }
}

/** Chip blanco con punto de color (§2.8 IngredientChips). */
@Composable
private fun ChipResultado(nombre: String) {
    val cat = categoriaDe(nombre)
    Row(
        modifier = Modifier
            .clip(PillShape)
            .background(Color.White)
            .border(1.dp, Color(0x4D64748B), PillShape)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(cat.colors.fg)
        )
        Text(
            nombre,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/** WarningBox (§2.8): faltantes prescindibles de la receta casi cocinable. */
@Composable
private fun AvisoFaltantes(faltantes: List<FaltanteVista>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RemyShapes.small)
            .background(Color(0xFFF1F5F9))
            .border(1.dp, Color(0x8064748B), RemyShapes.small)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                androidx.compose.material3.Icon(
                    Icons.Outlined.Warning,
                    contentDescription = null,
                    tint = Color(0xFFD97706),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    "Te falta o no tienes suficiente: ${faltantes.size}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFD97706),
                    maxLines = 2
                )
            }
            Text(
                "PENDIENTE",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFD97706),
                fontWeight = FontWeight.Bold
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RemyShapes.small)
                .background(Color.White)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            faltantes.forEach { f ->
                val motivo = if (f.insuficiente) "cantidad insuficiente" else "te falta"
                Text(
                    text = "${emojiDe(f.nombre)} ${f.nombre} · $motivo",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Text(
            "Puedes elegir en los filtros qué ingredientes permitir cuando falten o no tengas la cantidad exacta.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** ChefTip de resultados (§2.8). */
@Composable
private fun ChefTipResultados() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RemyShapes.medium)
            .background(Color(0xFFF1F5F9))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.material3.Icon(
                Icons.Outlined.Lightbulb,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "CONSEJO DEL CHEF REMY",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF22C55E),
                    letterSpacing = 0.8.sp,
                    fontWeight = FontWeight.Bold
                )
                androidx.compose.material3.Icon(
                    Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp)
                )
            }
            Text(
                "Los ingredientes prescindibles como hierbas frescas o especias aromáticas pueden sustituirse por una pizca de orégano seco, pimienta negra o un golpe de calor al final para intensificar los sabores de tu despensa sin gastar de más.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 17.sp
            )
        }
    }
}

/** Estado vacío de resultados. */
@Composable
private fun EstadoVacioResultados(onVolverFiltros: () -> Unit, onIrADespensa: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Aún no hay sugerencias: revisa los filtros o los ingredientes de tu despensa.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            RemyCta(text = "Revisar filtros", onClick = onVolverFiltros, height = 44.dp, fontSize = 13.sp)
            RemyCta(text = "Ir a la despensa", onClick = onIrADespensa, height = 44.dp, fontSize = 13.sp)
        }
    }
}

// ---------------------------------------------------------------------------
// Utilidades compartidas
// ---------------------------------------------------------------------------

/** Cuadro de icono 44×44 r12 de las FilterCards (§2.6). */
@Composable
private fun CuadroIcono(size: Dp, bg: Color, icono: ImageVector, tint: Color) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .background(bg),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.Icon(
            icono,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
    }
}

/** Botón circular 32×32 (expand/collapse y steppers). */
@Composable
private fun BotonMini(
    icon: ImageVector,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(PillShape)
            .background(Color(0xFFF1F5F9))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.Icon(
            icon,
            contentDescription = null,
            tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.size(18.dp)
        )
    }
}

/** Chip de filtro activo/inactivo (misma pinta que FilterPill, 32dp). */
@Composable
private fun FiltroChip(label: String, active: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .height(32.dp)
            .clip(PillShape)
            .background(if (active) MaterialTheme.colorScheme.primary else Color.White)
            .border(
                1.dp,
                if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
                PillShape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            color = if (active) Color.White else MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (active) FontWeight.Bold else FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

/** Segmento Completo / Incompleto (§2.8 ToggleSegment). */
@Composable
private fun Segmento(label: String, active: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .height(40.dp)
            .clip(PillShape)
            .background(if (active) MaterialTheme.colorScheme.primary else Color.White)
            .then(
                if (active) Modifier
                else Modifier.border(1.dp, Color(0x8064748B), PillShape)
            )
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            color = if (active) Color.White else MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
    }
}
