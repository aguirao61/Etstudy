package com.example.studyapp.user_profile.data.tables.banner_table

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface BannerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBanners(banners: List<BannerLocalEntity>)

    @Upsert
    suspend fun upsertBanners(banners: List<BannerLocalEntity>)

    @Delete
    suspend fun deleteBanners(banners: List<BannerLocalEntity>)

    @Query("SELECT * FROM banners WHERE uniqueBannerId = :uniqueBannerId")
    suspend fun getBannerByUid(uniqueBannerId: Int): BannerLocalEntity?

    @Query("SELECT * FROM banners " +
            "ORDER BY bannerSubject DESC, bannerPoints DESC" +
            "")
    suspend fun getAllBanners(): List<BannerLocalEntity>

    @Query("DELETE FROM banners")
    suspend fun deleteAllBanners()
}