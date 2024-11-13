package com.arygm.quickfix.kaspresso

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arygm.quickfix.kaspresso.element.QuickFixSearchBarElement
import com.arygm.quickfix.ui.elements.QuickFixTextFieldCustom
import com.arygm.quickfix.ui.theme.poppinsTypography
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuickFixTextFieldCustomTest : TestCase() {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testQuickFixTextFieldCustom() = run {
    val text = mutableStateOf("")
    // Step 1: Set up the content to test
    composeTestRule.setContent {
      QuickFixTextFieldCustom(
          showLeadingIcon = { true },
          showTrailingIcon = { true },
          leadingIcon = Icons.Outlined.Search,
          trailingIcon = { Icons.Default.Clear },
          descriptionLeadIcon = "Search",
          descriptionTrailIcon = "Clear",
          placeHolderText = "Find your perfect fix with QuickFix",
          shape = CircleShape,
          textStyle = poppinsTypography.bodyMedium,
          textColor = colorScheme.onBackground,
          placeHolderColor = colorScheme.onBackground,
          leadIconColor = colorScheme.onBackground,
          trailIconColor = colorScheme.onBackground,
          widthField = 330.dp,
          heightField = 40.dp,
          moveContentHorizontal = 5.dp,
          moveContentBottom = 0.dp,
          moveContentTop = 0.dp,
          sizeIconGroup = 30.dp,
          spaceBetweenLeadIconText = 0.dp,
          onValueChange = { newText -> text.value = newText },
          value = text.value,
          onClick = true)
    }
    step("Test UI elements are displayed when text is empty") {
      ComposeScreen.onComposeScreen<QuickFixSearchBarElement>(composeTestRule) {
        // Assert leading icon is displayed
        composeTestRule.onRoot(useUnmergedTree = true).printToLog("TAG")
        leadingIcon { assertIsDisplayed() }
        placeHolder {
          assertIsDisplayed()
          assertTextEquals("Find your perfect fix with QuickFix")
        }
        // Assert the trailing icon is not displayed because the text is empty
        clearButton { assertDoesNotExist() }
        // Assert the input field is displayed
        textFieldCustom { assertIsDisplayed() }
      }
    }

    step("Test text input and clear functionality") {
      ComposeScreen.onComposeScreen<QuickFixSearchBarElement>(composeTestRule) {
        // Input some text
        textFieldCustom { performTextInput("QuickFix") }

        // Verify the text input was successful and placeholder is gone
        textFieldCustom { assertTextEquals("QuickFix") }

        // The placeholder should no longer exist when the text is not empty
        placeHolder { assertDoesNotExist() }

        // Assert that the trailing icon (clear button) is now visible
        clearButton {
          assertIsDisplayed()
          assertIsEnabled()
          // Click the clear button to reset the text field
          performClick()
        }

        // Verify that the text field is cleared
        textFieldCustom { assertTextEquals("") }
      }
    }

    step("Test UI elements when text is non-empty and cleared") {
      ComposeScreen.onComposeScreen<QuickFixSearchBarElement>(composeTestRule) {
        // Assert the leading icon is still displayed
        leadingIcon { assertIsDisplayed() }

        // Assert the text field is empty after clearing
        textFieldCustom { assertTextEquals("") }

        // Assert that the placeholder is visible again
        placeHolder { assertIsDisplayed() }

        // Assert that the clear button is no longer visible after clearing the text
        clearButton { assertDoesNotExist() }
      }
    }

    step("Test horizontal scrolling behavior") {
      ComposeScreen.onComposeScreen<QuickFixSearchBarElement>(composeTestRule) {
        // Input long text to trigger horizontal scrolling
        textFieldCustom {
          performTextInput("This is a very long text to trigger scrolling behavior")
        }

        // Assert that the text field contains the input
        textFieldCustom {
          assertTextEquals("This is a very long text to trigger scrolling behavior")
        }
      }
    }
  }
}
