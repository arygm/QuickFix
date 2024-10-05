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

@Composable
fun CalendarScreen(
    navigationActions: NavigationActions,
    LoD: Boolean,
    isUser: Boolean
) {
    val backgroundColor = if (LoD) Color.White else Color(0xFF282828)
    Scaffold(
        containerColor = backgroundColor,
        content = { pd ->
            Text(
                modifier = Modifier.padding(pd),
                text = "Calendar Screen"
            )
        },
        bottomBar = {
            BottomNavigationMenu(
                onTabSelect = { destination -> navigationActions.navigateTo(destination) },
                isUser = isUser,
                selectedItem = Route.CALENDAR,
                LoD = LoD
            )
        }
    )
}