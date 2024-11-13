package com.arygm.quickfix.ui.search

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arygm.quickfix.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchWorkerProfileResultTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testProfileImageIsDisplayed() {
    composeTestRule.setContent {
      SearchWorkerProfileResult(
          profileImage = R.drawable.placeholder_worker,
          name = "Moha Abbes",
          category = "Exterior Painter",
          rating = 4.0f,
          reviewCount = 160,
          location = "Rennens",
          price = "42",
          onBookClick = {})
    }

    composeTestRule
        .onNodeWithContentDescription("Profile image of Moha Abbes, Exterior Painter")
        .assertExists()
        .assertIsDisplayed()
  }

  @Test
  fun testProfileDetailsAreDisplayed() {
    composeTestRule.setContent {
      SearchWorkerProfileResult(
          profileImage = R.drawable.placeholder_worker,
          name = "Moha Abbes",
          category = "Exterior Painter",
          rating = 4.0f,
          reviewCount = 160,
          location = "Rennens",
          price = "42",
          onBookClick = {})
    }

    composeTestRule.onNodeWithText("Moha Abbes").assertExists().assertIsDisplayed()

    composeTestRule.onNodeWithText("Exterior Painter").assertExists().assertIsDisplayed()

    composeTestRule.onNodeWithText("4.0 ★").assertExists().assertIsDisplayed()

    composeTestRule.onNodeWithText("(160+)").assertExists().assertIsDisplayed()

    composeTestRule.onNodeWithText("Rennens").assertExists().assertIsDisplayed()

    composeTestRule.onNodeWithText("42").assertExists().assertIsDisplayed()
  }

  @Test
  fun testBookButtonClick() {
    var clicked = false
    composeTestRule.setContent {
      SearchWorkerProfileResult(
          profileImage = R.drawable.placeholder_worker,
          name = "Moha Abbes",
          category = "Exterior Painter",
          rating = 4.0f,
          reviewCount = 160,
          location = "Rennens",
          price = "42",
          onBookClick = { clicked = true })
    }

    composeTestRule.onNodeWithText("Book").assertExists().assertIsDisplayed().performClick()

    assert(clicked)
  }
}
