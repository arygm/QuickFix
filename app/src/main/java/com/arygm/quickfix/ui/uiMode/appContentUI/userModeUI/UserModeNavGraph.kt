package com.arygm.quickfix.ui.userModeUI

import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.arygm.quickfix.dataStore
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.account.LoggedInAccountViewModel
import com.arygm.quickfix.model.category.CategoryViewModel
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.locations.LocationViewModel
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.profile.Profile
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
) {
    val rootNavControllerUserMode = rememberNavController()
    val navigationActionsRoot = remember { NavigationActions(rootNavControllerUserMode) }


    val loggedInAccountViewModel: LoggedInAccountViewModel =
        viewModel(factory = LoggedInAccountViewModel.Factory)
    val searchViewModel: SearchViewModel = viewModel(factory = SearchViewModel.Factory)
    val announcementViewModel: AnnouncementViewModel =
        viewModel(factory = AnnouncementViewModel.Factory)

    // Initialized here because needed for the bottom bar

    val isUser = true // TODO: This variable needs to get its value after the authentication

    NavHost(
        navController = rootNavControllerUserMode,
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
            HomeNavHost(
                isUser,
                onScreenChange
            ) // , loggedInAccountViewModel, chatViewModel)
        }

        composable(UserRoute.SEARCH) {
            SearchNavHost(
                isUser,
                navigationActionsRoot,
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

        composable(UserRoute.DASHBOARD) { DashBoardNavHost(isUser, onScreenChange) }

        composable(UserRoute.PROFILE) {
            ProfileNavHost(
                accountViewModel,
                loggedInAccountViewModel,
                workerViewModel,
                navigationActionsRoot,
                onScreenChange,
                categoryViewModel,
                preferencesViewModel,
                locationViewModel,
                testBitmapPP, testLocation)
        }
    }
}

@Composable
fun HomeNavHost(
    isUser: Boolean,
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
    navigationActionsRoot: NavigationActions,
    onScreenChange: (String) -> Unit,
    categoryViewModel: CategoryViewModel,
    preferencesViewModel: PreferencesViewModel,
    locationViewModel: LocationViewModel,
    testBitmapPP: Bitmap? = null,
    testLocation: Location = Location()
) {

    val profileNavController = rememberNavController()
    val profileNavigationActions = remember { NavigationActions(profileNavController) }

    LaunchedEffect(profileNavigationActions.currentScreen) {
        onScreenChange(profileNavigationActions.currentScreen)
    }
    NavHost(navController = profileNavController, startDestination = UserScreen.PROFILE) {
        composable(UserScreen.PROFILE) {
            ProfileScreen(profileNavigationActions, navigationActionsRoot, preferencesViewModel)
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
fun DashBoardNavHost(isUser: Boolean, onScreenChange: (String) -> Unit) {
    val dashboardNavController = rememberNavController()
    val navigationActions = remember { NavigationActions(dashboardNavController) }
    LaunchedEffect(navigationActions.currentScreen) {
        onScreenChange(navigationActions.currentScreen)
    }
    NavHost(navController = dashboardNavController, startDestination = UserScreen.DASHBOARD) {
        composable(UserScreen.DASHBOARD) { DashboardScreen(navigationActions, isUser) }
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
