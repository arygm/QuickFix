package com.arygm.quickfix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.arygm.quickfix.resources.C
import com.arygm.quickfix.ui.ActivityScreen
import com.arygm.quickfix.ui.AnnouncementScreen
import com.arygm.quickfix.ui.CalendarScreen
import com.arygm.quickfix.ui.HomeScreen
import com.arygm.quickfix.ui.MapScreen
import com.arygm.quickfix.ui.OtherScreen
import com.arygm.quickfix.ui.authentication.LogInScreen
import com.arygm.quickfix.ui.authentication.PasswordScreen
import com.arygm.quickfix.ui.authentication.RegistrationScreen
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Route
import com.arygm.quickfix.ui.navigation.Screen
import com.arygm.quickfix.ui.theme.SampleAppTheme
import com.github.se.bootcamp.ui.authentication.WelcomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SampleAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .semantics { testTag = C.Tag.main_screen_container },
                    color = MaterialTheme.colorScheme.background
                ) {
                    QuickFixApp()
                }
            }
        }
    }
}

@Composable
fun QuickFixApp() {
    val navController = rememberNavController()
    val navigationActions = NavigationActions(navController)
    val LoD = true
    val isUser = true //Todo Set this boolean value after the connexion

    NavHost(navController = navController, startDestination = Route.HOME) {
        navigation(
            startDestination = Screen.WELCOME,
            route = Route.WELCOME,
        ) {
            composable(Screen.WELCOME) { WelcomeScreen(navigationActions, true) }
            composable(Screen.LOGIN) { LogInScreen(navigationActions, true) }
            composable(Screen.INFO) { RegistrationScreen(navigationActions, true) }
            composable(Screen.PASSWORD) { PasswordScreen(navigationActions, true) }
        }
        navigation(
            startDestination = Screen.HOME,
            route = Route.HOME,
        ) {
            composable(Screen.HOME) { HomeScreen(navigationActions, LoD, isUser) }
        }
        navigation(
            startDestination = Screen.CALENDAR,
            route = Route.CALENDAR,
        ) {
            composable(Screen.CALENDAR) { CalendarScreen(navigationActions, LoD, isUser) }
        }
        navigation(
            startDestination = Screen.ANNOUNCEMENT,
            route = Route.ANNOUNCEMENT,
        ) {
            composable(Screen.ANNOUNCEMENT) { AnnouncementScreen(navigationActions, LoD, isUser) }
        }
        navigation(
            startDestination = Screen.MAP,
            route = Route.MAP,
        ) {
            composable(Screen.MAP) { MapScreen(navigationActions, LoD, isUser) }
        }
        navigation(
            startDestination = Screen.ACTIVITY,
            route = Route.ACTIVITY,
        ) {
            composable(Screen.ACTIVITY) { ActivityScreen(navigationActions, LoD, isUser) }
        }
        navigation(
            startDestination = Screen.OTHER,
            route = Route.OTHER,
        ) {
            composable(Screen.OTHER) { OtherScreen(navigationActions, LoD = LoD, isUser) }
        }

    }
}
