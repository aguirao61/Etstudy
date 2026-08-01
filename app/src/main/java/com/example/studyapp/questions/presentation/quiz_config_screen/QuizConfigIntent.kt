package com.example.studyapp.questions.presentation.quiz_config_screen

sealed class QuizConfigIntent {
    data class Initialize(val flowType: String) : QuizConfigIntent()
    data class OnTopicSelect(val index: Int) : QuizConfigIntent()
    data class OnQuestionCountChange(val count: Int) : QuizConfigIntent()
    data class OnRandomOrderToggle(val isRandom: Boolean) : QuizConfigIntent()
    data class OnTimerToggle(val isEnabled: Boolean) : QuizConfigIntent()
    data class OnCorrectionModeToggle(val immediate: Boolean) : QuizConfigIntent()
    data object OnBackClick : QuizConfigIntent()
    data object OnStartTestClick : QuizConfigIntent()
}
