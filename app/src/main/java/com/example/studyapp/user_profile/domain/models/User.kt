package com.example.studyapp.user_profile.domain.models

import com.example.studyapp.core.domain.ExperienceCalculator

data class User(
    val uniqueUserId: Int = 0,
    val username: String,
    val level: Int = 1,
    val experience: Int = 0,
    val userSongName: String = "",
    val userSongArtist: String = "",
    val studyPoints: Int = 0,
    val questionsAnswered: Int = 0,
    val correctAnswers: Int = 0,
    val completedTests: Int = 0,
    val completedCourses: Int = 0,
    val equippedIconId: Int? = null,
    val equippedBannerId: Int? = null,
    val equippedIconUrl: String? = null,
    val equippedBannerColour: Long? = null,
    val equippedBannerTextColour: Long? = null,
    val equippedBannerIcon: String? = null,
    val equippedBannerContent: String? = null
) {
    val maxExperience: Int
        get() = ExperienceCalculator.calculateMaxExp(level)

    companion object {
        fun calculateMaxExp(level: Int): Int = ExperienceCalculator.calculateMaxExp(level)
    }
}
