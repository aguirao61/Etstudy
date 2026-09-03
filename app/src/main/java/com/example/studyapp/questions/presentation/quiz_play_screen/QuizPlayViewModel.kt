package com.example.studyapp.questions.presentation.quiz_play_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyapp.core.domain.GameEngine
import com.example.studyapp.core.domain.GameResult
import com.example.studyapp.questions.domain.models.Attempt
import com.example.studyapp.questions.domain.models.AttemptQuestion
import com.example.studyapp.questions.domain.repositories.SubjectRepository
import com.example.studyapp.questions.domain.SubjectFlow
import kotlinx.coroutines.channels.Channel
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QuizPlayViewModel(
    private val subjectRepository: SubjectRepository,
    private val gameEngine: GameEngine,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(QuizPlayState())
    val state: StateFlow<QuizPlayState> = _state.asStateFlow()

    private val _effect = Channel<QuizPlayEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        val userId = savedStateHandle.get<Int>("userId") ?: 0
        val subjectId = savedStateHandle.get<Int>("subjectId") ?: 0
        val moduleId = savedStateHandle.get<Int>("moduleId") ?: 0
        val count = savedStateHandle.get<Int>("count") ?: 10
        val immediate = savedStateHandle.get<Boolean>("immediate") ?: false
        val isRandom = savedStateHandle.get<Boolean>("isRandom") ?: false
        val timerEnabled = savedStateHandle.get<Boolean>("timerEnabled") ?: false
        val flowName = savedStateHandle.get<String>("flowType") ?: SubjectFlow.TEST.name
        val flow = try { SubjectFlow.valueOf(flowName) } catch (_: Exception) { SubjectFlow.TEST }
        
        _state.update { it.copy(
            immediateCorrection = immediate,
            isTimerEnabled = timerEnabled
        ) }

        if (timerEnabled) {
            viewModelScope.launch {
                while (!_state.value.isFinished) {
                    delay(1000L)
                    _state.update { it.copy(timeElapsedSeconds = it.timeElapsedSeconds + 1) }
                }
            }
        }

        viewModelScope.launch {
            val questions = when (flow) {
                SubjectFlow.ERROR_TEST -> {
                    if (moduleId == 0) {
                        subjectRepository.getFailedQuestionsForCourse(userId, subjectId, count, isRandom)
                    } else {
                        subjectRepository.getFailedQuestionsForModule(userId, moduleId, count, isRandom)
                    }
                }
                else -> {
                    if (moduleId == 0) {
                        subjectRepository.getQuestionsForCourse(subjectId, count, isRandom)
                    } else {
                        subjectRepository.getQuestionsForModule(moduleId, count, isRandom)
                    }
                }
            }

            _state.update { it.copy(
                questions = questions,
                isLoading = false
            ) }
        }
    }

    fun onIntent(intent: QuizPlayIntent) {
        when (intent) {
            is QuizPlayIntent.OnOptionSelect -> handleOptionSelect(intent.index)
            QuizPlayIntent.OnNextClick -> moveToNext()
            QuizPlayIntent.OnBackClick -> moveToPrevious()
            QuizPlayIntent.OnCloseClick -> { /* Handle close effect if needed */ }
            is QuizPlayIntent.OnQuestionJump -> jumpToQuestion(intent.index)
            QuizPlayIntent.OnValidateClick -> validateCurrentQuestion()
        }
    }

    private fun handleOptionSelect(index: Int) {
        if (_state.value.isCorrected) return
        
        val currentQuestion = _state.value.currentQuestion ?: return
        
        if (currentQuestion.isMultiSelect) {
            _state.update { 
                val currentSelection = it.selectedOptionIndices
                val newSelection = if (currentSelection.contains(index)) {
                    currentSelection - index
                } else {
                    currentSelection + index
                }
                it.copy(
                    selectedOptionIndices = newSelection,
                    userAnswers = it.userAnswers + (it.currentIndex to newSelection)
                )
            }
        } else {
            val newSelection = setOf(index)
            _state.update { it.copy(
                selectedOptionIndices = newSelection,
                userAnswers = it.userAnswers + (it.currentIndex to newSelection)
            ) }

            // En modo corrección inmediata, para selección única validamos al elegir?
            // El usuario dijo: "Las respuestas se deben validar al pulsar siguiente... cambia el boton de siguiente por el de validar"
            // Así que para selección única también esperamos a pulsar el botón (ahora Validar).
        }
    }

    private fun validateCurrentQuestion() {
        val s = _state.value
        val currentQuestion = s.currentQuestion ?: return
        if (s.isCorrected) return

        val isCorrect = s.selectedOptionIndices == currentQuestion.correctAnswerIndices.toSet()
        _state.update { 
            it.copy(
                isCorrected = true,
                validatedIndices = it.validatedIndices + it.currentIndex,
                score = if (isCorrect) it.score + 1 else it.score
            ) 
        }
    }

    private fun moveToNext() {
        val s = _state.value
        
        // Si estamos en corrección inmediata y NO está corregida aún, VALIDAMOS en lugar de pasar
        if (s.immediateCorrection && !s.isCorrected) {
            validateCurrentQuestion()
            return
        }

        if (s.currentIndex < s.questions.size - 1) {
            val nextIndex = s.currentIndex + 1
            
            // Si no hay corrección inmediata, validamos al pasar (solo si se ha seleccionado algo)
            var updatedScore = s.score
            if (!s.immediateCorrection && !s.isCorrected && s.selectedOptionIndices.isNotEmpty()) {
                val isCorrect = s.selectedOptionIndices == s.currentQuestion?.correctAnswerIndices?.toSet()
                updatedScore = if (isCorrect) s.score + 1 else s.score
            }

            _state.update { it.copy(
                currentIndex = nextIndex,
                selectedOptionIndices = it.userAnswers[nextIndex] ?: emptySet(),
                isCorrected = it.immediateCorrection && it.validatedIndices.contains(nextIndex),
                score = updatedScore
            ) }
        } else {
            // Finalizar
            var finalScore = s.score
            if (!s.immediateCorrection && !s.isCorrected && s.selectedOptionIndices.isNotEmpty()) {
                val isCorrect = s.selectedOptionIndices == s.currentQuestion?.correctAnswerIndices?.toSet()
                finalScore = if (isCorrect) s.score + 1 else s.score
            }

            _state.update { it.copy(isFinished = true, score = finalScore) }
            
            val userId = savedStateHandle.get<Int>("userId") ?: 0
            val subjectId = savedStateHandle.get<Int>("subjectId") ?: 0
            val flowName = savedStateHandle.get<String>("flowType") ?: SubjectFlow.TEST.name
            val flow = try { SubjectFlow.valueOf(flowName) } catch (_: Exception) { SubjectFlow.TEST }

            viewModelScope.launch {
                val attemptQuestions = s.questions.mapIndexed { index, question ->
                    val userSelection = s.userAnswers[index] ?: emptySet()
                    val wasValidated = if (s.immediateCorrection) s.validatedIndices.contains(index) else true
                    
                    val isCorrect = wasValidated && userSelection == question.correctAnswerIndices.toSet()
                    val isBlank = userSelection.isEmpty()
                    
                    val points = if (isCorrect) 1 
                                 else if (isBlank || !wasValidated) 0 
                                 else -1

                    AttemptQuestion(
                        questionId = question.id,
                        questionText = question.text,
                        userAnswers = userSelection,
                        correctAnswers = question.correctAnswerIndices,
                        options = question.options,
                        isCorrect = isCorrect,
                        points = points
                    )
                }

                val failedIds = attemptQuestions.filter { it.points == -1 }.map { it.questionId }
                val correctIds = attemptQuestions.filter { it.points == 1 }.map { it.questionId }

                val result = gameEngine.processQuizResults(
                    userId = userId,
                    subjectId = subjectId,
                    totalQuestions = s.questions.size,
                    correctAnswers = finalScore,
                    failedQuestionIds = failedIds,
                    correctQuestionIds = correctIds,
                    isErrorTest = flow == SubjectFlow.ERROR_TEST
                )

                if (result is GameResult.Success) {
                    val attempt = Attempt(
                        totalQuestions = s.questions.size,
                        score = finalScore,
                        correctCount = finalScore,
                        incorrectCount = attemptQuestions.count { it.points == -1 },
                        blankCount = attemptQuestions.count { it.points == 0 },
                        xpGained = result.xpGained,
                        studyPointsGained = result.studyPointsGained,
                        previousLevel = result.previousLevel,
                        newLevel = result.newLevel,
                        timeElapsedSeconds = s.timeElapsedSeconds,
                        milestoneUnlocked = result.milestoneUnlocked,
                        questions = attemptQuestions
                    )
                    
                    _effect.send(QuizPlayEffect.NavigateToResults(attempt))
                }
            }
        }
    }

    private fun moveToPrevious() {
        if (_state.value.currentIndex > 0) {
            val prevIndex = _state.value.currentIndex - 1
            _state.update { it.copy(
                currentIndex = prevIndex,
                selectedOptionIndices = it.userAnswers[prevIndex] ?: emptySet(),
                isCorrected = it.immediateCorrection && it.validatedIndices.contains(prevIndex)
            ) }
        }
    }

    private fun jumpToQuestion(index: Int) {
        if (index in _state.value.questions.indices) {
            _state.update { it.copy(
                currentIndex = index,
                selectedOptionIndices = it.userAnswers[index] ?: emptySet(),
                isCorrected = it.immediateCorrection && it.validatedIndices.contains(index)
            ) }
        }
    }
}
