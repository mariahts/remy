package com.remy.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Los bocetos usan una sans-serif geométrica y redondeada (parece Plus Jakarta Sans).
// Mientras no se añadan los archivos de la fuente, se usa la del sistema.
// Para activarla: copiar los .ttf a app/src/main/res/font/ (nombres en minúsculas y
// con guiones bajos), añadir los imports de Font y de androidx.compose.ui.res.*
// y cambiar la línea de RemyFontFamily por el bloque comentado.
private val RemyFontFamily: FontFamily = FontFamily.Default
/*
private val RemyFontFamily = FontFamily(
    Font(R.font.plus_jakarta_sans_regular, FontWeight.Normal),
    Font(R.font.plus_jakarta_sans_medium, FontWeight.Medium),
    Font(R.font.plus_jakarta_sans_semibold, FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans_bold, FontWeight.Bold),
    Font(R.font.plus_jakarta_sans_extrabold, FontWeight.ExtraBold),
)
*/

val Typography = Typography(
    // Cantidad grande del selector ("500 g")
    displaySmall = TextStyle(
        fontFamily = RemyFontFamily, fontWeight = FontWeight.ExtraBold,
        fontSize = 36.sp, lineHeight = 44.sp, letterSpacing = 0.sp
    ),
    // Título de pantalla ("Despensa", "Sugerencias")
    headlineMedium = TextStyle(
        fontFamily = RemyFontFamily, fontWeight = FontWeight.Bold,
        fontSize = 26.sp, lineHeight = 32.sp, letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = RemyFontFamily, fontWeight = FontWeight.Bold,
        fontSize = 22.sp, lineHeight = 28.sp, letterSpacing = 0.sp
    ),
    // Nombre de receta y títulos de diálogo
    titleLarge = TextStyle(
        fontFamily = RemyFontFamily, fontWeight = FontWeight.Bold,
        fontSize = 20.sp, lineHeight = 26.sp, letterSpacing = 0.sp
    ),
    // Nombre de alimento
    titleMedium = TextStyle(
        fontFamily = RemyFontFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp, lineHeight = 24.sp, letterSpacing = 0.sp
    ),
    titleSmall = TextStyle(
        fontFamily = RemyFontFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = RemyFontFamily, fontWeight = FontWeight.Normal,
        fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = RemyFontFamily, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.sp
    ),
    // Texto secundario ("Despensa · Pasta", descripciones)
    bodySmall = TextStyle(
        fontFamily = RemyFontFamily, fontWeight = FontWeight.Normal,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.sp
    ),
    // Botones y chips de filtro
    labelLarge = TextStyle(
        fontFamily = RemyFontFamily, fontWeight = FontWeight.Bold,
        fontSize = 15.sp, lineHeight = 20.sp, letterSpacing = 0.sp
    ),
    // Chips de ingredientes, kcal, proteína
    labelMedium = TextStyle(
        fontFamily = RemyFontFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.sp
    ),
    // Etiquetas de categoría y estado ("Cereales", "Usar pronto")
    labelSmall = TextStyle(
        fontFamily = RemyFontFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 10.sp, lineHeight = 14.sp, letterSpacing = 0.2.sp
    )
)
