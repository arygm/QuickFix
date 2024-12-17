package com.arygm.quickfix.ui.elements

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.arygm.quickfix.model.messaging.ChatViewModel
import com.arygm.quickfix.model.quickfix.QuickFix
import com.arygm.quickfix.model.quickfix.QuickFixViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickFixDisplayImagesScreen(
    navigationActions: NavigationActions,
    chatViewModel: ChatViewModel,
    quickFixViewModel: QuickFixViewModel
) {
  // Récupérer la conversation active depuis ChatViewModel
  val activeChat = chatViewModel.selectedChat.collectAsState().value

  if (activeChat == null) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("No active chat selected.")
    }
    return
  }

  // Récupérer les images associées au QuickFix
  var quickFix by remember { mutableStateOf<QuickFix?>(null) }

  LaunchedEffect(activeChat.quickFixUid) {
    quickFixViewModel.fetchQuickFix(activeChat.quickFixUid) { result -> quickFix = result }
  }

  quickFix?.let { fix ->
    val imageUrls = fix.imageUrl

    Scaffold(
        topBar = {
          TopAppBar(
              title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                      // Bouton "Select All" en mode sélection

                      // Titre central
                      Text(
                          text = "${imageUrls.size} images",
                          style = MaterialTheme.typography.titleMedium,
                          color = MaterialTheme.colorScheme.primary)
                      // Bouton "Done" en mode sélection
                    }
              },
              navigationIcon = {
                IconButton(onClick = { navigationActions.goBack() }) {
                  Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                }
              },
          )
        },
    ) { paddingValues ->
      LazyVerticalGrid(
          columns = GridCells.Fixed(2),
          modifier = Modifier.fillMaxSize().padding(paddingValues),
          contentPadding = PaddingValues(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(imageUrls.size) { index ->
              val imageUrl = imageUrls[index]
              Box(modifier = Modifier.fillMaxWidth().aspectRatio(1f).testTag("imageCard")) {
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)))
              }
            }
          }
    }
  }
      ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("No images found.")
      }
}
