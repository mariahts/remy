package com.remy.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.remy.app.data.SupabaseClientProvider
import com.remy.app.ui.components.FotoReceta
import com.remy.app.ui.components.RemyCta
import com.remy.app.ui.components.RemyHeaderSombra
import com.remy.app.ui.components.SectionCard
import com.remy.app.ui.theme.Green100
import com.remy.app.ui.theme.Green200
import com.remy.app.ui.theme.Green600
import com.remy.app.ui.theme.Green700
import com.remy.app.ui.theme.Red100
import com.remy.app.ui.theme.Red600
import com.remy.app.ui.theme.RemyShapes
import com.remy.app.ui.theme.Slate500
import com.remy.app.ui.theme.Slate600
import com.remy.app.ui.theme.Slate900
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
    val usuario = SupabaseClientProvider.client.auth.currentUserOrNull()
    val nombre = usuario?.userMetadata?.get("full_name")?.toString()?.trim('"')
        ?: usuario?.userMetadata?.get("name")?.toString()?.trim('"')
        ?: usuario?.email?.substringBefore('@')
        ?: "Usuario"
    val correo = usuario?.email ?: "Sin correo"
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().background(Green100)) {
        RemyHeaderSombra(
            titulo = "Perfil",
            bg = Green100,
            logoSize = 40.dp,
            trailing = {
                IconButton(onClick = onVolver) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Green600)
                }
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(key = "info") {
                SectionCard(modifier = Modifier.fillMaxWidth(), shape = RemyShapes.extraLarge, padding = PaddingValues(20.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(Green200)
                                    .border(1.dp, Green600.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.Person, contentDescription = null, tint = Green700, modifier = Modifier.size(32.dp))
                            }
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Información del usuario", style = MaterialTheme.typography.labelMedium, color = Slate500)
                                Text(nombre, style = MaterialTheme.typography.headlineSmall, color = Slate900, fontWeight = FontWeight.Bold)
                                Text(correo, style = MaterialTheme.typography.bodyMedium, color = Slate600)
                            }
                        }
                        if (usuario != null) {
                            Row(
                                modifier = Modifier
                                    .clip(RemyShapes.medium)
                                    .background(Red100.copy(alpha = 0.5f))
                                    .border(1.dp, Red100, RemyShapes.medium)
                                    .clickable { scope.launch { SupabaseClientProvider.client.auth.signOut() } }
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Outlined.Logout, contentDescription = null, tint = Red600, modifier = Modifier.size(18.dp))
                                Text("Cerrar sesión", color = Red600, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            item(key = "fav-header") {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Outlined.Favorite, contentDescription = null, tint = Green600, modifier = Modifier.size(20.dp))
                    Text("Recetas favoritas", style = MaterialTheme.typography.titleMedium, color = Slate900, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Green200)
                            .padding(horizontal = 10.dp, vertical = 2.dp)
                    ) {
                        Text("${favoritas.size}", style = MaterialTheme.typography.labelMedium, color = Green700, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (favoritas.isEmpty()) {
                item(key = "fav-empty") {
                    SectionCard(modifier = Modifier.fillMaxWidth(), shape = RemyShapes.large, padding = PaddingValues(24.dp)) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier.size(56.dp).clip(CircleShape).background(Green200),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.Favorite, contentDescription = null, tint = Green600)
                            }
                            Text(
                                "Aún no tienes recetas favoritas guardadas.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Slate600
                            )
                            Text(
                                "Marca una receta como favorita desde su detalle o desde el listado.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate500
                            )
                        }
                    }
                }
            } else {
                items(favoritas, key = { it.receta.id }) { item ->
                    val receta = item.receta
                    SectionCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RemyShapes.extraLarge,
                        border = null,
                        glow = false,
                        padding = PaddingValues(0.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth().height(140.dp)) {
                            FotoReceta(receta.nombre, Modifier.fillMaxSize())
                        }
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(receta.nombre, style = MaterialTheme.typography.titleMedium, color = Slate900, fontWeight = FontWeight.SemiBold)
                            RemyCta(
                                text = "Ver receta",
                                onClick = { onRecetaClick(receta.id) },
                                modifier = Modifier.fillMaxWidth(),
                                height = 44.dp,
                                iconAfter = Icons.AutoMirrored.Filled.ArrowForward,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}