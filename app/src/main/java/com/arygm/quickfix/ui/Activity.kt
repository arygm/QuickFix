package com.arygm.quickfix.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.arygm.quickfix.ui.navigation.BottomNavigationMenu
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreen(
    navigationActions: NavigationActions,
    isUser: Boolean
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "ACTIVITY Screen") }
            )
        },
        bottomBar = {
            // Boolean isUser = true for this HomeScreen
            MeowBottomNavigationMenu(
                selectedItem = Route.ACTIVITY, // Start with the "Home" route
                onTabSelect = { selectedDestination ->
                    // Use this block to navigate based on the selected tab
                    navigationActions.navigateTo(selectedDestination)
                },
                isUser = isUser // Assuming the user is of type User
            )
        },
        content = { padding ->
            // Main content of the HomeScreen
            Text(
                text = "Welcome to the ACTIVITY Screen",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        }
    )
}