package com.example.studyapp.user_profile.data.tables.daily_mission_table

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyMissionDao {
    @Query("SELECT * FROM daily_missions WHERE userId = :userId LIMIT 1")
    suspend fun getDailyMissionByUserId(userId: Int): DailyMissionLocalEntity?

    @Query("SELECT * FROM daily_missions WHERE userId = :userId LIMIT 1")
    fun getDailyMissionFlow(userId: Int): Flow<DailyMissionLocalEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyMission(mission: DailyMissionLocalEntity)

    @Update
    suspend fun updateDailyMission(mission: DailyMissionLocalEntity)
}
