package com.example.studyapp.core.presentation.auth_screen

sealed class AuthEffect {
    data class NavigateToHome(val userId: Int) : AuthEffect()
    data class ShowToast(val message: String) : AuthEffect()
}
