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
import com.arygm.quickfix.ui.elements.QuickFixMainTopBar
import com.arygm.quickfix.ui.navigation.BottomNavigationMenu
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Route
import com.arygm.quickfix.ui.navigation.TopLevelDestination

@Composable
fun OtherScreen(navigationActions: NavigationActions, isUser: Boolean = true) {

  // Use Scaffold for the layout structure
  Scaffold(
      containerColor = colorScheme.background,
      topBar = { QuickFixMainTopBar("OTHER") },
      bottomBar = {
        // Use MeowBottomNavigationMenu for the bottom navigation bar
        BottomNavigationMenu(
            selectedItem = Route.OTHER,
            onTabSelect = { selectedTab: TopLevelDestination ->
              navigationActions.navigateTo(selectedTab)
            },
            isUser = isUser, // Pass the user type to determine the tabs
        )
      },
      content = { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                  text = "Welcome to the other features Screen",
                  modifier = Modifier.padding(paddingValues))
            }
      })
}
