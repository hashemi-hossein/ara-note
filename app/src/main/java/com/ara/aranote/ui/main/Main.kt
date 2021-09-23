package com.ara.aranote.ui.main

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppMain(
    navController: NavHostController = rememberAnimatedNavController(),
    startDestination: String = NavScreen.Home.route,
) {

    AnimatedNavHost(
        navController = navController,
        startDestination = startDestination,
    ) {

        composable(NavScreen.Home.route) {
            Text(text = "")
        }
    }
}

sealed class NavScreen(val route: String) {

    object Home : NavScreen("Home")
}
