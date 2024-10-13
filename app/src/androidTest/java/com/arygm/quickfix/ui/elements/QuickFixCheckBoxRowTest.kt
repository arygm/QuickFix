package com.arygm.quickfix.ui.elements

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class QuickFixCheckBoxRowTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var onCheckedChange: (Boolean) -> Unit
  private lateinit var onUnderlinedTextClick: () -> Unit
  private var checked: Boolean = false
  private lateinit var label: String
  private lateinit var underlinedText: String

  @Before
  fun setUp() {
    onCheckedChange = mock() // Mocking the onCheckedChange lambda function
    onUnderlinedTextClick = mock() // Mocking the onUnderlinedTextClick lambda function
    label = "I agree to the terms"
    underlinedText = "Learn more"
  }

  @Test
  fun quickFixCheckBoxRowIsDisplayed() {
    composeTestRule.setContent {
      QuickFixCheckBoxRow(
          checked = checked,
          onCheckedChange = onCheckedChange,
          label = label,
          underlinedText = underlinedText,
          onUnderlinedTextClick = onUnderlinedTextClick)
    }

    // Check that the checkbox and text are displayed
    composeTestRule.onNodeWithTag("checkbox").assertIsDisplayed()

    composeTestRule.onNodeWithTag("checkBoxInfo").assertIsDisplayed().assertTextEquals(label)

    composeTestRule
        .onNodeWithTag("clickableLink")
        .assertIsDisplayed()
        .assertTextEquals(underlinedText)
  }

  @Test
  fun quickFixCheckBoxRowClickCheckboxChangesState() {
    composeTestRule.setContent {
      QuickFixCheckBoxRow(
          checked = checked,
          onCheckedChange = onCheckedChange,
          label = label,
          underlinedText = underlinedText,
          onUnderlinedTextClick = onUnderlinedTextClick)
    }

    // Simulate checkbox click and verify the state change
    composeTestRule.onNodeWithTag("checkbox").performClick()

    verify(onCheckedChange).invoke(!checked)
  }

  @Test
  fun quickFixCheckBoxRowUnderlinedTextClick() {
    composeTestRule.setContent {
      QuickFixCheckBoxRow(
          checked = checked,
          onCheckedChange = onCheckedChange,
          label = label,
          underlinedText = underlinedText,
          onUnderlinedTextClick = onUnderlinedTextClick)
    }

    // Perform a click on the underlined text and verify the click callback
    composeTestRule.onNodeWithTag("clickableLink").performClick()

    verify(onUnderlinedTextClick).invoke()
  }
}
