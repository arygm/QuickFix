package com.arygm.quickfix.ui.search

import ServiceTypeBottomBar
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class ServiceTypeTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testTitleIsDisplayed() {
    composeTestRule.setContent {
      ServiceTypeBottomBar(
          serviceTypes = listOf("Exterior Painter", "Interior Painter"),
          selectedService = null,
          onServiceSelected = {},
          onApply = {},
          onReset = {})
    }

    // Check if the title is displayed
    composeTestRule
        .onNodeWithTag("serviceTypeTitle")
        .assertExists()
        .assertTextEquals("Service type")
  }

  @Test
  fun testServiceTypeItemsAreDisplayed() {
    composeTestRule.setContent {
      ServiceTypeBottomBar(
          serviceTypes = listOf("Exterior Painter", "Interior Painter"),
          selectedService = null,
          onServiceSelected = {},
          onApply = {},
          onReset = {})
    }

    // Check if the grid is displayed
    composeTestRule.onNodeWithTag("serviceTypeGrid").assertExists()

    // Check if specific service items are displayed
    composeTestRule
        .onNodeWithTag("serviceTypeItem_0")
        .assertExists()
        .assertTextEquals("Exterior Painter")

    composeTestRule
        .onNodeWithTag("serviceTypeItem_1")
        .assertExists()
        .assertTextEquals("Interior Painter")
  }

  @Test
  fun testApplyButtonClick() {
    var isApplied = false

    composeTestRule.setContent {
      ServiceTypeBottomBar(
          serviceTypes = listOf("Exterior Painter"),
          selectedService = null,
          onServiceSelected = {},
          onApply = { isApplied = true },
          onReset = {})
    }

    // Click the Apply button
    composeTestRule.onNodeWithTag("applyButton").assertExists().performClick()

    // Assert the Apply action is triggered
    assert(isApplied)
  }

  @Test
  fun testResetButtonClick() {
    var isReset = false

    composeTestRule.setContent {
      ServiceTypeBottomBar(
          serviceTypes = listOf("Exterior Painter"),
          selectedService = null,
          onServiceSelected = {},
          onApply = {},
          onReset = { isReset = true })
    }

    // Click the Reset button
    composeTestRule.onNodeWithTag("resetButton").assertExists().performClick()

    // Assert the Reset action is triggered
    assert(isReset)
  }

  @Test
  fun testSelectServiceType() {
    var selectedService: String? = null

    composeTestRule.setContent {
      ServiceTypeBottomBar(
          serviceTypes = listOf("Exterior Painter", "Interior Painter"),
          selectedService = selectedService,
          onServiceSelected = { selectedService = it },
          onApply = {},
          onReset = {})
    }

    // Click the first service type
    composeTestRule.onNodeWithTag("serviceTypeItem_0").assertExists().performClick()

    // Assert the correct service type was selected
    assert(selectedService == "Exterior Painter")
  }
}
