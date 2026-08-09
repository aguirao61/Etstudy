package com.example.studyapp.questions.data.tables.courses_table

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class CourseLocalEntity(
    @PrimaryKey(autoGenerate = true) val uniqueCourseId: Int = 0,
    val courseName: String,
    val courseCode: String
)
