package com.example.studyapp.core.presentation.home_screen

import com.example.studyapp.questions.domain.SubjectFlow

sealed class HomeEffect {
    data class ShowToast(val message: String) : HomeEffect()
    data class NavigateToSubjects(val flow: SubjectFlow) : HomeEffect()
}
