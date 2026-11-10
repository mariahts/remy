package com.remy.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.DinnerDining
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.remy.app.R
import com.remy.app.data.SupabaseClientProvider
import com.remy.app.ui.theme.Green050
import com.remy.app.ui.theme.Green100
import com.remy.app.ui.theme.Green200
import com.remy.app.ui.theme.Green600
import com.remy.app.ui.theme.Green700
import com.remy.app.ui.theme.RemyShapes
import com.remy.app.ui.theme.Slate500
import com.remy.app.ui.theme.Slate600
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
    var mostrarContrasena by remember { mutableStateOf(false) }
    var cargando by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf<String?>(null) }
    var esError by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val auth = remember { SupabaseClientProvider.client.auth }

    fun ejecutarAccion(esRegistro: Boolean = false, demo: Boolean = false, google: Boolean = false) {
        scope.launch {
            cargando = true
            mensaje = null
            esError = true
            try {
                when {
                    google -> auth.signInWith(Google)
                    demo -> {
                        correo = DEMO_CORREO
                        contrasena = DEMO_CONTRASENA
                        auth.signInWith(Email) {
                            email = DEMO_CORREO
                            password = DEMO_CONTRASENA
                        }
                    }
                    esRegistro -> {
                        auth.signUpWith(Email) {
                            email = correo.trim()
                            password = contrasena
                        }
                        mensaje = "Cuenta creada. Revisa tu correo si es necesario confirmar la cuenta."
                        esError = false
                    }
                    else -> auth.signInWith(Email) {
                        email = correo.trim()
                        password = contrasena
                    }
                }
            } catch (e: Exception) {
                mensaje = e.message ?: "No se ha podido completar el acceso."
                esError = true
                if (google) cargando = false
            } finally {
                if (!google) cargando = false
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Green050)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .size(104.dp)
                .clip(CircleShape)
                .background(Green100)
                .border(1.5.dp, Green200, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.remy_logo),
                contentDescription = "Logotipo de REMY",
                modifier = Modifier.size(84.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "REMY",
            style = MaterialTheme.typography.headlineLarge,
            color = Slate900,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Cocina delicioso con lo que tienes en casa",
            style = MaterialTheme.typography.bodyLarge,
            color = Slate600,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RemyShapes.extraLarge)
                .background(Color.White)
                .border(1.dp, Green200, RemyShapes.extraLarge)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (mensaje != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RemyShapes.medium)
                        .background(if (esError) Color(0xFFFEF2F2) else Green100)
                        .border(1.dp, if (esError) Color(0xFFFECACA) else Green200, RemyShapes.medium)
                        .padding(12.dp)
                ) {
                    Text(
                        text = mensaje!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (esError) Color(0xFF991B1B) else Green700
                    )
                }
            }

            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Correo electrónico") },
                leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = Slate500) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RemyShapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Green600,
                    unfocusedBorderColor = Green200
                )
            )

            OutlinedTextField(
                value = contrasena,
                onValueChange = { contrasena = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = Slate500) },
                trailingIcon = {
                    IconButton(onClick = { mostrarContrasena = !mostrarContrasena }) {
                        Icon(
                            if (mostrarContrasena) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = if (mostrarContrasena) "Ocultar contraseña" else "Mostrar contraseña",
                            tint = Slate500
                        )
                    }
                },
                singleLine = true,
                visualTransformation = if (mostrarContrasena) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RemyShapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Green600,
                    unfocusedBorderColor = Green200
                )
            )

            Button(
                onClick = { ejecutarAccion() },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = !cargando && correo.isNotBlank() && contrasena.isNotBlank(),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Green600,
                    contentColor = Color.White,
                    disabledContainerColor = Green200
                )
            ) {
                if (cargando) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Entrar a mi cocina", fontWeight = FontWeight.SemiBold)
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Green200)
                Text("o continúa con", style = MaterialTheme.typography.labelMedium, color = Slate500)
                HorizontalDivider(modifier = Modifier.weight(1f), color = Green200)
            }

            OutlinedButton(
                onClick = { ejecutarAccion(google = true) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = !cargando,
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Green200),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White, contentColor = Slate900)
            ) {
                Text("Acceder con Google", fontWeight = FontWeight.SemiBold)
            }

            OutlinedButton(
                onClick = { ejecutarAccion(demo = true) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = !cargando,
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Green200),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White, contentColor = Slate900)
            ) {
                Icon(Icons.Outlined.DinnerDining, contentDescription = null, tint = Green600, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Entrar con cuenta de prueba", fontWeight = FontWeight.SemiBold)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("¿No tienes cuenta?", style = MaterialTheme.typography.bodyMedium, color = Slate600)
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Crear cuenta",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Green600,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(enabled = !cargando && correo.isNotBlank() && contrasena.isNotBlank()) { ejecutarAccion(esRegistro = true) }
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RemyShapes.large)
                .background(Color.White)
                .border(1.dp, Green200, RemyShapes.large)
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(Green100),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Inventory2, contentDescription = null, tint = Green700, modifier = Modifier.size(22.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Despensa inteligente", style = MaterialTheme.typography.titleSmall, color = Slate900, fontWeight = FontWeight.SemiBold)
                Text(
                    "Aprovecha ingredientes al máximo y reduce el desperdicio en cada receta.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate600,
                    lineHeight = 16.sp
                )
                Text(
                    "Demo: $DEMO_CORREO",
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate500,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}