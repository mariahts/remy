package com.remy.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Kitchen
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.remy.app.R
import com.remy.app.data.model.Alimento
import com.remy.app.ui.components.CategoriaIcono
import com.remy.app.ui.components.ContenidoConEstado
import com.remy.app.ui.components.IngredientIcon
import com.remy.app.ui.components.PrimaryButton
import com.remy.app.ui.components.RemyCard
import com.remy.app.ui.components.RemyChip
import com.remy.app.ui.components.RemyLogo
import com.remy.app.ui.components.RemySearchField
import com.remy.app.ui.components.RemyTopBar
import com.remy.app.ui.components.categoriaDeIngrediente
import com.remy.app.ui.components.formatoCantidad
import com.remy.app.ui.theme.AvisoCaducadoBg
import com.remy.app.ui.theme.AvisoCaducadoFg
import com.remy.app.ui.theme.AzulTinta
import com.remy.app.ui.theme.BordeTarjeta
import com.remy.app.ui.theme.FondoCrema
import com.remy.app.ui.theme.Melocoton
import com.remy.app.ui.theme.PillShape
import com.remy.app.ui.theme.REMYTheme
import com.remy.app.ui.theme.SuperficieBlanca
import com.remy.app.ui.theme.Terracota
import com.remy.app.ui.theme.TextoPrincipal
import com.remy.app.ui.theme.TextoSecundario
import com.remy.app.ui.viewmodel.BienvenidaViewModel
import com.remy.app.ui.viewmodel.DespensaViewModel
import com.remy.app.ui.viewmodel.ErroresAlimento

private enum class FiltroDespensa(val etiqueta: Int) {
    TODOS(R.string.despensa_todos),
    FRESCOS(R.string.despensa_frescos),
    DESPENSA(R.string.despensa_despensa),
    ESPECIAS(R.string.despensa_especias),
    LACTEOS(R.string.despensa_lacteos)
}

private fun FiltroDespensa.contiene(nombre: String): Boolean {
    val cat = categoriaDeIngrediente(nombre)
    return when (this) {
        FiltroDespensa.TODOS -> true
        FiltroDespensa.FRESCOS -> cat in listOf(
            CategoriaIcono.VERDURA, CategoriaIcono.FRUTA, CategoriaIcono.CARNE,
            CategoriaIcono.PESCADO, CategoriaIcono.LACTEO, CategoriaIcono.HUEVO
        )
        FiltroDespensa.DESPENSA -> cat in listOf(
            CategoriaIcono.CEREAL, CategoriaIcono.ACEITE
        )
        FiltroDespensa.ESPECIAS -> cat == CategoriaIcono.ESPECIA
        FiltroDespensa.LACTEOS -> cat == CategoriaIcono.LACTEO
    }
}

@Composable
fun DespensaScreen(
    viewModel: DespensaViewModel = viewModel(factory = DespensaViewModel.Factory),
    bienvenidaViewModel: BienvenidaViewModel = viewModel(factory = BienvenidaViewModel.Factory),
    onVerSugerencias: () -> Unit = {},
    onPerfilClick: () -> Unit = {}
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    var mostrarFormulario by rememberSaveable { mutableStateOf(false) }
    var mostrarBasicos by rememberSaveable { mutableStateOf(false) }
    var editando by remember { mutableStateOf<Alimento?>(null) }
    var query by rememberSaveable { mutableStateOf("") }
    var filtro by rememberSaveable { mutableStateOf(FiltroDespensa.TODOS) }

    fun abrirFormulario(alimento: Alimento?) {
        editando = alimento
        mostrarFormulario = true
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        containerColor = FondoCrema,
        topBar = {
            RemyTopBar(
                titulo = stringResource(R.string.despensa_titulo),
                onAvatarClick = onPerfilClick
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { abrirFormulario(null) },
                containerColor = Terracota,
                contentColor = androidx.compose.ui.graphics.Color.White,
                shape = PillShape,
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(bottom = 8.dp),
                icon = {
                    Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                },
                text = {
                    Text(
                        text = stringResource(R.string.despensa_anadir),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            RemySearchField(
                value = query,
                onValueChange = { query = it },
                placeholder = stringResource(R.string.despensa_buscar),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FiltroDespensa.entries.forEach { opcion ->
                    RemyChip(
                        text = stringResource(opcion.etiqueta),
                        selected = filtro == opcion,
                        onClick = { filtro = opcion }
                    )
                }
            }

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                ContenidoConEstado(
                    estado = estado,
                    onReintentar = viewModel::reintentar,
                    mensajeCargando = stringResource(R.string.despensa_cargando)
                ) { alimentos ->
                    // TODO(v1): el modelo Alimento no guarda fecha de caducidad.
                    // La sección «Revísalos primero» se omite hasta que exista ese dato.
                    val filtrados = alimentos.filter { alimento ->
                        (query.isBlank() || alimento.nombre.contains(query, ignoreCase = true)) &&
                            filtro.contiene(alimento.nombre)
                    }
                    if (filtrados.isEmpty()) {
                        DespensaVacia(
                            hayFiltros = query.isNotBlank() || filtro != FiltroDespensa.TODOS,
                            onAnadir = { abrirFormulario(null) },
                            onMarcarBasicos = { mostrarBasicos = true },
                            onVerSugerencias = onVerSugerencias
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = 20.dp, end = 20.dp, top = 4.dp, bottom = 104.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            item(key = "contador") {
                                Text(
                                    text = stringResource(R.string.despensa_contador, filtrados.size),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = TextoSecundario,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                            }
                            items(filtrados, key = { it.id }) { alimento ->
                                FilaAlimento(
                                    alimento = alimento,
                                    onEditar = { abrirFormulario(alimento) },
                                    onBorrar = { viewModel.borrar(alimento) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarFormulario) {
        FormularioAlimento(
            alimento = editando,
            onGuardar = { nombre, cantidad, unidad ->
                viewModel.guardar(editando, nombre, cantidad, unidad)
            },
            onCerrar = { mostrarFormulario = false }
        )
    }

    if (mostrarBasicos) {
        HojaBasicos(
            ingredientes = bienvenidaViewModel.ingredientesComunes,
            onConfirmar = { seleccionados ->
                bienvenidaViewModel.insertarSeleccion(seleccionados - "__otro__") { mostrarBasicos = false }
            },
            onCerrar = { mostrarBasicos = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilaAlimento(
    alimento: Alimento,
    onEditar: () -> Unit,
    onBorrar: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { valor ->
            if (valor == SwipeToDismissBoxValue.EndToStart) {
                onBorrar()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(24.dp))
                    .background(AvisoCaducadoBg)
                    .padding(horizontal = 22.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        Icons.Outlined.Delete,
                        contentDescription = null,
                        tint = AvisoCaducadoFg,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = stringResource(R.string.despensa_eliminar),
                        style = MaterialTheme.typography.labelLarge,
                        color = AvisoCaducadoFg,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) {
        RemyCard(modifier = Modifier.fillMaxWidth(), onClick = onEditar, padding = 14.dp) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                IngredientIcon(nombre = alimento.nombre)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = alimento.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextoPrincipal,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = stringResource(
                            R.string.despensa_cantidad,
                            alimento.cantidad.formatoCantidad(),
                            alimento.unidad
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextoSecundario,
                        maxLines = 1
                    )
                }
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = stringResource(R.string.despensa_toca_editar),
                    tint = TextoSecundario.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun DespensaVacia(
    hayFiltros: Boolean,
    onAnadir: () -> Unit,
    onMarcarBasicos: () -> Unit,
    onVerSugerencias: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!hayFiltros) {
            RemyLogo(size = 96.dp, conBorde = true)
        } else {
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(CircleShape)
                    .background(Melocoton),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.Kitchen,
                    contentDescription = null,
                    tint = AzulTinta,
                    modifier = Modifier.size(38.dp)
                )
            }
        }
        Spacer(Modifier.height(18.dp))
        Text(
            text = stringResource(
                if (hayFiltros) R.string.despensa_sin_resultados
                else R.string.despensa_vacia_titulo
            ),
            style = MaterialTheme.typography.titleLarge,
            color = TextoPrincipal,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(
                if (hayFiltros) R.string.despensa_sin_resultados_texto
                else R.string.despensa_vacia_texto
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSecundario
        )
        Spacer(Modifier.height(20.dp))
        if (!hayFiltros) {
            PrimaryButton(
                text = stringResource(R.string.despensa_anadir_ingredientes),
                onClick = onAnadir,
                icon = Icons.Outlined.Add
            )
            Spacer(Modifier.height(10.dp))
            TextButton(onClick = onMarcarBasicos) {
                Text(
                    text = stringResource(R.string.despensa_basicos_golpe),
                    color = Terracota,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            TextButton(onClick = onVerSugerencias) {
                Text(
                    text = stringResource(R.string.inicio_ver_sugerencias),
                    color = Terracota,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun HojaBasicos(
    ingredientes: List<com.remy.app.ui.viewmodel.IngredienteComun>,
    onConfirmar: (Set<String>) -> Unit,
    onCerrar: () -> Unit
) {
    var seleccionados by rememberSaveable { mutableStateOf(setOf<String>()) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onCerrar,
        sheetState = sheetState,
        containerColor = FondoCrema
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = stringResource(R.string.bienvenida_titulo),
                style = MaterialTheme.typography.headlineSmall,
                color = TextoPrincipal,
                fontWeight = FontWeight.ExtraBold
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ingredientes.forEach { ing ->
                    ChipBasico(
                        texto = ing.nombre,
                        seleccionado = ing.nombre in seleccionados,
                        onClick = {
                            seleccionados = if (ing.nombre in seleccionados) seleccionados - ing.nombre else seleccionados + ing.nombre
                        }
                    )
                }
                ChipBasico(
                    texto = stringResource(R.string.bienvenida_otro),
                    seleccionado = "__otro__" in seleccionados,
                    onClick = {
                        seleccionados = if ("__otro__" in seleccionados) seleccionados - "__otro__" else seleccionados + "__otro__"
                    }
                )
            }
            val total = seleccionados.size
            Text(
                text = when (total) {
                    0 -> stringResource(R.string.bienvenida_ninguno)
                    1 -> stringResource(R.string.bienvenida_seleccionado)
                    else -> stringResource(R.string.bienvenida_seleccionados, total)
                },
                style = MaterialTheme.typography.titleSmall,
                color = if (total > 0) AzulTinta else TextoSecundario,
                fontWeight = FontWeight.Bold
            )
            PrimaryButton(
                text = stringResource(R.string.despensa_anadir_ingredientes),
                onClick = { onConfirmar(seleccionados) }
            )
        }
    }
}

@Composable
private fun ChipBasico(texto: String, seleccionado: Boolean, onClick: () -> Unit) {
    val fondo = if (seleccionado) AzulTinta else SuperficieBlanca
    val contenido = if (seleccionado) androidx.compose.ui.graphics.Color.White else TextoPrincipal
    val borde = if (seleccionado) AzulTinta else BordeTarjeta
    Box(
        modifier = Modifier
            .clip(PillShape)
            .background(fondo)
            .border(1.dp, borde, PillShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            if (seleccionado) {
                Icon(Icons.Outlined.Check, contentDescription = null, tint = contenido, modifier = Modifier.size(16.dp))
            }
            Text(texto, style = MaterialTheme.typography.labelLarge, color = contenido, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormularioAlimento(
    alimento: Alimento?,
    onGuardar: (String, String, String) -> ErroresAlimento,
    onCerrar: () -> Unit
) {
    var nombre by rememberSaveable { mutableStateOf(alimento?.nombre ?: "") }
    var cantidad by rememberSaveable { mutableStateOf(alimento?.cantidad?.formatoCantidad() ?: "") }
    var unidad by rememberSaveable { mutableStateOf(alimento?.unidad ?: "g") }
    var errores by remember { mutableStateOf(ErroresAlimento()) }
    val unidades = listOf("g", "kg", "ml", "l", "ud", "cda")
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onCerrar,
        sheetState = sheetState,
        containerColor = SuperficieBlanca,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = stringResource(
                    if (alimento == null) R.string.despensa_form_anadir
                    else R.string.despensa_form_editar
                ),
                style = MaterialTheme.typography.headlineSmall,
                color = TextoPrincipal,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.despensa_form_ayuda),
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario
            )
            CampoFormulario(
                valor = nombre,
                onValorChange = { nombre = it },
                etiqueta = stringResource(R.string.despensa_campo_nombre),
                error = errores.nombre
            )
            CampoFormulario(
                valor = cantidad,
                onValorChange = { cantidad = it },
                etiqueta = stringResource(R.string.despensa_campo_cantidad),
                error = errores.cantidad,
                tipoTeclado = KeyboardType.Decimal
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CampoFormulario(
                    valor = unidad,
                    onValorChange = { unidad = it },
                    etiqueta = stringResource(R.string.despensa_campo_unidad),
                    error = errores.unidad
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    unidades.forEach { opcion ->
                        RemyChip(
                            text = opcion,
                            selected = unidad.trim().equals(opcion, ignoreCase = true),
                            onClick = { unidad = opcion }
                        )
                    }
                }
            }
            Spacer(Modifier.height(2.dp))
            PrimaryButton(
                text = stringResource(R.string.despensa_guardar),
                onClick = {
                    val resultado = onGuardar(nombre, cantidad, unidad)
                    if (resultado.hayErrores) errores = resultado else onCerrar()
                }
            )
        }
    }
}

@Composable
private fun CampoFormulario(
    valor: String,
    onValorChange: (String) -> Unit,
    etiqueta: String,
    error: String?,
    tipoTeclado: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        label = { Text(etiqueta) },
        singleLine = true,
        isError = error != null,
        supportingText = { error?.let { Text(it) } },
        keyboardOptions = KeyboardOptions(keyboardType = tipoTeclado),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = FondoCrema,
            unfocusedContainerColor = FondoCrema,
            focusedBorderColor = Terracota,
            unfocusedBorderColor = BordeTarjeta,
            focusedLabelColor = Terracota,
            unfocusedLabelColor = TextoSecundario,
            focusedTextColor = TextoPrincipal,
            unfocusedTextColor = TextoPrincipal
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(name = "Despensa", showBackground = true, backgroundColor = 0xFFFFFBF5)
@Composable
private fun PreviewDespensa() {
    REMYTheme {
        Column(modifier = Modifier.fillMaxSize().background(FondoCrema)) {
            RemyTopBar(titulo = "Mi despensa")
            RemySearchField(
                value = "",
                onValueChange = {},
                placeholder = "Buscar en mi despensa",
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(12.dp))
            FilaAlimento(
                alimento = Alimento(
                    id = 1, nombre = "Arroz", cantidad = 1.0, unidad = "kg"
                ),
                onEditar = {},
                onBorrar = {}
            )
        }
    }
}
