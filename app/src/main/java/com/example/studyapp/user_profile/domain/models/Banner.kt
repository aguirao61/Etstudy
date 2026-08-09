package com.example.studyapp.user_profile.domain.models

data class Banner(
    val id: Int,
    val bannerContent: String,
    val bannerColour: Long,
    val bannerTextColour: Long,
    val bannerIcon: String,
    val bannerSubject: String,
    val bannerPoints: Int,
    val courseId: Int? = null,
    val isObtained: Boolean = false
)
