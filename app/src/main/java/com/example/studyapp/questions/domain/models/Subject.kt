package com.example.studyapp.questions.domain.models

data class Subject(
    val id: Int,
    val name: String,
    val code: String,
    val isFavorite: Boolean = false,
    val failedQuestionsCount: Int = 0,
    val testsPassed: Int = 0
)
