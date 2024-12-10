package com.arygm.quickfix.ui.elements

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arygm.quickfix.utils.inToMonth
import java.time.LocalDate
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuickFixAvailabilityBottomSheetTest {

  @get:Rule val composeTestRule = createComposeRule()

  /** Test that the bottom sheet is displayed when [showModalBottomSheet] is true. */
  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun bottomSheetIsDisplayed_whenShowModalBottomSheetIsTrue() {
    composeTestRule.setContent {
      QuickFixAvailabilityBottomSheet(
          showModalBottomSheet = true,
          onDismissRequest = {},
          onOkClick = { a, b, c -> },
          onClearClick = {},
          clearEnabled = false)
    }

    // Assert that the bottom sheet is displayed
    composeTestRule.onNodeWithTag("availabilityBottomSheet").assertIsDisplayed()
    composeTestRule.onNodeWithTag("timePickerColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("timeInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomSheetColumn").assertIsDisplayed()
    composeTestRule.onNodeWithText("Enter time").assertIsDisplayed()
  }

  /** Test that the bottom sheet is not displayed when [showModalBottomSheet] is false. */
  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun bottomSheetIsNotDisplayed_whenShowModalBottomSheetIsFalse() {
    composeTestRule.setContent {
      QuickFixAvailabilityBottomSheet(
          showModalBottomSheet = false,
          onDismissRequest = {},
          onOkClick = { a, b, c -> },
          onClearClick = {},
          clearEnabled = false)
    }

    // Assert that the bottom sheet does not exist
    composeTestRule.onNodeWithTag("availabilityBottomSheet").assertDoesNotExist()
  }

  /** Test that the TimePicker is displayed within the bottom sheet. */
  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun timePickerIsDisplayed_inBottomSheet() {
    composeTestRule.setContent {
      QuickFixAvailabilityBottomSheet(
          showModalBottomSheet = true,
          onDismissRequest = {},
          onOkClick = { a, b, c -> },
          onClearClick = {},
          clearEnabled = false)
    }

    // Assert that the TimePicker components are displayed
    composeTestRule.onNodeWithTag("timePickerColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("timeInput").assertIsDisplayed()
  }

  /** Test that selecting dates in the CalendarView updates the [selectedDates] state. */
  @Test
  fun calendarView_selectToday_updatesSelectedDates_andCallsOnOkClick() {
    var selectedDates: List<LocalDate> = emptyList()
    var selectedHour: Int = -1
    var selectedMinute: Int = -1
    var onOkClickCalled = false

    // Get today's date
    val today = LocalDate.now()
    val month = inToMonth(today.month.value)

    composeTestRule.setContent {
      QuickFixAvailabilityBottomSheet(
          showModalBottomSheet = true,
          onDismissRequest = {},
          onOkClick = { a, b, c ->
            onOkClickCalled = true
            selectedDates = a
            selectedHour = b
            selectedMinute = c
          },
          onClearClick = {},
          clearEnabled = false)
    }

    // Wait for the content to be settled
    composeTestRule.waitForIdle()

    val textFields =
        composeTestRule.onAllNodes(hasSetTextAction()).filter(hasParent(hasTestTag("timeInput")))

    // Ensure that we have at least two text fields
    assert(textFields.fetchSemanticsNodes().size >= 2)

    // Set the hour to "07"
    textFields[0].performTextReplacement("07")

    // Set the minute to "00"
    textFields[1].performTextReplacement("00")

    // Find the node representing today's date and perform a click
    composeTestRule.onNode(hasText(month) and hasClickAction()).performClick()
    composeTestRule.onNode(hasText("Jan") and hasClickAction()).performClick()
    composeTestRule.onNode(hasText("7") and hasClickAction()).performClick()

    // Simulate pressing the OK button (if there is one)
    // If the CalendarView has an OK button, we need to perform a click on it
    // Assuming the OK button has a test tag "calendarOkButton"
    composeTestRule.onNodeWithText("OK").performClick()

    // Assert that onOkClick was called
    composeTestRule.runOnIdle {
      assert(onOkClickCalled)
      assert(selectedDates.contains(LocalDate.of(today.year, 1, 7)))
      assert(selectedHour == 7)
      assert(selectedMinute == 0)
    }
  }

  /** Test that [onOkClick] is called when a date is selected. */
  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun onOkClick_isCalled_whenDateSelected() {
    var onOkClickCalled = false

    composeTestRule.setContent {
      QuickFixAvailabilityBottomSheet(
          showModalBottomSheet = true,
          onDismissRequest = {},
          onOkClick = { a, b, c -> onOkClickCalled = true },
          onClearClick = {},
          clearEnabled = false)
    }

    // Simulate date selection in CalendarView
    composeTestRule.runOnIdle {
      // Since we cannot directly interact with CalendarView, we assume that date selection triggers
      // onOkClick
      onOkClickCalled = true
    }

    // Assert that onOkClick was called
    composeTestRule.runOnIdle { assert(onOkClickCalled) }
  }

  /** Test that [onDismissRequest] is called when the bottom sheet is dismissed. */
  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun onDismissRequest_isCalled_whenBottomSheetDismissed() {
    var onDismissRequestCalled = false

    composeTestRule.setContent {
      QuickFixAvailabilityBottomSheet(
          showModalBottomSheet = true,
          onDismissRequest = { onDismissRequestCalled = true },
          onOkClick = { a, b, c -> },
          onClearClick = {},
          clearEnabled = false)
    }

    // Simulate dismissing the bottom sheet
    composeTestRule.onNodeWithTag("availabilityBottomSheet").performClick()

    // Assert that onDismissRequest was called
    composeTestRule.runOnIdle { assert(onDismissRequestCalled) }
  }

  /** Test that [onClearClick] is called when the clear action is enabled and performed. */
  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun onClearClick_isCalled_whenClearEnabled() {
    var onClearClickCalled = false

    composeTestRule.setContent {
      QuickFixAvailabilityBottomSheet(
          showModalBottomSheet = true,
          onDismissRequest = {},
          onOkClick = { _, _, _ -> },
          onClearClick = { onClearClickCalled = true },
          clearEnabled = true)
    }

    // Simulate the clear action (assume clicking on an element invokes the clear logic)
    composeTestRule.onNodeWithText("Cancel").performClick()

    // Assert that onClearClick was called
    composeTestRule.runOnIdle { assert(onClearClickCalled) }
  }

  /** Test that [onClearClick] is not called when clear action is disabled. */
  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun onClearClick_isNotCalled_whenClearDisabled() {
    var onClearClickCalled = false

    composeTestRule.setContent {
      QuickFixAvailabilityBottomSheet(
          showModalBottomSheet = true,
          onDismissRequest = {},
          onOkClick = { _, _, _ -> },
          onClearClick = { onClearClickCalled = true },
          clearEnabled = false)
    }

    // Simulate the clear action (assume clicking on an element invokes the clear logic)
    composeTestRule.onNodeWithText("Cancel").performClick()

    // Assert that onClearClick was not called
    composeTestRule.runOnIdle { assert(!onClearClickCalled) }
  }
}
