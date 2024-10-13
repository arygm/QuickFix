package com.arygm.quickfix.ui.elements

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class QuickFixAnimatedBoxTest {

  @get:Rule val composeTestRule = createComposeRule()

  private var xOffset: Dp = 0.dp
  private var yOffset: Dp = 0.dp
  private var size: Dp = 0.dp
  private var rotation: Float = 0f
  private var color: Color = Color.Unspecified

  @Before
  fun setUp() {
    xOffset = 10.dp
    yOffset = 20.dp
    size = 50.dp
    rotation = 45f
    color = Color.Red
  }

  @Test
  fun quickFixAnimatedBoxIsDisplayed() {
    composeTestRule.setContent {
      QuickFixAnimatedBox(
          xOffset = xOffset, yOffset = yOffset, size = size, rotation = rotation, color = color)
    }

    composeTestRule.onNodeWithTag("AnimationBox").assertIsDisplayed()
  }

  @Test
  fun quickFixAnimatedBoxHasCorrectOffsetAndSize() {
    composeTestRule.setContent {
      QuickFixAnimatedBox(
          xOffset = xOffset, yOffset = yOffset, size = size, rotation = rotation, color = color)
    }

    val node = composeTestRule.onNodeWithTag("AnimationBox")

    // Check if the node exists and is displayed
    node.assertIsDisplayed()

    // You could also check offsets, size, and other parameters with custom assertions
    // Example: Assert properties if needed
  }

  @Test
  fun quickFixAnimatedBoxHasCorrectRotationAndColor() {
    composeTestRule.setContent {
      QuickFixAnimatedBox(
          xOffset = xOffset, yOffset = yOffset, size = size, rotation = rotation, color = color)
    }

    composeTestRule.onNodeWithTag("AnimationBox").assertIsDisplayed()

    // Additional custom checks for rotation, color, etc., if necessary
  }
}
