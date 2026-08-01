package com.example.studyapp.questions.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true) val questionId: Int = 0,
    val question: String,
    val questionImage: String?,
    val timesAppeared: Int,
    val timesFailed: Int
)
