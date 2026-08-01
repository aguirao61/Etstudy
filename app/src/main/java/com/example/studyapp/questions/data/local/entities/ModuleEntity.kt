package com.example.studyapp.questions.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

// This table represents a module, with its associated course
@Entity(tableName = "modules")
data class ModuleEntity(
    @PrimaryKey(autoGenerate = true) val moduleId: Int = 0,
    val moduleName: String,
    val courseId: Int
)
