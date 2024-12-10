package com.arygm.quickfix.ui.uiMode.workerMode

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.uiMode.workerMode.home.HomeScreen
import com.arygm.quickfix.ui.uiMode.workerMode.navigation.WorkerScreen

@Composable
fun WorkerModeNavGraph(
    onScreenChange: (String) -> Unit,
) {
    val rootNavControllerWorkerMode = rememberNavController()
    val navigationActions = NavigationActions(rootNavControllerWorkerMode)

    LaunchedEffect(navigationActions.currentScreen) {
        onScreenChange(navigationActions.currentScreen)
    }

    NavHost(navController = rootNavControllerWorkerMode, startDestination = WorkerScreen.HOME) {
        composable(WorkerScreen.HOME) {
            HomeScreen(navigationActions)
        }
    }
}