package com.example.studyapp.core.presentation.screen.home_screen

import com.example.studyapp.questions.domain.SubjectFlow

sealed class HomeEffect {
    data class ShowToast(val message: String) : HomeEffect()
    data class NavigateToStudy(val flow: SubjectFlow) : HomeEffect()
    data object NavigateToProfile : HomeEffect()
    data object NavigateToCommunity : HomeEffect()
    data object NavigateToSettings : HomeEffect()
}
