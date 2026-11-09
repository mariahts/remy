package com.remy.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Green600,
    onPrimary = Color.White,
    primaryContainer = Green100,
    onPrimaryContainer = Green900,
    secondary = Green700,
    onSecondary = Color.White,
    secondaryContainer = Green100,
    onSecondaryContainer = Green900,
    tertiary = Orange600,
    onTertiary = Color.White,
    tertiaryContainer = Orange100,
    onTertiaryContainer = Orange800,
    background = Mint,
    onBackground = Slate900,
    surface = Color.White,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate500,
    outline = Slate300,
    outlineVariant = Slate200,
    error = Red600,
    onError = Color.White,
    errorContainer = Red100,
    onErrorContainer = Red900,
    surfaceBright = Color.White,
    surfaceDim = Color(0xFFCBE2D1),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF4FCF6),
    surfaceContainer = Color(0xFFEAF9EE),
    surfaceContainerHigh = Mint,
    surfaceContainerHighest = Color.White // las Card se pintan con este color
)

/** Tema único de REMY (solo modo claro, sin colores dinámicos del sistema). */
@Composable
fun REMYTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        shapes = RemyShapes,
        content = content
    )
}
