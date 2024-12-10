package com.arygm.quickfix.ui.elements

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import java.time.LocalDate
import java.time.LocalTime
import org.junit.Rule
import org.junit.Test

class QuickFixDateTimePickerTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun datePickerOpensAndSelectsDate() {
    var selectedDate: LocalDate? = null
    composeTestRule.setContent {
      QuickFixDateTimePicker(
          onDateTimeSelected = { date, _ -> selectedDate = date },
          onDismissRequest = {},
          timeViewTest = false)
    }
    val today = LocalDate.now()

    // Assert DatePickerDialog is displayed
    composeTestRule.onNodeWithText("Select Date").assertIsDisplayed()

    // Simulate date selection
    composeTestRule.onNodeWithText(today.dayOfMonth.toString()).performClick()
    composeTestRule.onNodeWithText("OK").performClick()

    composeTestRule.onNodeWithText("OK").performClick()

    // Assert that selectedDate is updated
    assert(selectedDate != null)
    assert(selectedDate == LocalDate.now())
  }

  @Test
  fun timePickerOpensAndSelectsTime() {
    var selectedTime: LocalTime? = null
    composeTestRule.setContent {
      QuickFixDateTimePicker(
          onDateTimeSelected = { _, time -> selectedTime = time },
          onDismissRequest = {},
          timeViewTest = false)
    }
    val today = LocalDate.now()

    // Simulate date selection to advance to time picker
    composeTestRule.onNodeWithText(today.dayOfMonth.toString()).performClick()
    composeTestRule.onNodeWithText("OK").performClick()

    // Assert TimePickerDialog is displayed
    composeTestRule.onNodeWithText("Select Time").assertIsDisplayed()

    // Simulate time selection (e.g., 14:30)

    composeTestRule.onNodeWithContentDescription("14 hours").performClick() // Select hour
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("OK").performClick()

    // Assert that selectedTime is updated
    assert(selectedTime != null)
    assert(selectedTime == LocalTime.of(14, LocalTime.now().minute))
  }

  @Test
  fun switchBetweenPickerAndInputMode() {
    composeTestRule.setContent {
      TimePickerDialog(onDismissRequest = {}, onTimeSelected = { _, _ -> }, timeViewTest = false)
    }

    // Assert picker is initially in input mode
    composeTestRule
        .onNodeWithContentDescription("time_picker_button_select_picker_mode")
        .assertIsDisplayed()

    // Switch to picker mode
    composeTestRule
        .onNodeWithContentDescription("time_picker_button_select_picker_mode")
        .performClick()

    // Assert picker is now in picker mode
    composeTestRule
        .onNodeWithContentDescription("time_picker_button_select_input_mode")
        .assertIsDisplayed()
  }

  @Test
  fun dismissDialogCorrectly() {
    var dismissed = false
    composeTestRule.setContent {
      QuickFixDateTimePicker(
          onDateTimeSelected = { _, _ -> },
          onDismissRequest = { dismissed = true },
          timeViewTest = false)
    }

    // Dismiss the dialog
    composeTestRule.onNodeWithText("Cancel").performClick()

    // Assert dismiss flag is true
    assert(dismissed)
  }

  @Test
  fun selectsDateAndTimeCorrectly() {
    var selectedDateTime: Pair<LocalDate, LocalTime>? = null
    composeTestRule.setContent {
      QuickFixDateTimePicker(
          onDateTimeSelected = { date, time -> selectedDateTime = date to time },
          onDismissRequest = {},
          timeViewTest = false)
    }
    val today = LocalDate.now()

    // Select date
    composeTestRule.onNodeWithText(today.dayOfMonth.toString()).performClick()
    composeTestRule.onNodeWithText("OK").performClick()

    // Select time
    composeTestRule.onNodeWithText("Select Time").performClick()
    composeTestRule
        .onNodeWithContentDescription("time_picker_button_select_picker_mode")
        .performClick()
    composeTestRule.onNodeWithContentDescription("14 hours").performClick() // Select hour
    composeTestRule.onNodeWithText("OK").performClick()

    // Assert that date and time are passed correctly
    assert(selectedDateTime != null)
    assert(selectedDateTime?.first == LocalDate.now())
    assert(selectedDateTime?.second == LocalTime.of(14, LocalTime.now().minute))
  }
}
