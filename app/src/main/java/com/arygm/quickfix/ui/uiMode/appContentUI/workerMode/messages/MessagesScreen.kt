package com.arygm.quickfix.ui.uiMode.appContentUI.workerMode.messages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arygm.quickfix.ui.theme.poppinsTypography

@Composable
fun MessagesScreen() {
  Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Messages Screen", style = poppinsTypography.headlineLarge)
      }
}
