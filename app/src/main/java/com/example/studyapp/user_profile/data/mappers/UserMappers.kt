package com.example.studyapp.user_profile.data.mappers

import com.example.studyapp.user_profile.data.tables.banner_table.BannerLocalEntity
import com.example.studyapp.user_profile.data.tables.user_banner_table.UserBannerDisplay
import com.example.studyapp.user_profile.data.tables.user_table.UserLocalEntity
import com.example.studyapp.user_profile.domain.models.Banner
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
        equippedIconId = equippedIconId,
        equippedBannerId = equippedBannerId,
        equippedIconUrl = iconImage,
        equippedBannerColour = bannerEntity?.bannerColour,
        equippedBannerTextColour = bannerEntity?.bannerTextColour,
        equippedBannerIcon = bannerEntity?.bannerIcon,
        equippedBannerContent = bannerEntity?.bannerContent
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
        equippedIconId = equippedIconId ?: this.equippedIconId,
        equippedBannerId = equippedBannerId ?: this.equippedBannerId
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
