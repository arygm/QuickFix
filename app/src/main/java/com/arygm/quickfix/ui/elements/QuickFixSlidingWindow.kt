package com.arygm.quickfix.ui.elements

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

@Composable
fun QuickFixSlidingWindow(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
  BoxWithConstraints {
    val density = LocalDensity.current
    val screenWidthPx = constraints.maxWidth.toFloat()
    val screenWidthDp = with(density) { constraints.maxWidth.toDp() }

    // Calculate window width (90% of the screen width) and the left gap (10%)
    val windowWidthDp = screenWidthDp * 0.9f
    val windowWidthPx = with(density) { windowWidthDp.toPx() }
    val gapWidthPx = screenWidthPx - windowWidthPx

    // Dismiss threshold (e.g., 25% of the window width)
    val dismissThreshold = windowWidthPx * 0.25f

    // State for drag offset
    var dragOffsetX by remember { mutableStateOf(0f) }

    // Animatable for the window's horizontal position
    val animatableOffsetX = remember { Animatable(screenWidthPx) }

    // Coroutine scope for launching animations
    val coroutineScope = rememberCoroutineScope()

    // Launch animation when isVisible changes
    LaunchedEffect(isVisible) {
      if (isVisible) {
        // Reset drag offset
        dragOffsetX = 0f
        // Animate in from off-screen to the gap position
        animatableOffsetX.snapTo(screenWidthPx)
        animatableOffsetX.animateTo(
            targetValue = gapWidthPx, animationSpec = tween(durationMillis = 100))
      } else {
        // Animate out to off-screen
        animatableOffsetX.animateTo(
            targetValue = screenWidthPx, animationSpec = tween(durationMillis = 100))
      }
    }

    Box(Modifier.fillMaxSize()) {
      if (isVisible || animatableOffsetX.isRunning) {
        // Show the window only when it's visible or animating
        Box(
            modifier =
                Modifier.fillMaxHeight()
                    .width(windowWidthDp)
                    .offset {
                      IntOffset(x = (animatableOffsetX.value + dragOffsetX).roundToInt(), y = 0)
                    }
                    .pointerInput(Unit) {
                      detectHorizontalDragGestures(
                          onDragEnd = {
                            if (dragOffsetX > dismissThreshold) {
                              onDismiss()
                            } else {
                              coroutineScope.launch {
                                animatableOffsetX.animateTo(
                                    targetValue = gapWidthPx,
                                    animationSpec = tween(durationMillis = 100))
                              }
                              dragOffsetX = 0f
                            }
                          },
                          onHorizontalDrag = { _, dragAmount ->
                            dragOffsetX = (dragOffsetX + dragAmount).coerceAtLeast(0f)
                          })
                    }
                    .shadow(
                        elevation = 16.dp,
                        shape =
                            RoundedCornerShape(
                                topStart = 0.dp,
                                topEnd = 16.dp,
                                bottomEnd = 16.dp,
                                bottomStart = 0.dp),
                        clip = false)
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))) {
              content()
            }
      }
    }
  }
}
