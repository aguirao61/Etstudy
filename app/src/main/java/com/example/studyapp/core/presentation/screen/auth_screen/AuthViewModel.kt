package com.example.studyapp.core.presentation.screen.auth_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyapp.user_profile.domain.repositories.UserRepository
import com.example.studyapp.user_profile.domain.models.User
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    private val _effect = Channel<AuthEffect>()
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.OnUuidChange -> {
                _state.update { it.copy(uuidInput = intent.uuid) }
            }
            is AuthIntent.OnAliasChange -> {
                _state.update { it.copy(aliasInput = intent.alias) }
            }
            AuthIntent.ToggleMode -> {
                _state.update { it.copy(isLoginMode = !it.isLoginMode, error = null) }
            }
            AuthIntent.OnSubmit -> {
                if (_state.value.isLoading) return
                if (_state.value.isLoginMode) {
                    login()
                } else {
                    register()
                }
            }
            AuthIntent.DismissSuccessDialog -> {
                _state.update { it.copy(showCreationSuccessDialog = false) }
                val userId = _state.value.createdUserUuid?.toIntOrNull() ?: 0
                sendEffect(AuthEffect.NavigateToHome(userId))
            }
        }
    }

    private fun login() {
        val uuidStr = _state.value.uuidInput
        val userId = uuidStr.toIntOrNull()
        
        if (userId == null) {
            _state.update { it.copy(error = "El UUID debe ser un número") }
            return
        }
        
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val user = userRepository.getUserById(userId)
            if (user != null) {
                sendEffect(AuthEffect.NavigateToHome(userId))
            } else {
                _state.update { it.copy(isLoading = false, error = "Usuario no encontrado") }
            }
        }
    }

    private fun register() {
        val alias = _state.value.aliasInput
        if (alias.isBlank()) {
            _state.update { it.copy(error = "El alias no puede estar vacío") }
            return
        }

        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val newUser = User(username = alias)
            val newId = userRepository.insertUser(newUser)
            
            _state.update { it.copy(
                createdUserUuid = newId.toString(),
                showCreationSuccessDialog = true
            ) }
        }
    }

    private fun sendEffect(effect: AuthEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
