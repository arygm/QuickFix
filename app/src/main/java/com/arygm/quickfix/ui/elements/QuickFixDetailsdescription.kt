package com.arygm.quickfix.ui.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
          ImageSelector(screenWidth, screenHeight * 1.2f, quickFix)

          // Selected Services
          Spacer(modifier = Modifier.height(screenHeight * 0.02f))

          Text(
              text = "Selected Services",
              style = poppinsTypography.headlineMedium,
              color = MaterialTheme.colorScheme.onBackground,
              modifier = Modifier.padding(bottom = screenHeight * 0.01f))

          allServicesToShow.forEach { service ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = screenHeight * 0.01f)) {
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
          Spacer(modifier = Modifier.height(screenHeight * 0.02f))
          Text(
              text = "Description",
              style = poppinsTypography.headlineMedium,
              color = MaterialTheme.colorScheme.onBackground,
              modifier = Modifier.padding(bottom = screenHeight * 0.01f))
          Column {
            Text(
                text = if (isExpanded) quickFix.title else quickFix.title.take(250) + "...",
                style = poppinsTypography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
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
fun ImageSelector(screenWidth: Dp, screenHeight: Dp, quickFix: QuickFix) {
  Row(
      modifier =
          Modifier.fillMaxWidth()
              .height(screenHeight * 0.23f) // Adjust height as needed
              .clip(RoundedCornerShape(8.dp))
              .background(MaterialTheme.colorScheme.background)
              .padding(8.dp), // General padding for uniformity
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically) {
        quickFix.imageUrl.take(2).forEach { imageUrl ->
          Box(
              modifier =
                  Modifier.weight(1f) // Ensure equal spacing for each image
                      .height(screenHeight * 0.20f) // Maintain a square aspect ratio
                      .clip(RoundedCornerShape(8.dp)) // Clip corners
                      .background(MaterialTheme.colorScheme.secondary)
                      .padding(8.dp), // Specific padding for each image
              contentAlignment = Alignment.Center) {
                // Placeholder for an image. Replace with actual image loading logic using Coil or
                // Glide
                Text(
                    text = "Image",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondary)
              }
        }
      }
}
