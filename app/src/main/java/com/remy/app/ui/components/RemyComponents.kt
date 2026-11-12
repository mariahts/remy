package com.remy.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.BakeryDining
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Egg
import androidx.compose.material.icons.outlined.Grain
import androidx.compose.material.icons.outlined.Icecream
import androidx.compose.material.icons.outlined.OilBarrel
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.SetMeal
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import com.remy.app.R
import com.remy.app.logica.indiceTonoPastel
import com.remy.app.ui.theme.AvisoCaducadoBg
import com.remy.app.ui.theme.AvisoCaducadoFg
import com.remy.app.ui.theme.AvisoFaltaBg
import com.remy.app.ui.theme.AvisoFaltaFg
import com.remy.app.ui.theme.AvisoNeutroBg
import com.remy.app.ui.theme.AvisoNeutroFg
import com.remy.app.ui.theme.AvisoProntoBg
import com.remy.app.ui.theme.AvisoProntoFg
import com.remy.app.ui.theme.AzulTinta
import com.remy.app.ui.theme.BordeTarjeta
import com.remy.app.ui.theme.FondoCrema
import com.remy.app.ui.theme.Melocoton
import com.remy.app.ui.theme.PillShape
import com.remy.app.ui.theme.SuperficieBlanca
import com.remy.app.ui.theme.Terracota
import com.remy.app.ui.theme.TextoPrincipal
import com.remy.app.ui.theme.TextoSecundario
import com.remy.app.ui.theme.TONOS_PASTEL

// ---------------------------------------------------------------------------
// Logo circular de REMY.
// ---------------------------------------------------------------------------
@Composable
fun RemyLogo(
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    conBorde: Boolean = false
) {
    Image(
        painter = androidx.compose.ui.res.painterResource(id = R.drawable.remy_logo_gris),
        contentDescription = "REMY",
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .then(
                if (conBorde) {
                    Modifier.border(3.dp, Melocoton, CircleShape)
                } else {
                    Modifier.border(1.dp, BordeTarjeta, CircleShape)
                }
            )
    )
}

// ---------------------------------------------------------------------------
// Cabecera común: logo + REMY + título + avatar o flecha de volver.
// ---------------------------------------------------------------------------
@Composable
fun RemyTopBar(
    titulo: String,
    modifier: Modifier = Modifier,
    onAvatarClick: (() -> Unit)? = null,
    onVolver: (() -> Unit)? = null,
    conFondo: Boolean = true
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (conFondo) Modifier.background(FondoCrema) else Modifier)
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        RemyLogo(size = 44.dp)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "REMY",
                style = MaterialTheme.typography.labelMedium,
                color = Terracota,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 3.sp,
                maxLines = 1
            )
            if (titulo.isNotBlank()) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextoPrincipal,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        when {
            onVolver != null -> IconButton(
                onClick = onVolver,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(SuperficieBlanca)
                    .border(1.dp, BordeTarjeta, CircleShape)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = TextoPrincipal,
                    modifier = Modifier.size(22.dp)
                )
            }

            onAvatarClick != null -> Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Melocoton)
                    .clickable(onClick = onAvatarClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.Person,
                    contentDescription = "Perfil",
                    tint = TextoPrincipal,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Tarjeta blanca con borde cálido y esquinas de 24 dp.
// ---------------------------------------------------------------------------
@Composable
fun RemyCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    padding: Dp = 18.dp,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(24.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .shadow(
                elevation = 2.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.03f),
                spotColor = Color.Black.copy(alpha = 0.05f)
            )
            .clip(shape)
            .background(SuperficieBlanca)
            .border(1.dp, BordeTarjeta, shape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(padding),
        content = content
    )
}

// ---------------------------------------------------------------------------
// Botones.
// ---------------------------------------------------------------------------
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = PillShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Terracota,
            contentColor = Color.White,
            disabledContainerColor = Terracota.copy(alpha = 0.7f),
            disabledContentColor = Color.White.copy(alpha = 0.9f)
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 24.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Composable
fun OutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp),
    fillWidth: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = PillShape,
        border = BorderStroke(1.dp, AzulTinta.copy(alpha = 0.35f)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = SuperficieBlanca,
            contentColor = AzulTinta
        ),
        contentPadding = contentPadding,
        modifier = modifier
            .then(if (fillWidth) Modifier.fillMaxWidth() else Modifier)
            .height(52.dp)
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

// ---------------------------------------------------------------------------
// Chip de filtro / categoría.
// ---------------------------------------------------------------------------
@Composable
fun RemyChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    Row(
        modifier = modifier
            .height(40.dp)
            .clip(PillShape)
            .background(if (selected) AzulTinta else SuperficieBlanca)
            .border(1.dp, if (selected) AzulTinta else BordeTarjeta, PillShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (icon != null) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (selected) Color.White else TextoSecundario,
                modifier = Modifier.size(16.dp)
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) Color.White else TextoSecundario,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

// ---------------------------------------------------------------------------
// Campo de búsqueda en píldora blanca.
// ---------------------------------------------------------------------------
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun RemySearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(PillShape)
            .background(SuperficieBlanca)
            .border(1.dp, BordeTarjeta, PillShape)
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            Icons.Filled.Search,
            contentDescription = null,
            tint = TextoSecundario,
            modifier = Modifier.size(20.dp)
        )
        Box(modifier = Modifier.weight(1f)) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextoSecundario.copy(alpha = 0.75f),
                    maxLines = 1
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextoPrincipal),
                cursorBrush = SolidColor(Terracota),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = ImeAction.Search),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Insignias de estado.
// ---------------------------------------------------------------------------
enum class VarianteBadge { LISTA, FALTAN, PRONTO, CADUCADO, NEUTRO }

@Composable
fun StatusBadge(
    text: String,
    variante: VarianteBadge,
    modifier: Modifier = Modifier
) {
    val (fondo, frente) = when (variante) {
        VarianteBadge.LISTA -> AzulTinta to Color.White
        VarianteBadge.FALTAN -> AvisoFaltaBg to AvisoFaltaFg
        VarianteBadge.PRONTO -> AvisoProntoBg to AvisoProntoFg
        VarianteBadge.CADUCADO -> AvisoCaducadoBg to AvisoCaducadoFg
        VarianteBadge.NEUTRO -> AvisoNeutroBg to AvisoNeutroFg
    }
    Box(
        modifier = modifier
            .clip(PillShape)
            .background(fondo)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = frente,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

// ---------------------------------------------------------------------------
// Foto de receta. Prioridad: la foto que el usuario hizo al cocinar, despues
// la foto del catalogo y, si la receta no tiene ninguna, un bloque de color
// pastel tal y como aparece en el diseño de la app.
// ---------------------------------------------------------------------------
@Composable
fun RecipePhoto(
    model: Any?,
    nombre: String,
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(20.dp),
    contentScale: ContentScale = ContentScale.Crop
) {
    val tono = TONOS_PASTEL[indiceTonoPastel(nombre)]
    val fotoCatalogo = ImagenesReceta.para(nombre)
    Box(
        modifier = modifier
            .clip(shape)
            .background(tono),
        contentAlignment = Alignment.Center
    ) {
        if (model != null) {
            val contexto = LocalContext.current
            AsyncImage(
                model = ImageRequest.Builder(contexto)
                    .data(model)
                    .crossfade(true)
                    .build(),
                contentDescription = nombre,
                contentScale = contentScale,
                placeholder = ColorPainter(tono),
                error = ColorPainter(Color.Transparent),
                modifier = Modifier.fillMaxSize()
            )
        } else if (fotoCatalogo != null) {
            Image(
                painter = painterResource(fotoCatalogo),
                contentDescription = nombre,
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Icono circular de ingrediente según su categoría.
// ---------------------------------------------------------------------------
enum class CategoriaIcono(val fondo: Color) {
    CARNE(AvisoFaltaBg),
    PESCADO(AvisoNeutroBg),
    VERDURA(AvisoNeutroBg),
    LACTEO(AvisoProntoBg),
    CEREAL(AvisoProntoBg),
    ESPECIA(Melocoton),
    ACEITE(AvisoProntoBg),
    FRUTA(AvisoFaltaBg),
    HUEVO(AvisoProntoBg),
    OTRO(AvisoNeutroBg)
}

fun categoriaDeIngrediente(nombre: String): CategoriaIcono {
    val n = nombre.lowercase()
    return when {
        listOf("huevo", "huevos").any { n.contains(it) } -> CategoriaIcono.HUEVO
        listOf("aceite", "aove", "vinagre").any { n.contains(it) } -> CategoriaIcono.ACEITE
        listOf("sal", "pimienta", "comino", "pimentón", "pimenton", "orégano", "oregano", "canela", "azúcar", "azucar", "especia", "ajo en polvo").any { n.contains(it) } -> CategoriaIcono.ESPECIA
        listOf("leche", "queso", "yogur", "nata", "mantequilla", "lácteo", "lacteo").any { n.contains(it) } -> CategoriaIcono.LACTEO
        listOf("arroz", "pasta", "pan", "harina", "avena", "cereal", "macarrón", "macarron", "lenteja", "garbanzo", "fideo", "cuscús", "cuscus").any { n.contains(it) } -> CategoriaIcono.CEREAL
        listOf("pollo", "carne", "ternera", "cerdo", "pavo", "jamón", "jamon", "chorizo", "atún", "atun", "salmón", "salmon", "bacalao", "pescado", "gamba", "camarón", "camaron", "marisco").any { n.contains(it) } -> CategoriaIcono.CARNE
        listOf("manzana", "plátano", "platano", "fresa", "naranja", "limón", "limon", "fruta", "pera", "uva").any { n.contains(it) } -> CategoriaIcono.FRUTA
        listOf("tomate", "lechuga", "espinaca", "verdura", "zanahoria", "cebolla", "pimiento", "calabacín", "calabacin", "patata", "champiñón", "champinon", "brócoli", "brocoli", "ajo", "guisante").any { n.contains(it) } -> CategoriaIcono.VERDURA
        else -> CategoriaIcono.OTRO
    }
}

@Composable
fun IngredientIcon(
    nombre: String,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    categoria: String? = null
) {
    val cat = when {
        categoria.isNullOrBlank() -> categoriaDeIngrediente(nombre)
        else -> categoriaDeIngrediente(categoria)
    }
    val icono = when (cat) {
        CategoriaIcono.CARNE, CategoriaIcono.PESCADO -> Icons.Outlined.SetMeal
        CategoriaIcono.VERDURA -> Icons.Outlined.Eco
        CategoriaIcono.LACTEO -> Icons.Outlined.Icecream
        CategoriaIcono.CEREAL -> Icons.Outlined.Grain
        CategoriaIcono.ESPECIA -> Icons.Outlined.Spa
        CategoriaIcono.ACEITE -> Icons.Outlined.OilBarrel
        CategoriaIcono.FRUTA -> Icons.Outlined.BakeryDining
        CategoriaIcono.HUEVO -> Icons.Outlined.Egg
        CategoriaIcono.OTRO -> Icons.Outlined.Eco
    }
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(cat.fondo),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icono,
            contentDescription = null,
            tint = AzulTinta,
            modifier = Modifier.size(size * 0.48f)
        )
    }
}

// ---------------------------------------------------------------------------
// Barra inferior de navegación. Icono encima de la etiqueta, como en el diseño.
// ---------------------------------------------------------------------------
data class DestinoBarra(
    val ruta: String,
    val etiqueta: String,
    val icono: ImageVector
)

@Composable
fun RemyBottomBar(
    destinos: List<DestinoBarra>,
    rutaActual: String?,
    onNavegar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                ambientColor = Color.Black.copy(alpha = 0.05f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(SuperficieBlanca)
            .navigationBarsPadding()
            .padding(horizontal = 6.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        destinos.forEach { destino ->
            val activa = rutaActual == destino.ruta
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(PillShape)
                    .clickable { onNavegar(destino.ruta) }
                    .padding(vertical = 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (activa) AzulTinta else Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        destino.icono,
                        contentDescription = destino.etiqueta,
                        tint = if (activa) Color.White else TextoSecundario,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Text(
                    text = destino.etiqueta,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (activa) TextoPrincipal else TextoSecundario,
                    fontWeight = if (activa) FontWeight.Bold else FontWeight.SemiBold,
                    maxLines = 1
                )
            }
        }
    }
}
