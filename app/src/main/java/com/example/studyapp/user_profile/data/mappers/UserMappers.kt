package com.example.studyapp.user_profile.data.mappers

import com.example.studyapp.user_profile.data.tables.banner_table.BannerLocalEntity
import com.example.studyapp.user_profile.data.tables.icon_table.IconLocalEntity
import com.example.studyapp.user_profile.data.tables.user_banner_table.UserBannerDisplay
import com.example.studyapp.user_profile.data.tables.user_icon_table.UserIconDisplay
import com.example.studyapp.user_profile.data.tables.user_table.UserLocalEntity
import com.example.studyapp.user_profile.domain.models.Banner
import com.example.studyapp.user_profile.domain.models.Icon
import com.example.studyapp.user_profile.domain.models.ThemeMode
import com.example.studyapp.user_profile.domain.models.User

fun UserLocalEntity.toDomainModel(
    iconImage: String? = null,
    bannerEntity: BannerLocalEntity? = null,
    dynamicCompletedCourses: Int? = null
): User {
    return User(
        uniqueUserId = uniqueUserId,
        username = username,
        level = level,
        experience = experience,
        userSongName = userSongName,
        userSongArtist = userSongArtist,
        studyPoints = studyPoints,
        questionsAnswered = questionsAnswered,
        correctAnswers = correctAnswers,
        completedTests = completedTests,
        completedCourses = dynamicCompletedCourses ?: completedCourses,
        currentStreak = currentStreak,
        maxStreak = maxStreak,
        lastStreakUpdate = lastStreakUpdate,
        equippedIconId = equippedIconId,
        equippedBannerId = equippedBannerId,
        equippedIconUrl = iconImage,
        equippedBannerColour = bannerEntity?.bannerColour,
        equippedBannerTextColour = bannerEntity?.bannerTextColour,
        equippedBannerIcon = bannerEntity?.bannerIcon,
        equippedBannerContent = bannerEntity?.bannerContent,
        selectedTheme = try { ThemeMode.valueOf(selectedTheme) } catch (e: Exception) { ThemeMode.SYSTEM }
    )
}

fun User.toLocalEntity(
    equippedIconId: Int? = null,
    equippedBannerId: Int? = null
): UserLocalEntity {
    return UserLocalEntity(
        uniqueUserId = uniqueUserId,
        username = username,
        level = level,
        experience = experience,
        userSongName = userSongName,
        userSongArtist = userSongArtist,
        studyPoints = studyPoints,
        questionsAnswered = questionsAnswered,
        correctAnswers = correctAnswers,
        completedTests = completedTests,
        completedCourses = completedCourses,
        currentStreak = currentStreak,
        maxStreak = maxStreak,
        lastStreakUpdate = lastStreakUpdate,
        equippedIconId = equippedIconId ?: this.equippedIconId,
        equippedBannerId = equippedBannerId ?: this.equippedBannerId,
        selectedTheme = selectedTheme.name
    )
}

fun UserBannerDisplay.toDomainModel() = Banner(
    id = uniqueBannerId,
    bannerContent = bannerContent,
    bannerColour = bannerColour,
    bannerTextColour = bannerTextColour,
    bannerIcon = bannerIcon,
    bannerSubject = bannerSubject,
    bannerPoints = bannerPoints,
    courseId = uniqueCourseId,
    isObtained = isBannerObtained ?: false
)

fun IconLocalEntity.toDomainModel() = Icon(
    id = uniqueIconId,
    name = iconName,
    description = iconDescription,
    imageUrl = iconImage
)

fun UserIconDisplay.toDomainModel() = Icon(
    id = uniqueIconId,
    name = iconName,
    description = iconDescription,
    imageUrl = iconImage,
    isUnlocked = isUnlocked
)
