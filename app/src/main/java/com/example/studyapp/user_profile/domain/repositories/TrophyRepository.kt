package com.example.studyapp.user_profile.domain.repositories

import com.example.studyapp.user_profile.domain.models.Trophy

interface TrophyRepository {
    suspend fun getAllTrophies(): List<Trophy>
    suspend fun unlockTrophy(userId: Int, trophyId: Int)
}