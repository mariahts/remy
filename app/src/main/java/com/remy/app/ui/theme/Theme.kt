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
    background = Green050,       // mint bg app #F0FDF4 (SPEC §1.1)
    onBackground = Slate900,
    surface = Color.White,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate600,
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

/** Par bg/texto/borde de un chip de categoría (SPEC §1.1). */
@Immutable
data class ChipColors(
    val bg: Color,
    val fg: Color,
    val border: Color
)

/**
 * Colores propios de REMY que Material 3 no define: acentos de filtro,
 * categorías de alimentos y chips de ingrediente (SPEC-FRONT §1.1).
 * Uso: MaterialTheme.remyColors.warning / .chipGranos
 */
@Immutable
data class RemyColors(
    // Avisos e información
    val warning: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
    val success: Color,
    val info: Color,
    val infoContainer: Color,
    // Acentos de filtro (§2.5)
    val violet: Color,
    val violetContainer: Color,
    val blue: Color,
    val blueContainer: Color,
    val amber: Color,
    val amberContainer: Color,
    // Chips de categoría de ingrediente
    val chipGranos: ChipColors,
    val chipHuevo: ChipColors,
    val chipVerdura: ChipColors,
    val chipAceite: ChipColors,
    val chipProteina: ChipColors,
    // Categorías del selector de recetas (ya existentes)
    val categoryCereales: Color,
    val categoryVerduras: Color,
    val categoryProteinas: Color,
    val categoryFrescos: Color,
    val categoryAceites: Color
)

private val DefaultRemyColors = RemyColors(
    warning = Amber600,
    warningContainer = Amber100,
    onWarningContainer = Amber700,
    success = Green600,
    info = Indigo700,
    infoContainer = Indigo100,
    violet = Violet600,
    violetContainer = Violet100,
    blue = Blue600,
    blueContainer = Blue50,
    amber = Amber600,
    amberContainer = Amber100,
    chipGranos = ChipColors(bg = Yellow400, fg = Yellow600, border = Amber300),
    chipHuevo = ChipColors(bg = Amber100, fg = Amber600, border = Amber200),
    chipVerdura = ChipColors(bg = RedRose50, fg = RedRose500, border = RedRose200),
    chipAceite = ChipColors(bg = ChipAceiteBg, fg = ChipAceiteFg, border = ChipAceiteBorder),
    chipProteina = ChipColors(bg = Orange100, fg = Orange600, border = Orange300),
    categoryCereales = Yellow400,
    categoryVerduras = Color(0xFFFFE4C8),
    categoryProteinas = Orange100,
    categoryFrescos = Green100,
    categoryAceites = ChipAceiteBg
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
