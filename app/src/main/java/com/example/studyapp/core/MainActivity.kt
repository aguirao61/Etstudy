package com.example.studyapp.core

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.studyapp.core.presentation.screen.auth_screen.AuthScreen
import com.example.studyapp.core.presentation.screen.auth_screen.AuthViewModel
import com.example.studyapp.core.presentation.screen.home_screen.StudyHomeScreen
import com.example.studyapp.core.presentation.screen.home_screen.HomeViewModel
import com.example.studyapp.core.presentation.ui.theme.StudyAppTheme
import com.example.studyapp.questions.domain.models.Attempt
import com.example.studyapp.questions.presentation.quiz_play_screen.QuizPlayScreen
import com.example.studyapp.questions.presentation.quiz_play_screen.QuizPlayViewModel
import com.example.studyapp.questions.presentation.quiz_results_screen.QuizResultsScreen
import com.example.studyapp.questions.presentation.quiz_results_screen.QuizResultsViewModel
import com.example.studyapp.questions.presentation.subject_screen.SubjectsScreen
import com.example.studyapp.questions.presentation.subject_screen.SubjectsViewModel
import com.example.studyapp.questions.presentation.quiz_config_screen.QuizConfigScreen
import com.example.studyapp.questions.presentation.quiz_config_screen.QuizConfigViewModel
import com.example.studyapp.user_profile.domain.models.ThemeMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val appModule = (LocalContext.current.applicationContext as App).appModule
            val navController = rememberNavController()

            // Función de navegación segura para los botones de volver de la UI
            val safePopBackStack = {
                val currentEntry = navController.currentBackStackEntry
                val previousEntry = navController.previousBackStackEntry
                // Solo hacemos pop si la pantalla actual está activa y hay una pantalla a la que volver
                // Esto evita que clics rápidos cierren la pantalla principal (Home)
                if (currentEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED && previousEntry != null) {
                    navController.popBackStack()
                }
            }

            // Observe the current user's theme mode
            val navBackStackEntry by navController.currentBackStackEntryFlow.collectAsState(initial = null)
            val userId = navBackStackEntry?.arguments?.getInt("userId") ?: 0
            
            val userState by appModule.userRepository.getUserFlow(userId).collectAsState(initial = null)
            val themeMode = userState?.selectedTheme ?: ThemeMode.SYSTEM
            
            val darkTheme = when(themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            StudyAppTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(navController = navController, startDestination = "auth") {
                        composable("auth") {
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
                                onNavigateToHome = { id ->
                                    navController.navigate("home/$id") {
                                        popUpTo("auth") { inclusive = true }
                                    }
                                }
                            )
                        }
                        
                        composable(
                            route = "home/{userId}",
                            arguments = listOf(navArgument("userId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val currentUserId = backStackEntry.arguments?.getInt("userId") ?: 0
                            val homeViewModel: HomeViewModel = viewModel(
                                factory = object : ViewModelProvider.Factory {
                                    @Suppress("UNCHECKED_CAST")
                                    override fun <T : ViewModel> create(modelClass: Class<T>, extras: androidx.lifecycle.viewmodel.CreationExtras): T {
                                        val savedStateHandle = extras.createSavedStateHandle()
                                        savedStateHandle["userId"] = currentUserId
                                        return HomeViewModel(appModule.userRepository, savedStateHandle) as T
                                    }
                                }
                            )
                            StudyHomeScreen(
                                viewModel = homeViewModel,
                                onNavigateToStudy = { flow ->
                                    navController.navigate("study/$currentUserId/${flow.name}")
                                },
                                onNavigateToProfile = {
                                    navController.navigate("profile/$currentUserId")
                                },
                                onNavigateToCommunity = {
                                    navController.navigate("community/$currentUserId")
                                },
                                onNavigateToSettings = {
                                    navController.navigate("settings/$currentUserId")
                                }
                            )
                        }

                        composable(
                            route = "community/{userId}",
                            arguments = listOf(navArgument("userId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val currentUserId = backStackEntry.arguments?.getInt("userId") ?: 0
                            val communityViewModel: com.example.studyapp.user_profile.presentation.screens.community_screen.CommunityViewModel = viewModel(
                                factory = object : ViewModelProvider.Factory {
                                    @Suppress("UNCHECKED_CAST")
                                    override fun <T : ViewModel> create(modelClass: Class<T>, extras: androidx.lifecycle.viewmodel.CreationExtras): T {
                                        val savedStateHandle = extras.createSavedStateHandle()
                                        savedStateHandle["userId"] = currentUserId
                                        return _root_ide_package_.com.example.studyapp.user_profile.presentation.screens.community_screen.CommunityViewModel(
                                            appModule.userRepository,
                                            savedStateHandle
                                        ) as T
                                    }
                                }
                            )
                            _root_ide_package_.com.example.studyapp.user_profile.presentation.screens.community_screen.CommunityScreen(
                                viewModel = communityViewModel,
                                onBackClick = { safePopBackStack() }
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
                                val currentUserId = parentEntry.arguments?.getInt("userId") ?: 0
                                val flowType = parentEntry.arguments?.getString("flowType") ?: "BROWSE"

                                val subjectsViewModel: SubjectsViewModel = viewModel(
                                    factory = object : ViewModelProvider.Factory {
                                        @Suppress("UNCHECKED_CAST")
                                        override fun <T : ViewModel> create(modelClass: Class<T>, extras: androidx.lifecycle.viewmodel.CreationExtras): T {
                                            val savedStateHandle = extras.createSavedStateHandle()
                                            savedStateHandle["userId"] = currentUserId
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
                                    onNavigateBack = { safePopBackStack() },
                                    onNavigateToQuizConfig = { id, name ->
                                        navController.navigate("quiz_config/$id/$name")
                                    },
                                    onNavigateToProfile = {
                                        navController.navigate("profile/$currentUserId")
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
                                val currentUserId = parentEntry.arguments?.getInt("userId") ?: 0
                                val flowType = parentEntry.arguments?.getString("flowType") ?: "TEST"
                                
                                val quizConfigViewModel: QuizConfigViewModel = viewModel(
                                    factory = object : ViewModelProvider.Factory {
                                        @Suppress("UNCHECKED_CAST")
                                        override fun <T : ViewModel> create(modelClass: Class<T>, extras: androidx.lifecycle.viewmodel.CreationExtras): T {
                                            val savedStateHandle = extras.createSavedStateHandle()
                                            savedStateHandle["userId"] = currentUserId
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
                                    onBackClick = { safePopBackStack() },
                                    onStartTest = { sId, mId, count, random, _, immediate, flow ->
                                        navController.navigate("quiz_play/$sId/$mId/$count/$immediate/$random/$flow")
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
                                val currentUserId = parentEntry.arguments?.getInt("userId") ?: 0
                                
                                val quizPlayViewModel: QuizPlayViewModel = viewModel(
                                    factory = object : ViewModelProvider.Factory {
                                        @Suppress("UNCHECKED_CAST")
                                        override fun <T : ViewModel> create(modelClass: Class<T>, extras: androidx.lifecycle.viewmodel.CreationExtras): T {
                                            val savedStateHandle = extras.createSavedStateHandle()
                                            savedStateHandle["userId"] = currentUserId
                                            return QuizPlayViewModel(
                                                appModule.subjectRepository,
                                                appModule.gameEngine,
                                                savedStateHandle
                                            ) as T
                                        }
                                    }
                                )
                                QuizPlayScreen(
                                    viewModel = quizPlayViewModel,
                                    onBackClick = { safePopBackStack() },
                                    onNavigateToResults = { attempt ->
                                        navController.navigate("quiz_results/$currentUserId") {
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
                                arguments = listOf(navArgument("userId") { type = NavType.IntType })
                            ) { entry ->
                                val currentUserId = entry.arguments?.getInt("userId") ?: 0
                                
                                val quizResultsViewModel: QuizResultsViewModel = viewModel(
                                    factory = object : ViewModelProvider.Factory {
                                        @Suppress("UNCHECKED_CAST")
                                        override fun <T : ViewModel> create(modelClass: Class<T>, extras: androidx.lifecycle.viewmodel.CreationExtras): T {
                                            val savedStateHandle = extras.createSavedStateHandle()
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
                                    onBackToConfigClick = { safePopBackStack() },
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
                            val currentUserId = backStackEntry.arguments?.getInt("userId") ?: 0
                            val profileViewModel: com.example.studyapp.user_profile.presentation.screens.profile_screen.ProfileViewModel = viewModel(
                                factory = object : ViewModelProvider.Factory {
                                    @Suppress("UNCHECKED_CAST")
                                    override fun <T : ViewModel> create(modelClass: Class<T>, extras: androidx.lifecycle.viewmodel.CreationExtras): T {
                                        val savedStateHandle = extras.createSavedStateHandle()
                                        savedStateHandle["userId"] = currentUserId
                                        return _root_ide_package_.com.example.studyapp.user_profile.presentation.screens.profile_screen.ProfileViewModel(
                                            appModule.userRepository,
                                            appModule.iconRepository,
                                            savedStateHandle
                                        ) as T
                                    }
                                }
                            )
                            _root_ide_package_.com.example.studyapp.user_profile.presentation.screens.profile_screen.ProfileScreen(
                                viewModel = profileViewModel,
                                onBackClick = { safePopBackStack() },
                                onNavigateToInventory = {
                                    navController.navigate("inventory/$currentUserId")
                                },
                                onNavigateToBanners = {
                                    navController.navigate("banners/$currentUserId")
                                },
                                onNavigateToSettings = {
                                    navController.navigate("settings/$currentUserId")
                                }
                            )
                        }

                        composable(
                            route = "settings/{userId}",
                            arguments = listOf(navArgument("userId") { type = NavType.IntType })
                        ) { entry ->
                            val currentUserId = entry.arguments?.getInt("userId") ?: 0
                            val settingsViewModel: com.example.studyapp.user_profile.presentation.screens.settings_screen.SettingsViewModel = viewModel(
                                factory = object : ViewModelProvider.Factory {
                                    @Suppress("UNCHECKED_CAST")
                                    override fun <T : ViewModel> create(modelClass: Class<T>, extras: androidx.lifecycle.viewmodel.CreationExtras): T {
                                        val savedStateHandle = extras.createSavedStateHandle()
                                        savedStateHandle["userId"] = currentUserId
                                        return _root_ide_package_.com.example.studyapp.user_profile.presentation.screens.settings_screen.SettingsViewModel(
                                            appModule.userRepository,
                                            savedStateHandle
                                        ) as T
                                    }
                                }
                            )
                            _root_ide_package_.com.example.studyapp.user_profile.presentation.screens.settings_screen.SettingsScreen(
                                viewModel = settingsViewModel,
                                onBackClick = { safePopBackStack() },
                                onLogoutClick = {
                                    navController.navigate("auth") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(
                            route = "inventory/{userId}",
                            arguments = listOf(navArgument("userId") { type = NavType.IntType })
                        ) { entry ->
                            val currentUserId = entry.arguments?.getInt("userId") ?: 0
                            val inventoryViewModel: com.example.studyapp.user_profile.presentation.screens.inventory_screen.InventoryViewModel = viewModel(
                                factory = object : ViewModelProvider.Factory {
                                    @Suppress("UNCHECKED_CAST")
                                    override fun <T : ViewModel> create(modelClass: Class<T>, extras: androidx.lifecycle.viewmodel.CreationExtras): T {
                                        val savedStateHandle = extras.createSavedStateHandle()
                                        savedStateHandle["userId"] = currentUserId
                                        return _root_ide_package_.com.example.studyapp.user_profile.presentation.screens.inventory_screen.InventoryViewModel(
                                            appModule.iconRepository,
                                            appModule.userRepository,
                                            savedStateHandle
                                        ) as T
                                    }
                                }
                            )
                            _root_ide_package_.com.example.studyapp.user_profile.presentation.screens.inventory_screen.InventoryScreen(
                                viewModel = inventoryViewModel,
                                onBackClick = { safePopBackStack() }
                            )
                        }

                        composable(
                            route = "banners/{userId}",
                            arguments = listOf(navArgument("userId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val currentUserId = backStackEntry.arguments?.getInt("userId") ?: 0
                            val bannersViewModel: com.example.studyapp.user_profile.presentation.screens.banner_screen.BannersViewModel = viewModel(
                                factory = object : ViewModelProvider.Factory {
                                    @Suppress("UNCHECKED_CAST")
                                    override fun <T : ViewModel> create(modelClass: Class<T>, extras: androidx.lifecycle.viewmodel.CreationExtras): T {
                                        val savedStateHandle = extras.createSavedStateHandle()
                                        savedStateHandle["userId"] = currentUserId
                                        return _root_ide_package_.com.example.studyapp.user_profile.presentation.screens.banner_screen.BannersViewModel(
                                            appModule.bannerRepository,
                                            appModule.userRepository,
                                            savedStateHandle
                                        ) as T
                                    }
                                }
                            )
                            _root_ide_package_.com.example.studyapp.user_profile.presentation.screens.banner_screen.BannersScreen(
                                viewModel = bannersViewModel,
                                onBackClick = { safePopBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
