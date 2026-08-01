package com.example.studyapp.questions.domain

data class Question(
    val id: Int,
    val text: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val topicLabel: String = ""
)
