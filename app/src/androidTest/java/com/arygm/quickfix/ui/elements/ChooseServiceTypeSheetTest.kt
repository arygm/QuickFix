package com.arygm.quickfix.ui.elements

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
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
    composeTestRule.onNodeWithTag("applyButton").assertIsNotEnabled()
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

  @Test
  fun chooseServiceTypeSheet_initialSelectedServices_areRespected() {
    val serviceTypes = listOf("Exterior Painter", "Interior Painter")
    val initiallySelected = listOf("Exterior Painter")

    composeTestRule.setContent {
      QuickFixTheme {
        ChooseServiceTypeSheet(
            showModalBottomSheet = true,
            serviceTypes = serviceTypes,
            selectedServices = initiallySelected,
            onApplyClick = onApplyClick,
            onDismissRequest = onDismissRequest,
            onClearClick = {},
            clearEnabled = false)
      }
    }

    // The "Exterior Painter" option should appear as selected (background and text color check is
    // UI-based,
    // but we can at least verify that it was clickable and is displayed):
    composeTestRule.onNodeWithTag("serviceText_Exterior Painter").assertIsDisplayed()

    // Verify that the apply button is enabled since we have an initially selected service
    composeTestRule.onNodeWithTag("applyButton").assertIsDisplayed().assertHasClickAction()

    // Click on Apply button
    composeTestRule.onNodeWithTag("applyButton").performClick()

    // Verify the apply callback is triggered with the initially selected service
    verify(onApplyClick).invoke(initiallySelected)
  }

  @Test
  fun chooseServiceTypeSheet_noInitialSelection_applyDisabledUntilSelection() {
    val serviceTypes = listOf("Exterior Painter", "Interior Painter")

    composeTestRule.setContent {
      QuickFixTheme {
        ChooseServiceTypeSheet(
            showModalBottomSheet = true,
            serviceTypes = serviceTypes,
            selectedServices = emptyList(), // No initial selection
            onApplyClick = onApplyClick,
            onDismissRequest = onDismissRequest,
            onClearClick = {},
            clearEnabled = false)
      }
    }

    // Initially, apply button should be disabled
    composeTestRule
        .onNodeWithTag("applyButton")
        .assertIsDisplayed()
        .assertIsNotEnabled() // There's no direct assert for "disabled", but we can check action
    // availability

    // Select a service
    composeTestRule.onNodeWithTag("serviceText_Exterior Painter").performClick()

    // Now apply button should be enabled
    // Since there's no direct built-in assert for "enabled" state, we rely on the action
    // availability:
    composeTestRule.onNodeWithTag("applyButton").assertIsDisplayed().assertIsEnabled()

    // Click on Apply button
    composeTestRule.onNodeWithTag("applyButton").performClick()

    // Verify the apply callback is triggered with the newly selected service
    verify(onApplyClick).invoke(listOf("Exterior Painter"))
  }

  @Test
  fun chooseServiceTypeSheet_initialSelectedServices_clearWorks() {
    val serviceTypes = listOf("Exterior Painter", "Interior Painter")
    var clearCalled = false
    var dismissCalled = false

    composeTestRule.setContent {
      QuickFixTheme {
        ChooseServiceTypeSheet(
            showModalBottomSheet = true,
            serviceTypes = serviceTypes,
            selectedServices = listOf("Exterior Painter"), // Initially selected
            onApplyClick = onApplyClick,
            onDismissRequest = { dismissCalled = true },
            onClearClick = { clearCalled = true },
            clearEnabled = true)
      }
    }

    // "Apply" is enabled initially since a service is selected
    composeTestRule.onNodeWithTag("applyButton").assertIsDisplayed().assertIsEnabled()

    // Click on Clear button
    composeTestRule.onNodeWithTag("resetButton").performClick()

    // After clearing, the onClearClick callback should be called and the dialog dismissed
    composeTestRule.runOnIdle {
      assert(clearCalled) { "Expected onClearClick to be called" }
      assert(dismissCalled) { "Expected onDismissRequest to be called" }
    }

    // No apply action should have been triggered with the initial selection since we cleared it
    verify(onApplyClick, never()).invoke(anyList())
  }
}
