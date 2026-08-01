package com.example.studyapp.questions.presentation.subject_screen

import com.example.studyapp.questions.domain.Subject
import com.example.studyapp.questions.domain.SubjectFlow

data class SubjectsState(
    val subjects: List<Subject> = emptyList(),
    val allSubjectsFiltered: List<Subject> = emptyList(),
    val favoriteSubjectsFiltered: List<Subject> = emptyList(),
    val searchQuery: String = "",
    val selectedTab: Int = 0,
    val userName: String = "Usuario123",
    val level: Int = 363,
    val expCurrent: Int = 370342,
    val expMax: Int = 400000,
    val isLoading: Boolean = false,
    val flowType: SubjectFlow = SubjectFlow.BROWSE
)
