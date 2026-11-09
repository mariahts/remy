package com.remy.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.remy.app.R

// Plus Jakarta Sans (variable) — la familia del HTML de Stitch.
// Los .ttf variables están en res/font/; se instancian con FontVariation por peso.
private val RemyFontFamily = FontFamily(
    Font(
        R.font.plus_jakarta_sans,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(FontVariation.weight(400))
    ),
    Font(
        R.font.plus_jakarta_sans,
        weight = FontWeight.Medium,
        variationSettings = FontVariation.Settings(FontVariation.weight(500))
    ),
    Font(
        R.font.plus_jakarta_sans,
        weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(FontVariation.weight(600))
    ),
    Font(
        R.font.plus_jakarta_sans,
        weight = FontWeight.Bold,
        variationSettings = FontVariation.Settings(FontVariation.weight(700))
    ),
    Font(
        R.font.plus_jakarta_sans,
        weight = FontWeight.ExtraBold,
        variationSettings = FontVariation.Settings(FontVariation.weight(800))
    ),
)

// Escala del mockup convertida (sp ≈ px × 0.85) — SPEC-FRONT §1.2:
// display-lg 32px→27sp · headline-lg 24–28px→20–24sp · headline-md 22px→19sp
// headline-sm 18px→15sp · title-lg 22px→19sp · title-md 16px→14sp · title-sm 14px→12sp
// body 16/14/12px→14/12/10sp · label-lg 14px→12sp · label-md 12px→10sp
// label-sm 11px→9sp · micro 10px→8.5sp
val Typography = Typography(
    // Cantidad grande del numpad / display ("500", "35")
    displaySmall = TextStyle(
        fontFamily = RemyFontFamily, fontWeight = FontWeight.ExtraBold,
        fontSize = 27.sp, lineHeight = 34.sp, letterSpacing = 0.sp
    ),
    // Título hero de pantalla
    displayMedium = TextStyle(
        fontFamily = RemyFontFamily, fontWeight = FontWeight.ExtraBold,
        fontSize = 30.sp, lineHeight = 38.sp, letterSpacing = 0.sp
    ),
    // Título de pantalla grande (headline-lg)
    headlineLarge = TextStyle(
        fontFamily = RemyFontFamily, fontWeight = FontWeight.Bold,
        fontSize = 22.sp, lineHeight = 28.sp, letterSpacing = 0.sp
    ),
    // Título de pantalla ("Despensa", "Sugerencias") — usa RemyHeader
    headlineMedium = TextStyle(
        fontFamily = RemyFontFamily, fontWeight = FontWeight.Bold,
        fontSize = 20.sp, lineHeight = 26.sp, letterSpacing = 0.sp
    ),
    // Nombre de receta / títulos de sheet (headline-md 22px → 19sp)
    headlineSmall = TextStyle(
        fontFamily = RemyFontFamily, fontWeight = FontWeight.Bold,
        fontSize = 19.sp, lineHeight = 25.sp, letterSpacing = (-0.2).sp
    ),
    // Título de card de receta / sub-headline (18px → 15sp)
    titleLarge = TextStyle(
        fontFamily = RemyFontFamily, fontWeight = FontWeight.Bold,
        fontSize = 15.sp, lineHeight = 21.sp, letterSpacing = 0.sp
    ),
    // Nombre de alimento / item (title-md 16px → 14sp)
    titleMedium = TextStyle(
        fontFamily = RemyFontFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.sp
    ),
    // Sub-título (title-sm 14px → 12sp)
    titleSmall = TextStyle(
        fontFamily = RemyFontFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp, lineHeight = 17.sp, letterSpacing = 0.1.sp
    ),
    // Cuerpo grande (body 16px → 14sp)
    bodyLarge = TextStyle(
        fontFamily = RemyFontFamily, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 21.sp, letterSpacing = 0.sp
    ),
    // Cuerpo (body 14px → 12sp)
    bodyMedium = TextStyle(
        fontFamily = RemyFontFamily, fontWeight = FontWeight.Normal,
        fontSize = 12.sp, lineHeight = 17.sp, letterSpacing = 0.sp
    ),
    // Texto secundario (body 12px → 10sp)
    bodySmall = TextStyle(
        fontFamily = RemyFontFamily, fontWeight = FontWeight.Normal,
        fontSize = 10.sp, lineHeight = 14.sp, letterSpacing = 0.sp
    ),
    // Botones (label-lg 14px → 12sp)
    labelLarge = TextStyle(
        fontFamily = RemyFontFamily, fontWeight = FontWeight.Bold,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.sp
    ),
    // Chips, metadatos (label-md 12px → 10sp)
    labelMedium = TextStyle(
        fontFamily = RemyFontFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 10.sp, lineHeight = 14.sp, letterSpacing = 0.sp
    ),
    // Badges, pills pequeñas (label-sm 11px → 9sp)
    labelSmall = TextStyle(
        fontFamily = RemyFontFamily, fontWeight = FontWeight.Bold,
        fontSize = 9.sp, lineHeight = 12.sp, letterSpacing = 0.4.sp
    )
)
