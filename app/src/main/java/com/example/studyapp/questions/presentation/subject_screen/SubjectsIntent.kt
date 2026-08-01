package com.example.studyapp.questions.presentation.subject_screen

import com.example.studyapp.questions.domain.Subject

sealed class SubjectsIntent {
    data class Initialize(val flowType: String) : SubjectsIntent()
    data class OnSearchQueryChange(val query: String) : SubjectsIntent()
    data class OnTabSelect(val index: Int) : SubjectsIntent()
    data class OnFavoriteToggle(val subject: Subject) : SubjectsIntent()
    data class OnSubjectClick(val subject: Subject) : SubjectsIntent()
    data object OnProfileClick : SubjectsIntent()
    data object OnBackClick : SubjectsIntent()
}
