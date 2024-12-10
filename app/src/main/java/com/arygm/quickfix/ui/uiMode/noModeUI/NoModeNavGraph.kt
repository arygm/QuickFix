package com.arygm.quickfix.ui.noModeUI

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.arygm.quickfix.dataStore
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.ui.authentication.GoogleInfoScreen
import com.arygm.quickfix.ui.authentication.LogInScreen
import com.arygm.quickfix.ui.authentication.RegisterScreen
import com.arygm.quickfix.ui.authentication.ResetPasswordScreen
import com.arygm.quickfix.ui.authentication.WelcomeScreen
import com.arygm.quickfix.ui.elements.QuickFixOfflineBar
import com.arygm.quickfix.ui.navigation.BottomNavigationMenu
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.noModeUI.navigation.NoModeRoute
import com.arygm.quickfix.ui.noModeUI.navigation.NoModeScreen

@Composable
fun NoModeNavHost(
    rootNavigationActions: NavigationActions,
    accountViewModel: AccountViewModel,
    preferencesViewModel: PreferencesViewModel,
    userViewModel: ProfileViewModel,
    isOffline: Boolean
) {
    val rootSharedModelNavController = rememberNavController()
    val navigationActions = NavigationActions(rootSharedModelNavController)

    Scaffold(
        topBar = { QuickFixOfflineBar(isVisible = isOffline) },
        ) { innerPadding ->
        NavHost(
            navController = rootSharedModelNavController,
            startDestination = NoModeRoute.WELCOME,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                // You can change whatever you want for transitions
                EnterTransition.None
            },
            exitTransition = {
                // You can change whatever you want for transitions
                ExitTransition.None
            }) {
            navigation(
                startDestination = NoModeScreen.WELCOME,
                route = NoModeRoute.WELCOME,
            ) {
                composable(NoModeRoute.WELCOME) {
                    WelcomeScreen(
                        navigationActions,
                        accountViewModel,
                        userViewModel,
                        preferencesViewModel,
                        rootNavigationActions
                    )
                }
                composable(NoModeRoute.LOGIN) {
                    LogInScreen(navigationActions, accountViewModel, preferencesViewModel, rootNavigationActions)
                }
                composable(NoModeRoute.REGISTER) {
                    RegisterScreen(
                        rootNavigationActions,
                        navigationActions,
                        accountViewModel,
                        userViewModel,
                        preferencesViewModel
                    )
                }
                composable(NoModeRoute.GOOGLE_INFO) {
                    GoogleInfoScreen(
                        navigationActions,
                        accountViewModel,
                        userViewModel,
                        preferencesViewModel,
                        navigationActions
                    )
                }
                composable(NoModeRoute.RESET_PASSWORD) {
                    ResetPasswordScreen(navigationActions, accountViewModel)
                }
            }
        }
    }
}