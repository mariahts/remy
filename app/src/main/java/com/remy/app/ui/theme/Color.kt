package com.remy.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ---------------------------------------------------------------------------
// Nueva paleta REMY (rediseño)
// ---------------------------------------------------------------------------

val FondoCrema = Color(0xFFFFFBF5)
val SuperficieBlanca = Color(0xFFFFFFFF)
val BordeTarjeta = Color(0xFFF1E9DC)

val TextoPrincipal = Color(0xFF14213D)
val TextoSecundario = Color(0xFF475569)

val Terracota = Color(0xFFC2410C)      // acción principal
val AzulTinta = Color(0xFF14213D)       // estructura/selección
val Melocoton = Color(0xFFFFEDD5)       // avatar y círculo del logo

// Tonos pasteles de las miniaturas de receta (sustituyen a las fotos reales
// mientras no haya imágenes propias con licencia).
val TonoPastelMelocoton = Color(0xFFE9A27A)
val TonoPastelMostaza = Color(0xFFF4C98B)
val TonoPastelAzul = Color(0xFF9DC3D6)
val TonoPastelLavanda = Color(0xFFC7D2E8)
val TonoPastelRosa = Color(0xFFFBCFE8)
val TonoPastelAzulClaro = Color(0xFFDCE7F5)

val TONOS_PASTEL = listOf(
    TonoPastelMelocoton,
    TonoPastelMostaza,
    TonoPastelAzul,
    TonoPastelLavanda,
    TonoPastelRosa,
    TonoPastelAzulClaro
)

// Avisos
val AvisoFaltaBg = Color(0xFFFFEDD5)
val AvisoFaltaFg = Color(0xFF9A3412)
val AvisoProntoBg = Color(0xFFFEF3C7)
val AvisoProntoFg = Color(0xFF92400E)
val AvisoCaducadoBg = Color(0xFFFEE2E2)
val AvisoCaducadoFg = Color(0xFF991B1B)
val AvisoNeutroBg = Color(0xFFE8EEF3)
val AvisoNeutroFg = Color(0xFF334155)
val NaranjaAviso = Color(0xFFEA580C)

// Degradado suave de fondo (crema)
val RemyBackgroundBrush: Brush = Brush.verticalGradient(
    colors = listOf(FondoCrema, Color(0xFFFFF7ED))
)

// CTA brush (terracota)
val CtaBrush: Brush = Brush.horizontalGradient(
    colors = listOf(Terracota, Color(0xFF9A3412))
)

// ---------------------------------------------------------------------------
// Aliases de compatibilidad para no romper código existente durante la migración.
// Se irán eliminando conforme se actualicen componentes y pantallas.
// ---------------------------------------------------------------------------
val White = Color(0xFFFFFFFF)

val Green900 = TextoPrincipal
val Green800 = TextoPrincipal
val Green700 = Terracota
val Green600 = Terracota
val Green500 = Terracota
val Green400 = Melocoton
val Green300 = BordeTarjeta
val Green200 = BordeTarjeta
val Green100 = Melocoton
val Green050 = FondoCrema

val Mint = FondoCrema
val MintBorder = BordeTarjeta
val MintDeep = Color(0xFFFFF7ED)

val Slate900 = TextoPrincipal
val Slate700 = TextoSecundario
val Slate600 = TextoSecundario
val Slate500 = Color(0xFF64748B)
val Slate400 = Color(0xFF94A3B8)
val Slate300 = Color(0xFFCBD5E1)
val Slate200 = BordeTarjeta
val Slate100 = Color(0xFFF8FAF6)
val Slate50 = FondoCrema

val Red600 = Color(0xFFDC2626)
val Red700 = AvisoCaducadoFg
val Red900 = AvisoCaducadoFg
val Red400 = Color(0xFFF87171)
val Red100 = AvisoCaducadoBg
val Red50 = AvisoCaducadoBg
val RedRosa50 = AvisoCaducadoBg
val RedRose100 = AvisoCaducadoBg
val RedRose200 = Color(0xFFFECACA)
val RedRose500 = Red600
val RedRose50 = AvisoCaducadoBg

val Orange600 = Terracota
val Orange800 = AvisoFaltaFg
val Orange700 = Terracota
val Orange100 = AvisoFaltaBg
val Orange50 = Color(0xFFFFF7ED)
val Orange300 = Color(0xFFFED7AA)

val Amber600 = AvisoProntoFg
val Amber700 = AvisoProntoFg
val Amber100 = AvisoProntoBg
val Amber200 = Color(0xFFFDE68A)
val Amber300 = Color(0xFFFCD34D)
val Amber50 = AvisoProntoBg

val Yellow400 = Color(0xFFFDE68A)
val Yellow600 = AvisoProntoFg

val Violet600 = AzulTinta
val Violet700 = AzulTinta
val Violet100 = Color(0xFFE8EEF3)
val Violet50 = Color(0xFFF8FAFC)

val Blue600 = AzulTinta
val Blue700 = AzulTinta
val Blue100 = AvisoNeutroBg
val Blue50 = Color(0xFFF8FAFC)
val Blue200 = AvisoNeutroBg

val Indigo700 = AzulTinta
val Indigo50 = AvisoNeutroBg
val Indigo100 = AvisoNeutroBg

val Emerald300 = BordeTarjeta
val Emerald200 = BordeTarjeta
val Emerald100 = Melocoton
val Emerald50 = Melocoton

val ChipAceiteBg = AvisoNeutroBg
val ChipAceiteFg = AvisoNeutroFg
val ChipAceiteBorder = BordeTarjeta

val DetailGreen500 = Terracota
val DetailGreen900 = TextoPrincipal
val DetailGreen950 = TextoPrincipal
val DetailMint = Melocoton
val DetailOrange700 = Terracota
val DetailOrange900 = TextoPrincipal
val DetailDarkPill = AzulTinta
val Terracota100 = Melocoton
val Terracota50 = Melocoton
val Terracota700 = Terracota
