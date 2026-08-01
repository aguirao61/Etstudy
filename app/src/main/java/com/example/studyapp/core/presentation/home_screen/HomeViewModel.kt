package com.example.studyapp.core.presentation.home_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyapp.questions.domain.SubjectFlow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _effect = Channel<HomeEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        _state.update {
            it.copy(
                userName = "Usuario123",
                expCurrent = 370342,
                expMax = 400000,
                level = 363
            )
        }
    }

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.OnStartClick -> {
                _state.update { it.copy(isStartPopupVisible = true) }
            }
            HomeIntent.OnGuideClick -> {
                sendEffect(HomeEffect.ShowToast("Botón GUÍA pulsado"))
            }
            HomeIntent.OnProfileClick -> {
                sendEffect(HomeEffect.NavigateToProfile)
            }
            is HomeIntent.OnNavClick -> {
                if (intent.item == "Cursos") {
                    sendEffect(HomeEffect.NavigateToStudy(SubjectFlow.BROWSE))
                } else {
                    sendEffect(HomeEffect.ShowToast("Navegación: ${intent.item} pulsada"))
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
                } else {
                    sendEffect(HomeEffect.ShowToast("Opción seleccionada: ${intent.option}"))
                }
            }
        }
    }

    private fun sendEffect(effect: HomeEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
