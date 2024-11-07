package com.arygm.quickfix.ui.search

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import org.junit.Rule
import org.junit.Test

class SearchCategoryButtonTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun searchCategoryButton_displaysTitleAndDescription() {
    composeTestRule.setContent {
      SearchCategoryButton(
          icon = Icons.Outlined.Build,
          title = "Plumbing",
          description = "Find plumbers for residential or commercial projects.",
          backgroundColor = Color.White,
          height = 75.dp,
          size = 40.dp)
    }

    // Check if title and description texts are displayed
    composeTestRule.onNodeWithText("Plumbing").assertIsDisplayed()
    composeTestRule
        .onNodeWithText("Find plumbers for residential or commercial projects.")
        .assertIsDisplayed()
  }

  @Test
  fun searchCategoryButton_clickAction() {
    // Create a variable to track if the button was clicked
    val clicked = mutableStateOf(false)

    composeTestRule.setContent {
      SearchCategoryButton(
          icon = Icons.Outlined.Build,
          title = "Plumbing",
          description = "Find plumbers for residential or commercial projects.",
          backgroundColor = Color.White,
          height = 75.dp,
          size = 40.dp,
          onClick = { clicked.value = true })
    }

    // Perform a click on the button
    composeTestRule.onNodeWithText("Plumbing").performClick()

    // Assert that the click action was triggered
    assert(clicked.value)
  }

  @Test
  fun searchCategoryButton_checkBackgroundColor() {
    val testBackgroundColor = Color.White

    composeTestRule.setContent {
      SearchCategoryButton(
          icon = Icons.Outlined.Build,
          title = "Plumbing",
          description = "Find plumbers for residential or commercial projects.",
          backgroundColor = testBackgroundColor,
          height = 75.dp,
          size = 40.dp)
    }

    // Use the test tag to check for the expected background color
    composeTestRule
        .onNodeWithTag("backgroundColorTag-${testBackgroundColor.toArgb()}")
        .assertExists()
  }
}
