package com.arygm.quickfix.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.arygm.quickfix.ui.elements.QuickFixMainTopBar
import com.arygm.quickfix.ui.navigation.BottomNavigationMenu
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Route

@Composable
fun AnnouncementScreen(navigationActions: NavigationActions, isUser: Boolean) {

  // Use Scaffold for the layout structure
  Scaffold(
      containerColor = colorScheme.background,
      topBar = {
        QuickFixMainTopBar("ANNOUNCEMENT", modifier = Modifier.testTag("AnnouncementTopBar"))
      },
      bottomBar = {
        // Boolean isUser = true for this HomeScreen
        BottomNavigationMenu(
            selectedItem = Route.ANNOUNCEMENT, // Start with the "Home" route
            onTabSelect = { selectedDestination ->
              // Use this block to navigate based on the selected tab
              navigationActions.navigateTo(selectedDestination)
            },
            isUser = isUser, // Assuming the user is of type User,
        )
      },
      content = { padding ->
        Column(
            modifier = Modifier.fillMaxSize().testTag("AnnouncementContent"),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                  text = "Welcome to the ANNOUNCEMENT Screen",
                  modifier = Modifier.padding(padding).testTag("AnnouncementText"))
            }
      })
}
