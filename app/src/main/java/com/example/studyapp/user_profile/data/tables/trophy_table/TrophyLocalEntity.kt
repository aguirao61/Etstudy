package com.example.studyapp.user_profile.data.tables.trophy_table

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trophies")
data class TrophyLocalEntity(
    @PrimaryKey(autoGenerate = true) val uniqueTrophyId: Int = 0,
    val trophyName: String = "",
    val trophyIcon: String = "",
    val trophySubject: String = "",
    val trophyPoints: Int = 0
)
