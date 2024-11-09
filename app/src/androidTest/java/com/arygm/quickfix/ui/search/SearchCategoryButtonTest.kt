package com.arygm.quickfix.ui.search

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.arygm.quickfix.model.categories.WorkerCategory
import com.arygm.quickfix.ressources.C
import org.junit.Rule
import org.junit.Test

class SearchCategoryButtonTest {

  @get:Rule val composeTestRule = createComposeRule()

  private val item = WorkerCategory.PAINTING

  @Test
  fun searchCategoryButton_displaysTitleAndDescription() {
    val expandedState = mutableStateOf(false)
    composeTestRule.setContent {
      ExpandableCategoryItem(
          item = item,
          isExpanded = expandedState.value,
          onExpandedChange = { expandedState.value = it },
      )
    }

    // Check if title and description texts are displayed
    composeTestRule.onNodeWithText(item.displayName).assertIsDisplayed()
    composeTestRule.onNodeWithText(item.description).assertIsDisplayed()
  }

  @Test
  fun searchCategoryButton_clickAction() {
    // Create a variable to track if the button was clicked
    val expandedState = mutableStateOf(false)

    composeTestRule.setContent {
      ExpandableCategoryItem(
          item = item,
          isExpanded = expandedState.value,
          onExpandedChange = { expandedState.value = it },
      )
    }

    // Perform a click on the button
    composeTestRule.onNodeWithTag(C.Tag.expandableCategoryItem).performClick()

    // Assert that the click action was triggered
    assert(expandedState.value)
  }

  @Test
  fun testExpandableCategoryItem_AnimatedVisibility() {
    // Step 1: Control the clock to handle animations
    composeTestRule.mainClock.autoAdvance = false

    // Step 2: Set up the initial state
    val isExpandedState = mutableStateOf(false)
    val item = WorkerCategory.PAINTING // Replace with an appropriate WorkerCategory
    composeTestRule.setContent {
      // Provide LocalInspectionMode if you prefer to disable animations
      // CompositionLocalProvider(LocalInspectionMode provides true) {
      ExpandableCategoryItem(
          item = item,
          isExpanded = isExpandedState.value,
          onExpandedChange = { isExpandedState.value = it },
      )
      // }
    }

    // Step 3: Perform click to expand the item
    composeTestRule.onNodeWithTag(C.Tag.expandableCategoryItem).performClick()

    // Step 4: Advance the clock to allow animations to complete
    composeTestRule.mainClock.advanceTimeBy(1000) // Adjust based on your animation duration
    composeTestRule.waitForIdle()
    // Optionally, check for a specific subcategory
    val subCategoryName = "Residential Painting"
    composeTestRule
        .onNodeWithTag("${C.Tag.subCategoryName}_$subCategoryName")
        .assertIsDisplayed()
        .assertHasClickAction()

    // Step 6: Perform click to collapse the item
    composeTestRule.onNodeWithTag(C.Tag.expandableCategoryItem).performClick()

    // Advance the clock again for the collapse animation
    composeTestRule.mainClock.advanceTimeBy(1000)
    composeTestRule.waitForIdle()

    // Step 7: Assert that subcategories are no longer displayed
    composeTestRule.onNodeWithTag(C.Tag.subCategories).assertDoesNotExist()

    // Step 8: Restore the clock to auto-advance
    composeTestRule.mainClock.autoAdvance = true
  }
}
