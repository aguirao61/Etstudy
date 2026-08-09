package com.example.studyapp.user_profile.domain.use_cases

import com.example.studyapp.core.domain.ExperienceCalculator
import com.example.studyapp.questions.domain.repositories.SubjectRepository
import com.example.studyapp.user_profile.domain.models.User
import com.example.studyapp.user_profile.domain.repositories.BannerRepository
import com.example.studyapp.user_profile.domain.repositories.UserRepository
import kotlinx.coroutines.flow.first

class ProcessQuizResultsUseCase(
    private val userRepository: UserRepository,
    private val subjectRepository: SubjectRepository,
    private val bannerRepository: BannerRepository
) {
    suspend operator fun invoke(
        userId: Int,
        subjectId: Int,
        totalQuestions: Int,
        correctAnswers: Int,
        failedQuestionIds: List<Int>,
        correctQuestionIds: List<Int>,
        isErrorTest: Boolean
    ): ExperienceResult {
        val user = userRepository.getUserById(userId) ?: return ExperienceResult.Error("User not found")
        
        // 1. Mark failures / Clear successes
        failedQuestionIds.forEach { qId ->
            subjectRepository.markQuestionAsFailed(userId, qId)
        }
        
        if (isErrorTest) {
            correctQuestionIds.forEach { qId ->
                subjectRepository.clearQuestionFailedStatus(userId, qId)
            }
        }

        // 2. Calculate XP (1xp per question + 1xp per correct + 5xp per quiz)
        val xpGained = totalQuestions*100 + correctAnswers*100 + 500
        
        // 2. Calculate Basic Study Points (same as XP)
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
            // Check requirement based on the banner's specific subject if defined, 
            // otherwise use current subject (default behavior for legacy or global banners)
            val targetSubjectId = banner.courseId ?: subjectId
            val testsPassedInSubject = subjectRepository.getTestsPassed(userId, targetSubjectId)

            // Requirement is based on APROBADOS (Passed Tests)
            // AND we only unlock it if it's the current subject OR if it's a generic banner
            val isCorrectSubject = banner.courseId == null || banner.courseId == subjectId
            
            if (isCorrectSubject && testsPassedInSubject >= banner.bannerPoints) {
                bannerRepository.unlockBanner(userId, banner.id, banner.courseId)
                studyPointsGained += (banner.bannerPoints * 10)
            }
        }
        
        // 5. Check for subject completion (250 tests) -> 5000 points
        var newlyCompletedCourse = false
        if (currentSubjectTests >= 250) {
            val newlyCompleted = subjectRepository.markCourseAsCompleted(userId, subjectId)
            if (newlyCompleted) {
                studyPointsGained += 5000
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
        
        return ExperienceResult.Success(
            xpGained = xpGained,
            newLevel = currentLevel,
            levelUp = levelsGained > 0,
            previousLevel = user.level,
            previousExp = user.experience,
            studyPointsGained = studyPointsGained
        )
    }
}
