package com.example.studyapp.questions.data.tables.answers_table

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AnswersDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAnswers(answers: List<AnswersLocalEntity>)

    @Query("SELECT * FROM answers WHERE uniqueQuestionId = :questionId")
    suspend fun getAnswersByQuestionId(questionId: Int): List<AnswersLocalEntity>

    @Query("SELECT * FROM answers WHERE uniqueQuestionId IN (:questionsIds)")
    suspend fun getAnswersForQuestions(questionsIds: List<Int>): List<AnswersLocalEntity>

    @Query("DELETE FROM answers")
    suspend fun deleteAllAnswers()
}