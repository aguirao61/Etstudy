package com.example.studyapp.questions.presentation.quiz_config_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyapp.questions.domain.SubjectFlow
import com.example.studyapp.questions.domain.repositories.SubjectRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QuizConfigViewModel(
    private val subjectRepository: SubjectRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(QuizConfigState())
    val state: StateFlow<QuizConfigState> = _state.asStateFlow()

    private val _effect = Channel<QuizConfigEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        val userId = savedStateHandle.get<Int>("userId") ?: 0
        val subjectId = savedStateHandle.get<Int>("subjectId") ?: 0
        val subjectName = savedStateHandle.get<String>("subjectName") ?: ""
        val flowName = savedStateHandle.get<String>("flowType") ?: SubjectFlow.TEST.name
        val flow = try { SubjectFlow.valueOf(flowName) } catch (e: Exception) { SubjectFlow.TEST }

        _state.update {
            it.copy(
                subjectId = subjectId,
                subjectName = subjectName,
                flowType = flow,
                showProgress = true
            )
        }

        viewModelScope.launch {
            val passedCount = subjectRepository.getTestsPassed(userId, subjectId)
            _state.update { it.copy(passedTestsCount = passedCount) }

            val totalQuestions = if (flow == SubjectFlow.ERROR_TEST) {
                subjectRepository.getFailedQuestionCountForCourse(userId, subjectId)
            } else {
                subjectRepository.getQuestionCountForCourse(subjectId)
            }

            subjectRepository.getModules(userId, subjectId).collect { modules ->
                val topicsWithCounts = mutableListOf<Pair<String, Int>>()
                val topicIds = mutableListOf<Int>()
                
                // Opción "Todos los temas"
                topicsWithCounts.add("Todos los temas" to totalQuestions)
                topicIds.add(0)
                
                // Módulos individuales
                modules.forEach { module ->
                    val count = if (flow == SubjectFlow.ERROR_TEST) {
                        subjectRepository.getFailedQuestionCountForModule(userId, module.id)
                    } else {
                        subjectRepository.getQuestionCountForModule(module.id)
                    }
                    topicsWithCounts.add("T${module.number} · ${module.name}" to count)
                    topicIds.add(module.id)
                }

                _state.update {
                    it.copy(
                        topics = topicsWithCounts,
                        topicIds = topicIds,
                        totalQuestionsAvailable = totalQuestions,
                        questionCount = 25.coerceAtMost(totalQuestions)
                    )
                }
            }
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
                val moduleId = if (s.topicIds.isNotEmpty()) s.topicIds[s.selectedTopicIndex] else 0
                sendEffect(
                    QuizConfigEffect.StartQuiz(
                        subjectId = s.subjectId,
                        moduleId = moduleId,
                        questionCount = s.questionCount,
                        isRandom = s.isRandomOrder,
                        isTimerEnabled = s.isTimerEnabled,
                        immediateCorrection = s.immediateCorrection,
                        flowType = s.flowType.name
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
