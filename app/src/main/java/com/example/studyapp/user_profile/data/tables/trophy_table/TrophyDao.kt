package com.example.studyapp.user_profile.data.tables.trophy_table

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TrophyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateTrophies(trophies: List<TrophyLocalEntity>)

    @Query("SELECT * FROM trophies " +
            "ORDER BY trophySubject DESC, trophyPoints DESC" +
            "")
    suspend fun getAllTrophies(): List<TrophyLocalEntity>

    @Query("SELECT * FROM trophies " +
            "WHERE uniqueTrophyId IN (:uniqueTrophiesIds)" +
            "ORDER BY trophySubject DESC, trophyPoints DESC" +
            "")
    suspend fun getTrophiesByUids(uniqueTrophiesIds: List<Int>): List<TrophyLocalEntity>
}