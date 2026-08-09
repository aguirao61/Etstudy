package com.example.studyapp.user_profile.data.repositories

import com.example.studyapp.user_profile.data.mappers.toDomainModel
import com.example.studyapp.user_profile.data.tables.banner_table.BannerDao
import com.example.studyapp.user_profile.data.tables.user_banner_table.UserBannerDao
import com.example.studyapp.user_profile.data.tables.user_table.UserProfileDao
import com.example.studyapp.user_profile.domain.models.Banner
import com.example.studyapp.user_profile.domain.repositories.BannerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BannerRepositoryImpl(
    private val bannerDao: BannerDao,
    private val userBannerDao: UserBannerDao,
    private val userDao: UserProfileDao
) : BannerRepository {

    override fun getUserBanners(userId: Int): Flow<List<Banner>> {
        return userBannerDao.getUserBannersWithStatus(userId).map { list ->
            list.map { it.toDomainModel() }
        }
    }

    override suspend fun unlockBanner(userId: Int, bannerId: Int, courseId: Int?) {
        userBannerDao.unlockBanner(userId, bannerId, courseId)
    }

    override suspend fun equipBanner(userId: Int, bannerId: Int?) {
        val user = userDao.getUserById(userId)
        val finalBannerId = if (bannerId != null && bannerId > 0) bannerId else null
        userDao.updateEquippedCosmetics(userId, user?.equippedIconId, finalBannerId)
    }
}
