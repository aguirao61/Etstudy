package com.example.studyapp.questions.presentation.subject_screen

import com.example.studyapp.questions.domain.models.Subject

sealed class SubjectsEffect {
    data class ShowToast(val message: String) : SubjectsEffect()
    data object NavigateBack : SubjectsEffect()
    data class NavigateToQuizConfig(val subject: Subject) : SubjectsEffect()
    data object NavigateToProfile : SubjectsEffect()
}
