package com.arygm.quickfix.ui.noModeUI

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.offline.small.PreferencesViewModelUserProfile
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.ui.authentication.GoogleInfoScreen
import com.arygm.quickfix.ui.authentication.LogInScreen
import com.arygm.quickfix.ui.authentication.RegisterScreen
import com.arygm.quickfix.ui.authentication.ResetPasswordScreen
import com.arygm.quickfix.ui.authentication.WelcomeScreen
import com.arygm.quickfix.ui.elements.QuickFixOfflineBar
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.noModeUI.navigation.NoModeRoute

@Composable
fun NoModeNavHost(
    rootNavigationActions: NavigationActions,
    accountViewModel: AccountViewModel,
    preferencesViewModel: PreferencesViewModel,
    userViewModel: ProfileViewModel,
    isOffline: Boolean,
    userPreferencesViewModel: PreferencesViewModelUserProfile
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
          composable(NoModeRoute.WELCOME) {
            WelcomeScreen(
                navigationActions,
                accountViewModel,
                userViewModel,
                preferencesViewModel,
                rootNavigationActions,
                userPreferencesViewModel)
          }

          composable(NoModeRoute.LOGIN) {
            LogInScreen(
                navigationActions,
                accountViewModel,
                preferencesViewModel,
                rootNavigationActions,
                userPreferencesViewModel,
                userViewModel)
          }
          composable(NoModeRoute.REGISTER) {
            RegisterScreen(
                rootNavigationActions,
                navigationActions,
                accountViewModel,
                userViewModel,
                preferencesViewModel,
                userPreferencesViewModel)
          }
          composable(NoModeRoute.GOOGLE_INFO) {
            GoogleInfoScreen(
                rootNavigationActions,
                accountViewModel,
                userViewModel,
                preferencesViewModel,
                navigationActions,
                userPreferencesViewModel)
          }
          composable(NoModeRoute.RESET_PASSWORD) {
            ResetPasswordScreen(navigationActions, accountViewModel)
          }
        }
  }
}
