package com.example.studyapp.user_profile.domain.use_cases

import com.example.studyapp.core.domain.ExperienceCalculator
import com.example.studyapp.user_profile.domain.models.User
import com.example.studyapp.user_profile.domain.repositories.UserRepository

sealed class ExperienceSource {
    data class QuizCompleted(
        val totalQuestions: Int,
        val correctAnswers: Int
    ) : ExperienceSource()
}

sealed class ExperienceResult {
    data class Success(
        val xpGained: Int,
        val newLevel: Int,
        val levelUp: Boolean,
        val previousLevel: Int = 0,
        val previousExp: Int = 0,
        val studyPointsGained: Int = 0
    ) : ExperienceResult()
    data class Error(val message: String) : ExperienceResult()
}

class AddExperienceUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: Int, source: ExperienceSource): ExperienceResult {
        val user = userRepository.getUserById(userId) ?: return ExperienceResult.Error("User not found")
        
        val xpGained = when (source) {
            is ExperienceSource.QuizCompleted -> {
                val questionsExp = source.totalQuestions * 1
                val correctExp = source.correctAnswers * 1
                val testExp = 5
                questionsExp + correctExp + testExp
            }
        }
        
        var currentLevel = user.level
        var currentExp = user.experience + xpGained
        var levelsGained = 0
        
        while (currentLevel < 999) {
            val maxExp = ExperienceCalculator.calculateMaxExp(currentLevel)
            if (currentExp >= maxExp) {
                currentExp -= maxExp
                currentLevel++
                levelsGained++
            } else {
                break
            }
        }
        
        // Clamping level to max 999
        if (currentLevel >= 999) {
            currentLevel = 999
        }

        // Update user stats
        val previousLevel = user.level
        val previousExp = user.experience

        val updatedUser = when (source) {
            is ExperienceSource.QuizCompleted -> {
                user.copy(
                    level = currentLevel,
                    experience = currentExp,
                    questionsAnswered = user.questionsAnswered + source.totalQuestions,
                    correctAnswers = user.correctAnswers + source.correctAnswers,
                    completedTests = user.completedTests + 1
                )
            }
        }
        
        userRepository.updateUser(updatedUser)
        
        return ExperienceResult.Success(
            xpGained = xpGained,
            newLevel = currentLevel,
            levelUp = levelsGained > 0,
            previousLevel = previousLevel,
            previousExp = previousExp
        )
    }
}
