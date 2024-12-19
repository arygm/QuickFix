package com.arygm.quickfix.ui.elements

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arygm.quickfix.ui.theme.poppinsTypography
import kotlinx.coroutines.CoroutineScope

@Composable
fun QuickFixStepper(
    steps: List<String>, // List of step titles
    icons: List<ImageVector>, // List of icons for each step
    currentStep: Int, // 1-based index for the active step
    heightRatio: Dp, // Height ratio of the stepper
    widthRatio: Dp, // Width ratio of the stepper
    coroutineScope: CoroutineScope
) {
  Row(
      modifier =
          Modifier.fillMaxWidth()
              .height(IntrinsicSize.Min)
              .padding(vertical = 16.dp, horizontal = 16.dp)
              .testTag("QuickFixStepper"), // Test tag for the entire stepper
      verticalAlignment = Alignment.CenterVertically) {
        steps.forEachIndexed { index, step ->
          val isCurrent = index + 1 == currentStep
          val isDone = index + 1 < currentStep
          val isToDo = index + 1 > currentStep

          // Animated Border Color
          val borderColor by
              animateColorAsState(
                  targetValue =
                      when {
                        isDone -> colorScheme.primary // Color for done
                        isCurrent -> colorScheme.primary // Color for current
                        else -> colorScheme.tertiaryContainer // Color for ToDo
                      },
                  animationSpec = tween(durationMillis = 600),
                  label = "Border Animation")

          // Animated Divider Color
          val dividerColor by
              animateColorAsState(
                  targetValue = if (isDone) colorScheme.primary else colorScheme.tertiaryContainer,
                  animationSpec = tween(durationMillis = 600),
                  label = "Divider Animation")

          // Step
          Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier =
                  Modifier.width(48.dp * widthRatio.value)
                      .fillMaxHeight()
                      .testTag("Step_$index") // Test tag for each step
              ) {
                // Circle with Icon
                Box(
                    contentAlignment = Alignment.Center,
                    modifier =
                        Modifier.size(48.dp * widthRatio.value)
                            .clip(CircleShape)
                            .border(width = 1.25.dp, color = borderColor, shape = CircleShape)
                            .background(
                                when {
                                  isDone -> colorScheme.primary // Background for done
                                  else -> colorScheme.surface // Background for ToDo and Current
                                })
                            .testTag("StepIcon_$index") // Test tag for step icon
                    ) {
                      Icon(
                          imageVector = icons[index],
                          contentDescription = "Step Icon",
                          tint =
                              when {
                                isDone -> colorScheme.surface // Icon color for done
                                isCurrent -> colorScheme.primary // Icon color for current
                                else -> colorScheme.tertiaryContainer // Icon color for ToDo
                              })
                    }

                Spacer(modifier = Modifier.height(8.dp * heightRatio.value))

                // Step Titles
                Text(
                    text = "STEP ${index + 1}",
                    style = poppinsTypography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = colorScheme.onSecondaryContainer,
                    modifier = Modifier.testTag("StepLabel_$index") // Test tag for step label
                    )
                Text(
                    text = step,
                    style =
                        poppinsTypography.bodySmall.copy(
                            fontWeight = FontWeight.Medium, fontSize = 8.sp),
                    color = colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier =
                        Modifier.testTag("StepDescription_$index") // Test tag for step description
                    )
              }

          // Connector Line (except after the last step)
          if (index < steps.size - 1) {
            HorizontalDivider(
                color = dividerColor,
                thickness = 2.dp,
                modifier =
                    Modifier.weight(1f)
                        .padding(bottom = 38.dp * heightRatio.value)
                        .testTag("Connector_$index") // Test tag for connector
                )
          }
        }
      }
}
