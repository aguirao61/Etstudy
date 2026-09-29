package com.example.studyapp.core

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
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
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavOptionsBuilder
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
import com.example.studyapp.user_profile.presentation.screens.community_screen.CommunityScreen
import com.example.studyapp.user_profile.presentation.screens.community_screen.CommunityViewModel
import com.example.studyapp.user_profile.presentation.screens.profile_screen.ProfileScreen
import com.example.studyapp.user_profile.presentation.screens.profile_screen.ProfileViewModel
import com.example.studyapp.user_profile.presentation.screens.settings_screen.SettingsScreen
import com.example.studyapp.user_profile.presentation.screens.settings_screen.SettingsViewModel
import com.example.studyapp.user_profile.presentation.screens.inventory_screen.InventoryScreen
import com.example.studyapp.user_profile.presentation.screens.inventory_screen.InventoryViewModel
import com.example.studyapp.user_profile.presentation.screens.banner_screen.BannersScreen
import com.example.studyapp.user_profile.presentation.screens.banner_screen.BannersViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val appModule = (LocalContext.current.applicationContext as App).appModule
            val navController = rememberNavController()

            // Safe navigation function for returning to previous screen
            val safePopBackStack = {
                val currentEntry = navController.currentBackStackEntry
                val previousEntry = navController.previousBackStackEntry
                if (currentEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED && previousEntry != null) {
                    navController.popBackStack()
                }
            }

            // Safe navigation function for navigating to a new screen
            val safeNavigate: (String, (NavOptionsBuilder.() -> Unit)?) -> Unit = { route, builder ->
                val currentEntry = navController.currentBackStackEntry
                if (currentEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                    if (builder != null) {
                        navController.navigate(route, builder)
                    } else {
                        navController.navigate(route)
                    }
                }
            }

            // Observe the current user's theme mode across back stack entries
            val navBackStackEntry by navController.currentBackStackEntryFlow.collectAsState(initial = null)
            val userId = remember(navBackStackEntry) {
                var id = navBackStackEntry?.arguments?.getInt("userId") ?: 0
                if (id == 0) {
                    try {
                        val parentEntry = navController.getBackStackEntry("study/{userId}/{flowType}")
                        id = parentEntry.arguments?.getInt("userId") ?: 0
                    } catch (_: Exception) {}
                }
                id
            }
            
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
                    NavHost(
                        navController = navController,
                        startDestination = "auth",
                        // No animated transitions between screens
                        enterTransition = { EnterTransition.None },
                        exitTransition = { ExitTransition.None },
                        popEnterTransition = { EnterTransition.None },
                        popExitTransition = { ExitTransition.None }
                        ) {
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
                                    safeNavigate("home/$id") {
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
                                    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                                        val savedStateHandle = extras.createSavedStateHandle()
                                        savedStateHandle["userId"] = currentUserId
                                        return HomeViewModel(
                                            appModule.userRepository,
                                            appModule.dailyMissionRepository,
                                            savedStateHandle
                                        ) as T
                                    }
                                }
                            )
                            StudyHomeScreen(
                                viewModel = homeViewModel,
                                onNavigateToStudy = { flow ->
                                    safeNavigate("study/$currentUserId/${flow.name}", null)
                                },
                                onNavigateToProfile = {
                                    safeNavigate("profile/$currentUserId", null)
                                },
                                onNavigateToCommunity = {
                                    safeNavigate("community/$currentUserId", null)
                                },
                                onNavigateToSettings = {
                                    safeNavigate("settings/$currentUserId", null)
                                }
                            )
                        }

                        composable(
                            route = "community/{userId}",
                            arguments = listOf(navArgument("userId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val currentUserId = backStackEntry.arguments?.getInt("userId") ?: 0
                            val communityViewModel: CommunityViewModel = viewModel(
                                factory = object : ViewModelProvider.Factory {
                                    @Suppress("UNCHECKED_CAST")
                                    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                                        val savedStateHandle = extras.createSavedStateHandle()
                                        savedStateHandle["userId"] = currentUserId
                                        return CommunityViewModel(
                                            appModule.userRepository,
                                            savedStateHandle
                                        ) as T
                                    }
                                }
                            )
                            CommunityScreen(
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
                                        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
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
                                        safeNavigate("quiz_config/$id/$name", null)
                                    },
                                    onNavigateToProfile = {
                                        safeNavigate("profile/$currentUserId", null)
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
                                        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
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
                                    onStartTest = { sId, mId, count, random, timer, immediate, flow ->
                                        safeNavigate("quiz_play/$sId/$mId/$count/$immediate/$random/$timer/$flow", null)
                                    }
                                )
                            }
                            
                            composable(
                                route = "quiz_play/{subjectId}/{moduleId}/{count}/{immediate}/{isRandom}/{timerEnabled}/{flowType}",
                                arguments = listOf(
                                    navArgument("subjectId") { type = NavType.IntType },
                                    navArgument("moduleId") { type = NavType.IntType },
                                    navArgument("count") { type = NavType.IntType },
                                    navArgument("immediate") { type = NavType.BoolType },
                                    navArgument("isRandom") { type = NavType.BoolType },
                                    navArgument("timerEnabled") { type = NavType.BoolType },
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
                                        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
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
                                        safeNavigate("quiz_results/$currentUserId") {
                                            popUpTo("quiz_play/{subjectId}/{moduleId}/{count}/{immediate}/{isRandom}/{timerEnabled}/{flowType}") {
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
                                        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
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
                                        safeNavigate("home/$currentUserId") {
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
                            val profileViewModel: ProfileViewModel = viewModel(
                                factory = object : ViewModelProvider.Factory {
                                    @Suppress("UNCHECKED_CAST")
                                    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                                        val savedStateHandle = extras.createSavedStateHandle()
                                        savedStateHandle["userId"] = currentUserId
                                        return ProfileViewModel(
                                            appModule.userRepository,
                                            appModule.iconRepository,
                                            savedStateHandle
                                        ) as T
                                    }
                                }
                            )
                            ProfileScreen(
                                viewModel = profileViewModel,
                                onBackClick = { safePopBackStack() },
                                onNavigateToInventory = {
                                    safeNavigate("inventory/$currentUserId", null)
                                },
                                onNavigateToBanners = {
                                    safeNavigate("banners/$currentUserId", null)
                                },
                                onNavigateToSettings = {
                                    safeNavigate("settings/$currentUserId", null)
                                }
                            )
                        }

                        composable(
                            route = "settings/{userId}",
                            arguments = listOf(navArgument("userId") { type = NavType.IntType })
                        ) { entry ->
                            val currentUserId = entry.arguments?.getInt("userId") ?: 0
                            val settingsViewModel: SettingsViewModel = viewModel(
                                factory = object : ViewModelProvider.Factory {
                                    @Suppress("UNCHECKED_CAST")
                                    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                                        val savedStateHandle = extras.createSavedStateHandle()
                                        savedStateHandle["userId"] = currentUserId
                                        return SettingsViewModel(
                                            appModule.userRepository,
                                            savedStateHandle
                                        ) as T
                                    }
                                }
                            )
                            SettingsScreen(
                                viewModel = settingsViewModel,
                                onBackClick = { safePopBackStack() },
                                onLogoutClick = {
                                    safeNavigate("auth") {
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
                            val inventoryViewModel: InventoryViewModel = viewModel(
                                factory = object : ViewModelProvider.Factory {
                                    @Suppress("UNCHECKED_CAST")
                                    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                                        val savedStateHandle = extras.createSavedStateHandle()
                                        savedStateHandle["userId"] = currentUserId
                                        return InventoryViewModel(
                                            appModule.iconRepository,
                                            appModule.userRepository,
                                            savedStateHandle
                                        ) as T
                                    }
                                }
                            )
                            InventoryScreen(
                                viewModel = inventoryViewModel,
                                onBackClick = { safePopBackStack() }
                            )
                        }

                        composable(
                            route = "banners/{userId}",
                            arguments = listOf(navArgument("userId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val currentUserId = backStackEntry.arguments?.getInt("userId") ?: 0
                            val bannersViewModel: BannersViewModel = viewModel(
                                factory = object : ViewModelProvider.Factory {
                                    @Suppress("UNCHECKED_CAST")
                                    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                                        val savedStateHandle = extras.createSavedStateHandle()
                                        savedStateHandle["userId"] = currentUserId
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
                                onBackClick = { safePopBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
