package com.arygm.quickfix.ui.camera

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.arygm.quickfix.R
import com.arygm.quickfix.model.search.AnnouncementViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.poppinsTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickFixDisplayImages(
    canDelete: Boolean = true,
    navigationActions: NavigationActions, // Navigation actions parameter
    announcementViewModel: AnnouncementViewModel,
    images: List<Bitmap> = emptyList() // added these for testing
) {
  val uploadedImages by announcementViewModel.uploadedImages.collectAsState()
  val imagesToDisplay = images.ifEmpty { uploadedImages }
  var isSelecting by remember { mutableStateOf(false) }
  var selectedImages by remember { mutableStateOf(setOf<Bitmap>()) }

  Scaffold(
      topBar = {
        TopAppBar(
            title = {
              Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically) {
                    // "Select All" button or placeholder
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                      if (isSelecting && canDelete) {
                        TextButton(
                            modifier = Modifier.testTag("selectionButton"),
                            onClick = { selectedImages = imagesToDisplay.toSet() }) {
                              Text(
                                  "Select all",
                                  style = MaterialTheme.typography.headlineSmall,
                                  color = colorScheme.primary)
                            }
                      }
                    }

                    // Title in the center
                    Text(
                        text = "${imagesToDisplay.size} elements",
                        style = poppinsTypography.headlineMedium,
                        color = colorScheme.primary,
                        modifier = Modifier.weight(2f).testTag("DisplayedImagesTitle"),
                        textAlign = TextAlign.Center)

                    // "Done" button or placeholder
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                      if (isSelecting && canDelete) {
                        TextButton(
                            modifier = Modifier.testTag("endSelectionButton"),
                            onClick = {
                              isSelecting = false
                              selectedImages = emptySet()
                            }) {
                              Text(
                                  "Done",
                                  style = MaterialTheme.typography.headlineSmall,
                                  color = colorScheme.primary)
                            }
                      }
                    }
                  }
            },
            navigationIcon = {
              if (!isSelecting) {
                IconButton(
                    onClick = { navigationActions.goBack() },
                    modifier = Modifier.testTag("goBackButton")) {
                      Icon(
                          Icons.Outlined.ArrowBack,
                          contentDescription = "Back",
                          tint = colorScheme.primary)
                    }
              }
            },
            actions = {
              if (canDelete) {
                if (!isSelecting) {
                  Log.d("isSelecting", "is not selecting")
                  IconButton(
                      modifier = Modifier.testTag("SelectImagesButton"),
                      onClick = { isSelecting = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.selectimages),
                            contentDescription = "Select images",
                            modifier = Modifier.size(20.dp),
                            tint = colorScheme.primary)
                      }
                }
              }
            })
      },
      bottomBar = {
        if (isSelecting && selectedImages.isNotEmpty()) {
          Box(
              modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
              contentAlignment = Alignment.Center) {
                Button(
                    onClick = {
                      announcementViewModel.deleteUploadedImages(selectedImages.toList())
                      isSelecting = false
                      selectedImages = emptySet()
                    },
                    modifier = Modifier.fillMaxWidth(0.63f).testTag("nbOfSelectedPhotos"),
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)) {
                      Icon(Icons.Outlined.Delete, contentDescription = null)
                      Spacer(modifier = Modifier.width(8.dp))
                      Text(
                          text =
                              if (selectedImages.size == 1) "1 photo selected"
                              else "${selectedImages.size} photos selected",
                          color = colorScheme.onPrimary,
                          style = MaterialTheme.typography.titleMedium,
                          modifier = Modifier.testTag("nbOfSelectedPhotos"))
                    }
              }
        }
      }) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(paddingValues).fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              items(imagesToDisplay.size) { index ->
                val image = imagesToDisplay[index]
                Box(modifier = Modifier.fillMaxSize()) {
                  Card(
                      modifier =
                          Modifier.fillMaxWidth()
                              .aspectRatio(1f)
                              .clickable {
                                if (isSelecting) {
                                  selectedImages =
                                      if (selectedImages.contains(image)) {
                                        selectedImages - image
                                      } else {
                                        selectedImages + image
                                      }
                                }
                              }
                              .testTag("imageCard_$index"), // Added test tag here
                      shape = MaterialTheme.shapes.medium,
                      elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
                        Image(
                            painter = rememberAsyncImagePainter(image),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize())
                      }
                  if (isSelecting) {
                    Icon(
                        imageVector =
                            if (selectedImages.contains(image)) {
                              Icons.Default.CheckCircle
                            } else {
                              Icons.Default.RadioButtonUnchecked
                            },
                        contentDescription = null,
                        tint =
                            if (selectedImages.contains(image)) colorScheme.primary else Color.Gray,
                        modifier =
                            Modifier.size(24.dp)
                                .align(Alignment.TopStart)
                                .padding(4.dp)
                                .clickable {
                                  selectedImages =
                                      if (selectedImages.contains(image)) {
                                        selectedImages - image
                                      } else {
                                        selectedImages + image
                                      }
                                }
                                .testTag("selectionIcon_$index") // Added test tag here
                        )
                  }
                }
              }
            }
      }
}
