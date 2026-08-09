package com.example.studyapp.user_profile.data.tables.icon_table

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "icons")
data class IconLocalEntity(
    @PrimaryKey(autoGenerate = true) val uniqueIconId: Int = 0,
    val iconImage: String = "" //icon url
)
