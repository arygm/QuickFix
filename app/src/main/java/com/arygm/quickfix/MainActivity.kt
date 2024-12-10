package com.arygm.quickfix

import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.account.LoggedInAccountViewModel
import com.arygm.quickfix.model.category.CategoryViewModel
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.locations.LocationViewModel
import com.arygm.quickfix.model.messaging.ChatViewModel
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.profile.dataFields.AddOnService
import com.arygm.quickfix.model.profile.dataFields.IncludedService
import com.arygm.quickfix.model.quickfix.QuickFix
import com.arygm.quickfix.model.quickfix.Status
import com.arygm.quickfix.model.search.AnnouncementViewModel
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.ui.authentication.GoogleInfoScreen
import com.arygm.quickfix.ui.authentication.LogInScreen
import com.arygm.quickfix.ui.authentication.RegisterScreen
import com.arygm.quickfix.ui.authentication.ResetPasswordScreen
import com.arygm.quickfix.ui.authentication.WelcomeScreen
import com.arygm.quickfix.ui.camera.QuickFixDisplayImages
import com.arygm.quickfix.ui.dashboard.DashboardScreen
import com.arygm.quickfix.ui.elements.LocationSearchCustomScreen
import com.arygm.quickfix.ui.elements.QuickFixOfflineBar
import com.arygm.quickfix.ui.home.FakeMessageScreen
import com.arygm.quickfix.ui.home.HomeScreen
import com.arygm.quickfix.ui.navigation.BottomNavigationMenu
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Route
import com.arygm.quickfix.ui.navigation.Screen
import com.arygm.quickfix.ui.profile.AccountConfigurationScreen
import com.arygm.quickfix.ui.profile.ProfileScreen
import com.arygm.quickfix.ui.profile.becomeWorker.BusinessScreen
import com.arygm.quickfix.ui.quickfix.QuickFixThirdStep
import com.arygm.quickfix.ui.search.QuickFixFinderScreen
import com.arygm.quickfix.ui.search.SearchWorkerResult
import com.arygm.quickfix.ui.theme.QuickFixTheme
import com.arygm.quickfix.utils.LocationHelper
import com.google.firebase.Timestamp
import kotlinx.coroutines.delay
import java.util.Date

val Context.dataStore by preferencesDataStore(name = "quickfix_preferences")

class MainActivity : ComponentActivity() {

  private lateinit var locationHelper: LocationHelper

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    locationHelper = LocationHelper(this, this)
    val fakeQuickFix =
        QuickFix(
            "",
            Status.PENDING,
            listOf("Image 1 URL", "Image 2 URL"),
            listOf(Timestamp.now(), Timestamp(Date(2022, 1, 1))),
            Timestamp.now(),
            listOf(IncludedService("Service 1"), IncludedService("Service 2")),
            listOf(AddOnService("Service 1"), AddOnService("Service 2")),
            "",
            "PlaceHolder User Name from userId",
            "",
            "Painting Bedroom",
            "",
            emptyList(),
            Location(0.0, 0.0, "Fake Location"))

      val fakeWorkerProfile =
          WorkerProfile(
              displayName = "PlaceHolder Worker Name",
              fieldOfWork = "Painting"
          )

    setContent {
      QuickFixTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          QuickFixThirdStep(fakeQuickFix, fakeWorkerProfile)
            //QuickFixApp()
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
  val context = LocalContext.current
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
  val announcementViewModel: AnnouncementViewModel =
      viewModel(factory = AnnouncementViewModel.Factory)
  val categoryViewModel: CategoryViewModel = viewModel(factory = CategoryViewModel.Factory)

  val preferencesViewModel: PreferencesViewModel =
      viewModel(factory = PreferencesViewModel.Factory(LocalContext.current.dataStore))

  // Initialized here because needed for the bottom bar
  val profileNavController = rememberNavController()
  val profileNavigationActions = remember { NavigationActions(profileNavController) }

  val isUser = true // TODO: This variable needs to get its value after the authentication
  val screen by remember { navigationActionsRoot::currentScreen }
  var screenInProfileNavHost by remember { mutableStateOf<String?>(null) }
  var screenInSearchNavHost by remember { mutableStateOf<String?>(null) }

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
          screenInSearchNavHost?.let {
            it != Screen.DISPLAY_UPLOADED_IMAGES && it != Screen.SEARCH_LOCATION
          } ?: true &&
          screenInProfileNavHost?.let {
            it != Screen.ACCOUNT_CONFIGURATION && it != Screen.TO_WORKER
          } ?: true
    }
  }

  var showBottomBar by remember { mutableStateOf(false) }

  var isOffline by remember { mutableStateOf(!isConnectedToInternet(context)) }

  // Simulate monitoring connectivity (replace this with actual monitoring in production)
  LaunchedEffect(Unit) {
    while (true) {
      isOffline = !isConnectedToInternet(context)
      delay(3000) // Poll every 3 seconds
    }
  }

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
                      navigationActionsRoot, accountViewModel, userViewModel, preferencesViewModel)
                }
                composable(Screen.LOGIN) {
                  LogInScreen(navigationActionsRoot, accountViewModel, preferencesViewModel)
                }
                composable(Screen.REGISTER) {
                  RegisterScreen(
                      navigationActionsRoot, accountViewModel, userViewModel, preferencesViewModel)
                }
                composable(Screen.GOOGLE_INFO) {
                  GoogleInfoScreen(
                      navigationActionsRoot, accountViewModel, userViewModel, preferencesViewModel)
                }
                composable(Screen.RESET_PASSWORD) {
                  ResetPasswordScreen(navigationActionsRoot, accountViewModel)
                }
              }

              composable(Route.HOME) {
                HomeNavHost(isUser) // , loggedInAccountViewModel, chatViewModel)
              }

              composable(Route.SEARCH) {
                SearchNavHost(
                    isUser,
                    navigationActionsRoot,
                    searchViewModel,
                    userViewModel,
                    loggedInAccountViewModel,
                    accountViewModel,
                    announcementViewModel,
                    { currentScreen ->
                      screenInSearchNavHost = currentScreen // Mise à jour de l'écran actif
                    },
                    categoryViewModel,
                    preferencesViewModel)
              }

              composable(Route.DASHBOARD) { DashBoardNavHost(isUser) }

              composable(Route.PROFILE) {
                ProfileNavHost(
                    accountViewModel,
                    loggedInAccountViewModel,
                    workerViewModel,
                    navigationActionsRoot,
                    onScreenChange = { currentScreen -> screenInProfileNavHost = currentScreen },
                    categoryViewModel,
                    preferencesViewModel)
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

fun isConnectedToInternet(context: Context): Boolean {
  val connectivityManager =
      context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
  val network = connectivityManager.activeNetwork ?: return false
  val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
  return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

@Composable
fun ProfileNavHost(
    accountViewModel: AccountViewModel,
    loggedInAccountViewModel: LoggedInAccountViewModel,
    workerViewModel: ProfileViewModel,
    navigationActionsRoot: NavigationActions,
    onScreenChange: (String) -> Unit,
    categoryViewModel: CategoryViewModel,
    preferencesViewModel: PreferencesViewModel
) {

  val profileNavController = rememberNavController()
  val profileNavigationActions = remember { NavigationActions(profileNavController) }

  LaunchedEffect(profileNavigationActions.currentScreen) {
    onScreenChange(profileNavigationActions.currentScreen)
  }
  NavHost(navController = profileNavController, startDestination = Screen.PROFILE) {
    composable(Screen.PROFILE) {
      ProfileScreen(profileNavigationActions, navigationActionsRoot, preferencesViewModel)
    }
    composable(Screen.ACCOUNT_CONFIGURATION) {
      AccountConfigurationScreen(profileNavigationActions, accountViewModel, preferencesViewModel)
    }
    composable(Screen.TO_WORKER) {
      BusinessScreen(
          profileNavigationActions,
          accountViewModel,
          workerViewModel,
          loggedInAccountViewModel,
          categoryViewModel,
      )
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
    profileViewModel: ProfileViewModel,
    loggedInAccountViewModel: LoggedInAccountViewModel,
    accountViewModel: AccountViewModel,
    announcementViewModel: AnnouncementViewModel,
    onScreenChange: (String) -> Unit,
    categoryViewModel: CategoryViewModel,
    preferencesViewModel: PreferencesViewModel
) {
  val searchNavController = rememberNavController()
  val navigationActions = remember { NavigationActions(searchNavController) }
  LaunchedEffect(navigationActions.currentScreen) {
    onScreenChange(navigationActions.currentScreen)
  }
  val locationViewModel: LocationViewModel = viewModel(factory = LocationViewModel.Factory)
  NavHost(
      navController = searchNavController,
      startDestination = Screen.SEARCH,
  ) {
    composable(Screen.SEARCH) {
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
    composable(Screen.DISPLAY_UPLOADED_IMAGES) {
      QuickFixDisplayImages(isUser, navigationActions, announcementViewModel)
    }
    composable(Screen.SEARCH_WORKER_RESULT) {
      SearchWorkerResult(
          navigationActions,
          searchViewModel,
          accountViewModel,
          profileViewModel,
          preferencesViewModel)
    }
    composable(Screen.SEARCH_LOCATION) {
      LocationSearchCustomScreen(
          navigationActions = navigationActions, locationViewModel = locationViewModel)
    }
  }
}
