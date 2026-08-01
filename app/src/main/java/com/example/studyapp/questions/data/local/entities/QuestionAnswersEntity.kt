package com.example.studyapp.questions.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

// This table associates a correct answer to a question, this way we have a one to many relationship
@Entity(tableName = "question_answers")
data class QuestionAnswersEntity(
    @PrimaryKey(autoGenerate = true) val answerId: Int = 0,
    val questionId: Int,
    val answer: String
)
