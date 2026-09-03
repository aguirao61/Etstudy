package com.example.studyapp.questions.presentation.quiz_config_screen

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studyapp.core.presentation.ui.theme.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studyapp.questions.domain.SubjectFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizConfigScreen(
    flowType: String,
    viewModel: QuizConfigViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onStartTest: (subjectId: Int, topicIndex: Int, questionsCount: Int, isRandom: Boolean, isTimerEnabled: Boolean, immediateCorrection: Boolean, flowType: String) -> Unit = { _, _, _, _, _, _, _ -> }
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    val currentOnBackClick by rememberUpdatedState(onBackClick)
    val currentOnStartTest by rememberUpdatedState(onStartTest)

    LaunchedEffect(flowType) {
        viewModel.onIntent(QuizConfigIntent.Initialize(flowType))
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                QuizConfigEffect.NavigateBack -> currentOnBackClick()
                is QuizConfigEffect.StartQuiz -> {
                    currentOnStartTest(
                        effect.subjectId,
                        effect.moduleId,
                        effect.questionCount,
                        effect.isRandom,
                        effect.isTimerEnabled,
                        effect.immediateCorrection,
                        effect.flowType
                    )
                }
            }
        }
    }

    QuizConfigScreenContent(
        state = state,
        onIntent = viewModel::onIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizConfigScreenContent(
    state: QuizConfigState,
    onIntent: (QuizConfigIntent) -> Unit
) {
    val flowLabel = if (state.flowType == SubjectFlow.ERROR_TEST) "Test de fallos" else "Test normal"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Configuración",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = StudyTheme.textMain
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onIntent(QuizConfigIntent.OnBackClick) }) {
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
            // --- ENCABEZADO DE ASIGNATURA ---
            Column {
                Surface(
                    color = if (state.flowType == SubjectFlow.ERROR_TEST) Color(0xFFFEE2E2) else PrimaryBlueLight,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    Text(
                        text = flowLabel,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (state.flowType == SubjectFlow.ERROR_TEST) Color(0xFFEF4444) else PrimaryBlue,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Text(
                    text = state.subjectName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = StudyTheme.textMain
                )
                Text(
                    text = "${state.totalQuestionsAvailable} preguntas disponibles",
                    fontSize = 13.sp,
                    color = StudyTheme.textSub
                )

                if (state.showProgress) {
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    val progress = (state.passedTestsCount.toFloat() / 250f).coerceAtMost(1f)
                    
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Tests aprobados",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = StudyTheme.textMain
                            )
                            Text(
                                text = "${state.passedTestsCount} / 250",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = SuccessGreen
                            )
                        }
                        
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = SuccessGreen,
                            trackColor = LightSuccessGreen
                        )
                    }
                }
            }

            // --- 1. SELECCIÓN DE TEMA ---
            ConfigSectionCard(
                title = "Tema",
                icon = Icons.Default.MenuBook
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.topics.forEachIndexed { index, (topicName, count) ->
                        val isSelected = state.selectedTopicIndex == index
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) PrimaryBlueLight else StudyTheme.surfaceBg)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) PrimaryBlue else StudyTheme.cardBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onIntent(QuizConfigIntent.OnTopicSelect(index)) }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = topicName,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) PrimaryBlue else StudyTheme.textMain,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Surface(
                                color = if (isSelected) PrimaryBlue else Color(0xFFCBD5E1),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = count.toString(),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            // --- 2. NÚMERO DE PREGUNTAS ---
            ConfigSectionCard(
                title = "Preguntas",
                icon = null
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = state.questionCount.toString(),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryBlue
                        )
                        Text(
                            text = "de ${state.selectedTopicQuestionsCount}",
                            fontSize = 13.sp,
                            color = StudyTheme.textSub,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Slider(
                        value = state.questionCount.toFloat(),
                        onValueChange = { onIntent(QuizConfigIntent.OnQuestionCountChange(it.toInt())) },
                        valueRange = 5f..state.selectedTopicQuestionsCount.toFloat().coerceAtLeast(5f),
                        colors = SliderDefaults.colors(
                            thumbColor = PrimaryBlue,
                            activeTrackColor = PrimaryBlue,
                            inactiveTrackColor = StudyTheme.cardBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(10, 25, 50, state.selectedTopicQuestionsCount).forEach { value ->
                            if (value <= state.selectedTopicQuestionsCount && value > 0) {
                                val isSelected = state.questionCount == value
                                val label = if (value == state.selectedTopicQuestionsCount) "Todas" else value.toString()
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(if (isSelected) PrimaryBlue else StudyTheme.surfaceBg)
                                        .border(1.dp, if (isSelected) PrimaryBlue else StudyTheme.cardBorder, RoundedCornerShape(20.dp))
                                        .clickable { onIntent(QuizConfigIntent.OnQuestionCountChange(value)) }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else StudyTheme.textSub
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // --- 3. ORDEN ---
            ConfigSectionCard(
                title = "Orden",
                icon = Icons.Default.Shuffle
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(StudyTheme.surfaceBg, RoundedCornerShape(12.dp))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (!state.isRandomOrder) PrimaryBlue else Color.Transparent)
                            .clickable { onIntent(QuizConfigIntent.OnRandomOrderToggle(false)) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Ordenado",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (!state.isRandomOrder) Color.White else StudyTheme.textSub
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (state.isRandomOrder) PrimaryBlue else Color.Transparent)
                            .clickable { onIntent(QuizConfigIntent.OnRandomOrderToggle(true)) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Aleatorio",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (state.isRandomOrder) Color.White else StudyTheme.textSub
                        )
                    }
                }
            }

            // --- 4. TEMPORIZADOR ---
            ConfigSectionCard(
                title = "Temporizador",
                icon = Icons.Default.Timer
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Activar temporizador",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StudyTheme.textMain
                    )
                    Switch(
                        checked = state.isTimerEnabled,
                        onCheckedChange = { onIntent(QuizConfigIntent.OnTimerToggle(it)) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PrimaryBlue
                        )
                    )
                }
            }

            // --- 5. MODO DE CORRECCIÓN ---
            ConfigSectionCard(
                title = "Modo de corrección",
                icon = Icons.Default.CheckCircle
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CorrectionOptionItem(
                        title = "Corrección inmediata",
                        subtitle = "Se corrige al responder cada pregunta",
                        isSelected = state.immediateCorrection,
                        onClick = { onIntent(QuizConfigIntent.OnCorrectionModeToggle(true)) }
                    )
                    CorrectionOptionItem(
                        title = "Revisar al final",
                        subtitle = "Navega libremente y envía al terminar",
                        isSelected = !state.immediateCorrection,
                        onClick = { onIntent(QuizConfigIntent.OnCorrectionModeToggle(false)) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { onIntent(QuizConfigIntent.OnStartTestClick) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    disabledContainerColor = StudyTheme.cardBorder
                ),
                enabled = state.selectedTopicQuestionsCount > 0 && state.questionCount > 0
            )
 {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Iniciar test",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// -------------------------------------------------------------
// COMPONENTES AUXILIARES
// -------------------------------------------------------------

@Composable
private fun ConfigSectionCard(
    title: String,
    icon: ImageVector?,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = StudyTheme.cardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, StudyTheme.cardBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                icon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = StudyTheme.textSub,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = StudyTheme.textSub
                )
            }
            content()
        }
    }
}

@Composable
private fun CorrectionOptionItem(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) PrimaryBlueLight else StudyTheme.surfaceBg)
            .border(
                width = 1.dp,
                color = if (isSelected) PrimaryBlue else StudyTheme.cardBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Outlined.Circle,
            contentDescription = null,
            tint = if (isSelected) PrimaryBlue else StudyTheme.textSub,
            modifier = Modifier.size(22.dp)
        )
        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) PrimaryBlue else StudyTheme.textMain
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = StudyTheme.textSub
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuizConfigScreenPreview() {
    QuizConfigScreenContent(
        state = QuizConfigState(
            subjectName = "Preview Subject",
            topics = listOf("Tema 1" to 100, "Tema 2" to 50)
        ),
        onIntent = {}
    )
}
