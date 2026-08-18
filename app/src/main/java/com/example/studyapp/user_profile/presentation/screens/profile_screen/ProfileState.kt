package com.example.studyapp.user_profile.presentation.screens.profile_screen

import com.example.studyapp.user_profile.domain.models.Icon
import com.example.studyapp.user_profile.domain.models.User

data class ProfileState(
    val user: User? = null,
    val topIcons: List<Icon> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showEditNameDialog: Boolean = false,
    val tempName: String = "",
    val showEditMusicDialog: Boolean = false,
    val tempSongName: String = "",
    val tempArtistName: String = ""
)