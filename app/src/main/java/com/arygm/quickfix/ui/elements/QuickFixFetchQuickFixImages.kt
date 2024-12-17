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
    navigationActions: NavigationActions, // Handles navigation actions like "goBack"
    chatViewModel: ChatViewModel, // ViewModel to retrieve the active chat
    quickFixViewModel: QuickFixViewModel // ViewModel to fetch QuickFix details
) {
  // Retrieve the active chat from the ChatViewModel
  val activeChat = chatViewModel.selectedChat.collectAsState().value

  // If no active chat is available, display a placeholder message
  if (activeChat == null) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("No active chat selected.") // Display message
    }
    return
  }

  // Variable to hold QuickFix data fetched based on the chat's quickFixUid
  var quickFix by remember { mutableStateOf<QuickFix?>(null) }

  // Fetch QuickFix details when the screen is launched
  LaunchedEffect(activeChat.quickFixUid) {
    quickFixViewModel.fetchQuickFix(activeChat.quickFixUid) { result -> quickFix = result }
  }

  // If QuickFix data is available, display the images
  quickFix?.let { fix ->
    val imageUrls = fix.imageUrl // List of image URLs from QuickFix

    // Scaffold provides the structure for the screen (Top AppBar + Content)
    Scaffold(
        topBar = {
          TopAppBar(
              title = {
                // Row layout for TopAppBar title with spacing
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                      // Title showing the number of images
                      Text(
                          text = "${imageUrls.size} images",
                          style = MaterialTheme.typography.titleMedium,
                          color = MaterialTheme.colorScheme.primary)
                    }
              },
              navigationIcon = {
                // Navigation button to go back
                IconButton(onClick = { navigationActions.goBack() }) {
                  Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                }
              })
        }) { paddingValues ->
          // LazyVerticalGrid for displaying images in a grid layout
          LazyVerticalGrid(
              columns = GridCells.Fixed(2), // Two columns
              modifier = Modifier.fillMaxSize().padding(paddingValues),
              contentPadding = PaddingValues(8.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp),
              horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Display each image in the grid
                items(imageUrls.size) { index ->
                  val imageUrl = imageUrls[index]
                  Box(
                      modifier =
                          Modifier.fillMaxWidth()
                              .aspectRatio(1f)
                              .testTag("imageCard") // Test tag for UI testing
                      ) {
                        Image(
                            painter = rememberAsyncImagePainter(imageUrl), // Load image from URL
                            contentDescription = null,
                            contentScale = ContentScale.Crop, // Crop image to fit the box
                            modifier =
                                Modifier.fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp)) // Rounded corners
                            )
                      }
                }
              }
        }
  }
      // If QuickFix data is null, display a placeholder message
      ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("No images found.") // Display message if no images are available
      }
}
