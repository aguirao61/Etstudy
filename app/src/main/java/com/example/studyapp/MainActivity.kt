package com.example.studyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.studyapp.core.presentation.home_screen.StudyHomeScreen
import com.example.studyapp.core.presentation.ui.theme.StudyAppTheme
import com.example.studyapp.questions.presentation.quiz_config_screen.QuizConfigScreen
import com.example.studyapp.questions.presentation.quiz_play_screen.QuizPlayScreen
import com.example.studyapp.questions.presentation.quiz_results_screen.QuizResultsScreen
import com.example.studyapp.questions.presentation.subject_screen.SubjectsScreen
import com.example.studyapp.user_profile.presentation.profile_screen.ProfileScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudyAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    NavHost(
                        navController = navController,
                        startDestination = "home"
                    ) {
                        composable("home") {
                            StudyHomeScreen(
                                onNavigateToStudy = { flow ->
                                    navController.navigate("study/${flow.name}")
                                },
                                onNavigateToProfile = {
                                    navController.navigate("profile")
                                }
                            )
                        }

                        navigation(
                            route = "study/{flowType}",
                            startDestination = "subjects",
                            arguments = listOf(
                                navArgument("flowType") { type = NavType.StringType }
                            )
                        ) {
                            composable("subjects") { entry ->
                                val parentEntry = remember(entry) {
                                    navController.getBackStackEntry("study/{flowType}")
                                }
                                val flowType = parentEntry.arguments?.getString("flowType") ?: "BROWSE"
                                
                                SubjectsScreen(
                                    flowType = flowType,
                                    onNavigateBack = {
                                        navController.popBackStack()
                                    },
                                    onNavigateToQuizConfig = { id, name ->
                                        navController.navigate("quiz_config/$id/$name")
                                    },
                                    onNavigateToProfile = {
                                        navController.navigate("profile")
                                    }
                                )
                            }
                            composable(
                                route = "quiz_config/{subjectId}/{subjectName}",
                                arguments = listOf(
                                    navArgument("subjectId") { type = NavType.IntType },
                                    navArgument("subjectName") { type = NavType.StringType }
                                )
                            ) { entry ->
                                val parentEntry = remember(entry) {
                                    navController.getBackStackEntry("study/{flowType}")
                                }
                                val flowType = parentEntry.arguments?.getString("flowType") ?: "TEST"

                                QuizConfigScreen(
                                    flowType = flowType,
                                    onBackClick = {
                                        navController.popBackStack()
                                    },
                                    onStartTest = { subjectId, topicIndex, count, _, _, immediate ->
                                        navController.navigate("quiz_play/$subjectId/$topicIndex/$count/$immediate")
                                    }
                                )
                            }
                            composable(
                                route = "quiz_play/{subjectId}/{topicIndex}/{count}/{immediate}",
                                arguments = listOf(
                                    navArgument("subjectId") { type = NavType.IntType },
                                    navArgument("topicIndex") { type = NavType.IntType },
                                    navArgument("count") { type = NavType.IntType },
                                    navArgument("immediate") { type = NavType.BoolType }
                                )
                            ) {
                                QuizPlayScreen(
                                    onBackClick = {
                                        navController.popBackStack()
                                    },
                                    onNavigateToResults = { score, total ->
                                        navController.navigate("quiz_results/$score/$total") {
                                            // Pop the quiz_play screen so back from results goes to config
                                            popUpTo("quiz_play/{subjectId}/{topicIndex}/{count}/{immediate}") { inclusive = true }
                                        }
                                    }
                                )
                            }
                            composable(
                                route = "quiz_results/{score}/{total}",
                                arguments = listOf(
                                    navArgument("score") { type = NavType.IntType },
                                    navArgument("total") { type = NavType.IntType }
                                )
                            ) { entry ->
                                val score = entry.arguments?.getInt("score") ?: 0
                                val total = entry.arguments?.getInt("total") ?: 0
                                QuizResultsScreen(
                                    score = score,
                                    total = total,
                                    onBackToConfigClick = {
                                        navController.popBackStack()
                                    }
                                )
                            }
                        }

                        composable("profile") {
                            ProfileScreen(
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
}
