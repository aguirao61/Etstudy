package com.example.studyapp.questions.presentation.quiz_play_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyapp.questions.domain.Question
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QuizPlayViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(QuizPlayState())
    val state: StateFlow<QuizPlayState> = _state.asStateFlow()

    private val _effect = Channel<QuizPlayEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        val count = savedStateHandle.get<Int>("count") ?: 10
        val immediate = savedStateHandle.get<Boolean>("immediate") ?: false
        val topicIndex = savedStateHandle.get<Int>("topicIndex") ?: 0
        
        val topicLabel = when(topicIndex) {
            1 -> "T1"
            2 -> "T2"
            3 -> "T3"
            4 -> "T4"
            else -> "Mix"
        }

        // Mock data
        val mockQuestions = List(count) { i ->
            Question(
                id = i,
                text = "En fatiga, la longitud crítica de grieta se puede determinar a partir de la tenacidad de fractura del material",
                options = listOf("Verdadero", "Falso"),
                correctAnswerIndex = 0,
                topicLabel = topicLabel
            )
        }

        _state.update { it.copy(
            questions = mockQuestions,
            immediateCorrection = immediate
        ) }
    }

    fun onIntent(intent: QuizPlayIntent) {
        when (intent) {
            is QuizPlayIntent.OnOptionSelect -> handleOptionSelect(intent.index)
            QuizPlayIntent.OnNextClick -> moveToNext()
            QuizPlayIntent.OnBackClick -> moveToPrevious()
            QuizPlayIntent.OnCloseClick -> { /* Handle close effect if needed */ }
            is QuizPlayIntent.OnQuestionJump -> jumpToQuestion(intent.index)
        }
    }

    private fun handleOptionSelect(index: Int) {
        if (_state.value.isCorrected) return
        
        _state.update { it.copy(
            selectedOptionIndex = index,
            userAnswers = it.userAnswers + (it.currentIndex to index)
        ) }

        if (_state.value.immediateCorrection) {
            // Validate immediately
            val isCorrect = index == _state.value.currentQuestion?.correctAnswerIndex
            _state.update { 
                it.copy(
                    isCorrected = true,
                    score = if (isCorrect) it.score + 1 else it.score - 1
                ) 
            }
            
            // Auto advance after 500ms (only if NOT the last question)
            if (_state.value.currentIndex < _state.value.questions.size - 1) {
                viewModelScope.launch {
                    delay(500)
                    moveToNext()
                }
            }
        }
    }

    private fun moveToNext() {
        val s = _state.value
        if (s.currentIndex < s.questions.size - 1) {
            val nextIndex = s.currentIndex + 1
            // Update score if not immediate (manual validation on next)
            val updatedScore = if (!s.immediateCorrection && s.selectedOptionIndex != null) {
                val isCorrect = s.selectedOptionIndex == s.currentQuestion?.correctAnswerIndex
                if (isCorrect) s.score + 1 else s.score - 1
            } else s.score

            _state.update { it.copy(
                currentIndex = nextIndex,
                selectedOptionIndex = it.userAnswers[nextIndex],
                isCorrected = it.immediateCorrection && it.userAnswers.containsKey(nextIndex),
                score = updatedScore
            ) }
        } else {
            // Finish
             val finalScore = if (!s.immediateCorrection && s.selectedOptionIndex != null) {
                val isCorrect = s.selectedOptionIndex == s.currentQuestion?.correctAnswerIndex
                if (isCorrect) s.score + 1 else s.score - 1
            } else s.score

            _state.update { it.copy(isFinished = true, score = finalScore) }
            viewModelScope.launch {
                _effect.send(QuizPlayEffect.NavigateToResults(finalScore, s.questions.size))
            }
        }
    }

    private fun sendEffect(effect: QuizPlayEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }

    private fun moveToPrevious() {
        if (_state.value.currentIndex > 0) {
            val prevIndex = _state.value.currentIndex - 1
            _state.update { it.copy(
                currentIndex = prevIndex,
                selectedOptionIndex = it.userAnswers[prevIndex],
                isCorrected = it.immediateCorrection && it.userAnswers.containsKey(prevIndex)
            ) }
        }
    }

    private fun jumpToQuestion(index: Int) {
        if (index in _state.value.questions.indices) {
            _state.update { it.copy(
                currentIndex = index,
                selectedOptionIndex = it.userAnswers[index],
                isCorrected = it.immediateCorrection && it.userAnswers.containsKey(index)
            ) }
        }
    }
}
