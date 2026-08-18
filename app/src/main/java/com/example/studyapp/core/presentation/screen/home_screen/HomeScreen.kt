package com.example.studyapp.core.presentation.screen.home_screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studyapp.core.presentation.components.UserProfileCard
import com.example.studyapp.core.presentation.ui.theme.*
import com.example.studyapp.questions.domain.SubjectFlow

@Composable
fun StudyHomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onNavigateToStudy: (SubjectFlow) -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToCommunity: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Observar efectos
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is HomeEffect.NavigateToStudy -> {
                    onNavigateToStudy(effect.flow)
                }
                HomeEffect.NavigateToProfile -> {
                    onNavigateToProfile()
                }
                HomeEffect.NavigateToCommunity -> {
                    onNavigateToCommunity()
                }
                HomeEffect.NavigateToSettings -> {
                    onNavigateToSettings()
                }
            }
        }
    }

    StudyHomeScreenContent(
        state = state,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun StudyHomeScreenContent(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit
) {
    if (state.isStartPopupVisible) {
        StartTestDialog(
            onDismiss = { onIntent(HomeIntent.DismissStartPopup) },
            onOptionClick = { onIntent(HomeIntent.OnTestOptionClick(it)) })
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                onNavClick = { item -> onIntent(HomeIntent.OnNavClick(item)) }
            )
        },
        containerColor = StudyTheme.surfaceBg
    ) { innerPadding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. COMPONENTE DE PERFIL SUPERIOR
                UserProfileCard(
                    name = state.userName,
                    expCurrent = state.expCurrent,
                    expMax = state.expMax,
                    level = state.level,
                    iconUrl = state.equippedIconUrl,
                    modifier = Modifier.padding(top = 16.dp),
                    onClick = { onIntent(HomeIntent.OnProfileClick) }
                )

                // Espacio flexible
                Spacer(modifier = Modifier.weight(1f))

                // CONTENIDO INFERIOR
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    // Icono Guía
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        FloatingGuideButton(onClick = { onIntent(HomeIntent.OnGuideClick) })
                    }

                    // Botón central "INICIAR"
                    CustomStartButton(
                        text = "INICIAR",
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                PrimaryBlue,
                                PrimaryBlueDark
                            )
                        ),
                        onClick = { onIntent(HomeIntent.OnStartClick) }
                    )

                    // Banner de Desafíos
                    ChallengeBanner()
                }
            }
        }
    }
}

// -------------------------------------------------------------
// COMPONENTES DE LA PANTALLA
// -------------------------------------------------------------

@Composable
fun StartTestDialog(onDismiss: () -> Unit, onOptionClick: (String) -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = StudyTheme.cardBg,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Selecciona una opción",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = StudyTheme.textMain,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                CustomStartButton(
                    text = "TESTS",
                    icon = Icons.Default.Assignment,
                    brush = Brush.verticalGradient(colors = listOf(MutedGreen, MutedGreenDark)), // Verde suave
                    onClick = { onOptionClick("Tests") }
                )

                CustomStartButton(
                    text = "TESTS DE FALLOS",
                    icon = Icons.Default.Warning,
                    brush = Brush.verticalGradient(colors = listOf(MutedRed, MutedRedDark)), // Rojo suave
                    onClick = { onOptionClick("Tests de fallos") }
                )

                CustomStartButton(
                    text = "PRÓXIMAMENTE...",
                    brush = Brush.verticalGradient(colors = listOf(MutedGray, MutedGrayDark)), // Gris suave
                    onClick = { onOptionClick("Próximamente") }
                )
                
                TextButton(onClick = onDismiss, modifier = Modifier.padding(top = 8.dp)) {
                    Text("CANCELAR", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CustomStartButton(
    text: String,
    brush: Brush,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .width(220.dp)
            .height(60.dp)
            .border(2.dp, Color.White, RoundedCornerShape(30.dp)),
        shape = RoundedCornerShape(30.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = brush),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Text(
                    text = text,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun ChallengeBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2FE))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "DESAFÍOS ESPECIALES",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF0369A1)
                )
                Text(
                    text = "Completa las tareas de hoy",
                    fontSize = 12.sp,
                    color = StudyTheme.textSub
                )
            }
            Icon(
                imageVector = Icons.Default.School,
                contentDescription = null,
                tint = Color(0xFF0284C7),
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun FloatingGuideButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = CircleShape,
        modifier = Modifier.size(56.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Book, contentDescription = "Guía", modifier = Modifier.size(20.dp))
            Text("Guía", fontSize = 9.sp)
        }
    }
}

@Composable
fun BottomNavigationBar(onNavClick: (String) -> Unit) {
    NavigationBar(
        containerColor = StudyTheme.cardBg,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            Triple("Inicio", Icons.Default.Home, true),
            Triple("Comunidad", Icons.Default.Group, false),
            Triple("Cursos", Icons.Default.School, false),
            Triple("Ajustes", Icons.Default.Settings, false)
        )

        items.forEach { (label, icon, isSelected) ->
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavClick(label) },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryBlueDark, // Azul vibrante para el seleccionado
                    selectedTextColor = PrimaryBlueDark,
                    unselectedIconColor = StudyTheme.textSub,         // Gris azulado para los otros
                    unselectedTextColor = StudyTheme.textSub,
                    indicatorColor = Color(0xFFDBEAFE)    // Fondo suave para el icono seleccionado
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StudyHomeScreenPreview() {
    StudyHomeScreenContent(
        state = HomeState(
            userName = "Preview User",
            expCurrent = 50,
            expMax = 100,
            level = 10
        ),
        onIntent = {}
    )
}
