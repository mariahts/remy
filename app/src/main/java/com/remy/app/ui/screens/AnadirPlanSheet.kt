package com.remy.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.remy.app.R
import com.remy.app.data.Receta
import com.remy.app.data.RecetaConIngredientes
import com.remy.app.data.TipoComida
import com.remy.app.domain.model.DisponibilidadReceta
import com.remy.app.domain.model.IngredienteFaltante
import com.remy.app.domain.model.MotivoFaltante
import com.remy.app.ui.components.OutlineButton
import com.remy.app.ui.components.RecipePhoto
import com.remy.app.ui.components.RemyChip
import com.remy.app.ui.components.RemySearchField
import com.remy.app.ui.components.StatusBadge
import com.remy.app.ui.components.VarianteBadge
import com.remy.app.ui.components.formatoCantidad
import com.remy.app.ui.theme.FondoCrema
import com.remy.app.ui.theme.REMYTheme
import com.remy.app.ui.theme.SuperficieBlanca
import com.remy.app.ui.theme.TextoPrincipal
import com.remy.app.ui.theme.TextoSecundario
import java.io.File

/** Sheet para elegir una receta y añadirla al plan de un momento del día. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnadirPlanSheet(
    tipoInicial: TipoComida,
    recetas: List<Pair<RecetaConIngredientes, DisponibilidadReceta>>,
    onDismiss: () -> Unit,
    onAnadir: (TipoComida, Long) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var tipoSeleccionado by rememberSaveable(tipoInicial) { mutableStateOf(tipoInicial) }
    var busqueda by rememberSaveable { mutableStateOf("") }
    var pendiente by remember { mutableStateOf<RecetaConIngredientes?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = FondoCrema
    ) {
        ContenidoAnadirPlan(
            tipoSeleccionado = tipoSeleccionado,
            onTipoChange = { tipoSeleccionado = it },
            busqueda = busqueda,
            onBusquedaChange = { busqueda = it },
            recetas = recetas,
            onAnadir = { recetaId ->
                val item = recetas.firstOrNull { it.first.receta.id == recetaId }
                if (item?.second is DisponibilidadReceta.Faltan) {
                    pendiente = item.first
                } else {
                    onAnadir(tipoSeleccionado, recetaId)
                }
            }
        )
    }

    pendiente?.let { receta ->
        val disponibilidad = recetas
            .firstOrNull { it.first.receta.id == receta.receta.id }
            ?.second
        val faltantes = (disponibilidad as? DisponibilidadReceta.Faltan)?.faltantes.orEmpty()
        AvisoFaltantesDialog(
            receta = receta,
            faltantes = faltantes,
            onConfirmar = {
                pendiente = null
                onAnadir(tipoSeleccionado, receta.receta.id)
            },
            onDescartar = { pendiente = null }
        )
    }
}

/** Avisa de lo que falta antes de planificar una receta que no está completa. */
@Composable
private fun AvisoFaltantesDialog(
    receta: RecetaConIngredientes,
    faltantes: List<IngredienteFaltante>,
    onConfirmar: () -> Unit,
    onDescartar: () -> Unit
) {
    val detalle = faltantes.map { faltante ->
        when (faltante.motivo) {
            MotivoFaltante.AUSENTE -> stringResource(
                R.string.plan_faltante_ausente,
                faltante.nombre
            )
            MotivoFaltante.CANTIDAD_INSUFICIENTE -> stringResource(
                R.string.plan_faltante_insuficiente,
                faltante.nombre,
                faltante.necesario.formatoCantidad(),
                faltante.unidad,
                (faltante.disponible ?: 0.0).formatoCantidad()
            )
        }
    }.joinToString("\n")

    AlertDialog(
        onDismissRequest = onDescartar,
        title = { Text(stringResource(R.string.plan_faltantes_titulo)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.plan_faltantes_texto, receta.receta.nombre))
                Text(detalle)
                Text(stringResource(R.string.plan_faltantes_confirmar))
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirmar) {
                Text(stringResource(R.string.plan_faltantes_anadir))
            }
        },
        dismissButton = {
            TextButton(onClick = onDescartar) {
                Text(stringResource(R.string.comun_cancelar))
            }
        }
    )
}

@Composable
internal fun ContenidoAnadirPlan(
    tipoSeleccionado: TipoComida,
    onTipoChange: (TipoComida) -> Unit,
    busqueda: String,
    onBusquedaChange: (String) -> Unit,
    recetas: List<Pair<RecetaConIngredientes, DisponibilidadReceta>>,
    onAnadir: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val consulta = busqueda.trim().lowercase()
    val filtradas = if (consulta.isEmpty()) recetas else recetas.filter {
        it.first.receta.nombre.lowercase().contains(consulta)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = stringResource(R.string.plan_anadir_a, nombreTipoArticulo(tipoSeleccionado)),
            style = MaterialTheme.typography.headlineSmall,
            color = TextoPrincipal,
            fontWeight = FontWeight.ExtraBold
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ChipTipo(TipoComida.DESAYUNO, tipoSeleccionado, onTipoChange)
            ChipTipo(TipoComida.COMIDA, tipoSeleccionado, onTipoChange)
            ChipTipo(TipoComida.CENA, tipoSeleccionado, onTipoChange)
        }

        RemySearchField(
            value = busqueda,
            onValueChange = onBusquedaChange,
            placeholder = stringResource(R.string.plan_buscar_receta)
        )

        Text(
            text = stringResource(R.string.plan_primero_despensa),
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSecundario
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filtradas, key = { it.first.receta.id }) { (receta, disponibilidad) ->
                FilaRecetaPlan(
                    receta = receta,
                    disponibilidad = disponibilidad,
                    onAnadir = { onAnadir(receta.receta.id) }
                )
            }
        }
    }
}

@Composable
private fun ChipTipo(
    tipo: TipoComida,
    seleccionado: TipoComida,
    onTipoChange: (TipoComida) -> Unit
) {
    val etiqueta = when (tipo) {
        TipoComida.DESAYUNO -> stringResource(R.string.plan_chip_desayuno)
        TipoComida.COMIDA -> stringResource(R.string.plan_chip_comida)
        TipoComida.CENA -> stringResource(R.string.plan_chip_cena)
    }
    RemyChip(
        text = etiqueta,
        selected = tipo == seleccionado,
        onClick = { onTipoChange(tipo) }
    )
}

@Composable
private fun nombreTipoArticulo(tipo: TipoComida): String = when (tipo) {
    TipoComida.DESAYUNO -> stringResource(R.string.plan_desayuno)
    TipoComida.COMIDA -> stringResource(R.string.plan_comida)
    TipoComida.CENA -> stringResource(R.string.plan_cena)
}

@Composable
private fun FilaRecetaPlan(
    receta: RecetaConIngredientes,
    disponibilidad: DisponibilidadReceta,
    onAnadir: () -> Unit
) {
    val datos = receta.receta
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SuperficieBlanca)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        RecipePhoto(
            model = datos.fotoCocinada?.let { File(it) },
            nombre = datos.nombre,
            modifier = Modifier.size(52.dp),
            shape = RoundedCornerShape(14.dp)
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text = datos.nombre,
                style = MaterialTheme.typography.titleSmall,
                color = TextoPrincipal,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (datos.tiempoMinutos > 0) {
                    Text(
                        text = stringResource(R.string.comun_minutos, datos.tiempoMinutos),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextoSecundario
                    )
                    Text("·", style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
                }
                BadgeDisponibilidad(disponibilidad)
            }
        }
        OutlineButton(
            text = stringResource(R.string.plan_anadir),
            onClick = onAnadir,
            modifier = Modifier.widthIn(min = 88.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            fillWidth = false
        )
    }
}

@Composable
private fun BadgeDisponibilidad(disponibilidad: DisponibilidadReceta) {
    when (disponibilidad) {
        is DisponibilidadReceta.Lista -> StatusBadge(
            text = stringResource(R.string.plan_badge_lista),
            variante = VarianteBadge.LISTA
        )
        is DisponibilidadReceta.Faltan -> {
            val n = disponibilidad.cantidad
            StatusBadge(
                text = if (n == 1) stringResource(R.string.plan_badge_falta)
                else stringResource(R.string.plan_badge_faltan, n),
                variante = VarianteBadge.FALTAN
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFBF5, heightDp = 700)
@Composable
private fun ContenidoAnadirPlanPreview() {
    fun faltantes(vararg nombres: String) = nombres.mapIndexed { indice, nombre ->
        IngredienteFaltante(
            alimentoId = indice.toLong() + 1,
            nombre = nombre,
            necesario = 200.0,
            unidad = "g",
            disponible = null,
            motivo = MotivoFaltante.AUSENTE
        )
    }
    val demo = listOf(
        RecetaConIngredientes(
            Receta(id = 1, nombre = "Tortilla de patatas", tiempoMinutos = 25, kcal = 380, porciones = 4),
            emptyList()
        ) to DisponibilidadReceta.Lista,
        RecetaConIngredientes(
            Receta(id = 2, nombre = "Pasta con tomate", tiempoMinutos = 20, kcal = 450, porciones = 4),
            emptyList()
        ) to DisponibilidadReceta.Faltan(faltantes("Tomate")),
        RecetaConIngredientes(
            Receta(id = 3, nombre = "Ensalada César", tiempoMinutos = 15, kcal = 320, porciones = 2),
            emptyList()
        ) to DisponibilidadReceta.Faltan(faltantes("Lechuga", "Pollo"))
    )
    REMYTheme {
        ContenidoAnadirPlan(
            tipoSeleccionado = TipoComida.CENA,
            onTipoChange = {},
            busqueda = "",
            onBusquedaChange = {},
            recetas = demo,
            onAnadir = {}
        )
    }
}
