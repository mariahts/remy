package com.remy.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Esquinas redondeadas según SPEC-FRONT §1.3:
// card pequeña 8 · estándar 12 · media 16 · grande/FAB 16–24 · sheet 28–40 · diálogo 24
val RemyShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),   // filas internas, micro-badges
    small = RoundedCornerShape(8.dp),        // mini-cards, teclas de numpad
    medium = RoundedCornerShape(12.dp),      // cards estándar, inputs
    large = RoundedCornerShape(16.dp),       // cards media, FAB
    extraLarge = RoundedCornerShape(24.dp)   // cards grandes de alimento/receta, diálogos
)

// Forma de "píldora": botones CTA, chips, buscador, pestañas activas.
val PillShape = RoundedCornerShape(percent = 50)

// Radios de hoja inferior (3 variantes del mockup) y diálago.
val SheetShape28 = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
val SheetShape32 = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
val SheetShape40 = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
val DialogShape24 = RoundedCornerShape(24.dp)
