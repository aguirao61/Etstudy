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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studyapp.core.presentation.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizPlayScreen(
    viewModel: QuizPlayViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onNavigateToResults: (score: Int, total: Int) -> Unit = { _, _ -> }
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is QuizPlayEffect.NavigateToResults -> {
                    onNavigateToResults(effect.score, effect.total)
                }
            }
        }
    }

    if (state.isFinished) {
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
                    correctAnswers = state.questions.map { it.correctAnswerIndex },
                    immediateCorrection = state.immediateCorrection,
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
                            if (state.immediateCorrection) {
                                Text(
                                    text = "${state.score} pts",
                                    color = if (state.score >= 0) SuccessGreen else ErrorRed,
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
                QuizBottomBar(
                    canGoBack = state.currentIndex > 0,
                    onBack = { viewModel.onIntent(QuizPlayIntent.OnBackClick) },
                    onNext = { viewModel.onIntent(QuizPlayIntent.OnNextClick) },
                    showNext = true, // Siempre mostramos siguiente/finalizar
                    nextButtonText = if (state.currentIndex == state.questions.size - 1) "Finalizar" else "Siguiente"
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
                            text = question.topicLabel,
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

                Spacer(modifier = Modifier.height(24.dp))

                // Question Text
                Text(
                    text = question.text,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = StudyTheme.textMain,
                    lineHeight = 26.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Options
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    question.options.forEachIndexed { index, option ->
                        val isCorrect = if (state.isCorrected) {
                            if (index == question.correctAnswerIndex) true
                            else if (index == state.selectedOptionIndex) false
                            else null
                        } else null

                        QuizOptionItem(
                            text = option,
                            isSelected = state.selectedOptionIndex == index,
                            isCorrect = isCorrect,
                            immediateCorrection = state.immediateCorrection,
                            onClick = { viewModel.onIntent(QuizPlayIntent.OnOptionSelect(index)) }
                        )
                    }
                }

                if (state.isCorrected) {
                    Spacer(modifier = Modifier.height(24.dp))
                    if (state.selectedOptionIndex == question.correctAnswerIndex) {
                        CorrectionBanner(
                            text = "¡Correcta! (+1)",
                            icon = Icons.Default.CheckCircle,
                            color = SuccessGreen,
                            bgColor = LightSuccessGreen
                        )
                    } else {
                        CorrectionBanner(
                            text = "Incorrecta (-1)",
                            icon = Icons.Default.Cancel,
                            color = ErrorRed,
                            bgColor = LightErrorRed,
                            extraText = "Respuesta: ${question.options[question.correctAnswerIndex]}"
                        )
                    }
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
            }
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

@Composable
fun QuizOptionItem(
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean?, 
    immediateCorrection: Boolean,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = when {
            isCorrect == true -> SuccessGreen
            isCorrect == false && isSelected -> ErrorRed
            isSelected && !immediateCorrection -> StudyTheme.textMain
            else -> StudyTheme.cardBorder
        }, label = "border"
    )

    val bgColor = when {
        isCorrect == true -> LightSuccessGreen
        isCorrect == false && isSelected -> LightErrorRed
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
                width = if (isSelected || isCorrect != null) 1.5.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = isCorrect == null) { onClick() },
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
                    .border(2.dp, radioColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
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

            if (isCorrect == true) {
                Icon(Icons.Default.Check, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(20.dp))
            } else if (isCorrect == false && isSelected) {
                Icon(Icons.Default.Close, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun QuizSummaryDrawerContent(
    questionsCount: Int,
    currentIndex: Int,
    userAnswers: Map<Int, Int>,
    correctAnswers: List<Int>,
    immediateCorrection: Boolean,
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
                val isAnswered = userAnswers.containsKey(index)
                val isCurrent = index == currentIndex
                
                val boxColor = when {
                    immediateCorrection && isAnswered -> {
                        if (userAnswers[index] == correctAnswers[index]) SuccessGreen else ErrorRed
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
                        color = if (isAnswered) Color.White else StudyTheme.textMain,
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
