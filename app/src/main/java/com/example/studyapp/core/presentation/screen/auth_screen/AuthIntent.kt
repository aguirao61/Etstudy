package com.example.studyapp.core.presentation.screen.auth_screen

sealed class AuthIntent {
    data class OnUuidChange(val uuid: String) : AuthIntent()
    data class OnAliasChange(val alias: String) : AuthIntent()
    object ToggleMode : AuthIntent()
    object OnSubmit : AuthIntent()
    object DismissSuccessDialog : AuthIntent()
}
