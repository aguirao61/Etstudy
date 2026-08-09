package com.example.studyapp.questions.data.repository

import com.example.studyapp.questions.data.mappers.toDomainModel
import com.example.studyapp.questions.data.tables.answers_table.AnswersDao
import com.example.studyapp.questions.data.tables.courses_table.CoursesDao
import com.example.studyapp.questions.data.tables.failed_questions_table.FailedQuestionsDao
import com.example.studyapp.questions.data.tables.modules_table.ModulesDao
import com.example.studyapp.questions.data.tables.questions_table.QuestionsDao
import com.example.studyapp.questions.data.tables.user_courses_table.UserCoursesDao
import com.example.studyapp.questions.data.tables.user_courses_table.UserCoursesLocalEntity
import com.example.studyapp.questions.data.tables.user_modules_table.UserModulesDao
import com.example.studyapp.questions.domain.models.Module
import com.example.studyapp.questions.domain.models.Question
import com.example.studyapp.questions.domain.models.Subject
import com.example.studyapp.questions.domain.repositories.SubjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SubjectRepositoryImpl(
    private val coursesDao: CoursesDao,
    private val modulesDao: ModulesDao,
    private val questionsDao: QuestionsDao,
    private val answersDao: AnswersDao,
    private val userCoursesDao: UserCoursesDao,
    private val userModulesDao: UserModulesDao,
    private val failedQuestionsDao: FailedQuestionsDao
) : SubjectRepository {

    override fun getSubjects(userId: Int): Flow<List<Subject>> {
        return userCoursesDao.getSubjectsWithUserStatus(userId).map { list ->
            list.map { it.toDomainModel() }
        }
    }

    override fun getModules(userId: Int, subjectId: Int): Flow<List<Module>> {
        return userModulesDao.getModulesWithProgress(userId, subjectId).map { list ->
            list.map { it.toDomainModel() }
        }
    }

    override suspend fun getQuestionCountForModule(moduleId: Int): Int {
        return questionsDao.getQuestionCountForModule(moduleId)
    }

    override suspend fun getQuestionCountForCourse(courseId: Int): Int {
        return questionsDao.getQuestionCountForCourse(courseId)
    }

    override suspend fun toggleFavorite(userId: Int, subjectId: Int) {
        val userCourse = userCoursesDao.getUserCourse(userId, subjectId)
        if (userCourse != null) {
            userCoursesDao.updateFavorite(userId, subjectId, !userCourse.isFavorite)
        } else {
            userCoursesDao.insertOrUpdateUserCourse(
                UserCoursesLocalEntity(
                    uniqueUserId = userId,
                    uniqueCourseId = subjectId,
                    isFavorite = true
                )
            )
        }
    }

    override suspend fun getQuestionsForCourse(courseId: Int, limit: Int, isRandom: Boolean): List<Question> {
        val questions = if (isRandom) {
            questionsDao.getQuestionsForCourseRandom(courseId, limit)
        } else {
            questionsDao.getQuestionsForCourse(courseId, limit)
        }
        return questions.map { q ->
            val answers = answersDao.getAnswersByQuestionId(q.uniqueQuestionId)
            q.toDomainModel(answers)
        }
    }

    override suspend fun getQuestionsForModule(moduleId: Int, limit: Int, isRandom: Boolean): List<Question> {
        val questions = if (isRandom) {
            questionsDao.getQuestionsForModuleRandom(moduleId, limit)
        } else {
            questionsDao.getQuestionsForModule(moduleId, limit)
        }
        return questions.map { q ->
            val answers = answersDao.getAnswersByQuestionId(q.uniqueQuestionId)
            q.toDomainModel(answers)
        }
    }

    override suspend fun getFailedQuestionCountForModule(userId: Int, moduleId: Int): Int {
        return failedQuestionsDao.getFailedQuestionCountForModule(userId, moduleId)
    }

    override suspend fun getFailedQuestionCountForCourse(userId: Int, courseId: Int): Int {
        return failedQuestionsDao.getFailedQuestionCountForCourse(userId, courseId)
    }

    override suspend fun getFailedQuestionsForCourse(
        userId: Int,
        courseId: Int,
        limit: Int,
        isRandom: Boolean
    ): List<Question> {
        val questions = if (isRandom) {
            questionsDao.getFailedQuestionsForCourseRandom(userId, courseId, limit)
        } else {
            questionsDao.getFailedQuestionsForCourse(userId, courseId, limit)
        }
        return questions.map { q ->
            val answers = answersDao.getAnswersByQuestionId(q.uniqueQuestionId)
            q.toDomainModel(answers)
        }
    }

    override suspend fun getFailedQuestionsForModule(
        userId: Int,
        moduleId: Int,
        limit: Int,
        isRandom: Boolean
    ): List<Question> {
        val questions = if (isRandom) {
            questionsDao.getFailedQuestionsForModuleRandom(userId, moduleId, limit)
        } else {
            questionsDao.getFailedQuestionsForModule(userId, moduleId, limit)
        }
        return questions.map { q ->
            val answers = answersDao.getAnswersByQuestionId(q.uniqueQuestionId)
            q.toDomainModel(answers)
        }
    }

    override suspend fun markQuestionAsFailed(userId: Int, questionId: Int) {
        failedQuestionsDao.markAsFailed(userId, questionId)
    }

    override suspend fun clearQuestionFailedStatus(userId: Int, questionId: Int) {
        failedQuestionsDao.clearFailedStatus(userId, questionId)
    }

    override suspend fun incrementTestsCompleted(userId: Int, subjectId: Int) {
        userCoursesDao.incrementTestsCompleted(userId, subjectId)
    }

    override suspend fun incrementTestsPassed(userId: Int, subjectId: Int) {
        userCoursesDao.incrementTestsPassed(userId, subjectId)
    }

    override suspend fun getTestsPassed(userId: Int, subjectId: Int): Int {
        return userCoursesDao.getTestsPassed(userId, subjectId) ?: 0
    }

    override suspend fun getTestsCompleted(userId: Int, subjectId: Int): Int {
        return userCoursesDao.getUserCourse(userId, subjectId)?.testsCompleted ?: 0
    }

    override suspend fun markCourseAsCompleted(userId: Int, subjectId: Int): Boolean {
        val userCourse = userCoursesDao.getUserCourse(userId, subjectId)
        return if (userCourse != null && !userCourse.isCourseCompleted) {
            userCoursesDao.insertOrUpdateUserCourse(userCourse.copy(isCourseCompleted = true))
            true
        } else false
    }
}
