package com.example.studyapp.core.presentation.auth_screen

data class AuthState(
    val isLoginMode: Boolean = true,
    val uuidInput: String = "",
    val aliasInput: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val createdUserUuid: String? = null,
    val showCreationSuccessDialog: Boolean = false
)
