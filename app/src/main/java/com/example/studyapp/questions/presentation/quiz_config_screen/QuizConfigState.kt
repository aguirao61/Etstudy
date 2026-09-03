package com.example.studyapp.questions.presentation.quiz_config_screen

import com.example.studyapp.questions.domain.SubjectFlow

data class QuizConfigState(
    val subjectId: Int = 0,
    val subjectName: String = "",
    val flowType: SubjectFlow = SubjectFlow.TEST,
    val topics: List<Pair<String, Int>> = emptyList(),
    val topicIds: List<Int> = emptyList(),
    val selectedTopicIndex: Int = 0,
    val questionCount: Int = 25,
    val isRandomOrder: Boolean = true,
    val isTimerEnabled: Boolean = false,
    val immediateCorrection: Boolean = true,
    val totalQuestionsAvailable: Int = 0,
    val passedTestsCount: Int = 0,
    val showProgress: Boolean = false
) {
    val selectedTopicQuestionsCount: Int
        get() = if (topics.isNotEmpty() && selectedTopicIndex in topics.indices) topics[selectedTopicIndex].second else 0
}
