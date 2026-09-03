package com.example.studyapp.user_profile.data.tables.daily_mission_table

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_missions")
data class DailyMissionLocalEntity(
    @PrimaryKey
    val userId: Int,
    val lastUpdate: Long = 0,
    val quizzesCompleted: Int = 0,
    val quizzesPassed: Int = 0,
    val subjectsStudied: String = "", // Comma separated IDs
    val claimedMask: Int = 0 // Bitmask: 1=5 quizzes, 2=2 passed, 4=2 subjects, 8=All bonus
)
