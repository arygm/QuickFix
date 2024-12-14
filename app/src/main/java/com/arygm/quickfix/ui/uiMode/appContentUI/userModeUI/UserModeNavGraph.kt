package com.arygm.quickfix.ui.userModeUI

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.account.LoggedInAccountViewModel
import com.arygm.quickfix.model.category.CategoryViewModel
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.locations.LocationViewModel
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.offline.small.PreferencesViewModelUserProfile
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.search.AnnouncementViewModel
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.model.switchModes.ModeViewModel
import com.arygm.quickfix.ui.camera.QuickFixDisplayImages
import com.arygm.quickfix.ui.dashboard.DashboardScreen
import com.arygm.quickfix.ui.elements.LocationSearchCustomScreen
import com.arygm.quickfix.ui.elements.QuickFixOfflineBar
import com.arygm.quickfix.ui.home.FakeMessageScreen
import com.arygm.quickfix.ui.home.HomeScreen
import com.arygm.quickfix.ui.navigation.BottomNavigationMenu
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.profile.AccountConfigurationScreen
import com.arygm.quickfix.ui.profile.becomeWorker.BusinessScreen
import com.arygm.quickfix.ui.search.QuickFixFinderScreen
import com.arygm.quickfix.ui.search.SearchWorkerResult
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.profile.ProfileScreen
import com.arygm.quickfix.ui.userModeUI.navigation.USER_TOP_LEVEL_DESTINATIONS
import com.arygm.quickfix.ui.userModeUI.navigation.UserRoute
import com.arygm.quickfix.ui.userModeUI.navigation.UserScreen
import com.arygm.quickfix.ui.userModeUI.navigation.getBottomBarIdUser
import kotlinx.coroutines.delay

@Composable
fun UserModeNavHost(
    testBitmapPP: Bitmap?,
    testLocation: Location = Location(),
    modeViewModel: ModeViewModel,
    userViewModel: ProfileViewModel,
    workerViewModel: ProfileViewModel,
    accountViewModel: AccountViewModel,
    categoryViewModel: CategoryViewModel,
    locationViewModel: LocationViewModel,
    preferencesViewModel: PreferencesViewModel,
    rootMainNavigationActions: NavigationActions,
    userPreferencesViewModel: PreferencesViewModelUserProfile,
    appContentNavigationActions: NavigationActions,
    isOffline: Boolean
) {
  val userNavController = rememberNavController()
  val userNavigationActions = remember { NavigationActions(userNavController) }

  val loggedInAccountViewModel: LoggedInAccountViewModel =
      viewModel(factory = LoggedInAccountViewModel.Factory)
  val searchViewModel: SearchViewModel = viewModel(factory = SearchViewModel.Factory)
  val announcementViewModel: AnnouncementViewModel =
      viewModel(factory = AnnouncementViewModel.Factory)

  // Initialized here because needed for the bottom bar

  val isUser = true // TODO: This variable needs to get its value after the authentication
  var currentScreen by remember { mutableStateOf<String?>(null) }
  val shouldShowBottomBar by remember {
    derivedStateOf {
      currentScreen?.let {
        it != UserScreen.DISPLAY_UPLOADED_IMAGES && it != UserScreen.SEARCH_LOCATION
      } ?: true &&
          currentScreen?.let {
            it != UserScreen.ACCOUNT_CONFIGURATION && it != UserScreen.TO_WORKER
          } ?: true
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
                    userNavigationActions.navigateTo(selectedDestination)
                  },
                  navigationActions = userNavigationActions,
                  tabList = USER_TOP_LEVEL_DESTINATIONS,
                  getBottomBarId = getBottomBarIdUser)
            }
      }) { innerPadding ->
        NavHost(
            navController = userNavigationActions.navController,
            startDestination = UserRoute.HOME,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
              // You can change whatever you want for transitions
              EnterTransition.None
            },
            exitTransition = {
              // You can change whatever you want for transitions
              ExitTransition.None
            }) {
              composable(UserRoute.HOME) {
                HomeNavHost(
                    onScreenChange = {
                      currentScreen = it
                    }) // , loggedInAccountViewModel, chatViewModel)
              }

              composable(UserRoute.SEARCH) {
                SearchNavHost(
                    isUser,
                    userNavigationActions,
                    searchViewModel,
                    userViewModel,
                    loggedInAccountViewModel,
                    accountViewModel,
                    announcementViewModel,
                    onScreenChange = { currentScreen = it },
                    categoryViewModel,
                    preferencesViewModel,
                    locationViewModel)
              }

              composable(UserRoute.DASHBOARD) {
                DashBoardNavHost(onScreenChange = { currentScreen = it })
              }

              composable(UserRoute.PROFILE) {
                ProfileNavHost(
                    accountViewModel,
                    loggedInAccountViewModel,
                    workerViewModel,
                    userNavigationActions,
                    onScreenChange = { currentScreen = it },
                    categoryViewModel,
                    preferencesViewModel,
                    locationViewModel,
                    testBitmapPP,
                    testLocation,
                    rootMainNavigationActions,
                    userPreferencesViewModel,
                    appContentNavigationActions,
                    modeViewModel)
              }
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
      startDestination = UserScreen.HOME,
      route = UserRoute.HOME,
  ) {
    composable(UserScreen.HOME) { HomeScreen(navigationActions) }
    // Add MessageScreen as a nested composable within Home
    composable(UserScreen.MESSAGES) {
      //  MessageScreen(
      //      loggedInAccountViewModel = loggedInAccountViewModel, chatViewModel =
      // chatViewModel,navigationActions)
      FakeMessageScreen(navigationActions)
    }
  }
}

@Composable
fun ProfileNavHost(
    accountViewModel: AccountViewModel,
    loggedInAccountViewModel: LoggedInAccountViewModel,
    workerViewModel: ProfileViewModel,
    userNavigationActions: NavigationActions,
    onScreenChange: (String) -> Unit,
    categoryViewModel: CategoryViewModel,
    preferencesViewModel: PreferencesViewModel,
    locationViewModel: LocationViewModel,
    testBitmapPP: Bitmap? = null,
    testLocation: Location = Location(),
    rootMainNavigationActions: NavigationActions,
    userPreferencesViewModel: PreferencesViewModelUserProfile,
    appContentNavigationActions: NavigationActions,
    modeViewModel: ModeViewModel
) {

  val profileNavController = rememberNavController()
  val profileNavigationActions = remember { NavigationActions(profileNavController) }

  LaunchedEffect(profileNavigationActions.currentScreen) {
    onScreenChange(profileNavigationActions.currentScreen)
  }
  NavHost(navController = profileNavController, startDestination = UserScreen.PROFILE) {
    composable(UserScreen.PROFILE) {
      ProfileScreen(
          profileNavigationActions,
          rootMainNavigationActions,
          preferencesViewModel,
          userPreferencesViewModel,
          appContentNavigationActions,
          modeViewModel)
    }
    composable(UserScreen.ACCOUNT_CONFIGURATION) {
      AccountConfigurationScreen(profileNavigationActions, accountViewModel, preferencesViewModel)
    }
    composable(UserScreen.TO_WORKER) {
      BusinessScreen(
          profileNavigationActions,
          accountViewModel,
          workerViewModel,
          loggedInAccountViewModel,
          preferencesViewModel,
          categoryViewModel,
          locationViewModel,
          testBitmapPP,
          testLocation)
    }
  }
}

@Composable
fun DashBoardNavHost(onScreenChange: (String) -> Unit) {
  val dashboardNavController = rememberNavController()
  val navigationActions = remember { NavigationActions(dashboardNavController) }
  LaunchedEffect(navigationActions.currentScreen) {
    onScreenChange(navigationActions.currentScreen)
  }
  NavHost(navController = dashboardNavController, startDestination = UserScreen.DASHBOARD) {
    composable(UserScreen.DASHBOARD) { DashboardScreen(navigationActions) }
  }
}

@Composable
fun SearchNavHost(
    isUser: Boolean,
    navigationActionsRoot: NavigationActions,
    searchViewModel: SearchViewModel,
    profileViewModel: ProfileViewModel,
    loggedInAccountViewModel: LoggedInAccountViewModel,
    accountViewModel: AccountViewModel,
    announcementViewModel: AnnouncementViewModel,
    onScreenChange: (String) -> Unit,
    categoryViewModel: CategoryViewModel,
    preferencesViewModel: PreferencesViewModel,
    locationViewModel: LocationViewModel
) {
  val searchNavController = rememberNavController()
  val navigationActions = remember { NavigationActions(searchNavController) }
  LaunchedEffect(navigationActions.currentScreen) {
    onScreenChange(navigationActions.currentScreen)
  }
  NavHost(
      navController = searchNavController,
      startDestination = UserScreen.SEARCH,
  ) {
    composable(UserScreen.SEARCH) {
      QuickFixFinderScreen(
          navigationActions,
          navigationActionsRoot,
          isUser,
          profileViewModel,
          loggedInAccountViewModel,
          searchViewModel,
          accountViewModel,
          announcementViewModel,
          categoryViewModel)
    }
    composable(UserScreen.DISPLAY_UPLOADED_IMAGES) {
      QuickFixDisplayImages(isUser, navigationActions, announcementViewModel)
    }
    composable(UserScreen.SEARCH_WORKER_RESULT) {
      SearchWorkerResult(
          navigationActions,
          searchViewModel,
          accountViewModel,
          profileViewModel,
          preferencesViewModel)
    }
    composable(UserScreen.SEARCH_LOCATION) {
      LocationSearchCustomScreen(
          navigationActions = navigationActions, locationViewModel = locationViewModel)
    }
  }
}
