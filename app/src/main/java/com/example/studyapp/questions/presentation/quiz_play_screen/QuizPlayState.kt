package com.example.studyapp.questions.presentation.quiz_play_screen

import com.example.studyapp.questions.domain.models.Question

data class QuizPlayState(
    val questions: List<Question> = emptyList(),
    val currentIndex: Int = 0,
    val selectedOptionIndices: Set<Int> = emptySet(),
    val userAnswers: Map<Int, Set<Int>> = emptyMap(), // questionIndex -> set of optionIndices
    val validatedIndices: Set<Int> = emptySet(),
    val isCorrected: Boolean = false,
    val score: Int = 0,
    val immediateCorrection: Boolean = false,
    val isTimerEnabled: Boolean = false,
    val timeElapsedSeconds: Long = 0,
    val isFinished: Boolean = false,
    val isLoading: Boolean = true
) {
    val currentQuestion: Question? get() = questions.getOrNull(currentIndex)
}
