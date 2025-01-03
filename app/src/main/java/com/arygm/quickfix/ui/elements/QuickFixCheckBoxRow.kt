package com.arygm.quickfix.ui.elements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

@Composable
fun QuickFixCheckBoxRow(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String = "",
    underlinedText: String = "",
    onUnderlinedTextClick: () -> Unit,
    labelBis: String = "",
    underlinedTextBis: String = "",
    onUnderlinedTextClickBis: (() -> Unit)? = null,
    colorScheme: ColorScheme = MaterialTheme.colorScheme
) {
  Column(
      modifier = modifier.fillMaxWidth().padding(end = 3.dp),
  ) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.testTag("checkBoxRow")) {
          Checkbox(
              checked = checked,
              onCheckedChange = onCheckedChange,
              colors =
                  CheckboxDefaults.colors(
                      checkedColor = colorScheme.primary,
                      uncheckedColor = colorScheme.tertiary,
                      checkmarkColor = Color.Transparent),
              modifier = modifier.size(10.dp).testTag("checkbox"))
          Spacer(modifier = modifier.width(1.dp))
          Text(
              label,
              style = MaterialTheme.typography.headlineSmall,
              color = colorScheme.onSurface,
              modifier = modifier.testTag("checkBoxInfo"))
          Text(
              text = underlinedText,
              style = MaterialTheme.typography.headlineSmall,
              color = colorScheme.primary,
              textDecoration = TextDecoration.Underline,
              maxLines = 1,
              modifier =
                  modifier.clickable(onClick = onUnderlinedTextClick).testTag("clickableLink"))
        }
    Row(
        modifier = modifier.padding(start = 22.dp),
        verticalAlignment = Alignment.CenterVertically) {
          Text(
              labelBis,
              style = MaterialTheme.typography.headlineSmall,
              color = colorScheme.onSurface,
              modifier = modifier.testTag("checkBoxInfoBis"))
          Text(
              text = underlinedTextBis,
              style = MaterialTheme.typography.headlineSmall,
              color = colorScheme.primary,
              textDecoration = TextDecoration.Underline,
              modifier =
                  modifier
                      .clickable(
                          onClick = {
                            if (onUnderlinedTextClickBis != null) onUnderlinedTextClickBis()
                          })
                      .testTag("clickableLinkBis"))
        }
    Spacer(modifier = modifier.height(10.dp))
  }
}
