package com.example.studyapp.questions.presentation.quiz_play_screen

sealed class QuizPlayIntent {
    data class OnOptionSelect(val index: Int) : QuizPlayIntent()
    object OnNextClick : QuizPlayIntent()
    object OnBackClick : QuizPlayIntent()
    object OnCloseClick : QuizPlayIntent()
    data class OnQuestionJump(val index: Int) : QuizPlayIntent()
}
