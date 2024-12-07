package com.arygm.quickfix.ui.elements

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.arygm.quickfix.ui.theme.QuickFixTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyList
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify

class ChooseServiceTypeSheetTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var onApplyClick: (List<String>) -> Unit
  private lateinit var onDismissRequest: () -> Unit

  @Before
  fun setup() {
    onApplyClick = mock()
    onDismissRequest = mock()
  }

  @Test
  fun chooseServiceTypeSheet_displaysCorrectly() {
    val serviceTypes = listOf("Exterior Painter", "Interior Painter")
    composeTestRule.setContent {
      QuickFixTheme {
        ChooseServiceTypeSheet(
            showModalBottomSheet = true,
            serviceTypes = serviceTypes,
            onApplyClick = onApplyClick,
            onDismissRequest = onDismissRequest,
            onClearClick = {},
            clearEnabled = false)
      }
    }

    // Assert the dialog is displayed
    composeTestRule.onNodeWithTag("chooseServiceTypeModalSheet").assertIsDisplayed()
    composeTestRule.onNodeWithTag("lazyServiceRow").assertIsDisplayed()
    composeTestRule.onNodeWithText("Service Type").assertIsDisplayed()

    // Assert each service type is displayed
    serviceTypes.forEach { service ->
      composeTestRule.onNodeWithTag("serviceText_$service").assertIsDisplayed()
      composeTestRule.onNodeWithText(service).assertIsDisplayed()
    }

    // Assert Apply and Reset buttons are displayed
    composeTestRule.onNodeWithTag("applyButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("resetButton").assertIsDisplayed()
  }

  @Test
  fun chooseServiceTypeSheet_applyButtonClick_invokesCallback() {
    val serviceTypes = listOf("Exterior Painter", "Interior Painter")
    composeTestRule.setContent {
      QuickFixTheme {
        ChooseServiceTypeSheet(
            showModalBottomSheet = true,
            serviceTypes = serviceTypes,
            onApplyClick = onApplyClick,
            onDismissRequest = onDismissRequest,
            onClearClick = {},
            clearEnabled = false)
      }
    }

    // Click on Apply button
    composeTestRule.onNodeWithTag("applyButton").performClick()

    // Verify the apply callback is triggered with an empty list
    verify(onApplyClick).invoke(emptyList())
  }

  @Test
  fun chooseServiceTypeSheet_resetButtonClick_clearsSelection() {
    val serviceTypes = listOf("Exterior Painter", "Interior Painter")
    composeTestRule.setContent {
      QuickFixTheme {
        ChooseServiceTypeSheet(
            showModalBottomSheet = true,
            serviceTypes = serviceTypes,
            onApplyClick = onApplyClick,
            onDismissRequest = onDismissRequest,
            onClearClick = {},
            clearEnabled = false)
      }
    }

    // Select a service
    composeTestRule.onNodeWithTag("serviceText_Exterior Painter").performClick()

    // Click on Reset button
    composeTestRule.onNodeWithTag("resetButton").performClick()

    // Verify the selection list is cleared
    verify(onApplyClick, never()).invoke(anyList())
  }

  @Test
  fun chooseServiceTypeSheet_serviceSelectionUpdatesState() {
    val serviceTypes = listOf("Exterior Painter", "Interior Painter")
    composeTestRule.setContent {
      QuickFixTheme {
        ChooseServiceTypeSheet(
            showModalBottomSheet = true,
            serviceTypes = serviceTypes,
            onApplyClick = onApplyClick,
            onDismissRequest = onDismissRequest,
            onClearClick = {},
            clearEnabled = false)
      }
    }

    // Click on "Exterior Painter" to select it
    composeTestRule.onNodeWithTag("serviceText_Exterior Painter").performClick()

    // Click on "Interior Painter" to select it
    composeTestRule.onNodeWithTag("serviceText_Interior Painter").performClick()

    // Click on Apply button
    composeTestRule.onNodeWithTag("applyButton").performClick()

    // Verify the apply callback is triggered with the selected services
    verify(onApplyClick).invoke(listOf("Exterior Painter", "Interior Painter"))
  }

  @Test
  fun chooseServiceTypeSheet_resetButtonDisablesWhenNoSelection() {
    val serviceTypes = listOf("Exterior Painter", "Interior Painter")
    composeTestRule.setContent {
      QuickFixTheme {
        ChooseServiceTypeSheet(
            showModalBottomSheet = true,
            serviceTypes = serviceTypes,
            onApplyClick = onApplyClick,
            onDismissRequest = onDismissRequest,
            onClearClick = {},
            clearEnabled = false)
      }
    }

    // Assert Reset button is initially not clickable (dimmed)
    composeTestRule.onNodeWithTag("resetButton").assertIsDisplayed()

    // Select a service
    composeTestRule.onNodeWithTag("serviceText_Exterior Painter").performClick()

    // Reset button should now be clickable
    composeTestRule.onNodeWithTag("resetButton").performClick()

    // Verify the list is cleared after reset
    verify(onApplyClick, never()).invoke(listOf("Exterior Painter"))
  }

  @Test
  fun chooseServiceTypeSheet_notDisplayedWhenShowModalBottomSheetIsFalse() {
    val serviceTypes = listOf("Exterior Painter", "Interior Painter")
    composeTestRule.setContent {
      QuickFixTheme {
        ChooseServiceTypeSheet(
            showModalBottomSheet = false,
            serviceTypes = serviceTypes,
            onApplyClick = onApplyClick,
            onDismissRequest = onDismissRequest,
            onClearClick = {},
            clearEnabled = false)
      }
    }

    // Assert the dialog is not displayed
    composeTestRule.onNodeWithTag("chooseServiceTypeModalSheet").assertDoesNotExist()
  }

  /** Test that the Clear button calls [onClearClick] when enabled. */
  @Test
  fun clearButtonCallsOnClearClickWhenEnabled() {
    var clearCalled = false
    var dismissCalled = false
    val serviceTypes = listOf("Exterior Painter", "Interior Painter")

    composeTestRule.setContent {
      QuickFixTheme {
        ChooseServiceTypeSheet(
            showModalBottomSheet = true,
            serviceTypes = serviceTypes,
            onApplyClick = onApplyClick,
            onDismissRequest = { dismissCalled = true },
            onClearClick = { clearCalled = true },
            clearEnabled = true)
      }
    }

    // Simulate clicking the Clear button
    composeTestRule.onNodeWithTag("resetButton").performClick()

    // Assert that onClearClick and onDismissRequest were called
    composeTestRule.runOnIdle {
      assert(clearCalled) { "Expected onClearClick to be called" }
      assert(dismissCalled) { "Expected onDismissRequest to be called" }
    }
  }

  /**
   * Test that the Clear button is disabled and does not call [onClearClick] when [clearEnabled] is
   * false.
   */
  @Test
  fun clearButtonDoesNotCallOnClearClickWhenDisabled() {
    var clearCalled = false
    var dismissCalled = false
    val serviceTypes = listOf("Exterior Painter", "Interior Painter")

    composeTestRule.setContent {
      QuickFixTheme {
        ChooseServiceTypeSheet(
            showModalBottomSheet = true,
            serviceTypes = serviceTypes,
            onApplyClick = onApplyClick,
            onDismissRequest = { dismissCalled = true },
            onClearClick = { clearCalled = true },
            clearEnabled = false)
      }
    }

    // Simulate clicking the Clear button
    composeTestRule.onNodeWithTag("resetButton").performClick()

    // Assert that onClearClick was not called
    composeTestRule.runOnIdle {
      assert(!clearCalled) { "Expected onClearClick not to be called" }
      assert(!dismissCalled) { "Expected onDismissRequest not to be called" }
    }
  }
}
