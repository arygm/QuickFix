package com.arygm.quickfix.ui.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.arygm.quickfix.ui.theme.poppinsTypography

@Composable
fun QuickFixDetailsScreen(
    quickFix: QuickFix,
    onShowMoreToggle: (Boolean) -> Unit,
    isExpanded: Boolean
) {
  BoxWithConstraints(
      modifier =
          Modifier.fillMaxWidth()
              .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
              .padding(16.dp)) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        // Combine services logic
        val includedServicesToShow = quickFix.includedServices.take(2)
        val addOnServicesToShow = quickFix.addOnServices.take(2)

        // If the total number of displayed services is less than 4, fill the remainder with the
        // other list
        val extraIncludedServices =
            quickFix.includedServices
                .drop(2)
                .take(4 - includedServicesToShow.size - addOnServicesToShow.size)
        val extraAddOnServices =
            quickFix.addOnServices
                .drop(2)
                .take(4 - includedServicesToShow.size - addOnServicesToShow.size)

        val allServicesToShow =
            includedServicesToShow +
                addOnServicesToShow +
                extraIncludedServices +
                extraAddOnServices

        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
          // Images Section
          ImageSelector(screenWidth, screenWidth * 2f, quickFix, {})

          // Selected Services
          Spacer(modifier = Modifier.height(8.dp))

          Text(
              text = "Selected Services",
              style = poppinsTypography.headlineMedium,
              color = MaterialTheme.colorScheme.onBackground,
              modifier = Modifier.padding(bottom = 8.dp))

          allServicesToShow.forEach { service ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp)) {
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

          // Description Section with "Show More"
          Spacer(modifier = Modifier.height(8.dp))
          Text(
              text = "Description",
              style = poppinsTypography.headlineMedium,
              color = MaterialTheme.colorScheme.onBackground,
              modifier = Modifier.padding(bottom = 8.dp))
          Text(
              text =
                  if (isExpanded) quickFix.title
                  else quickFix.title.take(250) + if (quickFix.title.length > 250) ("...") else "",
              style = poppinsTypography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurface,
          )
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
    screenWidth: Dp,
    screenHeight: Dp,
    quickFix: QuickFix,
    onViewAllImages: () -> Unit = {} // Callback pour afficher l'écran complet
) {
  Row(
      modifier =
          Modifier.fillMaxWidth()
              .height(screenHeight * 0.23f)
              .clip(RoundedCornerShape(8.dp))
              .background(MaterialTheme.colorScheme.background)
              .padding(8.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically) {
        val visibleImages = quickFix.imageUrl.take(2) // Prendre les 2 premières images
        val remainingImagesCount = quickFix.imageUrl.size - 2 // Nombre d'images restantes

        visibleImages.forEachIndexed { index, imageUrl ->
          Box(
              modifier =
                  Modifier.testTag("imageCard")
                      .weight(1f)
                      .height(screenHeight * 0.20f)
                      .clip(RoundedCornerShape(8.dp))
                      .background(MaterialTheme.colorScheme.secondary)
                      .clickable {
                        if (index == 1 && remainingImagesCount > 0) {
                          // Naviguer vers l'écran complet des images
                          onViewAllImages()
                        }
                      }) {
                SubcomposeAsyncImage(
                    model = imageUrl,
                    contentDescription = "QuickFix Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    loading = {
                      Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "Loading...",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            textAlign = TextAlign.Center)
                      }
                    },
                    error = {
                      Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "Error",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Red,
                            textAlign = TextAlign.Center)
                      }
                    },
                    success = { SubcomposeAsyncImageContent() })

                // Overlay +X si plus de 2 images et c'est la 2ᵉ image
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
