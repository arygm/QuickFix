package com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import com.arygm.quickfix.ui.navigation.TopLevelDestination

object UserRoute {
  const val HOME = "Home"
  const val SEARCH = "Search"
  const val DASHBOARD = "Dashboard"
  const val PROFILE = "Profile"
}

object UserScreen {
  const val HOME = "Home Screen"
  const val SEARCH = "Search Screen"
  const val DASHBOARD = "Dashboard Screen"
  const val MESSAGES = "Messages Screen"
  const val CALENDAR = "Calendar Screen"
  const val PROFILE = "Profile Screen"
  const val ACCOUNT_CONFIGURATION = "Account configuration Screen"
  const val TO_WORKER = "To Worker Screen"
  const val SEARCH_WORKER_RESULT = "Search Worker Result Screen"
  const val DISPLAY_UPLOADED_IMAGES = "Displayed images Screen"
  const val SEARCH_LOCATION = "SEARCH_Location Screen"
  const val ANNOUNCEMENT_DETAIL = "Announcement detail screen"
  const val QUICKFIX_ONBOARDING = "QuickFix OnBoarding Screen"
}

object UserTopLevelDestinations {
  val HOME = TopLevelDestination(route = UserRoute.HOME, icon = Icons.Filled.Home, textId = "Home")
  val PROFILE =
      TopLevelDestination(
          route = UserRoute.PROFILE, icon = Icons.Filled.AccountCircle, textId = "Profile")
  val SEARCH =
      TopLevelDestination(route = UserRoute.SEARCH, icon = Icons.Filled.Search, textId = "Search")
  val DASHBOARD =
      TopLevelDestination(
          route = UserRoute.DASHBOARD, icon = Icons.Filled.Menu, textId = "Dashboard")
}

val USER_TOP_LEVEL_DESTINATIONS =
    listOf(
        UserTopLevelDestinations.HOME,
        UserTopLevelDestinations.SEARCH,
        UserTopLevelDestinations.DASHBOARD,
        UserTopLevelDestinations.PROFILE,
    )

val getBottomBarIdUser: (String) -> Int = { route ->
  when (route) {
    UserRoute.HOME -> 1
    UserRoute.SEARCH -> 2
    UserRoute.DASHBOARD -> 3
    UserRoute.PROFILE -> 4
    UserScreen.HOME -> 1
    UserScreen.SEARCH -> 2
    UserScreen.DASHBOARD -> 3
    UserScreen.PROFILE -> 4
    else -> -1 // Should not happen
  }
}
