package com.example.studyapp.user_profile.data.tables.user_trophy_table

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserTrophyDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun initializeUserTrophies(userTrophies: List<UserTrophyLocalEntity>)

    @Query("UPDATE user_trophies SET isTrophyObtained = 1 WHERE uniqueUserId = :userId AND uniqueTrophyId = :trophyId")
    suspend fun unlockTrophy(userId: Int, trophyId: Int)

    @Query("SELECT * FROM user_trophies WHERE uniqueUserId = :userId")
    suspend fun getAllUserTrophies(userId: Int): List<UserTrophyLocalEntity>

    @Query("SELECT * FROM user_trophies WHERE uniqueUserId = :userId AND isTrophyObtained = 1")
    suspend fun getUnlockedUserTrophies(userId: Int): List<UserTrophyLocalEntity>
}