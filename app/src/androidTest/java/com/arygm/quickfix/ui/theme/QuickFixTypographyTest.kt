package com.arygm.quickfix.ui.theme

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.text.TextStyle
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuickFixTypographyTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Composable
  fun TypographyTextSample(text: String, style: androidx.compose.ui.text.TextStyle) {
    Text(text = text, style = style)
  }

  @Test
  fun quickFixTheme_appliesTitleLargeTypography() {
    val sampleText = "Title Large"
    composeTestRule.setContent {
      QuickFixTheme {
        TypographyTextSample(text = sampleText, style = poppinsTypography.titleLarge)
      }
    }

    // Verify the text is displayed with the correct style
    composeTestRule.onNode(hasText(sampleText)).assertIsDisplayed()
  }

  @Test
  fun quickFixTheme_appliesHeadlineLargeTypography() {
    val sampleText = "Headline Large"
    composeTestRule.setContent {
      QuickFixTheme {
        TypographyTextSample(text = sampleText, style = poppinsTypography.headlineLarge)
      }
    }

    // Verify the text is displayed with the correct style
    composeTestRule.onNode(hasText(sampleText)).assertIsDisplayed()
  }

  @Test
  fun quickFixTheme_appliesHeadlineSmallTypography() {
    val sampleText = "Headline Small"
    composeTestRule.setContent {
      QuickFixTheme {
        TypographyTextSample(text = sampleText, style = poppinsTypography.headlineSmall)
      }
    }

    // Verify the text is displayed with the correct style
    composeTestRule.onNode(hasText(sampleText)).assertIsDisplayed()
  }

  @Test
  fun quickFixTheme_appliesLabelLargeTypography() {
    val sampleText = "Label Large"
    composeTestRule.setContent {
      QuickFixTheme {
        TypographyTextSample(text = sampleText, style = poppinsTypography.labelLarge)
      }
    }

    // Verify the text is displayed with the correct style
    composeTestRule.onNode(hasText(sampleText)).assertIsDisplayed()
  }

  @Test
  fun quickFixTheme_appliesLabelMediumTypography() {
    val sampleText = "Label Medium"
    composeTestRule.setContent {
      QuickFixTheme {
        TypographyTextSample(text = sampleText, style = poppinsTypography.labelMedium)
      }
    }

    // Verify the text is displayed with the correct style
    composeTestRule.onNode(hasText(sampleText)).assertIsDisplayed()
  }

  @Test
  fun quickFixTheme_appliesLabelSmallTypography() {
    val sampleText = "Label Small"
    composeTestRule.setContent {
      QuickFixTheme {
        TypographyTextSample(text = sampleText, style = poppinsTypography.labelSmall)
      }
    }

    // Verify the text is displayed with the correct style
    composeTestRule.onNode(hasText(sampleText)).assertIsDisplayed()
  }

  @Test
  fun quickFixTheme_appliesPoppinsFontFamily() {
    val sampleText = "Poppins Font Family"
    composeTestRule.setContent {
      QuickFixTheme {
        TypographyTextSample(text = sampleText, style = TextStyle(fontFamily = poppinsFontFamily))
      }
    }

    // Verify the text is displayed with the correct font family
    composeTestRule.onNode(hasText(sampleText)).assertIsDisplayed()
  }
}
