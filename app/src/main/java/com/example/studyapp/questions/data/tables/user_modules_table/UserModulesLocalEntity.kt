package com.example.studyapp.questions.data.tables.user_modules_table

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.studyapp.questions.data.tables.modules_table.ModuleLocalEntity
import com.example.studyapp.user_profile.data.tables.user_table.UserLocalEntity

@Entity(
    tableName = "user_modules",
    indices = [
        Index(value = ["uniqueUserId", "uniqueModuleId"], unique = true)
    ],
    foreignKeys = [
        ForeignKey(
            entity = UserLocalEntity::class,
            parentColumns = ["uniqueUserId"],
            childColumns = ["uniqueUserId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ModuleLocalEntity::class,
            parentColumns = ["uniqueModuleId"],
            childColumns = ["uniqueModuleId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserModulesLocalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val uniqueUserId: Int,
    val uniqueModuleId: Int,
    val isCompleted: Boolean = false
)
