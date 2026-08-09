package com.example.studyapp.questions.presentation.subject_screen

import com.example.studyapp.questions.domain.models.Subject
import com.example.studyapp.questions.domain.SubjectFlow

data class SubjectsState(
    val subjects: List<Subject> = emptyList(),
    val allSubjectsFiltered: List<Subject> = emptyList(),
    val favoriteSubjectsFiltered: List<Subject> = emptyList(),
    val searchQuery: String = "",
    val selectedTab: Int = 0,
    val userName: String = "",
    val level: Int = 1,
    val expCurrent: Int = 0,
    val expMax: Int = 100,
    val isLoading: Boolean = false,
    val flowType: SubjectFlow = SubjectFlow.BROWSE
)
