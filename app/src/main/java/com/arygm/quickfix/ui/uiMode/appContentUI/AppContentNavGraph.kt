package com.arygm.quickfix.ui.uiMode.appContentUI

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.arygm.quickfix.dataStore
import com.arygm.quickfix.isConnectedToInternet
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.account.LoggedInAccountViewModel
import com.arygm.quickfix.model.category.CategoryViewModel
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.locations.LocationViewModel
import com.arygm.quickfix.model.messaging.ChatViewModel
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.search.AnnouncementViewModel
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.model.switchModes.ModeViewModel
import com.arygm.quickfix.ui.elements.QuickFixOfflineBar
import com.arygm.quickfix.ui.navigation.BottomNavigationMenu
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.RootRoute
import com.arygm.quickfix.ui.noModeUI.NoModeNavHost
import com.arygm.quickfix.ui.noModeUI.navigation.NoModeRoute
import com.arygm.quickfix.ui.noModeUI.navigation.NoModeScreen
import com.arygm.quickfix.ui.uiMode.appContentUI.navigation.AppContentRoute
import com.arygm.quickfix.ui.uiMode.workerMode.WorkerModeNavGraph
import com.arygm.quickfix.ui.userModeUI.UserModeNavHost
import com.arygm.quickfix.ui.userModeUI.navigation.UserScreen
import com.arygm.quickfix.utils.loadAppMode
import kotlinx.coroutines.delay

@Composable
fun AppContentNavGraph(testBitmapPP: Bitmap?,
                       testLocation: Location = Location(),
                       preferencesViewModel : PreferencesViewModel,
                       userViewModel: ProfileViewModel,
                       accountViewModel: AccountViewModel,
                       isOffline: Boolean,
                       rootNavigationActions: NavigationActions
                       ) {
    val rootNavController = rememberNavController()
    val navigationActionsRoot = remember { NavigationActions(rootNavController) }

    val modeViewModel: ModeViewModel = viewModel(factory = ModeViewModel.Factory)
    val workerViewModel: ProfileViewModel =
        viewModel(key = "workerViewModel", factory = ProfileViewModel.WorkerFactory)
    val categoryViewModel: CategoryViewModel = viewModel(factory = CategoryViewModel.Factory)
    val locationViewModel: LocationViewModel = viewModel(factory = LocationViewModel.Factory)


    var currentScreen by remember { mutableStateOf<String?>(null) }

    // Make `bottomBarVisible` reactive to changes in `screen`
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

    var currentAppMode by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        currentAppMode = when(loadAppMode(preferencesViewModel)){
            "User" -> AppContentRoute.USER_MODE
            "Worker" -> AppContentRoute.WORKER_MODE
            else -> {
                AppContentRoute.USER_MODE
            }
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
                    modeViewModel = modeViewModel,
                    onTabSelect = { selectedDestination ->
                        // Use this block to navigate based on the selected tab
                        navigationActionsRoot.navigateTo(selectedDestination)
                        Log.d("user", navigationActionsRoot.currentRoute())
                    },
                    navigationActions = navigationActionsRoot)
            }
        }) { innerPadding ->
        NavHost(
            navController = rootNavController,
            startDestination = currentAppMode,
            modifier = Modifier.padding(innerPadding), // Apply padding from the Scaffold
            enterTransition = {
                // You can change whatever you want for transitions
                EnterTransition.None
            },
            exitTransition = {
                // You can change whatever you want for transitions
                ExitTransition.None
            }) {

            composable(AppContentRoute.USER_MODE) {
                UserModeNavHost(
                    testBitmapPP,
                    testLocation,
                    modeViewModel,
                    userViewModel,
                    workerViewModel,
                    accountViewModel,
                    categoryViewModel,
                    locationViewModel,
                    preferencesViewModel,
                    onScreenChange = {
                        currentScreen = it
                    }
                )
            }

            composable(AppContentRoute.WORKER_MODE) {
                WorkerModeNavGraph(
                    onScreenChange = {
                        currentScreen = it
                    }
                )
            }
        }
    }
}