package com.arygm.quickfix.ui.quickfix

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.window.PopupProperties
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.locations.LocationViewModel
import com.arygm.quickfix.model.messaging.ChatViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.dataFields.AddOnService
import com.arygm.quickfix.model.profile.dataFields.IncludedService
import com.arygm.quickfix.model.quickfix.QuickFix
import com.arygm.quickfix.model.quickfix.QuickFixViewModel
import com.arygm.quickfix.model.quickfix.Status
import com.arygm.quickfix.ressources.C
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.elements.QuickFixCheckedListElement
import com.arygm.quickfix.ui.elements.QuickFixDateTimePicker
import com.arygm.quickfix.ui.elements.QuickFixTextFieldCustom
import com.arygm.quickfix.ui.elements.dashedBorder
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.profile.becomeWorker.views.personal.CameraBottomSheet
import com.arygm.quickfix.ui.theme.poppinsTypography
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun QuickFixFirstStep(
    locationViewModel: LocationViewModel,
    navigationActions: NavigationActions,
    quickFixViewModel: QuickFixViewModel,
    chatViewModel: ChatViewModel,
    workerName: String,
    profileViewModel: ProfileViewModel
) {

  val focusManager = LocalFocusManager.current
  val context = LocalContext.current
  var quickFixTile by remember { mutableStateOf("") }
  val listServices = listOf("Service 1", "Service 2", "Service 3", "Service 4", "Service 5")
  val checkedStatesServices = remember { mutableStateListOf(*Array(listServices.size) { false }) }

  val listAddOnServices =
      listOf(
          "Add-on Service 1",
          "Add-on Service 2",
          "Add-on Service 3",
          "Add-on Service 4",
          "Add-on Service 5")
  val checkedStatesAddOnServices = remember {
    mutableStateListOf(*Array(listAddOnServices.size) { false })
  }

  var quickNote by remember { mutableStateOf("") }

  var locationTitle by remember { mutableStateOf("") }
  var locationQuickFix by remember { mutableStateOf(Location(0.0, 0.0, "")) }
  var locationExpanded by remember { mutableStateOf(false) }
  val locationSuggestions by locationViewModel.locationSuggestions.collectAsState()

  var listDates by remember { mutableStateOf(emptyList<LocalDateTime>()) }
  var showDateTimePopup by remember { mutableStateOf(false) }
  var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
  var selectedTime by remember { mutableStateOf<LocalTime?>(null) }
  val dateFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM")
  val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

  var cameraBottomSheet by remember { mutableStateOf(false) }
  var listOfImagePath by remember { mutableStateOf(emptyList<String>()) }
  val stepDone by remember {
    derivedStateOf {
      quickFixTile.isNotEmpty() &&
          checkedStatesServices.any { it } &&
          listDates.isNotEmpty() &&
          locationTitle.isNotEmpty()
    }
  }
  BoxWithConstraints(
      modifier =
          Modifier.background(colorScheme.surface).pointerInput(Unit) {
            detectTapGestures(onTap = { focusManager.clearFocus() })
          }) {
        val widthRatio = maxWidth / 411
        val heightRatio = maxHeight / 860
        val sizeRatio = minOf(widthRatio, heightRatio)
        if (showDateTimePopup) {
          Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            QuickFixDateTimePicker(
                onDateTimeSelected = { date, time ->
                  listDates = listDates + LocalDateTime.of(date, time)
                  Log.d("QuickFixFirstStep", "listDates: $listDates")
                  showDateTimePopup = false
                },
                onDismissRequest = { showDateTimePopup = false })
          }
        }

        if (cameraBottomSheet) {
          CameraBottomSheet(
              onDismissRequest = { cameraBottomSheet = false },
              modifier = Modifier.fillMaxWidth(),
              onActionRequest = { imagePath -> listOfImagePath = listOfImagePath + imagePath },
              onShowBottomSheetChange = { cameraBottomSheet = false },
              sheetState = rememberModalBottomSheetState(),
          )
        }

        LazyColumn(
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 8.dp)) {
              item {
                QuickFixTextFieldCustom(
                    value = quickFixTile,
                    onValueChange = {
                      quickFixTile = it
                      // update the quickfix tile
                    },
                    placeHolderText = "Enter a title ...",
                    placeHolderColor = colorScheme.onSecondaryContainer,
                    label =
                        @Composable {
                          Text(
                              text = "Title",
                              style = poppinsTypography.labelSmall,
                              fontWeight = FontWeight.Medium,
                              color = colorScheme.onBackground,
                              modifier = Modifier.padding(horizontal = 4.dp))
                        },
                    showLabel = true,
                    shape = RoundedCornerShape(5.dp),
                    widthField = 400 * widthRatio,
                    moveContentHorizontal = 10.dp,
                    borderColor = colorScheme.tertiaryContainer,
                    borderThickness = 1.5.dp,
                    hasShadow = false,
                    textStyle =
                        poppinsTypography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                        ),
                    showLeadingIcon = { false },
                    showTrailingIcon = { false },
                )
              }

              item {
                Text(
                    text = "Features services",
                    style = poppinsTypography.labelSmall,
                    color = colorScheme.onBackground,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp, top = 10.dp))
              }

              items(listServices.size) { index ->
                QuickFixCheckedListElement(
                    listServices,
                    checkedStatesServices,
                    index,
                    widthRatio = widthRatio,
                    heightRatio = heightRatio)
              }

              item {
                Text(
                    text = "Add-on services",
                    style = poppinsTypography.labelSmall,
                    color = colorScheme.onBackground,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 4.dp))
              }

              items(listAddOnServices.size) { index ->
                QuickFixCheckedListElement(listAddOnServices, checkedStatesAddOnServices, index)
              }

              item { Spacer(modifier = Modifier.height(16.dp)) }

              item {
                QuickFixTextFieldCustom(
                    heightField = 150.dp * heightRatio.value,
                    widthField = 400.dp * widthRatio.value,
                    value = quickNote,
                    onValueChange = { quickNote = it },
                    shape = RoundedCornerShape(8.dp),
                    showLabel = true,
                    label =
                        @Composable {
                          Text(
                              text =
                                  buildAnnotatedString {
                                    append("Quick note")
                                    withStyle(
                                        style =
                                            SpanStyle(
                                                color = colorScheme.tertiaryContainer,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium)) {
                                          append(" (optional)")
                                        }
                                  },
                              style =
                                  poppinsTypography.headlineMedium.copy(
                                      fontSize = 12.sp, fontWeight = FontWeight.Medium),
                              color = colorScheme.onBackground,
                              modifier = Modifier.padding(bottom = 8.dp, start = 4.dp, top = 4.dp))
                        },
                    hasShadow = false,
                    borderColor = colorScheme.tertiaryContainer,
                    placeHolderText = "Type a description...",
                    maxChar = 1500,
                    showCharCounter = true,
                    moveCounter = 17.dp,
                    charCounterTextStyle =
                        poppinsTypography.headlineMedium.copy(
                            fontSize = 12.sp, fontWeight = FontWeight.Medium),
                    charCounterColor = colorScheme.onSecondaryContainer,
                    singleLine = false,
                    showLeadingIcon = { false },
                    showTrailingIcon = { false },
                )
              }

              item {
                if (listDates.isEmpty()) {
                  Row(
                      horizontalArrangement = Arrangement.Center,
                      modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { showDateTimePopup = true },
                            modifier = Modifier.padding(vertical = 16.dp),
                            shape = RoundedCornerShape(10.dp),
                        ) {
                          Row(
                              verticalAlignment = Alignment.CenterVertically,
                              horizontalArrangement = Arrangement.End,
                              modifier = Modifier.wrapContentWidth()) {
                                Icon(
                                    imageVector = Icons.Default.Event,
                                    contentDescription = "Event",
                                    tint = colorScheme.onPrimary,
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Add Suggested Date",
                                    style = poppinsTypography.labelSmall,
                                    color = colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold)
                              }
                        }
                      }
                }
              }
              item {
                if (listDates.isNotEmpty()) {
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
                                  text = "Suggested Date",
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
                            onClick = { showDateTimePopup = true },
                            modifier = Modifier.padding(top = 16.dp, end = 4.dp).weight(0.2f),
                            content = {
                              Icon(
                                  imageVector = Icons.Default.Add,
                                  contentDescription = "Add a suggested Date",
                              )
                            },
                            colors =
                                IconButtonDefaults.iconButtonColors(
                                    contentColor = colorScheme.primary,
                                ))
                      }
                }
              }
              items(listDates.size) { index ->
                HorizontalDivider(
                    color = colorScheme.background,
                    thickness = 1.5.dp,
                    modifier = Modifier.fillMaxWidth(0.5f).padding(start = 4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
                      Text(
                          text = listDates[index].toLocalDate().format(dateFormatter),
                          style = poppinsTypography.labelSmall,
                          color = colorScheme.onBackground,
                          fontWeight = FontWeight.Medium,
                          modifier = Modifier.weight(0.335f))
                      Text(
                          text = listDates[index].toLocalTime().format(timeFormatter),
                          style = poppinsTypography.labelSmall,
                          color = colorScheme.onBackground,
                          fontWeight = FontWeight.Medium,
                          modifier = Modifier.weight(0.35f))
                      TextButton(
                          onClick = { listDates = listDates.toMutableList().apply { /* Edit */} },
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
                            listDates = listDates.toMutableList().apply { removeAt(index) }
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

              item {
                QuickFixTextFieldCustom(
                    value = locationTitle,
                    onValueChange = {
                      locationExpanded = it.isNotEmpty()
                      locationTitle = it
                      if (locationExpanded) {
                        locationViewModel.setQuery(it)
                      }
                    },
                    singleLine = true,
                    placeHolderText = "Enter a location ...",
                    showLeadingIcon = { false },
                    showTrailingIcon = { false },
                    hasShadow = false,
                    placeHolderColor = colorScheme.onSecondaryContainer,
                    label =
                        @Composable {
                          Text(
                              text = "Location",
                              style = poppinsTypography.labelSmall,
                              fontWeight = FontWeight.Medium,
                              color = colorScheme.onBackground,
                              modifier = Modifier.padding(horizontal = 4.dp))
                        },
                    showLabel = true,
                    shape = RoundedCornerShape(5.dp),
                    widthField = 400 * widthRatio,
                    moveContentHorizontal = 10.dp,
                    borderColor = colorScheme.tertiaryContainer,
                    borderThickness = 1.5.dp,
                    textStyle =
                        poppinsTypography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                        ),
                )

                DropdownMenu(
                    expanded = locationExpanded,
                    properties = PopupProperties(focusable = false),
                    onDismissRequest = { locationExpanded = false },
                    modifier = Modifier.width(400 * widthRatio),
                    containerColor = colorScheme.surface,
                ) {
                  locationSuggestions.forEachIndexed { index, location ->
                    DropdownMenuItem(
                        onClick = {
                          locationExpanded = false
                          locationViewModel.setQuery(location.name)
                          locationTitle = location.name
                          locationQuickFix = location
                        },
                        text = {
                          Text(
                              text = location.name,
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
              }
              item {
                Text(
                    text = "Attached picture",
                    style = poppinsTypography.labelSmall,
                    color = colorScheme.onBackground,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp, top = 16.dp))
              }

              item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                      items(listOfImagePath.size) { index ->
                        SubcomposeAsyncImage(
                            model = listOfImagePath[index].takeIf { it.isNotEmpty() },
                            contentDescription = "Profile Picture",
                            modifier =
                                Modifier.fillMaxSize().semantics {
                                  testTag = C.Tag.personalInfoScreenprofilePicture
                                },
                            contentScale = ContentScale.FillBounds,
                            alignment = Alignment.Center,
                            loading = {
                              Box(
                                  contentAlignment = Alignment.Center,
                                  modifier = Modifier.fillMaxSize()) {
                                    Icon(
                                        imageVector = Icons.Outlined.CameraAlt,
                                        contentDescription = "Placeholder",
                                        tint = colorScheme.onBackground,
                                        modifier = Modifier.size(30.dp))
                                  }
                            },
                            error = {
                              Box(
                                  contentAlignment = Alignment.Center,
                                  modifier = Modifier.fillMaxSize()) {
                                    Icon(
                                        imageVector = Icons.Outlined.CameraAlt,
                                        contentDescription = "Error",
                                        tint = colorScheme.onBackground,
                                        modifier = Modifier.size(30.dp))
                                  }
                            },
                            success = {
                              SubcomposeAsyncImageContent(
                                  modifier =
                                      Modifier.padding(bottom = 16.dp)
                                          .size(120.dp)
                                          .clip(RoundedCornerShape(10.dp)))
                            })
                      }
                    }
              }
              item {
                Box(
                    modifier =
                        Modifier.fillMaxWidth().padding(top = 4.dp).let {
                          if (listOfImagePath.isEmpty()) {
                            it.height(100.dp)
                                .dashedBorder(
                                    width = 1.5.dp,
                                    brush = SolidColor(colorScheme.onSecondaryContainer),
                                    shape = RoundedCornerShape(10.dp),
                                    on = 7.dp,
                                    off = 7.dp)
                                .background(
                                    color = colorScheme.background,
                                    shape = RoundedCornerShape(10.dp))
                          } else {
                            it
                          }
                        }) {
                      if (listOfImagePath.isEmpty()) {
                        QuickFixButton(
                            buttonText = "Upload Pictures",
                            buttonColor = colorScheme.background,
                            onClickAction = { cameraBottomSheet = true },
                            modifier = Modifier.fillMaxWidth(),
                            height = 100.dp,
                            textColor = colorScheme.onBackground,
                            textStyle =
                                poppinsTypography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp,
                                ),
                            leadingIcon = Icons.Default.PhotoLibrary,
                            contentPadding = PaddingValues(0.dp))
                      } else {
                        QuickFixButton(
                            buttonText = "Add more pictures",
                            buttonColor = colorScheme.primary,
                            onClickAction = { cameraBottomSheet = true },
                            height = 50.dp,
                            textColor = colorScheme.onPrimary,
                            textStyle =
                                poppinsTypography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp,
                                ),
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            leadingIcon = Icons.Default.PhotoLibrary,
                            leadingIconTint = colorScheme.onPrimary,
                            contentPadding = PaddingValues(0.dp))
                      }
                    }
              }
              item {
                Row(
                    modifier =
                        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceAround) {
                      QuickFixButton(
                          buttonText = "Cancel",
                          buttonColor = Color.Transparent,
                          onClickAction = { /* navigationActions.goBack() */},
                          modifier = Modifier.weight(0.5f),
                          textColor = colorScheme.onSecondaryContainer,
                          textStyle =
                              poppinsTypography.labelMedium.copy(
                                  fontWeight = FontWeight.SemiBold,
                                  fontSize = 20.sp,
                              ),
                      )
                      Spacer(modifier = Modifier.width(8.dp))
                      QuickFixButton(
                          buttonText = "Continue",
                          buttonColor = colorScheme.primary,
                          onClickAction = {
                            quickFixViewModel.addQuickFix(
                                QuickFix(
                                    uid = quickFixViewModel.getRandomUid(),
                                    status = Status.PENDING,
                                    imageUrl = listOfImagePath,
                                    date =
                                        listDates.map {
                                          Timestamp(it.atZone(ZoneId.systemDefault()).toInstant())
                                        },
                                    time =
                                        Timestamp(
                                            listDates
                                                .first()
                                                .atZone(ZoneId.systemDefault())
                                                .toInstant()),
                                    includedServices =
                                        listServices
                                            .filterIndexed { index, _ ->
                                              checkedStatesServices[index]
                                            }
                                            .map { IncludedService(it) },
                                    addOnServices =
                                        listAddOnServices
                                            .filterIndexed { index, _ ->
                                              checkedStatesAddOnServices[index]
                                            }
                                            .map { AddOnService(it) },
                                    workerName = workerName,
                                    userName = "Place Holder, to change",
                                    title = quickFixTile,
                                    chatUid = chatViewModel.getRandomUid(),
                                    bill = emptyList(),
                                    location = locationQuickFix),
                                onSuccess = {
                                  /* navigationActions.goToQuickFixSecondStep() */
                                  Toast.makeText(context, "QuickFix added", Toast.LENGTH_SHORT)
                                      .show()
                                },
                                onFailure = { /* Handle failure */})
                          },
                          modifier = Modifier.weight(0.5f),
                          textColor = colorScheme.onPrimary,
                          textStyle =
                              poppinsTypography.labelMedium.copy(
                                  fontWeight = FontWeight.SemiBold,
                                  fontSize = 20.sp,
                              ),
                          enabled = stepDone)
                    }
              }
            }
      }
}
