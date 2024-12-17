package com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.arygm.quickfix.model.search.AnnouncementViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.poppinsTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementDetailScreen(
    announcementViewModel: AnnouncementViewModel,
    navigationActions: NavigationActions,
) {
  Box(
      modifier = Modifier.fillMaxSize().testTag("AnnouncementDetailScreen"),
      contentAlignment = Alignment.Center) {
        Column {
          // Top App Bar with Go Back button
          TopAppBar(
              title = {
                Text(
                    text = "Announcement Detail",
                    color = colorScheme.primary,
                    style = poppinsTypography.headlineMedium,
                    modifier = Modifier.testTag("AnnouncementDetailTopBarTitle"))
              },
              navigationIcon = {
                IconButton(
                    onClick = { navigationActions.goBack() },
                    modifier = Modifier.testTag("GoBackButton")) {
                      Icon(
                          imageVector = Icons.Default.ArrowBack,
                          contentDescription = "Go Back",
                          tint = colorScheme.primary)
                    }
              },
              modifier = Modifier.fillMaxWidth())

          // Centered Text
          Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                modifier = Modifier.testTag("AnnoucementDetailTitle"),
                text = "Announcement Detail",
                style = poppinsTypography.headlineLarge)
          }
        }
      }
}
