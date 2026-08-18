package com.example.studyapp.user_profile.presentation.screens.settings_screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studyapp.core.presentation.ui.theme.*
import com.example.studyapp.user_profile.domain.models.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(),
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    if (state.showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { if (!state.isLoading) viewModel.onDismissDeleteConfirmation() },
            title = { Text("¿Eliminar cuenta?") },
            text = { 
                if (state.isLoading) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Borrando datos...")
                    }
                } else {
                    Text("Esta acción es permanente y borrará todo tu progreso, puntos y objetos obtenidos.")
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.onConfirmDeleteAccount(onLogoutClick) },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                    enabled = !state.isLoading
                ) {
                    Text("Eliminar para siempre")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.onDismissDeleteConfirmation() },
                    enabled = !state.isLoading
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Ajustes",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = StudyTheme.textMain
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = StudyTheme.textMain
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = StudyTheme.surfaceBg)
            )
        },
        containerColor = StudyTheme.surfaceBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Apariencia",
                style = MaterialTheme.typography.titleSmall,
                color = StudyTheme.textSub,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            ThemeOption(
                title = "Modo Claro",
                icon = Icons.Default.LightMode,
                isSelected = state.themeMode == ThemeMode.LIGHT,
                onClick = { viewModel.onThemeChange(ThemeMode.LIGHT) }
            )

            ThemeOption(
                title = "Modo Oscuro",
                icon = Icons.Default.DarkMode,
                isSelected = state.themeMode == ThemeMode.DARK,
                onClick = { viewModel.onThemeChange(ThemeMode.DARK) }
            )

            ThemeOption(
                title = "Sistema",
                icon = Icons.Default.SettingsSuggest,
                isSelected = state.themeMode == ThemeMode.SYSTEM,
                onClick = { viewModel.onThemeChange(ThemeMode.SYSTEM) }
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = StudyTheme.cardBorder.copy(alpha = 0.5f)
            )

            Text(
                text = "Cuenta",
                style = MaterialTheme.typography.titleSmall,
                color = StudyTheme.textSub,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // ID del usuario
            SettingsItem(
                title = "ID de Usuario (UUID)",
                subtitle = state.userId.toString(),
                icon = Icons.Default.Key,
                trailing = {
                    IconButton(onClick = { 
                        clipboardManager.setText(AnnotatedString(state.userId.toString()))
                        Toast.makeText(context, "ID copiado al portapapeles", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copiar", tint = StudyTheme.textSub, modifier = Modifier.size(18.dp))
                    }
                }
            )

            SettingsItem(
                title = "Cambiar Usuario",
                subtitle = "Cerrar sesión actual",
                icon = Icons.AutoMirrored.Filled.Logout,
                onClick = if (state.isLoading) null else onLogoutClick,
                contentColor = PrimaryBlue
            )

            SettingsItem(
                title = "Eliminar Cuenta",
                subtitle = "Borrar permanentemente todos tus datos",
                icon = Icons.Default.DeleteForever,
                onClick = if (state.isLoading) null else { viewModel::onDeleteAccountClick },
                contentColor = ErrorRed
            )
        }
    }
}

@Composable
fun ThemeOption(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else StudyTheme.textSub,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else StudyTheme.textMain,
                modifier = Modifier.weight(1f)
            )

            RadioButton(
                selected = isSelected,
                onClick = onClick
            )
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    contentColor: Color = StudyTheme.textMain
) {
    Surface(
        onClick = { onClick?.invoke() },
        enabled = onClick != null,
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (onClick != null) contentColor else StudyTheme.textSub,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = if (onClick != null) contentColor else StudyTheme.textMain
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = StudyTheme.textSub
                )
            }
            
            if (trailing != null) {
                trailing()
            }
        }
    }
}
