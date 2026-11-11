package com.remy.app.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.remy.app.R
import com.remy.app.data.Receta
import com.remy.app.data.RecetaConIngredientes
import com.remy.app.data.SupabaseClientProvider
import com.remy.app.ui.components.ContenidoConEstado
import com.remy.app.ui.components.PrimaryButton
import com.remy.app.ui.components.RecipePhoto
import com.remy.app.ui.components.RemyCard
import com.remy.app.ui.components.RemyTopBar
import com.remy.app.ui.theme.AzulTinta
import com.remy.app.ui.theme.BordeTarjeta
import com.remy.app.ui.theme.FondoCrema
import com.remy.app.ui.theme.Melocoton
import com.remy.app.ui.theme.REMYTheme
import com.remy.app.ui.theme.SuperficieBlanca
import com.remy.app.ui.theme.Terracota
import com.remy.app.ui.theme.TextoPrincipal
import com.remy.app.ui.theme.TextoSecundario
import com.remy.app.ui.viewmodel.CocinadasViewModel
import io.github.jan.supabase.auth.auth
import java.io.File
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.Locale

@Composable
fun InicioScreen(
    onRecetaClick: (Long) -> Unit,
    onPerfilClick: () -> Unit = {},
    onIrADespensa: () -> Unit = {},
    onVerSugerencias: () -> Unit = {},
    viewModel: CocinadasViewModel = viewModel(factory = CocinadasViewModel.Factory),
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val nombre = nombreDeUsuario()

    Column(modifier = Modifier.fillMaxSize().background(FondoCrema)) {
        RemyTopBar(
            titulo = stringResource(R.string.inicio_titulo),
            onAvatarClick = onPerfilClick
        )
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            ContenidoConEstado(
                estado = estado,
                onReintentar = viewModel::reintentar,
                mensajeCargando = stringResource(R.string.inicio_cargando)
            ) { datos ->
                ContenidoInicio(
                    nombre = nombre,
                    cocinadas = datos.cocinadas,
                    sugerencia = datos.sugerencia,
                    onRecetaClick = onRecetaClick,
                    onIrADespensa = onIrADespensa,
                    onVerSugerencias = onVerSugerencias
                )
            }
        }
    }
}

@Composable
private fun ContenidoInicio(
    nombre: String,
    cocinadas: List<RecetaConIngredientes>,
    sugerencia: RecetaConIngredientes?,
    onRecetaClick: (Long) -> Unit,
    onIrADespensa: () -> Unit,
    onVerSugerencias: () -> Unit
) {
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

        if (sugerencia != null) {
            item(key = "hero") {
                HeroReceta(
                    receta = sugerencia,
                    onCocinar = { onRecetaClick(sugerencia.receta.id) }
                )
            }
        }

        item(key = "atajos") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AtajoCard(
                    texto = stringResource(R.string.inicio_anadir_despensa),
                    icono = Icons.Outlined.Kitchen,
                    onClick = onIrADespensa,
                    modifier = Modifier.weight(1f)
                )
                AtajoCard(
                    texto = stringResource(R.string.inicio_ver_sugerencias),
                    icono = Icons.Outlined.AutoAwesome,
                    onClick = onVerSugerencias,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item(key = "semana") {
            TiraSemanal(cocinadas = cocinadas)
        }

        if (cocinadas.isNotEmpty()) {
            item(key = "recientes-titulo") {
                Text(
                    text = stringResource(R.string.inicio_recientes),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextoPrincipal,
                    fontWeight = FontWeight.Bold
                )
            }
            item(key = "recientes") {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(cocinadas, key = { it.receta.id }) { item ->
                        TarjetaCocinada(
                            receta = item,
                            onClick = { onRecetaClick(item.receta.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroReceta(receta: RecetaConIngredientes, onCocinar: () -> Unit) {
    RemyCard(modifier = Modifier.fillMaxWidth(), padding = 0.dp) {
        Box {
            RecipePhoto(
                model = receta.receta.fotoCocinada?.let { File(it) },
                nombre = receta.receta.nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp),
                shape = RectangleShape
            )
            Box(
                modifier = Modifier
                    .padding(14.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Terracota)
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text(
                    text = stringResource(R.string.inicio_receta_hoy),
                    style = MaterialTheme.typography.labelSmall,
                    color = androidx.compose.ui.graphics.Color.White,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = receta.receta.nombre,
                style = MaterialTheme.typography.headlineSmall,
                color = TextoPrincipal,
                fontWeight = FontWeight.ExtraBold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                MetaTexto(
                    icono = Icons.Outlined.Schedule,
                    texto = stringResource(R.string.comun_minutos, receta.receta.tiempoMinutos)
                )
                MetaTexto(
                    icono = Icons.Outlined.Whatshot,
                    texto = stringResource(R.string.comun_kcal, receta.receta.kcal)
                )
            }
            Spacer(Modifier.height(2.dp))
            PrimaryButton(
                text = stringResource(R.string.inicio_cocinar_ahora),
                onClick = onCocinar,
                icon = Icons.AutoMirrored.Filled.ArrowForward
            )
        }
    }
}

@Composable
private fun MetaTexto(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    texto: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(icono, contentDescription = null, tint = TextoSecundario, modifier = Modifier.size(16.dp))
        Text(texto, style = MaterialTheme.typography.bodyMedium, color = TextoSecundario)
    }
}

@Composable
private fun AtajoCard(
    texto: String,
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
    }
}

@Composable
private fun TiraSemanal(cocinadas: List<RecetaConIngredientes>) {
    val hoy = LocalDate.now()
    val zona = ZoneId.systemDefault()
    val diasCocinados = cocinadas.mapNotNull { item ->
        item.receta.fechaCocinada?.let { fechaLocal(it, zona) }
    }.toSet()

    RemyCard(modifier = Modifier.fillMaxWidth(), padding = 16.dp) {
        Text(
            text = stringResource(R.string.inicio_semana),
            style = MaterialTheme.typography.titleMedium,
            color = TextoPrincipal,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            (6 downTo 0).forEach { diasAtras ->
                val dia = hoy.minusDays(diasAtras.toLong())
                DiaSemana(
                    dia = dia,
                    esHoy = diasAtras == 0,
                    cocinado = dia in diasCocinados
                )
            }
        }
    }
}

@Composable
private fun DiaSemana(dia: LocalDate, esHoy: Boolean, cocinado: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = inicialDia(dia.dayOfWeek.value),
            style = MaterialTheme.typography.labelSmall,
            color = if (esHoy) Terracota else TextoSecundario,
            fontWeight = if (esHoy) FontWeight.ExtraBold else FontWeight.SemiBold
        )
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(if (esHoy) AzulTinta else SuperficieBlanca)
                .then(
                    if (!esHoy) {
                        Modifier.background(SuperficieBlanca)
                    } else {
                        Modifier
                    }
                )
                .padding(0.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (esHoy) AzulTinta else SuperficieBlanca),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = dia.dayOfMonth.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    color = if (esHoy) androidx.compose.ui.graphics.Color.White else TextoPrincipal,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(if (cocinado) Terracota else androidx.compose.ui.graphics.Color.Transparent)
        )
    }
}

@Composable
private fun TarjetaCocinada(receta: RecetaConIngredientes, onClick: () -> Unit) {
    RemyCard(
        modifier = Modifier.width(150.dp),
        onClick = onClick,
        padding = 10.dp
    ) {
        RecipePhoto(
            model = receta.receta.fotoCocinada?.let { File(it) },
            nombre = receta.receta.nombre,
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp),
            shape = RoundedCornerShape(16.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = receta.receta.nombre,
            style = MaterialTheme.typography.titleSmall,
            color = TextoPrincipal,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private fun inicialDia(dia: Int): String = when (dia) {
    1 -> "L"
    2 -> "M"
    3 -> "X"
    4 -> "J"
    5 -> "V"
    6 -> "S"
    else -> "D"
}

@Composable
private fun nombreDeUsuario(): String {
    val usuario = SupabaseClientProvider.client.auth.currentUserOrNull()
    val nombre = usuario?.userMetadata?.get("full_name")?.toString()?.trim('"')
        ?: usuario?.userMetadata?.get("name")?.toString()?.trim('"')
        ?: usuario?.email?.substringBefore('@')
    return nombre?.takeIf { it.isNotBlank() } ?: "cocinero"
}

private fun fechaLocal(millis: Long, zona: ZoneId): LocalDate =
    java.time.Instant.ofEpochMilli(millis).atZone(zona).toLocalDate()

@Composable
private fun saludoSegunHora(): String = when (LocalTime.now().hour) {
    in 5..11 -> stringResource(R.string.inicio_buenos_dias)
    in 12..19 -> stringResource(R.string.inicio_buenas_tardes)
    else -> stringResource(R.string.inicio_buenas_noches)
}

@Preview(name = "Inicio", showBackground = true, backgroundColor = 0xFFFFFBF5)
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
            tiempoMinutos = 35
        ),
        ingredientes = emptyList()
    )
    REMYTheme {
        Column(modifier = Modifier.fillMaxSize().background(FondoCrema)) {
            RemyTopBar(titulo = "Mi cocina")
            ContenidoInicio(
                nombre = "María",
                cocinadas = listOf(receta),
                sugerencia = receta,
                onRecetaClick = {},
                onIrADespensa = {},
                onVerSugerencias = {}
            )
        }
    }
}
