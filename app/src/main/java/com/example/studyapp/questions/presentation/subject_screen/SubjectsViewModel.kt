package com.example.studyapp.questions.presentation.subject_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyapp.questions.domain.models.Subject
import com.example.studyapp.questions.domain.SubjectFlow
import com.example.studyapp.questions.domain.repositories.SubjectRepository
import com.example.studyapp.user_profile.domain.repositories.UserRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SubjectsViewModel(
    private val subjectRepository: SubjectRepository,
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(SubjectsState())
    val state: StateFlow<SubjectsState> = _state.asStateFlow()

    private val _effect = Channel<SubjectsEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        val userId = savedStateHandle.get<Int>("userId") ?: 0

        // Recuperar el modo de la navegación
        val flowName = savedStateHandle.get<String>("flowType") ?: SubjectFlow.BROWSE.name
        val flow = try {
            SubjectFlow.valueOf(flowName)
        } catch (e: Exception) {
            SubjectFlow.BROWSE
        }

        _state.update { it.copy(flowType = flow, isLoading = true) }

        // Carga reactiva de asignaturas
        viewModelScope.launch {
            subjectRepository.getSubjects(userId).collectLatest { subjects ->
                val uniqueSubjects = subjects.distinctBy { it.id }
                _state.update { it.copy(subjects = uniqueSubjects, isLoading = false) }
                applyFilters()
            }
        }

        // Carga reactiva del perfil de usuario
        viewModelScope.launch {
            userRepository.getUserFlow(userId).collectLatest { user ->
                if (user != null) {
                    _state.update {
                        it.copy(
                            userName = user.username,
                            level = user.level,
                            expCurrent = user.experience,
                            expMax = user.maxExperience,
                            equippedIconUrl = user.equippedIconUrl
                        )
                    }
                }
            }
        }
    }

    fun onIntent(intent: SubjectsIntent) {
        when (intent) {
            is SubjectsIntent.Initialize -> {
                val flow = try {
                    SubjectFlow.valueOf(intent.flowType)
                } catch (e: Exception) {
                    SubjectFlow.BROWSE
                }
                _state.update { it.copy(flowType = flow) }
            }
            is SubjectsIntent.OnSearchQueryChange -> {
                _state.update { it.copy(searchQuery = intent.query) }
                applyFilters()
            }
            is SubjectsIntent.OnTabSelect -> {
                _state.update { it.copy(selectedTab = intent.index) }
            }
            is SubjectsIntent.OnFavoriteToggle -> {
                toggleFavorite(intent.subject)
            }
            is SubjectsIntent.OnSubjectClick -> {
                sendEffect(SubjectsEffect.NavigateToQuizConfig(intent.subject))
            }
            SubjectsIntent.OnProfileClick -> {
                sendEffect(SubjectsEffect.NavigateToProfile)
            }
            SubjectsIntent.OnBackClick -> {
                sendEffect(SubjectsEffect.NavigateBack)
            }
        }
    }

    private fun applyFilters() {
        _state.update { currentState ->
            val query = currentState.searchQuery
            
            val allFiltered = currentState.subjects.filter { subject ->
                subject.name.contains(query, true) || subject.code.contains(query, true)
            }
            val favsFiltered = allFiltered.filter { it.isFavorite }
            
            currentState.copy(
                allSubjectsFiltered = allFiltered,
                favoriteSubjectsFiltered = favsFiltered
            )
        }
    }

    private fun toggleFavorite(subject: Subject) {
        val userId = savedStateHandle.get<Int>("userId") ?: 0
        viewModelScope.launch {
            subjectRepository.toggleFavorite(userId, subject.id)
        }
    }

    private fun sendEffect(effect: SubjectsEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
