package com.arygm.quickfix.ui.noModeUI

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
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
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.noModeUI.navigation.NoModeRoute
import com.arygm.quickfix.ui.noModeUI.navigation.NoModeScreen

@Composable
fun NoModeNavHost(
    accountViewModel: AccountViewModel,
    preferencesViewModel: PreferencesViewModel,
    userViewModel: ProfileViewModel,
    onScreenChange: (String) -> Unit
) {
    val rootSharedModelNavController = rememberNavController()
    val navigationActions = NavigationActions(rootSharedModelNavController)

    LaunchedEffect(navigationActions.currentScreen) {
        onScreenChange(navigationActions.currentScreen)
    }

    NavHost(
        navController = rootSharedModelNavController,
        startDestination = NoModeRoute.WELCOME,
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
            composable(NoModeScreen.WELCOME) {
                WelcomeScreen(
                    navigationActions,
                    accountViewModel,
                    userViewModel,
                    preferencesViewModel
                )
            }
            composable(NoModeScreen.LOGIN) {
                LogInScreen(navigationActions, accountViewModel, preferencesViewModel)
            }
            composable(NoModeScreen.REGISTER) {
                RegisterScreen(
                    navigationActions,
                    accountViewModel,
                    userViewModel,
                    preferencesViewModel
                )
            }
            composable(NoModeScreen.GOOGLE_INFO) {
                GoogleInfoScreen(
                    navigationActions,
                    accountViewModel,
                    userViewModel,
                    preferencesViewModel
                )
            }
            composable(NoModeScreen.RESET_PASSWORD) {
                ResetPasswordScreen(navigationActions, accountViewModel)
            }
        }
    }
}