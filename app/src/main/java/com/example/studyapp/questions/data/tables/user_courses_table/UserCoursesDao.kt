package com.example.studyapp.questions.data.tables.user_courses_table

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserCoursesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUserCourse(userCourse: UserCoursesLocalEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUserCoursesIgnore(userCourses: List<UserCoursesLocalEntity>)

    @Query("SELECT * FROM user_courses WHERE uniqueUserId = :userId AND uniqueCourseId = :courseId LIMIT 1")
    suspend fun getUserCourse(userId: Int, courseId: Int): UserCoursesLocalEntity?

    @Query("UPDATE user_courses SET isFavorite = :isFavorite WHERE uniqueUserId = :userId AND uniqueCourseId = :courseId")
    suspend fun updateFavorite(userId: Int, courseId: Int, isFavorite: Boolean)

    @Query("UPDATE user_courses SET testsCompleted = testsCompleted + 1 WHERE uniqueUserId = :userId AND uniqueCourseId = :courseId")
    suspend fun incrementTestsCompleted(userId: Int, courseId: Int)

    @Query("UPDATE user_courses SET testsPassed = testsPassed + 1 WHERE uniqueUserId = :userId AND uniqueCourseId = :courseId")
    suspend fun incrementTestsPassed(userId: Int, courseId: Int)

    @Query("SELECT testsPassed FROM user_courses WHERE uniqueUserId = :userId AND uniqueCourseId = :courseId")
    suspend fun getTestsPassed(userId: Int, courseId: Int): Int?

    @Query("SELECT COUNT(*) FROM user_courses WHERE uniqueUserId = :userId AND isCourseCompleted = 1")
    fun getCompletedCoursesCountFlow(userId: Int): Flow<Int>

    @Query("SELECT COUNT(*) FROM user_courses WHERE uniqueUserId = :userId AND isCourseCompleted = 1")
    suspend fun getCompletedCoursesCount(userId: Int): Int

    @Query("""
        SELECT c.*, uc.isFavorite, uc.isCourseCompleted, uc.testsCompleted, uc.testsPassed,
               (SELECT COUNT(DISTINCT f.uniqueQuestionId)
                FROM failed_questions f
                JOIN relation_modules_and_questions r ON f.uniqueQuestionId = r.uniqueQuestionId
                JOIN modules m ON r.uniqueModuleId = m.uniqueModuleId
                WHERE f.uniqueUserId = :userId AND f.isFailed = 1 AND m.uniqueCourseId = c.uniqueCourseId) as failedQuestionsCount
        FROM courses c 
        LEFT JOIN user_courses uc ON c.uniqueCourseId = uc.uniqueCourseId AND uc.uniqueUserId = :userId
        GROUP BY c.uniqueCourseId
    """)
    fun getSubjectsWithUserStatus(userId: Int): Flow<List<UserCourseDisplay>>
}

data class UserCourseDisplay(
    val uniqueCourseId: Int,
    val courseName: String,
    val courseCode: String,
    val isFavorite: Boolean?,
    val isCourseCompleted: Boolean?,
    val testsCompleted: Int?,
    val testsPassed: Int?,
    val failedQuestionsCount: Int? = 0
)
