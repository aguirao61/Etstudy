package com.example.studyapp.user_profile.presentation.screens.settings_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyapp.user_profile.domain.models.ThemeMode
import com.example.studyapp.user_profile.domain.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    private val userId: Int = savedStateHandle.get<Int>("userId") ?: 0

    init {
        _state.update { it.copy(userId = userId) }
        observeUser()
    }

    private fun observeUser() {
        viewModelScope.launch {
            userRepository.getUserFlow(userId).collect { user ->
                user?.let { u ->
                    _state.update { it.copy(themeMode = u.selectedTheme) }
                }
            }
        }
    }

    fun onThemeChange(themeMode: ThemeMode) {
        viewModelScope.launch {
            userRepository.updateUserTheme(userId, themeMode.name)
        }
    }

    fun onDeleteAccountClick() {
        if (_state.value.isLoading) return
        _state.update { it.copy(showDeleteConfirmation = true) }
    }

    fun onDismissDeleteConfirmation() {
        if (_state.value.isLoading) return
        _state.update { it.copy(showDeleteConfirmation = false) }
    }

    fun onConfirmDeleteAccount(onAccountDeleted: () -> Unit) {
        if (_state.value.isLoading) return
        
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            userRepository.deleteUser(userId)
            _state.update { it.copy(showDeleteConfirmation = false, isLoading = false) }
            onAccountDeleted()
        }
    }
}
