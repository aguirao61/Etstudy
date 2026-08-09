package com.example.studyapp.questions.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Attempt(
    val totalQuestions: Int,
    val score: Int,
    val correctCount: Int,
    val incorrectCount: Int,
    val blankCount: Int,
    val xpGained: Int,
    val studyPointsGained: Int,
    val previousLevel: Int = 0,
    val newLevel: Int = 0,
    val questions: List<AttemptQuestion>
) : Parcelable

@Parcelize
data class AttemptQuestion(
    val questionId: Int,
    val questionText: String,
    val userAnswers: Set<Int>,
    val correctAnswers: List<Int>,
    val options: List<String>,
    val isCorrect: Boolean,
    val points: Int
) : Parcelable
