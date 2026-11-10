package com.remy.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Egg
import androidx.compose.material.icons.outlined.Grain
import androidx.compose.material.icons.outlined.Kitchen
import androidx.compose.material.icons.outlined.OilBarrel
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SetMeal
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.remy.app.R
import com.remy.app.data.Alimento
import com.remy.app.ui.EstadoCarga
import com.remy.app.ui.components.FilterPill
import com.remy.app.ui.components.IconCircle
import com.remy.app.ui.components.PantallaCargando
import com.remy.app.ui.components.PantallaError
import com.remy.app.ui.components.RemyCta
import com.remy.app.ui.components.SearchPill
import com.remy.app.ui.components.SectionCard
import com.remy.app.ui.components.categoriaDe
import com.remy.app.ui.components.formatoCantidad
import com.remy.app.ui.theme.Amber600
import com.remy.app.ui.theme.DetailGreen900
import com.remy.app.ui.theme.DialogShape24
import com.remy.app.ui.theme.Emerald300
import com.remy.app.ui.theme.Green100
import com.remy.app.ui.theme.Green600
import com.remy.app.ui.theme.Green700
import com.remy.app.ui.theme.Green800
import com.remy.app.ui.theme.Mint
import com.remy.app.ui.theme.MintBorder
import com.remy.app.ui.theme.PillShape
import com.remy.app.ui.theme.Red100
import com.remy.app.ui.theme.Red600
import com.remy.app.ui.theme.Slate100
import com.remy.app.ui.theme.Slate200
import com.remy.app.ui.theme.Slate500
import com.remy.app.ui.theme.Slate600
import com.remy.app.ui.theme.Slate900
import com.remy.app.ui.viewmodel.DespensaViewModel
import com.remy.app.ui.viewmodel.ErroresAlimento

// Estado cosmético derivado de la categoría (los mockups muestran estas etiquetas)
private data class EstadoInfo(val texto: String, val color: Color, val conCalendario: Boolean = false)

private fun estadoDe(nombre: String): EstadoInfo = when {
    listOf("huevo", "huevos").any { nombre.contains(it, true) } ->
        EstadoInfo("Óptimo", Color(0xFFA33900))
    listOf("leche", "queso", "yogur", "mantequilla", "nata").any { nombre.contains(it, true) } ->
        EstadoInfo("Consumir pronto", Color(0xFFA33900), conCalendario = true)
    listOf("tomate", "tomates", "lechuga", "cebolla", "pimiento", "verdura", "espinaca", "ajo", "zanahoria", "calabac").any { nombre.contains(it, true) } ->
        EstadoInfo("Usar pronto", Green700)
    listOf("pollo", "pechuga", "pescado", "salmón", "salmon", "carne", "ternera", "cerdo", "atún", "atun", "bacalao", "camarón", "camaron", "gamba").any { nombre.contains(it, true) } ->
        EstadoInfo("Consumir en 2 días", Green600)
    else -> EstadoInfo("Básico esencial", Slate600)
}

private fun iconoDe(nombre: String): ImageVector = when {
    listOf("huevo", "huevos").any { nombre.contains(it, true) } -> Icons.Outlined.Egg
    listOf("tomate", "tomates", "lechuga", "espinaca", "verdura", "ajo", "cebolla", "pimiento", "zanahoria").any { nombre.contains(it, true) } -> Icons.Outlined.Eco
    listOf("aceite", "aove").any { nombre.contains(it, true) } -> Icons.Outlined.OilBarrel
    listOf("pollo", "pechuga", "carne", "ternera", "cerdo", "pescado", "salmón", "salmon", "atún", "atun", "bacalao", "camarón", "camaron", "gamba").any { nombre.contains(it, true) } -> Icons.Outlined.SetMeal
    listOf("arroz", "pasta", "cereal", "pan", "harina", "avena").any { nombre.contains(it, true) } -> Icons.Outlined.Grain
    else -> Icons.Outlined.Kitchen
}

@Composable
fun DespensaScreen(
    viewModel: DespensaViewModel = viewModel(factory = DespensaViewModel.Factory),
    onVerSugerencias: () -> Unit = {}
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val alimentos = when (val actual = estado) {
        is EstadoCarga.Datos -> actual.datos
        else -> emptyList()
    }
    var mostrarFormulario by rememberSaveable { mutableStateOf(false) }
    var editando by remember { mutableStateOf<Alimento?>(null) }
    var confirmandoBorrado by remember { mutableStateOf<Alimento?>(null) }
    var query by rememberSaveable { mutableStateOf("") }
    var chipActivo by rememberSaveable { mutableStateOf("Todos") }

    fun abrirFormulario(alimento: Alimento?) {
        editando = alimento
        mostrarFormulario = true
    }

    val chips = listOf("Todos", "Frescos", "Despensa", "Especias", "Lácteos")
    val filtrados = alimentos.filter { a ->
        (query.isBlank() || a.nombre.contains(query, ignoreCase = true)) &&
            coincideChip(chipActivo, a.nombre)
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        containerColor = Mint,
        topBar = { DespensaHeader() },
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .padding(end = 16.dp, bottom = 80.dp)
                    .size(56.dp)
                    .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = Green600.copy(alpha = 0.45f), ambientColor = Green600.copy(alpha = 0.30f))
                    .clip(RoundedCornerShape(16.dp))
                    .background(Green600)
                    .clickable { abrirFormulario(null) },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Añadir alimento", tint = Color.White, modifier = Modifier.size(28.dp))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when (estado) {
                is EstadoCarga.Cargando -> {
                    PantallaCargando("Cargando tu despensa…")
                    return@Column
                }

                is EstadoCarga.Error -> {
                    PantallaError(
                        mensaje = (estado as EstadoCarga.Error).mensaje,
                        onReintentar = viewModel::reintentar
                    )
                    return@Column
                }

                is EstadoCarga.Datos -> Unit
            }

            if (alimentos.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Aún no tienes alimentos", style = MaterialTheme.typography.titleMedium, color = Slate900)
                    Text(
                        "Añade tu primer ingrediente y empieza a cocinar sin desperdicio.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate600,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                    RemyCta(
                        text = "Añadir alimento",
                        onClick = { abrirFormulario(null) },
                        modifier = Modifier.padding(top = 16.dp),
                        icon = Icons.Filled.Add
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Buscador funcional (filtra por nombre)
                    item {
                        SearchPill(
                            value = query,
                            onValueChange = { query = it },
                            placeholder = "Buscar ingredientes, marcas...",
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = Icons.Outlined.Search,
                            iconTint = Slate500,
                            trailingIcon = if (query.isNotEmpty()) Icons.Outlined.Close else null,
                            onTrailingClick = { query = "" }
                        )
                    }
                    // Chips de categoría (filtro por palabras clave, sin tocar la BD)
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            chips.forEach { c ->
                                FilterPill(
                                    label = c,
                                    active = c == chipActivo,
                                    onClick = { chipActivo = c },
                                    leadingIcon = if (c == chipActivo) Icons.Filled.Check else null
                                )
                            }
                        }
                    }
                    // Banner resumen
                    item {
                        SectionCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            padding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconCircle(size = 32.dp, bg = Green100, shadow = false) {
                                    Icon(Icons.Outlined.Kitchen, contentDescription = null, tint = Green600, modifier = Modifier.size(18.dp))
                                }
                                Spacer(Modifier.width(10.dp))
                                Column(Modifier.weight(1f)) {
                                    Text("${alimentos.size} ingredientes listos", style = MaterialTheme.typography.titleSmall, color = Slate900)
                                    Text(
                                        "Todo listo para sugerirte recetas",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Slate600,
                                        fontWeight = FontWeight.Normal
                                    )
                                }
                                Row(
                                    modifier = Modifier
                                        .clip(PillShape)
                                        .background(Green100)
                                        .padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Icon(Icons.Outlined.Visibility, contentDescription = null, tint = Green800, modifier = Modifier.size(14.dp))
                                    Text("Ver Vacío", color = Green800, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    if (filtrados.isEmpty()) {
                        item {
                            Text(
                                "No hay ingredientes que coincidan con la búsqueda.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Slate600,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }

                    items(filtrados, key = { it.id }) { alimento ->
                        FoodItemCard(
                            alimento = alimento,
                            onEditar = { abrirFormulario(alimento) },
                            onBorrar = { confirmandoBorrado = alimento }
                        )
                    }

                    // HintCard → Sugerencias
                    item {
                        SectionCard(
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                            shape = MaterialTheme.shapes.medium,
                            border = MintBorder,
                            glow = true,
                            padding = androidx.compose.foundation.layout.PaddingValues(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable(onClick = onVerSugerencias)) {
                                IconCircle(size = 40.dp, bg = Green100, shadow = false) {
                                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = Green600, modifier = Modifier.size(22.dp))
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text("¿Qué puedes cocinar hoy?", style = MaterialTheme.typography.titleSmall, color = Slate900)
                                    Text(
                                        "Con tus ${alimentos.size} ingredientes tienes recetas disponibles.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Slate600
                                    )
                                }
                                Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = Green600, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarFormulario) {
        FormularioAlimentoDialog(
            alimento = editando,
            onGuardar = { nombre, cantidad, unidad ->
                viewModel.guardar(editando, nombre, cantidad, unidad)
            },
            onCerrar = { mostrarFormulario = false }
        )
    }

    confirmandoBorrado?.let { alimento ->
        ConfirmarBorrarDialog(
            alimento = alimento,
            onConfirmar = {
                viewModel.borrar(alimento)
                confirmandoBorrado = null
            },
            onCancelar = { confirmandoBorrado = null }
        )
    }
}

// Chip de la fila de alimento según los mockups HTML (null = sin chip, p. ej. aceite/especias)
private data class ChipItem(val label: String, val bg: Color, val fg: Color, val dot: Color? = null)

private fun chipDe(nombre: String): ChipItem? = when {
    listOf("aceite", "aove").any { nombre.contains(it, true) } -> null
    esEspecia(nombre) -> null
    listOf("arroz", "pasta", "cereal", "pan", "harina", "avena", "lenteja", "garbanzo", "quinoa", "maíz", "maiz", "espagu", "macarr", "fideos").any { nombre.contains(it, true) } ->
        ChipItem("Cereales", Color(0xFFE2E7FF), Color(0xFF3E4A3D))
    listOf("huevo", "huevos", "leche", "queso", "yogur", "mantequilla", "nata").any { nombre.contains(it, true) } ->
        ChipItem("Frescos", Color(0xFFFFDBCE), Color(0xFF7F2B00), dot = Color(0xFFA33900))
    listOf("pollo", "pechuga", "carne", "ternera", "cerdo", "pescado", "salmón", "salmon", "atún", "atun", "bacalao", "camarón", "camaron", "gamba", "jamón", "jamon", "tofu").any { nombre.contains(it, true) } ->
        ChipItem("Proteína", Green100, Green800)
    listOf("tomate", "tomates", "lechuga", "cebolla", "pimiento", "verdura", "espinaca", "ajo", "zanahoria", "calabac").any { nombre.contains(it, true) } ->
        ChipItem("Verdura", Emerald300, DetailGreen900)
    else -> null
}

private fun esEspecia(nombre: String): Boolean =
    nombre.trim().equals("sal", true) ||
        listOf("azúcar", "azucar", "pimienta", "especia", "orégano", "oregano", "comino", "azafrán", "pimentón").any { nombre.contains(it, true) }

// Chips de filtro fijos de los mockups: Todos / Frescos / Despensa / Especias / Lácteos
private fun coincideChip(chip: String, nombre: String): Boolean {
    val fresco = listOf("huevo", "huevos", "tomate", "tomates", "lechuga", "cebolla", "pimiento", "verdura", "espinaca", "ajo", "zanahoria", "calabac").any { nombre.contains(it, true) }
    val lacteo = listOf("leche", "queso", "yogur", "mantequilla", "nata").any { nombre.contains(it, true) }
    return when (chip) {
        "Todos" -> true
        "Frescos" -> fresco
        "Especias" -> esEspecia(nombre)
        "Lácteos" -> lacteo
        else -> !fresco && !lacteo && !esEspecia(nombre) // "Despensa": básicos
    }
}

@Composable
private fun ChipCategoria(nombre: String) {
    val chip = chipDe(nombre) ?: return
    Row(
        modifier = Modifier
            .clip(PillShape)
            .background(chip.bg)
            .padding(horizontal = 8.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        chip.dot?.let { punto ->
            Box(Modifier.size(6.dp).clip(CircleShape).background(punto))
        }
        Text(
            chip.label,
            style = MaterialTheme.typography.labelSmall,
            color = chip.fg,
            letterSpacing = 0.sp
        )
    }
}

@Composable
private fun DespensaHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, shape = androidx.compose.ui.graphics.RectangleShape, ambientColor = Color.Black.copy(alpha = 0.05f), spotColor = Color.Black.copy(alpha = 0.06f))
            .background(Mint.copy(alpha = 0.95f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.remy_logo),
                contentDescription = "Logo REMY",
                modifier = Modifier.size(56.dp).clip(CircleShape)
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    "REMY",
                    style = MaterialTheme.typography.labelSmall,
                    color = Green600,
                    letterSpacing = 2.sp
                )
                Text("Despensa", style = MaterialTheme.typography.headlineMedium, color = Slate900)
            }
            Box(
                modifier = Modifier.size(32.dp).clip(CircleShape).background(Green600),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Person, contentDescription = "Perfil", tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }
        HorizontalDivider(color = MintBorder, thickness = 1.dp)
    }
}

@Composable
private fun FoodItemCard(
    alimento: Alimento,
    onEditar: () -> Unit,
    onBorrar: () -> Unit
) {
    val cat = categoriaDe(alimento.nombre)
    val estado = estadoDe(alimento.nombre)
    SectionCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        padding = androidx.compose.foundation.layout.PaddingValues(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconCircle(
                size = 48.dp,
                bg = cat.colors.bg,
                border = cat.colors.border
            ) {
                Icon(iconoDe(alimento.nombre), contentDescription = null, tint = cat.colors.fg, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        alimento.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        color = Slate900,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    ChipCategoria(alimento.nombre)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 3.dp)
                ) {
                    Text(
                        "${alimento.cantidad.formatoCantidad()} ${alimento.unidad}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Green600,
                        fontWeight = FontWeight.Medium
                    )
                    Box(Modifier.size(4.dp).clip(CircleShape).background(Slate300Or()))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        if (estado.conCalendario) {
                            Icon(Icons.Outlined.CalendarToday, contentDescription = null, tint = estado.color, modifier = Modifier.size(13.dp))
                        }
                        Text(
                            estado.texto,
                            style = MaterialTheme.typography.labelSmall,
                            color = estado.color,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.sp
                        )
                    }
                }
            }
            Spacer(Modifier.width(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                RoundIconBtn(Icons.Outlined.Edit, "Editar", onClick = onEditar)
                RoundIconBtn(Icons.Outlined.Delete, "Borrar", onClick = onBorrar)
            }
        }
    }
}

@Composable
private fun RoundIconBtn(icon: ImageVector, desc: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Slate100)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = desc, tint = Slate600, modifier = Modifier.size(20.dp))
    }
}

private fun Slate300Or(): Color = Slate200

// ---------------------------------------------------------------------------
// Diálogo "¿Eliminar ingrediente?" — SPEC-FRONT §2.4
// ---------------------------------------------------------------------------
@Composable
private fun ConfirmarBorrarDialog(
    alimento: Alimento,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    val cat = categoriaDe(alimento.nombre)
    val estado = estadoDe(alimento.nombre)
    AlertDialog(
        onDismissRequest = onCancelar,
        containerColor = Mint,
        shape = DialogShape24,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconCircle(size = 56.dp, bg = Red100, shadow = false) {
                    Icon(Icons.Outlined.DeleteForever, contentDescription = null, tint = Red600, modifier = Modifier.size(28.dp))
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    "¿Eliminar ingrediente?",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Slate900,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    "Este ingrediente se retirará de tu despensa y dejará de tenerse en cuenta para sugerir recetas. Esta acción no se puede deshacer.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Slate500,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        },
        text = {
            // Vista previa del ingrediente
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(Color.White)
                    .border(1.dp, Green100, MaterialTheme.shapes.medium)
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    IconCircle(size = 44.dp, bg = cat.colors.bg, border = cat.colors.border, shadow = false) {
                        Icon(iconoDe(alimento.nombre), contentDescription = null, tint = cat.colors.fg, modifier = Modifier.size(22.dp))
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(alimento.nombre, style = MaterialTheme.typography.titleSmall, color = Slate900)
                            ChipCategoria(alimento.nombre)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(top = 2.dp)) {
                            Text(
                                "${alimento.cantidad.formatoCantidad()} ${alimento.unidad}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Green600,
                                fontWeight = FontWeight.Medium
                            )
                            Text(" • ", style = MaterialTheme.typography.bodyMedium, color = Slate500)
                            Text(
                                estado.texto,
                                style = MaterialTheme.typography.bodyMedium,
                                color = estado.color,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Slate100)
                        .clickable(onClick = onCancelar),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Cancelar", color = Slate600, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .shadow(8.dp, RoundedCornerShape(12.dp), spotColor = Red600.copy(alpha = 0.35f))
                        .clip(RoundedCornerShape(12.dp))
                        .background(Red600)
                        .clickable(onClick = onConfirmar),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Outlined.Delete, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Text("Eliminar", color = Color.White, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        dismissButton = {}
    )
}

// ---------------------------------------------------------------------------
// Formulario de alimento (añadir/editar) — funcionalidad intacta, estilo mockup
// ---------------------------------------------------------------------------
@Composable
private fun FormularioAlimentoDialog(
    alimento: Alimento?,
    onGuardar: (String, String, String) -> ErroresAlimento,
    onCerrar: () -> Unit,
) {
    var nombre by rememberSaveable { mutableStateOf(alimento?.nombre ?: "") }
    var cantidad by rememberSaveable { mutableStateOf(alimento?.cantidad?.formatoCantidad() ?: "") }
    var unidad by rememberSaveable { mutableStateOf(alimento?.unidad ?: "") }
    var errores by remember { mutableStateOf(ErroresAlimento()) }

    AlertDialog(
        onDismissRequest = onCerrar,
        containerColor = Mint,
        shape = DialogShape24,
        title = {
            Text(
                if (alimento == null) "Añadir alimento" else "Editar alimento",
                style = MaterialTheme.typography.headlineSmall,
                color = Slate900
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    if (alimento == null) "Indica el nombre, la cantidad y la unidad del ingrediente."
                    else "Actualiza los datos del ingrediente.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Slate600
                )
                FormField(
                    value = nombre, onValueChange = { nombre = it },
                    label = "Nombre", isError = errores.nombre != null,
                    error = errores.nombre
                )
                FormField(
                    value = cantidad, onValueChange = { cantidad = it },
                    label = "Cantidad", isError = errores.cantidad != null,
                    error = errores.cantidad,
                    keyboardType = KeyboardType.Decimal
                )
                FormField(
                    value = unidad, onValueChange = { unidad = it },
                    label = "Unidad (g, ml, u...)", isError = errores.unidad != null,
                    error = errores.unidad
                )
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onCerrar, modifier = Modifier.weight(1f)) {
                    Text("Cancelar", color = Slate600, fontWeight = FontWeight.SemiBold)
                }
                Box(Modifier.weight(1.4f)) {
                    RemyCta(
                        text = "Guardar",
                        onClick = {
                            val resultado = onGuardar(nombre, cantidad, unidad)
                            if (resultado.hayErrores) errores = resultado else onCerrar()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        height = 44.dp
                    )
                }
            }
        },
        dismissButton = {}
    )
}

@Composable
private fun FormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean,
    error: String?,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        isError = isError,
        supportingText = { error?.let { Text(it) } },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = MaterialTheme.shapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = Green600,
            unfocusedBorderColor = Green100
        ),
        modifier = Modifier.fillMaxWidth()
    )
}
