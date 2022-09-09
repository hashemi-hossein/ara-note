package com.ara.aranote.ui.navigation

sealed class NavScreen(val route: String) {

    object Home : NavScreen("Home")
    object NoteDetail : NavScreen("NoteDetail")
    object NotebooksList : NavScreen("NotebooksList")
    object Settings : NavScreen("Settings")
}
