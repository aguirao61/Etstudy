package com.example.studyapp.questions.presentation.quiz_play_screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Timer
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
import com.example.studyapp.core.presentation.ui.theme.*
import com.example.studyapp.core.util.NumberFormatter
import com.example.studyapp.questions.domain.models.Attempt
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizPlayScreen(
    viewModel: QuizPlayViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onNavigateToResults: (Attempt) -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is QuizPlayEffect.NavigateToResults -> {
                    onNavigateToResults(effect.attempt)
                }
            }
        }
    }

    if (state.isFinished) {
        return
    }

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryBlue)
        }
        return
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp),
                drawerContainerColor = StudyTheme.cardBg
            ) {
                QuizSummaryDrawerContent(
                    questionsCount = state.questions.size,
                    currentIndex = state.currentIndex,
                    userAnswers = state.userAnswers,
                    correctAnswers = state.questions.map { it.correctAnswerIndices },
                    immediateCorrection = state.immediateCorrection,
                    validatedIndices = state.validatedIndices,
                    onQuestionClick = {
                        viewModel.onIntent(QuizPlayIntent.OnQuestionJump(it))
                        scope.launch { drawerState.close() }
                    }
                )
            }
        },
        gesturesEnabled = true
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(text = "Test", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = StudyTheme.textMain)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = PrimaryBlue)
                        }
                    },
                    actions = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (state.isTimerEnabled) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = StudyTheme.textSub,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = NumberFormatter.formatDuration(state.timeElapsedSeconds),
                                    color = StudyTheme.textSub,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            if (state.immediateCorrection) {
                                Text(
                                    text = "${state.score} pts",
                                    color = if (state.score >= 0) StudyTheme.success else StudyTheme.error,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Resumen", tint = StudyTheme.textSub)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = StudyTheme.cardBg)
                )
            },
            bottomBar = {
                val isLast = state.currentIndex == state.questions.size - 1
                val showValidate = state.immediateCorrection && !state.isCorrected
                
                QuizBottomBar(
                    canGoBack = state.currentIndex > 0,
                    onBack = { viewModel.onIntent(QuizPlayIntent.OnBackClick) },
                    onNext = { viewModel.onIntent(QuizPlayIntent.OnNextClick) },
                    showNext = true,
                    nextButtonText = when {
                        showValidate -> "Validar"
                        isLast -> "Finalizar"
                        else -> "Siguiente"
                    }
                )
            },
            containerColor = StudyTheme.surfaceBg
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                val question = state.currentQuestion ?: return@Scaffold

                // Sub-header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = StudyTheme.topicPillBg,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = question.topicLabel.ifEmpty { "Pregunta" },
                            color = StudyTheme.topicPillText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Text(
                        text = "${state.currentIndex + 1} / ${state.questions.size}",
                        color = StudyTheme.textSub,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                
                if (question.isMultiSelect) {
                    Text(
                        text = "Selección múltiple",
                        color = PrimaryBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Question Text
                Text(
                    text = question.text,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = StudyTheme.textMain,
                    lineHeight = 26.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Options
                Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                    question.options.forEachIndexed { index, option ->
                        val isSelected = state.selectedOptionIndices.contains(index)
                        val isCorrect = if (state.isCorrected) {
                            question.correctAnswerIndices.contains(index)
                        } else null

                        QuizOptionItem(
                            text = option,
                            isSelected = isSelected,
                            isCorrect = isCorrect,
                            immediateCorrection = state.immediateCorrection,
                            enabled = !state.isCorrected,
                            onClick = { viewModel.onIntent(QuizPlayIntent.OnOptionSelect(index)) }
                        )
                    }
                }

                if (state.isCorrected) {
                    val isCorrect = state.selectedOptionIndices == question.correctAnswerIndices.toSet()
                    
                    if (isCorrect) {
                        CorrectionBanner(
                            text = "¡Correcta! (+1)",
                            icon = Icons.Default.CheckCircle,
                            color = StudyTheme.success,
                            bgColor = StudyTheme.successBg
                        )
                    } else {
                        val correctTexts = question.correctAnswerIndices.joinToString(", ") { question.options[it] }
                        CorrectionBanner(
                            text = "Incorrecta (-1)",
                            icon = Icons.Default.Cancel,
                            color = StudyTheme.error,
                            bgColor = StudyTheme.errorBg,
                            extraText = "Respuesta: $correctTexts"
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun CorrectionBanner(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    bgColor: Color,
    extraText: String? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = text,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = StudyTheme.textMain
                )
                extraText?.let {
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        color = StudyTheme.textSub
                    )
                }
            }
        }
    }
}

@Composable
fun QuizOptionItem(
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean?, 
    immediateCorrection: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = when {
            isCorrect == true && immediateCorrection -> StudyTheme.success
            isCorrect == false && isSelected && immediateCorrection -> StudyTheme.error
            isSelected && !immediateCorrection -> StudyTheme.textMain
            else -> StudyTheme.cardBorder
        }, label = "border"
    )

    val bgColor = when {
        isCorrect == true && immediateCorrection -> StudyTheme.successBg
        isCorrect == false && isSelected && immediateCorrection -> StudyTheme.errorBg
        isSelected && !immediateCorrection -> LevelColor
        else -> StudyTheme.cardBg
    }

    val textColor = if (isSelected && !immediateCorrection && isCorrect == null) {
        Color.White
    } else StudyTheme.textMain

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = enabled) { onClick() },
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val radioColor = if (isSelected) {
                if (!immediateCorrection && isCorrect == null) SelectionLevelDark
                else PrimaryBlue
            } else StudyTheme.textSub.copy(alpha = 0.3f)

            Box(
                modifier = Modifier
                    .size(20.dp)
                    .border(2.dp, radioColor, RoundedCornerShape(4.dp)), // Cuadrado para multi-select?
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(radioColor)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = textColor,
                modifier = Modifier.weight(1f)
            )

            if (isCorrect == true && immediateCorrection) {
                Icon(Icons.Default.Check, contentDescription = null, tint = StudyTheme.success, modifier = Modifier.size(20.dp))
            } else if (isCorrect == false && isSelected && immediateCorrection) {
                Icon(Icons.Default.Close, contentDescription = null, tint = StudyTheme.error, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun QuizSummaryDrawerContent(
    questionsCount: Int,
    currentIndex: Int,
    userAnswers: Map<Int, Set<Int>>,
    correctAnswers: List<List<Int>>,
    immediateCorrection: Boolean,
    validatedIndices: Set<Int>,
    onQuestionClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StudyTheme.cardBg)
            .padding(16.dp)
    ) {
        Text(
            text = "Resumen del Test",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = StudyTheme.textMain,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(questionsCount) { index ->
                val isAnswered = userAnswers.containsKey(index) && userAnswers[index]?.isNotEmpty() == true
                val isCurrent = index == currentIndex
                
                val boxColor = when {
                    immediateCorrection && isAnswered -> {
                        if (validatedIndices.contains(index)) {
                            val isCorrect = userAnswers[index] == correctAnswers[index].toSet()
                            if (isCorrect) StudyTheme.success else StudyTheme.error
                        } else {
                            StudyTheme.cardBorder
                        }
                    }
                    !immediateCorrection && isAnswered -> LevelColor
                    else -> StudyTheme.cardBorder
                }

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(boxColor)
                        .border(
                            width = if (isCurrent) 2.dp else 0.dp,
                            color = PrimaryBlue,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { onQuestionClick(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (index + 1).toString(),
                        color = if (isAnswered && (!immediateCorrection || validatedIndices.contains(index))) Color.White else StudyTheme.textMain,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun QuizBottomBar(
    canGoBack: Boolean,
    onBack: () -> Unit,
    onNext: () -> Unit,
    showNext: Boolean,
    nextButtonText: String = "Siguiente"
) {
    Surface(
        color = StudyTheme.cardBg,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable(enabled = canGoBack) { onBack() },
                color = StudyTheme.surfaceBg,
                shape = CircleShape
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Anterior",
                        tint = if (canGoBack) StudyTheme.textSub else StudyTheme.textSub.copy(alpha = 0.3f)
                    )
                }
            }

            if (showNext) {
                Button(
                    onClick = onNext,
                    modifier = Modifier
                        .height(48.dp)
                        .weight(1f)
                        .padding(start = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = nextButtonText, fontWeight = FontWeight.Bold)
                        if (nextButtonText == "Siguiente") {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}
