package com.example.studyapp.questions.domain

data class Subject(
    val id: Int,
    val name: String,
    val code: String,
    val isFavorite: Boolean = false
)
