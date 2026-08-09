package com.example.studyapp.user_profile.domain.repositories

import com.example.studyapp.user_profile.domain.models.Banner
import kotlinx.coroutines.flow.Flow

interface BannerRepository {
    fun getUserBanners(userId: Int): Flow<List<Banner>>
    suspend fun unlockBanner(userId: Int, bannerId: Int, courseId: Int?)
    suspend fun equipBanner(userId: Int, bannerId: Int?)
}
