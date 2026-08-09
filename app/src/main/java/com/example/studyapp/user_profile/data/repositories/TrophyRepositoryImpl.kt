package com.example.studyapp.user_profile.data.repositories

import com.example.studyapp.user_profile.data.tables.trophy_table.TrophyDao
import com.example.studyapp.user_profile.data.tables.trophy_table.TrophyLocalEntity
import com.example.studyapp.user_profile.data.tables.user_trophy_table.UserTrophyDao
import com.example.studyapp.user_profile.domain.repositories.TrophyRepository
import com.example.studyapp.user_profile.domain.models.Trophy

class TrophyRepositoryImpl(
    private val trophyDao: TrophyDao,
    private val userTrophyDao: UserTrophyDao
) : TrophyRepository {

    override suspend fun getAllTrophies(): List<Trophy> {
        return trophyDao.getAllTrophies().map { it.toDomainModel() }
    }

    override suspend fun unlockTrophy(userId: Int, trophyId: Int) {
        userTrophyDao.unlockTrophy(userId, trophyId)
    }
}

fun TrophyLocalEntity.toDomainModel() = Trophy(
    trophyName = trophyName,
    trophyIcon = trophyIcon,
    trophySubject = trophySubject,
    trophyPoints = trophyPoints
)
