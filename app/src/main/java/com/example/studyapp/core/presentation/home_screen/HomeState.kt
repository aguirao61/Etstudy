package com.example.studyapp.core.presentation.home_screen

data class HomeState(
    val userName: String = "",
    val expCurrent: Int = 0,
    val expMax: Int = 0,
    val level: Int = 0,
    val isLoading: Boolean = false,
    val isStartPopupVisible: Boolean = false
)
