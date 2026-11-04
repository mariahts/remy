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
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.remy.app.data.RecetaConIngredientes
import com.remy.app.ui.viewmodel.RecetasViewModel

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

/** Detalle de receta. De momento solo se navega hasta aquí; el contenido completo llega en #32. */
@Composable
fun RecetaDetalleScreen(id: Long) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Receta $id", style = MaterialTheme.typography.headlineMedium)
    }
}
