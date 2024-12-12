package com.arygm.quickfix.ui.uiMode.workerMode

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.account.LoggedInAccountViewModel
import com.arygm.quickfix.model.category.CategoryViewModel
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.locations.LocationViewModel
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.switchModes.ModeViewModel
import com.arygm.quickfix.ui.dashboard.DashboardScreen
import com.arygm.quickfix.ui.elements.QuickFixOfflineBar
import com.arygm.quickfix.ui.navigation.BottomNavigationMenu
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.uiMode.appContentUI.workerMode.announcements.AnnouncementsScreen
import com.arygm.quickfix.ui.uiMode.appContentUI.workerMode.messages.MessagesScreen
import com.arygm.quickfix.ui.uiMode.workerMode.home.HomeScreen
import com.arygm.quickfix.ui.uiMode.workerMode.navigation.WorkerRoute
import com.arygm.quickfix.ui.uiMode.workerMode.navigation.WorkerScreen
import com.arygm.quickfix.ui.userModeUI.navigation.UserScreen
import com.arygm.quickfix.ui.uiMode.appContentUI.workerMode.profile.ProfileScreen
import com.arygm.quickfix.ui.uiMode.workerMode.navigation.WORKER_TOP_LEVEL_DESTINATIONS
import com.arygm.quickfix.ui.uiMode.workerMode.navigation.getBottomBarIdWorker
import kotlinx.coroutines.delay


@Composable
fun WorkerModeNavGraph(
    workerNavigationActions: NavigationActions,
    modeViewModel: ModeViewModel,
    isOffline: Boolean,
) {
    var currentScreen by remember { mutableStateOf<String?>(null) }
    val shouldShowBottomBar by remember {
        derivedStateOf {
            true
        }
    }

    var showBottomBar by remember { mutableStateOf(false) }

    // Delay the appearance of the bottom bar
    LaunchedEffect(shouldShowBottomBar) {
        if (shouldShowBottomBar) {
            delay(200) // Adjust the delay duration (in milliseconds) as needed
            showBottomBar = true
        } else {
            showBottomBar = false
        }
    }
    Scaffold(
        topBar = { QuickFixOfflineBar(isVisible = isOffline) },
        bottomBar = {
            // Show BottomNavigationMenu only if the route is not part of the login/registration flow
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically { fullHeight -> fullHeight }, // Slide in from the bottom
                exit = slideOutVertically { fullHeight -> fullHeight }, // Slide out to the bottom
                modifier = Modifier.testTag("BNM")) {
                BottomNavigationMenu(
                    onTabSelect = { selectedDestination ->
                        // Use this block to navigate based on the selected tab
                        workerNavigationActions.navigateTo(selectedDestination)
                    },
                    navigationActions = workerNavigationActions,
                    tabList = WORKER_TOP_LEVEL_DESTINATIONS,
                    getBottomBarId = getBottomBarIdWorker)
            }
        }) { innerPadding ->

        NavHost(
            navController = workerNavigationActions.navController,
            startDestination = WorkerRoute.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(WorkerRoute.HOME) {
                HomeNavHost(onScreenChange = { currentScreen = it })
            }
            composable(WorkerRoute.MESSAGES) {
                MessagesNavHost(onScreenChange = { currentScreen = it })
            }
            composable(WorkerRoute.ANNOUNCEMENT) {
                AnnouncementsNavHost(onScreenChange = { currentScreen = it })
            }
            composable(WorkerRoute.PROFILE) {
                ProfileNavHost(onScreenChange = { currentScreen = it })
            }
        }
    }
}

@Composable
fun MessagesNavHost(onScreenChange: (String) -> Unit) {
    val dashboardNavController = rememberNavController()
    val navigationActions = remember { NavigationActions(dashboardNavController) }
    LaunchedEffect(navigationActions.currentScreen) {
        onScreenChange(navigationActions.currentScreen)
    }
    NavHost(navController = dashboardNavController, startDestination = WorkerScreen.MESSAGES) {
        composable(WorkerScreen.MESSAGES) { MessagesScreen() }
    }
}

@Composable
fun AnnouncementsNavHost(
    onScreenChange: (String) -> Unit,
) {
    val announcementsNavController = rememberNavController()
    val navigationActions = remember { NavigationActions(announcementsNavController) }
    LaunchedEffect(navigationActions.currentScreen) {
        onScreenChange(navigationActions.currentScreen)
    }
    NavHost(
        navController = announcementsNavController,
        startDestination = WorkerScreen.ANNOUNCEMENT,
    ) {
        composable(WorkerScreen.ANNOUNCEMENT) {
            AnnouncementsScreen()
        }
    }
}

@Composable
fun HomeNavHost(
    onScreenChange: (String) -> Unit = {},
) {
    val homeNavController = rememberNavController()
    val navigationActions = remember { NavigationActions(homeNavController) }

    LaunchedEffect(navigationActions.currentScreen) {
        onScreenChange(navigationActions.currentScreen)
    }
    NavHost(
        navController = homeNavController,
        startDestination = WorkerScreen.HOME
    ) {
        composable(WorkerScreen.HOME) { HomeScreen() }
    }
}

@Composable
fun ProfileNavHost(
    onScreenChange: (String) -> Unit,
) {
    val profileNavController = rememberNavController()
    val profileNavigationActions = remember { NavigationActions(profileNavController) }

    LaunchedEffect(profileNavigationActions.currentScreen) {
        onScreenChange(profileNavigationActions.currentScreen)
    }
    NavHost(navController = profileNavController, startDestination = WorkerScreen.PROFILE) {
        composable(WorkerScreen.PROFILE) {
            ProfileScreen()
        }
    }
}

