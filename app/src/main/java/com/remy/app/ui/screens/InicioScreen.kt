package com.remy.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Kitchen
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.remy.app.R
import com.remy.app.data.Receta
import com.remy.app.data.RecetaConIngredientes
import com.remy.app.data.SupabaseClientProvider
import com.remy.app.data.TipoComida
import com.remy.app.ui.components.ContenidoConEstado
import com.remy.app.ui.components.PrimaryButton
import com.remy.app.ui.components.RecipePhoto
import com.remy.app.ui.components.RemyCard
import com.remy.app.ui.components.RemyTopBar
import com.remy.app.ui.components.StatusBadge
import com.remy.app.ui.components.VarianteBadge
import com.remy.app.ui.components.mesEnEspanol
import com.remy.app.ui.theme.AzulTinta
import com.remy.app.ui.theme.BordeTarjeta
import com.remy.app.ui.theme.FondoCrema
import com.remy.app.ui.theme.Melocoton
import com.remy.app.ui.theme.NaranjaAviso
import com.remy.app.ui.theme.REMYTheme
import com.remy.app.ui.theme.SuperficieBlanca
import com.remy.app.ui.theme.Terracota
import com.remy.app.ui.theme.TextoPrincipal
import com.remy.app.ui.theme.TextoSecundario
import com.remy.app.ui.viewmodel.DisponibilidadReceta
import com.remy.app.ui.viewmodel.PlanDia
import com.remy.app.ui.viewmodel.PlanViewModel
import io.github.jan.supabase.auth.auth
import java.io.File
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale

@Composable
fun InicioScreen(
    onRecetaClick: (Long) -> Unit,
    onPerfilClick: () -> Unit = {},
    onIrADespensa: () -> Unit = {},
    onVerSugerencias: () -> Unit = {},
    planViewModel: PlanViewModel = viewModel(factory = PlanViewModel.Factory)
) {
    val estado by planViewModel.estado.collectAsStateWithLifecycle()
    val nombre = nombreDeUsuario()
    var tipoSheet by remember { mutableStateOf<TipoComida?>(null) }

    // Asegurar que el día seleccionado arranque en hoy al entrar.
    LaunchedEffect(Unit) { planViewModel.seleccionarDia(LocalDate.now()) }

    Column(modifier = Modifier.fillMaxSize().background(FondoCrema)) {
        RemyTopBar(
            titulo = stringResource(R.string.inicio_titulo),
            onAvatarClick = onPerfilClick
        )
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            ContenidoConEstado(
                estado = estado,
                onReintentar = { /* El flujo se reintenta al cambiar de día; forzamos hoy */ planViewModel.seleccionarDia(LocalDate.now()) },
                mensajeCargando = stringResource(R.string.inicio_cargando)
            ) { planDia ->
                val hoy = LocalDate.now()
                ContenidoInicio(
                    nombre = nombre,
                    planDia = planDia,
                    hoy = hoy,
                    onRecetaClick = onRecetaClick,
                    onIrADespensa = onIrADespensa,
                    onVerSugerencias = onVerSugerencias,
                    onDiaClick = { planViewModel.seleccionarDia(it) },
                    onAbrirSheet = { tipoSheet = it }
                )

                tipoSheet?.let { tipo ->
                    AnadirPlanSheet(
                        tipoInicial = tipo,
                        recetas = planDia.recetasParaElegir,
                        onDismiss = { tipoSheet = null },
                        onAnadir = { tipoElegido, recetaId ->
                            planViewModel.planificar(tipoElegido, recetaId)
                            tipoSheet = null
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ContenidoInicio(
    nombre: String,
    planDia: PlanDia,
    hoy: LocalDate,
    onRecetaClick: (Long) -> Unit,
    onIrADespensa: () -> Unit,
    onVerSugerencias: () -> Unit,
    onDiaClick: (LocalDate) -> Unit,
    onAbrirSheet: (TipoComida) -> Unit
) {
    val recetaHoy = recetaDeHoy(planDia, hoy)
    val listas = planDia.listasParaCocinar

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item(key = "saludo") {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = saludoSegunHora(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextoSecundario
                )
                Text(
                    text = stringResource(R.string.inicio_saludo, nombre),
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextoPrincipal,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        recetaHoy?.let { (receta, disponibilidad) ->
            item(key = "hero") {
                HeroRecetaHoy(
                    receta = receta,
                    disponibilidad = disponibilidad,
                    onCocinar = { onRecetaClick(receta.receta.id) }
                )
            }
        }

        item(key = "atajos") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AtajoInicio(
                    texto = stringResource(R.string.inicio_anadir_despensa),
                    subtitulo = null,
                    icono = Icons.Outlined.Kitchen,
                    onClick = onIrADespensa,
                    modifier = Modifier.weight(1f)
                )
                AtajoInicio(
                    texto = stringResource(R.string.inicio_ver_sugerencias),
                    subtitulo = if (listas == 1) stringResource(R.string.inicio_lista_para_cocinar)
                    else stringResource(R.string.inicio_listas_para_cocinar, listas),
                    icono = Icons.Outlined.AutoAwesome,
                    onClick = onVerSugerencias,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item(key = "semana") {
            SemanaPlan(
                hoy = hoy,
                seleccionado = planDia.fecha,
                diasConPlan = planDia.diasConPlan,
                onDiaClick = onDiaClick
            )
        }

        item(key = "plan-dia") {
            PlanDelDia(
                planDia = planDia,
                onRecetaClick = onRecetaClick,
                onAnadir = onAbrirSheet
            )
        }
    }
}

private fun recetaDeHoy(
    planDia: PlanDia,
    @Suppress("UNUSED_PARAMETER") hoy: LocalDate
): Pair<RecetaConIngredientes, DisponibilidadReceta>? {
    // Prioridad: comida planificada para hoy (comida > desayuno > cena), si no, sugerencia.
    val orden = listOf(TipoComida.COMIDA, TipoComida.DESAYUNO, TipoComida.CENA)
    val planificada = orden.firstNotNullOfOrNull { tipo ->
        planDia.comidasHoy[tipo]?.let { it.plan.receta to it.disponibilidad }
    }
    if (planificada != null) return planificada
    // Sugerencia: primera lista para cocinar, o la primera disponible.
    val candidata = planDia.recetasParaElegir.firstOrNull() ?: return null
    return candidata.first to candidata.second
}

@Composable
private fun HeroRecetaHoy(
    receta: RecetaConIngredientes,
    disponibilidad: DisponibilidadReceta,
    onCocinar: () -> Unit
) {
    RemyCard(modifier = Modifier.fillMaxWidth(), padding = 0.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RecipePhoto(
                model = receta.receta.fotoCocinada?.let { File(it) },
                nombre = receta.receta.nombre,
                modifier = Modifier.size(96.dp),
                shape = RoundedCornerShape(18.dp)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                StatusBadge(
                    text = when (disponibilidad) {
                        is DisponibilidadReceta.Lista -> stringResource(R.string.inicio_receta_lista)
                        is DisponibilidadReceta.Faltan ->
                            stringResource(R.string.sugerencias_badge_faltan, disponibilidad.cantidad)
                    },
                    variante = when (disponibilidad) {
                        is DisponibilidadReceta.Lista -> VarianteBadge.LISTA
                        is DisponibilidadReceta.Faltan -> VarianteBadge.FALTAN
                    }
                )
                Text(
                    text = receta.receta.nombre,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextoPrincipal,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Meta(icono = Icons.Outlined.Schedule, texto = stringResource(R.string.comun_minutos, receta.receta.tiempoMinutos))
                    val porciones = receta.receta.porciones.coerceAtLeast(1)
                    Meta(
                        icono = Icons.Outlined.Whatshot,
                        texto = stringResource(R.string.comun_kcal_racion, kotlin.math.round(receta.receta.kcal.toFloat() / porciones).toInt())
                    )
                }
            }
        }
        Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = 0.dp).padding(bottom = 14.dp)) {
            PrimaryButton(
                text = stringResource(R.string.inicio_cocinar_ahora),
                onClick = onCocinar,
                icon = Icons.AutoMirrored.Filled.ArrowForward
            )
        }
    }
}

@Composable
private fun Meta(icono: androidx.compose.ui.graphics.vector.ImageVector, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(icono, contentDescription = null, tint = TextoSecundario, modifier = Modifier.size(15.dp))
        Text(texto, style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
    }
}

@Composable
private fun AtajoInicio(
    texto: String,
    subtitulo: String?,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    RemyCard(modifier = modifier, onClick = onClick, padding = 16.dp) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Melocoton),
            contentAlignment = Alignment.Center
        ) {
            Icon(icono, contentDescription = null, tint = AzulTinta, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = texto,
            style = MaterialTheme.typography.titleSmall,
            color = TextoPrincipal,
            fontWeight = FontWeight.Bold,
            maxLines = 2
        )
        if (subtitulo != null) {
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitulo,
                style = MaterialTheme.typography.bodySmall,
                color = TextoSecundario
            )
        }
    }
}

@Composable
private fun SemanaPlan(
    hoy: LocalDate,
    seleccionado: LocalDate,
    diasConPlan: Set<LocalDate>,
    onDiaClick: (LocalDate) -> Unit
) {
    val inicio = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val dias = (0..6).map { inicio.plusDays(it.toLong()) }

    RemyCard(modifier = Modifier.fillMaxWidth(), padding = 16.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.inicio_semana),
                style = MaterialTheme.typography.titleMedium,
                color = TextoPrincipal,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = mesEnEspanol(hoy.month),
                style = MaterialTheme.typography.labelMedium,
                color = TextoSecundario
            )
        }
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            dias.forEach { dia ->
                val esHoy = dia == hoy
                val esSeleccionado = dia == seleccionado
                val tienePlan = dia in diasConPlan
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onDiaClick(dia) }
                        .padding(horizontal = 2.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = inicialDia(dia.dayOfWeek),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (esHoy) AzulTinta else TextoSecundario,
                        fontWeight = if (esHoy) FontWeight.ExtraBold else FontWeight.SemiBold
                    )
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (esHoy) AzulTinta else if (esSeleccionado) Melocoton else SuperficieBlanca)
                            .border(
                                width = if (!esHoy && !esSeleccionado) 1.dp else 0.dp,
                                color = BordeTarjeta,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = dia.dayOfMonth.toString(),
                            style = MaterialTheme.typography.labelLarge,
                            color = if (esHoy) Color.White else TextoPrincipal,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (tienePlan) Terracota else Color.Transparent)
                    )
                }
            }
        }
    }
}

private fun inicialDia(dia: DayOfWeek): String = when (dia) {
    DayOfWeek.MONDAY -> "L"
    DayOfWeek.TUESDAY -> "M"
    DayOfWeek.WEDNESDAY -> "X"
    DayOfWeek.THURSDAY -> "J"
    DayOfWeek.FRIDAY -> "V"
    DayOfWeek.SATURDAY -> "S"
    DayOfWeek.SUNDAY -> "D"
}

@Composable
private fun PlanDelDia(
    planDia: PlanDia,
    onRecetaClick: (Long) -> Unit,
    onAnadir: (TipoComida) -> Unit
) {
    val filas = listOf(
        Triple(TipoComida.DESAYUNO, R.string.inicio_desayuno, R.string.inicio_anadir_desayuno),
        Triple(TipoComida.COMIDA, R.string.inicio_comida, R.string.inicio_anadir_comida),
        Triple(TipoComida.CENA, R.string.inicio_cena, R.string.inicio_anadir_cena)
    )
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        filas.forEach { (tipo, labelRes, anadirRes) ->
            val comida = planDia.comidaDe(tipo)
            if (comida != null) {
                FilaComidaPlanificada(
                    etiqueta = stringResource(labelRes),
                    receta = comida.plan.receta,
                    disponibilidad = comida.disponibilidad,
                    onClick = { onRecetaClick(comida.plan.receta.receta.id) }
                )
            } else {
                FilaComidaVacia(
                    etiqueta = stringResource(labelRes),
                    textoAnadir = stringResource(anadirRes),
                    onClick = { onAnadir(tipo) }
                )
            }
        }
    }
}

@Composable
private fun FilaComidaPlanificada(
    etiqueta: String,
    receta: RecetaConIngredientes,
    disponibilidad: DisponibilidadReceta,
    onClick: () -> Unit
) {
    val datos = receta.receta
    RemyCard(modifier = Modifier.fillMaxWidth(), onClick = onClick, padding = 14.dp) {
        Text(etiqueta, style = MaterialTheme.typography.labelSmall, color = TextoSecundario, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RecipePhoto(
                model = datos.fotoCocinada?.let { File(it) },
                nombre = datos.nombre,
                modifier = Modifier.size(52.dp),
                shape = RoundedCornerShape(14.dp)
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = datos.nombre,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextoPrincipal,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
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
            }
            AvisoDisponibilidad(disponibilidad)
        }
    }
}

@Composable
private fun AvisoDisponibilidad(disponibilidad: DisponibilidadReceta) {
    when (disponibilidad) {
        is DisponibilidadReceta.Lista -> Text(
            text = stringResource(R.string.inicio_lista),
            style = MaterialTheme.typography.labelMedium,
            color = AzulTinta,
            fontWeight = FontWeight.Bold
        )
        is DisponibilidadReceta.Faltan -> Text(
            text = stringResource(R.string.inicio_faltan, disponibilidad.cantidad),
            style = MaterialTheme.typography.labelMedium,
            color = NaranjaAviso,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun FilaComidaVacia(etiqueta: String, textoAnadir: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(
                BorderStroke(1.dp, BordeTarjeta),
                RoundedCornerShape(20.dp)
            )
            .background(SuperficieBlanca.copy(alpha = 0.6f))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(etiqueta, style = MaterialTheme.typography.labelSmall, color = TextoSecundario, fontWeight = FontWeight.Bold)
            Text(
                text = textoAnadir,
                style = MaterialTheme.typography.titleSmall,
                color = Terracota,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun nombreDeUsuario(): String {
    val usuario = SupabaseClientProvider.client.auth.currentUserOrNull()
    val nombre = usuario?.userMetadata?.get("full_name")?.toString()?.trim('"')
        ?: usuario?.userMetadata?.get("name")?.toString()?.trim('"')
        ?: usuario?.email?.substringBefore('@')
    return nombre?.takeIf { it.isNotBlank() } ?: "cocinero"
}

@Composable
private fun saludoSegunHora(): String = when (LocalTime.now().hour) {
    in 5..11 -> stringResource(R.string.inicio_buenos_dias)
    in 12..19 -> stringResource(R.string.inicio_buenas_tardes)
    else -> stringResource(R.string.inicio_buenas_noches)
}

@Preview(name = "Inicio", showBackground = true, backgroundColor = 0xFFFFFBF5, heightDp = 1000)
@Composable
private fun PreviewInicio() {
    val receta = RecetaConIngredientes(
        receta = Receta(
            id = 1,
            nombre = "Arroz con pollo",
            kcal = 520,
            proteinas = 32,
            regionOrigen = "Mediterráneo",
            tipoPlato = "Principal",
            tiempoMinutos = 35,
            porciones = 4
        ),
        ingredientes = emptyList()
    )
    val hoy = LocalDate.now()
    val plan = PlanDia(
        fecha = hoy,
        comidas = mapOf(
            TipoComida.COMIDA to com.remy.app.ui.viewmodel.ComidaPlanificadaVista(
                plan = com.remy.app.data.ComidaPlanificadaConReceta(
                    plan = com.remy.app.data.PlannedMeal(id = 1, fecha = hoy.toString(), tipoComida = TipoComida.COMIDA, recetaId = 1),
                    receta = receta
                ),
                disponibilidad = DisponibilidadReceta.Lista
            )
        ),
        diasConPlan = setOf(hoy, hoy.plusDays(2)),
        recetasParaElegir = listOf(receta to DisponibilidadReceta.Lista)
    )
    REMYTheme {
        Column(modifier = Modifier.fillMaxSize().background(FondoCrema)) {
            RemyTopBar(titulo = "Mi cocina")
            ContenidoInicio(
                nombre = "María",
                planDia = plan,
                hoy = hoy,
                onRecetaClick = {},
                onIrADespensa = {},
                onVerSugerencias = {},
                onDiaClick = {},
                onAbrirSheet = {}
            )
        }
    }
}
