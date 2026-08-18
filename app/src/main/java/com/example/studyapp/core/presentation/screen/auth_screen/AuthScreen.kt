package com.example.studyapp.core.presentation.screen.auth_screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.studyapp.core.presentation.ui.theme.*

@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onNavigateToHome: (userId: Int) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is AuthEffect.NavigateToHome -> onNavigateToHome(effect.userId)
                is AuthEffect.ShowToast -> { /* Handle toast */ }
            }
        }
    }

    if (state.showCreationSuccessDialog) {
        SuccessDialog(
            uuid = state.createdUserUuid ?: "",
            onDismiss = { viewModel.onIntent(AuthIntent.DismissSuccessDialog) }
        )
    }

    Scaffold(
        containerColor = StudyTheme.surfaceBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (state.isLoginMode) "Bienvenido de nuevo" else "Crear Perfil",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = StudyTheme.textMain,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (state.isLoginMode) 
                    "Introduce tu UUID para recuperar tu progreso" 
                else 
                    "Elige un alias para tu nueva aventura de estudio",
                fontSize = 14.sp,
                color = StudyTheme.textSub,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            if (state.isLoginMode) {
                OutlinedTextField(
                    value = state.uuidInput,
                    onValueChange = { viewModel.onIntent(AuthIntent.OnUuidChange(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Tu UUID de acceso") },
                    leadingIcon = { Icon(Icons.Default.Key, contentDescription = null) },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = StudyTheme.cardBorder
                    )
                )
            } else {
                OutlinedTextField(
                    value = state.aliasInput,
                    onValueChange = { viewModel.onIntent(AuthIntent.OnAliasChange(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Alias / Nombre de usuario") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = StudyTheme.cardBorder
                    )
                )
            }

            if (state.error != null) {
                Text(
                    text = state.error!!,
                    color = ErrorRed,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.onIntent(AuthIntent.OnSubmit) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = if (state.isLoginMode) "Entrar" else "Crear Cuenta",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { viewModel.onIntent(AuthIntent.ToggleMode) }) {
                Text(
                    text = if (state.isLoginMode) 
                        "¿No tienes cuenta? Crea una nueva" 
                    else 
                        "¿Ya tienes cuenta? Inicia sesión",
                    color = PrimaryBlue
                )
            }
        }
    }
}

@Composable
fun SuccessDialog(
    uuid: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Entendido, ir al inicio")
            }
        },
        title = {
            Text(text = "¡Perfil creado con éxito!", fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                Text("Este es tu UUID único. Guárdalo bien, lo necesitarás para iniciar sesión en otros dispositivos:")
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    color = PrimaryBlueLight,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = uuid,
                        modifier = Modifier.padding(16.dp),
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = PrimaryBlueDark,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Recuerda que siempre puedes consultar tu UUID en Ajustes.",
                    fontSize = 12.sp,
                    color = StudyTheme.textSub
                )
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}
