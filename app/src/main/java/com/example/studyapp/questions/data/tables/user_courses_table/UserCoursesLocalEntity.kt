package com.example.studyapp.questions.data.tables.user_courses_table

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.studyapp.questions.data.tables.courses_table.CourseLocalEntity
import com.example.studyapp.user_profile.data.tables.user_table.UserLocalEntity

@Entity(
    tableName = "user_courses",
    indices = [
        Index(value = ["uniqueUserId", "uniqueCourseId"], unique = true)
    ],
    foreignKeys = [
        ForeignKey(
            entity = UserLocalEntity::class,
            parentColumns = ["uniqueUserId"],
            childColumns = ["uniqueUserId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CourseLocalEntity::class,
            parentColumns = ["uniqueCourseId"],
            childColumns = ["uniqueCourseId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserCoursesLocalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val uniqueUserId: Int,
    val uniqueCourseId: Int,

    val isFavorite: Boolean = false,
    val isCourseCompleted: Boolean = false,
    val testsCompleted: Int = 0,
    val testsPassed: Int = 0
)
