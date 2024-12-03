package com.arygm.quickfix.ui.elements

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
      QuickFixesWidget(quickFixList = sampleData, onShowAllClick = {}, onItemClick = {})
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
      QuickFixesWidget(quickFixList = sampleData, onShowAllClick = {}, onItemClick = {})
    }

    // Verify initial state shows only first three items
    composeTestRule.onAllNodesWithTagPrefix("QuickFixItem_").assertCountEquals(3)

    // Click on "Show All" button
    composeTestRule.onNodeWithTag("ShowAllButton").performClick()

    // Verify all items are displayed
    composeTestRule.onAllNodesWithTagPrefix("QuickFixItem_").assertCountEquals(5)

    // Click on "Show Less" button
    composeTestRule.onNodeWithTag("ShowAllButton").performClick()

    // Verify only three items are displayed again
    composeTestRule.onAllNodesWithTagPrefix("QuickFixItem_").assertCountEquals(3)
  }

  @Test
  fun testItemClick() {
    val sampleData = listOf(QuickFix("Ramy", "Bathroom painting", "Sat, 12 Oct 2024"))

    var clickedItem: QuickFix? = null

    composeTestRule.setContent {
      QuickFixesWidget(
          quickFixList = sampleData, onShowAllClick = {}, onItemClick = { clickedItem = it })
    }

    // Perform click on the item
    composeTestRule.onNodeWithTag("QuickFixItem_Ramy").performClick()

    // Verify that the clicked item is correct
    assert(clickedItem == sampleData[0])
  }

  // Helper function to find nodes with tag prefix
  private fun SemanticsNodeInteractionsProvider.onAllNodesWithTagPrefix(
      tagPrefix: String
  ): SemanticsNodeInteractionCollection {
    return onAllNodes(hasTestTagPrefix(tagPrefix))
  }

  private fun hasTestTagPrefix(prefix: String): SemanticsMatcher {
    return SemanticsMatcher("${SemanticsProperties.TestTag.name} starts with '$prefix'") {
      val testTag = it.config.getOrNull(SemanticsProperties.TestTag)
      testTag != null && testTag.startsWith(prefix)
    }
  }
}
