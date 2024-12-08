package com.arygm.quickfix

import android.graphics.Bitmap
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arygm.quickfix.ui.theme.QuickFixTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun bottomNavigation_isNotVisibleOnLoginScreen() {
    val testBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

    composeTestRule.setContent { QuickFixTheme { QuickFixApp(testBitmap) } }

    // Verify that the bottom bar is hidden on the Login screen
    composeTestRule.onNodeWithTag("BNM").assertIsNotDisplayed()
  }
}
