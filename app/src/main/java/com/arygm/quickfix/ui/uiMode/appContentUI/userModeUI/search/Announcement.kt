package com.arygm.quickfix.ui.search

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBoxState.Companion.Saver
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.arygm.quickfix.R
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.account.LoggedInAccountViewModel
import com.arygm.quickfix.model.category.CategoryViewModel
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.locations.LocationViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.UserProfile
import com.arygm.quickfix.model.search.Announcement
import com.arygm.quickfix.model.search.AnnouncementViewModel
import com.arygm.quickfix.model.search.AvailabilitySlot
import com.arygm.quickfix.ui.camera.QuickFixUploadImageSheet
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.elements.QuickFixDateTimePicker
import com.arygm.quickfix.ui.elements.QuickFixTextFieldCustom
import com.arygm.quickfix.ui.elements.dashedBorder
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.profile.becomeWorker.views.professional.calculateMaxTextWidth
import com.arygm.quickfix.ui.theme.poppinsTypography
import com.arygm.quickfix.ui.userModeUI.navigation.UserScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.firebase.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun AnnouncementScreen(
    announcementViewModel: AnnouncementViewModel =
        viewModel(factory = AnnouncementViewModel.Factory),
    loggedInAccountViewModel: LoggedInAccountViewModel =
        viewModel(factory = LoggedInAccountViewModel.Factory),
    profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.UserFactory),
    accountViewModel: AccountViewModel = viewModel(factory = AccountViewModel.Factory),
    categoryViewModel: CategoryViewModel = viewModel(factory = CategoryViewModel.Factory),
    navigationActions: NavigationActions,
    isUser: Boolean = true,
    locationViewModel: LocationViewModel = viewModel(factory = LocationViewModel.Factory)
) {
  val loggedInAccount by loggedInAccountViewModel.loggedInAccount.collectAsState()
  val userId = loggedInAccount?.uid ?: "Should not happen"

  var selectedSubcategoryName by rememberSaveable { mutableStateOf("") }
  var title by rememberSaveable { mutableStateOf("") }
  var subcategoryTitle by rememberSaveable { mutableStateOf("") }
  var description by rememberSaveable { mutableStateOf("") }

  var locationLat by rememberSaveable { mutableStateOf<Double?>(null) }
  var locationLon by rememberSaveable { mutableStateOf<Double?>(null) }
  var locationName by rememberSaveable { mutableStateOf<String?>(null) }

  val location =
      if (locationLat != null && locationLon != null && locationName != null) {
        Location(latitude = locationLat!!, longitude = locationLon!!, name = locationName!!)
      } else null

  var locationTitle by rememberSaveable { mutableStateOf("") }
  var locationExpanded by remember { mutableStateOf(false) }
  val locationSuggestions by locationViewModel.locationSuggestions.collectAsState()

  var titleIsEmpty by rememberSaveable { mutableStateOf(true) }
  var locationIsSelected by rememberSaveable { mutableStateOf(false) }
  var descriptionIsEmpty by rememberSaveable { mutableStateOf(true) }

  fun LocalDateTime.toMillis(): Long =
      this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
  fun millisToLocalDateTime(millis: Long): LocalDateTime =
      LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault())

  val availabilitySaver =
      androidx.compose.runtime.saveable.Saver<List<Pair<Long, Long>>, List<List<Long>>>(
          save = { list -> list.map { pair -> listOf(pair.first, pair.second) } },
          restore = { saved -> saved.mapNotNull { if (it.size == 2) it[0] to it[1] else null } })

  var listAvailability by
      rememberSaveable(stateSaver = availabilitySaver) {
        mutableStateOf(emptyList<Pair<Long, Long>>())
      }

  var isEditingIndex by rememberSaveable { mutableStateOf<Int?>(null) }
  var showStartAvailabilityPopup by remember { mutableStateOf(false) }
  var showEndAvailabilityPopup by remember { mutableStateOf(false) }
  var tempStartMillis by remember { mutableStateOf<Long?>(null) }

  val dateFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM")
  val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

  var subcategoryExpanded by remember { mutableStateOf(false) }

  val uploadedImages by announcementViewModel.uploadedImages.collectAsState()
  var showUploadImageSheet by rememberSaveable { mutableStateOf(false) }

  val categories by categoryViewModel.categories.collectAsState()
  val allSubcategories = categories.flatMap { it.subcategories }

  val selectedSubcategory = allSubcategories.find { it.name == selectedSubcategoryName }
  val categoryIsSelected = selectedSubcategory != null

  LaunchedEffect(Unit) {
    categoryViewModel.getCategories()
    val selectedLocation = navigationActions.getFromBackStack("selectedLocation") as? Location
    if (selectedLocation != null) {
      locationLat = selectedLocation.latitude
      locationLon = selectedLocation.longitude
      locationName = selectedLocation.name
      locationIsSelected = true
      locationTitle = selectedLocation.name
    }
  }

  val sheetState = rememberModalBottomSheetState()

  val resetAnnouncementParameters = {
    title = ""
    subcategoryTitle = ""
    selectedSubcategoryName = ""
    description = ""
    locationLat = null
    locationLon = null
    locationName = null
    locationTitle = ""
    titleIsEmpty = true
    descriptionIsEmpty = true
    locationIsSelected = false
    listAvailability = emptyList()
    navigationActions.saveToCurBackStack("selectedLocation", null)
    announcementViewModel.clearUploadedImages()
  }

  val updateUserProfileWithAnnouncement: (Announcement) -> Unit = { announcement ->
    profileViewModel.fetchUserProfile(userId) { profile ->
      if (profile is UserProfile) {
        val announcementList = profile.announcements + announcement.announcementId
        profileViewModel.updateProfile(
            UserProfile(profile.locations, announcementList, profile.wallet, profile.uid),
            onSuccess = {
              accountViewModel.fetchUserAccount(profile.uid) { account ->
                loggedInAccountViewModel.setLoggedInAccount(account!!)
              }
            },
            onFailure = { e ->
              Log.e("ProfileViewModel", "Failed to update profile: ${e.message}")
            })
      } else {
        Log.e("Wrong profile", "Should be a user profile")
      }
    }
  }

  val handleSuccessfulImageUpload: (String, List<String>) -> Unit =
      { announcementId, uploadedImageUrls ->
        val availabilitySlots =
            listAvailability.map { (startMillis, endMillis) ->
              val start = Timestamp(startMillis / 1000, ((startMillis % 1000) * 1000000).toInt())
              val end = Timestamp(endMillis / 1000, ((endMillis % 1000) * 1000000).toInt())
              AvailabilitySlot(start = start, end = end)
            }

        val announcement =
            Announcement(
                announcementId = announcementId,
                userId = userId,
                title = title,
                category = selectedSubcategory?.name ?: "",
                description = description,
                location = location,
                availability = availabilitySlots,
                quickFixImages = uploadedImageUrls)
        announcementViewModel.announce(announcement)
        updateUserProfileWithAnnouncement(announcement)
        resetAnnouncementParameters()
      }

  if (showStartAvailabilityPopup) {
    Dialog(onDismissRequest = { showStartAvailabilityPopup = false }) {
      QuickFixDateTimePicker(
          onDateTimeSelected = { date, time ->
            val start = LocalDateTime.of(date, time)
            tempStartMillis = start.toMillis()
            showStartAvailabilityPopup = false
            showEndAvailabilityPopup = true
          },
          onDismissRequest = { showStartAvailabilityPopup = false })
    }
  }

  if (showEndAvailabilityPopup) {
    Dialog(onDismissRequest = { showEndAvailabilityPopup = false }) {
      QuickFixDateTimePicker(
          onDateTimeSelected = { date, time ->
            val end = LocalDateTime.of(date, time)
            tempStartMillis?.let { startMillis ->
              if (isEditingIndex == null) {
                listAvailability = listAvailability + (startMillis to end.toMillis())
              } else {
                val mutable = listAvailability.toMutableList()
                mutable[isEditingIndex!!] = (startMillis to end.toMillis())
                listAvailability = mutable
                isEditingIndex = null
              }
            }
            tempStartMillis = null
            showEndAvailabilityPopup = false
          },
          onDismissRequest = { showEndAvailabilityPopup = false })
    }
  }

  BoxWithConstraints {
    val widthRatio = maxWidth / 411
    val heightRatio = maxHeight / 860

    val categoryTextStyle =
        MaterialTheme.typography.labelMedium.copy(
            fontSize = 10.sp, color = colorScheme.onBackground, fontWeight = FontWeight.Medium)

    val maxCategoryTextWidth =
        calculateMaxTextWidth(
            texts = allSubcategories.map { it.name }, textStyle = categoryTextStyle)

    val dropdownMenuWidth = maxCategoryTextWidth + 40.dp

    Scaffold(
        containerColor = colorScheme.surface,
        topBar = {},
        modifier = Modifier.testTag("AnnouncementContent")) { padding ->
          Column(
              modifier =
                  Modifier.fillMaxSize()
                      .padding(padding)
                      .padding(start = 14.dp, end = 14.dp, top = 30.dp)
                      .verticalScroll(rememberScrollState()),
              horizontalAlignment = Alignment.Start,
              verticalArrangement = Arrangement.Top) {
                // Title
                QuickFixTextFieldCustom(
                    value = title,
                    onValueChange = {
                      title = it
                      titleIsEmpty = title.isEmpty()
                    },
                    placeHolderText = "Enter the title of your quickFix",
                    placeHolderColor = colorScheme.onSecondaryContainer,
                    shape = RoundedCornerShape(8.dp),
                    moveContentHorizontal = 10.dp,
                    heightField = 40.dp,
                    widthField = 380.dp * widthRatio.value,
                    showLabel = true,
                    label = {
                      Text(
                          text =
                              buildAnnotatedString {
                                append("Title")
                                withStyle(style = SpanStyle(color = colorScheme.primary)) {
                                  append(" *")
                                }
                              },
                          style =
                              MaterialTheme.typography.headlineMedium.copy(
                                  fontSize = 12.sp, fontWeight = FontWeight.Medium),
                          color = colorScheme.onBackground,
                          modifier = Modifier.testTag("titleText"))
                    },
                    hasShadow = false,
                    borderColor = colorScheme.tertiaryContainer,
                    modifier = Modifier.testTag("titleInput"))

                Spacer(modifier = Modifier.height(17.dp))

                // Subcategory
                Text(
                    text =
                        buildAnnotatedString {
                          append("Subcategory")
                          withStyle(style = SpanStyle(color = colorScheme.primary)) { append(" *") }
                        },
                    style =
                        MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 12.sp, fontWeight = FontWeight.Medium),
                    color = colorScheme.onBackground,
                    modifier = Modifier.testTag("categoryText"))

                Box {
                  QuickFixTextFieldCustom(
                      value = subcategoryTitle,
                      onValueChange = { newValue ->
                        subcategoryTitle = newValue
                        subcategoryExpanded = newValue.isNotEmpty() && allSubcategories.isNotEmpty()
                      },
                      placeHolderText = "Select a subcategory",
                      placeHolderColor = colorScheme.onSecondaryContainer,
                      shape = RoundedCornerShape(8.dp),
                      moveContentHorizontal = 10.dp,
                      heightField = 40.dp,
                      widthField = 380.dp * widthRatio.value,
                      showLabel = false,
                      hasShadow = false,
                      borderColor = colorScheme.tertiaryContainer,
                      modifier =
                          Modifier.testTag("categoryInput") // This will serve as category input
                      )

                  DropdownMenu(
                      expanded = subcategoryExpanded,
                      properties = PopupProperties(focusable = false),
                      onDismissRequest = { subcategoryExpanded = false },
                      modifier = Modifier.width(dropdownMenuWidth * widthRatio.value),
                      containerColor = colorScheme.surface) {
                        val filteredSubcategories =
                            allSubcategories.filter {
                              it.name.contains(subcategoryTitle, ignoreCase = true)
                            }

                        filteredSubcategories.forEachIndexed { index, sub ->
                          DropdownMenuItem(
                              text = { Text(text = sub.name, style = categoryTextStyle) },
                              onClick = {
                                subcategoryExpanded = false
                                subcategoryTitle = sub.name
                                selectedSubcategoryName = sub.name
                              },
                              modifier = Modifier.height(30.dp * heightRatio.value))
                          if (index < filteredSubcategories.size - 1) {
                            HorizontalDivider(
                                color = colorScheme.onSecondaryContainer, thickness = 1.5.dp)
                          }
                        }
                      }
                }

                Spacer(modifier = Modifier.height(17.dp))

                // Description
                QuickFixTextFieldCustom(
                    value = description,
                    onValueChange = {
                      description = it
                      descriptionIsEmpty = description.isEmpty()
                    },
                    placeHolderText = "Describe the quickFix",
                    placeHolderColor = colorScheme.onSecondaryContainer,
                    shape = RoundedCornerShape(8.dp),
                    moveContentHorizontal = 10.dp,
                    heightField = 150.dp,
                    widthField = 380.dp * widthRatio.value,
                    showLabel = true,
                    label = {
                      Text(
                          text =
                              buildAnnotatedString {
                                append("Description")
                                withStyle(style = SpanStyle(color = colorScheme.primary)) {
                                  append(" *")
                                }
                              },
                          style =
                              MaterialTheme.typography.headlineMedium.copy(
                                  fontSize = 12.sp, fontWeight = FontWeight.Medium),
                          color = colorScheme.onBackground,
                          modifier = Modifier.testTag("descriptionText"))
                    },
                    hasShadow = false,
                    borderColor = colorScheme.tertiaryContainer,
                    singleLine = false,
                    maxChar = 1500,
                    showCharCounter = true,
                    moveCounter = 17.dp,
                    charCounterTextStyle =
                        MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp, fontWeight = FontWeight.Medium),
                    charCounterColor = colorScheme.onSecondaryContainer,
                    modifier = Modifier.testTag("descriptionInput"))

                Spacer(modifier = Modifier.height(17.dp))

                // Location
                Text(
                    text =
                        buildAnnotatedString {
                          append("Location")
                          withStyle(style = SpanStyle(color = colorScheme.primary)) { append(" *") }
                        },
                    style =
                        MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 12.sp, fontWeight = FontWeight.Medium),
                    color = colorScheme.onBackground,
                    modifier = Modifier.testTag("locationText"))

                QuickFixTextFieldCustom(
                    value = locationTitle,
                    onValueChange = {
                      locationExpanded = it.isNotEmpty() && locationSuggestions.isNotEmpty()
                      locationTitle = it
                      if (it.isNotEmpty()) {
                        locationViewModel.setQuery(it)
                      }
                    },
                    singleLine = true,
                    placeHolderText = "Location",
                    showLeadingIcon = { false },
                    showTrailingIcon = { false },
                    hasShadow = false,
                    placeHolderColor = colorScheme.onSecondaryContainer,
                    showLabel = true,
                    shape = RoundedCornerShape(5.dp),
                    widthField = 380.dp * widthRatio.value,
                    moveContentHorizontal = 10.dp,
                    borderColor = colorScheme.tertiaryContainer,
                    borderThickness = 1.5.dp,
                    textStyle = poppinsTypography.labelSmall.copy(fontWeight = FontWeight.Medium),
                    modifier = Modifier.testTag("locationInput"))

                DropdownMenu(
                    expanded = locationExpanded,
                    properties = PopupProperties(focusable = false),
                    onDismissRequest = { locationExpanded = false },
                    modifier = Modifier.width(380.dp * widthRatio.value),
                    containerColor = colorScheme.surface,
                ) {
                  locationSuggestions.forEachIndexed { index, suggestion ->
                    DropdownMenuItem(
                        onClick = {
                          locationExpanded = false
                          locationViewModel.setQuery(suggestion.name)
                          locationTitle = suggestion.name
                          locationLat = suggestion.latitude
                          locationLon = suggestion.longitude
                          locationName = suggestion.name
                          locationIsSelected = true
                        },
                        text = {
                          Text(
                              text = suggestion.name,
                              style = poppinsTypography.labelSmall,
                              fontWeight = FontWeight.Medium,
                              color = colorScheme.onBackground,
                              modifier = Modifier.padding(horizontal = 4.dp))
                        })
                    if (index < locationSuggestions.size - 1) {
                      HorizontalDivider(
                          color = colorScheme.onSecondaryContainer, thickness = 1.5.dp)
                    }
                  }
                }

                Spacer(modifier = Modifier.height(17.dp))

                // Availability
                val startAvailability = {
                  isEditingIndex = null
                  showStartAvailabilityPopup = true
                }

                if (listAvailability.isEmpty()) {
                  Row(
                      horizontalArrangement = Arrangement.Center,
                      modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = startAvailability,
                            modifier = Modifier.padding(vertical = 16.dp),
                            shape = RoundedCornerShape(10.dp),
                        ) {
                          Row(
                              verticalAlignment = Alignment.CenterVertically,
                              horizontalArrangement = Arrangement.End,
                              modifier = Modifier.wrapContentWidth()) {
                                Icon(
                                    painter = painterResource(R.drawable.calendar),
                                    contentDescription = "Calendar",
                                    tint = colorScheme.onPrimary,
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Add Availability",
                                    style = poppinsTypography.labelSmall,
                                    color = colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold)
                              }
                        }
                      }
                } else {
                  Row(
                      horizontalArrangement = Arrangement.SpaceAround,
                      verticalAlignment = Alignment.Top,
                      modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier =
                                Modifier.fillMaxWidth()
                                    .padding(top = 16.dp, start = 4.dp)
                                    .weight(0.8f),
                            horizontalAlignment = Alignment.Start) {
                              Text(
                                  text = "Availability",
                                  style = poppinsTypography.headlineMedium,
                                  color = colorScheme.onBackground,
                                  fontWeight = FontWeight.SemiBold,
                                  fontSize = 16.sp,
                                  modifier = Modifier.padding(bottom = 16.dp))
                              Row(
                                  modifier = Modifier.fillMaxWidth(0.8f).padding(bottom = 4.dp),
                              ) {
                                Text(
                                    text = "Day",
                                    style = poppinsTypography.labelSmall,
                                    color = colorScheme.onBackground,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(0.42f))
                                Text(
                                    text = "Time",
                                    style = poppinsTypography.labelSmall,
                                    color = colorScheme.onBackground,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(0.38f))
                              }
                            }

                        IconButton(
                            onClick = startAvailability,
                            modifier = Modifier.padding(top = 16.dp, end = 4.dp).weight(0.2f),
                            content = {
                              Icon(
                                  imageVector = Icons.Default.Add,
                                  contentDescription = "Add Availability",
                              )
                            },
                            colors =
                                IconButtonDefaults.iconButtonColors(
                                    contentColor = colorScheme.primary,
                                ))
                      }

                  listAvailability.forEachIndexed { index, (startMillis, endMillis) ->
                    HorizontalDivider(
                        color = colorScheme.background,
                        thickness = 1.5.dp,
                        modifier = Modifier.fillMaxWidth(0.5f).padding(start = 4.dp))

                    val start = millisToLocalDateTime(startMillis)
                    val end = millisToLocalDateTime(endMillis)

                    val startDay = start.toLocalDate().format(dateFormatter)
                    val endDay = end.toLocalDate().format(dateFormatter)
                    val startTimeText = start.toLocalTime().format(timeFormatter)
                    val endTimeText = end.toLocalTime().format(timeFormatter)

                    val dayText =
                        if (startDay == endDay) {
                          startDay
                        } else {
                          "$startDay - $endDay"
                        }

                    val timeText = "$startTimeText - $endTimeText"

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
                          Text(
                              text = dayText,
                              style = poppinsTypography.labelSmall,
                              color = colorScheme.onBackground,
                              fontWeight = FontWeight.Medium,
                              modifier = Modifier.weight(0.335f))
                          Text(
                              text = timeText,
                              style = poppinsTypography.labelSmall,
                              color = colorScheme.onBackground,
                              fontWeight = FontWeight.Medium,
                              modifier = Modifier.weight(0.35f))
                          TextButton(
                              onClick = {
                                isEditingIndex = index
                                tempStartMillis = startMillis
                                showStartAvailabilityPopup = true
                              },
                              modifier = Modifier.wrapContentWidth().weight(0.15f),
                              shape = RoundedCornerShape(10.dp),
                              colors =
                                  ButtonDefaults.textButtonColors(
                                      contentColor = colorScheme.primary,
                                  ),
                              contentPadding = PaddingValues(0.dp)) {
                                Text(
                                    text = "Edit",
                                    style = poppinsTypography.labelSmall,
                                    fontWeight = FontWeight.SemiBold)
                              }
                          TextButton(
                              onClick = {
                                listAvailability =
                                    listAvailability.toMutableList().apply { removeAt(index) }
                              },
                              modifier = Modifier.wrapContentWidth().weight(0.15f),
                              shape = RoundedCornerShape(10.dp),
                              colors =
                                  ButtonDefaults.textButtonColors(
                                      contentColor = colorScheme.primary,
                                  ),
                              contentPadding = PaddingValues(0.dp)) {
                                Text(
                                    text = "Remove",
                                    style = poppinsTypography.labelSmall,
                                    fontWeight = FontWeight.SemiBold)
                              }
                        }
                  }
                }

                Spacer(modifier = Modifier.height(17.dp))

                // Upload images section
                Text(
                    text = "Attached picture",
                    style = poppinsTypography.labelSmall,
                    color = colorScheme.onBackground,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp, top = 16.dp))

                if (uploadedImages.isEmpty()) {
                  Column(
                      modifier =
                          Modifier.fillMaxWidth()
                              .height(100.dp)
                              .testTag(
                                  "picturesButton") // Tag for the upload pictures button scenario
                              .dashedBorder(
                                  width = 1.5.dp,
                                  brush = SolidColor(colorScheme.onSecondaryContainer),
                                  shape = RoundedCornerShape(10.dp),
                                  on = 7.dp,
                                  off = 7.dp)
                              .background(
                                  color = colorScheme.background,
                                  shape = RoundedCornerShape(10.dp)),
                      verticalArrangement = Arrangement.Center,
                      horizontalAlignment = Alignment.CenterHorizontally) {
                        QuickFixButton(
                            buttonText = "Upload Pictures",
                            buttonColor = colorScheme.background,
                            onClickAction = { showUploadImageSheet = true },
                            modifier = Modifier.wrapContentWidth(),
                            height = 50.dp,
                            textColor = colorScheme.onBackground,
                            textStyle =
                                poppinsTypography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp,
                                ),
                            leadingIcon = Icons.Default.PhotoLibrary,
                            contentPadding = PaddingValues(0.dp))
                      }
                } else {
                  Box(
                      modifier =
                          Modifier.fillMaxWidth()
                              .height(100.dp)
                              .padding(horizontal = 16.dp)
                              .testTag("uploadedImagesBox"), // Tag the uploaded images box
                      contentAlignment = Alignment.Center) {
                        LazyRow(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier =
                                Modifier.fillMaxWidth()
                                    .height(100.dp)
                                    .testTag("uploadedImagesLazyRow")) {
                              val visibleImages = uploadedImages.take(3)
                              val remainingImageCount = uploadedImages.size - 3

                              items(visibleImages.size) { index ->
                                Box(
                                    modifier =
                                        Modifier.padding(4.dp)
                                            .size(90.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .testTag("uploadedImageCard$index")) {
                                      Image(
                                          painter = rememberAsyncImagePainter(visibleImages[index]),
                                          contentDescription = "Image $index",
                                          modifier =
                                              Modifier.fillMaxSize().testTag("uploadedImage$index"),
                                          contentScale = ContentScale.Crop)

                                      if (index == 2 && remainingImageCount > 0) {
                                        Box(
                                            modifier =
                                                Modifier.fillMaxSize()
                                                    .background(Color.Black.copy(alpha = 0.6f))
                                                    .clickable {
                                                      navigationActions.navigateTo(
                                                          UserScreen.DISPLAY_UPLOADED_IMAGES)
                                                    }
                                                    .testTag("remainingImagesOverlay"),
                                            contentAlignment = Alignment.Center) {
                                              Text(
                                                  text = "+$remainingImageCount",
                                                  color = Color.White,
                                                  style = MaterialTheme.typography.bodyLarge)
                                            }
                                      }

                                      IconButton(
                                          onClick = {
                                            announcementViewModel.deleteUploadedImages(
                                                listOf(visibleImages[index]))
                                          },
                                          modifier =
                                              Modifier.align(Alignment.TopEnd)
                                                  .padding(4.dp)
                                                  .size(24.dp)
                                                  .clip(CircleShape)
                                                  .testTag("deleteImageButton$index")) {
                                            Icon(
                                                imageVector = Icons.Filled.Close,
                                                contentDescription = "Remove Image",
                                                tint = Color.White,
                                                modifier =
                                                    Modifier.background(
                                                        color = Color.Black.copy(alpha = 0.6f),
                                                        shape = CircleShape))
                                          }
                                    }
                              }
                            }
                      }

                  Spacer(modifier = Modifier.height(8.dp))
                  Row(
                      horizontalArrangement = Arrangement.Center,
                      modifier = Modifier.fillMaxWidth()) {
                        QuickFixButton(
                            buttonText = "Add more pictures",
                            buttonColor = colorScheme.primary,
                            onClickAction = { showUploadImageSheet = true },
                            height = 50.dp,
                            textColor = colorScheme.onPrimary,
                            textStyle =
                                poppinsTypography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp,
                                ),
                            modifier = Modifier.wrapContentWidth().padding(horizontal = 16.dp),
                            leadingIcon = Icons.Default.PhotoLibrary,
                            leadingIconTint = colorScheme.onPrimary,
                            contentPadding = PaddingValues(0.dp))
                      }
                }

                Spacer(modifier = Modifier.height(17.dp))

                // Mandatory fields message
                Text(
                    text = "* Mandatory fields",
                    color =
                        if (titleIsEmpty ||
                            !categoryIsSelected ||
                            !locationIsSelected ||
                            descriptionIsEmpty)
                            colorScheme.error
                        else colorScheme.onSecondaryContainer,
                    style =
                        MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.sp, fontWeight = FontWeight.Medium),
                    modifier = Modifier.testTag("mandatoryText"))

                Spacer(modifier = Modifier.height(17.dp))

                // Post announcement button
                QuickFixButton(
                    buttonText = "Post your announcement",
                    onClickAction = {
                      val announcementId = announcementViewModel.getNewUid()
                      val images = announcementViewModel.uploadedImages.value

                      if (images.isEmpty()) {
                        handleSuccessfulImageUpload(announcementId, emptyList())
                      } else {
                        announcementViewModel.uploadAnnouncementImages(
                            announcementId = announcementId,
                            images = images,
                            onSuccess = { uploadedImageUrls ->
                              handleSuccessfulImageUpload(announcementId, uploadedImageUrls)
                            },
                            onFailure = { e ->
                              Log.e(
                                  "AnnouncementViewModel", "Failed to upload images: ${e.message}")
                            })
                      }
                    },
                    buttonColor = colorScheme.primary,
                    textColor = colorScheme.onPrimary,
                    textStyle =
                        MaterialTheme.typography.titleMedium.copy(
                            fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
                    modifier =
                        Modifier.width(380.dp * widthRatio.value)
                            .height(50.dp)
                            .testTag("announcementButton"),
                    enabled =
                        !titleIsEmpty &&
                            categoryIsSelected &&
                            locationIsSelected &&
                            !descriptionIsEmpty)
              }
        }

    QuickFixUploadImageSheet(
        sheetState = sheetState,
        showModalBottomSheet = showUploadImageSheet,
        onDismissRequest = { showUploadImageSheet = false },
        onShowBottomSheetChange = { showUploadImageSheet = it },
        onActionRequest = { value -> announcementViewModel.addUploadedImage(value) })
  }
}
