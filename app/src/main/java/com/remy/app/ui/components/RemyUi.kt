package com.remy.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Backspace
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.remy.app.ui.theme.ChipColors
import com.remy.app.ui.theme.Green050
import com.remy.app.ui.theme.Green100
import com.remy.app.ui.theme.Green200
import com.remy.app.ui.theme.Green500
import com.remy.app.ui.theme.Green600
import com.remy.app.ui.theme.Green700
import com.remy.app.ui.theme.PillShape
import com.remy.app.ui.theme.RemyShapes
import com.remy.app.ui.theme.Slate100
import com.remy.app.ui.theme.Slate200
import com.remy.app.ui.theme.Slate500
import com.remy.app.ui.theme.Slate600
import com.remy.app.ui.theme.Slate900
import com.remy.app.ui.theme.remyColors

// ---------------------------------------------------------------------------
// Componentes transversales del front Stitch (SPEC-FRONT §1 y §4)
// ---------------------------------------------------------------------------

/** Sombra de card del mockup: 0 2px 12px rgba(22,163,74,.04). */
fun Modifier.remyCardShadow(shape: Shape = RemyShapes.medium): Modifier =
    this.shadow(
        elevation = 6.dp,
        shape = shape,
        ambientColor = Green600.copy(alpha = 0.06f),
        spotColor = Green600.copy(alpha = 0.10f)
    )

/** Sombra glow de card seleccionada / hint: 0 8px 24px -4px rgba(22,163,74,.08). */
fun Modifier.remyGlowShadow(shape: Shape = RemyShapes.medium): Modifier =
    this.shadow(
        elevation = 12.dp,
        shape = shape,
        ambientColor = Green600.copy(alpha = 0.10f),
        spotColor = Green600.copy(alpha = 0.16f)
    )

/** Sombra de CTA: 0 4px 16px rgba(22,163,74,.25). */
fun Modifier.remyCtaShadow(shape: Shape = PillShape): Modifier =
    this.shadow(
        elevation = 10.dp,
        shape = shape,
        ambientColor = Green600.copy(alpha = 0.30f),
        spotColor = Green600.copy(alpha = 0.40f)
    )

// ---------------------------------------------------------------------------
// Buscador (SearchPill) — h48, bg blanco, r24/pill, borde #DCFCE7
// ---------------------------------------------------------------------------
@Composable
fun SearchPill(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    iconTint: Color = Slate500,
    trailingIcon: ImageVector? = null,
    onTrailingClick: (() -> Unit)? = null
) {
    val shape = PillShape
    Row(
        modifier = modifier
            .height(48.dp)
            .shadow(4.dp, shape, ambientColor = Color.Black.copy(alpha = 0.03f), spotColor = Color.Black.copy(alpha = 0.05f))
            .clip(shape)
            .background(Color.White)
            .border(1.dp, Green100, shape)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leadingIcon != null) {
            Icon(leadingIcon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            androidx.compose.foundation.layout.Spacer(Modifier.width(10.dp))
        }
        Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            if (value.isEmpty()) {
                Text(
                    placeholder,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Slate500,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Slate900),
                cursorBrush = SolidColor(Green600),
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (trailingIcon != null) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Slate100)
                    .clickable(enabled = onTrailingClick != null) { onTrailingClick?.invoke() },
                contentAlignment = Alignment.Center
            ) {
                Icon(trailingIcon, contentDescription = "Limpiar", tint = Slate500, modifier = Modifier.size(16.dp))
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Chip de filtro/estado — activo: pill verde con check; inactivo: blanco borde mint
// ---------------------------------------------------------------------------
@Composable
fun FilterPill(
    label: String,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null
) {
    val bg = if (active) Green600 else Color.White
    val fg = if (active) Color.White else Slate900
    val border = if (active) Green700 else Green100
    Row(
        modifier = modifier
            .height(32.dp)
            .shadow(if (active) 6.dp else 2.dp, PillShape, ambientColor = Green600.copy(alpha = 0.2f), spotColor = Green600.copy(alpha = 0.35f))
            .clip(PillShape)
            .background(bg)
            .border(1.dp, border, PillShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (leadingIcon != null) {
            Icon(leadingIcon, contentDescription = null, tint = fg, modifier = Modifier.size(14.dp))
        }
        Text(
            label,
            color = fg,
            fontSize = 10.sp,
            fontWeight = if (active) FontWeight.Bold else FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

// ---------------------------------------------------------------------------
// Pill de metadato (macros, tiempo, badges)
// ---------------------------------------------------------------------------
@Composable
fun MetaPill(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    tint: Color = Green600,
    bg: Color = Slate100,
    fg: Color = Slate900,
    leadingDot: Color? = null
) {
    Row(
        modifier = modifier
            .clip(PillShape)
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        if (leadingDot != null) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(leadingDot))
        }
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(14.dp))
        }
        Text(text, color = fg, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
    }
}

// ---------------------------------------------------------------------------
// Chip pequeño de categoría junto al nombre del alimento
// ---------------------------------------------------------------------------
@Composable
fun CategoriaChip(categoria: Categoria, modifier: Modifier = Modifier) {
    val c = categoria.colors
    Row(
        modifier = modifier
            .clip(PillShape)
            .background(c.bg)
            .border(1.dp, c.border, PillShape)
            .padding(horizontal = 8.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (categoria.conDot) {
            Box(Modifier.size(6.dp).clip(CircleShape).background(c.fg))
        }
        Text(categoria.label, color = c.fg, fontSize = 9.sp, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}

// ---------------------------------------------------------------------------
// IconCircle — círculo de icono por color de categoría (28–56dp)
// ---------------------------------------------------------------------------
@Composable
fun IconCircle(
    size: Dp,
    bg: Color,
    modifier: Modifier = Modifier,
    border: Color? = null,
    shadow: Boolean = true,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .size(size)
            .let { if (shadow) it.shadow(3.dp, CircleShape, ambientColor = Color.Black.copy(alpha = 0.05f), spotColor = Color.Black.copy(alpha = 0.08f)) else it }
            .clip(CircleShape)
            .background(bg)
            .then(if (border != null) Modifier.border(1.dp, border, CircleShape) else Modifier),
        contentAlignment = Alignment.Center
    ) { content() }
}

/** Círculo con emoji (fotos de ingrediente del mockup). */
@Composable
fun EmojiCircle(emoji: String, size: Dp = 28.dp, bg: Color = Green100, modifier: Modifier = Modifier) {
    IconCircle(size = size, bg = bg, modifier = modifier, shadow = false) {
        Text(emoji, fontSize = (size.value * 0.5f).sp)
    }
}

// ---------------------------------------------------------------------------
// Categorías por palabra clave (sin tocar la BD — SPEC decisión)
// ---------------------------------------------------------------------------
data class Categoria(val label: String, val colors: ChipColors, val conDot: Boolean = false)

@androidx.compose.runtime.Composable
fun categoriaDe(nombre: String): Categoria = when {
    listOf("aceite", "aove").any { nombre.contains(it, true) } ->
        Categoria("Aceite", MaterialTheme.remyColors.chipAceite)
    listOf("arroz", "pasta", "cereal", "pan", "harina", "avena", "lenteja", "garbanzo", "quinoa", "maíz", "maiz", "espagu", "macarr", "fideos").any { nombre.contains(it, true) } ->
        Categoria("Cereales", MaterialTheme.remyColors.chipGranos)
    listOf("huevo").any { nombre.contains(it, true) } ->
        Categoria("Huevo", MaterialTheme.remyColors.chipHuevo)
    listOf("leche", "queso", "yogur", "mantequilla", "nata").any { nombre.contains(it, true) } ->
        Categoria("Lácteos", MaterialTheme.remyColors.chipHuevo, conDot = true)
    listOf("pollo", "pechuga", "carne", "ternera", "cerdo", "pescado", "salmón", "salmon", "atún", "atun", "gamba", "jamón", "jamon", "tofu").any { nombre.contains(it, true) } ->
        Categoria("Proteína", MaterialTheme.remyColors.chipProteina)
    else ->
        Categoria("Verdura", MaterialTheme.remyColors.chipVerdura, conDot = true)
}

fun emojiDe(nombre: String): String = when {
    listOf("arroz", "risotto", "tortilla", "paella", "bowls").any { nombre.contains(it, true) } -> "🍚"
    listOf("huevo", "huevos").any { nombre.contains(it, true) } -> "🥚"
    listOf("pasta", "espagu", "macarr", "fideos", "lasaña", "lasana").any { nombre.contains(it, true) } -> "🍝"
    listOf("salmón", "salmon", "pescado", "atún", "atun", "merluza").any { nombre.contains(it, true) } -> "🐟"
    listOf("pollo", "pechuga", "carne", "ternera").any { nombre.contains(it, true) } -> "🍗"
    listOf("tomate", "tomates").any { nombre.contains(it, true) } -> "🍅"
    listOf("queso", "leche").any { nombre.contains(it, true) } -> "🧀"
    listOf("pan", "tostada").any { nombre.contains(it, true) } -> "🍞"
    listOf("ensalada", "verdura", "verduras", "lechuga").any { nombre.contains(it, true) } -> "🥗"
    listOf("sopa", "crema").any { nombre.contains(it, true) } -> "🍲"
    else -> "🍽️"
}

// ---------------------------------------------------------------------------
// FotoReceta — placeholder igual al SVG del preview: degradado mint + círculo
// blanco + emoji + etiqueta verde. (Los SVG llevan <text>, no convertibles.)
// ---------------------------------------------------------------------------
@Composable
fun FotoReceta(
    nombre: String,
    modifier: Modifier = Modifier,
    caption: String? = null
) {
    Box(
        modifier = modifier.background(
            Brush.linearGradient(listOf(Green100, Green050))
        ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .shadow(2.dp, CircleShape, ambientColor = Color.Black.copy(alpha = 0.04f), spotColor = Color.Black.copy(alpha = 0.06f))
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(2.dp, Green200, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(emojiDe(nombre), fontSize = 40.sp)
            }
            Text(
                caption ?: nombre.split(" ").firstOrNull().orEmpty().replaceFirstChar { it.uppercase() },
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Botón CTA verde (pill, sombra)
// ---------------------------------------------------------------------------
@Composable
fun RemyCta(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 48.dp,
    icon: ImageVector? = null,
    iconAfter: ImageVector? = null,
    fontSize: androidx.compose.ui.unit.TextUnit = 12.sp
) {
    Row(
        modifier = modifier
            .height(height)
            .remyCtaShadow(PillShape)
            .clip(PillShape)
            .background(Green600)
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            androidx.compose.foundation.layout.Spacer(Modifier.width(8.dp))
        }
        Text(text, color = Color.White, fontSize = fontSize, fontWeight = FontWeight.Bold, maxLines = 1)
        if (iconAfter != null) {
            androidx.compose.foundation.layout.Spacer(Modifier.width(8.dp))
            Icon(iconAfter, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
        }
    }
}

// ---------------------------------------------------------------------------
// Card base blanca con borde mint y sombra suave (SPEC §1.3)
// ---------------------------------------------------------------------------
@Composable
fun SectionCard(
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = RemyShapes.medium,
    bg: Color = Color.White,
    border: Color? = Green100,
    glow: Boolean = false,
    padding: androidx.compose.foundation.layout.PaddingValues = androidx.compose.foundation.layout.PaddingValues(16.dp),
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .let { if (glow) it.remyGlowShadow(shape) else it.remyCardShadow(shape) }
            .clip(shape)
            .background(bg)
            .then(if (border != null) Modifier.border(1.dp, border, shape) else Modifier)
            .padding(padding),
        content = content
    )
}

// ---------------------------------------------------------------------------
// Selector de unidad segmentado (Gramos / Kilos / Unidades)
// ---------------------------------------------------------------------------
@Composable
fun UnitSelector(
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val labels = listOf("Gramos (g)", "Kilos (kg)", "Unidades (ud)")
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(PillShape)
            .background(Color.White)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        labels.forEachIndexed { i, label ->
            val active = i == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(32.dp)
                    .let { if (active) it.shadow(3.dp, PillShape, spotColor = Green600.copy(alpha = 0.35f)) else it }
                    .clip(PillShape)
                    .background(if (active) Green600 else Color.Transparent)
                    .clickable { onSelect(i) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    label,
                    color = if (active) Color.White else Slate600,
                    fontSize = 10.sp,
                    fontWeight = if (active) FontWeight.Bold else FontWeight.SemiBold,
                    maxLines = 1
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Numpad 3×4 — dígitos, CLR y borrado (SPEC §2.2/§2.3/§2.5)
// ---------------------------------------------------------------------------
@Composable
fun Numpad(
    onDigit: (Char) -> Unit,
    onClear: () -> Unit,
    onBackspace: () -> Unit,
    modifier: Modifier = Modifier,
    keyHeight: Dp = 44.dp,
    keyShape: androidx.compose.ui.graphics.Shape = RemyShapes.medium,
    clrColor: Color = Slate500,
    digitSize: androidx.compose.ui.unit.TextUnit = 14.sp
) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("123", "456", "789").forEach { fila ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                fila.forEach { d ->
                    NumpadKey(
                        modifier = Modifier.weight(1f),
                        height = keyHeight,
                        shape = keyShape,
                        onClick = { onDigit(d) }
                    ) { Text("$d", fontSize = digitSize, fontWeight = FontWeight.SemiBold, color = Slate900) }
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NumpadKey(
                modifier = Modifier.weight(1f),
                height = keyHeight,
                shape = keyShape,
                bg = Slate100,
                onClick = onClear
            ) { Text("CLR", fontSize = digitSize, fontWeight = FontWeight.Bold, color = clrColor) }
            NumpadKey(
                modifier = Modifier.weight(1f),
                height = keyHeight,
                shape = keyShape,
                onClick = { onDigit('0') }
            ) { Text("0", fontSize = digitSize, fontWeight = FontWeight.SemiBold, color = Slate900) }
            NumpadKey(
                modifier = Modifier.weight(1f),
                height = keyHeight,
                shape = keyShape,
                onClick = onBackspace
            ) {
                Icon(
                    androidx.compose.material.icons.Icons.Outlined.Backspace,
                    contentDescription = "Borrar",
                    tint = Slate500,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun NumpadKey(
    modifier: Modifier,
    height: Dp,
    shape: androidx.compose.ui.graphics.Shape,
    onClick: () -> Unit,
    bg: Color = Color.White,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .height(height)
            .clip(shape)
            .background(bg)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) { content() }
}

// ---------------------------------------------------------------------------
// Stepper −/+ (48×48) con valor grande al centro (mockup §2.2/§2.3)
// ---------------------------------------------------------------------------
@Composable
fun BigStepper(
    value: String,
    unit: String,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        StepperBtn(onClick = onMinus, bg = Slate100, icon = androidx.compose.material.icons.Icons.Outlined.Remove, tint = Green600)
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(value, style = MaterialTheme.typography.displaySmall, color = Slate900, fontSize = 27.sp)
            Text(unit, style = MaterialTheme.typography.titleLarge, color = Green600, modifier = Modifier.padding(bottom = 3.dp))
        }
        StepperBtn(onClick = onPlus, bg = Green600, icon = androidx.compose.material.icons.Icons.Outlined.Add, tint = Color.White, shadow = true)
    }
}

@Composable
fun StepperBtn(
    onClick: () -> Unit,
    bg: Color,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    shadow: Boolean = false
) {
    Box(
        modifier = modifier
            .size(size)
            .let { if (shadow) it.shadow(6.dp, CircleShape, spotColor = Green600.copy(alpha = 0.35f)) else it }
            .clip(CircleShape)
            .background(bg)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
    }
}
