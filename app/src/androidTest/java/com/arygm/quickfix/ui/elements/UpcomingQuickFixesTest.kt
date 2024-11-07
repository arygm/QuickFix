package com.arygm.quickfix.ui.elements

// Import statements
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UpcomingQuickFixesTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testUpcomingQuickFixesDisplaysItems() {
    val sampleData =
        listOf(
            QuickFix("Ramy", "Bathroom painting", "Sat, 12 Oct 2024"),
            QuickFix("Mehdi", "Laying kitchen tiles", "Sun, 13 Oct 2024"),
            QuickFix("Moha", "Toilet plumbing", "Mon, 14 Oct 2024"))

    composeTestRule.setContent {
      UpcomingQuickFixes(quickFixList = sampleData, onShowAllClick = {}, onItemClick = {})
    }

    // Verify that the first three items are displayed
    sampleData.forEach {
      composeTestRule.onNodeWithText(it.name).assertIsDisplayed()
      composeTestRule.onNodeWithText(it.taskDescription).assertIsDisplayed()
      composeTestRule.onNodeWithText(it.date).assertIsDisplayed()
    }
  }

  @Test
  fun testShowAllButtonTogglesItemCount() {
    val sampleData = List(5) { index -> QuickFix("Name $index", "Task $index", "Date $index") }

    composeTestRule.setContent {
      UpcomingQuickFixes(quickFixList = sampleData, onShowAllClick = {}, onItemClick = {})
    }

    // Verify initial state shows only first three items
    composeTestRule.onAllNodesWithText("Name", substring = true).assertCountEquals(3)

    // Click on "Show All" button
    composeTestRule.onNodeWithText("Show All").performClick()

    // Verify all items are displayed
    composeTestRule.onAllNodesWithText("Name", substring = true).assertCountEquals(5)

    // Click on "Show Less" button
    composeTestRule.onNodeWithText("Show Less").performClick()

    // Verify only three items are displayed again
    composeTestRule.onAllNodesWithText("Name", substring = true).assertCountEquals(3)
  }

  @Test
  fun testItemClick() {
    val sampleData = listOf(QuickFix("Ramy", "Bathroom painting", "Sat, 12 Oct 2024"))

    var clickedItem: QuickFix? = null

    composeTestRule.setContent {
      UpcomingQuickFixes(
          quickFixList = sampleData, onShowAllClick = {}, onItemClick = { clickedItem = it })
    }

    // Perform click on the item
    composeTestRule.onNodeWithText("Ramy").performClick()

    // Verify that the clicked item is correct
    assert(clickedItem == sampleData[0])
  }
}
