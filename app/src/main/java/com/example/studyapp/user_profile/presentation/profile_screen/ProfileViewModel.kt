package com.example.studyapp.user_profile.presentation.profile_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyapp.user_profile.domain.repositories.UserRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    private val _effect = Channel<ProfileEffect>()
    val effect = _effect.receiveAsFlow()

    private val userId: Int = savedStateHandle.get<Int>("userId") ?: 0

    init {
        observeUser()
    }

    private fun observeUser() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            userRepository.getUserFlow(userId).collect { user ->
                _state.update { it.copy(isLoading = false, user = user) }
            }
        }
    }

    fun onIntent(intent: ProfileIntent) {
        when (intent) {
            ProfileIntent.OnEditNameClick -> {
                _state.update { it.copy(
                    showEditNameDialog = true,
                    tempName = it.user?.username ?: ""
                ) }
            }
            is ProfileIntent.OnTempNameChange -> {
                _state.update { it.copy(tempName = intent.name) }
            }
            ProfileIntent.OnSaveNameClick -> {
                saveName()
            }
            ProfileIntent.OnDismissEditDialog -> {
                _state.update { it.copy(showEditNameDialog = false) }
            }
            ProfileIntent.OnEditMusicClick -> {
                _state.update { it.copy(
                    showEditMusicDialog = true,
                    tempSongName = it.user?.userSongName ?: "",
                    tempArtistName = it.user?.userSongArtist ?: ""
                ) }
            }
            is ProfileIntent.OnTempSongChange -> {
                _state.update { it.copy(tempSongName = intent.song) }
            }
            is ProfileIntent.OnTempArtistChange -> {
                _state.update { it.copy(tempArtistName = intent.artist) }
            }
            ProfileIntent.OnSaveMusicClick -> {
                saveMusic()
            }
            ProfileIntent.OnDismissMusicDialog -> {
                _state.update { it.copy(showEditMusicDialog = false) }
            }
        }
    }

    private fun saveName() {
        val currentUser = _state.value.user ?: return
        val newName = _state.value.tempName
        
        if (newName.isBlank()) return

        viewModelScope.launch {
            val updatedUser = currentUser.copy(username = newName)
            userRepository.updateUser(updatedUser)
            _state.update { it.copy(showEditNameDialog = false) }
        }
    }

    private fun saveMusic() {
        val currentUser = _state.value.user ?: return
        val newSong = _state.value.tempSongName
        val newArtist = _state.value.tempArtistName

        viewModelScope.launch {
            val updatedUser = currentUser.copy(userSongName = newSong, userSongArtist = newArtist)
            userRepository.updateUser(updatedUser)
            _state.update { it.copy(showEditMusicDialog = false) }
        }
    }
}
