package com.arygm.quickfix.ui.elements

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.arygm.quickfix.model.quickfix.QuickFix
import com.arygm.quickfix.model.quickfix.QuickFixViewModel
import com.arygm.quickfix.ui.theme.poppinsTypography

@Composable
fun QuickFixDetailsScreen(
    quickFix: QuickFix, // QuickFix object containing all details
    onShowMoreToggle: (Boolean) -> Unit, // Callback for toggling description expansion
    isExpanded: Boolean, // Flag indicating whether the description is expanded
    quickFixViewModel: QuickFixViewModel
) {
  // BoxWithConstraints allows you to get the available width and height
  Log.d("QuickFixDetailsScreen", "quickFix: ${quickFix.uid}]")
  BoxWithConstraints(
      modifier =
          Modifier.fillMaxWidth()
              .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
              .padding(16.dp)) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        // Select the first 2 services from each list to display
        val includedServicesToShow = quickFix.includedServices.take(2)
        val addOnServicesToShow = quickFix.addOnServices.take(2)

        // If there are still slots left, fill them with additional services
        val extraIncludedServices =
            quickFix.includedServices
                .drop(2)
                .take(4 - includedServicesToShow.size - addOnServicesToShow.size)
        val extraAddOnServices =
            quickFix.addOnServices
                .drop(2)
                .take(4 - includedServicesToShow.size - addOnServicesToShow.size)

        // Combine all services to show
        val allServicesToShow =
            includedServicesToShow +
                addOnServicesToShow +
                extraIncludedServices +
                extraAddOnServices

        // Layout in a vertical column
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
          // Section for displaying images
          ImageSelector(screenWidth, screenWidth * 2f, quickFix, {}, quickFixViewModel)

          Spacer(modifier = Modifier.height(8.dp))

          // Title for Selected Services
          Text(
              text = "Selected Services",
              style = poppinsTypography.headlineMedium,
              color = MaterialTheme.colorScheme.onBackground,
              modifier = Modifier.padding(bottom = 8.dp))

          // Loop through and display all selected services
          allServicesToShow.forEach { service ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp)) {
                  // Display an icon based on whether the service is included
                  Icon(
                      imageVector =
                          if (quickFix.includedServices.contains(service)) Icons.Default.Check
                          else Icons.Default.Star,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.size(screenWidth * 0.05f))
                  Spacer(modifier = Modifier.width(screenWidth * 0.02f))
                  Text(
                      text = service.name,
                      style = poppinsTypography.bodyMedium,
                      color =
                          if (quickFix.includedServices.contains(service))
                              MaterialTheme.colorScheme.onBackground
                          else MaterialTheme.colorScheme.primary)
                }
          }

          // Description Section
          Spacer(modifier = Modifier.height(8.dp))
          Text(
              text = "Description",
              style = poppinsTypography.headlineMedium,
              color = MaterialTheme.colorScheme.onBackground,
              modifier = Modifier.padding(bottom = 8.dp))
          // Show the full description or truncate it based on isExpanded
          Text(
              text =
                  if (isExpanded) quickFix.title
                  else quickFix.title.take(250) + if (quickFix.title.length > 250) "..." else "",
              style = poppinsTypography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurface)

          // Button for toggling description expansion
          Column(
              modifier = Modifier.fillMaxWidth(),
              horizontalAlignment = Alignment.CenterHorizontally) {
                TextButton(
                    onClick = { onShowMoreToggle(!isExpanded) },
                    modifier = Modifier.align(Alignment.CenterHorizontally)) {
                      Text(
                          text = if (isExpanded) "Show less" else "Show more",
                          style = poppinsTypography.labelMedium,
                          color = MaterialTheme.colorScheme.primary)
                    }
              }
        }
      }
}

@Composable
fun ImageSelector(
    screenWidth: Dp, // Screen width for sizing images
    screenHeight: Dp, // Screen height for sizing images
    quickFix: QuickFix, // QuickFix object containing image URLs
    onViewAllImages: () -> Unit = {}, // Callback to view all images
    quickFixViewModel: QuickFixViewModel
) {
  // Row layout to display images
  Row(
      modifier =
          Modifier.fillMaxWidth()
              .height(screenHeight * 0.23f)
              .clip(RoundedCornerShape(8.dp))
              .background(MaterialTheme.colorScheme.background)
              .padding(8.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically) {
        var quickFixImages by remember { mutableStateOf<List<Bitmap>>(emptyList()) }
        LaunchedEffect(Unit) {
          quickFixViewModel.fetchQuickFixAsBitmaps(
              quickFix.uid,
              onSuccess = { quickFixImages = it.map { it.second } },
              onFailure = {
                Log.e("QuickFixDetailsScreen", "Failed to fetch images: ${it.message}")
              })
        }
        val visibleImages = quickFixImages.take(2) // Show only the first 2 images
        val remainingImagesCount = quickFixImages.size - 2 // Count of remaining images

        // Loop through and display each image
        visibleImages.forEachIndexed { index, imageUrl ->
          Box(
              modifier =
                  Modifier.testTag("imageCard") // Tag for testing
                      .weight(1f)
                      .height(screenHeight * 0.20f)
                      .clip(RoundedCornerShape(8.dp))
                      .background(MaterialTheme.colorScheme.secondary)
                      .clickable {
                        // Navigate to full image view if there are more images
                        if (index == 1 && remainingImagesCount > 0) {
                          onViewAllImages()
                        }
                      }) {
                // Display image using SubcomposeAsyncImage
                SubcomposeAsyncImage(
                    model = imageUrl,
                    contentDescription = "QuickFix Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    loading = {
                      // Display loading text while image is loading
                      Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "Loading...",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            textAlign = TextAlign.Center)
                      }
                    },
                    error = {
                      // Display error message if image fails to load
                      Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "Error",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Red,
                            textAlign = TextAlign.Center)
                      }
                    },
                    success = { SubcomposeAsyncImageContent() })

                // Overlay showing remaining image count on the second image
                if (index == 1 && remainingImagesCount > 0) {
                  Box(
                      modifier =
                          Modifier.fillMaxSize()
                              .background(Color.Black.copy(alpha = 0.6f))
                              .align(Alignment.Center),
                      contentAlignment = Alignment.Center) {
                        Text(
                            text = "+$remainingImagesCount",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            textAlign = TextAlign.Center)
                      }
                }
              }
        }
      }
}
