package com.example.studyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.studyapp.core.presentation.home_screen.StudyHomeScreen
import com.example.studyapp.core.presentation.ui.theme.StudyAppTheme
import com.example.studyapp.questions.presentation.quiz_config_screen.QuizConfigScreen
import com.example.studyapp.questions.presentation.subject_screen.SubjectsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudyAppTheme {
                val navController = rememberNavController()
                
                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    composable("home") {
                        StudyHomeScreen(
                            onNavigateToSubjects = { flow ->
                                navController.navigate("subjects/${flow.name}")
                            }
                        )
                    }
                    composable(
                        route = "subjects/{flowType}",
                        arguments = listOf(
                            navArgument("flowType") { type = NavType.StringType }
                        )
                    ) {
                        SubjectsScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToQuizConfig = { id, name, flow ->
                                navController.navigate("quiz_config/$id/$name/$flow")
                            }
                        )
                    }
                    composable(
                        route = "quiz_config/{subjectId}/{subjectName}/{flowType}",
                        arguments = listOf(
                            navArgument("subjectId") { type = NavType.IntType },
                            navArgument("subjectName") { type = NavType.StringType },
                            navArgument("flowType") { type = NavType.StringType }
                        )
                    ) {
                        QuizConfigScreen(
                            onBackClick = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
