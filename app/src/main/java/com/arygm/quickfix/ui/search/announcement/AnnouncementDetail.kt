package com.arygm.quickfix.ui.search.announcement

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.ElectricalServices
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arygm.quickfix.R
import com.arygm.quickfix.model.search.AnnouncementViewModel
import com.arygm.quickfix.model.search.AvailabilitySlot
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.elements.QuickFixTextFieldCustom
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.poppinsTypography
import java.text.SimpleDateFormat
import java.util.Locale

private const val PADDING_BETWEEN_ELEM = 8

@Composable
fun AnnouncementDetailScreen(
    announcementViewModel: AnnouncementViewModel,
    navigationActions: NavigationActions,
    isUser: Boolean,
    onUpdateClicked: () -> Unit = {}
) {
  BoxWithConstraints {
    val widthRatio = maxWidth / 411
    val heightRatio = maxHeight / 860
    val sizeRatio = minOf(widthRatio, heightRatio)

    val selectedAnnouncement by announcementViewModel.selectedAnnouncement.collectAsState()
    val imagesMap by announcementViewModel.announcementImagesMap.collectAsState()

    if (selectedAnnouncement != null) {
      val announcement = selectedAnnouncement!!

      val description = remember { mutableStateOf(announcement.description) }
      var descriptionError by remember { mutableStateOf(false) }
      val images = imagesMap[announcement.announcementId] ?: emptyList()
      val bannerText = if (images.isNotEmpty()) "1 of ${images.size}" else null

      var isEditing by remember { mutableStateOf(false) }
      val dates = remember {
        mutableStateListOf<AvailabilitySlot>().apply { addAll(announcement.availability) }
      }

      Box(
          modifier =
              Modifier.fillMaxSize()
                  .background(color = colorScheme.surface)
                  .align(Alignment.Center)) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(PADDING_BETWEEN_ELEM.dp)) {
                  item {
                    // Top Image Section
                    Box(
                        modifier =
                            Modifier.fillMaxWidth()
                                .height(200.dp * heightRatio.value)
                                .clip(
                                    RoundedCornerShape(
                                        bottomStart = 10.dp * sizeRatio.value,
                                        bottomEnd =
                                            10.dp * sizeRatio.value)) // Rounded bottom corners
                                .background(Color.Gray)) {
                          // Display the first image or a placeholder
                          if (images.isNotEmpty()) {
                            Image(
                                bitmap = images.first().asImageBitmap(),
                                contentDescription = "Announcement Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize())
                          }

                          // Back button
                          IconButton(
                              onClick = { navigationActions.goBack() },
                              modifier =
                                  Modifier.size(50.dp * sizeRatio.value)
                                      .align(Alignment.TopStart)
                                      .clip(CircleShape)
                                      .padding(PADDING_BETWEEN_ELEM.dp * sizeRatio.value)
                                      .background(
                                          MaterialTheme.colorScheme.surface, shape = CircleShape)
                                      .padding(
                                          horizontal = PADDING_BETWEEN_ELEM.dp * sizeRatio.value,
                                          vertical = 4.dp * sizeRatio.value)) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Go Back",
                                    tint = MaterialTheme.colorScheme.primary)
                              }

                          // Edit/Delete button (only for users)
                          if (isUser) {
                            IconButton(
                                onClick = {
                                  if (isEditing) { // Can delete
                                    announcementViewModel.deleteAnnouncementById(
                                        announcement.announcementId)
                                    announcementViewModel.unselectAnnouncement()
                                    navigationActions.goBack()
                                  } else { // Start editing
                                    isEditing = !isEditing
                                  }
                                },
                                modifier =
                                    Modifier.size(50.dp * sizeRatio.value)
                                        .align(Alignment.TopEnd)
                                        .clip(CircleShape)
                                        .padding(PADDING_BETWEEN_ELEM.dp * sizeRatio.value)
                                        .background(
                                            MaterialTheme.colorScheme.surface, shape = CircleShape)
                                        .padding(
                                            horizontal = PADDING_BETWEEN_ELEM.dp * sizeRatio.value,
                                            vertical = 4.dp * sizeRatio.value)) {
                                  Icon(
                                      painter =
                                          if (isEditing) painterResource(id = R.drawable.delete)
                                          else painterResource(id = R.drawable.edit),
                                      contentDescription = if (isEditing) "Delete" else "Edit",
                                      tint = MaterialTheme.colorScheme.primary)
                                }
                          }

                          // Banner for number of images
                          bannerText?.let {
                            Text(
                                text = it,
                                style = poppinsTypography.bodyMedium.copy(fontSize = 12.sp),
                                fontWeight = FontWeight.Normal,
                                color = colorScheme.onSurface,
                                modifier =
                                    Modifier.align(Alignment.BottomEnd)
                                        .padding(PADDING_BETWEEN_ELEM.dp * sizeRatio.value)
                                        .background(
                                            colorScheme.surface,
                                            RoundedCornerShape(4.dp * sizeRatio.value))
                                        .padding(
                                            horizontal = PADDING_BETWEEN_ELEM.dp * sizeRatio.value,
                                            vertical = 4.dp * sizeRatio.value))
                          }
                        }
                  }
                  item {
                    // Content Section
                    Column(
                        modifier =
                            Modifier.fillMaxWidth()
                                .padding(
                                    horizontal = 16.dp * sizeRatio.value) // Add padding for content
                        ) {
                          // Announcement Title
                          Text(
                              text = announcement.title,
                              style = poppinsTypography.titleMedium.copy(fontSize = 22.sp),
                              color = colorScheme.onBackground,
                              fontWeight = FontWeight.Bold,
                          )

                          Spacer(
                              modifier =
                                  Modifier.height(PADDING_BETWEEN_ELEM.dp * heightRatio.value))

                          // Description
                          QuickFixTextFieldCustom(
                              modifier = Modifier.semantics { testTag = "descriptionHolder" },
                              widthField = 380.dp * widthRatio.value,
                              value = description.value,
                              onValueChange = { description.value = it },
                              textColor = colorScheme.onSurface,
                              shape = RoundedCornerShape(PADDING_BETWEEN_ELEM.dp),
                              showLabel = true,
                              label = {
                                Text(
                                    text = "Description",
                                    style =
                                        poppinsTypography.headlineMedium.copy(
                                            fontSize = 12.sp, fontWeight = FontWeight.Medium),
                                    color = colorScheme.onBackground)
                              },
                              hasShadow = false,
                              borderColor =
                                  if (isEditing) colorScheme.tertiaryContainer
                                  else colorScheme.surface,
                              placeHolderText = "Type a description...",
                              isError = descriptionError,
                              errorText = "Please enter a description",
                              showError = descriptionError,
                              singleLine = false,
                              isTextField = isEditing,
                              enabled = isEditing,
                              heightInEnabled = true,
                              minHeight = 40.dp * heightRatio.value,
                          )

                          Spacer(
                              modifier =
                                  Modifier.height(PADDING_BETWEEN_ELEM.dp * heightRatio.value))
                          Divider(
                              color = colorScheme.onSurface.copy(alpha = 0.2f), thickness = 1.dp)
                          Spacer(
                              modifier =
                                  Modifier.height(PADDING_BETWEEN_ELEM.dp * heightRatio.value))

                          // Location Section
                          Row(
                              modifier =
                                  Modifier.fillMaxWidth()
                                      .padding(vertical = PADDING_BETWEEN_ELEM.dp),
                              verticalAlignment = Alignment.CenterVertically) {
                                // Location Icon
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Location Icon",
                                    tint = colorScheme.onSurface,
                                    modifier =
                                        Modifier.size(35.dp * sizeRatio.value)
                                            .padding(end = PADDING_BETWEEN_ELEM.dp))

                                Column {
                                  // Location Title
                                  Text(
                                      text = "Location",
                                      style =
                                          poppinsTypography.headlineMedium.copy(
                                              fontSize = 12.sp, fontWeight = FontWeight.Medium),
                                      color = colorScheme.onBackground)
                                  // Location Value
                                  Text(
                                      text = announcement.location?.name ?: "No location available",
                                      style = MaterialTheme.typography.labelSmall,
                                      color = colorScheme.onSurface)
                                }
                              }

                          Spacer(
                              modifier =
                                  Modifier.height(PADDING_BETWEEN_ELEM.dp * heightRatio.value))
                          Divider(
                              color = colorScheme.onSurface.copy(alpha = 0.2f), thickness = 1.dp)
                          Spacer(
                              modifier =
                                  Modifier.height(PADDING_BETWEEN_ELEM.dp * heightRatio.value))

                          // Date and Time Section
                          Row(
                              modifier = Modifier.fillMaxWidth(),
                              verticalAlignment = Alignment.CenterVertically) {
                                // Calendar Icon
                                Icon(
                                    painter = painterResource(id = R.drawable.calendar),
                                    contentDescription = "Calendar Icon",
                                    tint = colorScheme.onSurface,
                                    modifier =
                                        Modifier.size(35.dp * sizeRatio.value)
                                            .padding(
                                                end =
                                                    PADDING_BETWEEN_ELEM
                                                        .dp) // Space between icon and text
                                    )

                                // Section Title
                                Text(
                                    text = "Date and time",
                                    style =
                                        poppinsTypography.headlineMedium.copy(
                                            fontSize = 12.sp, fontWeight = FontWeight.Medium),
                                    color = colorScheme.onBackground)
                              }

                          Spacer(
                              modifier =
                                  Modifier.height(PADDING_BETWEEN_ELEM.dp * heightRatio.value))

                          // Define date and time formatters
                          val dateFormatter = SimpleDateFormat("EEE, dd MMM", Locale.getDefault())
                          val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

                          // Availability List or Fallback Message
                          if (dates.isNotEmpty()) {
                            // Availability Slots Header
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween) {
                                  Text(
                                      text = "Day",
                                      style =
                                          MaterialTheme.typography.bodyMedium.copy(
                                              fontWeight = FontWeight.Medium),
                                      color = colorScheme.onSurface)
                                  Text(
                                      text = "Time",
                                      style =
                                          MaterialTheme.typography.bodyMedium.copy(
                                              fontWeight = FontWeight.Medium),
                                      color = colorScheme.onSurface)
                                }

                            Spacer(
                                modifier =
                                    Modifier.height(PADDING_BETWEEN_ELEM.dp * heightRatio.value))

                            // Display Each Availability Slot
                            dates.forEach { slot ->
                              val day =
                                  dateFormatter.format(
                                      slot.start.toDate()) // Format date as "EEE, dd MMM"
                              val timeRange =
                                  "${timeFormatter.format(slot.start.toDate())} - ${
                                            timeFormatter.format(slot.end.toDate())
                                        }" // Format time as "hh:mm a"
                              Row(
                                  modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                                  horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(
                                        text = day, // Displays the day
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = colorScheme.onSurface)
                                    Text(
                                        text = timeRange, // Displays the time range
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = colorScheme.onSurface)
                                  }
                            }
                          } else {
                            // Fallback Message for Empty Availability
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center) {
                                  Text(
                                      text = "Availability has not been specified",
                                      style = MaterialTheme.typography.labelSmall,
                                      color = colorScheme.onSurface)
                                }
                          }

                          if (isEditing) {
                            Text(
                                text = "+ Add new",
                                style =
                                    MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.primary),
                                modifier =
                                    Modifier.padding(top = 8.dp, start = 8.dp).clickable {
                                      // Add a new AvailabilitySlot with default start and end times
                                      dates.add(
                                          AvailabilitySlot(
                                              start = com.google.firebase.Timestamp.now(),
                                              end = com.google.firebase.Timestamp.now()))
                                    })
                          }
                        }
                  }
                }

            // Buttons logic
            Box(
                modifier =
                    Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(bottom = 16.dp)) {
                  Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.Center) {
                        if (isUser) {
                          if (isEditing) {
                            QuickFixButton(
                                buttonText = "Update announce",
                                onClickAction = {
                                    // Check if description or availability have changed
                                    val descriptionChanged = description.value != announcement.description
                                    val availabilityChanged = dates.toList() != announcement.availability

                                    if (descriptionChanged || availabilityChanged) {
                                        // Create a new Announcement object with updated fields
                                        val updatedAnnouncement = announcement.copy(
                                            description = description.value,
                                            availability = dates.toList()
                                        )
                                        announcementViewModel.updateAnnouncement(updatedAnnouncement)
                                    }
                                    announcementViewModel.unselectAnnouncement()
                                    navigationActions.goBack()
                                },
                                buttonColor = colorScheme.primary,
                                textColor = colorScheme.onPrimary,
                                leadingIcon = Icons.Default.Edit,
                                leadingIconTint = colorScheme.onPrimary)
                          }
                        } else {
                          QuickFixButton(
                              buttonText = "Propose a quickfix",
                              onClickAction = {
                                // TODO: Add the onclick action for proposing a quickfix
                              },
                              buttonColor = colorScheme.primary,
                              textColor = colorScheme.onPrimary,
                              leadingIcon = Icons.Outlined.ElectricalServices,
                              leadingIconTint = colorScheme.onPrimary)
                        }
                      }
                }
          }
    }
  }
}
