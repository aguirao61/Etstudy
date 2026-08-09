package com.example.studyapp.user_profile.data.tables.user_trophy_table

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.studyapp.user_profile.data.tables.trophy_table.TrophyLocalEntity
import com.example.studyapp.user_profile.data.tables.user_table.UserLocalEntity

@Entity(
    tableName = "user_trophies",
    foreignKeys = [
        ForeignKey(
            entity = TrophyLocalEntity::class,
            parentColumns = ["uniqueTrophyId"],
            childColumns = ["uniqueTrophyId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserLocalEntity::class,
            parentColumns = ["uniqueUserId"],
            childColumns = ["uniqueUserId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["uniqueTrophyId"]),
        Index(value = ["uniqueUserId"])
    ]
)
data class UserTrophyLocalEntity(
    @PrimaryKey(autoGenerate = true) val userTrophyId: Int = 0,
    val isTrophyObtained: Boolean = false,

    val uniqueUserId: Int,
    val uniqueTrophyId: Int
)
