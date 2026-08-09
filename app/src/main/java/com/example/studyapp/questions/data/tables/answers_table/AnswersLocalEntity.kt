package com.example.studyapp.questions.data.tables.answers_table

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.studyapp.questions.data.tables.questions_table.QuestionLocalEntity

@Entity(
    tableName = "answers",
    foreignKeys = [
        ForeignKey(
            entity = QuestionLocalEntity::class,
            parentColumns = ["uniqueQuestionId"],
            childColumns = ["uniqueQuestionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["uniqueQuestionId"])
    ]
)
data class AnswersLocalEntity(
    @PrimaryKey(autoGenerate = true) val uniqueAnswerId: Int = 0,
    val uniqueQuestionId: Int,
    val answerText: String,
    val isCorrect: Boolean
)
