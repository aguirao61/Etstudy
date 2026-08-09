package com.example.studyapp.questions.data.tables.questions_table

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class QuestionLocalEntity(
    @PrimaryKey(autoGenerate = true) val uniqueQuestionId: Int = 0,
    val questionText: String = "",
    val questionImage: String? = "",
    val questionVersion: Int = 1,
    val isQuestionDeleted: Boolean = false
)
