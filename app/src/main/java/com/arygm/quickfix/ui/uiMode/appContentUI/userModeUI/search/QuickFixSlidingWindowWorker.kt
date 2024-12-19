package com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.arygm.quickfix.R
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.elements.QuickFixSlidingWindow
import com.arygm.quickfix.ui.elements.RatingBar

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuickFixSlidingWindowWorker(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    screenHeight: Dp,
    screenWidth: Dp,
    onContinueClick: () -> Unit,
    workerProfile: WorkerProfile
) {
  var saved by remember { mutableStateOf(false) }
  var showFullDescription by remember { mutableStateOf(false) }

  QuickFixSlidingWindow(isVisible = isVisible, onDismiss = onDismiss) {
    Column(
        modifier =
            Modifier.clip(RoundedCornerShape(topStart = 25f, bottomStart = 25f))
                .fillMaxWidth()
                .background(colorScheme.background)
                .testTag("sliding_window_content")) {
          // Top Bar with Banner Image and Profile Picture
          Box(modifier = Modifier.fillMaxWidth().height(screenHeight * 0.23f)) {
            Image(
                painter = painterResource(id = R.drawable.placeholder_worker), // Default fallback
                contentDescription = "Banner",
                modifier = Modifier.fillMaxWidth().height(screenHeight * 0.2f),
                contentScale = ContentScale.Crop)
            QuickFixButton(
                buttonText = if (saved) "saved" else "save",
                onClickAction = { saved = !saved },
                buttonColor = colorScheme.surface,
                textColor = colorScheme.onBackground,
                textStyle = MaterialTheme.typography.labelMedium,
                contentPadding = PaddingValues(horizontal = screenWidth * 0.01f),
                modifier =
                    Modifier.align(Alignment.BottomEnd)
                        .width(screenWidth * 0.25f)
                        .offset(x = -(screenWidth * 0.04f)),
                leadingIcon = if (saved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder)

            Image(
                painter =
                    painterResource(id = R.drawable.placeholder_worker), // Fallback for profile
                contentDescription = "Profile Picture",
                modifier =
                    Modifier.size(screenHeight * 0.1f)
                        .align(Alignment.BottomStart)
                        .offset(x = screenWidth * 0.04f)
                        .clip(CircleShape),
                contentScale = ContentScale.Crop)
          }

          // Worker Information
          Column(modifier = Modifier.fillMaxWidth().padding(horizontal = screenWidth * 0.04f)) {
            Text(
                text = workerProfile.fieldOfWork,
                style = MaterialTheme.typography.headlineLarge,
                color = colorScheme.onBackground)
            Text(
                text = workerProfile.location?.name ?: "Unknown Location",
                style = MaterialTheme.typography.headlineSmall,
                color = colorScheme.onBackground)
          }

          // Scrollable Content
          Column(
              modifier =
                  Modifier.fillMaxWidth()
                      .verticalScroll(rememberScrollState())
                      .background(colorScheme.surface)) {
                Spacer(modifier = Modifier.height(screenHeight * 0.02f))

                // Description
                val descriptionText =
                    if (showFullDescription || workerProfile.description.length <= 100) {
                      workerProfile.description
                    } else {
                      workerProfile.description.take(100) + "..."
                    }
                Text(
                    text = descriptionText,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = screenWidth * 0.04f))

                if (workerProfile.description.length > 100) {
                  Text(
                      text = if (showFullDescription) "Show less" else "Show more",
                      style = MaterialTheme.typography.bodySmall.copy(color = colorScheme.primary),
                      modifier =
                          Modifier.padding(horizontal = screenWidth * 0.04f).clickable {
                            showFullDescription = !showFullDescription
                          })
                }

                Spacer(modifier = Modifier.height(screenHeight * 0.02f))

                // Services Section
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = screenWidth * 0.04f)) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Included Services",
                        style = MaterialTheme.typography.headlineMedium,
                        color = colorScheme.onBackground)
                    workerProfile.includedServices.forEach { service ->
                      Text(
                          text = "• ${service.name}",
                          style = MaterialTheme.typography.bodySmall,
                          color = colorScheme.onSurface)
                    }
                  }

                  Spacer(modifier = Modifier.width(screenWidth * 0.02f))

                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Add-On Services",
                        style = MaterialTheme.typography.headlineMedium,
                        color = colorScheme.primary)
                    workerProfile.addOnServices.forEach { service ->
                      Text(
                          text = "• ${service.name}",
                          style = MaterialTheme.typography.bodySmall,
                          color = colorScheme.primary)
                    }
                  }
                }

                Spacer(modifier = Modifier.height(screenHeight * 0.03f))

                // Continue Button
                QuickFixButton(
                    buttonText = "Continue",
                    onClickAction = onContinueClick,
                    buttonColor = colorScheme.primary,
                    textColor = colorScheme.onPrimary,
                    textStyle = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = screenWidth * 0.04f))

                Spacer(modifier = Modifier.height(screenHeight * 0.02f))

                // Tags
                Text(
                    text = "Tags",
                    style = MaterialTheme.typography.headlineMedium,
                    color = colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = screenWidth * 0.04f))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(screenWidth * 0.02f),
                    verticalArrangement = Arrangement.spacedBy(screenHeight * 0.01f),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = screenWidth * 0.04f)) {
                      workerProfile.tags.forEach { tag ->
                        Text(
                            text = tag,
                            color = colorScheme.primary,
                            style = MaterialTheme.typography.bodySmall,
                            modifier =
                                Modifier.border(
                                        1.dp, colorScheme.primary, MaterialTheme.shapes.small)
                                    .padding(horizontal = 8.dp, vertical = 4.dp))
                      }
                    }

                Spacer(modifier = Modifier.height(screenHeight * 0.02f))

                // Reviews
                Text(
                    text = "Reviews",
                    style = MaterialTheme.typography.headlineMedium,
                    color = colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = screenWidth * 0.04f))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = screenWidth * 0.04f)) {
                      RatingBar(
                          workerProfile.rating.toFloat(),
                          modifier = Modifier.height(screenHeight * 0.03f))
                    }
              }
        }
  }
}
