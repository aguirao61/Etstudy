package com.example.studyapp.user_profile.data.tables.user_icon_table

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.studyapp.user_profile.data.tables.icon_table.IconLocalEntity
import com.example.studyapp.user_profile.data.tables.user_table.UserLocalEntity

@Entity(
    tableName = "user_icons",
    foreignKeys = [
        ForeignKey(
            entity = UserLocalEntity::class,
            parentColumns = ["uniqueUserId"],
            childColumns = ["uniqueUserId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = IconLocalEntity::class,
            parentColumns = ["uniqueIconId"],
            childColumns = ["uniqueIconId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["uniqueUserId", "uniqueIconId"], unique = true)
    ]
)
data class UserIconLocalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val uniqueUserId: Int,
    val uniqueIconId: Int,
    val isUnlocked: Boolean = false
)
