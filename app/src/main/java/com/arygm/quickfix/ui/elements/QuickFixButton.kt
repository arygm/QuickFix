package com.arygm.quickfix.ui.elements

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun QuickFixButton(
    buttonText: String,
    onClickAction: () -> Unit,
    buttonColor: Color,
    buttonOpacity: Float = 1f,
    textColor: Color,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
  Button(
      onClick = onClickAction,
      colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
      modifier =
          modifier
              .fillMaxWidth(0.8f)
              .height(50.dp)
              .padding(bottom = 8.dp)
              .graphicsLayer(alpha = buttonOpacity)
              .testTag("quickfixButton"),
      shape = RoundedCornerShape(10.dp),
      enabled = enabled) {
        Text(text = buttonText, style = MaterialTheme.typography.labelMedium, color = textColor)
      }
}
