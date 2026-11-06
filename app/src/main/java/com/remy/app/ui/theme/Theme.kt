package com.remy.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
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

/**
 * Colores propios de REMY que Material 3 no define: avisos, información y
 * categorías de alimentos (cereales, verduras, proteínas, frescos, aceites).
 * Uso: MaterialTheme.remyColors.warning
 */
@Immutable
data class RemyColors(
    val warning: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
    val success: Color,
    val info: Color,
    val infoContainer: Color,
    val categoryCereales: Color,
    val categoryVerduras: Color,
    val categoryProteinas: Color,
    val categoryFrescos: Color,
    val categoryAceites: Color
)

private val DefaultRemyColors = RemyColors(
    warning = Orange600,
    warningContainer = Orange100,
    onWarningContainer = Orange800,
    success = Green600,
    info = Indigo700,
    infoContainer = Indigo100,
    categoryCereales = Color(0xFFFEF08A),
    categoryVerduras = Color(0xFFFFE4C8),
    categoryProteinas = Orange100,
    categoryFrescos = Green100,
    categoryAceites = Color(0xFFECFCCB)
)

private val LocalRemyColors = staticCompositionLocalOf { DefaultRemyColors }

val MaterialTheme.remyColors: RemyColors
    @Composable
    @ReadOnlyComposable
    get() = LocalRemyColors.current

/** Tema único de REMY (solo modo claro, sin colores dinámicos del sistema). */
@Composable
fun REMYTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalRemyColors provides DefaultRemyColors) {
        MaterialTheme(
            colorScheme = LightColorScheme,
            typography = Typography,
            shapes = RemyShapes,
            content = content
        )
    }
}