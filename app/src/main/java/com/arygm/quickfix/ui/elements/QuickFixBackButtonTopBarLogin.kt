package com.arygm.quickfix.ui.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

@Composable
fun QuickFixBackButtonTopBarLogin(
    onBackClick: () -> Unit,
    title: String = "",
    color: Color = MaterialTheme.colorScheme.background,
    modifier: Modifier = Modifier
) {
  Box(modifier = Modifier.fillMaxSize()) {
    Image(
        painter =
            painterResource(
                id =
                    com.arygm.quickfix.R.drawable.worker_image), // Replace with your image resource
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize())
    QuickFixBackButtonTopBar(
        onBackClick = onBackClick, title = title, color = color, modifier = modifier)
  }
}
