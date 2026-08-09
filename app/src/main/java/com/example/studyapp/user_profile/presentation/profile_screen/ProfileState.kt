package com.example.studyapp.user_profile.presentation.profile_screen

import com.example.studyapp.user_profile.domain.models.User

data class ProfileState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showEditNameDialog: Boolean = false,
    val tempName: String = "",
    val showEditMusicDialog: Boolean = false,
    val tempSongName: String = "",
    val tempArtistName: String = ""
)