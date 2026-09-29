package com.example.studyapp.user_profile.presentation.screens.profile_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.studyapp.R
import com.example.studyapp.core.presentation.ui.theme.*
import com.example.studyapp.core.util.NumberFormatter
import com.example.studyapp.user_profile.presentation.components.StudyBanner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onBackClick: () -> Unit = {},
    onNavigateToInventory: () -> Unit = {},
    onNavigateToBanners: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val user = state.user

    if (state.showEditNameDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onIntent(ProfileIntent.OnDismissEditDialog) },
            title = { Text(text = "Cambiar nombre") },
            text = {
                OutlinedTextField(
                    value = state.tempName,
                    onValueChange = { viewModel.onIntent(ProfileIntent.OnTempNameChange(it)) },
                    label = { Text("Nombre de usuario") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.onIntent(ProfileIntent.OnSaveNameClick) }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onIntent(ProfileIntent.OnDismissEditDialog) }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (state.showEditMusicDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onIntent(ProfileIntent.OnDismissMusicDialog) },
            title = { Text(text = "Cambiar música de perfil") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = state.tempSongName,
                        onValueChange = { viewModel.onIntent(ProfileIntent.OnTempSongChange(it)) },
                        label = { Text("Nombre de la canción") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = state.tempArtistName,
                        onValueChange = { viewModel.onIntent(ProfileIntent.OnTempArtistChange(it)) },
                        label = { Text("Artista") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.onIntent(ProfileIntent.OnSaveMusicClick) }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onIntent(ProfileIntent.OnDismissMusicDialog) }) {
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
                        text = "Mi Perfil",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = StudyTheme.textMain
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Ajustes",
                            tint = StudyTheme.textMain
                        )
                    }
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
        if (user == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // --- 1. BANNER DE LOGRO DE CUMBRE ---
                if (user.equippedBannerContent != null) {
                    StudyBanner(
                        text = user.equippedBannerContent,
                        bannerColour = Color(user.equippedBannerColour ?: 0L),
                        bannerTextColour = Color(user.equippedBannerTextColour ?: 0L),
                        bannerIcon = user.equippedBannerIcon ?: "",
                        onClick = onNavigateToBanners
                    )
                } else {
                    // Empty state for banner
                    OutlinedCard(
                        onClick = onNavigateToBanners,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StudyTheme.cardBorder)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Sin banner equipado",
                                color = StudyTheme.textSub,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // --- 2. CABECERA: RANK Y NOMBRE ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "NIVEL ",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = LevelColor
                        )
                        Text(
                            text = user.level.toString(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = LevelColor
                        )
                    }

                    // Nombre con botón de editar
                    Surface(
                        color = StudyTheme.cardBg,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StudyTheme.cardBorder),
                        modifier = Modifier.clickable { 
                            viewModel.onIntent(ProfileIntent.OnEditNameClick)
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = user.username,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = StudyTheme.textMain
                            )
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar",
                                tint = StudyTheme.textSub,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // --- 3. TARJETA DE AVATAR DESTACADO / PERSONAJE ---
                Card(
                    onClick = onNavigateToInventory,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = StudyTheme.cardBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StudyTheme.cardBorder)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Avatar dinámico grande sin fondo circular ni textos
                        Box(
                            modifier = Modifier.size(140.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (user.equippedIconUrl != null) {
                                val context = androidx.compose.ui.platform.LocalContext.current
                                val resId = remember(user.equippedIconUrl) {
                                    val id = context.resources.getIdentifier(user.equippedIconUrl, "drawable", context.packageName)
                                    if (id != 0) id else R.drawable.img
                                }
                                Image(
                                    painter = painterResource(id = resId),
                                    contentDescription = "Avatar",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(100.dp)
                                )
                            }
                        }
                    }
                }

                // --- 4. PANEL DE EXPERIENCIA DEL JUGADOR ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = StudyTheme.cardBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StudyTheme.cardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = ExpColor,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Experiencia del Jugador",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = StudyTheme.textMain
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Barra de EXP
                        LinearProgressIndicator(
                            progress = { user.experience.toFloat() / user.maxExperience },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = ExpColor,
                            trackColor = PrimaryBlueLight
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${NumberFormatter.formatWithCommas(user.experience)} / ${NumberFormatter.formatWithCommas(user.maxExperience)} EXP",
                            fontSize = 10.sp,
                            color = ExpColor,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }

                // --- 5. PANEL DE ESTADÍSTICAS (ESTILO POWER LEVEL) ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = StudyTheme.cardBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StudyTheme.cardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Puntos Totales / Nivel de Poder
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Puntos de Estudio",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StudyTheme.textSub
                                )
                                Text(
                                    text = NumberFormatter.formatWithCommas(user.studyPoints),
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PrimaryBlue
                                )
                            }
                            val bonusBg = if (LocalDarkTheme.current) Color(0xFF423D33) else Color(0xFFFEF3C7)
                            Surface(
                                color = bonusBg,
                                shape = CircleShape
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Whatshot,
                                        contentDescription = null,
                                        tint = LevelColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${user.maxStreak} RACHA",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = LevelColor
                                    )
                                }
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = StudyTheme.cardBorder
                        )

                        // Métricas detalladas
                        StatRow(label = "Preguntas Respondidas", value = user.questionsAnswered.toString())
                        StatRow(label = "Preguntas Correctas", value = user.correctAnswers.toString())
                        StatRow(label = "Tests Completados", value = user.completedTests.toString())
                        StatRow(label = "Asignaturas Dominadas", value = user.completedCourses.toString())
                    }
                }

                // --- 6. MÚSICA / AUDIO DE FONDO ---
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.onIntent(ProfileIntent.OnEditMusicClick) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = StudyTheme.cardBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StudyTheme.cardBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                color = PrimaryBlue,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier
                                        .padding(6.dp)
                                        .size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (user.userSongName.isNotBlank()) user.userSongName else "Sin música",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = StudyTheme.textMain
                                )
                                Text(
                                    text = user.userSongArtist.ifBlank { "Seleccionar pista" },
                                    fontSize = 11.sp,
                                    color = StudyTheme.textSub
                                )
                            }
                        }
                    }
                }

                // --- 7. INVENTARIO (TOP 5 OBJETOS) ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = StudyTheme.cardBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StudyTheme.cardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Inventory2,
                                    contentDescription = null,
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Inventario",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = StudyTheme.textMain
                                )
                            }
                            Text(
                                text = "Ver todo",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue,
                                modifier = Modifier.clickable { onNavigateToInventory() }
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Mostrar los 5 iconos más importantes (o placeholders si hay menos)
                            repeat(5) { index ->
                                val icon = state.topIcons.getOrNull(index)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(StudyTheme.surfaceBg)
                                        .border(1.dp, StudyTheme.cardBorder, RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (icon != null) {
                                        val context = androidx.compose.ui.platform.LocalContext.current
                                        val resId = remember(icon.imageUrl) {
                                            val id = context.resources.getIdentifier(icon.imageUrl, "drawable", context.packageName)
                                            if (id != 0) id else R.drawable.img
                                        }
                                        if (icon.isUnlocked) {
                                            Image(
                                                painter = painterResource(id = resId),
                                                contentDescription = icon.name,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = "Bloqueado",
                                                tint = StudyTheme.textSub.copy(alpha = 0.4f),
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = StudyTheme.textSub.copy(alpha = 0.2f),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

// -------------------------------------------------------------
// COMPONENTE AUXILIAR
// -------------------------------------------------------------

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = StudyTheme.textSub,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 13.sp,
            color = StudyTheme.textMain,
            fontWeight = FontWeight.Bold
        )
    }
}
