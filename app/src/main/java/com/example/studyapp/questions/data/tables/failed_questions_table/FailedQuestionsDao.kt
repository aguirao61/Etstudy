package com.example.studyapp.questions.data.tables.failed_questions_table

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface FailedQuestionsDao {
    @Query("SELECT * FROM failed_questions WHERE uniqueUserId = :userId AND uniqueQuestionId = :questionId LIMIT 1")
    suspend fun getFailedQuestion(userId: Int, questionId: Int): FailedQuestionsLocalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(failedQuestion: FailedQuestionsLocalEntity)

    @Transaction
    suspend fun markAsFailed(userId: Int, questionId: Int) {
        val existing = getFailedQuestion(userId, questionId)
        if (existing != null) {
            insertOrUpdate(existing.copy(
                failedQuestionCount = existing.failedQuestionCount + 1,
                isFailed = true
            ))
        } else {
            insertOrUpdate(FailedQuestionsLocalEntity(
                uniqueUserId = userId,
                uniqueQuestionId = questionId,
                failedQuestionCount = 1,
                isFailed = true
            ))
        }
    }

    @Query("UPDATE failed_questions SET isFailed = 0 WHERE uniqueUserId = :userId AND uniqueQuestionId = :questionId")
    suspend fun clearFailedStatus(userId: Int, questionId: Int)

    @Query("""
        SELECT COUNT(DISTINCT q.uniqueQuestionId) 
        FROM questions q
        JOIN failed_questions f ON q.uniqueQuestionId = f.uniqueQuestionId
        JOIN relation_modules_and_questions r ON q.uniqueQuestionId = r.uniqueQuestionId
        WHERE f.uniqueUserId = :userId AND f.isFailed = 1 AND r.uniqueModuleId = :moduleId
    """)
    suspend fun getFailedQuestionCountForModule(userId: Int, moduleId: Int): Int

    @Query("""
        SELECT COUNT(DISTINCT q.uniqueQuestionId) 
        FROM questions q
        JOIN failed_questions f ON q.uniqueQuestionId = f.uniqueQuestionId
        JOIN relation_modules_and_questions r ON q.uniqueQuestionId = r.uniqueQuestionId
        JOIN modules m ON r.uniqueModuleId = m.uniqueModuleId
        WHERE f.uniqueUserId = :userId AND f.isFailed = 1 AND m.uniqueCourseId = :courseId
    """)
    suspend fun getFailedQuestionCountForCourse(userId: Int, courseId: Int): Int
}
