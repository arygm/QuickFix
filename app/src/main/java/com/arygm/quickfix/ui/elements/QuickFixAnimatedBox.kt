package com.arygm.quickfix.ui.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.zIndex
import com.arygm.quickfix.utils.ANIMATED_BOX_ROTATION
import com.arygm.quickfix.utils.ANIMATED_BOX_SIZE
import com.arygm.quickfix.utils.BOX_OFFSET_Y

@Composable
fun QuickFixAnimatedBox(
    xOffset: Dp,
    yOffset: Dp = BOX_OFFSET_Y,
    size: Dp = ANIMATED_BOX_SIZE,
    rotation: Float = ANIMATED_BOX_ROTATION,
    color: Color = colorScheme.primary,
    widthRatio: Float = 1f,
    heightRatio: Float = 1f,
) {
  Box(
      modifier =
          Modifier.requiredSize(size * widthRatio, size * heightRatio)
              .offset(x = xOffset, y = yOffset)
              .graphicsLayer(rotationZ = rotation)
              .background(color)
              .zIndex(1f)
              .testTag("AnimationBox"))
}
