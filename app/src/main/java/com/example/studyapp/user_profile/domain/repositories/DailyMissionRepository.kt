package com.example.studyapp.user_profile.domain.repositories

import com.example.studyapp.user_profile.domain.models.DailyMission
import kotlinx.coroutines.flow.Flow

interface DailyMissionRepository {
    suspend fun getDailyMission(userId: Int): DailyMission?
    fun getDailyMissionFlow(userId: Int): Flow<DailyMission?>
    suspend fun updateDailyMission(mission: DailyMission)
    suspend fun createDailyMission(userId: Int)
    suspend fun checkAndResetDailyMission(userId: Int)
}
