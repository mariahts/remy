package com.remy.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Esquinas muy redondeadas en todo el diseño.
val RemyShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),   // etiquetas pequeñas
    small = RoundedCornerShape(12.dp),       // teclas del teclado numérico
    medium = RoundedCornerShape(16.dp),      // elementos internos de tarjetas
    large = RoundedCornerShape(24.dp),       // tarjetas de alimentos y recetas
    extraLarge = RoundedCornerShape(28.dp)   // hojas inferiores y diálogos
)

// Forma de "píldora": botones, chips, buscador, pestañas de la barra inferior.
val PillShape = RoundedCornerShape(percent = 50)