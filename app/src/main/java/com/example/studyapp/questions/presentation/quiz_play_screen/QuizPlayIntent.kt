package com.example.studyapp.questions.presentation.quiz_play_screen

sealed class QuizPlayIntent {
    data class OnOptionSelect(val index: Int) : QuizPlayIntent()
    data object OnNextClick : QuizPlayIntent()
    data object OnBackClick : QuizPlayIntent()
    data object OnCloseClick : QuizPlayIntent()
    data class OnQuestionJump(val index: Int) : QuizPlayIntent()
    data object OnValidateClick : QuizPlayIntent()
}
