package com.arygm.quickfix.ui.uiMode.workerMode.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Home
import com.arygm.quickfix.ui.navigation.TopLevelDestination

object WorkerRoute {
  const val HOME = "Home"
  const val ANNOUNCEMENT = "Search"
  const val MESSAGES = "Dashboard"
  const val PROFILE = "Profile"
}

object WorkerScreen {
  const val HOME = "Home Screen"
  const val ANNOUNCEMENT = "Search Screen"
  const val MESSAGES = "Dashboard Screen"
  const val PROFILE = "Profile Screen"
}

object WorkerTopLevelDestinations {
  val HOME =
      TopLevelDestination(route = WorkerRoute.HOME, icon = Icons.Outlined.Home, textId = "Home")
  val ANNOUNCEMENT =
      TopLevelDestination(
          route = WorkerRoute.ANNOUNCEMENT, icon = Icons.Outlined.Campaign, textId = "Announcement")
  val MESSAGES =
      TopLevelDestination(
          route = WorkerRoute.MESSAGES, icon = Icons.Filled.MailOutline, textId = "Messages")
  val PROFILE =
      TopLevelDestination(
          route = WorkerRoute.PROFILE, icon = Icons.Filled.PersonOutline, textId = "Profile")
}

val WORKER_TOP_LEVEL_DESTINATIONS =
    listOf(
        WorkerTopLevelDestinations.HOME,
        WorkerTopLevelDestinations.ANNOUNCEMENT,
        WorkerTopLevelDestinations.MESSAGES,
        WorkerTopLevelDestinations.PROFILE)

val getBottomBarIdWorker: (String) -> Int = { route ->
  when (route) {
    WorkerRoute.HOME -> 1
    WorkerRoute.ANNOUNCEMENT -> 2
    WorkerRoute.MESSAGES -> 3
    WorkerRoute.PROFILE -> 4
    else -> -1 // Should not happen
  }
}
