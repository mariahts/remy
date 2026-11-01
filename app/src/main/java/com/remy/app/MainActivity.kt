package com.remy.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.remy.app.ui.navigation.AppNavigation
import com.remy.app.ui.theme.REMYTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            REMYTheme(dynamicColor = false) {
                AppNavigation()
            }
        }
    }
}