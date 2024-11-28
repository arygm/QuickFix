// QuickFixSlidingWindowTest.kt

package com.arygm.quickfix.ui.elements

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeRight
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuickFixSlidingWindowTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun slidingWindowIsDisplayedWhenVisible() {
    var isDismissed = false

    composeTestRule.setContent {
      QuickFixSlidingWindow(isVisible = true, onDismiss = { isDismissed = true }) {
        // Content inside the sliding window
        Box(modifier = Modifier.fillMaxSize().testTag("SlidingWindowContent"))
      }
    }

    // Wait for the animation to finish
    composeTestRule.mainClock.advanceTimeBy(200)

    // Check that the sliding window is displayed
    composeTestRule.onNodeWithTag("QuickFixSlidingWindowContent").assertExists().assertIsDisplayed()
  }

  @Test
  fun slidingWindowIsNotDisplayedWhenNotVisible() {
    composeTestRule.setContent {
      QuickFixSlidingWindow(isVisible = false, onDismiss = {}) {
        // Content inside the sliding window
        Box(modifier = Modifier.fillMaxSize().testTag("SlidingWindowContent"))
      }
    }

    // Wait to ensure any animations are completed
    composeTestRule.waitForIdle()

    // Check that the sliding window is not displayed
    composeTestRule.onNodeWithTag("QuickFixSlidingWindowContent").assertDoesNotExist()
  }

  @Test
  fun slidingWindowSnapsBackWhenDraggedBelowThreshold() {
    var isDismissed = false

    composeTestRule.setContent {
      QuickFixSlidingWindow(isVisible = true, onDismiss = { isDismissed = true }) {
        // Content inside the sliding window
        Box(modifier = Modifier.fillMaxSize().testTag("SlidingWindowContent"))
      }
    }

    val rootNode = composeTestRule.onRoot().fetchSemanticsNode()
    val screenWidth = rootNode.layoutInfo.width.toFloat()

    // Wait for the animation to finish
    composeTestRule.mainClock.advanceTimeBy(200)

    // Perform drag to the right below the dismiss threshold
    val slidingWindow =
        composeTestRule
            .onNodeWithTag("QuickFixSlidingWindowContent")
            .assertExists()
            .assertIsDisplayed()

    val windowSize = slidingWindow.fetchSemanticsNode().size

    // Dismiss threshold is 25% of window width
    val dismissThreshold = windowSize.width * 0.25f

    // Drag distance below the threshold
    val dragDistance = dismissThreshold - 10f

    slidingWindow.performTouchInput {
      swipeRight(startX = screenWidth / 2f, endX = screenWidth / 2f + dragDistance)
    }

    // Wait for any state updates
    composeTestRule.waitForIdle()

    // Check that onDismiss was not called
    assertTrue("onDismiss should not have been called when dragged below threshold", !isDismissed)

    // Check that the sliding window is still displayed
    composeTestRule.onNodeWithTag("QuickFixSlidingWindowContent").assertExists().assertIsDisplayed()
  }
}
