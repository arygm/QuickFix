package com.arygm.quickfix.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.arygm.quickfix.ui.navigation.BottomNavigationMenu
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Route
import com.arygm.quickfix.ui.navigation.TopLevelDestination

@Composable
fun OtherScreen(navigationActions: NavigationActions, isUser: Boolean = true, LoD: Boolean) {


    val backgroundColor = if (LoD) Color.White else Color(0xFF282828)
    // Use Scaffold for the layout structure
    Scaffold(
        containerColor = backgroundColor,
        content = { paddingValues ->
            // Main content for the OtherScreen
            Text(modifier = Modifier.padding(paddingValues), text = "Other Screen")
        },
        bottomBar = {
            // Use MeowBottomNavigationMenu for the bottom navigation bar
            BottomNavigationMenu(
                selectedItem = Route.OTHER,
                onTabSelect = { selectedTab: TopLevelDestination ->
                    navigationActions.navigateTo(selectedTab)
                },
                isUser = isUser, // Pass the user type to determine the tabs
                LoD = LoD
            )
        })
}
