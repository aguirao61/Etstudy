package com.example.studyapp.user_profile.domain.models

data class Icon(
    val id: Int,
    val name: String,
    val description: String,
    val imageUrl: String,
    val isUnlocked: Boolean = false
)
