package com.example.studyapp.questions.data.tables.user_modules_table

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserModulesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUserModule(userModule: UserModulesLocalEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUserModulesIgnore(userModules: List<UserModulesLocalEntity>)

    @Query("SELECT * FROM user_modules WHERE uniqueUserId = :userId AND uniqueModuleId = :moduleId LIMIT 1")
    suspend fun getUserModule(userId: Int, moduleId: Int): UserModulesLocalEntity?

    @Query("""
        SELECT m.*, um.isCompleted
        FROM modules m
        LEFT JOIN user_modules um ON m.uniqueModuleId = um.uniqueModuleId AND um.uniqueUserId = :userId
        WHERE m.uniqueCourseId = :courseId
        GROUP BY m.uniqueModuleId
        ORDER BY m.moduleNumber ASC
    """)
    fun getModulesWithProgress(userId: Int, courseId: Int): Flow<List<ModuleWithProgress>>
}

data class ModuleWithProgress(
    val uniqueModuleId: Int,
    val uniqueCourseId: Int,
    val moduleName: String,
    val moduleNumber: Int,
    val isCompleted: Boolean?
)
