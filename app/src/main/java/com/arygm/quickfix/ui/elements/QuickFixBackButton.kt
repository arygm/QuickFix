package com.arygm.quickfix.ui.elements

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun QuickFixBackButton(onClick: () -> Unit, color: Color, modifier: Modifier = Modifier) {
  IconButton(
      onClick = onClick,
      modifier = modifier.testTag("goBackButton").padding(start = 9.dp, top = 35.dp).size(48.dp)) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
            contentDescription = "Back",
            tint = color,
            modifier = Modifier.size(45.dp))
      }
}
