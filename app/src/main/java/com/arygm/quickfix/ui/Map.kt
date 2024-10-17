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
import com.arygm.quickfix.ui.navigation.NavigationActions

@Composable
fun MapScreen(navigationActions: NavigationActions, isUser: Boolean = true) {

  // Use Scaffold for the layout structure
  Scaffold(
      containerColor = colorScheme.background,
      topBar = { QuickFixMainTopBar("MAP", modifier = Modifier.testTag("MapTopBar")) },
      content = { padding ->
        Column(
            modifier = Modifier.fillMaxSize().testTag("MapContent"),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                  text = "Welcome to the MAP Screen",
                  modifier = Modifier.padding(padding).testTag("MapText"))
            }
      })
}
