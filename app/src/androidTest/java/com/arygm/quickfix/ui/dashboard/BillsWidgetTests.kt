package com.arygm.quickfix.ui.dashboard

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class BillsWidgetTests {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun billSample_displaysDefaultNumberOfItems() {
    val testBills =
        listOf(
            BillSneakPeak("Bill 1", "Task 1", "2023-11-26", 100.0),
            BillSneakPeak("Bill 2", "Task 2", "2023-11-27", 200.0),
            BillSneakPeak("Bill 3", "Task 3", "2023-11-28", 300.0),
            BillSneakPeak("Bill 4", "Task 4", "2023-11-29", 400.0))

    composeTestRule.setContent {
      BillsWidget(
          billList = testBills, onShowAllClick = {}, onItemClick = {}, itemsToShowDefault = 3)
    }

    // Verify that only the default number of items (3) are displayed initially
    composeTestRule.onNodeWithTag("BillItem_Bill 1").assertIsDisplayed()
    composeTestRule.onNodeWithTag("BillItem_Bill 2").assertIsDisplayed()
    composeTestRule.onNodeWithTag("BillItem_Bill 3").assertIsDisplayed()

    // Verify that the fourth item is not displayed
    composeTestRule.onNodeWithTag("BillItem_Bill 4").assertDoesNotExist()
  }

  @Test
  fun billSample_displaysAllItems_whenShowAllClicked() {
    val testBills =
        listOf(
            BillSneakPeak("Bill 1", "Task 1", "2023-11-26", 100.0),
            BillSneakPeak("Bill 2", "Task 2", "2023-11-27", 200.0),
            BillSneakPeak("Bill 3", "Task 3", "2023-11-28", 300.0),
            BillSneakPeak("Bill 4", "Task 4", "2023-11-29", 400.0))

    composeTestRule.setContent {
      BillsWidget(
          billList = testBills, onShowAllClick = {}, onItemClick = {}, itemsToShowDefault = 3)
    }

    // Click the "Show All" button
    composeTestRule.onNodeWithTag("ShowAllButton").performClick()

    // Verify that all items are displayed
    composeTestRule.onNodeWithTag("BillItem_Bill 1").assertIsDisplayed()
    composeTestRule.onNodeWithTag("BillItem_Bill 2").assertIsDisplayed()
    composeTestRule.onNodeWithTag("BillItem_Bill 3").assertIsDisplayed()
    composeTestRule.onNodeWithTag("BillItem_Bill 4").assertIsDisplayed()
  }
}
