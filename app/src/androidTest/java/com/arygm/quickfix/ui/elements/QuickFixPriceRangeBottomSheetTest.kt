package com.arygm.quickfix.ui.elements

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class QuickFixPriceRangeBottomSheetTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun modalBottomSheetIsVisibleWhenShowModalBottomSheetIsTrue() {
    composeTestRule.setContent {
      QuickFixPriceRangeBottomSheet(
          showModalBottomSheet = true, onApplyClick = { _, _ -> }, onDismissRequest = {})
    }

    // Verify that the modal bottom sheet is displayed
    composeTestRule.onNodeWithTag("priceRangeModalSheet").assertIsDisplayed()
  }

  @Test
  fun modalBottomSheetIsNotVisibleWhenShowModalBottomSheetIsFalse() {
    composeTestRule.setContent {
      QuickFixPriceRangeBottomSheet(
          showModalBottomSheet = false, onApplyClick = { _, _ -> }, onDismissRequest = {})
    }

    // Verify that the modal bottom sheet is not displayed
    composeTestRule.onNodeWithTag("priceRangeModalSheet").assertDoesNotExist()
  }

  @Test
  fun modalBottomSheetDisplaysCorrectContent() {
    composeTestRule.setContent {
      QuickFixPriceRangeBottomSheet(
          showModalBottomSheet = true, onApplyClick = { _, _ -> }, onDismissRequest = {})
    }

    // Verify that the title is displayed
    composeTestRule
        .onNodeWithTag("priceRangeTitle")
        .assertIsDisplayed()
        .assertTextEquals("Price Range")

    // Verify that the left price text ("0$") is displayed
    composeTestRule.onNodeWithTag("leftPriceText").assertIsDisplayed()
    composeTestRule.onNodeWithText("0$").assertIsDisplayed()

    // Verify that the right price text ("3000$") is displayed
    composeTestRule.onNodeWithTag("rightPriceText").assertIsDisplayed()
    composeTestRule.onNodeWithText("3000$").assertIsDisplayed()

    // Verify that the QuickFixPriceRange component is displayed
    composeTestRule.onNodeWithTag("priceRangeSlider").assertIsDisplayed()

    // Verify that the Apply button is displayed
    composeTestRule.onNodeWithTag("applyButton").assertIsDisplayed().assert(hasText("Apply"))
  }

  @Test
  fun applyButtonCallsOnApplyClickWithCorrectValues() {
    var appliedValue1 = -1
    var appliedValue2 = -1
    var dismissCalled = false

    composeTestRule.setContent {
      QuickFixPriceRangeBottomSheet(
          showModalBottomSheet = true,
          onApplyClick = { value1, value2 ->
            appliedValue1 = value1
            appliedValue2 = value2
          },
          onDismissRequest = { dismissCalled = true })
    }

    // Simulate clicking the Apply button
    composeTestRule.onNodeWithTag("applyButton").performClick()

    // Assert that onApplyClick was called with initial values (500 and 2500)
    composeTestRule.runOnIdle {
      assertEquals(500, appliedValue1)
      assertEquals(2500, appliedValue2)
      assert(dismissCalled) { "Expected onDismissRequest to be called" }
    }
  }
}
