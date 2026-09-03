package com.example.studyapp.core.domain

import com.example.studyapp.questions.domain.repositories.SubjectRepository
import com.example.studyapp.user_profile.domain.models.User
import com.example.studyapp.user_profile.domain.models.DailyMission
import com.example.studyapp.user_profile.domain.repositories.BannerRepository
import com.example.studyapp.user_profile.domain.repositories.DailyMissionRepository
import com.example.studyapp.user_profile.domain.repositories.IconRepository
import com.example.studyapp.user_profile.domain.repositories.UserRepository
import kotlinx.coroutines.flow.first
import java.util.Calendar

sealed class GameResult {
    data class Success(
        val xpGained: Int,
        val newLevel: Int,
        val levelUp: Boolean,
        val previousLevel: Int = 0,
        val previousExp: Int = 0,
        val studyPointsGained: Int = 0,
        val milestoneUnlocked: Boolean = false
    ) : GameResult()
    data class Error(val message: String) : GameResult()
}

class GameEngine(
    private val userRepository: UserRepository,
    private val subjectRepository: SubjectRepository,
    private val bannerRepository: BannerRepository,
    private val iconRepository: IconRepository,
    private val dailyMissionRepository: DailyMissionRepository
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
        
        // Mark failures / Clear successes
        failedQuestionIds.forEach { qId ->
            subjectRepository.markQuestionAsFailed(userId, qId)
        }
        
        if (isErrorTest) {
            correctQuestionIds.forEach { qId ->
                subjectRepository.clearQuestionFailedStatus(userId, qId)
            }
        }

        // Calculate XP (100xp per question + 100xp per correct + 500xp per quiz)
        val xpGained = totalQuestions * 100 + correctAnswers * 100 + 500
        
        // Calculate Basic Study Points (1 per question + 1 per correct + 5 per quiz)
        var studyPointsGained = totalQuestions + correctAnswers + 5
        
        // Increment tests for the subject
        subjectRepository.incrementTestsCompleted(userId, subjectId)
        
        val isPassed = totalQuestions > 0 && (correctAnswers.toDouble() / totalQuestions >= 0.5)
        if (isPassed) {
            subjectRepository.incrementTestsPassed(userId, subjectId)
        }
        
        val currentSubjectTests = subjectRepository.getTestsCompleted(userId, subjectId)
        
        // Check for banner unlocks
        var milestoneUnlocked = false
        val banners = bannerRepository.getUserBanners(userId).first()
        
        banners.filter { !it.isObtained }.forEach { banner ->
            val targetSubjectId = banner.courseId ?: subjectId
            val testsPassedInSubject = subjectRepository.getTestsPassed(userId, targetSubjectId)
            val isCorrectSubject = banner.courseId == null || banner.courseId == subjectId
            
            if (isCorrectSubject && testsPassedInSubject >= banner.bannerPoints) {
                bannerRepository.unlockBanner(userId, banner.id, banner.courseId)
                studyPointsGained += (banner.bannerPoints * 100)
                milestoneUnlocked = true
            }
        }
        
        // Check for subject completion (250 tests)
        var newlyCompletedCourse = false
        if (currentSubjectTests >= 250) {
            val newlyCompleted = subjectRepository.markCourseAsCompleted(userId, subjectId)
            if (newlyCompleted) {
                studyPointsGained += 50000
                newlyCompletedCourse = true
            }
        }

        // Update User Level and Exp
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

        // Update Streak Logic
        val currentTime = System.currentTimeMillis()
        var newCurrentStreak = user.currentStreak
        var newMaxStreak = user.maxStreak
        var newLastStreakUpdate = user.lastStreakUpdate

        if (currentTime > user.lastStreakUpdate) {
            val lastCalendar = Calendar.getInstance().apply { timeInMillis = user.lastStreakUpdate }
            val currentCalendar = Calendar.getInstance().apply { timeInMillis = currentTime }

            val isSameDay = lastCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) &&
                    lastCalendar.get(Calendar.DAY_OF_YEAR) == currentCalendar.get(Calendar.DAY_OF_YEAR)

            if (!isSameDay) {
                // Check if it's the next day
                lastCalendar.add(Calendar.DAY_OF_YEAR, 1)
                val isNextDay = lastCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) &&
                        lastCalendar.get(Calendar.DAY_OF_YEAR) == currentCalendar.get(Calendar.DAY_OF_YEAR)

                if (isNextDay) {
                    newCurrentStreak++
                } else {
                    newCurrentStreak = 1
                }
                
                if (newCurrentStreak > newMaxStreak) {
                    newMaxStreak = newCurrentStreak
                }
                newLastStreakUpdate = currentTime
            }
        }

        // Final Update
        val updatedUser = user.copy(
            level = currentLevel,
            experience = currentExp,
            studyPoints = user.studyPoints + studyPointsGained,
            questionsAnswered = user.questionsAnswered + totalQuestions,
            correctAnswers = user.correctAnswers + correctAnswers,
            completedTests = user.completedTests + 1,
            completedCourses = user.completedCourses + (if (newlyCompletedCourse) 1 else 0),
            currentStreak = newCurrentStreak,
            maxStreak = newMaxStreak,
            lastStreakUpdate = newLastStreakUpdate
        )
        
        // Final Update
        var finalUpdatedUser = updatedUser
        
        // Daily Missions Logic
        val missionXpBonus = processDailyMissions(userId, subjectId, isPassed)
        if (missionXpBonus > 0) {
            // Apply extra EXP from missions
            var currentLevelM = finalUpdatedUser.level
            var currentExpM = finalUpdatedUser.experience + missionXpBonus
            
            while (currentLevelM < 999) {
                val maxExp = ExperienceCalculator.calculateMaxExp(currentLevelM)
                if (currentExpM >= maxExp) {
                    currentExpM -= maxExp
                    currentLevelM++
                    levelsGained++
                } else {
                    break
                }
            }
            if (currentLevelM >= 999) currentLevelM = 999
            
            finalUpdatedUser = finalUpdatedUser.copy(
                level = currentLevelM,
                experience = currentExpM
            )
        }

        userRepository.updateUser(finalUpdatedUser)

        // Check for icon unlocks
        val iconsUnlocked = checkIconUnlocks(userId, finalUpdatedUser)
        if (iconsUnlocked) milestoneUnlocked = true
        
        return GameResult.Success(
            xpGained = xpGained + missionXpBonus,
            newLevel = finalUpdatedUser.level,
            levelUp = levelsGained > 0,
            previousLevel = user.level,
            previousExp = user.experience,
            studyPointsGained = studyPointsGained,
            milestoneUnlocked = milestoneUnlocked
        )
    }

    private suspend fun processDailyMissions(userId: Int, subjectId: Int, isPassed: Boolean): Int {
        var mission = dailyMissionRepository.getDailyMission(userId)
        val currentTime = System.currentTimeMillis()
        
        if (mission == null) {
            dailyMissionRepository.createDailyMission(userId)
            mission = dailyMissionRepository.getDailyMission(userId)!!
        }

        // Safeguard: Ignore if system time is before last update
        if (currentTime <= mission.lastUpdate && mission.lastUpdate != 0L) {
            return 0
        }

        // Check if it's a new day
        val lastCalendar = Calendar.getInstance().apply { timeInMillis = mission.lastUpdate }
        val currentCalendar = Calendar.getInstance().apply { timeInMillis = currentTime }
        val isNewDay = lastCalendar.get(Calendar.YEAR) != currentCalendar.get(Calendar.YEAR) ||
                lastCalendar.get(Calendar.DAY_OF_YEAR) != currentCalendar.get(Calendar.DAY_OF_YEAR)

        var quizzesCompleted = if (isNewDay) 0 else mission.quizzesCompleted
        var quizzesPassed = if (isNewDay) 0 else mission.quizzesPassed
        val subjectsStudiedSet = if (isNewDay) mutableSetOf<Int>() else mission.subjectsStudied.toMutableSet()
        var claimedMask = if (isNewDay) 0 else mission.claimedMask

        quizzesCompleted++
        if (isPassed) quizzesPassed++
        subjectsStudiedSet.add(subjectId)

        var xpBonus = 0

        // Mission 1: 5 quizzes -> 1000 EXP (Bit 1)
        if (quizzesCompleted >= 5 && (claimedMask and 1) == 0) {
            xpBonus += 1000
            claimedMask = claimedMask or 1
        }

        // Mission 2: 2 passed -> 1000 EXP (Bit 2)
        if (quizzesPassed >= 2 && (claimedMask and 2) == 0) {
            xpBonus += 1000
            claimedMask = claimedMask or 2
        }

        // Mission 3: 2 subjects -> 1000 EXP (Bit 4)
        if (subjectsStudiedSet.size >= 2 && (claimedMask and 4) == 0) {
            xpBonus += 1000
            claimedMask = claimedMask or 4
        }

        // Mission 4: All bonus -> 2000 EXP (Bit 8)
        if ((claimedMask and 7) == 7 && (claimedMask and 8) == 0) {
            xpBonus += 2000
            claimedMask = claimedMask or 8
        }

        dailyMissionRepository.updateDailyMission(
            mission.copy(
                lastUpdate = currentTime,
                quizzesCompleted = quizzesCompleted,
                quizzesPassed = quizzesPassed,
                subjectsStudied = subjectsStudiedSet.toList(),
                claimedMask = claimedMask
            )
        )

        return xpBonus
    }

    private suspend fun checkIconUnlocks(userId: Int, user: User): Boolean {
        var anyUnlocked = false
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
                anyUnlocked = true
            }
        }
        return anyUnlocked
    }
}
