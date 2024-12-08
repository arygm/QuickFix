package com.arygm.quickfix.ui.elements

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class QuickFixOfflineBarTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun offlineBar_isVisible_whenIsVisibleIsTrue() {
    val isVisible = mutableStateOf(true)

    composeTestRule.setContent { MaterialTheme { QuickFixOfflineBar(isVisible = isVisible.value) } }

    // Assert the bar is displayed with the correct text
    composeTestRule.onNodeWithText("No Internet Connection").assertExists().assertIsDisplayed()
  }

  @Test
  fun offlineBar_isHidden_whenIsVisibleIsFalse() {
    val isVisible = mutableStateOf(false)

    composeTestRule.setContent { MaterialTheme { QuickFixOfflineBar(isVisible = isVisible.value) } }

    // Assert the bar is not displayed
    composeTestRule.onNodeWithText("No Internet Connection").assertDoesNotExist()
  }
}
