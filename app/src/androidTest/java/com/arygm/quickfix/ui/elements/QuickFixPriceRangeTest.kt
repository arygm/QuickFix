package com.arygm.quickfix.ui.elements

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.height
import androidx.compose.ui.unit.width
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class QuickFixPriceRangeTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun initialRenderingTest() {
    composeTestRule.setContent {
      QuickFixPriceRange(
          modifier = Modifier.width(300.dp).height(80.dp),
          rangeColor = Color.Green,
          backColor = Color.LightGray,
          barHeight = 8.dp,
          circleRadius = 12.dp,
          cornerRadius = CornerRadius(4f, 4f),
          minValue = 0,
          maxValue = 3000,
          progress1InitialValue = 500,
          progress2InitialValue = 2500,
          tooltipSpacing = 4.dp,
          tooltipWidth = 50.dp,
          tooltipHeight = 30.dp,
          tooltipTriangleSize = 5.dp,
          onProgressChanged = { _, _ -> })
    }

    // Verify that the component is displayed
    composeTestRule.onNodeWithTag("QuickFixPriceRange").assertExists()
  }

  @Test
  fun dragLeftCircleTest() {
    var updatedValue1 = 0
    var updatedValue2 = 0

    composeTestRule.setContent {
      QuickFixPriceRange(
          modifier = Modifier.width(300.dp).height(80.dp),
          rangeColor = Color.Green,
          backColor = Color.LightGray,
          barHeight = 8.dp,
          circleRadius = 12.dp,
          cornerRadius = CornerRadius(4f, 4f),
          minValue = 0,
          maxValue = 3000,
          progress1InitialValue = 500,
          progress2InitialValue = 2500,
          tooltipSpacing = 4.dp,
          tooltipWidth = 50.dp,
          tooltipHeight = 30.dp,
          tooltipTriangleSize = 5.dp,
          onProgressChanged = { value1, value2 ->
            updatedValue1 = value1
            updatedValue2 = value2
          })
    }

    val quickFixNode = composeTestRule.onNodeWithTag("QuickFixPriceRange")

    // Get node bounds
    val nodeBounds = quickFixNode.getUnclippedBoundsInRoot()

    val density = composeTestRule.density

    // Convert Dp to pixels
    val widthPx = with(density) { nodeBounds.width.toPx() }
    val heightPx = with(density) { nodeBounds.height.toPx() }
    val leftPx = with(density) { nodeBounds.left.toPx() }
    val topPx = with(density) { nodeBounds.top.toPx() }

    // Calculate starting position of the left circle
    val startX = leftPx + (500f / 3000f) * widthPx
    val startY = topPx + heightPx / 2f

    // Calculate target position
    val targetX = leftPx + (1000f / 3000f) * widthPx

    // Perform touch input
    quickFixNode.performTouchInput {
      down(Offset(startX, startY))
      moveTo(Offset(targetX, startY))
      up()
    }

    // Assert that onProgressChanged is called with expected values
    composeTestRule.runOnIdle {
      val expectedValue1 = 1000
      assert(updatedValue1 == expectedValue1) { "Expected $expectedValue1 but was $updatedValue1" }
    }
  }

  @Test
  fun dragRightCircleTest() {
    var updatedValue1 = 0
    var updatedValue2 = 0

    composeTestRule.setContent {
      QuickFixPriceRange(
          modifier = Modifier.width(300.dp).height(80.dp),
          rangeColor = Color.Green,
          backColor = Color.LightGray,
          barHeight = 8.dp,
          circleRadius = 12.dp,
          cornerRadius = CornerRadius(4f, 4f),
          minValue = 0,
          maxValue = 3000,
          progress1InitialValue = 500,
          progress2InitialValue = 2500,
          tooltipSpacing = 4.dp,
          tooltipWidth = 50.dp,
          tooltipHeight = 30.dp,
          tooltipTriangleSize = 5.dp,
          onProgressChanged = { value1, value2 ->
            updatedValue1 = value1
            updatedValue2 = value2
          })
    }

    val quickFixNode = composeTestRule.onNodeWithTag("QuickFixPriceRange")

    // Get node bounds
    val nodeBounds = quickFixNode.getUnclippedBoundsInRoot()

    val density = composeTestRule.density

    // Convert Dp to pixels
    val widthPx = with(density) { nodeBounds.width.toPx() }
    val heightPx = with(density) { nodeBounds.height.toPx() }
    val leftPx = with(density) { nodeBounds.left.toPx() }
    val topPx = with(density) { nodeBounds.top.toPx() }

    // Calculate starting position of the right circle
    val startX = leftPx + (2500f / 3000f) * widthPx
    val startY = topPx + heightPx / 2f

    // Calculate target position
    val targetX = leftPx + (2000f / 3000f) * widthPx

    // Perform touch input
    quickFixNode.performTouchInput {
      down(Offset(startX, startY))
      moveTo(Offset(targetX, startY))
      up()
    }

    // Assert that onProgressChanged is called with expected values
    composeTestRule.runOnIdle {
      val expectedValue2 = 2000
      assert(updatedValue2 == expectedValue2) { "Expected $expectedValue2 but was $updatedValue2" }
    }
  }

  @Test
  fun circlesCannotCrossTest() {
    var updatedValue1 = 0
    var updatedValue2 = 0

    composeTestRule.setContent {
      QuickFixPriceRange(
          modifier = Modifier.width(300.dp).height(80.dp),
          rangeColor = Color.Green,
          backColor = Color.LightGray,
          barHeight = 8.dp,
          circleRadius = 12.dp,
          cornerRadius = CornerRadius(4f, 4f),
          minValue = 0,
          maxValue = 3000,
          progress1InitialValue = 1500,
          progress2InitialValue = 2000,
          tooltipSpacing = 4.dp,
          tooltipWidth = 50.dp,
          tooltipHeight = 30.dp,
          tooltipTriangleSize = 5.dp,
          onProgressChanged = { value1, value2 ->
            updatedValue1 = value1
            updatedValue2 = value2
          })
    }

    val quickFixNode = composeTestRule.onNodeWithTag("QuickFixPriceRange")

    // Get node bounds
    val nodeBounds = quickFixNode.getUnclippedBoundsInRoot()

    val density = composeTestRule.density

    // Convert Dp to pixels
    val widthPx = with(density) { nodeBounds.width.toPx() }
    val heightPx = with(density) { nodeBounds.height.toPx() }
    val leftPx = with(density) { nodeBounds.left.toPx() }
    val topPx = with(density) { nodeBounds.top.toPx() }

    // Attempt to drag left circle past the right circle
    val leftStartX = leftPx + (1500f / 3000f) * widthPx
    val rightStartX = leftPx + (2000f / 3000f) * widthPx
    val startY = topPx + heightPx / 2f
    val targetX = rightStartX + 50f // Attempt to cross over

    quickFixNode.performTouchInput {
      down(Offset(leftStartX, startY))
      moveTo(Offset(targetX, startY))
      up()
    }

    // Assert that left value is not greater than right value
    composeTestRule.runOnIdle {
      assert(updatedValue1 <= updatedValue2) { "Left value should not exceed right value" }
    }
  }

  @Test
  fun tooltipOverlappingTest() {
    var updatedValue1 = 0
    var updatedValue2 = 0

    composeTestRule.setContent {
      QuickFixPriceRange(
          modifier = Modifier.width(300.dp).height(80.dp),
          rangeColor = Color.Green,
          backColor = Color.LightGray,
          barHeight = 8.dp,
          circleRadius = 12.dp,
          cornerRadius = CornerRadius(4f, 4f),
          minValue = 0,
          maxValue = 3000,
          progress1InitialValue = 500,
          progress2InitialValue = 2500,
          tooltipSpacing = 4.dp,
          tooltipWidth = 50.dp,
          tooltipHeight = 30.dp,
          tooltipTriangleSize = 5.dp,
          onProgressChanged = { value1, value2 ->
            updatedValue1 = value1
            updatedValue2 = value2
          })
    }

    val quickFixNode = composeTestRule.onNodeWithTag("QuickFixPriceRange")

    // Get node bounds
    val nodeBounds = quickFixNode.getUnclippedBoundsInRoot()

    val density = composeTestRule.density

    // Convert Dp to pixels
    val widthPx = with(density) { nodeBounds.width.toPx() }
    val heightPx = with(density) { nodeBounds.height.toPx() }
    val leftPx = with(density) { nodeBounds.left.toPx() }
    val topPx = with(density) { nodeBounds.top.toPx() }

    // Move circles close to each other
    val startY = topPx + heightPx / 2f
    val leftTargetX = leftPx + (1500f / 3000f) * widthPx
    val rightTargetX = leftTargetX + 1f // Positions that cause overlapping

    // Move left circle
    quickFixNode.performTouchInput {
      down(Offset(leftPx + (500f / 3000f) * widthPx, startY))
      moveTo(Offset(leftTargetX, startY))
      up()
    }

    // Move right circle
    quickFixNode.performTouchInput {
      down(Offset(leftPx + (2500f / 3000f) * widthPx, startY))
      moveTo(Offset(rightTargetX, startY))
      up()
    }

    // Assert that values are close but not crossed
    composeTestRule.runOnIdle {
      assert(updatedValue1 < updatedValue2) { "Left value should be less than right value" }
    }
  }

  @Test
  fun initialValuesAtExtremesTest() {
    var updatedValue1 = 0
    var updatedValue2 = 0

    composeTestRule.setContent {
      QuickFixPriceRange(
          modifier = Modifier.width(300.dp).height(80.dp),
          rangeColor = Color.Green,
          backColor = Color.LightGray,
          barHeight = 8.dp,
          circleRadius = 12.dp,
          cornerRadius = CornerRadius(4f, 4f),
          minValue = 0,
          maxValue = 3000,
          progress1InitialValue = 0,
          progress2InitialValue = 3000,
          tooltipSpacing = 4.dp,
          tooltipWidth = 50.dp,
          tooltipHeight = 30.dp,
          tooltipTriangleSize = 5.dp,
          onProgressChanged = { value1, value2 ->
            updatedValue1 = value1
            updatedValue2 = value2
          })
    }

    // Assert initial values
    composeTestRule.runOnIdle {
      assert(updatedValue1 == 0) { "Expected 0 but was $updatedValue1" }
      assert(updatedValue2 == 3000) { "Expected 3000 but was $updatedValue2" }
    }
  }

  @Test
  fun zeroRangeTest() {
    var updatedValue1 = 0
    var updatedValue2 = 0

    composeTestRule.setContent {
      QuickFixPriceRange(
          modifier = Modifier.width(300.dp).height(80.dp),
          rangeColor = Color.Green,
          backColor = Color.LightGray,
          barHeight = 8.dp,
          circleRadius = 12.dp,
          cornerRadius = CornerRadius(4f, 4f),
          minValue = 1000,
          maxValue = 1000,
          progress1InitialValue = 1000,
          progress2InitialValue = 1000,
          tooltipSpacing = 4.dp,
          tooltipWidth = 50.dp,
          tooltipHeight = 30.dp,
          tooltipTriangleSize = 5.dp,
          onProgressChanged = { value1, value2 ->
            updatedValue1 = value1
            updatedValue2 = value2
          })
    }

    // Assert initial values
    composeTestRule.runOnIdle {
      assert(updatedValue1 == 1000) { "Expected 1000 but was $updatedValue1" }
      assert(updatedValue2 == 1000) { "Expected 1000 but was $updatedValue2" }
    }
  }
}
