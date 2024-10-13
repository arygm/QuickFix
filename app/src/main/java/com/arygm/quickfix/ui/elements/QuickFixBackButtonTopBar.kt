package com.arygm.quickfix.ui.elements

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickFixBackButtonTopBar(
    onBackClick: () -> Unit,
    title: String = "",
    modifier: Modifier = Modifier
) {
  TopAppBar(
      title = { Text(text = title) },
      navigationIcon = {
        QuickFixBackButton(
            onClick = onBackClick,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.testTag("goBackButton"))
      },
      colors =
          TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
      modifier = modifier.testTag("goBackTopBar"))
}
