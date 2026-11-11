package com.remy.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.remy.app.R
import com.remy.app.ui.components.PrimaryButton
import com.remy.app.ui.components.RemyLogo
import com.remy.app.ui.theme.AzulTinta
import com.remy.app.ui.theme.BordeTarjeta
import com.remy.app.ui.theme.FondoCrema
import com.remy.app.ui.theme.PillShape
import com.remy.app.ui.theme.REMYTheme
import com.remy.app.ui.theme.SuperficieBlanca
import com.remy.app.ui.theme.Terracota
import com.remy.app.ui.theme.TextoPrincipal
import com.remy.app.ui.theme.TextoSecundario
import com.remy.app.ui.viewmodel.BienvenidaViewModel
import com.remy.app.ui.viewmodel.IngredienteComun

@Composable
fun BienvenidaScreen(
    onIrASugerencias: () -> Unit,
    onIrAInicio: () -> Unit,
    viewModel: BienvenidaViewModel = viewModel(factory = BienvenidaViewModel.Factory)
) {
    val ingredientes = viewModel.ingredientesComunes
    var seleccionados by rememberSaveable { mutableStateOf(setOf<String>()) }

    BienvenidaContenido(
        ingredientes = ingredientes,
        seleccionados = seleccionados,
        onToggle = { nombre ->
            seleccionados = if (nombre in seleccionados) seleccionados - nombre else seleccionados + nombre
        },
        onVerSugerencias = {
            val aInsertar = seleccionados - OTRO
            viewModel.completarConSeleccion(aInsertar, onIrASugerencias)
        },
        onMasTarde = { viewModel.omitir(onIrAInicio) }
    )
}

private const val OTRO = "__otro__"

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun BienvenidaContenido(
    ingredientes: List<IngredienteComun>,
    seleccionados: Set<String>,
    onToggle: (String) -> Unit,
    onVerSugerencias: () -> Unit,
    onMasTarde: () -> Unit
) {
    val total = seleccionados.size
    val textoContador = when (total) {
        0 -> stringResource(R.string.bienvenida_ninguno)
        1 -> stringResource(R.string.bienvenida_seleccionado)
        else -> stringResource(R.string.bienvenida_seleccionados, total)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoCrema)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(top = 28.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RemyLogo(size = 96.dp, conBorde = true)
            Spacer(Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.bienvenida_titulo),
                style = MaterialTheme.typography.headlineMedium,
                color = TextoPrincipal,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.bienvenida_texto),
                style = MaterialTheme.typography.bodyLarge,
                color = TextoSecundario,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(22.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ingredientes.forEach { ing ->
                    val seleccionado = ing.nombre in seleccionados
                    ChipSeleccion(
                        texto = ing.nombre,
                        seleccionado = seleccionado,
                        onClick = { onToggle(ing.nombre) }
                    )
                }
                val otroSeleccionado = OTRO in seleccionados
                ChipSeleccion(
                    texto = stringResource(R.string.bienvenida_otro),
                    seleccionado = otroSeleccionado,
                    onClick = { onToggle(OTRO) }
                )
            }

            Spacer(Modifier.height(18.dp))
            Text(
                text = textoContador,
                style = MaterialTheme.typography.titleSmall,
                color = if (total > 0) AzulTinta else TextoSecundario,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PrimaryButton(
                text = stringResource(R.string.bienvenida_ver_sugerencias),
                onClick = onVerSugerencias
            )
            TextButton(onClick = onMasTarde) {
                Text(
                    text = stringResource(R.string.bienvenida_mas_tarde),
                    color = TextoSecundario,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun ChipSeleccion(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    val fondo = if (seleccionado) AzulTinta else SuperficieBlanca
    val contenido = if (seleccionado) Color.White else TextoPrincipal
    val borde = if (seleccionado) AzulTinta else BordeTarjeta

    Box(
        modifier = Modifier
            .clip(PillShape)
            .background(fondo)
            .border(1.dp, borde, PillShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.layout.Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (seleccionado) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = null,
                    tint = contenido,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = texto,
                style = MaterialTheme.typography.labelLarge,
                color = contenido,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFBF5)
@Composable
private fun BienvenidaPreview() {
    REMYTheme {
        BienvenidaContenido(
            ingredientes = listOf(
                IngredienteComun("Aceite de oliva", 500.0, "ml"),
                IngredienteComun("Arroz", 500.0, "g"),
                IngredienteComun("Harina", 500.0, "g"),
                IngredienteComun("Huevo", 200.0, "g"),
                IngredienteComun("Leche", 1000.0, "ml"),
                IngredienteComun("Cebolla", 200.0, "g")
            ),
            seleccionados = setOf("Arroz", "Huevo"),
            onToggle = {},
            onVerSugerencias = {},
            onMasTarde = {}
        )
    }
}
