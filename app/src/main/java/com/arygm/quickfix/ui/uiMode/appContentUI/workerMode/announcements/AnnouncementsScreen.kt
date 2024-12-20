package com.arygm.quickfix.ui.uiMode.appContentUI.workerMode.announcements

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.category.CategoryViewModel
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.search.AnnouncementViewModel
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.elements.QuickFixLocationFilterBottomSheet
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.poppinsTypography
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.search.SearchFilterButtons
import com.arygm.quickfix.ui.uiMode.workerMode.navigation.WorkerScreen
import com.arygm.quickfix.utils.loadUserId

@Composable
fun AnnouncementsScreen(
    announcementViewModel: AnnouncementViewModel,
    preferencesViewModel: PreferencesViewModel,
    workerProfileViewModel: ProfileViewModel,
    categoryViewModel: CategoryViewModel,
    accountViewModel: AccountViewModel,
    navigationActions: NavigationActions
) {
  val context = LocalContext.current
  var workerProfile by remember { mutableStateOf<WorkerProfile?>(null) }
  var uid by remember { mutableStateOf("Loading...") }

  LaunchedEffect(Unit) {
    uid = loadUserId(preferencesViewModel)
    workerProfileViewModel.fetchUserProfile(uid) { profile ->
      workerProfile = profile as WorkerProfile
    }
    announcementViewModel.getAnnouncementsForCurrentWorker()
  }

  val announcements by announcementViewModel.announcements.collectAsState()
  var filteredAnnouncements by remember { mutableStateOf(announcements) }
  val imagesForAnnouncements by announcementViewModel.announcementImagesMap.collectAsState()

  var showFilterButtons by remember { mutableStateOf(false) }

  var locationFilterApplied by remember { mutableStateOf(false) }
  var lastAppliedMaxDist by remember { mutableStateOf(200) }
  var selectedLocationIndex by remember { mutableStateOf<Int?>(null) }

  var phoneLocation by remember {
    mutableStateOf(com.arygm.quickfix.model.locations.Location(0.0, 0.0, "Default"))
  }
  var baseLocation by remember { mutableStateOf(phoneLocation) }
  var selectedLocation by remember { mutableStateOf(com.arygm.quickfix.model.locations.Location()) }
  var maxDistance by remember { mutableStateOf(0) }
  var showLocationBottomSheet by remember { mutableStateOf(false) }

  fun reapplyFilters() {
    var updatedAnnouncements = announcements

    if (locationFilterApplied) {
      updatedAnnouncements =
          announcementViewModel.filterAnnouncementsByDistance(
              updatedAnnouncements, selectedLocation, maxDistance)
    }

    filteredAnnouncements = updatedAnnouncements
  }

  val listOfButtons =
      listOf(
          SearchFilterButtons(
              onClick = {
                filteredAnnouncements = announcements
                locationFilterApplied = false
                lastAppliedMaxDist = 200
                selectedLocationIndex = null
                baseLocation = phoneLocation
              },
              text = "Clear",
              leadingIcon = Icons.Default.Clear,
              applied = false),
          SearchFilterButtons(
              onClick = { showLocationBottomSheet = true },
              text = "Location",
              leadingIcon = Icons.Default.LocationSearching,
              trailingIcon = Icons.Default.KeyboardArrowDown,
              applied = locationFilterApplied))

  BoxWithConstraints(modifier = Modifier.fillMaxSize().testTag("announcements_screen")) {
    val screenHeight = maxHeight
    val screenWidth = maxWidth
    Scaffold(modifier = Modifier.testTag("announcements_scaffold")) { paddingValues ->
      Column(
          modifier =
              Modifier.fillMaxWidth()
                  .padding(paddingValues)
                  .padding(top = screenHeight * 0.02f)
                  .testTag("main_column"),
          horizontalAlignment = Alignment.CenterHorizontally) {
            Column(
                modifier = Modifier.fillMaxWidth().testTag("title_column"),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top) {
                  Text(
                      text = "Announcements for you",
                      style = poppinsTypography.labelMedium,
                      fontSize = 24.sp,
                      fontWeight = FontWeight.SemiBold,
                      textAlign = TextAlign.Center,
                      modifier = Modifier.testTag("announcements_title"))
                  Text(
                      text = "Here are announcements that matches your profile",
                      style = poppinsTypography.labelSmall,
                      fontWeight = FontWeight.Medium,
                      fontSize = 12.sp,
                      color = colorScheme.onSurface,
                      textAlign = TextAlign.Center,
                      modifier = Modifier.testTag("announcements_subtitle"))
                }

            Row(
                modifier =
                    Modifier.fillMaxWidth()
                        .padding(top = screenHeight * 0.02f, bottom = screenHeight * 0.01f)
                        .padding(horizontal = screenWidth * 0.02f)
                        .wrapContentHeight()
                        .testTag("filter_buttons_row"),
                verticalAlignment = Alignment.CenterVertically,
            ) {
              IconButton(
                  onClick = { showFilterButtons = !showFilterButtons },
                  modifier = Modifier.padding(bottom = screenHeight * 0.01f).testTag("tuneButton"),
                  colors =
                      IconButtonDefaults.iconButtonColors(
                          containerColor =
                              if (showFilterButtons) colorScheme.primary else colorScheme.surface),
                  content = {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Filter",
                        tint =
                            if (showFilterButtons) colorScheme.onPrimary
                            else colorScheme.onBackground,
                        modifier = Modifier.testTag("tuneIcon"))
                  })

              Spacer(modifier = Modifier.width(10.dp).testTag("filter_spacer"))

              AnimatedVisibility(
                  visible = showFilterButtons,
                  modifier = Modifier.testTag("animated_filter_visibility")) {
                    LazyRow(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.testTag("lazy_filter_row")) {
                          items(listOfButtons.size) { index ->
                            val buttonData = listOfButtons[index]
                            QuickFixButton(
                                buttonText = buttonData.text,
                                onClickAction = buttonData.onClick,
                                buttonColor =
                                    if (buttonData.applied) colorScheme.primary
                                    else colorScheme.surface,
                                textColor =
                                    if (buttonData.applied) colorScheme.onPrimary
                                    else colorScheme.onBackground,
                                textStyle =
                                    poppinsTypography.labelSmall.copy(
                                        fontWeight = FontWeight.Medium),
                                height = screenHeight * 0.05f,
                                leadingIcon = buttonData.leadingIcon,
                                trailingIcon = buttonData.trailingIcon,
                                leadingIconTint =
                                    if (buttonData.applied) colorScheme.onPrimary
                                    else colorScheme.onBackground,
                                trailingIconTint =
                                    if (buttonData.applied) colorScheme.onPrimary
                                    else colorScheme.onBackground,
                                contentPadding =
                                    PaddingValues(
                                        vertical = 0.dp, horizontal = screenWidth * 0.02f),
                                modifier = Modifier.testTag("filter_button_${buttonData.text}"))
                            Spacer(
                                modifier =
                                    Modifier.width(screenHeight * 0.01f)
                                        .testTag("filter_button_spacer_$index"))
                          }
                        }
                  }
            }

            LazyColumn(modifier = Modifier.fillMaxWidth().testTag("worker_profiles_list")) {
              items(filteredAnnouncements.size) { index ->
                val announcement = filteredAnnouncements[index]

                LaunchedEffect(Unit) {
                  announcementViewModel.fetchAnnouncementImagesAsBitmaps(
                      announcement.announcementId)
                }

                val pairs = imagesForAnnouncements[announcement.announcementId] ?: emptyList()
                val bitmapToDisplay = pairs.firstOrNull()?.second

                AnnouncementCard(
                    modifier = Modifier.testTag("announcement_$index"),
                    announcement = announcement,
                    announcementImage = bitmapToDisplay,
                    accountViewModel = accountViewModel,
                    categoryViewModel = categoryViewModel) {
                      announcementViewModel.selectAnnouncement(announcement)
                      navigationActions.navigateTo(WorkerScreen.ANNOUNCEMENT_DETAIL)
                    }

                Spacer(
                    modifier =
                        Modifier.height(screenHeight * 0.004f)
                            .testTag("announcement_spacer_$index"))
              }
            }
          }
    }

    workerProfile?.let {
      QuickFixLocationFilterBottomSheet(
          showModalBottomSheet = showLocationBottomSheet,
          profile = it,
          phoneLocation = phoneLocation,
          selectedLocationIndex = selectedLocationIndex,
          onApplyClick = { location, max ->
            selectedLocation = location
            lastAppliedMaxDist = max
            baseLocation = location
            maxDistance = max

            if (location == com.arygm.quickfix.model.locations.Location(0.0, 0.0, "Default")) {
              Toast.makeText(context, "Enable Location In Settings", Toast.LENGTH_SHORT).show()
            }
            if (locationFilterApplied) {
              reapplyFilters()
            } else {
              filteredAnnouncements =
                  announcementViewModel.filterAnnouncementsByDistance(
                      filteredAnnouncements, location, max)
            }
            locationFilterApplied = true
          },
          onDismissRequest = { showLocationBottomSheet = false },
          onClearClick = {
            baseLocation = phoneLocation
            lastAppliedMaxDist = 200
            selectedLocation = com.arygm.quickfix.model.locations.Location()
            maxDistance = 0
            locationFilterApplied = false
            reapplyFilters()
          },
          clearEnabled = locationFilterApplied,
          end = lastAppliedMaxDist)
    }
  }
}
