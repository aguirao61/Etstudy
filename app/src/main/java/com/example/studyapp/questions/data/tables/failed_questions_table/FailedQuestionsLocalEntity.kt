package com.example.studyapp.questions.data.tables.failed_questions_table

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.studyapp.questions.data.tables.questions_table.QuestionLocalEntity
import com.example.studyapp.user_profile.data.tables.user_table.UserLocalEntity

@Entity(
    tableName = "failed_questions",
    foreignKeys = [
        ForeignKey(
            entity = UserLocalEntity::class,
            parentColumns = ["uniqueUserId"],
            childColumns = ["uniqueUserId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = QuestionLocalEntity::class,
            parentColumns = ["uniqueQuestionId"],
            childColumns = ["uniqueQuestionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["uniqueUserId"]),
        Index(value = ["uniqueQuestionId"])
    ]
)
data class FailedQuestionsLocalEntity(
    @PrimaryKey(autoGenerate = true) val failedQuestionId: Int = 0,
    val uniqueUserId: Int,
    val uniqueQuestionId: Int,

    val failedQuestionCount: Int = 0,
    val isFailed: Boolean = false
)
