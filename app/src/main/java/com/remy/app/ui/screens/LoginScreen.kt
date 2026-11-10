package com.remy.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.remy.app.data.SupabaseClientProvider
import com.remy.app.ui.theme.Green100
import com.remy.app.ui.theme.Green600
import com.remy.app.ui.theme.Slate500
import com.remy.app.ui.theme.Slate900
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.launch

private const val DEMO_CORREO = "demo@remy.app"
private const val DEMO_CONTRASENA = "Demo1234!"

@Composable
fun LoginScreen(modifier: Modifier = Modifier) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val auth = remember { SupabaseClientProvider.client.auth }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Green100)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "REMY",
            style = MaterialTheme.typography.headlineLarge,
            color = Green600,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Inicia sesión para continuar",
            style = MaterialTheme.typography.bodyLarge,
            color = Slate500,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Correo electrónico") },
            leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Contraseña") },
            leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        if (mensaje != null) {
            Text(
                text = mensaje!!,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Button(
            onClick = {
                scope.launch {
                    cargando = true
                    mensaje = null
                    try {
                        auth.signInWith(Email) {
                            email = correo.trim()
                            password = contrasena
                        }
                    } catch (e: Exception) {
                        mensaje = e.message ?: "No se ha podido iniciar sesión."
                    } finally {
                        cargando = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !cargando && correo.isNotBlank() && contrasena.isNotBlank()
        ) {
            if (cargando) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
            } else {
                Text("Iniciar sesión")
            }
        }

        OutlinedButton(
            onClick = {
                scope.launch {
                    cargando = true
                    mensaje = null
                    try {
                        auth.signUpWith(Email) {
                            email = correo.trim()
                            password = contrasena
                        }
                        mensaje = "Cuenta creada. Revisa tu correo si es necesario confirmar la cuenta."
                    } catch (e: Exception) {
                        mensaje = e.message ?: "No se ha podido crear la cuenta."
                    } finally {
                        cargando = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !cargando && correo.isNotBlank() && contrasena.isNotBlank()
        ) {
            Text("Crear cuenta")
        }

        Text("o", color = Slate500, style = MaterialTheme.typography.bodyMedium)

        OutlinedButton(
            onClick = {
                scope.launch {
                    cargando = true
                    mensaje = null
                    try {
                        auth.signInWith(Google)
                    } catch (e: Exception) {
                        mensaje = e.message ?: "No se ha podido iniciar sesión con Google."
                        cargando = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !cargando
        ) {
            Text("Continuar con Google")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Acceso de prueba (sin registrarte)",
            style = MaterialTheme.typography.titleSmall,
            color = Slate900,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Correo: $DEMO_CORREO\nContraseña: $DEMO_CONTRASENA",
            style = MaterialTheme.typography.bodyMedium,
            color = Slate500,
            textAlign = TextAlign.Center
        )
        Button(
            onClick = {
                scope.launch {
                    cargando = true
                    mensaje = null
                    correo = DEMO_CORREO
                    contrasena = DEMO_CONTRASENA
                    try {
                        auth.signInWith(Email) {
                            email = DEMO_CORREO
                            password = DEMO_CONTRASENA
                        }
                    } catch (e: Exception) {
                        mensaje = e.message ?: "No se ha podido iniciar sesión con la cuenta de prueba."
                    } finally {
                        cargando = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !cargando
        ) {
            Text("Entrar con cuenta de prueba")
        }
    }
}