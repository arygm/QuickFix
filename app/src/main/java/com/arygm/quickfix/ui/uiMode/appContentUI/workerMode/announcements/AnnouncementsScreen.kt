package com.arygm.quickfix.ui.uiMode.appContentUI.workerMode.announcements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arygm.quickfix.ui.theme.poppinsTypography

@Composable
fun AnnouncementsScreen() {
  Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Announcements Screen", style = poppinsTypography.headlineLarge)
      }
}
