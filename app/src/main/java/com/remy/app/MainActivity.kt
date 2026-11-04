package com.remy.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.remy.app.data.PrecargaRecetas
import com.remy.app.data.RemyDatabase
import com.remy.app.ui.navigation.AppNavigation
import com.remy.app.ui.theme.REMYTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        lifecycleScope.launch {
            PrecargaRecetas.siHaceFalta(RemyDatabase.obtener(applicationContext), applicationContext)
        }
        setContent {
            REMYTheme(dynamicColor = false) {
                AppNavigation()
            }
        }
    }
}
