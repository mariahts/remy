package com.remy.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Kitchen
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.remy.app.R
import com.remy.app.ui.components.DestinoBarra
import com.remy.app.ui.components.RemyBottomBar
import com.remy.app.ui.screens.DespensaScreen
import com.remy.app.ui.screens.InicioScreen
import com.remy.app.ui.screens.PerfilScreen
import com.remy.app.ui.screens.RecetaDetalleScreen
import com.remy.app.ui.screens.RecetasScreen
import com.remy.app.ui.screens.SugerenciasScreen
import com.remy.app.ui.theme.FondoCrema

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val destinos = listOf(
        DestinoBarra(Routes.INICIO, stringResource(R.string.nav_inicio), Icons.Outlined.Home),
        DestinoBarra(Routes.DESPENSA, stringResource(R.string.nav_despensa), Icons.Outlined.Kitchen),
        DestinoBarra(Routes.RECETAS, stringResource(R.string.nav_recetas), Icons.Outlined.MenuBook),
    )
    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route

    // La barra inferior solo aparece en los tres destinos principales.
    val mostrarBarra = destinos.any { it.ruta == rutaActual }

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
        containerColor = FondoCrema,
        bottomBar = {
            if (mostrarBarra) {
                RemyBottomBar(
                    destinos = destinos,
                    rutaActual = rutaActual,
                    onNavegar = irA,
                    modifier = Modifier.background(FondoCrema)
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.INICIO,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding()
                )
        ) {
            composable(Routes.INICIO) {
                InicioScreen(
                    onRecetaClick = { id -> navController.navigate(Routes.detalleReceta(id)) },
                    onPerfilClick = { navController.navigate(Routes.PERFIL) },
                    onIrADespensa = { irA(Routes.DESPENSA) },
                    onVerSugerencias = { navController.navigate(Routes.SUGERENCIAS) }
                )
            }
            composable(Routes.DESPENSA) {
                DespensaScreen(
                    onVerSugerencias = { navController.navigate(Routes.SUGERENCIAS) },
                    onPerfilClick = { navController.navigate(Routes.PERFIL) }
                )
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
                    onPerfilClick = { navController.navigate(Routes.PERFIL) },
                    onVolver = { navController.popBackStack() }
                )
            }
        }
    }
}
