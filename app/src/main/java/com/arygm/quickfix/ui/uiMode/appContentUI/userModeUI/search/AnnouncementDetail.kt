package com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.search

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
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arygm.quickfix.R
import com.arygm.quickfix.model.category.Category
import com.arygm.quickfix.model.category.CategoryViewModel
import com.arygm.quickfix.model.category.getCategoryIcon
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.search.AnnouncementViewModel
import com.arygm.quickfix.model.search.AvailabilitySlot
import com.arygm.quickfix.model.switchModes.AppMode
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.elements.QuickFixDateTimePicker
import com.arygm.quickfix.ui.elements.QuickFixTextFieldCustom
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.poppinsTypography
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.navigation.UserScreen
import com.arygm.quickfix.utils.loadAppMode
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Locale

private const val PADDING_BETWEEN_ELEM = 8

@Composable
fun AnnouncementDetailScreen(
    announcementViewModel: AnnouncementViewModel,
    categoryViewModel: CategoryViewModel = viewModel(factory = CategoryViewModel.Factory(LocalContext.current)),
    preferencesViewModel: PreferencesViewModel,
    navigationActions: NavigationActions,
) {
  var isUser by remember { mutableStateOf(true) }
  LaunchedEffect(Unit) { isUser = loadAppMode(preferencesViewModel) == AppMode.USER.name }
  fun LocalDateTime.toMillis(): Long =
      this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

  val selectedAnnouncement by announcementViewModel.selectedAnnouncement.collectAsState()
  val imagesMap by announcementViewModel.announcementImagesMap.collectAsState()

  if (selectedAnnouncement != null) {
    val announcement = selectedAnnouncement!!
    var category by remember { mutableStateOf(Category()) }
    LaunchedEffect(Unit) {
      categoryViewModel.getCategoryBySubcategoryId(
          announcement.category,
          onSuccess = {
            if (it != null) {
              category = it
            }
          })
    }

    val description = remember { mutableStateOf(announcement.description) }
    var descriptionError by remember { mutableStateOf(false) }
    val images = imagesMap[announcement.announcementId] ?: emptyList()
    val bannerText = if (images.isNotEmpty()) "1 of ${images.size}" else null

    var isEditing by remember { mutableStateOf(false) }
    val dates = remember {
      mutableStateListOf<AvailabilitySlot>().apply { addAll(announcement.availability) }
    }
    var isEditingIndex by rememberSaveable { mutableStateOf<Int?>(null) }
    var showStartAvailabilityPopup by remember { mutableStateOf(false) }
    var showEndAvailabilityPopup by remember { mutableStateOf(false) }
    var tempStart by remember { mutableStateOf<Timestamp?>(null) }

    if (showStartAvailabilityPopup) {
      Dialog(onDismissRequest = { showStartAvailabilityPopup = false }) {
        println("showStartAvailabilityPopup")
        QuickFixDateTimePicker(
            onDateTimeSelected = { date, time ->
              val start = LocalDateTime.of(date, time)
              val startMillis = start.toMillis()
              tempStart = millisToTimestamp(startMillis)
              showStartAvailabilityPopup = false
              showEndAvailabilityPopup = true
            },
            onDismissRequest = { showStartAvailabilityPopup = false },
            modifier = Modifier.testTag("startAvailabilityPicker"))
      }
    }

    if (showEndAvailabilityPopup) {
      Dialog(onDismissRequest = { showEndAvailabilityPopup = false }) {
        QuickFixDateTimePicker(
            onDateTimeSelected = { date, time ->
              val end = LocalDateTime.of(date, time)

              tempStart?.let { start ->
                if (isEditingIndex == null) {
                  val endMillis = end.toMillis()
                  val tempEnd = millisToTimestamp(endMillis)
                  dates.add(AvailabilitySlot(start = start, end = tempEnd))
                } else {
                  isEditingIndex = null
                }
              }
              tempStart = null
              showEndAvailabilityPopup = false
            },
            onDismissRequest = { showEndAvailabilityPopup = false },
            modifier = Modifier.testTag("endAvailabilityPicker"))
      }
    }

    BoxWithConstraints {
      val widthRatio = maxWidth / 411
      val heightRatio = maxHeight / 860
      val sizeRatio = minOf(widthRatio, heightRatio)

      Box(
          modifier =
              Modifier.fillMaxSize()
                  .background(color = colorScheme.surface)
                  .align(Alignment.Center)
                  .testTag("AnnouncementDetailScreenRoot") // Tag the entire screen root
          ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().testTag("AnnouncementDetailLazyColumn"),
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
                                .background(Color.Gray)
                                .clickable {
                                  navigationActions.navigateTo(UserScreen.DISPLAY_UPLOADED_IMAGES)
                                }
                                .testTag("TopImageBox")) {
                          // Display the first image or a placeholder
                          if (images.isNotEmpty()) {
                            Image(
                                bitmap = images.first().second.asImageBitmap(),
                                contentDescription = "Announcement Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize().testTag("AnnouncementMainImage"))
                          }

                          // Back button
                          IconButton(
                              onClick = { navigationActions.goBack() },
                              modifier =
                                  Modifier.size(50.dp * sizeRatio.value)
                                      .align(Alignment.TopStart)
                                      .clip(CircleShape)
                                      .padding(PADDING_BETWEEN_ELEM.dp * sizeRatio.value)
                                      .background(colorScheme.surface, shape = CircleShape)
                                      .padding(
                                          horizontal = PADDING_BETWEEN_ELEM.dp * sizeRatio.value,
                                          vertical = 4.dp * sizeRatio.value)
                                      .testTag("GoBackIconBtn")) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Go Back",
                                    tint = colorScheme.primary)
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
                                        .background(colorScheme.surface, shape = CircleShape)
                                        .padding(
                                            horizontal = PADDING_BETWEEN_ELEM.dp * sizeRatio.value,
                                            vertical = 4.dp * sizeRatio.value)
                                        .testTag("EditDeleteIconBtn")) {
                                  Icon(
                                      painter =
                                          if (isEditing) painterResource(id = R.drawable.delete)
                                          else painterResource(id = R.drawable.edit),
                                      contentDescription = if (isEditing) "Delete" else "Edit",
                                      tint = colorScheme.primary)
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
                                            vertical = 4.dp * sizeRatio.value)
                                        .testTag("BannerText"))
                          }
                        }
                  }
                  item {
                    // Content Section
                    Column(
                        modifier =
                            Modifier.fillMaxWidth()
                                .padding(horizontal = 16.dp * sizeRatio.value)
                                .testTag("AnnouncementContentColumn")) {
                          // Announcement Title
                          Row(
                              modifier = Modifier.fillMaxWidth().testTag("AnnouncementTitleRow"),
                              verticalAlignment = Alignment.CenterVertically) {
                                // Title Text
                                Text(
                                    text = announcement.title,
                                    style = poppinsTypography.titleMedium.copy(fontSize = 22.sp),
                                    color = colorScheme.onBackground,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.testTag("AnnouncementTitle"))

                                Spacer(modifier = Modifier.width(30.dp * widthRatio.value))

                                Icon(
                                    imageVector = getCategoryIcon(category),
                                    contentDescription = "Category Icon",
                                    tint = colorScheme.primary,
                                    modifier =
                                        Modifier.testTag("CategoryIconForSelectedAnnouncement"))
                              }

                          Spacer(
                              modifier =
                                  Modifier.height(PADDING_BETWEEN_ELEM.dp * heightRatio.value))

                          // Description
                          QuickFixTextFieldCustom(
                              modifier = Modifier.testTag("DescriptionTextField"),
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
                                      .padding(vertical = PADDING_BETWEEN_ELEM.dp)
                                      .testTag("LocationRow"),
                              verticalAlignment = Alignment.CenterVertically) {
                                // Location Icon
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Location Icon",
                                    tint = colorScheme.onSurface,
                                    modifier =
                                        Modifier.size(35.dp * sizeRatio.value)
                                            .padding(end = PADDING_BETWEEN_ELEM.dp)
                                            .testTag("LocationIcon"))

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
                                      style = poppinsTypography.labelSmall,
                                      color = colorScheme.onSurface,
                                      modifier = Modifier.testTag("LocationValue"))
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
                                            .padding(end = PADDING_BETWEEN_ELEM.dp)
                                            .testTag("CalendarIcon"))

                                // Section Title
                                Text(
                                    text = "Date and time",
                                    style =
                                        poppinsTypography.headlineMedium.copy(
                                            fontSize = 12.sp, fontWeight = FontWeight.Medium),
                                    color = colorScheme.onBackground,
                                    modifier = Modifier.testTag("DateAndTimeTitle"))
                              }

                          Spacer(
                              modifier =
                                  Modifier.height(PADDING_BETWEEN_ELEM.dp * heightRatio.value))

                          // Define date and time formatters
                          val dateFormatter = SimpleDateFormat("EEE, dd MMM", Locale.getDefault())
                          val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

                          // Availability
                          val startAvailability = {
                            isEditingIndex = null
                            showStartAvailabilityPopup = true
                          }
                          // Availability List or Fallback Message
                          Column(modifier = Modifier.testTag("AvailabilitySection")) {
                            if (dates.isNotEmpty()) {
                              // Availability Slots Header
                              Row(
                                  modifier =
                                      Modifier.fillMaxWidth()
                                          .padding(horizontal = 8.dp)
                                          .testTag("AvailabilityHeader"),
                                  horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(
                                        text = "Day",
                                        style =
                                            poppinsTypography.bodyMedium.copy(
                                                fontWeight = FontWeight.Medium),
                                        color = colorScheme.onSurface)
                                    Text(
                                        text = "Time",
                                        style =
                                            poppinsTypography.bodyMedium.copy(
                                                fontWeight = FontWeight.Medium),
                                        color = colorScheme.onSurface)
                                  }

                              Spacer(
                                  modifier =
                                      Modifier.height(PADDING_BETWEEN_ELEM.dp * heightRatio.value))

                              // Display Each Availability Slot
                              dates.forEachIndexed { index, slot ->
                                val day = dateFormatter.format(slot.start.toDate())
                                val timeRange =
                                    "${timeFormatter.format(slot.start.toDate())} - ${
                                                timeFormatter.format(slot.end.toDate())
                                            }"
                                Row(
                                    modifier =
                                        Modifier.fillMaxWidth()
                                            .padding(horizontal = 8.dp)
                                            .testTag("AvailabilitySlot_$index"),
                                    horizontalArrangement = Arrangement.SpaceBetween) {
                                      Text(
                                          text = day,
                                          style = poppinsTypography.bodyMedium,
                                          color = colorScheme.onSurface)
                                      Text(
                                          text = timeRange,
                                          style = poppinsTypography.bodyMedium,
                                          color = colorScheme.onSurface)
                                    }
                              }
                            } else {
                              // Fallback Message for Empty Availability
                              Box(
                                  modifier =
                                      Modifier.fillMaxWidth().testTag("EmptyAvailabilityBox"),
                                  contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "Availability has not been specified",
                                        style = poppinsTypography.labelSmall,
                                        color = colorScheme.onSurface)
                                  }
                            }

                            if (isEditing) {
                              Text(
                                  text = "+ Add new",
                                  style =
                                      poppinsTypography.bodyMedium.copy(
                                          color = colorScheme.primary),
                                  modifier =
                                      Modifier.padding(top = 8.dp, start = 8.dp)
                                          .clickable {
                                            // Add a new AvailabilitySlot with default start/end
                                            // times
                                            showStartAvailabilityPopup = true
                                          }
                                          .testTag("AddNewAvailabilityBtn"))
                            }
                          }
                        }
                  }
                }

            // Buttons logic
            Box(
                modifier =
                    Modifier.fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                        .testTag("BottomButtonBox")) {
                  Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.Center) {
                        if (isUser) {
                          if (isEditing) {
                            QuickFixButton(
                                buttonText = "Update announcement",
                                onClickAction = {
                                  // Check if description or availability have changed
                                  val descriptionChanged =
                                      description.value != announcement.description
                                  val availabilityChanged =
                                      dates.toList() != announcement.availability

                                  if (descriptionChanged || availabilityChanged) {
                                    // Create a new Announcement object with updated fields
                                    val updatedAnnouncement =
                                        announcement.copy(
                                            description = description.value,
                                            availability = dates.toList())
                                    announcementViewModel.updateAnnouncement(updatedAnnouncement)
                                  }
                                  announcementViewModel.unselectAnnouncement()
                                  navigationActions.goBack()
                                },
                                buttonColor = colorScheme.primary,
                                textColor = colorScheme.onPrimary,
                                leadingIcon = Icons.Default.Edit,
                                leadingIconTint = colorScheme.onPrimary,
                                modifier = Modifier.testTag("UpdateAnnouncementBtn"))
                          }
                        } else {
                          QuickFixButton(
                              buttonText = "Propose a quickfix",
                              onClickAction = {
                                // TODO: Add the onclick action for proposing a quickfix
                              },
                              buttonColor = colorScheme.primary,
                              textColor = colorScheme.onPrimary,
                              leadingIcon = getCategoryIcon(category),
                              leadingIconTint = colorScheme.onPrimary,
                              modifier = Modifier.testTag("ProposeQuickFixBtn"))
                        }
                      }
                }
          }
    }
  }
}
