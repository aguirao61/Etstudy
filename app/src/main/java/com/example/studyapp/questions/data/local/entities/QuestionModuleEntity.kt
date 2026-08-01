package com.example.studyapp.questions.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "question_module")
data class QuestionModuleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val questionId: Int,
    val moduleId: Int
)