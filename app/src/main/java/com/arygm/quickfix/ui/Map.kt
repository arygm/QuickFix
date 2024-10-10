package com.arygm.quickfix.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.arygm.quickfix.ui.navigation.BottomNavigationMenu
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navigationActions: NavigationActions, isUser: Boolean = true, LoD: Boolean) {

  val color1 = if (LoD) Color(0xFFF16138) else Color(0xFF633040)
  val backgroundColor = if (LoD) Color.White else Color(0xFF282828)

  // Use Scaffold for the layout structure
  Scaffold(
      containerColor = backgroundColor,
      topBar = {
        Surface(
            shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
            modifier = Modifier.fillMaxWidth()) {
              TopAppBar(
                  title = {
                    Box(modifier = Modifier.fillMaxWidth()) {
                      Text(
                          text = "Map",
                          color = backgroundColor,
                          modifier = Modifier.align(Alignment.Center))
                    }
                  },
                  colors = TopAppBarDefaults.topAppBarColors(containerColor = color1))
            }
      },
      bottomBar = {
        // Boolean isUser = true for this HomeScreen
        BottomNavigationMenu(
            selectedItem = Route.MAP, // Start with the "Home" route
            onTabSelect = { selectedDestination ->
              // Use this block to navigate based on the selected tab
              navigationActions.navigateTo(selectedDestination)
            },
            isUser = isUser, // Assuming the user is of type User,
            LoD = LoD)
      },
      content = { padding ->
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "Welcome to the MAP Screen", modifier = Modifier.padding(padding))
            }
      })
}
