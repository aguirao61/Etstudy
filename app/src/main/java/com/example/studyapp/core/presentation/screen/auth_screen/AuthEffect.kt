package com.example.studyapp.core.presentation.screen.auth_screen

sealed class AuthEffect {
    data class NavigateToHome(val userId: Int) : AuthEffect()
    data class ShowToast(val message: String) : AuthEffect()
}
