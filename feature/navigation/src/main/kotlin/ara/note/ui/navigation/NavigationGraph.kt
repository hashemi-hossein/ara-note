package ara.note.ui.navigation

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
import ara.note.ui.navigation.NavScreen.Home
import ara.note.ui.navigation.NavScreen.NoteDetail
import ara.note.ui.navigation.NavScreen.NotebooksList
import ara.note.ui.navigation.NavScreen.Settings
import ara.note.ui.screen.home.HomeScreen
import ara.note.ui.screen.home.HomeViewModel
import ara.note.ui.screen.notebookslist.NotebooksListScreen
import ara.note.ui.screen.notebookslist.NotebooksListViewModel
import ara.note.ui.screen.notedetail.NoteDetailScreen
import ara.note.ui.screen.notedetail.NoteDetailViewModel
import ara.note.ui.screen.settings.SettingsScreen
import ara.note.ui.screen.settings.SettingsViewModel
import ara.note.util.ANIMATION_DURATION
import ara.note.util.DEFAULT_NOTEBOOK_ID
import ara.note.util.INVALID_NOTE_ID
import ara.note.util.NAV_ARGUMENT_NOTEBOOK_ID
import ara.note.util.NAV_ARGUMENT_NOTE_ID
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun NavigationGraph(
    navController: NavHostController = rememberAnimatedNavController(),
    startDestination: String = Home.route,
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
            composable(route = Home.route) {
                val viewModel = hiltViewModel<HomeViewModel>()
                HomeScreen(
                    viewModel = viewModel,
                    navigateToNoteDetailScreen = { noteId, notebookId ->
                        navController.navigate(NoteDetail(noteId, notebookId).route)
                    },
                    navigateToSettingsScreen = { navController.navigate(Settings.route) },
                    navigateToNotebooksScreen = { navController.navigate(NotebooksList.route) },
                )
            }

            composable(
                route = NoteDetail().route,
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

            composable(route = Settings.route) {
                val viewModel = hiltViewModel<SettingsViewModel>()
                SettingsScreen(viewModel = viewModel) {
                    navController.navigateUp()
                }
            }

            composable(route = NotebooksList.route) {
                val viewModel = hiltViewModel<NotebooksListViewModel>()
                NotebooksListScreen(viewModel = viewModel) {
                    navController.navigateUp()
                }
            }
        }
    }
}
