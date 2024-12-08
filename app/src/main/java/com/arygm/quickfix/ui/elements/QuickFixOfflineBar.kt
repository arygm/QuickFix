package com.arygm.quickfix.ui.elements

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuickFixOfflineBar(isVisible: Boolean) {
  AnimatedVisibility(
      visible = isVisible,
      enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
      exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()) {
        Box(
            modifier =
                Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.error).padding(16.dp),
            contentAlignment = Alignment.Center) {
              Text(
                  text = "No Internet Connection",
                  color = MaterialTheme.colorScheme.background,
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold)
            }
      }
}
