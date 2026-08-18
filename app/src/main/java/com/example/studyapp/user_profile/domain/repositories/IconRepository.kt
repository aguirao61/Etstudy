package com.example.studyapp.user_profile.domain.repositories

import com.example.studyapp.user_profile.domain.models.Icon
import kotlinx.coroutines.flow.Flow

interface IconRepository {
    fun getAllIcons(userId: Int): Flow<List<Icon>>
    suspend fun getTopIcons(userId: Int, limit: Int): List<Icon>
    suspend fun unlockIcon(userId: Int, iconId: Int)
}
