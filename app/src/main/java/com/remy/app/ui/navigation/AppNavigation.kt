package com.remy.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.remy.app.ui.screens.CamaraScreen
import com.remy.app.ui.screens.DespensaScreen
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
        Destino(Routes.CAMARA, "Cámara"), // pestaña de prueba del prototipo de #25
    )
    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
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
                        label = { Text(destino.titulo) }
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
            composable(Routes.RECETAS) { RecetasScreen() }
            composable(Routes.SUGERENCIAS) { SugerenciasScreen() }
            composable(Routes.CAMARA) { CamaraScreen() }
        }
    }
}