package com.arygm.quickfix.ui.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import com.arygm.quickfix.model.quickfix.QuickFix

@Composable
fun QuickFixSlidingWindowContent(
    quickFix: QuickFix,
    onDismiss: () -> Unit,
    isVisible: Boolean = true
) {
  QuickFixSlidingWindow(isVisible = isVisible, onDismiss = onDismiss) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxHeight().background(MaterialTheme.colorScheme.surface)) {
          val screenWidth = maxWidth
          val screenHeight = maxHeight

          LazyColumn(
              modifier =
                  Modifier.fillMaxSize()
                      .padding(horizontal = screenWidth * 0.05f) // Relative padding
              ) {
                // Header with Title
                item {
                  Box(
                      modifier =
                          Modifier.fillMaxWidth()
                              .padding(
                                  vertical = screenHeight * 0.02f), // Relative vertical padding
                      contentAlignment = Alignment.Center) {
                        Text(
                            text = "${quickFix.userId}'s QuickFix request",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center)
                      }
                }

                // Spacer
                item { Spacer(modifier = Modifier.height(screenHeight * 0.01f)) }

                // Photo Description Section
                item {
                  Text(
                      text = "Photo description",
                      style = MaterialTheme.typography.bodyMedium,
                      color = MaterialTheme.colorScheme.onBackground)
                  Spacer(modifier = Modifier.height(screenHeight * 0.01f))
                  ImageSelector(screenWidth, screenHeight * 0.9f, quickFix)
                }

                // Spacer
                item { Spacer(modifier = Modifier.height(screenHeight * 0.02f)) }

                // Included and Add-On Services
                item {
                  Text(
                      text = "Selected services",
                      style = MaterialTheme.typography.headlineSmall,
                      color = MaterialTheme.colorScheme.onBackground)
                }
                item { Spacer(modifier = Modifier.height(screenHeight * 0.01f)) }

                // Included Services
                item {
                  Text(
                      text = "Included Services",
                      style = MaterialTheme.typography.bodyMedium,
                      color = MaterialTheme.colorScheme.onSurface)
                }
                items(quickFix.includedServices) { service ->
                  Row(
                      verticalAlignment = Alignment.CenterVertically,
                      modifier =
                          Modifier.padding(
                              vertical = screenHeight * 0.005f) // Relative vertical padding
                      ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(screenWidth * 0.04f) // Relative size
                            )
                        Spacer(modifier = Modifier.width(screenWidth * 0.02f)) // Relative spacing
                        Text(
                            text = service.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface)
                      }
                }

                // Spacer
                item { Spacer(modifier = Modifier.height(screenHeight * 0.01f)) }

                // Add-On Services
                item {
                  Text(
                      text = "Add-on Services",
                      style = MaterialTheme.typography.bodyMedium,
                      color = MaterialTheme.colorScheme.primary)
                }
                items(quickFix.addOnServices) { service ->
                  Row(
                      verticalAlignment = Alignment.CenterVertically,
                      modifier = Modifier.padding(vertical = screenHeight * 0.005f)) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(screenWidth * 0.04f))
                        Spacer(modifier = Modifier.width(screenWidth * 0.02f))
                        Text(
                            text = service.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary)
                      }
                }

                // Spacer
                item { Spacer(modifier = Modifier.height(screenHeight * 0.02f)) }

                // Description Section
                item {
                  Text(
                      text = "Description",
                      style = MaterialTheme.typography.headlineSmall,
                      color = MaterialTheme.colorScheme.onBackground)
                  Box(
                      modifier =
                          Modifier.fillMaxWidth()
                              .clip(RoundedCornerShape(screenWidth * 0.02f))
                              .background(MaterialTheme.colorScheme.background)
                              .padding(screenWidth * 0.03f)) {
                        Text(
                            text = quickFix.title,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                      }
                }

                // Spacer
                item { Spacer(modifier = Modifier.height(screenHeight * 0.02f)) }

                // Appointment Details
                item {
                  Text(
                      text = "Suggested appointment",
                      style = MaterialTheme.typography.headlineSmall,
                      color = MaterialTheme.colorScheme.onBackground)
                  Spacer(modifier = Modifier.height(screenHeight * 0.01f))

                  val appointmentTime =
                      quickFix.time.toDate().toString().split(" ")[3] // Extracts time
                  val appointmentDate =
                      quickFix.time
                          .toDate()
                          .toString()
                          .split(" ")
                          .take(3)
                          .joinToString(" ") // Extracts date

                  Box(
                      modifier =
                          Modifier.fillMaxWidth()
                              .clip(RoundedCornerShape(screenWidth * 0.02f))
                              .background(MaterialTheme.colorScheme.background)
                              .padding(screenWidth * 0.03f)) {
                        Column {
                          Row(
                              modifier = Modifier.fillMaxWidth(),
                              verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(screenWidth * 0.04f))
                                Spacer(modifier = Modifier.width(screenWidth * 0.02f))
                                Text(
                                    text = appointmentTime,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface)
                              }

                          Spacer(modifier = Modifier.height(screenHeight * 0.01f))

                          Row(
                              modifier = Modifier.fillMaxWidth(),
                              verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(screenWidth * 0.04f))
                                Spacer(modifier = Modifier.width(screenWidth * 0.02f))
                                Text(
                                    text = appointmentDate,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface)
                              }

                          Spacer(modifier = Modifier.height(screenHeight * 0.01f))

                          Row(
                              modifier = Modifier.fillMaxWidth(),
                              verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(screenWidth * 0.04f))
                                Spacer(modifier = Modifier.width(screenWidth * 0.02f))
                                Text(
                                    text = quickFix.location.name,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface)
                              }
                        }
                      }
                }

                // Spacer
                item { Spacer(modifier = Modifier.height(screenHeight * 0.02f)) }

                // Swipe Hint
                item {
                  Row(
                      modifier = Modifier.fillMaxWidth().padding(vertical = screenHeight * 0.02f),
                      horizontalArrangement = Arrangement.Center,
                      verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector =
                                Icons.AutoMirrored.Filled
                                    .ArrowForward, // Replace with an appropriate swipe icon
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.width(screenWidth * 0.02f))
                        Text(
                            text = "Swipe right to hide QuickFix details",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface)
                      }
                }
              }
        }
  }
}
