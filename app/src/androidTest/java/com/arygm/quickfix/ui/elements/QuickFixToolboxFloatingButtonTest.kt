package com.arygm.quickfix.ui.elements

import QuickFixToolboxFloatingButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuickFixToolboxFloatingButtonTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun mainIconIsDisplayedInitially() {
    composeTestRule.setContent {
      QuickFixToolboxFloatingButton(
          iconList = listOf(Icons.Default.Work, Icons.Default.Work), onIconClick = {})
    }

    // Check if the main icon is displayed
    composeTestRule.onNodeWithTag("mainIcon").assertIsDisplayed()

    // Sub icons should not be visible initially
    composeTestRule.onAllNodes(hasTestTagPrefix("subIcon")).assertCountEquals(0)
  }

  @Test
  fun clickingMainIconShowsSubIcons() {
    val subIcons = listOf(Icons.Default.Work, Icons.Default.Work, Icons.Default.Work)
    composeTestRule.setContent {
      QuickFixToolboxFloatingButton(iconList = subIcons, onIconClick = {})
    }

    // Click the main icon to expand
    composeTestRule.onNodeWithTag("mainIcon").performClick()

    // Now all sub-icons should be visible
    subIcons.indices.forEach { index ->
      composeTestRule.onNodeWithTag("subIcon$index").assertIsDisplayed()
    }
  }

  @Test
  fun clickingSubIconTriggersCallback() {
    val subIcons = listOf(Icons.Default.Work, Icons.Default.Work)
    var clickedIndex: Int? = null

    composeTestRule.setContent {
      QuickFixToolboxFloatingButton(
          iconList = subIcons, onIconClick = { index -> clickedIndex = index })
    }

    // Expand first
    composeTestRule.onNodeWithTag("mainIcon").performClick()

    // Click the first sub icon
    composeTestRule.onNodeWithTag("subIcon0").performClick()

    // Verify callback was triggered with correct index
    assertEquals(0, clickedIndex)
  }

  @Test
  fun clickingMainIconAgainHidesSubIcons() {
    val subIcons = listOf(Icons.Default.Work, Icons.Default.Work, Icons.Default.Work)
    composeTestRule.setContent {
      QuickFixToolboxFloatingButton(iconList = subIcons, onIconClick = {})
    }

    // Expand the button
    composeTestRule.onNodeWithTag("mainIcon").performClick()

    // Verify sub icons are shown
    subIcons.indices.forEach { index ->
      composeTestRule.onNodeWithTag("subIcon$index").assertIsDisplayed()
    }

    // Collapse the button
    composeTestRule.onNodeWithTag("mainIcon").performClick()

    // Verify sub icons are no longer visible
    composeTestRule.onAllNodes(hasTestTagPrefix("subIcon")).assertCountEquals(0)
  }

  // Helper matcher for subIcon test tags
  private fun hasTestTagPrefix(prefix: String): SemanticsMatcher {
    return hasTestTag(complicationTagMatcher(prefix).toString())
  }

  private fun complicationTagMatcher(prefix: String): (String) -> Boolean = {
    it.startsWith(prefix)
  }
}
