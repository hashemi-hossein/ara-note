package com.ara.aranote.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.ara.aranote.ui.screen.home.HomeScreen
import com.ara.aranote.ui.screen.home.HomeViewModel
import com.ara.aranote.ui.screen.notebookslist.NotebooksListScreen
import com.ara.aranote.ui.screen.notebookslist.NotebooksListViewModel
import ara.note.ui.screen.notedetail.NoteDetailScreen
import ara.note.ui.screen.notedetail.NoteDetailViewModel
import ara.note.ui.screen.settings.SettingsScreen
import ara.note.ui.screen.settings.SettingsViewModel
import com.ara.aranote.util.ANIMATION_DURATION
import com.ara.aranote.util.DEFAULT_NOTEBOOK_ID
import com.ara.aranote.util.INVALID_NOTE_ID
import com.ara.aranote.util.NAV_ARGUMENT_NOTEBOOK_ID
import com.ara.aranote.util.NAV_ARGUMENT_NOTE_ID
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavigationGraph(
    navController: NavHostController = rememberAnimatedNavController(),
    startDestination: String = NavScreen.Home.route,
) {
    BoxWithConstraints {
        val boxWith = constraints.maxWidth / 2

        AnimatedNavHost(
            navController = navController,
            startDestination = startDestination,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { boxWith },
                    animationSpec = tween(ANIMATION_DURATION),
                ).plus(fadeIn(animationSpec = tween(ANIMATION_DURATION)))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { boxWith },
                    animationSpec = tween(ANIMATION_DURATION),
                ).plus(fadeOut(animationSpec = tween(ANIMATION_DURATION)))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -boxWith },
                    animationSpec = tween(ANIMATION_DURATION),
                ).plus(fadeOut(animationSpec = tween(ANIMATION_DURATION)))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -boxWith },
                    animationSpec = tween(ANIMATION_DURATION),
                ).plus(fadeIn(animationSpec = tween(ANIMATION_DURATION)))
            },
        ) {
            composable(route = NavScreen.Home.route) {
                val viewModel = hiltViewModel<HomeViewModel>()
                HomeScreen(
                    viewModel = viewModel,
                    navigateToNoteDetailScreen = { noteId, notebookId ->
                        navController.navigate(NavScreen.NoteDetail(noteId, notebookId).route)
                    },
                    navigateToSettingsScreen = { navController.navigate(NavScreen.Settings.route) },
                    navigateToNotebooksScreen = { navController.navigate(NavScreen.NotebooksList.route) },
                )
            }

            composable(
                route = NavScreen.NoteDetail().route,
                arguments = listOf(
                    navArgument(NAV_ARGUMENT_NOTE_ID) {
                        type = NavType.IntType
                        defaultValue = INVALID_NOTE_ID
                    },
                    navArgument(NAV_ARGUMENT_NOTEBOOK_ID) {
                        type = NavType.IntType
                        defaultValue = DEFAULT_NOTEBOOK_ID
                    },
                ),
            ) {
                val viewModel = hiltViewModel<NoteDetailViewModel>()
                NoteDetailScreen(viewModel = viewModel) {
                    navController.navigateUp()
                }
            }

            composable(route = NavScreen.Settings.route) {
                val viewModel = hiltViewModel<SettingsViewModel>()
                SettingsScreen(viewModel = viewModel) {
                    navController.navigateUp()
                }
            }

            composable(route = NavScreen.NotebooksList.route) {
                val viewModel = hiltViewModel<NotebooksListViewModel>()
                NotebooksListScreen(viewModel = viewModel) {
                    navController.navigateUp()
                }
            }
        }
    }
}
