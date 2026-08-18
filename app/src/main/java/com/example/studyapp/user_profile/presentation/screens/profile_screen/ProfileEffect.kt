package com.example.studyapp.user_profile.presentation.screens.profile_screen

sealed class ProfileEffect {
    data class ShowToast(val message: String) : ProfileEffect()
}