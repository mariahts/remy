package com.remy.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.remy.app.R
import com.remy.app.ui.theme.Green600

/**
 * Cabecera de pantalla con el logotipo de REMY (mockup Stitch):
 * badge circular con el ratón cocinero, etiqueta "REMY" en verde
 * esmeralda y título de pantalla debajo. [trailing] se alinea a la
 * derecha (p. ej. [PersonCircle]).
 */
@Composable
fun RemyHeader(
    titulo: String,
    modifier: Modifier = Modifier,
    logoSize: Dp = 48.dp,
    trailing: @Composable () -> Unit = {}
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.remy_logo),
            contentDescription = "Logotipo de REMY",
            modifier = Modifier.size(logoSize).clip(CircleShape)
        )
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(
                text = "REMY",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 2.sp
            )
            Text(titulo, style = MaterialTheme.typography.headlineMedium)
        }
        Spacer(Modifier.weight(1f))
        trailing()
    }
}

/** Círculo de perfil verde con icono de persona (mockup: Circle 32/36 #16A34A). */
@Composable
fun PersonCircle(
    size: Dp = 32.dp,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Green600)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Outlined.Person,
            contentDescription = "Perfil",
            tint = androidx.compose.ui.graphics.Color.White,
            modifier = Modifier.size(size * 0.55f)
        )
    }
}

/** Cabecera con fondo y sombra inferior (TopAppBar de los mockups §2.7/§2.8). */
@Composable
fun RemyHeaderSombra(
    titulo: String,
    bg: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
    logoSize: Dp = 40.dp,
    trailing: @Composable () -> Unit = {}
) {
    RemyHeader(
        titulo = titulo,
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RectangleShape, ambientColor = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.04f), spotColor = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.06f))
            .background(bg)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        logoSize = logoSize,
        trailing = trailing
    )
}
