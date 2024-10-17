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
fun ActivityScreen(navigationActions: NavigationActions, isUser: Boolean) {
  // Use Scaffold for the layout structure
  Scaffold(
      containerColor = colorScheme.background,
      topBar = {
        QuickFixMainTopBar(title = "ACTIVITY", modifier = Modifier.testTag("ActivityTopBar"))
      },
      content = { padding ->
        Column(
            modifier = Modifier.fillMaxSize().testTag("ActivityContent"),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                  text = "Welcome to the ACTIVITY Screen",
                  modifier = Modifier.padding(padding).testTag("ActivityText"))
            }
      })
}
