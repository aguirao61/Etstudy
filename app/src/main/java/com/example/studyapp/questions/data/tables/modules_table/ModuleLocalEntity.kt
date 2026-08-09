package com.example.studyapp.questions.data.tables.modules_table

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.studyapp.questions.data.tables.courses_table.CourseLocalEntity

@Entity(
    tableName = "modules",
    foreignKeys = [
        ForeignKey(
            entity = CourseLocalEntity::class,
            parentColumns = ["uniqueCourseId"],
            childColumns = ["uniqueCourseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["uniqueCourseId"])
    ]
)
data class ModuleLocalEntity(
    @PrimaryKey(autoGenerate = true) val uniqueModuleId: Int = 0,
    val uniqueCourseId: Int,
    val moduleName: String,
    val moduleNumber: Int
)
