package com.remy.app.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Kitchen
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.remy.app.ui.screens.DespensaScreen
import com.remy.app.ui.screens.InicioScreen
import com.remy.app.ui.screens.PerfilScreen
import com.remy.app.ui.screens.RecetaDetalleScreen
import com.remy.app.ui.screens.RecetasScreen
import com.remy.app.ui.screens.SugerenciasScreen
import com.remy.app.ui.theme.PillShape

private data class Destino(val ruta: String, val titulo: String, val icono: ImageVector)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val destinos = listOf(
        Destino(Routes.INICIO, "Inicio", Icons.Outlined.CalendarMonth),
        Destino(Routes.DESPENSA, "Despensa", Icons.Outlined.Kitchen),
        Destino(Routes.RECETAS, "Recetas", Icons.Outlined.MenuBook),
        Destino(Routes.SUGERENCIAS, "Sugerencias", Icons.Filled.AutoAwesome),
    )
    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route

    val irA: (String) -> Unit = { ruta ->
        navController.navigate(ruta) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RectangleShape, ambientColor = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.04f), spotColor = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.08f))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                destinos.forEach { destino ->
                    val activa = rutaActual == destino.ruta
                    val colorTexto = if (activa) {
                        androidx.compose.ui.graphics.Color.White
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(PillShape)
                            .background(
                                if (activa) MaterialTheme.colorScheme.primary
                                else androidx.compose.ui.graphics.Color.Transparent
                            )
                            .clickable(onClick = { irA(destino.ruta) })
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                destino.icono,
                                contentDescription = destino.titulo,
                                tint = colorTexto,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                destino.titulo,
                                style = MaterialTheme.typography.labelSmall,
                                color = colorTexto,
                                fontWeight = if (activa) FontWeight.Bold else FontWeight.SemiBold,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.INICIO,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.INICIO) {
                InicioScreen(
                    onRecetaClick = { id -> navController.navigate(Routes.detalleReceta(id)) },
                    onPerfilClick = { navController.navigate(Routes.PERFIL) }
                )
            }
            composable(Routes.DESPENSA) {
                DespensaScreen(onVerSugerencias = { irA(Routes.SUGERENCIAS) })
            }
            composable(Routes.RECETAS) {
                RecetasScreen(
                    onRecetaClick = { id -> navController.navigate(Routes.detalleReceta(id)) },
                    onPerfilClick = { navController.navigate(Routes.PERFIL) }
                )
            }
            composable(
                route = Routes.RECETA_DETALLE,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { entrada ->
                RecetaDetalleScreen(
                    id = entrada.arguments?.getLong("id") ?: -1L,
                    onVolver = { navController.popBackStack() },
                    onPerfilClick = { navController.navigate(Routes.PERFIL) }
                )
            }
            composable(Routes.PERFIL) {
                PerfilScreen(
                    onVolver = { navController.popBackStack() },
                    onRecetaClick = { id -> navController.navigate(Routes.detalleReceta(id)) }
                )
            }
            composable(Routes.SUGERENCIAS) {
                SugerenciasScreen(
                    onRecetaClick = { id -> navController.navigate(Routes.detalleReceta(id)) },
                    onIrADespensa = { irA(Routes.DESPENSA) },
                    onPerfilClick = { navController.navigate(Routes.PERFIL) }
                )
            }
        }
    }
}
