package com.example.studyapp.questions.presentation.quiz_play_screen

import com.example.studyapp.questions.domain.Question

data class QuizPlayState(
    val questions: List<Question> = emptyList(),
    val currentIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val userAnswers: Map<Int, Int> = emptyMap(), // index -> optionIndex
    val isCorrected: Boolean = false,
    val score: Int = 0,
    val immediateCorrection: Boolean = false,
    val isFinished: Boolean = false
) {
    val currentQuestion: Question? get() = questions.getOrNull(currentIndex)
}
