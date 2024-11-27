package com.arygm.quickfix.ui.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun QuickFixButton(
    buttonText: String,
    onClickAction: () -> Unit,
    buttonColor: Color,
    buttonOpacity: Float = 1f,
    buttonColorDisabled: Color = MaterialTheme.colorScheme.background,
    textColorDisabled: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    textColor: Color,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    height: Dp = 50.dp,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null
) {
  Button(
      onClick = onClickAction,
      colors = ButtonDefaults.buttonColors(
              containerColor = buttonColor,             // Background when enabled
              contentColor = textColor,              // Text color when enabled
              disabledContainerColor = buttonColorDisabled,     // Background when disabled
              disabledContentColor = textColorDisabled
      ),
      modifier =
          modifier
              .fillMaxWidth(0.8f)
              .height(height)
              .padding(bottom = 8.dp)
              .graphicsLayer(alpha = buttonOpacity)
              .testTag("quickfixButton"),
      shape = RoundedCornerShape(10.dp),
      contentPadding = contentPadding,
      enabled = enabled) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = horizontalArrangement,
            verticalAlignment = Alignment.CenterVertically,
        ) {
          leadingIcon?.let {
            Image(
                imageVector = it,
                contentDescription = "leading_icon",
                modifier = Modifier.padding(end = 8.dp))
          }
          Text(text = buttonText, style = textStyle)
          trailingIcon?.let {
            Image(
                imageVector = it,
                contentDescription = "trailing_icon",
                modifier = Modifier.padding(start = 8.dp))
          }
        }
      }
}
