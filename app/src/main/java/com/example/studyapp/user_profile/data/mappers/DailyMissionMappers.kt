package com.example.studyapp.user_profile.data.mappers

import com.example.studyapp.user_profile.data.tables.daily_mission_table.DailyMissionLocalEntity
import com.example.studyapp.user_profile.domain.models.DailyMission

fun DailyMissionLocalEntity.toDomainModel(): DailyMission {
    return DailyMission(
        userId = userId,
        lastUpdate = lastUpdate,
        quizzesCompleted = quizzesCompleted,
        quizzesPassed = quizzesPassed,
        subjectsStudied = if (subjectsStudied.isBlank()) emptyList() else subjectsStudied.split(",").map { it.toInt() },
        claimedMask = claimedMask
    )
}

fun DailyMission.toLocalEntity(): DailyMissionLocalEntity {
    return DailyMissionLocalEntity(
        userId = userId,
        lastUpdate = lastUpdate,
        quizzesCompleted = quizzesCompleted,
        quizzesPassed = quizzesPassed,
        subjectsStudied = subjectsStudied.joinToString(","),
        claimedMask = claimedMask
    )
}
