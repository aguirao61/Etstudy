package com.example.studyapp.user_profile.data.tables.user_icon_table

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserIconDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun initializeUserIcons(userIcons: List<UserIconLocalEntity>)

    @Query("UPDATE user_icons SET isUnlocked = 1 WHERE uniqueUserId = :userId AND uniqueIconId = :iconId")
    suspend fun unlockIcon(userId: Int, iconId: Int)

    @Query("""
        SELECT i.*, ui.isUnlocked 
        FROM icons i 
        JOIN user_icons ui ON i.uniqueIconId = ui.uniqueIconId 
        WHERE ui.uniqueUserId = :userId
        ORDER BY i.uniqueIconId ASC
    """)
    fun getUserIconsWithStatus(userId: Int): Flow<List<UserIconDisplay>>

    @Query("""
        SELECT i.*, ui.isUnlocked 
        FROM icons i 
        JOIN user_icons ui ON i.uniqueIconId = ui.uniqueIconId 
        WHERE ui.uniqueUserId = :userId
        ORDER BY i.uniqueIconId ASC
        LIMIT :limit
    """)
    suspend fun getTopUserIconsWithStatus(userId: Int, limit: Int): List<UserIconDisplay>

    @Query("SELECT isUnlocked FROM user_icons WHERE uniqueUserId = :userId AND uniqueIconId = :iconId")
    suspend fun isIconUnlocked(userId: Int, iconId: Int): Boolean?
}

data class UserIconDisplay(
    val uniqueIconId: Int,
    val iconName: String,
    val iconDescription: String,
    val iconImage: String,
    val isUnlocked: Boolean
)
