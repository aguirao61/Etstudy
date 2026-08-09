package com.example.studyapp.user_profile.data.tables.user_banner_table

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.studyapp.questions.data.tables.courses_table.CourseLocalEntity
import com.example.studyapp.user_profile.data.tables.banner_table.BannerLocalEntity
import com.example.studyapp.user_profile.data.tables.user_table.UserLocalEntity

@Entity(
    tableName = "user_banners",
    foreignKeys = [
        ForeignKey(
            entity = BannerLocalEntity::class,
            parentColumns = ["uniqueBannerId"],
            childColumns = ["uniqueBannerId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserLocalEntity::class,
            parentColumns = ["uniqueUserId"],
            childColumns = ["uniqueUserId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CourseLocalEntity::class,
            parentColumns = ["uniqueCourseId"],
            childColumns = ["uniqueCourseId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["uniqueUserId", "uniqueBannerId"], unique = true),
        Index(value = ["uniqueCourseId"])
    ]
)
data class UserBannerLocalEntity(
    @PrimaryKey(autoGenerate = true) val userBannerId: Int = 0,
    val isBannerObtained: Boolean = false,

    val uniqueUserId: Int,
    val uniqueBannerId: Int,
    val uniqueCourseId: Int? = null
)
