package com.arygm.quickfix.ui.elements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String = "",
    underlinedText: String = "",
    onUnderlinedTextClick: () -> Unit,
    colorScheme: ColorScheme = MaterialTheme.colorScheme
) {
  Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
    Checkbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors =
            CheckboxDefaults.colors(
                checkedColor = colorScheme.primary,
                uncheckedColor = colorScheme.tertiary,
                checkmarkColor = Color.Transparent),
        modifier = Modifier.size(24.dp).testTag("checkbox"))
    Spacer(modifier = Modifier.width(10.dp))
    Text(
        label,
        style = MaterialTheme.typography.labelSmall,
        color = Color(0xFFC0C0C0),
        modifier = Modifier.testTag("checkBoxInfo"))
    Spacer(modifier = Modifier.width(4.dp))
    Text(
        text = underlinedText,
        style = MaterialTheme.typography.labelSmall,
        color = colorScheme.primary,
        textDecoration = TextDecoration.Underline,
        modifier = Modifier.clickable(onClick = onUnderlinedTextClick).testTag("clickableLink"))
  }
}
