package com.example.studyapp.questions.presentation.quiz_config_screen

import androidx.lifecycle.SavedStateHandle
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

class QuizConfigViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(QuizConfigState())
    val state: StateFlow<QuizConfigState> = _state.asStateFlow()

    private val _effect = Channel<QuizConfigEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        val subjectId = savedStateHandle.get<Int>("subjectId") ?: 0
        val subjectName = savedStateHandle.get<String>("subjectName") ?: ""
        val flowName = savedStateHandle.get<String>("flowType") ?: SubjectFlow.TEST.name
        val flow = try { SubjectFlow.valueOf(flowName) } catch (e: Exception) { SubjectFlow.TEST }

        // Mock topics data based on subjectId (simulating a DB fetch)
        val mockTopics = listOf(
            "Todos los temas" to 2391,
            "T1 · Sinterizado" to 569,
            "T2 · Soldadura" to 507,
            "T3 · Ens. Mecánicos" to 552,
            "T4 · Ens. No Destructivos" to 763
        )

        _state.update {
            it.copy(
                subjectId = subjectId,
                subjectName = subjectName,
                flowType = flow,
                topics = mockTopics,
                totalQuestionsAvailable = mockTopics[0].second,
                questionCount = 25.coerceAtMost(mockTopics[0].second)
            )
        }
    }

    fun onIntent(intent: QuizConfigIntent) {
        when (intent) {
            is QuizConfigIntent.Initialize -> {
                val flow = try {
                    SubjectFlow.valueOf(intent.flowType)
                } catch (e: Exception) {
                    SubjectFlow.TEST
                }
                _state.update { it.copy(flowType = flow) }
            }
            is QuizConfigIntent.OnTopicSelect -> {
                val newCount = _state.value.questionCount.coerceAtMost(_state.value.topics[intent.index].second)
                _state.update { 
                    it.copy(
                        selectedTopicIndex = intent.index,
                        questionCount = newCount
                    )
                }
            }
            is QuizConfigIntent.OnQuestionCountChange -> {
                _state.update { it.copy(questionCount = intent.count) }
            }
            is QuizConfigIntent.OnRandomOrderToggle -> {
                _state.update { it.copy(isRandomOrder = intent.isRandom) }
            }
            is QuizConfigIntent.OnTimerToggle -> {
                _state.update { it.copy(isTimerEnabled = intent.isEnabled) }
            }
            is QuizConfigIntent.OnCorrectionModeToggle -> {
                _state.update { it.copy(immediateCorrection = intent.immediate) }
            }
            QuizConfigIntent.OnBackClick -> {
                sendEffect(QuizConfigEffect.NavigateBack)
            }
            QuizConfigIntent.OnStartTestClick -> {
                val s = _state.value
                sendEffect(
                    QuizConfigEffect.StartQuiz(
                        subjectId = s.subjectId,
                        topicIndex = s.selectedTopicIndex,
                        questionCount = s.questionCount,
                        isRandom = s.isRandomOrder,
                        isTimerEnabled = s.isTimerEnabled,
                        immediateCorrection = s.immediateCorrection
                    )
                )
            }
        }
    }

    private fun sendEffect(effect: QuizConfigEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
