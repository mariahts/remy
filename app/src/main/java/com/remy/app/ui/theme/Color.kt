package com.remy.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ---------------------------------------------------------------------------
// Paleta de REMY — tokens según SPEC-FRONT §1.1 (mockups HTML de Stitch)
// ---------------------------------------------------------------------------

// Verdes de marca (CTA)
val Green900 = Color(0xFF14532D)   // CTA deep: texto sobre superficies mint
val Green800 = Color(0xFF166534)   // texto de chips "Proteína" / pills activas
val Green700 = Color(0xFF15803D)   // CTA dark: pressed
val Green600 = Color(0xFF16A34A)   // CTA primario: botones, chips activos, FAB, badges
val Green500 = Color(0xFF22C55E)   // CTA light: acentos, "En despensa"
val Green400 = Color(0xFF4ADE80)   // iconos sobre pills oscuras
val Green300 = Color(0xFF86EFAC)
val Green200 = Color(0xFFBBF7D0)   // borde mint
val Green100 = Color(0xFFDCFCE7)   // mint container: chips/badges/icon-circles
val Green050 = Color(0xFFF0FDF4)   // fondo de pantalla base (mint bg app)

val Mint = Color(0xFFE2F8E7)       // fondo alterno (Despensa, detalle plegadas_2)
val MintBorder = Color(0xFFC8F0D7)  // borde inferior del topbar de Despensa
val MintDeep = Color(0xFFDCF5E3)    // fondo de hojas inferiores y diálogos

// Neutros (texto y bordes)
val Slate900 = Color(0xFF0F172A)   // texto primario
val Slate700 = Color(0xFF334155)   // texto de botón "Cancelar"
val Slate600 = Color(0xFF475569)   // texto secundario
val Slate500 = Color(0xFF64748B)   // texto terciario / placeholders
val Slate400 = Color(0xFF94A3B8)
val Slate300 = Color(0xFFCBD5E1)
val Slate200 = Color(0xFFE2E8F0)   // borde neutro
val Slate100 = Color(0xFFF1F5F9)   // fondos inactivos, chips, teclas
val Slate50 = Color(0xFFF8FAFC)    // superficie alt inactiva

// Rojo (eliminar / error)
val Red600 = Color(0xFFDC2626)
val Red700 = Color(0xFFBE123C)
val Red900 = Color(0xFF991B1B)
val Red400 = Color(0xFFF43F5E)
val Red100 = Color(0xFFFEE2E2)
val Red50 = Color(0xFFFEE2E2)
val RedRosa50 = Color(0xFFFFF1F2)
val RedRose100 = Color(0xFFFECACA)
val RedRose200 = Color(0xFFFECDD3)
val RedRose500 = Color(0xFFE11D48)
val RedRose50 = Color(0xFFFFE4E6)

// Naranja (proteína / avisos)
val Orange600 = Color(0xFFEA580C)
val Orange800 = Color(0xFF9A3412)
val Orange700 = Color(0xFFC2410C)
val Orange100 = Color(0xFFFFEDD5)
val Orange50 = Color(0xFFFFF7ED)
val Orange300 = Color(0xFFFED7AA)

// Ámbar (alertas / vencimiento / chef tips)
val Amber600 = Color(0xFFD97706)
val Amber700 = Color(0xFFB45309)
val Amber100 = Color(0xFFFEF3C7)
val Amber200 = Color(0xFFFDE68A)
val Amber300 = Color(0xFFFDE047)
val Amber50 = Color(0xFFFEF9C3)

// Amarillo (granos / cereales)
val Yellow400 = Color(0xFFFEF08A)  // chip grano bg
val Yellow600 = Color(0xFFCA8A04)  // chip grano texto

// Violeta (filtro duración)
val Violet600 = Color(0xFF8B5CF6)
val Violet700 = Color(0xFF9333EA)
val Violet100 = Color(0xFFF3E8FF)
val Violet50 = Color(0xFFF5F3FF)

// Azul (filtro región de origen)
val Blue600 = Color(0xFF0284C7)
val Blue700 = Color(0xFF2563EB)
val Blue100 = Color(0xFFDBEAFE)
val Blue50 = Color(0xFFF0F9FF)
val Blue200 = Color(0xFFDAE2FD)

// Índigo (chips informativos)
val Indigo700 = Color(0xFF3730A3)
val Indigo50 = Color(0xFFE2E7FF)
val Indigo100 = Color(0xFFE0E7FF)

// Verde especial del mockup (banner "todos los ingredientes", radios)
val Emerald300 = Color(0xFF34D399)
val Emerald200 = Color(0xFFA7F3D0)
val Emerald100 = Color(0xFFD1FAE5)
val Emerald50 = Color(0xFF95F8A7)

// Chips de categoría (bg / texto / borde) — §1.1
// Granos    #FEF08A / #CA8A04 / #FDE047
// Huevo     #FEF3C7 / #D97706 / #FDE68A
// Verdura   #FFE4E6 / #E11D48 / #FECDD3
// Aceite    #ECFCCB / #4D7C0F / #D9F99D
// Proteína  #FFEDD5 / #EA580C / #FED7AA
val ChipAceiteBg = Color(0xFFECFCCB)
val ChipAceiteFg = Color(0xFF4D7C0F)
val ChipAceiteBorder = Color(0xFFD9F99D)

// Detalle de receta (variantes del mockup)
val DetailGreen500 = Color(0xFF00873A)   // pill "4 pasos"
val DetailGreen900 = Color(0xFF005323)   // texto consejo
val DetailGreen950 = Color(0xFF00210A)   // texto sobre #92F5A4
val DetailMint = Color(0xFF92F5A4)       // secondary-container del mockup
val DetailOrange700 = Color(0xFFCC4900)  // tertiary-container
val DetailOrange900 = Color(0xFF370E00)
val DetailDarkPill = Color(0xFF283044)   // pills oscuras del hero
val Terracota100 = Color(0xFFFFDBCE)
val Terracota50 = Color(0xFFFFDAD6)
val Terracota700 = Color(0xFFA33900)

// Fondo con degradado: blanco arriba, menta abajo (cabecera de Despensa)
val RemyBackgroundBrush: Brush = Brush.verticalGradient(
    colors = listOf(Color.White, Mint)
)

// Degradado del hero de Sugerencias (§2.6 usa blur-circle; este es el de auth CTA)
val CtaBrush: Brush = Brush.horizontalGradient(
    colors = listOf(Green500, Green600)
)
