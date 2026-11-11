package com.remy.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.remy.app.R
import com.remy.app.data.Alimento
import com.remy.app.data.Ingrediente
import com.remy.app.data.IngredienteConAlimento
import com.remy.app.data.PreferenciasRepository
import com.remy.app.data.Receta
import com.remy.app.data.RecetaDetalle
import com.remy.app.ui.components.FotoDelPlato
import com.remy.app.ui.components.IngredientIcon
import com.remy.app.ui.components.OutlineButton
import com.remy.app.ui.components.PrimaryButton
import com.remy.app.ui.components.RecipePhoto
import com.remy.app.ui.components.RemyCard
import com.remy.app.ui.components.RemyTopBar
import com.remy.app.ui.components.StatusBadge
import com.remy.app.ui.components.VarianteBadge
import com.remy.app.ui.components.fechaEnEspanol
import com.remy.app.ui.components.formatoCantidad
import com.remy.app.ui.theme.AvisoNeutroBg
import com.remy.app.ui.theme.AzulTinta
import com.remy.app.ui.theme.BordeTarjeta
import com.remy.app.ui.theme.FondoCrema
import com.remy.app.ui.theme.PillShape
import com.remy.app.ui.theme.REMYTheme
import com.remy.app.ui.theme.RemyShapes
import com.remy.app.ui.theme.SuperficieBlanca
import com.remy.app.ui.theme.Terracota
import com.remy.app.ui.theme.TextoPrincipal
import com.remy.app.ui.theme.TextoSecundario
import com.remy.app.ui.viewmodel.RecetaDetalleViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun RecetaDetalleScreen(
    id: Long,
    onVolver: () -> Unit,
    onPerfilClick: () -> Unit = {},
    viewModel: RecetaDetalleViewModel = viewModel(factory = RecetaDetalleViewModel.Factory),
) {
    LaunchedEffect(id) { viewModel.cargar(id) }
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val estadoCoccion by viewModel.estadoCoccion.collectAsStateWithLifecycle()
    var capturando by remember { mutableStateOf(false) }
    var racionesDeseadas by rememberSaveable(id) { mutableStateOf(1) }

    LaunchedEffect(estadoCoccion) {
        if (estadoCoccion is RecetaDetalleViewModel.EstadoCoccion.Guardado) {
            capturando = false
        }
    }

    when (val actual = estado) {
        RecetaDetalleViewModel.Estado.Cargando -> EstadoCargandoDetalle()
        RecetaDetalleViewModel.Estado.NoEncontrada -> EstadoNoEncontradoDetalle(onVolver)
        is RecetaDetalleViewModel.Estado.Encontrada -> if (capturando) {
            PantallaCapturaCoccion(
                id = id,
                racionesDeseadas = racionesDeseadas,
                estadoCoccion = estadoCoccion,
                onConfirmar = { foto, uri -> viewModel.marcarComoCocinada(id, foto, uri, racionesDeseadas) },
                onVolver = {
                    capturando = false
                    viewModel.limpiarCoccion()
                }
            )
        } else {
            DetalleReceta(
                detalle = actual.detalle,
                racionesDeseadas = racionesDeseadas,
                onCambiarRaciones = { racionesDeseadas = it },
                estadoCoccion = estadoCoccion,
                onVolver = onVolver,
                onEmpezarCoccion = { capturando = true },
                onLimpiarAviso = { viewModel.limpiarCoccion() }
            )
        }
    }
}

@Composable
private fun EstadoCargandoDetalle() {
    Box(modifier = Modifier.fillMaxSize().background(FondoCrema), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Terracota, strokeWidth = 4.dp)
    }
}

@Composable
private fun EstadoNoEncontradoDetalle(onVolver: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(FondoCrema).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.detalle_no_encontrada),
            style = MaterialTheme.typography.titleLarge,
            color = TextoPrincipal,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        OutlineButton(
            text = stringResource(R.string.detalle_volver_listado),
            onClick = onVolver,
            icon = Icons.AutoMirrored.Outlined.ArrowBack
        )
    }
}

@Composable
private fun PantallaCapturaCoccion(
    id: Long,
    racionesDeseadas: Int,
    estadoCoccion: RecetaDetalleViewModel.EstadoCoccion,
    onConfirmar: (android.graphics.Bitmap?, android.net.Uri?) -> Unit,
    onVolver: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().background(FondoCrema)) {
        RemyTopBar(titulo = stringResource(R.string.detalle_marcar_cocinada), onVolver = onVolver)
        CapturaPlato(
            estadoCoccion = estadoCoccion,
            alConfirmar = onConfirmar,
            alCancelar = onVolver,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun DetalleReceta(
    detalle: RecetaDetalle,
    racionesDeseadas: Int,
    onCambiarRaciones: (Int) -> Unit,
    estadoCoccion: RecetaDetalleViewModel.EstadoCoccion,
    onVolver: () -> Unit,
    onEmpezarCoccion: () -> Unit,
    onLimpiarAviso: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val preferencias = remember { PreferenciasRepository.delContexto(context) }
    val favoritas by preferencias.favoritas.collectAsStateWithLifecycle(initialValue = emptySet())
    val esFavorita = detalle.receta.id in favoritas

    ContenidoDetalle(
        detalle = detalle,
        racionesDeseadas = racionesDeseadas,
        onCambiarRaciones = onCambiarRaciones,
        esFavorita = esFavorita,
        onToggleFavorita = { scope.launch { preferencias.alternarFavorita(detalle.receta.id) } },
        estadoCoccion = estadoCoccion,
        onVolver = onVolver,
        onEmpezarCoccion = onEmpezarCoccion,
        onLimpiarAviso = onLimpiarAviso
    )
}

@Composable
private fun ContenidoDetalle(
    detalle: RecetaDetalle,
    racionesDeseadas: Int,
    onCambiarRaciones: (Int) -> Unit,
    esFavorita: Boolean,
    onToggleFavorita: () -> Unit,
    estadoCoccion: RecetaDetalleViewModel.EstadoCoccion,
    onVolver: () -> Unit,
    onEmpezarCoccion: () -> Unit,
    onLimpiarAviso: () -> Unit
) {
    val receta = detalle.receta
    val porciones = receta.porciones.coerceAtLeast(1)
    Scaffold(
        containerColor = FondoCrema,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            ColumnaAccionDetalle(
                yaCocinada = receta.fechaCocinada != null,
                estadoCoccion = estadoCoccion,
                onEmpezarCoccion = onEmpezarCoccion,
                onLimpiarAviso = onLimpiarAviso
            )
        }
    ) { pad ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = pad.calculateBottomPadding() + 24.dp)
        ) {
            item {
                HeroDetalle(receta = receta, esFavorita = esFavorita, onToggleFavorita = onToggleFavorita, onVolver = onVolver)
            }
            item { InfoRecetaDetalle(receta, porciones, racionesDeseadas, onCambiarRaciones) }
            item { TarjetaIngredientesDetalle(detalle, porciones, racionesDeseadas) }
            if (receta.pasos.isNotEmpty()) item { TarjetaPasosDetalle(receta.pasos) }
            val fechaCocinada = receta.fechaCocinada
            if (fechaCocinada != null) item { TarjetaCocinadaDetalle(fechaCocinada, receta.fotoCocinada) }
        }
    }
}

@Composable
private fun InfoRecetaDetalle(
    receta: Receta,
    porciones: Int,
    racionesDeseadas: Int,
    onCambiarRaciones: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = receta.nombre, style = MaterialTheme.typography.headlineMedium, color = TextoPrincipal)
        DatosRecetaDetalle(receta, porciones, racionesDeseadas)
        SelectorRaciones(
            raciones = racionesDeseadas,
            onMenos = { if (racionesDeseadas > 1) onCambiarRaciones(racionesDeseadas - 1) },
            onMas = { onCambiarRaciones(racionesDeseadas + 1) }
        )
    }
}

@Composable
private fun DatosRecetaDetalle(receta: Receta, porciones: Int, racionesDeseadas: Int) {
    val kcalPara = (receta.kcal.toFloat() * racionesDeseadas / porciones).roundToInt()
    val proteinasPara = (receta.proteinas.toFloat() * racionesDeseadas / porciones).roundToInt()
    val hidratosPara = (receta.hidratos.toFloat() * racionesDeseadas / porciones).roundToInt()
    val grasasPara = (receta.grasas.toFloat() * racionesDeseadas / porciones).roundToInt()
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (receta.tiempoMinutos > 0) DatoPildora(Icons.Outlined.Schedule, stringResource(R.string.comun_minutos, receta.tiempoMinutos))
        if (receta.kcal > 0) DatoPildora(Icons.Outlined.LocalFireDepartment, stringResource(R.string.comun_kcal, kcalPara))
        if (receta.proteinas > 0) DatoPildora(Icons.Outlined.FitnessCenter, stringResource(R.string.detalle_proteina, proteinasPara))
        if (receta.hidratos > 0) DatoPildora(Icons.Outlined.FitnessCenter, stringResource(R.string.detalle_hidratos, hidratosPara))
        if (receta.grasas > 0) DatoPildora(Icons.Outlined.FitnessCenter, stringResource(R.string.detalle_grasas, grasasPara))
        if (receta.tipoPlato.isNotBlank()) DatoPildora(Icons.Outlined.Restaurant, receta.tipoPlato)
        if (receta.regionOrigen.isNotBlank()) DatoPildora(Icons.Outlined.Public, receta.regionOrigen)
    }
}

@Composable
private fun TarjetaIngredientesDetalle(detalle: RecetaDetalle, porciones: Int, racionesDeseadas: Int) {
    val total = detalle.ingredientes.size
    val disponibles = detalle.ingredientes.count { it.alimento != null }
    val faltan = (total - disponibles).coerceAtLeast(0)
    RemyCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
        CabeceraIngredientes(racionesDeseadas, faltan, total)
        Spacer(Modifier.height(8.dp))
        detalle.ingredientes.forEachIndexed { indice, par ->
            if (indice > 0) HorizontalDivider(color = BordeTarjeta, thickness = 1.dp)
            FilaIngredienteDetalle(par = par, porcionesReceta = porciones, racionesDeseadas = racionesDeseadas)
        }
        if (total == 0) {
            Text(text = stringResource(R.string.detalle_sin_ingredientes), style = MaterialTheme.typography.bodyMedium, color = TextoSecundario)
        }
    }
}

@Composable
private fun CabeceraIngredientes(racionesDeseadas: Int, faltan: Int, total: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (racionesDeseadas == 1) stringResource(R.string.detalle_ingredientes_para_una)
            else stringResource(R.string.detalle_ingredientes_para, racionesDeseadas),
            style = MaterialTheme.typography.titleLarge,
            color = TextoPrincipal
        )
        if (faltan == 0 && total > 0) {
            StatusBadge(text = stringResource(R.string.sugerencias_badge_lista), variante = VarianteBadge.LISTA)
        } else if (faltan > 0) {
            StatusBadge(text = stringResource(R.string.sugerencias_badge_faltan, faltan), variante = VarianteBadge.FALTAN)
        }
    }
}

@Composable
private fun TarjetaPasosDetalle(pasos: List<String>) {
    RemyCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)) {
        Text(text = stringResource(R.string.detalle_pasos), style = MaterialTheme.typography.titleLarge, color = TextoPrincipal)
        Spacer(Modifier.height(4.dp))
        pasos.forEachIndexed { indice, paso -> FilaPaso(indice + 1, paso) }
    }
}

@Composable
private fun FilaPaso(numero: Int, paso: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(28.dp).clip(CircleShape).background(AzulTinta),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "$numero", style = MaterialTheme.typography.labelLarge, color = Color.White)
        }
        Text(text = paso, style = MaterialTheme.typography.bodyMedium, color = TextoSecundario, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun TarjetaCocinadaDetalle(fecha: Long, rutaFoto: String?) {
    RemyCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(imageVector = Icons.Outlined.Restaurant, contentDescription = null, tint = Terracota, modifier = Modifier.size(20.dp))
            Text(
                text = stringResource(R.string.detalle_cocinada_el, fechaEnEspanol(fecha)),
                style = MaterialTheme.typography.titleMedium,
                color = TextoPrincipal
            )
        }
        Spacer(Modifier.height(8.dp))
        FotoDelPlato(
            ruta = rutaFoto,
            modifier = Modifier.fillMaxWidth().heightIn(max = 220.dp).clip(RemyShapes.small),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun HeroDetalle(
    receta: Receta,
    esFavorita: Boolean,
    onToggleFavorita: () -> Unit,
    onVolver: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth().height(320.dp)) {
        RecipePhoto(model = receta.fotoCocinada, nombre = receta.nombre, modifier = Modifier.fillMaxSize(), shape = RectangleShape)
        Box(
            modifier = Modifier.fillMaxWidth().height(140.dp)
                .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.35f), Color.Transparent)))
        )
        BotonRedondo(
            icon = Icons.AutoMirrored.Outlined.ArrowBack,
            descripcion = stringResource(R.string.comun_volver),
            onClick = onVolver,
            modifier = Modifier.align(Alignment.TopStart).statusBarsPadding().padding(start = 16.dp, top = 12.dp)
        )
        BotonRedondo(
            icon = if (esFavorita) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
            descripcion = stringResource(if (esFavorita) R.string.recetas_quitar else R.string.recetas_guardar),
            onClick = onToggleFavorita,
            tint = if (esFavorita) Terracota else TextoPrincipal,
            modifier = Modifier.align(Alignment.TopEnd).statusBarsPadding().padding(end = 16.dp, top = 12.dp)
        )
    }
}

@Composable
private fun BotonRedondo(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    descripcion: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = TextoPrincipal
) {
    Box(
        modifier = modifier
            .size(44.dp)
            .shadow(3.dp, CircleShape, ambientColor = Color.Black.copy(alpha = 0.12f), spotColor = Color.Black.copy(alpha = 0.16f))
            .clip(CircleShape)
            .background(Color.White)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = descripcion, tint = tint, modifier = Modifier.size(22.dp))
    }
}

@Composable
private fun DatoPildora(icon: androidx.compose.ui.graphics.vector.ImageVector, texto: String) {
    Row(
        modifier = Modifier.clip(PillShape).background(AvisoNeutroBg).padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(icon, contentDescription = null, tint = TextoPrincipal, modifier = Modifier.size(16.dp))
        Text(text = texto, style = MaterialTheme.typography.labelLarge, color = TextoPrincipal)
    }
}

@Composable
private fun FilaIngredienteDetalle(par: IngredienteConAlimento, porcionesReceta: Int = 1, racionesDeseadas: Int = 1) {
    val nombre = par.alimento?.nombre ?: stringResource(R.string.detalle_ingrediente)
    val tiene = par.alimento != null
    val cantidadEscalada = par.ingrediente.cantidad * racionesDeseadas / porcionesReceta.coerceAtLeast(1)
    val cantidad = "${cantidadEscalada.formatoCantidad()} ${par.ingrediente.unidad}"

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        IngredientIcon(nombre = nombre)
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = nombre, style = MaterialTheme.typography.titleSmall, color = TextoPrincipal)
            Text(text = cantidad, style = MaterialTheme.typography.bodyMedium, color = TextoSecundario)
        }
        if (tiene) {
            Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(AzulTinta), contentAlignment = Alignment.Center) {
                Icon(imageVector = Icons.Outlined.Check, contentDescription = stringResource(R.string.detalle_en_despensa), tint = Color.White, modifier = Modifier.size(16.dp))
            }
        } else {
            StatusBadge(text = stringResource(R.string.detalle_falta), variante = VarianteBadge.FALTAN)
        }
    }
}

@Composable
private fun SelectorRaciones(raciones: Int, onMenos: () -> Unit, onMas: () -> Unit) {
    RemyCard(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = stringResource(R.string.detalle_raciones_titulo), style = MaterialTheme.typography.titleMedium, color = TextoPrincipal)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BotonRacion(Icons.Outlined.Remove, stringResource(R.string.detalle_menos_raciones), enabled = raciones > 1, destacado = raciones > 1, onClick = onMenos)
                Text(text = raciones.toString(), style = MaterialTheme.typography.titleLarge, color = TextoPrincipal, modifier = Modifier.width(28.dp), textAlign = TextAlign.Center)
                BotonRacion(Icons.Outlined.Add, stringResource(R.string.detalle_mas_raciones), enabled = true, destacado = true, onClick = onMas)
            }
        }
    }
}

@Composable
private fun BotonRacion(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    descripcion: String,
    enabled: Boolean,
    destacado: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier.size(44.dp).clip(CircleShape)
            .background(if (destacado) Terracota else BordeTarjeta)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = icon, contentDescription = descripcion, tint = if (destacado) Color.White else TextoSecundario)
    }
}

@Composable
private fun ColumnaAccionDetalle(
    yaCocinada: Boolean,
    estadoCoccion: RecetaDetalleViewModel.EstadoCoccion,
    onEmpezarCoccion: () -> Unit,
    onLimpiarAviso: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().background(SuperficieBlanca).navigationBarsPadding().padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val aviso = (estadoCoccion as? RecetaDetalleViewModel.EstadoCoccion.Guardado)?.aviso
        if (aviso != null) AvisoCoccionGuardada(aviso, onLimpiarAviso)
        PrimaryButton(
            text = stringResource(if (yaCocinada) R.string.detalle_volver_cocinar else R.string.detalle_marcar_cocinada),
            onClick = onEmpezarCoccion,
            modifier = Modifier.fillMaxWidth(),
            enabled = estadoCoccion !is RecetaDetalleViewModel.EstadoCoccion.Guardando,
            icon = Icons.Outlined.Restaurant
        )
    }
}

@Composable
private fun AvisoCoccionGuardada(aviso: String, onLimpiar: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = aviso, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
        TextButton(onClick = onLimpiar) { Text(stringResource(R.string.comun_entendido), color = Terracota) }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFBF5)
@Composable
private fun RecetaDetallePreview() {
    val receta = Receta(
        id = 1, nombre = "Lentejas con verduras", kcal = 420, proteinas = 22,
        regionOrigen = "Castilla y León", tipoPlato = "Principal", tiempoMinutos = 35, porciones = 4,
        pasos = listOf("Sofríe la cebolla y la zanahoria picadas.", "Añade las lentejas y el caldo, y cocina 25 minutos.")
    )
    val ingredientes = listOf(
        IngredienteConAlimento(
            ingrediente = Ingrediente(id = 1, recetaId = 1, alimentoId = 1, cantidad = 200.0, unidad = "g"),
            alimento = Alimento(id = 1, nombre = "Lentejas", cantidad = 500.0, unidad = "g")
        ),
        IngredienteConAlimento(
            ingrediente = Ingrediente(id = 2, recetaId = 1, alimentoId = 2, cantidad = 1.0, unidad = "ud"),
            alimento = null
        )
    )
    REMYTheme {
        ContenidoDetalle(
            detalle = RecetaDetalle(receta, ingredientes),
            racionesDeseadas = 2,
            onCambiarRaciones = {},
            esFavorita = true,
            onToggleFavorita = {},
            estadoCoccion = RecetaDetalleViewModel.EstadoCoccion.Normal,
            onVolver = {},
            onEmpezarCoccion = {},
            onLimpiarAviso = {}
        )
    }
}
