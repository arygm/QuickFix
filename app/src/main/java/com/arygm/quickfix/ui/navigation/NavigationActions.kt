package com.arygm.quickfix.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

object Route {
    const val WELCOME = "Welcome"
    const val HOME = "Home"
    const val ANNOUNCEMENT = "Annoucement"
    const val ACTIVITY = "Activity"
    const val OTHER = "Other"
    const val CALENDAR = "Calendar"
    const val MAP = "Map"
    const val PROFILE = "Profile"
    const val ACCOUNT_CONFIGURATION = "Account configuration"
}

object Screen {
    const val WELCOME = "Welcome Screen"
    const val LOGIN = "Login Screen"
    const val INFO = "Info Screen"
    const val PASSWORD = "Password Screen"
    const val HOME = "Home Screen"
    const val ANNOUNCEMENT = "Announcement Screen"
    const val ACTIVITY = "Activity Screen"
    const val OTHER = "Other Screen"
    const val CALENDAR = "Calendar Screen"
    const val MAP = "Map Screen"
    const val PROFILE = "Profile Screen"
    const val MESSAGES = "Messages Screen"
    const val ACCOUNT_CONFIGURATION = "Account configuration Screen"
}

data class TopLevelDestination(val route: String, val icon: ImageVector, val textId: String)

object TopLevelDestinations {
    val HOME = TopLevelDestination(route = Route.HOME, icon = Icons.Filled.Home, textId = "Home")
    val ANNOUNCEMENT =
        TopLevelDestination(
            route = Route.ANNOUNCEMENT, icon = Icons.Filled.AddCircle, textId = "Announcement"
        )
    val ACTIVITY =
        TopLevelDestination(route = Route.ACTIVITY, icon = Icons.Filled.Menu, textId = "Activity")
    val OTHER =
        TopLevelDestination(route = Route.OTHER, icon = Icons.Filled.MoreVert, textId = "Other")
    val CALENDAR =
        TopLevelDestination(
            route = Route.CALENDAR, icon = Icons.Filled.DateRange, textId = "Calendar"
        )
    val MAP = TopLevelDestination(route = Route.MAP, icon = Icons.Filled.Place, textId = "Map")
    val PROFILE = TopLevelDestination(
        route = Route.PROFILE,
        icon = Icons.Filled.AccountCircle,
        textId = "Profile"
    )
}

val USER_TOP_LEVEL_DESTINATIONS =
    listOf(
        TopLevelDestinations.HOME,
        TopLevelDestinations.ANNOUNCEMENT,
        TopLevelDestinations.ACTIVITY,
        TopLevelDestinations.PROFILE
    )
val WORKER_TOP_LEVEL_DESTINATIONS =
    listOf(
        TopLevelDestinations.HOME,
        TopLevelDestinations.CALENDAR,
        TopLevelDestinations.MAP,
        TopLevelDestinations.ACTIVITY,
        TopLevelDestinations.PROFILE
    )

fun getBottomBarId(route: String, isUser: Boolean): Int {
    return when (route) {
        Route.HOME -> 1
        Route.ANNOUNCEMENT -> 2
        Route.CALENDAR -> 2
        Route.MAP -> 3
        Route.ACTIVITY -> if (isUser) 3 else 4
        Route.OTHER -> if (isUser) 4 else 5
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
    }

    /**
     * Get the current route of the navigation controller.
     *
     * @return The current route
     */
    open fun currentRoute(): String {
        return navController.currentDestination?.route ?: ""
    }
}
