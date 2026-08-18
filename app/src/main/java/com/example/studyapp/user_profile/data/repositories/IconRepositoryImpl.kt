package com.example.studyapp.user_profile.data.repositories

import com.example.studyapp.user_profile.data.mappers.toDomainModel
import com.example.studyapp.user_profile.data.tables.icon_table.IconDao
import com.example.studyapp.user_profile.data.tables.user_icon_table.UserIconDao
import com.example.studyapp.user_profile.domain.models.Icon
import com.example.studyapp.user_profile.domain.repositories.IconRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class IconRepositoryImpl(
    private val iconDao: IconDao,
    private val userIconDao: UserIconDao
) : IconRepository {

    override fun getAllIcons(userId: Int): Flow<List<Icon>> {
        return userIconDao.getUserIconsWithStatus(userId).map { list ->
            list.map { it.toDomainModel() }
        }
    }

    override suspend fun getTopIcons(userId: Int, limit: Int): List<Icon> {
        return userIconDao.getTopUserIconsWithStatus(userId, limit).map { it.toDomainModel() }
    }

    override suspend fun unlockIcon(userId: Int, iconId: Int) {
        userIconDao.unlockIcon(userId, iconId)
    }
}
