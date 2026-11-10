package com.remy.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import java.time.LocalTime
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.remy.app.data.RecetaConIngredientes
import io.github.jan.supabase.auth.auth
import com.remy.app.ui.components.ContenidoConEstado
import com.remy.app.ui.components.FotoDelPlato
import com.remy.app.ui.components.FotoReceta
import com.remy.app.ui.components.PersonCircle
import com.remy.app.ui.components.RemyCta
import com.remy.app.ui.components.RemyHeaderSombra
import com.remy.app.ui.components.SectionCard
import com.remy.app.ui.components.fechaEnEspanol
import com.remy.app.ui.theme.Green100
import com.remy.app.ui.theme.Green200
import com.remy.app.ui.theme.Green600
import com.remy.app.ui.theme.Green700
import com.remy.app.ui.theme.Green900
import com.remy.app.ui.theme.PillShape
import com.remy.app.ui.theme.RemyShapes
import com.remy.app.ui.theme.Slate100
import com.remy.app.ui.theme.Slate400
import com.remy.app.ui.theme.Slate500
import com.remy.app.ui.theme.Slate600
import com.remy.app.ui.theme.Slate900
import com.remy.app.ui.viewmodel.CocinadasViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

/**
 * Pantalla de inicio con calendario y registro de recetas cocinadas.
 * Los días con recetas marcadas se señalan en el calendario y, al
 * seleccionar un día, se muestra el registro de lo cocinado.
 */
@Composable
fun InicioScreen(
    onRecetaClick: (Long) -> Unit,
    onPerfilClick: () -> Unit = {},
    viewModel: CocinadasViewModel = viewModel(factory = CocinadasViewModel.Factory),
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().background(Green100)) {
        RemyHeaderSombra(
            titulo = "Inicio",
            bg = Green100,
            logoSize = 40.dp,
            trailing = { PersonCircle(36.dp, onClick = onPerfilClick) }
        )

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            ContenidoConEstado(
                estado = estado,
                onReintentar = viewModel::reintentar,
                mensajeCargando = "Cargando tu calendario…"
            ) { cocinadas ->
                ContenidoInicio(cocinadas = cocinadas, onRecetaClick = onRecetaClick)
            }
        }
    }
}

@Composable
private fun ContenidoInicio(
    cocinadas: List<RecetaConIngredientes>,
    onRecetaClick: (Long) -> Unit,
) {
    val zona = ZoneId.systemDefault()
    val porDia = remember(cocinadas) {
        cocinadas
            .mapNotNull { item -> item.receta.fechaCocinada?.let { item to fechaLocal(it, zona) } }
            .groupBy({ it.second }, { it.first })
    }

    val hoy = LocalDate.now(zona)
    var mesVisible by rememberSaveable { mutableStateOf(YearMonth.from(hoy)) }
    var diaSeleccionado by rememberSaveable { mutableStateOf(hoy.toString()) }
    val seleccionado = runCatching { LocalDate.parse(diaSeleccionado) }.getOrDefault(hoy)

    val recetasDelDia = porDia[seleccionado].orEmpty()
    val usuario = com.remy.app.data.SupabaseClientProvider.client.auth.currentUserOrNull()
    val nombre = usuario?.userMetadata?.get("full_name")?.toString()?.trim('"')
        ?: usuario?.userMetadata?.get("name")?.toString()?.trim('"')
        ?: usuario?.email?.substringBefore('@')
        ?: "María"
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(key = "saludo") {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = saludoSegunHora(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Slate600
                )
                Text(
                    text = nombre,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Slate900,
                    fontWeight = FontWeight.Bold
                )
                if (usuario != null) {
                    TextButton(onClick = {
                        scope.launch {
                            com.remy.app.data.SupabaseClientProvider.client.auth.signOut()
                        }
                    }) {
                        Text("Cerrar sesión", color = Green600)
                    }
                }
            }
        }

        item(key = "calendario") {
            CalendarioMensual(
                mes = mesVisible,
                hoy = hoy,
                seleccionado = seleccionado,
                diasConRegistro = porDia.keys,
                onMesAnterior = { mesVisible = mesVisible.minusMonths(1) },
                onMesSiguiente = { mesVisible = mesVisible.plusMonths(1) },
                onSeleccionarDia = { diaSeleccionado = it.toString() }
            )
        }

        item(key = "titulo-registro") {
            Text(
                "Registro del día",
                style = MaterialTheme.typography.titleMedium,
                color = Slate900
            )
        }

        if (recetasDelDia.isEmpty()) {
            item(key = "vacio") { RegistroVacio(seleccionado) }
        } else {
            items(recetasDelDia, key = { "reg-${it.receta.id}-${seleccionado}" }) { item ->
                RegistroCocinado(item = item, onClick = { onRecetaClick(item.receta.id) })
            }
        }
    }
}

@Composable
private fun CalendarioMensual(
    mes: YearMonth,
    hoy: LocalDate,
    seleccionado: LocalDate,
    diasConRegistro: Set<LocalDate>,
    onMesAnterior: () -> Unit,
    onMesSiguiente: () -> Unit,
    onSeleccionarDia: (LocalDate) -> Unit,
) {
    val locale = Locale.forLanguageTag("es")
    val nombreMes = mes.month.getDisplayName(TextStyle.FULL, locale).replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(locale) else it.toString()
    }

    SectionCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RemyShapes.extraLarge,
        padding = PaddingValues(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onMesAnterior) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Mes anterior", tint = Green700)
                }
                Text(
                    "$nombreMes ${mes.year}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Slate900,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(onClick = onMesSiguiente) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Mes siguiente", tint = Green700)
                }
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("L", "M", "X", "J", "V", "S", "D").forEach { dia ->
                    Text(
                        text = dia,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate500,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            val primerDiaMes = mes.atDay(1)
            val desplazamiento = primerDiaMes.dayOfWeek.value - 1 // lunes = 0
            val diasMes = mes.lengthOfMonth()
            val totalCeldas = ((desplazamiento + diasMes + 6) / 7) * 7

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                for (fila in 0 until totalCeldas step 7) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        for (columna in 0 until 7) {
                            val indice = fila + columna
                            val numeroDia = indice - desplazamiento + 1
                            if (numeroDia in 1..diasMes) {
                                val fecha = mes.atDay(numeroDia)
                                DiaCalendario(
                                    fecha = fecha,
                                    esHoy = fecha == hoy,
                                    seleccionado = fecha == seleccionado,
                                    tieneRegistro = fecha in diasConRegistro,
                                    onClick = { onSeleccionarDia(fecha) },
                                    modifier = Modifier.weight(1f)
                                )
                            } else {
                                Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DiaCalendario(
    fecha: LocalDate,
    esHoy: Boolean,
    seleccionado: Boolean,
    tieneRegistro: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val fondo = when {
        seleccionado -> Green600
        esHoy -> Green200
        else -> androidx.compose.ui.graphics.Color.Transparent
    }
    val colorTexto = when {
        seleccionado -> androidx.compose.ui.graphics.Color.White
        esHoy -> Green900
        else -> Slate900
    }

    Column(
        modifier = modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(fondo)
            .then(
                if (esHoy && !seleccionado) Modifier.border(1.dp, Green600, CircleShape) else Modifier
            )
            .clickable(onClick = onClick)
            .padding(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = fecha.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodySmall,
            color = colorTexto,
            fontWeight = if (seleccionado || esHoy) FontWeight.Bold else FontWeight.Medium
        )
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .size(5.dp)
                .clip(CircleShape)
                .background(
                    when {
                        tieneRegistro && seleccionado -> androidx.compose.ui.graphics.Color.White
                        tieneRegistro -> Green600
                        else -> androidx.compose.ui.graphics.Color.Transparent
                    }
                )
        )
    }
}

@Composable
private fun RegistroVacio(dia: LocalDate) {
    SectionCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RemyShapes.large,
        padding = PaddingValues(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Green200),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Restaurant, contentDescription = null, tint = Green600)
            }
            Text(
                "Sin recetas cocinadas",
                style = MaterialTheme.typography.titleSmall,
                color = Slate900
            )
            Text(
                "No hay ningún registro para este día.",
                style = MaterialTheme.typography.bodySmall,
                color = Slate600,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun RegistroCocinado(item: RecetaConIngredientes, onClick: () -> Unit) {
    val receta = item.receta
    SectionCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RemyShapes.extraLarge,
        border = null,
        glow = false,
        padding = PaddingValues(0.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
            if (receta.fotoCocinada != null) {
                FotoDelPlato(
                    ruta = receta.fotoCocinada,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                FotoReceta(receta.nombre, Modifier.fillMaxSize())
            }
        }
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(receta.nombre, style = MaterialTheme.typography.titleMedium, color = Slate900)
            receta.fechaCocinada?.let { fecha ->
                Row(
                    modifier = Modifier
                        .clip(PillShape)
                        .background(Slate100)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Cocinada el ${fechaEnEspanol(fecha)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Green600,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            RemyCta(
                text = "Ver receta",
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                height = 44.dp,
                iconAfter = Icons.AutoMirrored.Filled.ArrowForward,
                fontSize = 14.sp
            )
        }
    }
}

private fun fechaLocal(millis: Long, zona: ZoneId): LocalDate =
    Instant.ofEpochMilli(millis).atZone(zona).toLocalDate()

private fun saludoSegunHora(): String = when (LocalTime.now().hour) {
    in 6..11 -> "Buenos días"
    in 12..19 -> "Buenas tardes"
    else -> "Buenas noches"
}
