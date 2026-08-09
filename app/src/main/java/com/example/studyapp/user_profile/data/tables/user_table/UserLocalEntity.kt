package com.example.studyapp.user_profile.data.tables.user_table

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.studyapp.user_profile.data.tables.banner_table.BannerLocalEntity
import com.example.studyapp.user_profile.data.tables.icon_table.IconLocalEntity

@Entity(
    tableName = "local_users",
    foreignKeys = [
        ForeignKey(
            entity = IconLocalEntity::class,
            parentColumns = ["uniqueIconId"],
            childColumns = ["equippedIconId"],
            onDelete = ForeignKey.SET_NULL // Si borras el icono de la BD, el equipado se pone a null
        ),
        ForeignKey(
            entity = BannerLocalEntity::class,
            parentColumns = ["uniqueBannerId"],
            childColumns = ["equippedBannerId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["equippedIconId"]),
        Index(value = ["equippedBannerId"])
    ]
)
data class UserLocalEntity(
    @PrimaryKey(autoGenerate = true)
    val uniqueUserId: Int = 0,
    val username: String,
    val level: Int,
    val experience: Int,
    val userSongName: String,
    val userSongArtist: String,
    val studyPoints: Int,
    val questionsAnswered: Int,
    val correctAnswers: Int,
    val completedTests: Int,
    val completedCourses: Int,
    // Tipos nullable (Int?) para poder inicializar la cuenta sin ningún cosmético equipado
    val equippedIconId: Int? = null,
    val equippedBannerId: Int? = null
)
