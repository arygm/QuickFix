package com.arygm.quickfix.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.arygm.quickfix.ui.navigation.BottomNavigationMenu
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Route
import com.arygm.quickfix.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navigationActions: NavigationActions, isUser: Boolean = true) {

  // Use Scaffold for the layout structure
  Scaffold(
      containerColor = colorScheme.background,
      topBar = {
        Surface(
            shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
            modifier = Modifier.fillMaxWidth().testTag("TopAppBarSurface")) {
              TopAppBar(
                  title = {
                    Box(modifier = Modifier.fillMaxWidth().testTag("TopAppBarTitle")) {
                      Text(
                          text = "HOME",
                          style = MaterialTheme.typography.headlineLarge,
                          color = colorScheme.background,
                          modifier = Modifier.align(Alignment.Center).testTag("HomeText"))
                    }
                  },
                  colors = TopAppBarDefaults.topAppBarColors(containerColor = colorScheme.primary),
                  navigationIcon = {
                    IconButton(
                        onClick = { navigationActions.navigateTo(Screen.PROFILE) },
                        Modifier.testTag("ProfileButton")) {
                          Icon(
                              imageVector = Icons.Outlined.AccountCircle,
                              contentDescription = "Profile",
                              tint = colorScheme.background)
                        }
                  },
                  actions = {
                    IconButton(
                        onClick = { navigationActions.navigateTo(Screen.MESSAGES) },
                        Modifier.testTag("MessagesButton")) {
                          Icon(
                              imageVector = Icons.Outlined.Email,
                              contentDescription = "Messages",
                              tint = colorScheme.background)
                        }
                  })
            }
      },
      bottomBar = {
        // Boolean isUser = true for this HomeScreen
        BottomNavigationMenu(
            selectedItem = Route.HOME, // Start with the "Home" route
            onTabSelect = { selectedDestination ->
              // Use this block to navigate based on the selected tab
              navigationActions.navigateTo(selectedDestination)
            },
            isUser = isUser, // Assuming the user is of type User
        )
      },
      content = { padding ->
        Column(
            modifier = Modifier.fillMaxSize().testTag("HomeContent"),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                  text = "Welcome to the Home Screen",
                  modifier = Modifier.padding(padding).testTag("WelcomeText"))
            }
      })
}
