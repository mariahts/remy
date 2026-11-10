package com.remy.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.remy.app.ui.components.FotoDelPlato
import com.remy.app.ui.components.FotoReceta
import com.remy.app.ui.components.MetaPill
import com.remy.app.ui.components.PersonCircle
import com.remy.app.ui.components.RemyCta
import com.remy.app.ui.components.RemyHeaderSombra
import com.remy.app.ui.components.SectionCard
import com.remy.app.ui.components.fechaEnEspanol
import com.remy.app.ui.theme.Amber600
import com.remy.app.ui.theme.Green100
import com.remy.app.ui.theme.Green200
import com.remy.app.ui.theme.Green600
import com.remy.app.ui.theme.Green900
import com.remy.app.ui.theme.PillShape
import com.remy.app.ui.theme.RemyShapes
import com.remy.app.ui.theme.Slate100
import com.remy.app.ui.theme.Slate600
import com.remy.app.ui.theme.Slate900
import com.remy.app.ui.viewmodel.CocinadasViewModel

/**
 * Historial de recetas cocinadas (#44): foto del plato, nombre y fecha de la
 * última cocción. Se alimenta del mismo Room que el detalle, así que al marcar
 * una receta como cocinada aparece aquí sin recargar nada.
 */
@Composable
fun CocinadasScreen(
    onRecetaClick: (Long) -> Unit,
    onIrARecetas: () -> Unit,
    viewModel: CocinadasViewModel = viewModel(factory = CocinadasViewModel.Factory),
) {
    val cocinadas by viewModel.cocinadas.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().background(Green100)) {
        RemyHeaderSombra(
            titulo = "Cocinadas",
            bg = Green100,
            logoSize = 40.dp,
            trailing = { PersonCircle(36.dp) }
        )

        if (cocinadas.isEmpty()) {
            CocinadasVacias(onIrARecetas = onIrARecetas)
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item(key = "contador") { ContadorCocinadas(cocinadas.size) }
                items(cocinadas, key = { it.receta.id }) { item ->
                    TarjetaCocinada(item) { onRecetaClick(item.receta.id) }
                }
            }
        }
    }
}

@Composable
private fun ContadorCocinadas(n: Int) {
    Row(
        modifier = Modifier
            .clip(PillShape)
            .background(Green200)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "$n ${if (n == 1) "receta cocinada" else "recetas cocinadas"}",
            style = MaterialTheme.typography.labelMedium,
            color = Green900
        )
    }
}

@Composable
private fun TarjetaCocinada(item: RecetaConIngredientes, onClick: () -> Unit) {
    val receta = item.receta

    SectionCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RemyShapes.extraLarge,
        border = null,
        glow = false,
        padding = PaddingValues(0.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(176.dp)) {
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(receta.nombre, style = MaterialTheme.typography.titleLarge, color = Slate900)

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

            if (receta.kcal > 0) {
                MetaPill(
                    "${receta.kcal} kcal",
                    icon = Icons.Outlined.LocalFireDepartment,
                    tint = Amber600,
                    bg = Slate100,
                    fg = Slate900
                )
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
private fun CocinadasVacias(onIrARecetas: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(Green200),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.Restaurant,
                    contentDescription = null,
                    tint = Green600,
                    modifier = Modifier.size(44.dp)
                )
            }
            Text(
                "Aún no has cocinado ninguna receta",
                style = MaterialTheme.typography.titleLarge,
                color = Slate900,
                textAlign = TextAlign.Center
            )
            Text(
                "Marca una receta como cocinada desde su detalle y aquí guardaremos la foto y la fecha.",
                style = MaterialTheme.typography.bodyMedium,
                color = Slate600,
                textAlign = TextAlign.Center
            )
            RemyCta(
                text = "Ver mis recetas",
                onClick = onIrARecetas,
                icon = Icons.Outlined.MenuBook,
                fontSize = 14.sp
            )
        }
    }
}
