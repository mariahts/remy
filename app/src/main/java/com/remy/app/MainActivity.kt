package com.remy.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.remy.app.data.DatosDemo
import com.remy.app.data.PreferenciasRepository
import com.remy.app.data.PrecargaRecetas
import com.remy.app.data.RemyDatabase
import com.remy.app.data.SupabaseClientProvider
import com.remy.app.ui.navigation.AppNavigation
import com.remy.app.ui.navigation.Routes
import com.remy.app.ui.screens.BienvenidaScreen
import com.remy.app.ui.screens.LoginScreen
import com.remy.app.ui.theme.REMYTheme
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.handleDeeplinks
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SupabaseClientProvider.client.handleDeeplinks(intent)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )
        lifecycleScope.launch {
            PrecargaRecetas.siHaceFalta(RemyDatabase.obtener(applicationContext), applicationContext)
        }
        setContent {
            REMYTheme {
                val sessionStatus by SupabaseClientProvider.client.auth.sessionStatus.collectAsState()
                when (sessionStatus) {
                    is SessionStatus.Authenticated -> {
                        val db = remember { RemyDatabase.obtener(applicationContext) }
                        val prefs = remember { PreferenciasRepository.delContexto(applicationContext) }
                        var destino by remember { mutableStateOf<DestinoInicial?>(null) }

                        LaunchedEffect(sessionStatus) {
                            val usuario = SupabaseClientProvider.client.auth.currentUserOrNull()
                            val usuarioId = usuario?.id ?: usuario?.email ?: "anonimo"
                            val correo = usuario?.email
                            val ultimo = prefs.ultimoUsuarioId.first()
                            if (ultimo != null && ultimo != usuarioId) {
                                DatosDemo.resetearDatosUsuario(db, prefs)
                            }
                            if (ultimo != usuarioId) {
                                prefs.guardarUltimoUsuario(usuarioId)
                            }
                            if (DatosDemo.esCuentaDemo(correo)) {
                                DatosDemo.sembrarDespensaSiHaceFalta(db)
                            }
                            val completada = prefs.bienvenidaCompletada.first()
                            val despensaVacia = db.alimentoDao().observarTodos().first().isEmpty()
                            destino = if (!completada && despensaVacia) DestinoInicial.Bienvenida else DestinoInicial.App
                        }

                        when (val actual = destino) {
                            null -> Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) { CircularProgressIndicator() }
                            DestinoInicial.Bienvenida -> {
                                var irASugerencias by remember { mutableStateOf(false) }
                                if (irASugerencias) {
                                    AppNavigation(rutaInicial = Routes.SUGERENCIAS)
                                } else {
                                    var mostrarApp by remember { mutableStateOf(false) }
                                    if (mostrarApp) {
                                        AppNavigation()
                                    } else {
                                        BienvenidaScreen(
                                            onIrASugerencias = { irASugerencias = true },
                                            onIrAInicio = { mostrarApp = true }
                                        )
                                    }
                                }
                            }
                            DestinoInicial.App -> AppNavigation()
                        }
                    }
                    SessionStatus.Initializing -> Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                    else -> LoginScreen()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        SupabaseClientProvider.client.handleDeeplinks(intent)
    }
}

private enum class DestinoInicial { Bienvenida, App }
