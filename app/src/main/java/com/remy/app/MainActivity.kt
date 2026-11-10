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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.remy.app.data.PrecargaRecetas
import com.remy.app.data.RemyDatabase
import com.remy.app.data.SupabaseClientProvider
import com.remy.app.ui.navigation.AppNavigation
import com.remy.app.ui.screens.LoginScreen
import com.remy.app.ui.theme.REMYTheme
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.handleDeeplinks
import io.github.jan.supabase.auth.status.SessionStatus
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
                    is SessionStatus.Authenticated -> AppNavigation()
                    SessionStatus.Initializing -> androidx.compose.foundation.layout.Box(
                        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        androidx.compose.material3.CircularProgressIndicator()
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
