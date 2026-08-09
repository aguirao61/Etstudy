package com.example.studyapp.questions.domain.models

data class Question(
    val id: Int,
    val text: String,
    val options: List<String>,
    val correctAnswerIndices: List<Int>,
    val topicLabel: String = ""
) {
    val isMultiSelect: Boolean get() = correctAnswerIndices.size > 1
}
