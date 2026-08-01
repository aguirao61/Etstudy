package com.example.studyapp.questions.presentation.subject_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyapp.questions.domain.Subject
import com.example.studyapp.questions.domain.SubjectFlow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SubjectsViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(SubjectsState())
    val state: StateFlow<SubjectsState> = _state.asStateFlow()

    private val _effect = Channel<SubjectsEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        // Recuperar el modo de la navegación
        val flowName = savedStateHandle.get<String>("flowType") ?: SubjectFlow.BROWSE.name
        val flow = try {
            SubjectFlow.valueOf(flowName)
        } catch (e: Exception) {
            SubjectFlow.BROWSE
        }

        // Mock data
        val initialSubjects = listOf(
            Subject(1, "Matemáticas Discretas", "MAT-101"),
            Subject(2, "Estructuras de Datos", "INF-201", isFavorite = true),
            Subject(3, "Bases de Datos", "INF-202"),
            Subject(4, "Física General", "FIS-101"),
            Subject(5, "Programación Orientada a Objetos", "INF-102", isFavorite = true),
            Subject(6, "Cálculo Integral", "MAT-102"),
            Subject(7, "Sistemas Operativos", "INF-301"),
            Subject(8, "Ingeniería de Software", "INF-302")
        )
        _state.update {
            it.copy(
                subjects = initialSubjects,
                flowType = flow
            )
        }
        applyFilters()
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
                if (_state.value.flowType == SubjectFlow.BROWSE) {
                    sendEffect(SubjectsEffect.ShowToast("Asignatura: ${intent.subject.name}"))
                } else {
                    sendEffect(SubjectsEffect.NavigateToQuizConfig(intent.subject))
                }
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
            val allFiltered = currentState.subjects.filter { 
                it.name.contains(query, true) || it.code.contains(query, true) 
            }
            val favsFiltered = allFiltered.filter { it.isFavorite }
            
            currentState.copy(
                allSubjectsFiltered = allFiltered,
                favoriteSubjectsFiltered = favsFiltered
            )
        }
    }

    private fun toggleFavorite(subject: Subject) {
        _state.update { currentState ->
            val updated = currentState.subjects.map {
                if (it.id == subject.id) it.copy(isFavorite = !it.isFavorite) else it
            }
            currentState.copy(subjects = updated)
        }
        applyFilters()
    }

    private fun sendEffect(effect: SubjectsEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
