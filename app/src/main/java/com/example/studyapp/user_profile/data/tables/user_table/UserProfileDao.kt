package com.example.studyapp.user_profile.data.tables.user_table

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: UserLocalEntity): Long

    @Query("SELECT * FROM local_users WHERE uniqueUserId = :userId LIMIT 1")
    suspend fun getUserById(userId: Int): UserLocalEntity?

    @Query("SELECT * FROM local_users WHERE uniqueUserId = :userId LIMIT 1")
    fun getUserFlow(userId: Int): Flow<UserLocalEntity?>

    @Update
    suspend fun updateUser(user: UserLocalEntity)

    @Query("SELECT uniqueUserId FROM local_users")
    suspend fun getAllUserIds(): List<Int>

    @Query("SELECT * FROM local_users")
    fun getAllUsersFlow(): Flow<List<UserLocalEntity>>

    @Query("""
        SELECT u.*, i.iconImage as equippedIconUrl
        FROM local_users u
        LEFT JOIN icons i ON u.equippedIconId = i.uniqueIconId
    """)
    fun getAllUsersWithIconsFlow(): Flow<List<UserWithIcon>>

    @Query("UPDATE local_users SET equippedIconId = :iconId, equippedBannerId = :bannerId WHERE uniqueUserId = :userId")
    suspend fun updateEquippedCosmetics(userId: Int, iconId: Int?, bannerId: Int?)

    @Query("UPDATE local_users SET level = :newLevel, experience = :newExperience WHERE uniqueUserId = :userId")
    suspend fun updateUserProgress(userId: Int, newLevel: Int, newExperience: Int)

    @Query("""
        UPDATE local_users 
        SET questionsAnswered = questionsAnswered + :answered,
            correctAnswers = correctAnswers + :correct,
            completedTests = completedTests + :completedTestsCount,
            completedCourses = completedCourses + :completedCoursesCount,
            studyPoints = :totalStudyPoints
        WHERE uniqueUserId = :userId
    """)
    suspend fun incrementStatsAndSetPoints(
        userId: Int,
        answered: Int,
        correct: Int,
        totalStudyPoints: Int,
        completedTestsCount: Int = 0,
        completedCoursesCount: Int = 0
    )

    @Query("UPDATE local_users SET selectedTheme = :theme WHERE uniqueUserId = :userId")
    suspend fun updateUserTheme(userId: Int, theme: String)

    @Query("DELETE FROM local_users WHERE uniqueUserId = :userId")
    suspend fun deleteUserById(userId: Int)
}

data class UserWithIcon(
    @androidx.room.Embedded val user: UserLocalEntity,
    val equippedIconUrl: String?
)