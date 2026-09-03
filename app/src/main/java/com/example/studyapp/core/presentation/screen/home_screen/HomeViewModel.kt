package com.example.studyapp.core.presentation.screen.home_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyapp.questions.domain.SubjectFlow
import com.example.studyapp.user_profile.domain.repositories.DailyMissionRepository
import com.example.studyapp.user_profile.domain.repositories.UserRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userRepository: UserRepository,
    private val dailyMissionRepository: DailyMissionRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _effect = Channel<HomeEffect>()
    val effect = _effect.receiveAsFlow()

        init {
        val userId = savedStateHandle.get<Int>("userId") ?: 0
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            // Check and reset daily missions and streak on startup
            launch { dailyMissionRepository.checkAndResetDailyMission(userId) }
            launch { userRepository.checkAndResetStreak(userId) }

            // Collect User
            launch {
                userRepository.getUserFlow(userId).collect { user ->
                    if (user != null) {
                        _state.update {
                            it.copy(
                                userName = user.username,
                                expCurrent = user.experience,
                                expMax = user.maxExperience,
                                level = user.level,
                                equippedIconUrl = user.equippedIconUrl,
                                currentStreak = user.currentStreak,
                                isLoading = false
                            )
                        }
                    } else {
                        _state.update { it.copy(isLoading = false) }
                    }
                }
            }

            // Collect Daily Missions
            launch {
                dailyMissionRepository.getDailyMissionFlow(userId).collect { mission ->
                    if (mission == null) {
                        dailyMissionRepository.createDailyMission(userId)
                    } else {
                        _state.update { it.copy(dailyMission = mission) }
                    }
                }
            }
        }
    }

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.OnStartClick -> {
                _state.update { it.copy(isStartPopupVisible = true) }
            }
            HomeIntent.OnGuideClick -> {
                // Guide Action
            }
            HomeIntent.OnProfileClick -> {
                sendEffect(HomeEffect.NavigateToProfile)
            }
            is HomeIntent.OnNavClick -> {
                when (intent.item) {
                    "Cursos" -> sendEffect(HomeEffect.NavigateToStudy(SubjectFlow.BROWSE))
                    "Comunidad" -> sendEffect(HomeEffect.NavigateToCommunity)
                    "Ajustes" -> sendEffect(HomeEffect.NavigateToSettings)
                    else -> { /* No hacer nada */ }
                }
            }
            HomeIntent.DismissStartPopup -> {
                _state.update { it.copy(isStartPopupVisible = false) }
            }
            is HomeIntent.OnTestOptionClick -> {
                _state.update { it.copy(isStartPopupVisible = false) }
                val flow = when (intent.option) {
                    "Tests" -> SubjectFlow.TEST
                    "Tests de fallos" -> SubjectFlow.ERROR_TEST
                    else -> null
                }
                
                if (flow != null) {
                    sendEffect(HomeEffect.NavigateToStudy(flow))
                }
            }
            HomeIntent.OnChallengesClick -> {
                _state.update { it.copy(isDailyChallengesVisible = true) }
            }
            HomeIntent.DismissChallengesDialog -> {
                _state.update { it.copy(isDailyChallengesVisible = false) }
            }
        }
    }

    private fun sendEffect(effect: HomeEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
