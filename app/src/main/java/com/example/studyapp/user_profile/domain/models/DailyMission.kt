package com.example.studyapp.user_profile.domain.models

data class DailyMission(
    val userId: Int,
    val lastUpdate: Long,
    val quizzesCompleted: Int,
    val quizzesPassed: Int,
    val subjectsStudied: List<Int>,
    val claimedMask: Int
) {
    val is5QuizzesClaimed: Boolean get() = (claimedMask and 1) != 0
    val is2PassedClaimed: Boolean get() = (claimedMask and 2) != 0
    val is2SubjectsClaimed: Boolean get() = (claimedMask and 4) != 0
    val isAllBonusClaimed: Boolean get() = (claimedMask and 8) != 0
    
    val quizzesCompletedProgress: Float get() = (quizzesCompleted.toFloat() / 5f).coerceAtMost(1f)
    val quizzesPassedProgress: Float get() = (quizzesPassed.toFloat() / 2f).coerceAtMost(1f)
    val subjectsStudiedProgress: Float get() = (subjectsStudied.size.toFloat() / 2f).coerceAtMost(1f)
    
    val allCompleted: Boolean get() = quizzesCompleted >= 5 && quizzesPassed >= 2 && subjectsStudied.size >= 2
}
