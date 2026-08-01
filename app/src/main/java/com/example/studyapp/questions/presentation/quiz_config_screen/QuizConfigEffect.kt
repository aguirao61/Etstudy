package com.example.studyapp.questions.presentation.quiz_config_screen

sealed class QuizConfigEffect {
    data object NavigateBack : QuizConfigEffect()
    data class StartQuiz(
        val subjectId: Int,
        val topicIndex: Int,
        val questionCount: Int,
        val isRandom: Boolean,
        val isTimerEnabled: Boolean,
        val immediateCorrection: Boolean
    ) : QuizConfigEffect()
}
