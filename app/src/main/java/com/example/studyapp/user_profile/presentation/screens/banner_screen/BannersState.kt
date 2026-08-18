package com.example.studyapp.user_profile.presentation.screens.banner_screen

import com.example.studyapp.user_profile.domain.models.Banner

data class BannersState(
    val banners: List<Banner> = emptyList(),
    val selectedBannerId: Int? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
