package com.example.studyapp.questions.presentation.quiz_play_screen

sealed class QuizPlayEffect {
    data class NavigateToResults(val score: Int, val total: Int) : QuizPlayEffect()
}
