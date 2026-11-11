package com.remy.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.remy.app.R
import com.remy.app.data.Receta
import com.remy.app.data.RecetaConIngredientes
import com.remy.app.data.SupabaseClientProvider
import com.remy.app.ui.components.RecipePhoto
import com.remy.app.ui.components.RemyCard
import com.remy.app.ui.components.RemyTopBar
import com.remy.app.ui.components.StatusBadge
import com.remy.app.ui.components.VarianteBadge
import com.remy.app.ui.theme.AzulTinta
import com.remy.app.ui.theme.BordeTarjeta
import com.remy.app.ui.theme.FondoCrema
import com.remy.app.ui.theme.Melocoton
import com.remy.app.ui.theme.PillShape
import com.remy.app.ui.theme.REMYTheme
import com.remy.app.ui.theme.Red600
import com.remy.app.ui.theme.Terracota
import com.remy.app.ui.theme.TextoPrincipal
import com.remy.app.ui.theme.TextoSecundario
import com.remy.app.ui.viewmodel.CifrasPerfil
import com.remy.app.ui.viewmodel.PerfilViewModel
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

@Composable
fun PerfilScreen(
    onVolver: () -> Unit,
    onRecetaClick: (Long) -> Unit,
    viewModel: PerfilViewModel = viewModel(factory = PerfilViewModel.Factory)
) {
    val favoritas by viewModel.favoritas.collectAsStateWithLifecycle()
    val cifras by viewModel.cifras.collectAsStateWithLifecycle()
    val usuario = SupabaseClientProvider.client.auth.currentUserOrNull()
    val nombre = usuario?.userMetadata?.get("full_name")?.toString()?.trim('"')
        ?: usuario?.userMetadata?.get("name")?.toString()?.trim('"')
        ?: usuario?.email?.substringBefore('@')
        ?: "Invitado"
    val correo = usuario?.email
    val scope = rememberCoroutineScope()

    ContenidoPerfil(
        nombre = nombre,
        correo = correo,
        cifras = cifras,
        favoritas = favoritas,
        onVolver = onVolver,
        onRecetaClick = onRecetaClick,
        onCerrarSesion = if (usuario != null) {
            { scope.launch { SupabaseClientProvider.client.auth.signOut() } }
        } else {
            null
        }
    )
}

@Composable
private fun ContenidoPerfil(
    nombre: String,
    correo: String?,
    cifras: CifrasPerfil,
    favoritas: List<RecetaConIngredientes>,
    onVolver: () -> Unit,
    onRecetaClick: (Long) -> Unit,
    onCerrarSesion: (() -> Unit)?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoCrema)
    ) {
        RemyTopBar(
            titulo = stringResource(R.string.perfil_titulo),
            onVolver = onVolver
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item(key = "usuario") {
                TarjetaUsuario(nombre = nombre, correo = correo)
            }

            item(key = "cifras") {
                FilaCifras(cifras = cifras)
            }

            item(key = "favoritas-titulo") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.perfil_favoritas_titulo),
                        style = MaterialTheme.typography.titleLarge,
                        color = TextoPrincipal
                    )
                    Spacer(Modifier.weight(1f))
                    StatusBadge(
                        text = favoritas.size.toString(),
                        variante = VarianteBadge.NEUTRO
                    )
                }
            }

            if (favoritas.isEmpty()) {
                item(key = "favoritas-vacio") {
                    TarjetaFavoritasVacio()
                }
            } else {
                item(key = "favoritas") {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(favoritas, key = { it.receta.id }) { item ->
                            TarjetaFavorita(
                                receta = item.receta,
                                onClick = { onRecetaClick(item.receta.id) }
                            )
                        }
                    }
                }
            }

            item(key = "ajustes") {
                TarjetaAjustes()
            }

            if (onCerrarSesion != null) {
                item(key = "cerrar-sesion") {
                    OutlinedButton(
                        onClick = onCerrarSesion,
                        shape = PillShape,
                        border = BorderStroke(1.dp, Red600),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Logout,
                            contentDescription = null,
                            tint = Red600,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = stringResource(R.string.perfil_cerrar_sesion),
                            style = MaterialTheme.typography.labelLarge,
                            color = Red600
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaUsuario(nombre: String, correo: String?) {
    RemyCard(modifier = Modifier.fillMaxWidth(), padding = 20.dp) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Melocoton),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    tint = AzulTinta,
                    modifier = Modifier.size(32.dp)
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(R.string.perfil_usuario),
                    style = MaterialTheme.typography.labelMedium,
                    color = TextoSecundario
                )
                Text(
                    text = nombre,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextoPrincipal,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = correo ?: stringResource(R.string.perfil_cuenta_demo),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario
                )
            }
        }
    }
}

@Composable
private fun FilaCifras(cifras: CifrasPerfil) {
    RemyCard(modifier = Modifier.fillMaxWidth(), padding = 20.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Cifra(valor = cifras.guardadas, etiqueta = stringResource(R.string.perfil_guardadas))
            Cifra(valor = cifras.favoritas, etiqueta = stringResource(R.string.perfil_favoritas))
            Cifra(valor = cifras.despensa, etiqueta = stringResource(R.string.perfil_en_despensa))
        }
    }
}

@Composable
private fun Cifra(valor: Int, etiqueta: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = valor.toString(),
            style = MaterialTheme.typography.headlineMedium,
            color = Terracota,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodySmall,
            color = TextoSecundario
        )
    }
}

@Composable
private fun TarjetaFavorita(receta: Receta, onClick: () -> Unit) {
    RemyCard(
        modifier = Modifier.width(168.dp),
        onClick = onClick,
        padding = 0.dp
    ) {
        RecipePhoto(
            model = receta.fotoCocinada,
            nombre = receta.nombre,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            shape = MaterialTheme.shapes.medium
        )
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = receta.nombre,
                style = MaterialTheme.typography.titleSmall,
                color = TextoPrincipal,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            val porciones = receta.porciones.coerceAtLeast(1)
            Text(
                text = stringResource(
                    R.string.recetas_kcal_proteina,
                    kotlin.math.round(receta.kcal.toFloat() / porciones).toInt(),
                    kotlin.math.round(receta.proteinas.toFloat() / porciones).toInt()
                ),
                style = MaterialTheme.typography.bodySmall,
                color = TextoSecundario
            )
        }
    }
}

@Composable
private fun TarjetaFavoritasVacio() {
    RemyCard(modifier = Modifier.fillMaxWidth(), padding = 24.dp) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Melocoton),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Favorite,
                    contentDescription = null,
                    tint = Terracota
                )
            }
            Text(
                text = stringResource(R.string.perfil_favoritas_vacio_titulo),
                style = MaterialTheme.typography.titleMedium,
                color = TextoPrincipal
            )
            Text(
                text = stringResource(R.string.perfil_favoritas_vacio_texto),
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario
            )
        }
    }
}

@Composable
private fun TarjetaAjustes() {
    // TODO: persistir en DataStore cuando existan preferencias de cuenta.
    var recordatorios by rememberSaveable { mutableStateOf(true) }

    RemyCard(modifier = Modifier.fillMaxWidth(), padding = 20.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = stringResource(R.string.perfil_ajustes),
                style = MaterialTheme.typography.titleLarge,
                color = TextoPrincipal
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.perfil_notificaciones),
                        style = MaterialTheme.typography.titleSmall,
                        color = TextoPrincipal
                    )
                    Text(
                        text = stringResource(R.string.perfil_notificaciones_texto),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextoSecundario
                    )
                }
                Switch(
                    checked = recordatorios,
                    onCheckedChange = { recordatorios = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Terracota,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = BordeTarjeta,
                        uncheckedBorderColor = BordeTarjeta
                    )
                )
            }
            HorizontalDivider(color = BordeTarjeta)
            FilaAjuste(
                titulo = stringResource(R.string.perfil_unidades),
                valor = stringResource(R.string.perfil_unidades_valor)
            )
            HorizontalDivider(color = BordeTarjeta)
            FilaAjuste(
                titulo = stringResource(R.string.perfil_acerca),
                valor = stringResource(R.string.perfil_version)
            )
        }
    }
}

@Composable
private fun FilaAjuste(titulo: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.titleSmall,
            color = TextoPrincipal,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSecundario
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 900)
@Composable
private fun PerfilScreenPreview() {
    REMYTheme {
        ContenidoPerfil(
            nombre = "María",
            correo = "maria@remy.app",
            cifras = CifrasPerfil(guardadas = 20, favoritas = 2, despensa = 14),
            favoritas = listOf(
                RecetaConIngredientes(
                    receta = Receta(
                        id = 1,
                        nombre = "Tortilla de patatas",
                        kcal = 320,
                        proteinas = 18,
                        regionOrigen = "España",
                        tipoPlato = "Principal",
                        tiempoMinutos = 25,
                        porciones = 4,
                        pasos = listOf("Pela y corta las patatas.", "Cuaja la tortilla.")
                    ),
                    ingredientes = emptyList()
                ),
                RecetaConIngredientes(
                    receta = Receta(
                        id = 2,
                        nombre = "Ensalada de garbanzos",
                        kcal = 280,
                        proteinas = 14,
                        regionOrigen = "Mediterránea",
                        tipoPlato = "Entrante",
                        tiempoMinutos = 15,
                        porciones = 2,
                        pasos = listOf("Mezcla los ingredientes.")
                    ),
                    ingredientes = emptyList()
                )
            ),
            onVolver = {},
            onRecetaClick = {},
            onCerrarSesion = {}
        )
    }
}
