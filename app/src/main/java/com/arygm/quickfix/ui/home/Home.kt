package com.arygm.quickfix.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.TopLevelDestination
import com.arygm.quickfix.ui.navigation.USER_TOP_LEVEL_DESTINATIONS
import com.arygm.quickfix.ui.navigation.Route
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import com.arygm.quickfix.ui.MeowBottomNavigationMenu

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigationActions: NavigationActions,
    isUser :Boolean
) {
    // Use Scaffold for the layout structure
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Home Screen") }
            )
        },
        bottomBar = {
            // Boolean isUser = true for this HomeScreen
            MeowBottomNavigationMenu(
                selectedItem = Route.HOME, // Start with the "Home" route
                onTabSelect = { selectedDestination ->
                    // Use this block to navigate based on the selected tab
                    navigationActions.navigateTo(selectedDestination)
                },
                isUser = isUser // Assuming the user is of type User
            )
        },
        content = { padding ->
            // Main content of the HomeScreen
            Text(text = "Welcome to the Home Screen", modifier = Modifier.fillMaxSize().padding(padding))
        }
    )
}