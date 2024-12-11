package com.arygm.quickfix.ui.uiMode.workerMode

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.uiMode.workerMode.home.HomeScreen
import com.arygm.quickfix.ui.uiMode.workerMode.navigation.WorkerRoute

@Composable
fun WorkerModeNavGraph(
    onScreenChange: (String) -> Unit,
    workerNavigationActions: NavigationActions
) {

    LaunchedEffect(workerNavigationActions.currentScreen) {
        onScreenChange(workerNavigationActions.currentScreen)
    }

    NavHost(navController = workerNavigationActions.navController, startDestination = WorkerRoute.HOME) {
        composable(WorkerRoute.HOME) {
            HomeScreen(workerNavigationActions)
        }
    }
}