package com.remy.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.remy.app.ui.viewmodel.PreferenciasViewModel

private val TIPOS_DE_PLATO = listOf("Entrante", "Principal", "Postre")

@Composable
fun SugerenciasScreen(viewModel: PreferenciasViewModel = viewModel(factory = PreferenciasViewModel.Factory)) {
    val filtrosActivos by viewModel.filtrosActivos.collectAsStateWithLifecycle()
    val maximoFaltantes by viewModel.maximoFaltantes.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Sugerencias", style = MaterialTheme.typography.headlineMedium)

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Filtrar por tipo de plato", style = MaterialTheme.typography.titleMedium)
            Text(
                "Los filtros quedan guardados: se recuperan al volver a abrir la app.",
                style = MaterialTheme.typography.bodySmall
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TIPOS_DE_PLATO.forEach { tipo ->
                    FilterChip(
                        selected = tipo in filtrosActivos,
                        onClick = { viewModel.alternarFiltro(tipo) },
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
                    onClick = { viewModel.cambiarMaximoFaltantes((maximoFaltantes - 1).coerceAtLeast(0)) },
                    enabled = maximoFaltantes > 0
                ) { Text("−") }
                Text(
                    "$maximoFaltantes",
                    modifier = Modifier.padding(horizontal = 12.dp),
                    style = MaterialTheme.typography.headlineSmall
                )
                TextButton(
                    onClick = { viewModel.cambiarMaximoFaltantes((maximoFaltantes + 1).coerceAtMost(10)) },
                    enabled = maximoFaltantes < 10
                ) { Text("+") }
            }
        }
    }
}
