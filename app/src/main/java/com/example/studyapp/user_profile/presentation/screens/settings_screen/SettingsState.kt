package com.example.studyapp.user_profile.presentation.screens.settings_screen

import com.example.studyapp.user_profile.domain.models.ThemeMode

data class SettingsState(
    val userId: Int = 0,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val isLoading: Boolean = false,
    val showDeleteConfirmation: Boolean = false
)
