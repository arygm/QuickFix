package com.arygm.quickfix.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.arygm.quickfix.utils.routeToScreen
import com.arygm.quickfix.utils.screenToRoute

object Route {
  const val CALENDAR = "Calendar"
  const val WELCOME = "Welcome"
  const val HOME = "Home"
  const val SEARCH = "Search"
  const val DASHBOARD = "Dashboard"
  const val INFO = "Info"
  const val LOGIN = "Login"
  const val PASSWORD = "Password"
  const val PROFILE = "Profile"
  const val ACCOUNT_CONFIGURATION = "Account configuration"
}

object Screen {
  const val WELCOME = "Welcome Screen"
  const val LOGIN = "Login Screen"
  const val INFO = "Info Screen"
  const val PASSWORD = "Password Screen"
  const val REGISTER = "Register Screen"
  const val HOME = "Home Screen"
  const val SEARCH = "Search Screen"
  const val DASHBOARD = "Dashboard Screen"
  const val MESSAGES = "Messages Screen"
  const val CALENDAR = "Calendar Screen"
  const val PROFILE = "Profile Screen"
  const val ACCOUNT_CONFIGURATION = "Account configuration Screen"
}

data class TopLevelDestination(val route: String, val icon: ImageVector, val textId: String)

object TopLevelDestinations {
  val HOME = TopLevelDestination(route = Route.HOME, icon = Icons.Filled.Home, textId = "Home")
  val PROFILE =
      TopLevelDestination(
          route = Route.PROFILE, icon = Icons.Filled.AccountCircle, textId = "Profile")
  val SEARCH =
      TopLevelDestination(route = Route.SEARCH, icon = Icons.Filled.Search, textId = "Search")
  val DASHBOARD =
      TopLevelDestination(route = Route.DASHBOARD, icon = Icons.Filled.Menu, textId = "Dashboard")
}

val USER_TOP_LEVEL_DESTINATIONS =
    listOf(
        TopLevelDestinations.HOME,
        TopLevelDestinations.SEARCH,
        TopLevelDestinations.DASHBOARD,
        TopLevelDestinations.PROFILE,
    )
val WORKER_TOP_LEVEL_DESTINATIONS =
    listOf(
        TopLevelDestinations.HOME,
        TopLevelDestinations.SEARCH,
        TopLevelDestinations.DASHBOARD,
        TopLevelDestinations.PROFILE)

fun getBottomBarId(route: String, isUser: Boolean): Int {
  return when (route) {
    Route.HOME -> 1
    Route.SEARCH -> 2
    Route.DASHBOARD -> 3
    Route.PROFILE -> 4
    else -> -1 // Should not happen
  }
}

open class NavigationActions(
    private val navController: NavHostController,
) {
  var currentScreen by mutableStateOf(Screen.WELCOME)

  /**
   * Navigate to the specified [TopLevelDestination]
   *
   * @param destination The top level destination to navigate to Clear the back stack when
   *   navigating to a new destination This is useful when navigating to a new screen from the
   *   bottom navigation bar as we don't want to keep the previous screen in the back stack
   */
  open fun navigateTo(destination: TopLevelDestination) {
    currentScreen = routeToScreen(destination.route)
    navController.navigate(destination.route) {
      popUpTo(navController.graph.findStartDestination().id) {
        saveState = true
        inclusive = true
      }
      launchSingleTop = true
      if (destination.route != Route.WELCOME) {
        restoreState = true
      }
    }
  }

  /**
   * Navigate to the specified screen.
   *
   * @param screen The screen to navigate to
   */
  open fun navigateTo(screen: String) {
    currentScreen = screen
    navController.navigate(screen)
  }

  /** Navigate back to the previous screen. */
  open fun goBack() {
    navController.popBackStack()
    currentScreen = routeToScreen(currentRoute())
  }

  /**
   * Get the current route of the navigation controller.
   *
   * @return The current route
   */
  open fun currentRoute(): String {
    return screenToRoute(navController.currentDestination?.route ?: "")
  }
}
