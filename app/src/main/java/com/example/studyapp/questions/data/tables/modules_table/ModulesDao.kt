package com.example.studyapp.questions.data.tables.modules_table

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ModulesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateModules(modules: List<ModuleLocalEntity>): List<Long>

    @Upsert
    suspend fun upsertModules(modules: List<ModuleLocalEntity>)

    @Delete
    suspend fun deleteModules(modules: List<ModuleLocalEntity>)

    @Query("SELECT * FROM modules")
    suspend fun getAllModules(): List<ModuleLocalEntity>

    @Query("SELECT uniqueModuleId FROM modules WHERE uniqueCourseId = :courseId AND moduleName = :name LIMIT 1")
    suspend fun getModuleIdByName(courseId: Int, name: String): Int?

    @Query("SELECT * FROM modules WHERE uniqueCourseId = :courseId ORDER BY moduleNumber ASC")
    fun getModulesByCourse(courseId: Int): Flow<List<ModuleLocalEntity>>

    @Query("DELETE FROM modules")
    suspend fun deleteAllModules()
}