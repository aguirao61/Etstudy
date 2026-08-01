package com.example.studyapp.questions.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey(autoGenerate = true) val courseId: Int = 0,
    val courseName: String
)
