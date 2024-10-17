package com.arygm.quickfix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.arygm.quickfix.ui.DashboardScreen
import com.arygm.quickfix.ui.ProfileScreen
import com.arygm.quickfix.ui.SearchScreen
import com.arygm.quickfix.ui.authentication.InfoScreen
import com.arygm.quickfix.ui.authentication.LogInScreen
import com.arygm.quickfix.ui.authentication.PasswordScreen
import com.arygm.quickfix.ui.authentication.WelcomeScreen
import com.arygm.quickfix.ui.home.HomeScreen
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Route
import com.arygm.quickfix.ui.navigation.Screen
import com.arygm.quickfix.ui.theme.QuickFixTheme

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
  val navigationActions = NavigationActions(navController)

  val isUser = false // TODO: This variable needs to get its value after the authentication
  NavHost(navController = navController, startDestination = Route.HOME) {
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
      // TODO Change the screen when implemented as well as the tests
      composable(Screen.PROFILE) { HomeScreen(navigationActions, isUser) }
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
      composable(Screen.PROFILE) { ProfileScreen(navigationActions, isUser) }
    }
  }
}
