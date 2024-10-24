package com.arygm.quickfix.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.arygm.quickfix.ui.navigation.NavigationActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navigationActions: NavigationActions, isUser: Boolean = true) {

  // Use Scaffold for the layout structure
  Scaffold(
      containerColor = colorScheme.background,
      topBar = {
        TopAppBar(
            title = {
              Text(
                  text = "Search",
                  color = colorScheme.primary,
                  style = MaterialTheme.typography.headlineLarge,
                  modifier = Modifier.testTag("SearchTopBarTitle"))
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = colorScheme.background),
            modifier = Modifier.testTag("SearchTopBar"))
      },
      content = { padding ->
        Column(
            modifier = Modifier.fillMaxSize().testTag("SearchContent"),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                  text = "Welcome to the SEARCH Screen",
                  modifier = Modifier.padding(padding).testTag("SearchText"))
            }
      })
}
