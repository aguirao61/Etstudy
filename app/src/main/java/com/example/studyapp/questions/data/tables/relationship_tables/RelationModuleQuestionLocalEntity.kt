package com.example.studyapp.questions.data.tables.relationship_tables

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.studyapp.questions.data.tables.modules_table.ModuleLocalEntity
import com.example.studyapp.questions.data.tables.questions_table.QuestionLocalEntity

@Entity(
    "relation_modules_and_questions",
    foreignKeys = [
        ForeignKey(
            entity = ModuleLocalEntity::class,
            parentColumns = ["uniqueModuleId"],
            childColumns = ["uniqueModuleId"],
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
        Index(value = ["uniqueModuleId"]),
        Index(value = ["uniqueQuestionId"])
    ]
)
data class RelationModuleQuestionLocalEntity(
    @PrimaryKey(autoGenerate = true) val uniqueRelationId: Int = 0,
    val uniqueModuleId: Int,
    val uniqueQuestionId: Int
)