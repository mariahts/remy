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
    primary = Terracota,
    onPrimary = Color.White,
    primaryContainer = Melocoton,
    onPrimaryContainer = TextoPrincipal,
    secondary = AzulTinta,
    onSecondary = Color.White,
    secondaryContainer = AvisoNeutroBg,
    onSecondaryContainer = TextoPrincipal,
    tertiary = AzulTinta,
    onTertiary = Color.White,
    tertiaryContainer = AvisoNeutroBg,
    onTertiaryContainer = TextoPrincipal,
    background = FondoCrema,
    onBackground = TextoPrincipal,
    surface = SuperficieBlanca,
    onSurface = TextoPrincipal,
    surfaceVariant = Color(0xFFF8F4EC),
    onSurfaceVariant = TextoSecundario,
    outline = BordeTarjeta,
    outlineVariant = BordeTarjeta,
    error = Red600,
    onError = Color.White,
    errorContainer = AvisoCaducadoBg,
    onErrorContainer = AvisoCaducadoFg,
    surfaceBright = SuperficieBlanca,
    surfaceDim = Color(0xFFF3EDE3),
    surfaceContainerLowest = SuperficieBlanca,
    surfaceContainerLow = Color(0xFFFFFDF9),
    surfaceContainer = Color(0xFFFFFAF2),
    surfaceContainerHigh = Color(0xFFFFF7ED),
    surfaceContainerHighest = SuperficieBlanca
)

/** Par bg/texto/borde para chips/badges (compatibilidad). */
@Immutable
data class ChipColors(
    val bg: Color,
    val fg: Color,
    val border: Color
)

@Immutable
data class RemyColors(
    // Avisos e información
    val warning: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
    val success: Color,
    val info: Color,
    val infoContainer: Color,
    // Acentos (compatibilidad)
    val violet: Color,
    val violetContainer: Color,
    val blue: Color,
    val blueContainer: Color,
    val amber: Color,
    val amberContainer: Color,
    // Chips de categoría
    val chipGranos: ChipColors,
    val chipHuevo: ChipColors,
    val chipVerdura: ChipColors,
    val chipAceite: ChipColors,
    val chipProteina: ChipColors,
    // Categorías
    val categoryCereales: Color,
    val categoryVerduras: Color,
    val categoryProteinas: Color,
    val categoryFrescos: Color,
    val categoryAceites: Color
)

private val DefaultRemyColors = RemyColors(
    warning = AvisoProntoFg,
    warningContainer = AvisoProntoBg,
    onWarningContainer = AvisoProntoFg,
    success = AzulTinta, // sin verde
    info = AzulTinta,
    infoContainer = AvisoNeutroBg,
    violet = AzulTinta,
    violetContainer = AvisoNeutroBg,
    blue = AzulTinta,
    blueContainer = AvisoNeutroBg,
    amber = AvisoProntoFg,
    amberContainer = AvisoProntoBg,
    chipGranos = ChipColors(bg = AvisoProntoBg, fg = AvisoProntoFg, border = BordeTarjeta),
    chipHuevo = ChipColors(bg = AvisoFaltaBg, fg = AvisoFaltaFg, border = BordeTarjeta),
    chipVerdura = ChipColors(bg = AvisoNeutroBg, fg = AvisoNeutroFg, border = BordeTarjeta),
    chipAceite = ChipColors(bg = AvisoNeutroBg, fg = AvisoNeutroFg, border = BordeTarjeta),
    chipProteina = ChipColors(bg = AvisoFaltaBg, fg = AvisoFaltaFg, border = BordeTarjeta),
    categoryCereales = AvisoProntoBg,
    categoryVerduras = AvisoNeutroBg,
    categoryProteinas = AvisoFaltaBg,
    categoryFrescos = Melocoton,
    categoryAceites = AvisoNeutroBg
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
