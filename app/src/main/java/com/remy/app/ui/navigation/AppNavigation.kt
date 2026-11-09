package com.remy.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.remy.app.ui.screens.DespensaScreen
import com.remy.app.ui.screens.RecetaDetalleScreen
import com.remy.app.ui.screens.RecetasScreen
import com.remy.app.ui.screens.SugerenciasScreen

private data class Destino(val ruta: String, val titulo: String)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val destinos = listOf(
        Destino(Routes.DESPENSA, "Despensa"),
        Destino(Routes.RECETAS, "Recetas"),
        Destino(Routes.SUGERENCIAS, "Sugerencias"),
    )
    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                destinos.forEach { destino ->
                    NavigationBarItem(
                        selected = rutaActual == destino.ruta,
                        onClick = {
                            navController.navigate(destino.ruta) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Text(destino.titulo.first().toString()) },
                        label = { Text(destino.titulo) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.DESPENSA,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.DESPENSA) { DespensaScreen() }
            composable(Routes.RECETAS) {
                RecetasScreen(onRecetaClick = { id -> navController.navigate(Routes.detalleReceta(id)) })
            }
            composable(
                route = Routes.RECETA_DETALLE,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { entrada ->
                RecetaDetalleScreen(
                    id = entrada.arguments?.getLong("id") ?: -1L,
                    onVolver = { navController.popBackStack() }
                )
            }
            composable(Routes.SUGERENCIAS) { SugerenciasScreen() }
        }
    }
}