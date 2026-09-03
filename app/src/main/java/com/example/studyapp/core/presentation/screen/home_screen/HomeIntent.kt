package com.example.studyapp.core.presentation.screen.home_screen

sealed class HomeIntent {
    data object OnStartClick : HomeIntent()
    data object OnGuideClick : HomeIntent()
    data object OnProfileClick : HomeIntent()
    data class OnNavClick(val item: String) : HomeIntent()
    data object DismissStartPopup : HomeIntent()
    data class OnTestOptionClick(val option: String) : HomeIntent()
    data object OnChallengesClick : HomeIntent()
    data object DismissChallengesDialog : HomeIntent()
}
