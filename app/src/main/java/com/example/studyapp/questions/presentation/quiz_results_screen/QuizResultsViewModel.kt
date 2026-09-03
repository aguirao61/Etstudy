package com.example.studyapp.questions.presentation.quiz_results_screen

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyapp.questions.domain.models.Attempt
import com.example.studyapp.user_profile.domain.models.User
import com.example.studyapp.user_profile.domain.repositories.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QuizResultsViewModel(
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(QuizResultsState())
    val state: StateFlow<QuizResultsState> = _state.asStateFlow()

    init {
        val userId = savedStateHandle.get<Int>("userId") ?: 0
        Log.d("QuizResultsVM", "Init with userId: $userId")
        
        // Fetch user data
        viewModelScope.launch {
            val user = userRepository.getUserById(userId)
            Log.d("QuizResultsVM", "Initial user fetch: ${user?.username}")
            if (user != null) {
                _state.update { it.copy(user = user) }
            } else {
                Log.e("QuizResultsVM", "User not found for ID: $userId")
            }
        }

        // Collect attempt from SavedStateHandle
        viewModelScope.launch {
            // Using getFlow or similar if available, but let's stick to getStateFlow or just a check
            val attempt = savedStateHandle.get<Attempt>("attempt")
            Log.d("QuizResultsVM", "Initial attempt check: ${attempt != null}")
            
            if (attempt != null) {
                handleAttempt(attempt, userId)
            } else {
                // If not available immediately, listen for it (in case of delayed setting)
                savedStateHandle.getStateFlow<Attempt?>("attempt", null).collect { asyncAttempt ->
                    Log.d("QuizResultsVM", "Async attempt collected: ${asyncAttempt != null}")
                    if (asyncAttempt != null) {
                        handleAttempt(asyncAttempt, userId)
                    }
                }
            }
        }
    }

    private suspend fun handleAttempt(attempt: Attempt, userId: Int) {
        _state.update { it.copy(attempt = attempt) }
        val user = _state.value.user ?: userRepository.getUserById(userId)
        if (user != null) {
            _state.update { it.copy(user = user) }
            
            // Trigger popups sequentially
            viewModelScope.launch {
                delay(1200) // Initial delay to sync with animations
                
                if (attempt.newLevel > attempt.previousLevel) {
                    _state.update { it.copy(showLevelUp = true) }
                    delay(3500) // Show level up for a few seconds
                    _state.update { it.copy(showLevelUp = false) }
                    delay(500) // Brief pause between popups
                }
                
                if (attempt.milestoneUnlocked) {
                    _state.update { it.copy(showMilestone = true) }
                    delay(3500) // Show milestone for a few seconds
                    _state.update { it.copy(showMilestone = false) }
                }
            }
            
            animateXp(user, attempt)
        }
    }

    private fun animateXp(user: User, attempt: Attempt) {
        viewModelScope.launch {
            // We want to show the progress increasing.
            // But 'user' is already updated in DB.
            // So we start from (user.exp - attempt.xpGained) and animate to user.exp.
            // This is simplified if we don't cross multiple levels.

            val totalGained = attempt.xpGained
            var currentAnimateExp = (user.experience - totalGained).coerceAtLeast(0)

            // For now, let's just animate the progress value from 0 to 1 if it's simpler
            // or use the real values.

            val steps = 50
            val expPerStep = totalGained / steps
            
            repeat(steps) {
                delay(20)
                currentAnimateExp += expPerStep
                _state.update { it.copy(animatedExp = currentAnimateExp) }
            }
            
            _state.update { it.copy(animatedExp = user.experience) }
        }
    }
}

data class QuizResultsState(
    val attempt: Attempt? = null,
    val user: User? = null,
    val animatedExp: Int = 0,
    val showLevelUp: Boolean = false,
    val showMilestone: Boolean = false,
    val isLoading: Boolean = false
)
