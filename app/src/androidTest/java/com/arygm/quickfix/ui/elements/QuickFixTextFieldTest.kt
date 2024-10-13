package com.arygm.quickfix.ui.elements

import QuickFixTextField
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInput
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class QuickFixTextFieldTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var onValueChange: (String) -> Unit
  private lateinit var label: String
  private lateinit var value: String
  private lateinit var errorText: String

  @Before
  fun setUp() {
    onValueChange = mock() // Mocking the onValueChange lambda function
    value = ""
    label = "Enter Text"
    errorText = "Invalid input"
  }

  @Test
  fun quickFixTextFieldIsDisplayed() {
    composeTestRule.setContent {
      QuickFixTextField(value = value, onValueChange = onValueChange, label = label)
    }

    // Check if the text field is displayed
    composeTestRule.onNodeWithTag("textField").assertIsDisplayed()

    // Check if the label is displayed correctly
    composeTestRule.onNodeWithTag("textField").assertTextContains(value)
  }

  @Test
  fun quickFixTextFieldUpdatesTextOnInput() {
    val inputText = "Updated Text"

    composeTestRule.setContent {
      QuickFixTextField(value = value, onValueChange = onValueChange, label = label)
    }

    // Simulate text input and verify the value change
    composeTestRule.onNodeWithTag("textField").performTextInput(inputText)

    verify(onValueChange).invoke(inputText)
  }

  @Test
  fun quickFixTextFieldShowsErrorTextWhenErrorOccurs() {
    composeTestRule.setContent {
      QuickFixTextField(
          value = value,
          onValueChange = onValueChange,
          label = label,
          isError = true,
          errorText = errorText,
          showError = true)
    }

    // Check if the error text is displayed
    composeTestRule.onNodeWithTag("errorText").assertIsDisplayed().assertTextEquals(errorText)
  }
}
