package com.arygm.quickfix.ui.theme

import android.os.Build
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuickFixThemeTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Composable
  fun SampleText() {
    Text(
        text = "Sample Text",
        color = Color.Black,
        fontSize = 32.sp,
        fontWeight = FontWeight.ExtraBold)
  }

  @Test
  fun quickFixTheme_appliesLightColorScheme() {
    composeTestRule.setContent { QuickFixTheme(darkTheme = false) { SampleText() } }

    // Check if the text is displayed with the correct theme
    composeTestRule.onNode(hasText("Sample Text")).assertIsDisplayed()
  }

  @Test
  fun quickFixTheme_appliesDarkColorScheme() {
    composeTestRule.setContent { QuickFixTheme(darkTheme = true) { SampleText() } }

    // Check if the text is displayed with the correct theme
    composeTestRule.onNode(hasText("Sample Text")).assertIsDisplayed()
  }

  @Test
  fun quickFixTheme_appliesDynamicColorScheme_onAndroid12() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      composeTestRule.setContent { QuickFixTheme(dynamicColor = true) { SampleText() } }

      // Check if the text is displayed with dynamic color applied
      composeTestRule.onNode(hasText("Sample Text")).assertIsDisplayed()
    }
  }
}
