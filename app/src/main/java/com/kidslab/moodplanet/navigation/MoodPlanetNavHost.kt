package com.kidslab.moodplanet.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kidslab.moodplanet.audio.CalmVoiceRecorder
import com.kidslab.moodplanet.di.ServiceLocator
import com.kidslab.moodplanet.ui.achievements.AchievementsSettingsScreen
import com.kidslab.moodplanet.ui.achievements.AchievementsSettingsViewModel
import com.kidslab.moodplanet.ui.breathing.BreathingScreen
import com.kidslab.moodplanet.ui.breathing.BreathingViewModel
import com.kidslab.moodplanet.ui.calmvoice.CalmVoiceScreen
import com.kidslab.moodplanet.ui.calmvoice.CalmVoiceViewModel
import com.kidslab.moodplanet.ui.collection.ToolCollectionScreen
import com.kidslab.moodplanet.ui.collection.ToolCollectionViewModel
import com.kidslab.moodplanet.ui.feelings.HowDoIFeelScreen
import com.kidslab.moodplanet.ui.feelings.HowDoIFeelViewModel
import com.kidslab.moodplanet.ui.stories.StoriesListScreen
import com.kidslab.moodplanet.ui.stories.StoriesListViewModel
import com.kidslab.moodplanet.ui.stories.StoryDetailScreen
import com.kidslab.moodplanet.ui.stories.StoryDetailViewModel
import com.kidslab.moodplanet.ui.talktoadult.TalkToAdultScreen
import com.kidslab.moodplanet.ui.toolbox.ToolboxScreen
import com.kidslab.moodplanet.ui.toolbox.ToolboxViewModel
import com.kidslab.moodplanet.ui.trigger.WhatHappenedScreen
import com.kidslab.moodplanet.ui.trigger.WhatHappenedViewModel
import com.kidslab.moodplanet.ui.weekly.WeeklyPlanetScreen
import com.kidslab.moodplanet.ui.weekly.WeeklyPlanetViewModel
import com.kidslab.moodplanet.ui.welcome.WelcomeScreen

private fun Long.orNull(): Long? = if (this < 0) null else this

private data class HubDestination(
    val navigateRoute: String,
    val templateRoute: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

private val hubDestinations = listOf(
    HubDestination(Routes.toolbox(null), Routes.TOOLBOX, "Herramientas", Icons.Filled.Build),
    HubDestination(Routes.STORIES, Routes.STORIES, "Historias", Icons.Filled.AutoStories),
    HubDestination(Routes.WEEKLY_PLANET, Routes.WEEKLY_PLANET, "Semana", Icons.Filled.CalendarMonth),
    HubDestination(Routes.TOOL_COLLECTION, Routes.TOOL_COLLECTION, "Colección", Icons.Filled.Collections),
    HubDestination(Routes.ACHIEVEMENTS_SETTINGS, Routes.ACHIEVEMENTS_SETTINGS, "Logros", Icons.Filled.EmojiEvents)
)

/**
 * Rutas base (sin argumentos de consulta) que forman parte de la barra de
 * navegación inferior: Caja de herramientas, Historias, Planeta semanal,
 * Colección y Logros/Ajustes.
 */
private val hubBaseRoutes = setOf(
    Routes.TOOLBOX, Routes.STORIES, Routes.WEEKLY_PLANET, Routes.TOOL_COLLECTION, Routes.ACHIEVEMENTS_SETTINGS
)

@Composable
fun MoodPlanetNavHost(serviceLocator: ServiceLocator) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute in hubBaseRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    hubDestinations.forEach { destination ->
                        NavigationBarItem(
                            selected = currentRoute == destination.templateRoute,
                            onClick = {
                                if (currentRoute != destination.templateRoute) {
                                    navController.navigate(destination.navigateRoute) {
                                        popUpTo(Routes.TOOLBOX) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = { Icon(destination.icon, contentDescription = destination.label) },
                            label = { Text(destination.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.WELCOME,
            modifier = Modifier.padding(innerPadding)
        ) {

        composable(Routes.WELCOME) {
            WelcomeScreen(onStart = { navController.navigate(Routes.HOW_DO_I_FEEL) })
        }

        composable(Routes.HOW_DO_I_FEEL) {
            val vm: HowDoIFeelViewModel = viewModel(factory = viewModelFactory {
                initializer { HowDoIFeelViewModel(serviceLocator.emotionRepository) }
            })
            HowDoIFeelScreen(
                viewModel = vm,
                onContinue = { emotionTypeId, intensity ->
                    navController.navigate(Routes.whatHappened(emotionTypeId, intensity))
                }
            )
        }

        composable(
            route = Routes.WHAT_HAPPENED,
            arguments = listOf(
                navArgument("emotionTypeId") { type = NavType.IntType },
                navArgument("intensity") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val emotionTypeId = backStackEntry.arguments?.getInt("emotionTypeId") ?: 0
            val intensity = backStackEntry.arguments?.getInt("intensity") ?: 1
            val vm: WhatHappenedViewModel = viewModel(factory = viewModelFactory {
                initializer {
                    WhatHappenedViewModel(
                        serviceLocator.emotionRepository,
                        serviceLocator.badgeRepository,
                        emotionTypeId,
                        intensity
                    )
                }
            })
            WhatHappenedScreen(
                viewModel = vm,
                onSaved = { entryId, _ ->
                    navController.navigate(Routes.toolbox(entryId)) {
                        popUpTo(Routes.HOW_DO_I_FEEL) { inclusive = true }
                    }
                },
                onSkip = {
                    navController.navigate(Routes.toolbox(null)) {
                        popUpTo(Routes.HOW_DO_I_FEEL) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Routes.TOOLBOX,
            arguments = listOf(navArgument("entryId") { type = NavType.LongType; defaultValue = -1L })
        ) { backStackEntry ->
            val entryId = (backStackEntry.arguments?.getLong("entryId") ?: -1L).orNull()
            val vm: ToolboxViewModel = viewModel(factory = viewModelFactory {
                initializer {
                    ToolboxViewModel(serviceLocator.toolRepository, serviceLocator.badgeRepository, entryId)
                }
            })
            ToolboxScreen(
                viewModel = vm,
                onOpenBreathing = { navController.navigate(Routes.breathing(entryId)) },
                onOpenCalmVoice = { navController.navigate(Routes.calmVoice(entryId)) },
                onOpenTalkToAdult = { navController.navigate(Routes.TALK_TO_ADULT) },
                onToolCompleted = { _, _ -> }
            )
        }

        composable(
            route = Routes.BREATHING,
            arguments = listOf(navArgument("entryId") { type = NavType.LongType; defaultValue = -1L })
        ) { backStackEntry ->
            val entryId = (backStackEntry.arguments?.getLong("entryId") ?: -1L).orNull()
            val vm: BreathingViewModel = viewModel(factory = viewModelFactory {
                initializer {
                    BreathingViewModel(serviceLocator.toolRepository, serviceLocator.badgeRepository, entryId)
                }
            })
            BreathingScreen(viewModel = vm, onFinished = { navController.popBackStack() })
        }

        composable(
            route = Routes.CALM_VOICE,
            arguments = listOf(navArgument("entryId") { type = NavType.LongType; defaultValue = -1L })
        ) { backStackEntry ->
            val entryId = (backStackEntry.arguments?.getLong("entryId") ?: -1L).orNull()
            val vm: CalmVoiceViewModel = viewModel(factory = viewModelFactory {
                initializer {
                    CalmVoiceViewModel(
                        CalmVoiceRecorder(),
                        serviceLocator.toolRepository,
                        serviceLocator.badgeRepository,
                        entryId
                    )
                }
            })
            CalmVoiceScreen(viewModel = vm, onFinished = { navController.popBackStack() })
        }

        composable(Routes.TALK_TO_ADULT) {
            TalkToAdultScreen(onClose = { navController.popBackStack() })
        }

        composable(Routes.STORIES) {
            val vm: StoriesListViewModel = viewModel(factory = viewModelFactory {
                initializer { StoriesListViewModel(serviceLocator.storyRepository) }
            })
            StoriesListScreen(
                viewModel = vm,
                onOpenStory = { storyId -> navController.navigate(Routes.storyDetail(storyId)) }
            )
        }

        composable(
            route = Routes.STORY_DETAIL,
            arguments = listOf(navArgument("storyId") { type = NavType.IntType })
        ) { backStackEntry ->
            val storyId = backStackEntry.arguments?.getInt("storyId") ?: 1
            val vm: StoryDetailViewModel = viewModel(factory = viewModelFactory {
                initializer {
                    StoryDetailViewModel(serviceLocator.storyRepository, serviceLocator.badgeRepository, storyId)
                }
            })
            StoryDetailScreen(viewModel = vm, onFinished = { navController.popBackStack() })
        }

        composable(Routes.WEEKLY_PLANET) {
            val vm: WeeklyPlanetViewModel = viewModel(factory = viewModelFactory {
                initializer {
                    WeeklyPlanetViewModel(serviceLocator.emotionRepository, serviceLocator.toolRepository)
                }
            })
            WeeklyPlanetScreen(viewModel = vm)
        }

        composable(Routes.TOOL_COLLECTION) {
            val vm: ToolCollectionViewModel = viewModel(factory = viewModelFactory {
                initializer { ToolCollectionViewModel(serviceLocator.toolRepository) }
            })
            ToolCollectionScreen(viewModel = vm)
        }

        composable(Routes.ACHIEVEMENTS_SETTINGS) {
            val vm: AchievementsSettingsViewModel = viewModel(factory = viewModelFactory {
                initializer {
                    AchievementsSettingsViewModel(serviceLocator.badgeRepository, serviceLocator.userRepository)
                }
            })
            AchievementsSettingsScreen(viewModel = vm)
        }
        }
    }
}
