package com.remy.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.remy.app.data.Alimento
import com.remy.app.ui.viewmodel.DespensaViewModel
import com.remy.app.ui.viewmodel.ErroresAlimento

private fun Double.formato(): String = if (this % 1.0 == 0.0) toLong().toString() else toString()

@Composable
fun DespensaScreen(viewModel: DespensaViewModel = viewModel(factory = DespensaViewModel.Factory)) {    val alimentos by viewModel.alimentos.collectAsStateWithLifecycle()
    var mostrarFormulario by rememberSaveable { mutableStateOf(false) }
    var editando by remember { mutableStateOf<Alimento?>(null) }

    fun abrirFormulario(alimento: Alimento?) {
        editando = alimento
        mostrarFormulario = true
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { abrirFormulario(null) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) { Text("+") }
        }
    ) { padding ->
        if (alimentos.isEmpty()) {
            Column(
                modifier = Modifier.padding(padding).fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Aún no tienes alimentos", style = MaterialTheme.typography.titleMedium)
                Button(onClick = { abrirFormulario(null) }, modifier = Modifier.padding(top = 12.dp)) {
                    Text("Añadir alimento")
                }
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
                items(alimentos, key = { it.id }) { alimento ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(alimento.nombre, style = MaterialTheme.typography.titleMedium)
                                Text("${alimento.cantidad.formato()} ${alimento.unidad}")
                            }
                            TextButton(onClick = { abrirFormulario(alimento) }) { Text("Editar") }
                            TextButton(onClick = { viewModel.borrar(alimento) }) { Text("Borrar") }
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
}

@Composable
private fun FormularioAlimentoDialog(
    alimento: Alimento?,
    onGuardar: (String, String, String) -> ErroresAlimento,
    onCerrar: () -> Unit,
) {
    var nombre by rememberSaveable { mutableStateOf(alimento?.nombre ?: "") }
    var cantidad by rememberSaveable { mutableStateOf(alimento?.cantidad?.formato() ?: "") }
    var unidad by rememberSaveable { mutableStateOf(alimento?.unidad ?: "") }
    var errores by remember { mutableStateOf(ErroresAlimento()) }

    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text(if (alimento == null) "Añadir alimento" else "Editar alimento") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nombre, onValueChange = { nombre = it },
                    label = { Text("Nombre") }, singleLine = true,
                    isError = errores.nombre != null,
                    supportingText = { errores.nombre?.let { Text(it) } }
                )
                OutlinedTextField(
                    value = cantidad, onValueChange = { cantidad = it },
                    label = { Text("Cantidad") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = errores.cantidad != null,
                    supportingText = { errores.cantidad?.let { Text(it) } }
                )
                OutlinedTextField(
                    value = unidad, onValueChange = { unidad = it },
                    label = { Text("Unidad (g, ml, u...)") }, singleLine = true,
                    isError = errores.unidad != null,
                    supportingText = { errores.unidad?.let { Text(it) } }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val resultado = onGuardar(nombre, cantidad, unidad)
                if (resultado.hayErrores) errores = resultado else onCerrar()
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onCerrar) { Text("Cancelar") } }
    )
}