package com.arygm.quickfix

import android.content.pm.PackageManager
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
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.account.LoggedInAccountViewModel
import com.arygm.quickfix.model.messaging.ChatViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.ui.account.AccountConfigurationScreen
import com.arygm.quickfix.ui.authentication.GoogleInfoScreen
import com.arygm.quickfix.ui.authentication.LogInScreen
import com.arygm.quickfix.ui.authentication.RegisterScreen
import com.arygm.quickfix.ui.authentication.ResetPasswordScreen
import com.arygm.quickfix.ui.authentication.WelcomeScreen
import com.arygm.quickfix.ui.dashboard.DashboardScreen
import com.arygm.quickfix.ui.home.FakeMessageScreen
import com.arygm.quickfix.ui.home.HomeScreen
import com.arygm.quickfix.ui.navigation.BottomNavigationMenu
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Route
import com.arygm.quickfix.ui.navigation.Screen
import com.arygm.quickfix.ui.profile.BusinessScreen
import com.arygm.quickfix.ui.profile.ProfileScreen
import com.arygm.quickfix.ui.search.QuickFixFinderScreen
import com.arygm.quickfix.ui.search.SearchWorkerResult
import com.arygm.quickfix.ui.theme.QuickFixTheme
import com.arygm.quickfix.utils.LocationHelper
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

  private lateinit var locationHelper: LocationHelper

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    locationHelper = LocationHelper(this, this)

    setContent {
      QuickFixTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          QuickFixApp()
        }
      }
    }

    // Check permissions and get location
    if (locationHelper.checkPermissions()) {
      locationHelper.getCurrentLocation { location ->
        location?.let {
          // Handle location (e.g., update UI, save location data)
          Log.d("MainActivity", "Latitude: ${it.latitude}, Longitude: ${it.longitude}")
        }
      }
    } else {
      locationHelper.requestPermissions()
    }
  }

  override fun onRequestPermissionsResult(
      requestCode: Int,
      permissions: Array<String>,
      grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == LocationHelper.PERMISSION_REQUEST_ACCESS_LOCATION &&
        grantResults.isNotEmpty() &&
        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      locationHelper.getCurrentLocation { location ->
        location?.let {
          Log.d("MainActivity", "Latitude: ${it.latitude}, Longitude: ${it.longitude}")
        }
      }
    }
  }
}

@Composable
@Preview
fun QuickFixApp() {

  val rootNavController = rememberNavController()
  val navigationActionsRoot = remember { NavigationActions(rootNavController) }

  val userViewModel: ProfileViewModel =
      viewModel(key = "userViewModel", factory = ProfileViewModel.UserFactory)
  val workerViewModel: ProfileViewModel =
      viewModel(key = "workerViewModel", factory = ProfileViewModel.WorkerFactory)
  val loggedInAccountViewModel: LoggedInAccountViewModel =
      viewModel(factory = LoggedInAccountViewModel.Factory)
  val accountViewModel: AccountViewModel = viewModel(factory = AccountViewModel.Factory)
  val chatViewModel: ChatViewModel = viewModel(factory = ChatViewModel.Factory)
  val searchViewModel: SearchViewModel = viewModel(factory = SearchViewModel.Factory)

  // Initialized here because needed for the bottom bar
  val profileNavController = rememberNavController()
  val profileNavigationActions = remember { NavigationActions(profileNavController) }

  val isUser = false // TODO: This variable needs to get its value after the authentication
  val screen by remember { navigationActionsRoot::currentScreen }
  var screenInProfileNavHost by remember { mutableStateOf<String?>(null) }

  // Make `bottomBarVisible` reactive to changes in `screen`
  val shouldShowBottomBar by remember {
    derivedStateOf {
      screen != Screen.WELCOME &&
          screen != Screen.LOGIN &&
          screen != Screen.INFO &&
          screen != Screen.PASSWORD &&
          screen != Screen.REGISTER &&
          screen != Screen.RESET_PASSWORD &&
          screen != Screen.GOOGLE_INFO &&
          screenInProfileNavHost?.let {
            it != Screen.ACCOUNT_CONFIGURATION && it != Screen.TO_WORKER
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
                    navigationActionsRoot.navigateTo(selectedDestination)
                    Log.d("user", navigationActionsRoot.currentRoute())
                  },
                  isUser = isUser, // Pass the user type to determine the tabs
                  navigationActions = navigationActionsRoot)
            }
      }) { innerPadding ->
        NavHost(
            navController = rootNavController,
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
                  WelcomeScreen(
                      navigationActionsRoot,
                      accountViewModel,
                      loggedInAccountViewModel,
                      userViewModel)
                }
                composable(Screen.LOGIN) {
                  LogInScreen(navigationActionsRoot, accountViewModel, loggedInAccountViewModel)
                }
                composable(Screen.REGISTER) {
                  RegisterScreen(
                      navigationActionsRoot,
                      accountViewModel,
                      loggedInAccountViewModel,
                      userViewModel)
                }
                composable(Screen.GOOGLE_INFO) {
                  GoogleInfoScreen(
                      navigationActionsRoot,
                      loggedInAccountViewModel,
                      accountViewModel,
                      userViewModel)
                }
                composable(Screen.RESET_PASSWORD) {
                  ResetPasswordScreen(navigationActionsRoot, accountViewModel)
                }
              }

              composable(Route.HOME) {
                HomeNavHost(isUser) // , loggedInAccountViewModel, chatViewModel)
              }

              composable(Route.SEARCH) {
                SearchNavHost(isUser, navigationActionsRoot, searchViewModel, accountViewModel)
              }

              composable(Route.DASHBOARD) { DashBoardNavHost(isUser) }

              composable(Route.PROFILE) {
                ProfileNavHost(
                    accountViewModel,
                    loggedInAccountViewModel,
                    workerViewModel,
                    navigationActionsRoot) { currentScreen ->
                      screenInProfileNavHost = currentScreen
                    }
              }
            }
      }
}

@Composable
fun HomeNavHost(
    isUser: Boolean,
    // loggedInAccountViewModel: LoggedInAccountViewModel,
    // chatViewModel: ChatViewModel
) {
  val homeNavController = rememberNavController()
  val navigationActions = remember { NavigationActions(homeNavController) }

  NavHost(
      navController = homeNavController,
      startDestination = Screen.HOME,
      route = Route.HOME,
  ) {
    composable(Screen.HOME) { HomeScreen(navigationActions, isUser) }
    // Add MessageScreen as a nested composable within Home
    composable(Screen.MESSAGES) {
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
    onScreenChange: (String) -> Unit
) {

  val profileNavController = rememberNavController()
  val profileNavigationActions = remember { NavigationActions(profileNavController) }

  LaunchedEffect(profileNavigationActions.currentScreen) {
    onScreenChange(profileNavigationActions.currentScreen)
  }
  NavHost(navController = profileNavController, startDestination = Screen.PROFILE) {
    composable(Screen.PROFILE) {
      ProfileScreen(
          profileNavigationActions,
          loggedInAccountViewModel = loggedInAccountViewModel,
          navigationActionsRoot)
    }
    composable(Screen.ACCOUNT_CONFIGURATION) {
      AccountConfigurationScreen(
          profileNavigationActions, accountViewModel, loggedInAccountViewModel)
    }
    composable(Screen.TO_WORKER) {
      BusinessScreen(
          profileNavigationActions, accountViewModel, workerViewModel, loggedInAccountViewModel)
    }
  }
}

@Composable
fun DashBoardNavHost(isUser: Boolean) {
  val dashboardNavController = rememberNavController()
  val navigationActions = remember { NavigationActions(dashboardNavController) }
  NavHost(navController = dashboardNavController, startDestination = Screen.DASHBOARD) {
    composable(Screen.DASHBOARD) { DashboardScreen(navigationActions, isUser) }
  }
}

@Composable
fun SearchNavHost(
    isUser: Boolean,
    navigationActionsRoot: NavigationActions,
    searchViewModel: SearchViewModel,
    accountViewModel: AccountViewModel
) {
  val searchNavController = rememberNavController()
  val navigationActions = remember { NavigationActions(searchNavController) }
  NavHost(
      navController = searchNavController,
      startDestination = Screen.SEARCH,
  ) {
    composable(Screen.SEARCH) {
      QuickFixFinderScreen(navigationActions, navigationActionsRoot, isUser, searchViewModel)
    }
    composable(Screen.SEARCH_WORKER_RESULT) {
      SearchWorkerResult(navigationActions, searchViewModel, accountViewModel)
    }
  }
}
