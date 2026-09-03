package com.example.studyapp.questions.presentation.quiz_results_screen

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studyapp.core.presentation.components.UserProfileCard
import com.example.studyapp.core.presentation.ui.theme.*
import com.example.studyapp.core.util.NumberFormatter
import com.example.studyapp.questions.domain.models.Attempt
import com.example.studyapp.questions.domain.models.AttemptQuestion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizResultsScreen(
    viewModel: QuizResultsViewModel = viewModel(),
    onBackToConfigClick: () -> Unit = {},
    onNavigateToHome: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val user = state.user
    val attempt = state.attempt

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resultados", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackToConfigClick) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = StudyTheme.surfaceBg)
            )
        },
        containerColor = StudyTheme.surfaceBg
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (user == null || attempt == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Cargando resultados...", color = StudyTheme.textSub)
                        if (user == null) Text("Buscando usuario...", fontSize = 10.sp)
                        if (attempt == null) Text("Esperando datos del test...", fontSize = 10.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    // 1. Perfil y Recompensas
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            UserProfileCard(
                                name = user.username,
                                expCurrent = state.animatedExp,
                                expMax = user.maxExperience,
                                level = user.level,
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                RewardBadge(
                                    label = "EXP",
                                    value = "+${NumberFormatter.formatWithCommas(attempt.xpGained)}",
                                    color = ExpColor,
                                    modifier = Modifier.weight(1f)
                                )
                                RewardBadge(
                                    label = "Estudio",
                                    value = "+${NumberFormatter.formatWithCommas(attempt.studyPointsGained)}",
                                    color = PrimaryBlue,
                                    modifier = Modifier.weight(1f)
                                )
                                if (attempt.timeElapsedSeconds > 0) {
                                    RewardBadge(
                                        label = "Tiempo",
                                        value = NumberFormatter.formatDuration(attempt.timeElapsedSeconds),
                                        color = GrayDark,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    // 2. Resultado Principal
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = StudyTheme.textSub,
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                text = "Resultado",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = StudyTheme.textMain
                            )
                            Text(
                            text = attempt.score.toString(),
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Black,
                            color = if (attempt.score >= 0) StudyTheme.success else StudyTheme.error
                        )
                            Text(
                                text = "de ${attempt.totalQuestions} preguntas",
                                fontSize = 14.sp,
                                color = StudyTheme.textSub
                            )
                        }
                    }

                    // 3. Tarjetas de Resumen
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SummaryCard(
                                label = "Correctas",
                                count = attempt.correctCount,
                                color = StudyTheme.success,
                                modifier = Modifier.weight(1f)
                            )
                            SummaryCard(
                                label = "Incorrectas",
                                count = attempt.incorrectCount,
                                color = StudyTheme.error,
                                modifier = Modifier.weight(1f)
                            )
                            SummaryCard(
                                label = "En blanco",
                                count = attempt.blankCount,
                                color = StudyTheme.textSub,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // 4. Detalle
                    item {
                        Text(
                            text = "Detalle",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = StudyTheme.textMain,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    itemsIndexed(attempt.questions) { index, questionResult ->
                        QuestionDetailItem(questionResult, index + 1)
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onBackToConfigClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            Text("REPETIR TEST", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = onNavigateToHome,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.5.dp, PrimaryBlue),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue)
                        ) {
                            Text("VOLVER AL INICIO", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Level Up Popup Overlay
            AnimatedVisibility(
                visible = state.showLevelUp,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                LevelUpPopup(level = user?.level ?: 0)
            }

            // Milestone Popup Overlay
            AnimatedVisibility(
                visible = state.showMilestone,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                MilestonePopup()
            }
        }
    }
}

@Composable
fun LevelUpPopup(level: Int) {
    val bonusBg = if (isSystemInDarkTheme()) Color(0xFF423D33) else Color(0xFFFEF3C7)
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        color = bonusBg,
        border = BorderStroke(4.dp, LevelColor),
        shadowElevation = 12.dp
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = LevelColor,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "¡SUBIDA DE NIVEL!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = LevelColor
            )
            Text(
                text = "Ahora eres nivel $level",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF92400E)
            )
        }
    }
}

@Composable
fun MilestonePopup() {
    val bonusBg = if (isSystemInDarkTheme()) Color(0xFF423D33) else Color(0xFFFEF3C7)
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        color = bonusBg,
        border = BorderStroke(4.dp, PrimaryBlue),
        shadowElevation = 12.dp
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "¡NUEVO HITO!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = PrimaryBlue
            )
        }
    }
}

@Composable
fun RewardBadge(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = value, fontWeight = FontWeight.Black, color = color, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = label, fontWeight = FontWeight.Bold, color = color, fontSize = 11.sp)
        }
    }
}

@Composable
fun SummaryCard(label: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = StudyTheme.cardBg),
        border = BorderStroke(1.dp, StudyTheme.cardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = count.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = StudyTheme.textSub
            )
        }
    }
}

@Composable
fun QuestionDetailItem(result: AttemptQuestion, index: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = StudyTheme.cardBg),
        border = BorderStroke(1.dp, StudyTheme.cardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = when {
                        result.isCorrect -> Icons.Default.CheckCircle
                        result.userAnswers.isEmpty() -> Icons.Default.RemoveCircleOutline
                        else -> Icons.Default.Cancel
                    },
                    contentDescription = null,
                    tint = when {
                        result.isCorrect -> StudyTheme.success
                        result.userAnswers.isEmpty() -> StudyTheme.textSub
                        else -> StudyTheme.error
                    },
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Pregunta $index",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = StudyTheme.textMain
                    )
                    Text(
                        text = result.questionText,
                        fontSize = 13.sp,
                        color = StudyTheme.textSub
                    )
                }
                Text(
                    text = if (result.points >= 0) "+${result.points}" else result.points.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (result.points > 0) StudyTheme.success else if (result.points < 0) StudyTheme.error else StudyTheme.textSub
                )
            }
            
            if (result.userAnswers.isNotEmpty() || !result.isCorrect) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = StudyTheme.cardBorder)
                
                Text(
                    text = "Tu respuesta: ${result.userAnswers.joinToString { result.options.getOrNull(it) ?: "" }}",
                    fontSize = 12.sp,
                    color = if (result.isCorrect) StudyTheme.success else StudyTheme.error
                )
                if (!result.isCorrect) {
                    Text(
                        text = "Correcta: ${result.correctAnswers.joinToString { result.options.getOrNull(it) ?: "" }}",
                        fontSize = 12.sp,
                        color = StudyTheme.success,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
