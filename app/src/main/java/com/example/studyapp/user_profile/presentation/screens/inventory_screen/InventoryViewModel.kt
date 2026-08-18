package com.example.studyapp.user_profile.presentation.screens.inventory_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyapp.user_profile.domain.models.Icon
import com.example.studyapp.user_profile.domain.repositories.IconRepository
import com.example.studyapp.user_profile.domain.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InventoryViewModel(
    private val iconRepository: IconRepository,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(InventoryState())
    val state: StateFlow<InventoryState> = _state.asStateFlow()

    private val userId: Int = savedStateHandle.get<Int>("userId") ?: 0

    init {
        loadIcons()
        observeUser()
    }

    private fun loadIcons() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            iconRepository.getAllIcons(userId).collect { icons ->
                _state.update { it.copy(isLoading = false, icons = icons) }
            }
        }
    }

    private fun observeUser() {
        viewModelScope.launch {
            userRepository.getUserFlow(userId).collectLatest { user ->
                _state.update { it.copy(equippedIconId = user?.equippedIconId) }
            }
        }
    }

    fun onEquipIcon(icon: Icon) {
        if (!icon.isUnlocked) return
        viewModelScope.launch {
            val user = userRepository.getUserById(userId)
            val newIconId = if (user?.equippedIconId == icon.id) null else icon.id
            userRepository.updateEquippedCosmetics(userId, newIconId, user?.equippedBannerId)
        }
    }
}
