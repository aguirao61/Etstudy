package com.example.studyapp.core.presentation.screen.home_screen

data class HomeState(
    val userName: String = "",
    val expCurrent: Int = 0,
    val expMax: Int = 0,
    val level: Int = 0,
    val equippedIconUrl: String? = null,
    val isLoading: Boolean = false,
    val isStartPopupVisible: Boolean = false
)
