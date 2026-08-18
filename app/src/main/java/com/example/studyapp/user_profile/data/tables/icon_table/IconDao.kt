package com.example.studyapp.user_profile.data.tables.icon_table

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert

@Dao
interface IconDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIcon(icon: IconLocalEntity): Long

    @Update
    suspend fun updateIcon(icon: IconLocalEntity)

    @Upsert
    suspend fun upsertIcons(icons: List<IconLocalEntity>)

    @Delete
    suspend fun deleteIcons(icons: List<IconLocalEntity>)

    @Query("SELECT * FROM icons")
    suspend fun getAllIcons(): List<IconLocalEntity>

    @Query("SELECT iconImage FROM icons WHERE uniqueIconId = :uniqueIconId")
    suspend fun getIconByUid(uniqueIconId: Int): String?

    @Query("SELECT * FROM icons ORDER BY uniqueIconId ASC LIMIT :limit")
    suspend fun getTopIcons(limit: Int): List<IconLocalEntity>
}
