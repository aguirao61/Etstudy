package com.example.studyapp.user_profile.presentation.screens.community_screen

import com.example.studyapp.user_profile.domain.models.User

enum class LeaderboardType {
    LEVEL, STUDY_POINTS
}

data class CommunityState(
    val users: List<User> = emptyList(),
    val leaderboardType: LeaderboardType = LeaderboardType.LEVEL,
    val currentUserId: Int = -1,
    val isLoading: Boolean = false
) {
    val sortedUsers: List<User>
        get() = when (leaderboardType) {
            LeaderboardType.LEVEL -> users.sortedWith(compareByDescending<User> { it.level }.thenByDescending { it.experience })
            LeaderboardType.STUDY_POINTS -> users.sortedByDescending { it.studyPoints }
        }
}
