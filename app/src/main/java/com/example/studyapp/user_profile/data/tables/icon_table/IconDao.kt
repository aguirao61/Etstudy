package com.example.studyapp.user_profile.data.tables.icon_table

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface IconDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateIcons(icons: List<IconLocalEntity>)

    @Query("SELECT * FROM icons")
    suspend fun getAllIcons(): List<IconLocalEntity>

    @Query("SELECT iconImage FROM icons WHERE uniqueIconId = :uniqueIconId")
    suspend fun getIconByUid(uniqueIconId: Int): String?
}