package com.arygm.quickfix.ui.search

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchWorkerResultScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testTopAppBarIsDisplayed() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val mockNavigationActions = MockNavigationActions(navController)
      SearchWorkerResult(navigationActions = mockNavigationActions)
    }

    composeTestRule.onNodeWithContentDescription("Back").assertExists().assertIsDisplayed()

    composeTestRule.onNodeWithContentDescription("Search").assertExists().assertIsDisplayed()
  }

  @Test
  fun testTitleAndDescriptionAreDisplayed() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val mockNavigationActions = MockNavigationActions(navController)
      SearchWorkerResult(navigationActions = mockNavigationActions)
    }

    composeTestRule.onNodeWithText("Sample Title").assertExists().assertIsDisplayed()

    composeTestRule
        .onNodeWithText("This is a sample description for the search result")
        .assertExists()
        .assertIsDisplayed()
  }

  @Test
  fun testFilterButtonsAreDisplayed() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val mockNavigationActions = MockNavigationActions(navController)
      SearchWorkerResult(navigationActions = mockNavigationActions)
    }

    // Target the LazyRow with the test tag "filter_buttons_row"
    val filterButtonsRow = composeTestRule.onNodeWithTag("filter_buttons_row")

    // Scroll to each button and check if it's displayed
    listOfButtons.forEachIndexed { index, button ->
      // Scroll to the specific index in the LazyRow
      filterButtonsRow.performScrollToIndex(index)

      // Check if the button is displayed after scrolling to its index
      composeTestRule
          .onNodeWithTag("filter_button_${button.text}")
          .assertExists()
          .assertIsDisplayed()
    }
  }

  @Test
  fun testProfileResultsAreDisplayed() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      val mockNavigationActions = MockNavigationActions(navController)
      SearchWorkerResult(navigationActions = mockNavigationActions)
    }

    // Target the LazyColumn with the test tag "worker_profiles_list"
    val workerProfilesList = composeTestRule.onNodeWithTag("worker_profiles_list")

    // Scroll through the LazyColumn to load each item and verify it’s displayed
    repeat(10) { index ->
      // Scroll to the specific index in the LazyColumn
      workerProfilesList.performScrollToIndex(index)

      composeTestRule.onNodeWithTag("worker_profile_result$index").assertIsDisplayed()
    }
  }
}
