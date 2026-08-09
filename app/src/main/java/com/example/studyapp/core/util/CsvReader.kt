package com.example.studyapp.core.util

import android.content.Context
import com.example.studyapp.user_profile.data.tables.banner_table.BannerLocalEntity
import com.example.studyapp.questions.data.tables.courses_table.CourseLocalEntity
import com.example.studyapp.questions.data.tables.questions_table.QuestionLocalEntity
import com.example.studyapp.questions.data.tables.answers_table.AnswersLocalEntity
import com.example.studyapp.questions.data.tables.relationship_tables.RelationModuleQuestionLocalEntity
import java.io.BufferedReader
import java.io.InputStreamReader

class CsvReader(private val context: Context) {

    /**
     * Reads banners from tfg_mock_data_banners.csv
     */
    fun readBanners(): List<BannerLocalEntity> {
        return readFromAssets("tfg_mock_data_banners.csv") { map ->
            BannerLocalEntity(
                bannerContent = map["bannerContent"] ?: "",
                bannerSubject = map["bannerCourse"] ?: "",
                bannerPoints = map["bannerPoints"]?.toIntOrNull() ?: 0,
                bannerColour = map["bannerColour"]?.parseColor() ?: 0L,
                bannerTextColour = map["bannerTextColour"]?.parseColor() ?: 0L,
                bannerIcon = map["bannerIcon"] ?: ""
            )
        }
    }

    /**
     * Reads courses from tfg_mock_data_courses.csv
     */
    fun readCourses(): List<CourseLocalEntity> {
        return readFromAssets("tfg_mock_data_courses.csv") { map ->
            CourseLocalEntity(
                courseName = map["asignatura"] ?: "",
                courseCode = map["codigo"] ?: ""
            )
        }
    }

    /**
     * Reads modules from tfg_mock_data_modules.csv
     */
    fun readModules(): List<ModuleCsvRow> {
        return readFromAssets("tfg_mock_data_modules.csv") { map ->
            ModuleCsvRow(
                courseCode = map["codigoAsignatura"] ?: "",
                moduleName = map["modulo"] ?: "",
                moduleNumber = map["number"]?.toIntOrNull() ?: 0
            )
        }
    }

    /**
     * Reads questions from tfg_mock_data_questions.csv
     */
    fun readQuestions(): List<QuestionLocalEntity> {
        return readFromAssets("tfg_mock_data_questions.csv") { map ->
            QuestionLocalEntity(
                uniqueQuestionId = map["questionId"]?.toIntOrNull() ?: 0,
                questionText = map["questionText"] ?: ""
            )
        }
    }

    /**
     * Reads answers from tfg_mock_data_answers.csv
     */
    fun readAnswers(): List<AnswersLocalEntity> {
        return readFromAssets("tfg_mock_data_answers.csv") { map ->
            AnswersLocalEntity(
                uniqueQuestionId = map["uniqueQuestionId"]?.toIntOrNull() ?: 0,
                answerText = map["answerText"] ?: "",
                isCorrect = map["isCorrect"]?.equals("TRUE", ignoreCase = true) ?: false
            )
        }
    }

    /**
     * Reads relations from tfg_mock_data_relationship_questions_modules.csv
     */
    fun readQuestionModuleRelations(): List<RelationModuleQuestionLocalEntity> {
        return readFromAssets("tfg_mock_data_relationship_questions_modules.csv") { map ->
            RelationModuleQuestionLocalEntity(
                uniqueModuleId = map["uniqueModuleId"]?.toIntOrNull() ?: 0,
                uniqueQuestionId = map["uniqueQuestionId"]?.toIntOrNull() ?: 0
            )
        }
    }

    private fun <T> readFromAssets(fileName: String, mapper: (Map<String, String>) -> T): List<T> {
        return try {
            val inputStream = context.assets.open(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val headerLine = reader.readLine() ?: return emptyList()
            val header = headerLine.split(",").map { it.trim().removePrefix("\uFEFF") }
            
            reader.lineSequence()
                .filter { it.isNotBlank() }
                .map { line ->
                    val values = parseCsvLine(line)
                    header.zip(values).toMap()
                }
                .map(mapper)
                .toList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        val current = StringBuilder()
        var inQuotes = false
        
        var i = 0
        while (i < line.length) {
            val char = line[i]
            when (char) {
                '\"' -> {
                    if (inQuotes && i + 1 < line.length && line[i + 1] == '\"') {
                        // Double quotes inside quotes -> escaped quote
                        current.append('\"')
                        i++
                    } else {
                        inQuotes = !inQuotes
                    }
                }
                ',' -> {
                    if (inQuotes) {
                        current.append(char)
                    } else {
                        result.add(current.toString().trim())
                        current.setLength(0)
                    }
                }
                else -> current.append(char)
            }
            i++
        }
        result.add(current.toString().trim())
        return result
    }

    private fun String.parseColor(): Long {
        return try {
            if (startsWith("0x")) {
                substring(2).toLong(16)
            } else if (startsWith("#")) {
                substring(1).toLong(16)
            } else {
                toLong()
            }
        } catch (e: Exception) {
            0L
        }
    }
}

/**
 * Data class representing a row in tfg_mock_data_modules.csv
 */
data class ModuleCsvRow(
    val courseCode: String,
    val moduleName: String,
    val moduleNumber: Int
)
