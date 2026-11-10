package com.remy.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.remy.app.ui.EstadoCarga
import com.remy.app.ui.theme.Green600
import com.remy.app.ui.theme.Red100
import com.remy.app.ui.theme.Red600
import com.remy.app.ui.theme.Slate600
import com.remy.app.ui.theme.Slate900

/**
 * Pinta el estado de una consulta asíncrona (#46): espera, error con reintento
 * o el contenido real. Así ninguna pantalla se queda en blanco mientras Room
 * responde.
 */
@Composable
fun <T> ContenidoConEstado(
    estado: EstadoCarga<T>,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier,
    mensajeCargando: String = "Cargando…",
    contenido: @Composable (T) -> Unit
) {
    when (estado) {
        is EstadoCarga.Cargando -> PantallaCargando(mensajeCargando, modifier)
        is EstadoCarga.Error -> PantallaError(estado.mensaje, onReintentar, modifier)
        is EstadoCarga.Datos -> contenido(estado.datos)
    }
}

@Composable
fun PantallaCargando(mensaje: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(color = Green600, strokeWidth = 3.dp)
            Text(
                mensaje,
                style = MaterialTheme.typography.bodyMedium,
                color = Slate600,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun PantallaError(mensaje: String, onReintentar: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Red100),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.Warning,
                    contentDescription = null,
                    tint = Red600,
                    modifier = Modifier.size(36.dp)
                )
            }
            Text(
                "No se han podido cargar los datos",
                style = MaterialTheme.typography.titleLarge,
                color = Slate900,
                textAlign = TextAlign.Center
            )
            Text(
                mensaje,
                style = MaterialTheme.typography.bodyMedium,
                color = Slate600,
                textAlign = TextAlign.Center
            )
            RemyCta(
                text = "Reintentar",
                onClick = onReintentar,
                icon = Icons.Outlined.Refresh,
                fontSize = 14.sp
            )
        }
    }
}
