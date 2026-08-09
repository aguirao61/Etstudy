package com.example.studyapp.questions.data.tables.questions_table

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface QuestionsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateQuestions(questions: List<QuestionLocalEntity>)

    @Upsert
    suspend fun upsertQuestions(questions: List<QuestionLocalEntity>)

    @Delete
    suspend fun deleteQuestions(questions: List<QuestionLocalEntity>)

    @Query("SELECT * FROM questions")
    suspend fun getAllQuestions(): List<QuestionLocalEntity>

    @Query("""
        SELECT COUNT(*) 
        FROM questions q
        JOIN relation_modules_and_questions r ON q.uniqueQuestionId = r.uniqueQuestionId
        WHERE r.uniqueModuleId = :moduleId
    """)
    suspend fun getQuestionCountForModule(moduleId: Int): Int

    @Query("""
        SELECT COUNT(DISTINCT q.uniqueQuestionId)
        FROM questions q
        JOIN relation_modules_and_questions r ON q.uniqueQuestionId = r.uniqueQuestionId
        JOIN modules m ON r.uniqueModuleId = m.uniqueModuleId
        WHERE m.uniqueCourseId = :courseId
    """)
    suspend fun getQuestionCountForCourse(courseId: Int): Int

    @Query("DELETE FROM questions")
    suspend fun deleteAllQuestions()

    @Query("""
        SELECT q.* 
        FROM questions q
        JOIN relation_modules_and_questions r ON q.uniqueQuestionId = r.uniqueQuestionId
        WHERE r.uniqueModuleId = :moduleId
        ORDER BY q.uniqueQuestionId ASC
        LIMIT :limit
    """)
    suspend fun getQuestionsForModule(moduleId: Int, limit: Int): List<QuestionLocalEntity>

    @Query("""
        SELECT q.* 
        FROM questions q
        JOIN relation_modules_and_questions r ON q.uniqueQuestionId = r.uniqueQuestionId
        WHERE r.uniqueModuleId = :moduleId
        ORDER BY RANDOM()
        LIMIT :limit
    """)
    suspend fun getQuestionsForModuleRandom(moduleId: Int, limit: Int): List<QuestionLocalEntity>

    @Query("""
        SELECT DISTINCT q.*
        FROM questions q
        JOIN relation_modules_and_questions r ON q.uniqueQuestionId = r.uniqueQuestionId
        JOIN modules m ON r.uniqueModuleId = m.uniqueModuleId
        WHERE m.uniqueCourseId = :courseId
        ORDER BY q.uniqueQuestionId ASC
        LIMIT :limit
    """)
    suspend fun getQuestionsForCourse(courseId: Int, limit: Int): List<QuestionLocalEntity>

    @Query("""
        SELECT DISTINCT q.*
        FROM questions q
        JOIN relation_modules_and_questions r ON q.uniqueQuestionId = r.uniqueQuestionId
        JOIN modules m ON r.uniqueModuleId = m.uniqueModuleId
        WHERE m.uniqueCourseId = :courseId
        ORDER BY RANDOM()
        LIMIT :limit
    """)
    suspend fun getQuestionsForCourseRandom(courseId: Int, limit: Int): List<QuestionLocalEntity>

    @Query("""
        SELECT q.* 
        FROM questions q
        JOIN failed_questions f ON q.uniqueQuestionId = f.uniqueQuestionId
        JOIN relation_modules_and_questions r ON q.uniqueQuestionId = r.uniqueQuestionId
        WHERE f.uniqueUserId = :userId AND f.isFailed = 1 AND r.uniqueModuleId = :moduleId
        ORDER BY q.uniqueQuestionId ASC
        LIMIT :limit
    """)
    suspend fun getFailedQuestionsForModule(userId: Int, moduleId: Int, limit: Int): List<QuestionLocalEntity>

    @Query("""
        SELECT q.* 
        FROM questions q
        JOIN failed_questions f ON q.uniqueQuestionId = f.uniqueQuestionId
        JOIN relation_modules_and_questions r ON q.uniqueQuestionId = r.uniqueQuestionId
        WHERE f.uniqueUserId = :userId AND f.isFailed = 1 AND r.uniqueModuleId = :moduleId
        ORDER BY RANDOM()
        LIMIT :limit
    """)
    suspend fun getFailedQuestionsForModuleRandom(userId: Int, moduleId: Int, limit: Int): List<QuestionLocalEntity>

    @Query("""
        SELECT DISTINCT q.*
        FROM questions q
        JOIN failed_questions f ON q.uniqueQuestionId = f.uniqueQuestionId
        JOIN relation_modules_and_questions r ON q.uniqueQuestionId = r.uniqueQuestionId
        JOIN modules m ON r.uniqueModuleId = m.uniqueModuleId
        WHERE f.uniqueUserId = :userId AND f.isFailed = 1 AND m.uniqueCourseId = :courseId
        ORDER BY q.uniqueQuestionId ASC
        LIMIT :limit
    """)
    suspend fun getFailedQuestionsForCourse(userId: Int, courseId: Int, limit: Int): List<QuestionLocalEntity>

    @Query("""
        SELECT DISTINCT q.*
        FROM questions q
        JOIN failed_questions f ON q.uniqueQuestionId = f.uniqueQuestionId
        JOIN relation_modules_and_questions r ON q.uniqueQuestionId = r.uniqueQuestionId
        JOIN modules m ON r.uniqueModuleId = m.uniqueModuleId
        WHERE f.uniqueUserId = :userId AND f.isFailed = 1 AND m.uniqueCourseId = :courseId
        ORDER BY RANDOM()
        LIMIT :limit
    """)
    suspend fun getFailedQuestionsForCourseRandom(userId: Int, courseId: Int, limit: Int): List<QuestionLocalEntity>
}
