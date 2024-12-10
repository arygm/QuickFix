package com.arygm.quickfix.ui.navigation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.arygm.quickfix.ui.noModeUI.navigation.NoModeScreen
import com.arygm.quickfix.ui.userModeUI.navigation.UserRoute
import com.arygm.quickfix.utils.routeToScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

open class NavigationActions(
    private val navController: NavHostController,
) {
  var currentScreen by mutableStateOf(NoModeScreen.WELCOME)

  // Allows the synchronization between the navigationActions and the bottom bar
  private val currentRoute_ = MutableStateFlow<String>(UserRoute.HOME)
  val currentRoute: StateFlow<String> = currentRoute_.asStateFlow()

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
      popUpTo(navController.graph.findStartDestination().id) { saveState = true }
      launchSingleTop = true
      if (destination.route != NoModeScreen.WELCOME) {
        restoreState = true
      }
    }
    currentRoute_.value = currentRoute()
  }

  /**
   * Navigate to the specified screen.
   *
   * @param screen The screen to navigate to
   */
  open fun navigateTo(screen: String) {
    currentScreen = screen
    navController.navigate(screen)
    currentRoute_.value = currentRoute()
  }

  /** Navigate back to the previous screen. */
  open fun goBack() {
    navController.popBackStack()
    currentScreen = routeToScreen(currentRoute())
    currentRoute_.value = currentRoute()
  }

  /**
   * Get the current route of the navigation controller.
   *
   * @return The current route
   */
  open fun currentRoute(): String {
    return navController.currentDestination?.route ?: ""
  }

  open fun saveToBackStack(key: String, value: Any) {
    navController.previousBackStackEntry?.savedStateHandle?.set(key, value)
  }

  open fun saveToCurBackStack(key: String, value: Any?) {
    val currentEntry = navController.currentBackStackEntry
    if (currentEntry == null) {
      Log.e("saveToBackStack", "No currentBackStackEntry available")
      return
    }

    if (value == null) {
      currentEntry.savedStateHandle.remove<Any>(key)
      Log.e("saveToBackStack", "Removed key: $key")
    } else {
      currentEntry.savedStateHandle.set(key, value)
      Log.e("saveToBackStack", "Saved key: $key with value: $value")
    }
  }

  open fun getFromBackStack(key: String): Any? {
    return navController.currentBackStackEntry?.savedStateHandle?.get<Any>(key)
  }
}
