package com.arygm.quickfix.ui.uiMode.workerMode

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.zIndex
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.offline.small.PreferencesViewModelUserProfile
import com.arygm.quickfix.model.switchModes.ModeViewModel
import com.arygm.quickfix.ui.elements.QuickFixOfflineBar
import com.arygm.quickfix.ui.navigation.BottomNavigationMenu
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.navigation.getBottomBarIdUser
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.profile.AccountConfigurationScreen
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.profile.WorkerProfileScreen
import com.arygm.quickfix.ui.uiMode.appContentUI.workerMode.announcements.AnnouncementsScreen
import com.arygm.quickfix.ui.uiMode.appContentUI.workerMode.messages.MessagesScreen
import com.arygm.quickfix.ui.uiMode.workerMode.home.HomeScreen
import com.arygm.quickfix.ui.uiMode.workerMode.navigation.WORKER_TOP_LEVEL_DESTINATIONS
import com.arygm.quickfix.ui.uiMode.workerMode.navigation.WorkerRoute
import com.arygm.quickfix.ui.uiMode.workerMode.navigation.WorkerScreen
import com.arygm.quickfix.ui.uiMode.workerMode.navigation.getBottomBarIdWorker
import kotlinx.coroutines.delay

@Composable
fun WorkerModeNavGraph(
    modeViewModel: ModeViewModel,
    isOffline: Boolean,
    appContentNavigationActions: NavigationActions,
    preferencesViewModel: PreferencesViewModel,
    accountViewModel: AccountViewModel,
    rootMainNavigationActions: NavigationActions,
    userPreferencesViewModel: PreferencesViewModelUserProfile
) {
  val workerNavController = rememberNavController()
  val workerNavigationActions = remember { NavigationActions(workerNavController) }
  var currentScreen by remember { mutableStateOf<String?>(null) }
  val shouldShowBottomBar by remember {
    derivedStateOf { currentScreen?.let { it != WorkerScreen.ACCOUNT_CONFIGURATION } ?: true }
  }
  val startDestination by modeViewModel.onSwitchStartDestWorker.collectAsState()
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
  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
        topBar = { QuickFixOfflineBar(isVisible = isOffline) },
        modifier = Modifier.fillMaxSize()) { innerPadding ->
          // Main content area
          Box(
              modifier = Modifier.fillMaxSize().padding(innerPadding) // Scaffold padding
              ) {
                // NavHost for worker routes
                NavHost(
                    navController = workerNavigationActions.navController,
                    startDestination = startDestination,
                    modifier = Modifier.fillMaxSize()) {
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
                        ProfileNavHost(
                            onScreenChange = { currentScreen = it },
                            appContentNavigationActions,
                            preferencesViewModel,
                            modeViewModel,
                            accountViewModel,
                            workerNavigationActions,
                            rootMainNavigationActions,
                            userPreferencesViewModel)
                      }
                    }

                // Bottom navigation bar at the bottom
                AnimatedVisibility(
                    visible = showBottomBar,
                    enter = slideInVertically { fullHeight -> fullHeight }, // Slide in from bottom
                    exit = slideOutVertically { fullHeight -> fullHeight }, // Slide out to bottom
                    modifier =
                        Modifier.align(Alignment.BottomCenter) // Align at bottom of this parent Box
                            .zIndex(1f)
                            .testTag("BNM")) {
                      BottomNavigationMenu(
                          onTabSelect = { selectedDestination ->
                            workerNavigationActions.navigateTo(selectedDestination)
                          },
                          navigationActions = workerNavigationActions,
                          tabList = WORKER_TOP_LEVEL_DESTINATIONS,
                          getBottomBarId = getBottomBarIdWorker)
                    }
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
    composable(WorkerScreen.ANNOUNCEMENT) { AnnouncementsScreen() }
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
  NavHost(navController = homeNavController, startDestination = WorkerScreen.HOME) {
    composable(WorkerScreen.HOME) { HomeScreen() }
  }
}

@Composable
fun ProfileNavHost(
    onScreenChange: (String) -> Unit,
    appContentNavigationActions: NavigationActions,
    preferencesViewModel: PreferencesViewModel,
    modeViewModel: ModeViewModel,
    accountViewModel: AccountViewModel,
    workerNavigationActions: NavigationActions,
    rootMainNavigationActions: NavigationActions,
    userPreferencesViewModel: PreferencesViewModelUserProfile
) {
  val profileNavController = rememberNavController()
  val profileNavigationActions = remember { NavigationActions(profileNavController) }

  LaunchedEffect(profileNavigationActions.currentScreen) {
    onScreenChange(profileNavigationActions.currentScreen)
  }
  NavHost(navController = profileNavController, startDestination = WorkerScreen.PROFILE) {
    composable(WorkerScreen.PROFILE) {
      WorkerProfileScreen(
          workerNavigationActions,
          profileNavigationActions,
          rootMainNavigationActions,
          preferencesViewModel,
          userPreferencesViewModel,
          appContentNavigationActions,
          modeViewModel)
    }
    composable(WorkerScreen.ACCOUNT_CONFIGURATION) {
      AccountConfigurationScreen(profileNavigationActions, accountViewModel, preferencesViewModel)
    }
  }
}
