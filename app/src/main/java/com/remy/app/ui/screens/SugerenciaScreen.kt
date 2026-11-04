package com.remy.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.remy.app.data.RecetaConIngredientes
import com.remy.app.ui.viewmodel.CasiCocinableVista
import com.remy.app.ui.viewmodel.GruposSugerencias
import com.remy.app.ui.viewmodel.PreferenciasViewModel
import com.remy.app.ui.viewmodel.SugerenciasViewModel

private val TIPOS_DE_PLATO = listOf("Entrante", "Principal", "Postre")

/**
 * Pantalla de sugerencias (#34): los dos grupos del algoritmo de #33 —
 * se puede cocinar ya y casi cocinables con sus faltantes — más los
 * filtros y el máximo persistentes de #29.
 */
@Composable
fun SugerenciasScreen(
    preferencias: PreferenciasViewModel = viewModel(factory = PreferenciasViewModel.Factory),
    sugerencias: SugerenciasViewModel = viewModel(factory = SugerenciasViewModel.Factory)
) {
    val filtrosActivos by preferencias.filtrosActivos.collectAsStateWithLifecycle()
    val maximoFaltantes by preferencias.maximoFaltantes.collectAsStateWithLifecycle()
    val grupos by sugerencias.grupos.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Sugerencias", style = MaterialTheme.typography.headlineMedium)

        // Preferencias de #29: los filtros se guardan y el máximo alimenta
        // al algoritmo de #33, que recalcula los grupos al cambiar
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Filtrar por tipo de plato", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TIPOS_DE_PLATO.forEach { tipo ->
                    FilterChip(
                        selected = tipo in filtrosActivos,
                        onClick = { preferencias.alternarFiltro(tipo) },
                        label = { Text(tipo) }
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                "Máximo de ingredientes faltantes: $maximoFaltantes",
                style = MaterialTheme.typography.titleMedium
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(
                    onClick = { preferencias.cambiarMaximoFaltantes((maximoFaltantes - 1).coerceAtLeast(0)) },
                    enabled = maximoFaltantes > 0
                ) { Text("−") }
                Text(
                    "$maximoFaltantes",
                    modifier = Modifier.padding(horizontal = 12.dp),
                    style = MaterialTheme.typography.headlineSmall
                )
                TextButton(
                    onClick = { preferencias.cambiarMaximoFaltantes((maximoFaltantes + 1).coerceAtMost(10)) },
                    enabled = maximoFaltantes < 10
                ) { Text("+") }
            }
        }

        if (!grupos.hayAlgunaSugerencia) {
            Text(
                "Aún no hay sugerencias: revisa los filtros o los ingredientes de la despensa.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Grupos(grupos)
        }
    }
}

@Composable
private fun Grupos(grupos: GruposSugerencias) {
    if (grupos.completas.isNotEmpty()) {
        Text(
            "Se puede cocinar ya (${grupos.completas.size})",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        grupos.completas.forEach { receta -> Tarjeta(receta) }
    }

    if (grupos.casiCocinables.isNotEmpty()) {
        Text(
            "Casi cocinable (${grupos.casiCocinables.size})",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.tertiary
        )
        grupos.casiCocinables.forEach { casi -> TarjetaCasi(casi) }
    }
}

@Composable
private fun Tarjeta(receta: RecetaConIngredientes) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(receta.receta.nombre, style = MaterialTheme.typography.titleMedium)
            Text(
                "${receta.receta.tiempoMinutos} min · ${receta.receta.tipoPlato} · ${receta.receta.regionOrigen}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun TarjetaCasi(casi: CasiCocinableVista) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(casi.receta.receta.nombre, style = MaterialTheme.typography.titleMedium)
            Text(
                "${casi.receta.receta.tiempoMinutos} min · ${casi.receta.receta.tipoPlato}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                "Falta: " + casi.faltantes.joinToString(", ") { it.nombre },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}
