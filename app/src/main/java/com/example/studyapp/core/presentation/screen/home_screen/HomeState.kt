package com.example.studyapp.core.presentation.screen.home_screen

import com.example.studyapp.user_profile.domain.models.DailyMission

data class HomeState(
    val userName: String = "",
    val expCurrent: Int = 0,
    val expMax: Int = 0,
    val level: Int = 0,
    val equippedIconUrl: String? = null,
    val currentStreak: Int = 0,
    val isLoading: Boolean = false,
    val isStartPopupVisible: Boolean = false,
    val isDailyChallengesVisible: Boolean = false,
    val dailyMission: DailyMission? = null
)
