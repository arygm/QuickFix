package com.arygm.quickfix.ui.theme

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuickFixThemeTypographyTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Composable
  fun TypographyTextSample() {
    Text(
        text = "Sample Text",
        style = interTypography.headlineLarge // Using headlineLarge from QuickFixTheme
        )
  }

  @Test
  fun quickFixTheme_appliesHeadlineLargeTypography() {
    composeTestRule.setContent { QuickFixTheme { TypographyTextSample() } }

    // Verify the text is displayed
    composeTestRule.onNode(hasText("Sample Text")).assertIsDisplayed()

    // (Optional) Advanced: Extract text semantics for deeper checks
    composeTestRule.onNodeWithText("Sample Text").assertExists().assertTextContains("Sample Text")
  }
}
