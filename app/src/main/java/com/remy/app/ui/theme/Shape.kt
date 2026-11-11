package com.remy.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Rediseño: tarjetas 24 dp, botones y chips en píldora.
val RemyShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),      // tarjetas
    extraLarge = RoundedCornerShape(24.dp)  // tarjetas grandes / diálogos
)

// Forma de píldora: botones CTA, chips, buscador, pestañas activas.
val PillShape = RoundedCornerShape(percent = 50)

// Compatibilidad con hojas/diálogos existentes
val SheetShape28 = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
val SheetShape32 = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
val SheetShape40 = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
val DialogShape24 = RoundedCornerShape(24.dp)
