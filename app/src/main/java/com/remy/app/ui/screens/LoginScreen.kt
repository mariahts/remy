package com.remy.app.ui.screens

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
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.remy.app.R
import com.remy.app.data.SupabaseClientProvider
import com.remy.app.ui.components.OutlineButton
import com.remy.app.ui.components.PrimaryButton
import com.remy.app.ui.components.RemyLogo
import com.remy.app.ui.theme.AvisoCaducadoBg
import com.remy.app.ui.theme.AvisoCaducadoFg
import com.remy.app.ui.theme.AvisoFaltaBg
import com.remy.app.ui.theme.AvisoFaltaFg
import com.remy.app.ui.theme.BordeTarjeta
import com.remy.app.ui.theme.FondoCrema
import com.remy.app.ui.theme.REMYTheme
import com.remy.app.ui.theme.SuperficieBlanca
import com.remy.app.ui.theme.Terracota
import com.remy.app.ui.theme.TextoPrincipal
import com.remy.app.ui.theme.TextoSecundario
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

    ContenidoLogin(
        correo = correo,
        onCorreoChange = { correo = it },
        contrasena = contrasena,
        onContrasenaChange = { contrasena = it },
        mostrarContrasena = mostrarContrasena,
        onMostrarContrasena = { mostrarContrasena = !mostrarContrasena },
        cargando = cargando,
        mensaje = mensaje,
        esError = esError,
        onEntrar = { ejecutarAccion() },
        onGoogle = { ejecutarAccion(google = true) },
        onDemo = { ejecutarAccion(demo = true) },
        onCrearCuenta = { ejecutarAccion(esRegistro = true) },
        modifier = modifier
    )
}

@Composable
private fun ContenidoLogin(
    correo: String,
    onCorreoChange: (String) -> Unit,
    contrasena: String,
    onContrasenaChange: (String) -> Unit,
    mostrarContrasena: Boolean,
    onMostrarContrasena: () -> Unit,
    cargando: Boolean,
    mensaje: String?,
    esError: Boolean,
    onEntrar: () -> Unit,
    onGoogle: () -> Unit,
    onDemo: () -> Unit,
    onCrearCuenta: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formularioValido = correo.isNotBlank() && contrasena.isNotBlank()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FondoCrema)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(28.dp))
        RemyLogo(size = 112.dp, conBorde = true)
        Spacer(Modifier.height(18.dp))
        Text(
            text = "REMY",
            style = MaterialTheme.typography.displaySmall,
            color = Terracota,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 6.sp
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.login_frase),
            style = MaterialTheme.typography.bodyLarge,
            color = TextoSecundario,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        Spacer(Modifier.height(28.dp))

        if (mensaje != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (esError) AvisoCaducadoBg else AvisoFaltaBg,
                        RoundedCornerShape(16.dp)
                    )
                    .border(
                        1.dp,
                        if (esError) AvisoCaducadoFg.copy(alpha = 0.25f) else BordeTarjeta,
                        RoundedCornerShape(16.dp)
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = mensaje,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (esError) AvisoCaducadoFg else AvisoFaltaFg
                )
            }
            Spacer(Modifier.height(14.dp))
        }

        OutlinedTextField(
            value = correo,
            onValueChange = onCorreoChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.login_correo)) },
            leadingIcon = {
                Icon(Icons.Outlined.Email, contentDescription = null, tint = TextoSecundario)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SuperficieBlanca,
                unfocusedContainerColor = SuperficieBlanca,
                focusedBorderColor = Terracota,
                unfocusedBorderColor = BordeTarjeta,
                focusedLabelColor = Terracota,
                unfocusedLabelColor = TextoSecundario,
                cursorColor = Terracota,
                focusedTextColor = TextoPrincipal,
                unfocusedTextColor = TextoPrincipal
            )
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = onContrasenaChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.login_contrasena)) },
            leadingIcon = {
                Icon(Icons.Outlined.Lock, contentDescription = null, tint = TextoSecundario)
            },
            trailingIcon = {
                IconButton(onClick = onMostrarContrasena) {
                    Icon(
                        if (mostrarContrasena) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = stringResource(
                            if (mostrarContrasena) R.string.login_ocultar_contrasena
                            else R.string.login_mostrar_contrasena
                        ),
                        tint = TextoSecundario
                    )
                }
            },
            singleLine = true,
            visualTransformation = if (mostrarContrasena) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SuperficieBlanca,
                unfocusedContainerColor = SuperficieBlanca,
                focusedBorderColor = Terracota,
                unfocusedBorderColor = BordeTarjeta,
                focusedLabelColor = Terracota,
                unfocusedLabelColor = TextoSecundario,
                cursorColor = Terracota,
                focusedTextColor = TextoPrincipal,
                unfocusedTextColor = TextoPrincipal
            )
        )

        Spacer(Modifier.height(20.dp))

        if (cargando) {
            PrimaryButton(
                text = stringResource(R.string.login_entrando),
                onClick = {},
                enabled = false
            )
        } else {
            PrimaryButton(
                text = stringResource(R.string.login_entrar),
                onClick = onEntrar,
                enabled = formularioValido,
                icon = Icons.AutoMirrored.Filled.ArrowForward
            )
        }

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = BordeTarjeta)
            Text(
                text = stringResource(R.string.login_o_continua),
                style = MaterialTheme.typography.labelMedium,
                color = TextoSecundario
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = BordeTarjeta)
        }

        Spacer(Modifier.height(16.dp))

        OutlineButton(
            text = stringResource(R.string.login_google),
            onClick = onGoogle,
            enabled = !cargando
        )

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clickable(enabled = !cargando, onClick = onDemo),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Outlined.DinnerDining,
                contentDescription = null,
                tint = Terracota,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.login_demo),
                style = MaterialTheme.typography.labelLarge,
                color = Terracota,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.login_sin_cuenta),
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.login_crear_cuenta),
                style = MaterialTheme.typography.bodyMedium,
                color = Terracota,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable(enabled = !cargando && formularioValido, onClick = onCrearCuenta)
                    .padding(horizontal = 4.dp, vertical = 6.dp)
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.login_demo_ayuda, DEMO_CORREO),
            style = MaterialTheme.typography.bodySmall,
            color = TextoSecundario.copy(alpha = 0.85f),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(12.dp))
    }
}

@Preview(name = "Login", showBackground = true, backgroundColor = 0xFFFFFBF5)
@Composable
private fun PreviewLogin() {
    REMYTheme {
        ContenidoLogin(
            correo = "demo@remy.app",
            onCorreoChange = {},
            contrasena = "Demo1234!",
            onContrasenaChange = {},
            mostrarContrasena = false,
            onMostrarContrasena = {},
            cargando = false,
            mensaje = null,
            esError = true,
            onEntrar = {},
            onGoogle = {},
            onDemo = {},
            onCrearCuenta = {}
        )
    }
}
