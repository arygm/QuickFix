package com.arygm.quickfix.ui.uiMode.appContentUI

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.category.CategoryViewModel
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.locations.LocationViewModel
import com.arygm.quickfix.model.messaging.ChatViewModel
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.offline.small.PreferencesViewModelUserProfile
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.quickfix.QuickFixViewModel
import com.arygm.quickfix.model.switchModes.AppMode
import com.arygm.quickfix.model.switchModes.ModeViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.uiMode.appContentUI.navigation.AppContentRoute
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.UserModeNavHost
import com.arygm.quickfix.ui.uiMode.workerMode.WorkerModeNavGraph
import com.arygm.quickfix.utils.loadAppMode

@Composable
fun AppContentNavGraph(
    testBitmapPP: Bitmap?,
    testLocation: Location = Location(),
    preferencesViewModel: PreferencesViewModel,
    userViewModel: ProfileViewModel,
    accountViewModel: AccountViewModel,
    isOffline: Boolean,
    rootNavigationActions: NavigationActions,
    modeViewModel: ModeViewModel,
    userPreferencesViewModel: PreferencesViewModelUserProfile,
    workerViewModel: ProfileViewModel,
    categoryViewModel: CategoryViewModel,
    locationViewModel: LocationViewModel,
    chatViewModel: ChatViewModel,
    quickFixViewModel: QuickFixViewModel
) {
    val appContentNavController = rememberNavController()
    val appContentNavigationActions = remember { NavigationActions(appContentNavController) }
    var currentAppMode by remember { mutableStateOf(AppMode.USER) }
    Log.d("userContent", "Current App Mode is empty: $currentAppMode")
    LaunchedEffect(Unit) {
        Log.d("userContent", "Loading App Mode")
        currentAppMode =
            when (loadAppMode(preferencesViewModel)) {
                "USER" -> AppMode.USER
                "WORKER" -> AppMode.WORKER
                else -> {
                    AppMode.WORKER
                }
            }
    }

    modeViewModel.switchMode(currentAppMode)
    Log.d("MainActivity", "$currentAppMode")
    val startDestination =
        when (currentAppMode) {
            AppMode.USER -> AppContentRoute.USER_MODE
            AppMode.WORKER -> AppContentRoute.WORKER_MODE
        }
    NavHost(
        navController = appContentNavigationActions.navController,
        startDestination = startDestination, // Apply padding from the Scaffold
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
                rootNavigationActions,
                userPreferencesViewModel,
                appContentNavigationActions,
                chatViewModel,
                quickFixViewModel,
                isOffline
            )
        }

        composable(AppContentRoute.WORKER_MODE) {
            WorkerModeNavGraph(
                modeViewModel,
                isOffline,
                appContentNavigationActions,
                preferencesViewModel,
                accountViewModel,
                rootNavigationActions,
                userPreferencesViewModel
            )
        }
    }
}
