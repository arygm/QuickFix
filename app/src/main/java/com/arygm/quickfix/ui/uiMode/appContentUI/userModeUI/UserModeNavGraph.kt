package com.arygm.quickfix.ui.userModeUI

import android.graphics.Bitmap
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.search.AnnouncementViewModel
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.model.switchModes.ModeViewModel
import com.arygm.quickfix.ui.camera.QuickFixDisplayImages
import com.arygm.quickfix.ui.dashboard.DashboardScreen
import com.arygm.quickfix.ui.elements.LocationSearchCustomScreen
import com.arygm.quickfix.ui.home.FakeMessageScreen
import com.arygm.quickfix.ui.home.HomeScreen
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.profile.AccountConfigurationScreen
import com.arygm.quickfix.ui.profile.ProfileScreen
import com.arygm.quickfix.ui.profile.becomeWorker.BusinessScreen
import com.arygm.quickfix.ui.search.QuickFixFinderScreen
import com.arygm.quickfix.ui.search.SearchWorkerResult
import com.arygm.quickfix.ui.userModeUI.navigation.UserRoute
import com.arygm.quickfix.ui.userModeUI.navigation.UserScreen

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
    onScreenChange: (String) -> Unit,
    rootMainNavigationActions: NavigationActions,
    userNavigationActions: NavigationActions,
) {

  val loggedInAccountViewModel: LoggedInAccountViewModel =
      viewModel(factory = LoggedInAccountViewModel.Factory)
  val searchViewModel: SearchViewModel = viewModel(factory = SearchViewModel.Factory)
  val announcementViewModel: AnnouncementViewModel =
      viewModel(factory = AnnouncementViewModel.Factory)

  // Initialized here because needed for the bottom bar

  val isUser = true // TODO: This variable needs to get its value after the authentication

  NavHost(
      navController = userNavigationActions.navController,
      startDestination = UserRoute.HOME,
      enterTransition = {
        // You can change whatever you want for transitions
        EnterTransition.None
      },
      exitTransition = {
        // You can change whatever you want for transitions
        ExitTransition.None
      }) {
        composable(UserRoute.HOME) {
          HomeNavHost(onScreenChange) // , loggedInAccountViewModel, chatViewModel)
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
              onScreenChange,
              categoryViewModel,
              preferencesViewModel,
              locationViewModel)
        }

        composable(UserRoute.DASHBOARD) { DashBoardNavHost(onScreenChange) }

        composable(UserRoute.PROFILE) {
          ProfileNavHost(
              accountViewModel,
              loggedInAccountViewModel,
              workerViewModel,
              userNavigationActions,
              onScreenChange,
              categoryViewModel,
              preferencesViewModel,
              locationViewModel,
              testBitmapPP,
              testLocation,
              rootMainNavigationActions,
          )
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
    rootMainNavigationActions: NavigationActions
) {

  val profileNavController = rememberNavController()
  val profileNavigationActions = remember { NavigationActions(profileNavController) }

  LaunchedEffect(profileNavigationActions.currentScreen) {
    onScreenChange(profileNavigationActions.currentScreen)
  }
  NavHost(navController = profileNavController, startDestination = UserScreen.PROFILE) {
    composable(UserScreen.PROFILE) {
      ProfileScreen(profileNavigationActions, rootMainNavigationActions, preferencesViewModel)
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
          accountViewModel,
          searchViewModel,
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
