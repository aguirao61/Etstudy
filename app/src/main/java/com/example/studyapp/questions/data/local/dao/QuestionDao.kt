package com.example.studyapp.questions.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.studyapp.questions.data.local.entities.QuestionEntity

@Dao
interface QuestionDao {

    // Get all questions
    @Query("SELECT question, questionImage FROM questions")
    suspend fun getAllQuestions(): List<QuestionEntity>

    // Get one specific question
    @Query("SELECT question, questionImage FROM questions WHERE questionId = :questionId")
    suspend fun getQuestion(questionId: Int): QuestionEntity

    // Get question data with question id
    @Query("SELECT timesAppeared, timesFailed FROM questions WHERE questionId = :questionId")
    suspend fun getQuestionData(questionId: Int): QuestionEntity
}