package com.example.studyapp.questions.data.mappers

import com.example.studyapp.questions.data.tables.answers_table.AnswersLocalEntity
import com.example.studyapp.questions.data.tables.modules_table.ModuleLocalEntity
import com.example.studyapp.questions.data.tables.questions_table.QuestionLocalEntity
import com.example.studyapp.questions.data.tables.user_courses_table.UserCourseDisplay
import com.example.studyapp.questions.data.tables.user_modules_table.ModuleWithProgress
import com.example.studyapp.questions.domain.models.Module
import com.example.studyapp.questions.domain.models.Question
import com.example.studyapp.questions.domain.models.Subject

fun UserCourseDisplay.toDomainModel(): Subject {
    return Subject(
        id = uniqueCourseId,
        name = courseName,
        code = courseCode,
        isFavorite = isFavorite ?: false,
        failedQuestionsCount = failedQuestionsCount ?: 0,
        testsPassed = testsPassed ?: 0
    )
}

fun ModuleLocalEntity.toDomainModel(): Module {
    return Module(
        id = uniqueModuleId,
        courseId = uniqueCourseId,
        name = moduleName,
        number = moduleNumber
    )
}

fun ModuleWithProgress.toDomainModel(): Module {
    return Module(
        id = uniqueModuleId,
        courseId = uniqueCourseId,
        name = moduleName,
        number = moduleNumber
    )
}

fun QuestionLocalEntity.toDomainModel(answers: List<AnswersLocalEntity>): Question {
    val correctIndices = answers.mapIndexedNotNull { index, ans ->
        if (ans.isCorrect) index else null
    }
    return Question(
        id = uniqueQuestionId,
        text = questionText,
        options = answers.map { it.answerText },
        correctAnswerIndices = correctIndices
    )
}
