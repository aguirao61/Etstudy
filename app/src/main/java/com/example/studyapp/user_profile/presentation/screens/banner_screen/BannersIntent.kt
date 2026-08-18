package com.example.studyapp.user_profile.presentation.screens.banner_screen

sealed class BannersIntent {
    data class OnBannerSelect(val bannerId: Int) : BannersIntent()
    data object OnEquipClick : BannersIntent()
}
