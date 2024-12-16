package com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.camera

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
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arygm.quickfix.R
import com.arygm.quickfix.model.search.AnnouncementViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.poppinsTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickFixDisplayImages(
    canDelete: Boolean = true,
    navigationActions: NavigationActions,
    announcementViewModel: AnnouncementViewModel,
    images: List<Bitmap> = emptyList() // For testing
) {
  val selectedAnnouncement by announcementViewModel.selectedAnnouncement.collectAsState()

  val defaultUploadedImages by announcementViewModel.uploadedImages.collectAsState()
  val announcementImagesMap by announcementViewModel.announcementImagesMap.collectAsState()

  // If we have a selected announcement, get its (URL, Bitmap) pairs, otherwise convert default
  // uploaded images
  val uploadedImages: List<Pair<String, Bitmap>> =
      if (selectedAnnouncement != null) {
        // Announcement images map is Map<String, List<Pair<String, Bitmap>>>
        announcementImagesMap[selectedAnnouncement!!.announcementId] ?: emptyList()
      } else {
        // Convert the default uploaded images (List<Bitmap>) into pairs with a placeholder URL
        defaultUploadedImages.mapIndexed { index, bitmap -> "local_$index" to bitmap }
      }

  // If the `images` parameter is not empty, use it by converting to pairs as well
  val imagesToDisplay: List<Pair<String, Bitmap>> =
      if (images.isNotEmpty()) {
        images.mapIndexed { index, bitmap -> "localparam_$index" to bitmap }
      } else {
        uploadedImages
      }

  var isSelecting by remember { mutableStateOf(false) }
  var selectedImages by remember { mutableStateOf(setOf<Pair<String, Bitmap>>()) }

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
                          Icons.AutoMirrored.Outlined.ArrowBack,
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
                      if (selectedAnnouncement != null) {
                        val announcementId = selectedAnnouncement!!.announcementId
                        val currentImages =
                            announcementViewModel.announcementImagesMap.value[announcementId]
                                ?: emptyList()

                        // Filter out the selected images (pairs)
                        val updatedImages = currentImages.filterNot { it in selectedImages }

                        // Update the announcement images map
                        val updatedMap =
                            announcementViewModel.announcementImagesMap.value.toMutableMap().apply {
                              put(announcementId, updatedImages)
                            }
                        announcementViewModel.setAnnouncementImagesMap(updatedMap)

                        // Since quickFixImages is a list of URLs, we can now derive it from
                        // updatedImages
                        val updatedQuickFixImages = updatedImages.map { it.first }

                        val updatedAnnouncement =
                            selectedAnnouncement!!.copy(quickFixImages = updatedQuickFixImages)
                        announcementViewModel.updateAnnouncement(updatedAnnouncement)
                      } else {
                        // If no selected announcement, delete uploaded images by extracting their
                        // bitmaps
                        val bitmapsToDelete = selectedImages.map { it.second }
                        announcementViewModel.deleteUploadedImages(bitmapsToDelete)
                      }
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
                val imagePair = imagesToDisplay[index] // This is (URL, Bitmap)
                Box(modifier = Modifier.fillMaxSize()) {
                  Card(
                      modifier =
                          Modifier.fillMaxWidth()
                              .aspectRatio(1f)
                              .clickable {
                                if (isSelecting) {
                                  selectedImages =
                                      if (selectedImages.contains(imagePair)) {
                                        selectedImages - imagePair
                                      } else {
                                        selectedImages + imagePair
                                      }
                                }
                              }
                              .testTag("imageCard_$index"),
                      shape = MaterialTheme.shapes.medium,
                      elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
                        // Use the bitmap from the pair
                        Image(
                            bitmap = imagePair.second.asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize())
                      }
                  if (isSelecting) {
                    Icon(
                        imageVector =
                            if (selectedImages.contains(imagePair)) {
                              Icons.Default.CheckCircle
                            } else {
                              Icons.Default.RadioButtonUnchecked
                            },
                        contentDescription = null,
                        tint =
                            if (selectedImages.contains(imagePair)) colorScheme.primary
                            else colorScheme.onSurface,
                        modifier =
                            Modifier.size(24.dp)
                                .align(Alignment.TopStart)
                                .padding(4.dp)
                                .clickable {
                                  selectedImages =
                                      if (selectedImages.contains(imagePair)) {
                                        selectedImages - imagePair
                                      } else {
                                        selectedImages + imagePair
                                      }
                                }
                                .testTag("selectionIcon_$index"))
                  }
                }
              }
            }
      }
}
