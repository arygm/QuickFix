// WorkerSlidingWindowContent.kt

import android.widget.RatingBar
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.HorizontalDivider
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
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.elements.QuickFixSlidingWindow
import com.arygm.quickfix.ui.elements.RatingBar

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuickFixSlidingWindowWorker(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    bannerImage: Int,
    profilePicture: Int,
    initialSaved: Boolean,
    workerCategory: String,
    workerAddress: String,
    description: String,
    includedServices: List<String>,
    addonServices: List<String>,
    workerRating: Double,
    tags: List<String>,
    reviews: List<String>,
    screenHeight: Dp,
    screenWidth: Dp,
    onContinueClick: () -> Unit
) {
  var saved by remember { mutableStateOf(initialSaved) }
  var showFullDescription by remember { mutableStateOf(false) }

  QuickFixSlidingWindow(isVisible = isVisible, onDismiss = onDismiss) {
    // Content of the sliding window
    Column(
        modifier =
            Modifier.clip(RoundedCornerShape(topStart = 25f, bottomStart = 25f))
                .fillMaxWidth()
                .background(colorScheme.background)
                .testTag("sliding_window_content")) {

          // Top Bar
          Box(
              modifier =
                  Modifier.fillMaxWidth()
                      .height(screenHeight * 0.23f)
                      .testTag("sliding_window_top_bar")) {
                // Banner Image
                Image(
                    painter = painterResource(id = bannerImage),
                    contentDescription = "Banner",
                    modifier =
                        Modifier.fillMaxWidth()
                            .height(screenHeight * 0.2f)
                            .testTag("sliding_window_banner_image"),
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
                            .offset(x = -(screenWidth * 0.04f))
                            .testTag("sliding_window_save_button"),
                    leadingIcon =
                        if (saved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder)

                // Profile picture overlapping the banner image
                Image(
                    painter = painterResource(id = profilePicture),
                    contentDescription = "Profile Picture",
                    modifier =
                        Modifier.size(screenHeight * 0.1f)
                            .align(Alignment.BottomStart)
                            .offset(x = screenWidth * 0.04f)
                            .clip(CircleShape)
                            .testTag("sliding_window_profile_picture"),
                    contentScale = ContentScale.Crop)
              }

          // Worker Field and Address under the profile picture
          Column(
              modifier =
                  Modifier.fillMaxWidth()
                      .padding(horizontal = screenWidth * 0.04f)
                      .testTag("sliding_window_worker_additional_info")) {
                Text(
                    text = workerCategory,
                    style = MaterialTheme.typography.headlineLarge,
                    color = colorScheme.onBackground,
                    modifier = Modifier.testTag("sliding_window_worker_category"))
                Text(
                    text = workerAddress,
                    style = MaterialTheme.typography.headlineSmall,
                    color = colorScheme.onBackground,
                    modifier = Modifier.testTag("sliding_window_worker_address"))
              }

          // Main content should be scrollable
          Column(
              modifier =
                  Modifier.fillMaxWidth()
                      .verticalScroll(rememberScrollState())
                      .background(colorScheme.surface)
                      .testTag("sliding_window_scrollable_content")) {
                Spacer(modifier = Modifier.height(screenHeight * 0.02f))

                // Description with "Show more" functionality
                val descriptionText =
                    if (showFullDescription || description.length <= 100) {
                      description
                    } else {
                      description.take(100) + "..."
                    }

                Text(
                    text = descriptionText,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurface,
                    modifier =
                        Modifier.padding(horizontal = screenWidth * 0.04f)
                            .testTag("sliding_window_description"))

                if (description.length > 100) {
                  Text(
                      text = if (showFullDescription) "Show less" else "Show more",
                      style = MaterialTheme.typography.bodySmall.copy(color = colorScheme.primary),
                      modifier =
                          Modifier.padding(horizontal = screenWidth * 0.04f)
                              .clickable { showFullDescription = !showFullDescription }
                              .testTag("sliding_window_description_show_more_button"))
                }

                // Delimiter between description and services
                Spacer(modifier = Modifier.height(screenHeight * 0.02f))

                HorizontalDivider(
                    modifier =
                        Modifier.padding(horizontal = screenWidth * 0.04f)
                            .testTag("sliding_window_horizontal_divider_1"),
                    thickness = 1.dp,
                    color = colorScheme.onSurface.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(screenHeight * 0.02f))

                // Services Section
                Row(
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(horizontal = screenWidth * 0.04f)
                            .testTag("sliding_window_services_row")) {
                      // Included Services
                      Column(
                          modifier =
                              Modifier.weight(1f)
                                  .testTag("sliding_window_included_services_column")) {
                            Text(
                                text = "Included Services",
                                style = MaterialTheme.typography.headlineMedium,
                                color = colorScheme.onBackground)
                            Spacer(modifier = Modifier.height(screenHeight * 0.01f))
                            includedServices.forEach { service ->
                              Text(
                                  text = "• $service",
                                  style = MaterialTheme.typography.bodySmall,
                                  color = colorScheme.onSurface,
                                  modifier = Modifier.padding(bottom = screenHeight * 0.005f))
                            }
                          }

                      Spacer(modifier = Modifier.width(screenWidth * 0.02f))

                      // Add-On Services
                      Column(
                          modifier =
                              Modifier.weight(1f).testTag("sliding_window_addon_services_column")) {
                            Text(
                                text = "Add-On Services",
                                style = MaterialTheme.typography.headlineMedium,
                                color = colorScheme.primary)
                            Spacer(modifier = Modifier.height(screenHeight * 0.01f))
                            addonServices.forEach { service ->
                              Text(
                                  text = "• $service",
                                  style = MaterialTheme.typography.bodySmall,
                                  color = colorScheme.primary,
                                  modifier = Modifier.padding(bottom = screenHeight * 0.005f))
                            }
                          }
                    }

                Spacer(modifier = Modifier.height(screenHeight * 0.03f))

                // Continue Button with Rate/HR
                QuickFixButton(
                    buttonText = "Continue",
                    onClickAction = onContinueClick,
                    buttonColor = colorScheme.primary,
                    textColor = colorScheme.onPrimary,
                    textStyle = MaterialTheme.typography.labelMedium,
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(horizontal = screenWidth * 0.04f)
                            .testTag("sliding_window_continue_button"))

                Spacer(modifier = Modifier.height(screenHeight * 0.02f))

                HorizontalDivider(
                    modifier =
                        Modifier.padding(horizontal = screenWidth * 0.04f)
                            .testTag("sliding_window_horizontal_divider_2"),
                    thickness = 1.dp,
                    color = colorScheme.onSurface.copy(alpha = 0.2f),
                )
                Spacer(modifier = Modifier.height(screenHeight * 0.02f))

                // Tags Section
                Text(
                    text = "Tags",
                    style = MaterialTheme.typography.headlineMedium,
                    color = colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = screenWidth * 0.04f))
                Spacer(modifier = Modifier.height(screenHeight * 0.01f))

                // Display tags using FlowRow for wrapping
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(screenWidth * 0.02f),
                    verticalArrangement = Arrangement.spacedBy(screenHeight * 0.01f),
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(horizontal = screenWidth * 0.04f)
                            .testTag("sliding_window_tags_flow_row"),
                ) {
                  tags.forEach { tag ->
                    Text(
                        text = tag,
                        color = colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall,
                        modifier =
                            Modifier.border(
                                    width = 1.dp,
                                    color = colorScheme.primary,
                                    shape = MaterialTheme.shapes.small)
                                .padding(
                                    horizontal = screenWidth * 0.02f,
                                    vertical = screenHeight * 0.005f))
                  }
                }

                Spacer(modifier = Modifier.height(screenHeight * 0.02f))

                HorizontalDivider(
                    modifier =
                        Modifier.padding(horizontal = screenWidth * 0.04f)
                            .testTag("sliding_window_horizontal_divider_3"),
                    thickness = 1.dp,
                    color = colorScheme.onSurface.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(screenHeight * 0.02f))

                Text(
                    text = "Reviews",
                    style = MaterialTheme.typography.headlineMedium,
                    color = colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = screenWidth * 0.04f))
                Spacer(modifier = Modifier.height(screenHeight * 0.01f))

                // Star Rating Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier =
                        Modifier.padding(horizontal = screenWidth * 0.04f)
                            .testTag("sliding_window_star_rating_row")) {
                      RatingBar(
                          workerRating.toFloat(),
                          modifier = Modifier.height(screenHeight * 0.03f).testTag("starsRow"))
                    }
                Spacer(modifier = Modifier.height(screenHeight * 0.01f))
                LazyRow(
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(horizontal = screenWidth * 0.04f)
                            .testTag("sliding_window_reviews_row")) {
                      itemsIndexed(reviews) { index, review ->
                        var isExpanded by remember { mutableStateOf(false) }
                        val displayText =
                            if (isExpanded || review.length <= 100) {
                              review
                            } else {
                              review.take(100) + "..."
                            }

                        Box(
                            modifier =
                                Modifier.padding(end = screenWidth * 0.02f)
                                    .width(screenWidth * 0.6f)
                                    .clip(RoundedCornerShape(25f))
                                    .background(colorScheme.background)) {
                              Column(modifier = Modifier.padding(screenWidth * 0.02f)) {
                                Text(
                                    text = displayText,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colorScheme.onSurface)
                                if (review.length > 100) {
                                  Text(
                                      text = if (isExpanded) "See less" else "See more",
                                      style =
                                          MaterialTheme.typography.bodySmall.copy(
                                              color = colorScheme.primary),
                                      modifier =
                                          Modifier.clickable { isExpanded = !isExpanded }
                                              .padding(top = screenHeight * 0.01f))
                                }
                              }
                            }
                      }
                    }

                Spacer(modifier = Modifier.height(screenHeight * 0.02f))
              }
        }
  }
}
