package com.arygm.quickfix

import android.os.Bundle
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.arygm.quickfix.ui.ActivityScreen
import com.arygm.quickfix.ui.AnnouncementScreen
import com.arygm.quickfix.ui.CalendarScreen
import com.arygm.quickfix.ui.MapScreen
import com.arygm.quickfix.ui.OtherScreen
import com.arygm.quickfix.ui.authentication.InfoScreen
import com.arygm.quickfix.ui.authentication.LogInScreen
import com.arygm.quickfix.ui.authentication.PasswordScreen
import com.arygm.quickfix.ui.authentication.WelcomeScreen
import com.arygm.quickfix.ui.home.HomeScreen
import com.arygm.quickfix.ui.navigation.BottomNavigationMenu
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Route
import com.arygm.quickfix.ui.navigation.Screen
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

  val isUser = false // TODO: This variable needs to get its value after the authentication
  val screen by remember { navigationActions::currentScreen }
  // Make `bottomBarVisible` reactive to changes in `screen`
  val shouldShowBottomBar by remember {
    derivedStateOf {
      screen != Screen.WELCOME &&
          screen != Screen.LOGIN &&
          screen != Screen.INFO &&
          screen != Screen.PASSWORD
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
                  selectedItem = Route.HOME, // Use the current route, or fallback to HOME
                  onTabSelect = { selectedDestination ->
                    // Use this block to navigate based on the selected tab
                    navigationActions.navigateTo(selectedDestination)
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
                composable(Screen.WELCOME) { WelcomeScreen(navigationActions) }
                composable(Screen.LOGIN) { LogInScreen(navigationActions) }
                composable(Screen.INFO) { InfoScreen(navigationActions) }
                composable(Screen.PASSWORD) { PasswordScreen(navigationActions) }
              }
              navigation(
                  startDestination = Screen.HOME,
                  route = Route.HOME,
              ) {
                composable(Screen.HOME) { HomeScreen(navigationActions, isUser) }
                composable(Screen.PROFILE) { HomeScreen(navigationActions, isUser) }
                composable(Screen.MESSAGES) { HomeScreen(navigationActions, isUser) }
              }
              navigation(
                  startDestination = Screen.CALENDAR,
                  route = Route.CALENDAR,
              ) {
                composable(Screen.CALENDAR) { CalendarScreen(navigationActions, isUser) }
              }
              navigation(
                  startDestination = Screen.ANNOUNCEMENT,
                  route = Route.ANNOUNCEMENT,
              ) {
                composable(Screen.ANNOUNCEMENT) { AnnouncementScreen(navigationActions, isUser) }
              }
              navigation(
                  startDestination = Screen.MAP,
                  route = Route.MAP,
              ) {
                composable(Screen.MAP) { MapScreen(navigationActions, isUser) }
              }
              navigation(
                  startDestination = Screen.ACTIVITY,
                  route = Route.ACTIVITY,
              ) {
                composable(Screen.ACTIVITY) { ActivityScreen(navigationActions, isUser) }
              }
              navigation(
                  startDestination = Screen.OTHER,
                  route = Route.OTHER,
              ) {
                composable(Screen.OTHER) { OtherScreen(navigationActions, isUser) }
              }
            }
      }
}
