package com.example.studyapp.core.domain

import com.example.studyapp.questions.domain.repositories.SubjectRepository
import com.example.studyapp.user_profile.domain.models.User
import com.example.studyapp.user_profile.domain.repositories.BannerRepository
import com.example.studyapp.user_profile.domain.repositories.IconRepository
import com.example.studyapp.user_profile.domain.repositories.UserRepository
import kotlinx.coroutines.flow.first

sealed class GameResult {
    data class Success(
        val xpGained: Int,
        val newLevel: Int,
        val levelUp: Boolean,
        val previousLevel: Int = 0,
        val previousExp: Int = 0,
        val studyPointsGained: Int = 0
    ) : GameResult()
    data class Error(val message: String) : GameResult()
}

class GameEngine(
    private val userRepository: UserRepository,
    private val subjectRepository: SubjectRepository,
    private val bannerRepository: BannerRepository,
    private val iconRepository: IconRepository
) {
    suspend fun processQuizResults(
        userId: Int,
        subjectId: Int,
        totalQuestions: Int,
        correctAnswers: Int,
        failedQuestionIds: List<Int>,
        correctQuestionIds: List<Int>,
        isErrorTest: Boolean
    ): GameResult {
        val user = userRepository.getUserById(userId) ?: return GameResult.Error("User not found")
        
        // 1. Mark failures / Clear successes
        failedQuestionIds.forEach { qId ->
            subjectRepository.markQuestionAsFailed(userId, qId)
        }
        
        if (isErrorTest) {
            correctQuestionIds.forEach { qId ->
                subjectRepository.clearQuestionFailedStatus(userId, qId)
            }
        }

        // 2. Calculate XP (100xp per question + 100xp per correct + 500xp per quiz)
        val xpGained = totalQuestions * 100 + correctAnswers * 100 + 500
        
        // 2. Calculate Basic Study Points (1 per question + 1 per correct + 5 per quiz)
        var studyPointsGained = totalQuestions + correctAnswers + 5
        
        // 3. Increment tests for the subject
        subjectRepository.incrementTestsCompleted(userId, subjectId)
        
        val isPassed = totalQuestions > 0 && (correctAnswers.toDouble() / totalQuestions >= 0.5)
        if (isPassed) {
            subjectRepository.incrementTestsPassed(userId, subjectId)
        }
        
        val currentSubjectTests = subjectRepository.getTestsCompleted(userId, subjectId)
        
        // 4. Check for banner unlocks
        val banners = bannerRepository.getUserBanners(userId).first()
        
        banners.filter { !it.isObtained }.forEach { banner ->
            val targetSubjectId = banner.courseId ?: subjectId
            val testsPassedInSubject = subjectRepository.getTestsPassed(userId, targetSubjectId)
            val isCorrectSubject = banner.courseId == null || banner.courseId == subjectId
            
            if (isCorrectSubject && testsPassedInSubject >= banner.bannerPoints) {
                bannerRepository.unlockBanner(userId, banner.id, banner.courseId)
                studyPointsGained += (banner.bannerPoints * 100)
            }
        }
        
        // 5. Check for subject completion (250 tests)
        var newlyCompletedCourse = false
        if (currentSubjectTests >= 250) {
            val newlyCompleted = subjectRepository.markCourseAsCompleted(userId, subjectId)
            if (newlyCompleted) {
                studyPointsGained += 50000
                newlyCompletedCourse = true
            }
        }

        // 6. Update User Level and Exp
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
        
        if (currentLevel >= 999) currentLevel = 999

        // 7. Final Update
        val updatedUser = user.copy(
            level = currentLevel,
            experience = currentExp,
            studyPoints = user.studyPoints + studyPointsGained,
            questionsAnswered = user.questionsAnswered + totalQuestions,
            correctAnswers = user.correctAnswers + correctAnswers,
            completedTests = user.completedTests + 1,
            completedCourses = user.completedCourses + (if (newlyCompletedCourse) 1 else 0)
        )
        
        userRepository.updateUser(updatedUser)

        // 8. Check for icon unlocks
        checkIconUnlocks(userId, updatedUser)
        
        return GameResult.Success(
            xpGained = xpGained,
            newLevel = currentLevel,
            levelUp = levelsGained > 0,
            previousLevel = user.level,
            previousExp = user.experience,
            studyPointsGained = studyPointsGained
        )
    }

    private suspend fun checkIconUnlocks(userId: Int, user: User) {
        val icons = iconRepository.getAllIcons(userId).first()
        icons.filter { !it.isUnlocked }.forEach { icon ->
            val shouldUnlock = when (icon.imageUrl) {
                "icono_nivel_maximo" -> user.level >= 999
                "icono_nivel_500" -> user.level >= 500
                "icono_1000_tests_completados" -> user.completedTests >= 1000
                "icono_2500_tests_completados" -> user.completedTests >= 2500
                "icono_5000_tests_completados" -> user.completedTests >= 5000
                else -> false
            }
            if (shouldUnlock) {
                iconRepository.unlockIcon(userId, icon.id)
            }
        }
    }
}
