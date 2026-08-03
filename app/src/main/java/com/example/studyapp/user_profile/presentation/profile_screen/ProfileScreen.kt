package com.example.studyapp.user_profile.presentation.profile_screen

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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studyapp.R
import com.example.studyapp.core.presentation.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit = {}
) {
    var showEditNameDialog by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("Usuario123") }
    var tempName by remember { mutableStateOf(userName) }

    if (showEditNameDialog) {
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = { Text(text = "Cambiar nombre") },
            text = {
                OutlinedTextField(
                    value = tempName,
                    onValueChange = { tempName = it },
                    label = { Text("Nombre de usuario") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    userName = tempName
                    showEditNameDialog = false
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) {
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // --- 1. BANNER DE LOGRO DE CUMBRE ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE047))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = Color(0xFFD97706),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "100 Test de Fluidos 1 Aprobados",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF92400E)
                    )
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
                        text = "363",
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
                        tempName = userName
                        showEditNameDialog = true 
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = userName,
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, StudyTheme.cardBorder)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(PrimaryBlueLight, StudyTheme.cardBg)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Placeholder para el avatar o ilustración grande
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(StudyTheme.cardBg)
                                .border(2.dp, PrimaryBlue, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(60.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Estudiante Leyenda",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = StudyTheme.textMain
                        )
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
                        /*
                        Text(
                            text = "Nvl 10",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = ExpColor
                        )
                        */
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Barra de EXP
                    LinearProgressIndicator(
                        progress = { 16f / 150f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape),
                        color = ExpColor,
                        trackColor = PrimaryBlueLight
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "16 / 150 EXP",
                        fontSize = 10.sp,
                        color = StudyTheme.textSub,
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
                                text = "2,185,525",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PrimaryBlue
                            )
                        }
                        Surface(
                            color = Color(0xFFFEF3C7),
                            shape = CircleShape
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = LevelColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "BONUS",
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
                    StatRow(label = "Preguntas Respondidas", value = "167,775")
                    StatRow(label = "Preguntas Correctas", value = "142,012")
                    StatRow(label = "Tests Completados", value = "3,309")
                    StatRow(label = "Asignaturas Dominadas", value = "742")
                }
            }



            // --- 6. MÚSICA / AUDIO DE FONDO ---
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                                text = "Música de Concentración",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = StudyTheme.textMain
                            )
                            Text(
                                text = "Lo-Fi Study Beats · Activo",
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
                            modifier = Modifier.clickable { /* Navegar al inventario */ }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Placeholders para los 5 objetos más importantes
                        repeat(5) { index ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(StudyTheme.surfaceBg)
                                    .border(1.dp, StudyTheme.cardBorder, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
//                                Icon(
//                                    imageVector = Icons.Default.AutoAwesome,
//                                    contentDescription = null,
//                                    tint = if (index < 2) LevelColor else StudyTheme.textSub.copy(alpha = 0.3f),
//                                    modifier = Modifier.size(24.dp)
//                                )
                                Image(
                                    painter = painterResource(id = R.drawable.trofeo_tecnologia_materiales_icon_1000),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(top = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
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

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}