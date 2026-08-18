package com.example.studyapp.user_profile.data.tables.icon_table

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "icons",
    indices = [Index(value = ["iconName"], unique = true)]
)
data class IconLocalEntity(
    @PrimaryKey(autoGenerate = true) val uniqueIconId: Int = 0,
    val iconName: String = "",
    val iconDescription: String = "",
    val iconImage: String = "" // resource name or url
)
