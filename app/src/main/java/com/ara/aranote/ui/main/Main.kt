package com.ara.aranote.ui.main

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.navArgument
import com.ara.aranote.domain.viewmodels.HomeViewModel
import com.ara.aranote.domain.viewmodels.NoteDetailViewModel
import com.ara.aranote.ui.screens.HomeScreen
import com.ara.aranote.ui.screens.NoteDetailScreen
import com.ara.aranote.util.INVALID_NOTE_ID
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppMain(
    navController: NavHostController = rememberAnimatedNavController(),
    startDestination: String = NavScreen.Home.route,
) {
    BoxWithConstraints {
        val boxWith = constraints.maxWidth / 2

        AnimatedNavHost(
            navController = navController,
            startDestination = startDestination,
            enterTransition = { _, _ ->
                slideInHorizontally(
                    initialOffsetX = { boxWith },
                    animationSpec = tween(300),
                ).plus(fadeIn(animationSpec = tween(300)))
            },
            popExitTransition = { _, _ ->
                slideOutHorizontally(
                    targetOffsetX = { boxWith },
                    animationSpec = tween(300),
                ).plus(fadeOut(animationSpec = tween(300)))
            },
            exitTransition = { _, _ ->
                slideOutHorizontally(
                    targetOffsetX = { -boxWith },
                    animationSpec = tween(300),
                ).plus(fadeOut(animationSpec = tween(300)))
            },
            popEnterTransition = { _, _ ->
                slideInHorizontally(
                    initialOffsetX = { -boxWith },
                    animationSpec = tween(300),
                ).plus(fadeIn(animationSpec = tween(300)))
            }
        ) {

            composable(NavScreen.Home.route) {
                val viewModel = hiltViewModel<HomeViewModel>()
                HomeScreen(
                    viewModel = viewModel,
                ) {
                    navController.navigate(NavScreen.NoteDetail.route + "/" + it)
                }
            }

            composable(
                NavScreen.NoteDetail.route + "/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType }),
            ) { backStackEntry: NavBackStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: INVALID_NOTE_ID
                val viewModel = hiltViewModel<NoteDetailViewModel>()
                LaunchedEffect(true) {
                    viewModel.prepareNote(id)
                }
                NoteDetailScreen(
                    viewModel = viewModel,
                    id = id,
                ) {
                    navController.navigateUp()
                }
            }
        }
    }
}

sealed class NavScreen(val route: String) {

    object Home : NavScreen("Home")

    object NoteDetail : NavScreen("NoteDetail")
}
