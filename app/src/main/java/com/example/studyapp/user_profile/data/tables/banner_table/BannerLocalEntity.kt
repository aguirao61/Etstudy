package com.example.studyapp.user_profile.data.tables.banner_table

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.studyapp.questions.data.tables.courses_table.CourseLocalEntity

@Entity(
    tableName = "banners",
    foreignKeys = [
        ForeignKey(
            entity = CourseLocalEntity::class,
            parentColumns = ["uniqueCourseId"],
            childColumns = ["uniqueCourseId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["uniqueCourseId"])]
)
data class BannerLocalEntity(
    @PrimaryKey(autoGenerate = true) val uniqueBannerId: Int = 0,
    val bannerContent: String = "",
    val bannerColour: Long = 0L,
    val bannerTextColour: Long = 0L,
    val bannerIcon: String = "",
    val bannerSubject: String = "",
    val bannerPoints: Int = 0,
    val uniqueCourseId: Int? = null
)
