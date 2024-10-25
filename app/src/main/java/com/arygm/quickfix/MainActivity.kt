package com.arygm.quickfix

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.arygm.quickfix.model.profile.LoggedInProfileViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.RegistrationViewModel
import com.arygm.quickfix.ui.DashboardScreen
import com.arygm.quickfix.ui.SearchScreen
import com.arygm.quickfix.ui.authentication.LogInScreen
import com.arygm.quickfix.ui.authentication.RegisterScreen
import com.arygm.quickfix.ui.authentication.WelcomeScreen
import com.arygm.quickfix.ui.home.HomeScreen
import com.arygm.quickfix.ui.navigation.BottomNavigationMenu
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Route
import com.arygm.quickfix.ui.navigation.Screen
import com.arygm.quickfix.ui.profile.BusinessScreen
import com.arygm.quickfix.ui.profile.ProfileConfigurationScreen
import com.arygm.quickfix.ui.profile.ProfileScreen
import com.arygm.quickfix.ui.theme.QuickFixTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      QuickFixTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          QuickFixApp()
        }
      }
    }
  }
}

@Composable
@Preview
fun QuickFixApp() {

  val navController = rememberNavController()
  val navigationActions = remember { NavigationActions(navController) }

  val userViewModel: ProfileViewModel =
      viewModel(key = "userViewModel", factory = ProfileViewModel.UserFactory)

  val workerViewModel: ProfileViewModel =
      viewModel(key = "workerViewModel", factory = ProfileViewModel.WorkerFactory)
  val loggedInProfileViewModel: LoggedInProfileViewModel = viewModel()
  val registrationViewModel = RegistrationViewModel()

  val isUser = false // TODO: This variable needs to get its value after the authentication
  val screen by remember { navigationActions::currentScreen }
  // Make `bottomBarVisible` reactive to changes in `screen`
  val shouldShowBottomBar by remember {
    derivedStateOf {
      screen != Screen.WELCOME &&
          screen != Screen.LOGIN &&
          screen != Screen.INFO &&
          screen != Screen.PASSWORD &&
          screen != Screen.REGISTER &&
          screen != Screen.ACCOUNT_CONFIGURATION &&
          screen != Screen.TO_WORKER
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
      bottomBar = {
        // Show BottomNavigationMenu only if the route is not part of the login/registration flow
        AnimatedVisibility(
            visible = showBottomBar,
            enter = slideInVertically { fullHeight -> fullHeight }, // Slide in from the bottom
            exit = slideOutVertically { fullHeight -> fullHeight }, // Slide out to the bottom
            modifier = Modifier.testTag("BNM")) {
              BottomNavigationMenu(
                  selectedItem =
                      navigationActions
                          .currentRoute(), // Use the current route, or fallback to HOME
                  onTabSelect = { selectedDestination ->
                    // Use this block to navigate based on the selected tab
                    navigationActions.navigateTo(selectedDestination)
                    Log.d("user", navigationActions.currentRoute())
                  },
                  isUser = isUser // Pass the user type to determine the tabs
                  )
            }
      }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.WELCOME,
            modifier = Modifier.padding(innerPadding), // Apply padding from the Scaffold
            enterTransition = {
              // You can change whatever you want for transitions
              EnterTransition.None
            },
            exitTransition = {
              // You can change whatever you want for transitions
              ExitTransition.None
            }) {
              navigation(
                  startDestination = Screen.WELCOME,
                  route = Route.WELCOME,
              ) {
                composable(Screen.WELCOME) {
                  WelcomeScreen(navigationActions, userViewModel, loggedInProfileViewModel)
                }
                composable(Screen.LOGIN) {
                  LogInScreen(navigationActions, userViewModel, loggedInProfileViewModel)
                }
                composable(Screen.REGISTER) {
                  RegisterScreen(navigationActions, userViewModel, loggedInProfileViewModel)
                }
              }
              navigation(
                  startDestination = Screen.HOME,
                  route = Route.HOME,
              ) {
                composable(Screen.HOME) { HomeScreen(navigationActions, isUser) }
                composable(Screen.MESSAGES) { HomeScreen(navigationActions, isUser) }
              }
              navigation(
                  startDestination = Screen.SEARCH,
                  route = Route.SEARCH,
              ) {
                composable(Screen.SEARCH) { SearchScreen(navigationActions, isUser) }
              }
              navigation(
                  startDestination = Screen.DASHBOARD,
                  route = Route.DASHBOARD,
              ) {
                composable(Screen.DASHBOARD) { DashboardScreen(navigationActions, isUser) }
              }
              navigation(
                  startDestination = Screen.PROFILE,
                  route = Route.PROFILE,
              ) {
                composable(Screen.PROFILE) {
                  ProfileScreen(
                      navigationActions,
                      isUser,
                      userViewModel,
                      workerViewModel,
                      loggedInProfileViewModel)
                }
                composable(Screen.ACCOUNT_CONFIGURATION) {
                  ProfileConfigurationScreen(
                      navigationActions,
                      isUser,
                      userViewModel,
                      workerViewModel,
                      loggedInProfileViewModel)
                }
                composable(Screen.TO_WORKER) {
                  BusinessScreen(
                      navigationActions, userViewModel, workerViewModel, loggedInProfileViewModel)
                }
              }
            }
      }
}
