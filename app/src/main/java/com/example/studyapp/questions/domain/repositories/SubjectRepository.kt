package com.example.studyapp.questions.domain.repositories

import com.example.studyapp.questions.domain.models.Module
import com.example.studyapp.questions.domain.models.Question
import com.example.studyapp.questions.domain.models.Subject
import kotlinx.coroutines.flow.Flow

interface SubjectRepository {
    fun getSubjects(userId: Int): Flow<List<Subject>>
    fun getModules(userId: Int, subjectId: Int): Flow<List<Module>>
    suspend fun getQuestionCountForModule(moduleId: Int): Int
    suspend fun getQuestionCountForCourse(courseId: Int): Int
    suspend fun toggleFavorite(userId: Int, subjectId: Int)

    suspend fun getQuestionsForCourse(courseId: Int, limit: Int, isRandom: Boolean): List<Question>
    suspend fun getQuestionsForModule(moduleId: Int, limit: Int, isRandom: Boolean): List<Question>

    suspend fun getFailedQuestionCountForModule(userId: Int, moduleId: Int): Int
    suspend fun getFailedQuestionCountForCourse(userId: Int, courseId: Int): Int
    suspend fun getFailedQuestionsForCourse(userId: Int, courseId: Int, limit: Int, isRandom: Boolean): List<Question>
    suspend fun getFailedQuestionsForModule(userId: Int, moduleId: Int, limit: Int, isRandom: Boolean): List<Question>

    suspend fun markQuestionAsFailed(userId: Int, questionId: Int)
    suspend fun clearQuestionFailedStatus(userId: Int, questionId: Int)

    suspend fun incrementTestsCompleted(userId: Int, subjectId: Int)
    suspend fun incrementTestsPassed(userId: Int, subjectId: Int)
    suspend fun getTestsCompleted(userId: Int, subjectId: Int): Int
    suspend fun getTestsPassed(userId: Int, subjectId: Int): Int
    suspend fun markCourseAsCompleted(userId: Int, subjectId: Int): Boolean
}
