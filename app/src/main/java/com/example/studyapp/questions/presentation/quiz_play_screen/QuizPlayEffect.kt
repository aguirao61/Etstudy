package com.example.studyapp.questions.presentation.quiz_play_screen

import com.example.studyapp.questions.domain.models.Attempt

sealed class QuizPlayEffect {
    data class NavigateToResults(val attempt: Attempt) : QuizPlayEffect()
}
