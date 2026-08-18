package com.example.studyapp.user_profile.presentation.screens.community_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyapp.user_profile.domain.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CommunityViewModel(
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(CommunityState())
    val state: StateFlow<CommunityState> = _state.asStateFlow()

    private val userId: Int = savedStateHandle.get<Int>("userId") ?: -1

    init {
        _state.update { it.copy(currentUserId = userId) }
        loadUsers()
    }

    private fun loadUsers() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            userRepository.getAllUsers().collect { users ->
                _state.update { it.copy(users = users, isLoading = false) }
            }
        }
    }

    fun onLeaderboardTypeChange(type: LeaderboardType) {
        _state.update { it.copy(leaderboardType = type) }
    }
}
