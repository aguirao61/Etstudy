package com.example.studyapp.user_profile.data.tables.user_banner_table

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserBannerDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun initializeUserBanners(userBanners: List<UserBannerLocalEntity>)

    @Query("""
        UPDATE user_banners 
        SET uniqueCourseId = (SELECT uniqueCourseId FROM banners WHERE uniqueBannerId = user_banners.uniqueBannerId)
        WHERE uniqueCourseId IS NULL
    """)
    suspend fun syncCourseIds()

    @Query("UPDATE user_banners SET isBannerObtained = 1 WHERE uniqueUserId = :userId AND uniqueBannerId = :bannerId AND (uniqueCourseId = :courseId OR uniqueCourseId IS NULL)")
    suspend fun unlockBanner(userId: Int, bannerId: Int, courseId: Int?)

    @Query("SELECT * FROM user_banners WHERE uniqueUserId = :userId")
    suspend fun getAllUserBanners(userId: Int): List<UserBannerLocalEntity>

    @Query("""
        SELECT b.*, ub.isBannerObtained, ub.uniqueCourseId
        FROM banners b
        LEFT JOIN user_banners ub ON b.uniqueBannerId = ub.uniqueBannerId AND ub.uniqueUserId = :userId
        ORDER BY b.bannerSubject DESC, b.bannerPoints DESC
    """)
    fun getUserBannersWithStatus(userId: Int): Flow<List<UserBannerDisplay>>
}

data class UserBannerDisplay(
    val uniqueBannerId: Int,
    val bannerContent: String,
    val bannerColour: Long,
    val bannerTextColour: Long,
    val bannerIcon: String,
    val bannerSubject: String,
    val bannerPoints: Int,
    val isBannerObtained: Boolean?,
    val uniqueCourseId: Int?
)
