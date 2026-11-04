package com.remy.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.remy.app.data.RecetaConIngredientes
import com.remy.app.data.RecetaDetalle
import com.remy.app.ui.viewmodel.RecetaDetalleViewModel
import com.remy.app.ui.viewmodel.RecetasViewModel

private fun Double.formato(): String = if (this % 1.0 == 0.0) toLong().toString() else toString()

@Composable
fun RecetasScreen(
    onRecetaClick: (Long) -> Unit,
    viewModel: RecetasViewModel = viewModel(factory = RecetasViewModel.Factory),
) {
    val recetas by viewModel.recetas.collectAsStateWithLifecycle()

    if (recetas.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Aún no hay recetas", style = MaterialTheme.typography.titleMedium)
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(recetas, key = { it.receta.id }) { item ->
                TarjetaReceta(item) { onRecetaClick(item.receta.id) }
            }
        }
    }
}

@Composable
private fun TarjetaReceta(item: RecetaConIngredientes, onClick: () -> Unit) {
    val receta = item.receta
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp).clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(receta.nombre, style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("${receta.tiempoMinutos} min", style = MaterialTheme.typography.bodyMedium)
                Text("·", style = MaterialTheme.typography.bodyMedium)
                Text(receta.tipoPlato, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

/** Detalle de receta: ingredientes, pasos, tiempo y nutrición. */
@Composable
fun RecetaDetalleScreen(
    id: Long,
    onVolver: () -> Unit,
    viewModel: RecetaDetalleViewModel = viewModel(factory = RecetaDetalleViewModel.Factory),
) {
    LaunchedEffect(id) { viewModel.cargar(id) }
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        TextButton(onClick = onVolver, modifier = Modifier.padding(start = 8.dp, top = 8.dp)) {
            Text("← Volver al listado")
        }

        when (val actual = estado) {
            RecetaDetalleViewModel.Estado.Cargando -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Cargando…", style = MaterialTheme.typography.bodyLarge)
                }
            }

            RecetaDetalleViewModel.Estado.NoEncontrada -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se encontró esta receta", style = MaterialTheme.typography.titleMedium)
                }
            }

            is RecetaDetalleViewModel.Estado.Encontrada -> {
                DetalleReceta(actual.detalle, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun DetalleReceta(detalle: RecetaDetalle, modifier: Modifier = Modifier) {
    val receta = detalle.receta
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(receta.nombre, style = MaterialTheme.typography.headlineSmall)
        Text(
            "${receta.tiempoMinutos} min · ${receta.tipoPlato} · ${receta.regionOrigen}",
            style = MaterialTheme.typography.bodyMedium
        )

        Text("Nutrición", style = MaterialTheme.typography.titleMedium)
        Text(
            "${receta.kcal} kcal · ${receta.proteinas} g de proteínas",
            style = MaterialTheme.typography.bodyLarge
        )

        Text("Ingredientes", style = MaterialTheme.typography.titleMedium)
        if (detalle.ingredientes.isEmpty()) {
            Text("Sin ingredientes registrados", style = MaterialTheme.typography.bodyMedium)
        } else {
            detalle.ingredientes.forEach { item ->
                val alimento = item.alimento?.nombre ?: "Alimento ${item.ingrediente.alimentoId}"
                Text(
                    "· ${item.ingrediente.cantidad.formato()} ${item.ingrediente.unidad} de $alimento",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Text("Pasos", style = MaterialTheme.typography.titleMedium)
        if (receta.pasos.isEmpty()) {
            Text("Sin pasos registrados", style = MaterialTheme.typography.bodyMedium)
        } else {
            receta.pasos.forEachIndexed { indice, paso ->
                Text("${indice + 1}. $paso", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
