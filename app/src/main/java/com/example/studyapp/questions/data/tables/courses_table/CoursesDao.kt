package com.example.studyapp.questions.data.tables.courses_table

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CoursesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateCourses(courses: List<CourseLocalEntity>)

    @Upsert
    suspend fun upsertCourses(courses: List<CourseLocalEntity>)

    @Delete
    suspend fun deleteCourses(courses: List<CourseLocalEntity>)

    @Query("SELECT * FROM courses")
    suspend fun getAllCourses(): List<CourseLocalEntity>

    @Query("SELECT uniqueCourseId FROM courses WHERE courseCode = :code LIMIT 1")
    suspend fun getCourseIdByCode(code: String): Int?

    @Query("DELETE FROM courses")
    suspend fun deleteAllCourses()
}