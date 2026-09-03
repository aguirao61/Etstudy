package com.example.studyapp.user_profile.data.repositories

import com.example.studyapp.user_profile.data.mappers.toDomainModel
import com.example.studyapp.user_profile.data.mappers.toLocalEntity
import com.example.studyapp.user_profile.data.tables.daily_mission_table.DailyMissionDao
import com.example.studyapp.user_profile.data.tables.daily_mission_table.DailyMissionLocalEntity
import com.example.studyapp.user_profile.domain.models.DailyMission
import com.example.studyapp.user_profile.domain.repositories.DailyMissionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar

class DailyMissionRepositoryImpl(
    private val dailyMissionDao: DailyMissionDao
) : DailyMissionRepository {
    override suspend fun getDailyMission(userId: Int): DailyMission? {
        return dailyMissionDao.getDailyMissionByUserId(userId)?.toDomainModel()
    }

    override fun getDailyMissionFlow(userId: Int): Flow<DailyMission?> {
        return dailyMissionDao.getDailyMissionFlow(userId).map { it?.toDomainModel() }
    }

    override suspend fun updateDailyMission(mission: DailyMission) {
        dailyMissionDao.updateDailyMission(mission.toLocalEntity())
    }

    override suspend fun createDailyMission(userId: Int) {
        dailyMissionDao.insertDailyMission(DailyMissionLocalEntity(userId = userId))
    }

    override suspend fun checkAndResetDailyMission(userId: Int) {
        val mission = getDailyMission(userId) ?: return
        val currentTime = System.currentTimeMillis()
        
        // Safeguard: Ignore if system time is before last update
        if (currentTime <= mission.lastUpdate && mission.lastUpdate != 0L) {
            return
        }

        val lastCalendar = Calendar.getInstance().apply { timeInMillis = mission.lastUpdate }
        val currentCalendar = Calendar.getInstance().apply { timeInMillis = currentTime }
        
        val isNewDay = lastCalendar.get(Calendar.YEAR) != currentCalendar.get(Calendar.YEAR) ||
                lastCalendar.get(Calendar.DAY_OF_YEAR) != currentCalendar.get(Calendar.DAY_OF_YEAR)
        
        if (isNewDay && mission.lastUpdate != 0L) {
            dailyMissionDao.updateDailyMission(
                DailyMissionLocalEntity(
                    userId = userId,
                    lastUpdate = currentTime,
                    quizzesCompleted = 0,
                    quizzesPassed = 0,
                    subjectsStudied = "",
                    claimedMask = 0
                )
            )
        }
    }
}
