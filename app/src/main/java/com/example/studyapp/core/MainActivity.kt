package com.example.studyapp.core

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studyapp.core.presentation.auth_screen.AuthScreen
import com.example.studyapp.core.presentation.auth_screen.AuthViewModel
import com.example.studyapp.core.presentation.home_screen.HomeViewModel
import com.example.studyapp.core.presentation.home_screen.StudyHomeScreen
import com.example.studyapp.core.presentation.ui.theme.StudyAppTheme
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import com.example.studyapp.user_profile.presentation.profile_screen.ProfileViewModel
import com.example.studyapp.user_profile.presentation.profile_screen.ProfileScreen
import com.example.studyapp.user_profile.presentation.inventory_screen.InventoryScreen
import com.example.studyapp.user_profile.presentation.banner_screen.BannersScreen
import com.example.studyapp.user_profile.presentation.banner_screen.BannersViewModel
import com.example.studyapp.questions.presentation.quiz_config_screen.QuizConfigScreen
import com.example.studyapp.questions.presentation.quiz_config_screen.QuizConfigViewModel
import com.example.studyapp.questions.domain.models.Attempt
import com.example.studyapp.questions.presentation.quiz_play_screen.QuizPlayScreen
import com.example.studyapp.questions.presentation.quiz_play_screen.QuizPlayViewModel
import com.example.studyapp.questions.presentation.quiz_results_screen.QuizResultsScreen
import com.example.studyapp.questions.presentation.quiz_results_screen.QuizResultsViewModel
import com.example.studyapp.questions.presentation.subject_screen.SubjectsViewModel
import com.example.studyapp.questions.presentation.subject_screen.SubjectsScreen

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
                        startDestination = "auth"
                    ) {
                        composable("auth") {
                            val appModule = (LocalContext.current.applicationContext as App).appModule
                            val authViewModel: AuthViewModel = viewModel(
                                factory = object : ViewModelProvider.Factory {
                                    @Suppress("UNCHECKED_CAST")
                                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                        return AuthViewModel(appModule.userRepository) as T
                                    }
                                }
                            )
                            AuthScreen(
                                viewModel = authViewModel,
                                onNavigateToHome = { userId ->
                                    navController.navigate("home/$userId") {
                                        popUpTo("auth") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(
                            route = "home/{userId}",
                            arguments = listOf(navArgument("userId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
                            val appModule = (LocalContext.current.applicationContext as App).appModule
                            val homeViewModel: HomeViewModel = viewModel(
                                factory = object : ViewModelProvider.Factory {
                                    @Suppress("UNCHECKED_CAST")
                                    override fun <T : ViewModel> create(modelClass: Class<T>, extras: androidx.lifecycle.viewmodel.CreationExtras): T {
                                        val savedStateHandle = extras.createSavedStateHandle()
                                        savedStateHandle["userId"] = userId
                                        return HomeViewModel(appModule.userRepository, savedStateHandle) as T
                                    }
                                }
                            )
                            StudyHomeScreen(
                                viewModel = homeViewModel,
                                onNavigateToStudy = { flow ->
                                    navController.navigate("study/$userId/${flow.name}")
                                },
                                onNavigateToProfile = {
                                    navController.navigate("profile/$userId")
                                }
                            )
                        }

                        navigation(
                            route = "study/{userId}/{flowType}",
                            startDestination = "subjects",
                            arguments = listOf(
                                navArgument("userId") { type = NavType.IntType },
                                navArgument("flowType") { type = NavType.StringType }
                            )
                        ) {
                            composable("subjects") { entry ->
                                val parentEntry = remember(entry) {
                                    navController.getBackStackEntry("study/{userId}/{flowType}")
                                }
                                val userId = parentEntry.arguments?.getInt("userId") ?: 0
                                val flowType =
                                    parentEntry.arguments?.getString("flowType") ?: "BROWSE"

                                val appModule = (LocalContext.current.applicationContext as App).appModule
                                val subjectsViewModel: SubjectsViewModel = viewModel(
                                    factory = object : ViewModelProvider.Factory {
                                        @Suppress("UNCHECKED_CAST")
                                        override fun <T : ViewModel> create(modelClass: Class<T>, extras: androidx.lifecycle.viewmodel.CreationExtras): T {
                                            val savedStateHandle = extras.createSavedStateHandle()
                                            savedStateHandle["userId"] = userId
                                            savedStateHandle["flowType"] = flowType
                                            return SubjectsViewModel(
                                                appModule.subjectRepository,
                                                appModule.userRepository,
                                                savedStateHandle
                                            ) as T
                                        }
                                    }
                                )

                                SubjectsScreen(
                                    flowType = flowType,
                                    viewModel = subjectsViewModel,
                                    onNavigateBack = {
                                        navController.popBackStack()
                                    },
                                    onNavigateToQuizConfig = { id, name ->
                                        navController.navigate("quiz_config/$id/$name")
                                    },
                                    onNavigateToProfile = {
                                        navController.navigate("profile/$userId")
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
                                    navController.getBackStackEntry("study/{userId}/{flowType}")
                                }
                                val userId = parentEntry.arguments?.getInt("userId") ?: 0
                                val flowType =
                                    parentEntry.arguments?.getString("flowType") ?: "TEST"

                                val subjectId = entry.arguments?.getInt("subjectId") ?: 0
                                val subjectName = entry.arguments?.getString("subjectName") ?: ""

                                val appModule = (LocalContext.current.applicationContext as App).appModule
                                val quizConfigViewModel: QuizConfigViewModel = viewModel(
                                    factory = object : ViewModelProvider.Factory {
                                        @Suppress("UNCHECKED_CAST")
                                        override fun <T : ViewModel> create(modelClass: Class<T>, extras: androidx.lifecycle.viewmodel.CreationExtras): T {
                                            val savedStateHandle = extras.createSavedStateHandle()
                                            savedStateHandle["userId"] = userId
                                            savedStateHandle["subjectId"] = subjectId
                                            savedStateHandle["subjectName"] = subjectName
                                            savedStateHandle["flowType"] = flowType
                                            return QuizConfigViewModel(
                                                appModule.subjectRepository,
                                                savedStateHandle
                                            ) as T
                                        }
                                    }
                                )

                                QuizConfigScreen(
                                    flowType = flowType,
                                    viewModel = quizConfigViewModel,
                                    onBackClick = {
                                        navController.popBackStack()
                                    },
                                    onStartTest = { subjectId, moduleId, count, random, _, immediate, flow ->
                                        navController.navigate("quiz_play/$subjectId/$moduleId/$count/$immediate/$random/$flow")
                                    }
                                )
                            }
                            composable(
                                route = "quiz_play/{subjectId}/{moduleId}/{count}/{immediate}/{isRandom}/{flowType}",
                                arguments = listOf(
                                    navArgument("subjectId") { type = NavType.IntType },
                                    navArgument("moduleId") { type = NavType.IntType },
                                    navArgument("count") { type = NavType.IntType },
                                    navArgument("immediate") { type = NavType.BoolType },
                                    navArgument("isRandom") { type = NavType.BoolType },
                                    navArgument("flowType") { type = NavType.StringType }
                                )
                            ) { entry ->
                                val parentEntry = remember(entry) {
                                    navController.getBackStackEntry("study/{userId}/{flowType}")
                                }
                                val userId = parentEntry.arguments?.getInt("userId") ?: 0
                                
                                val appModule = (LocalContext.current.applicationContext as App).appModule
                                val quizPlayViewModel: QuizPlayViewModel = viewModel(
                                    factory = object : ViewModelProvider.Factory {
                                        @Suppress("UNCHECKED_CAST")
                                        override fun <T : ViewModel> create(modelClass: Class<T>, extras: androidx.lifecycle.viewmodel.CreationExtras): T {
                                            val savedStateHandle = extras.createSavedStateHandle()
                                            savedStateHandle["userId"] = userId
                                            // The other arguments (subjectId, moduleId, count, immediate, isRandom, flowType) 
                                            // are already in the SavedStateHandle from the navigation entry
                                            return QuizPlayViewModel(
                                                appModule.subjectRepository,
                                                appModule.processQuizResultsUseCase,
                                                savedStateHandle
                                            ) as T
                                        }
                                    }
                                )
                                QuizPlayScreen(
                                    viewModel = quizPlayViewModel,
                                    onBackClick = {
                                        navController.popBackStack()
                                    },
                                    onNavigateToResults = { attempt ->
                                        // Set the attempt in the savedStateHandle of the destination BEFORE navigating
                                        // or just navigate and then set it.
                                        navController.navigate("quiz_results/$userId") {
                                            popUpTo("quiz_play/{subjectId}/{moduleId}/{count}/{immediate}/{isRandom}/{flowType}") {
                                                inclusive = true
                                            }
                                        }
                                        navController.currentBackStackEntry?.savedStateHandle?.set("attempt", attempt)
                                    }
                                )
                            }
                            composable(
                                route = "quiz_results/{userId}",
                                arguments = listOf(
                                    navArgument("userId") { type = NavType.IntType }
                                )
                            ) { entry ->
                                val currentUserId = entry.arguments?.getInt("userId") ?: 0
                                val appModule = (LocalContext.current.applicationContext as App).appModule
                                
                                val quizResultsViewModel: QuizResultsViewModel = viewModel(
                                    factory = object : ViewModelProvider.Factory {
                                        @Suppress("UNCHECKED_CAST")
                                        override fun <T : ViewModel> create(modelClass: Class<T>, extras: androidx.lifecycle.viewmodel.CreationExtras): T {
                                            // USE the entry's savedStateHandle instead of creating a new one
                                            // Or at least manually sync them.
                                            val savedStateHandle = extras.createSavedStateHandle()
                                            
                                            // Manually inject the attempt if it exists in the entry's handle
                                            val attempt = entry.savedStateHandle.get<Attempt>("attempt")
                                            savedStateHandle["attempt"] = attempt
                                            savedStateHandle["userId"] = currentUserId
                                            
                                            return QuizResultsViewModel(
                                                appModule.userRepository,
                                                savedStateHandle
                                            ) as T
                                        }
                                    }
                                )
                                
                                QuizResultsScreen(
                                    viewModel = quizResultsViewModel,
                                    onBackToConfigClick = {
                                        navController.popBackStack()
                                    },
                                    onNavigateToHome = {
                                        navController.navigate("home/$currentUserId") {
                                            popUpTo("home/$currentUserId") { inclusive = true }
                                        }
                                    }
                                )
                            }
                        }

                        composable(
                            route = "profile/{userId}",
                            arguments = listOf(navArgument("userId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
                            val appModule = (LocalContext.current.applicationContext as App).appModule
                            val profileViewModel: ProfileViewModel = viewModel(
                                factory = object : ViewModelProvider.Factory {
                                    @Suppress("UNCHECKED_CAST")
                                    override fun <T : ViewModel> create(modelClass: Class<T>, extras: androidx.lifecycle.viewmodel.CreationExtras): T {
                                        val savedStateHandle = extras.createSavedStateHandle()
                                        savedStateHandle["userId"] = userId
                                        return ProfileViewModel(appModule.userRepository, savedStateHandle) as T
                                    }
                                }
                            )
                            ProfileScreen(
                                viewModel = profileViewModel,
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onNavigateToInventory = {
                                    navController.navigate("inventory")
                                },
                                onNavigateToBanners = {
                                    navController.navigate("banners/$userId")
                                }
                            )
                        }

                        composable("inventory") {
                            InventoryScreen(
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        composable(
                            route = "banners/{userId}",
                            arguments = listOf(navArgument("userId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
                            val appModule = (LocalContext.current.applicationContext as App).appModule
                            val bannersViewModel: BannersViewModel = viewModel(
                                factory = object : ViewModelProvider.Factory {
                                    @Suppress("UNCHECKED_CAST")
                                    override fun <T : ViewModel> create(modelClass: Class<T>, extras: androidx.lifecycle.viewmodel.CreationExtras): T {
                                        val savedStateHandle = extras.createSavedStateHandle()
                                        savedStateHandle["userId"] = userId
                                        return BannersViewModel(
                                            appModule.bannerRepository,
                                            appModule.userRepository,
                                            savedStateHandle
                                        ) as T
                                    }
                                }
                            )
                            BannersScreen(
                                viewModel = bannersViewModel,
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
