package com.example.studyapp.questions.data.tables.relationship_tables

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RelationModuleQuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateRelations(relations: List<RelationModuleQuestionLocalEntity>)

    @Query("DELETE FROM relation_modules_and_questions")
    suspend fun deleteAllRelations()
}