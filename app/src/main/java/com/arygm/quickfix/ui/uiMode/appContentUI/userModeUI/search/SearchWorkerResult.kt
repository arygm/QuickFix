package com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.search

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.arygm.quickfix.MainActivity
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.UserProfile
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.quickfix.QuickFixViewModel
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.ui.elements.ChooseServiceTypeSheet
import com.arygm.quickfix.ui.elements.QuickFixAvailabilityBottomSheet
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.elements.QuickFixLocationFilterBottomSheet
import com.arygm.quickfix.ui.elements.QuickFixPriceRangeBottomSheet
import com.arygm.quickfix.ui.elements.QuickFixSlidingWindow
import com.arygm.quickfix.ui.elements.RatingBar
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.poppinsTypography
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.navigation.UserScreen
import com.arygm.quickfix.utils.GeocoderWrapper
import com.arygm.quickfix.utils.LocationHelper
import com.arygm.quickfix.utils.loadUserId
import java.time.LocalDate

data class SearchFilterButtons(
    val onClick: () -> Unit,
    val text: String,
    val leadingIcon: ImageVector? = null,
    val trailingIcon: ImageVector? = null,
    val applied: Boolean = false
)

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchWorkerResult(
    navigationActions: NavigationActions,
    searchViewModel: SearchViewModel,
    accountViewModel: AccountViewModel,
    userProfileViewModel: ProfileViewModel,
    preferencesViewModel: PreferencesViewModel,
    quickFixViewModel: QuickFixViewModel,
    geocoderWrapper: GeocoderWrapper = GeocoderWrapper(LocalContext.current),
    workerViewModel: ProfileViewModel
) {
  fun getCityNameFromCoordinates(latitude: Double, longitude: Double): String? {
    val addresses = geocoderWrapper.getFromLocation(latitude, longitude, 1)
    return addresses?.firstOrNull()?.locality
        ?: addresses?.firstOrNull()?.subAdminArea
        ?: addresses?.firstOrNull()?.adminArea
  }
  val locationHelper = LocationHelper(LocalContext.current, MainActivity())
  var phoneLocation by remember {
    mutableStateOf(com.arygm.quickfix.model.locations.Location(0.0, 0.0, "Default"))
  }
  var baseLocation by remember { mutableStateOf(phoneLocation) }
  val context = LocalContext.current
  var userProfile by remember { mutableStateOf<UserProfile?>(null) }
  var uid by remember { mutableStateOf("Loading...") }

  var loading by remember { mutableStateOf(true) } // Tracks if data is loading

  LaunchedEffect(Unit) {
    if (locationHelper.checkPermissions()) {
      locationHelper.getCurrentLocation { location ->
        if (location != null) {
          phoneLocation =
              com.arygm.quickfix.model.locations.Location(
                  location.latitude, location.longitude, "Phone Location")
          baseLocation = phoneLocation
        } else {
          Toast.makeText(context, "Unable to fetch location", Toast.LENGTH_SHORT).show()
        }
      }
    } else {
      Toast.makeText(context, "Enable Location In Settings", Toast.LENGTH_SHORT).show()
    }
    uid = loadUserId(preferencesViewModel)
    userProfileViewModel.fetchUserProfile(uid) { profile -> userProfile = profile as UserProfile }
  }

  var selectedWorker by remember { mutableStateOf(WorkerProfile()) }
  var selectedCityName by remember { mutableStateOf<String?>(null) }
  var showFilterButtons by remember { mutableStateOf(true) }
  var showAvailabilityBottomSheet by remember { mutableStateOf(false) }
  var showServicesBottomSheet by remember { mutableStateOf(false) }
  var showPriceRangeBottomSheet by remember { mutableStateOf(false) }
  var showLocationBottomSheet by remember { mutableStateOf(false) }
  val workerProfiles by searchViewModel.subCategoryWorkerProfiles.collectAsState()
  Log.d("Chill guy", workerProfiles.size.toString())
  var filteredWorkerProfiles by remember { mutableStateOf(workerProfiles) }
  val searchSubcategory by searchViewModel.searchSubcategory.collectAsState()
  val searchCategory by searchViewModel.searchCategory.collectAsState()

  var availabilityFilterApplied by remember { mutableStateOf(false) }
  var servicesFilterApplied by remember { mutableStateOf(false) }
  var priceFilterApplied by remember { mutableStateOf(false) }
  var locationFilterApplied by remember { mutableStateOf(false) }
  var ratingFilterApplied by remember { mutableStateOf(false) }

  var selectedDays by remember { mutableStateOf(emptyList<LocalDate>()) }
  var selectedHour by remember { mutableStateOf(0) }
  var selectedMinute by remember { mutableStateOf(0) }
  var selectedServices by remember { mutableStateOf(emptyList<String>()) }
  var selectedPriceStart by remember { mutableStateOf(0) }
  var selectedPriceEnd by remember { mutableStateOf(0) }
  var selectedLocation by remember { mutableStateOf(com.arygm.quickfix.model.locations.Location()) }
  var maxDistance by remember { mutableStateOf(0) }
  var selectedLocationIndex by remember { mutableStateOf<Int?>(null) }

  var lastAppliedPriceStart by remember { mutableStateOf(500) }
  var lastAppliedPriceEnd by remember { mutableStateOf(2500) }
  var lastAppliedMaxDist by remember { mutableStateOf(200) }

  fun reapplyFilters() {
    Log.d("Chill guy", "entered")
    var updatedProfiles = workerProfiles

    if (availabilityFilterApplied) {
      updatedProfiles =
          searchViewModel.filterWorkersByAvailability(
              updatedProfiles, selectedDays, selectedHour, selectedMinute)
    }

    if (servicesFilterApplied) {
      updatedProfiles = searchViewModel.filterWorkersByServices(updatedProfiles, selectedServices)
    }

    if (priceFilterApplied) {
      updatedProfiles =
          searchViewModel.filterWorkersByPriceRange(
              updatedProfiles, selectedPriceStart, selectedPriceEnd)
    }

    if (locationFilterApplied) {
      updatedProfiles =
          searchViewModel.filterWorkersByDistance(updatedProfiles, selectedLocation, maxDistance)
    }

    if (ratingFilterApplied) {
      updatedProfiles = searchViewModel.sortWorkersByRating(updatedProfiles)
    }

    Log.d("Chill guy", updatedProfiles.size.toString())
    filteredWorkerProfiles = updatedProfiles
  }

  val listOfButtons =
      listOf(
          SearchFilterButtons(
              onClick = {
                filteredWorkerProfiles = workerProfiles
                availabilityFilterApplied = false
                priceFilterApplied = false
                locationFilterApplied = false
                ratingFilterApplied = false
                servicesFilterApplied = false
                lastAppliedMaxDist = 200
                lastAppliedPriceStart = 500
                lastAppliedPriceEnd = 2500
                selectedLocationIndex = null
                selectedServices = emptyList()
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
              applied = locationFilterApplied),
          SearchFilterButtons(
              onClick = { showServicesBottomSheet = true },
              text = "Service Type",
              leadingIcon = Icons.Default.Handyman,
              trailingIcon = Icons.Default.KeyboardArrowDown,
              applied = servicesFilterApplied),
          SearchFilterButtons(
              onClick = { showAvailabilityBottomSheet = true },
              text = "Availability",
              leadingIcon = Icons.Default.CalendarMonth,
              trailingIcon = Icons.Default.KeyboardArrowDown,
              applied = availabilityFilterApplied),
          SearchFilterButtons(
              onClick = {
                if (ratingFilterApplied) {
                  ratingFilterApplied = false
                  reapplyFilters()
                } else {
                  filteredWorkerProfiles =
                      searchViewModel.sortWorkersByRating(filteredWorkerProfiles)
                  ratingFilterApplied = true
                }
              },
              text = "Highest Rating",
              leadingIcon = Icons.Default.WorkspacePremium,
              trailingIcon = if (ratingFilterApplied) Icons.Default.Clear else null,
              applied = ratingFilterApplied),
          SearchFilterButtons(
              onClick = { showPriceRangeBottomSheet = true },
              text = "Price Range",
              leadingIcon = Icons.Default.MonetizationOn,
              trailingIcon = Icons.Default.KeyboardArrowDown,
              applied = priceFilterApplied),
      )

  // ==========================================================================//
  // ============ TODO: REMOVE NO-DATA WHEN BACKEND IS IMPLEMENTED ============//
  // ==========================================================================//

  var bannerPicture by remember { mutableStateOf<Bitmap?>(null) }
  var profilePicture by remember { mutableStateOf<Bitmap?>(null) }

  // ==========================================================================//
  // ==========================================================================//
  // ==========================================================================//

  var isWindowVisible by remember { mutableStateOf(false) }
  var saved by remember { mutableStateOf(false) }

  val profileImagesMap by remember { mutableStateOf(mutableMapOf<String, Bitmap?>()) }
  val bannerImagesMap by remember { mutableStateOf(mutableMapOf<String, Bitmap?>()) }

  // Check if all required data is fetched
  LaunchedEffect(workerProfiles) {
    if (workerProfiles.isNotEmpty()) {
      workerProfiles.forEach { profile ->
        // Fetch profile images
        workerViewModel.fetchProfileImageAsBitmap(
            profile.uid,
            onSuccess = { bitmap ->
              profileImagesMap[profile.uid] = bitmap
              checkIfLoadingComplete(workerProfiles, profileImagesMap, bannerImagesMap) {
                loading = false
              }
            },
            onFailure = { Log.e("ProfileResults", "Failed to fetch profile image") })

        // Fetch banner images
        workerViewModel.fetchBannerImageAsBitmap(
            profile.uid,
            onSuccess = { bitmap ->
              bannerImagesMap[profile.uid] = bitmap
              checkIfLoadingComplete(workerProfiles, profileImagesMap, bannerImagesMap) {
                loading = false
              }
            },
            onFailure = { Log.e("ProfileResults", "Failed to fetch banner image") })
      }
    } else {
      loading = false // No profiles to load
    }
  }

  // Wrap everything in a Box to allow overlay
  BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
    val screenHeight = maxHeight
    val screenWidth = maxWidth
    Log.d("Screen Dimensions", "Height: $screenHeight, Width: $screenWidth")
    // Scaffold containing the main UI elements
    Scaffold(
        topBar = {
          CenterAlignedTopAppBar(
              title = {
                Text(text = "Search Results", style = MaterialTheme.typography.titleMedium)
              },
              navigationIcon = {
                IconButton(onClick = { navigationActions.goBack() }) {
                  Icon(
                      imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                      contentDescription = "Back")
                }
              },
              actions = {
                IconButton(onClick = { /* Handle search */}) {
                  Icon(
                      imageVector = Icons.Default.Search,
                      contentDescription = "Search",
                      tint = colorScheme.onBackground)
                }
              },
              colors =
                  TopAppBarDefaults.centerAlignedTopAppBarColors(
                      containerColor = colorScheme.surface),
          )
        }) { paddingValues ->
          // Main content inside the Scaffold
          if (loading) {
            // Display a loader
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
              CircularProgressIndicator(
                  color = colorScheme.primary, modifier = Modifier.size(64.dp))
            }
          } else {
            Column(
                modifier = Modifier.fillMaxWidth().padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally) {
                  Column(
                      modifier = Modifier.fillMaxWidth().background(colorScheme.surface),
                      horizontalAlignment = Alignment.CenterHorizontally,
                      verticalArrangement = Arrangement.Top) {
                        Text(
                            text = searchSubcategory?.name ?: "Unknown",
                            style = poppinsTypography.labelMedium,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = searchCategory?.description ?: "Unknown",
                            style = poppinsTypography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            color = colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                        )
                        Row(
                            modifier =
                                Modifier.fillMaxWidth()
                                    .padding(
                                        top = screenHeight * 0.02f, bottom = screenHeight * 0.01f)
                                    .padding(horizontal = screenWidth * 0.02f)
                                    .wrapContentHeight()
                                    .testTag("filter_buttons_row")
                                    .background(colorScheme.surface),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                          // Tune Icon - fixed, non-scrollable
                          IconButton(
                              onClick = { showFilterButtons = !showFilterButtons },
                              modifier =
                                  Modifier.padding(bottom = screenHeight * 0.01f)
                                      .testTag("tuneButton"),
                              content = {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = "Filter",
                                    tint =
                                        if (showFilterButtons) colorScheme.onPrimary
                                        else colorScheme.onBackground,
                                )
                              },
                              colors =
                                  IconButtonDefaults.iconButtonColors(
                                      containerColor =
                                          if (showFilterButtons) colorScheme.primary
                                          else colorScheme.surface),
                          )

                          Spacer(modifier = Modifier.width(10.dp))

                          AnimatedVisibility(visible = showFilterButtons) {
                            LazyRow(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.testTag("lazy_filter_row")) {
                                  items(listOfButtons.size) { index ->
                                    QuickFixButton(
                                        buttonText = listOfButtons[index].text,
                                        onClickAction = listOfButtons[index].onClick,
                                        buttonColor =
                                            if (listOfButtons[index].applied) colorScheme.primary
                                            else colorScheme.surface,
                                        textColor =
                                            if (listOfButtons[index].applied) colorScheme.onPrimary
                                            else colorScheme.onBackground,
                                        textStyle =
                                            poppinsTypography.labelSmall.copy(
                                                fontWeight = FontWeight.Medium),
                                        height = screenHeight * 0.05f,
                                        leadingIcon = listOfButtons[index].leadingIcon,
                                        trailingIcon = listOfButtons[index].trailingIcon,
                                        leadingIconTint =
                                            if (listOfButtons[index].applied) colorScheme.onPrimary
                                            else colorScheme.onBackground,
                                        trailingIconTint =
                                            if (listOfButtons[index].applied) colorScheme.onPrimary
                                            else colorScheme.onBackground,
                                        contentPadding =
                                            PaddingValues(
                                                vertical = 0.dp, horizontal = screenWidth * 0.02f),
                                        modifier =
                                            Modifier.testTag(
                                                "filter_button_${listOfButtons[index].text}"))
                                    Spacer(modifier = Modifier.width(screenHeight * 0.01f))
                                  }
                                }
                          }
                        }
                        Text(
                            modifier =
                                Modifier.align(Alignment.Start)
                                    .padding(start = screenWidth * 0.03f),
                            text = searchSubcategory?.scale?.longScale ?: "Unknown",
                            color = colorScheme.error,
                            style = poppinsTypography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.sp)
                      }

                  LazyColumn(modifier = Modifier.fillMaxWidth().testTag("worker_profiles_list")) {
                    items(filteredWorkerProfiles.size) { index ->
                      val profile = filteredWorkerProfiles[index]
                      var account by remember { mutableStateOf<Account?>(null) }
                      var distance by remember { mutableStateOf<Int?>(null) }
                      var cityName by remember { mutableStateOf<String?>(null) }
                      val profileImage = profileImagesMap[profile.uid]
                      val bannerImage = bannerImagesMap[profile.uid]
                      distance =
                          profile.location
                              ?.let { workerLocation ->
                                searchViewModel.calculateDistance(
                                    workerLocation.latitude,
                                    workerLocation.longitude,
                                    baseLocation.latitude,
                                    baseLocation.longitude)
                              }
                              ?.toInt()

                      LaunchedEffect(profile.uid) {
                        accountViewModel.fetchUserAccount(profile.uid) { fetchedAccount: Account? ->
                          account = fetchedAccount
                        }
                      }

                      account?.let { acc ->
                        val locationName =
                            if (profile.location?.name.isNullOrEmpty()) "Unknown"
                            else profile.location?.name

                        locationName?.let {
                          cityName =
                              profile.location?.let { it1 ->
                                getCityNameFromCoordinates(it1.latitude, profile.location.longitude)
                              }
                          Log.d("Chill guy", cityName.toString())
                          cityName?.let { it1 ->
                            profileImage?.let { it2 ->
                              SearchWorkerProfileResult(
                                  modifier = Modifier.testTag("worker_profile_result$index"),
                                  profileImage = it2,
                                  name = profile.displayName,
                                  category = profile.fieldOfWork,
                                  rating =
                                      profile.reviews.map { review -> review.rating }.average(),
                                  reviewCount = profile.reviews.size,
                                  location = it1,
                                  price = profile.price.toString(),
                                  onBookClick = {
                                    selectedWorker = profile
                                    selectedCityName = cityName
                                    isWindowVisible = true
                                    profilePicture = it2
                                    bannerPicture = bannerImage!!
                                  },
                                  distance = distance,
                              )
                            }
                          }
                        }
                      }
                      Spacer(modifier = Modifier.height(screenHeight * 0.004f))
                    }
                  }
                }
          }
        }

    QuickFixAvailabilityBottomSheet(
        showAvailabilityBottomSheet,
        onDismissRequest = { showAvailabilityBottomSheet = false },
        onOkClick = { days, hour, minute ->
          selectedDays = days
          selectedHour = hour
          selectedMinute = minute
          if (availabilityFilterApplied) {
            reapplyFilters()
          } else {
            filteredWorkerProfiles =
                searchViewModel.filterWorkersByAvailability(
                    filteredWorkerProfiles, days, hour, minute)
          }
          availabilityFilterApplied = true
        },
        onClearClick = {
          availabilityFilterApplied = false
          selectedDays = emptyList()
          selectedHour = 0
          selectedMinute = 0
          reapplyFilters()
        },
        clearEnabled = availabilityFilterApplied)

    searchSubcategory?.let {
      ChooseServiceTypeSheet(
          showServicesBottomSheet,
          it.tags,
          selectedServices = selectedServices,
          onApplyClick = { services ->
            selectedServices = services
            if (servicesFilterApplied) {
              reapplyFilters()
            } else {
              filteredWorkerProfiles =
                  searchViewModel.filterWorkersByServices(filteredWorkerProfiles, selectedServices)
            }
            servicesFilterApplied = true
          },
          onDismissRequest = { showServicesBottomSheet = false },
          onClearClick = {
            selectedServices = emptyList()
            servicesFilterApplied = false
            reapplyFilters()
          },
          clearEnabled = servicesFilterApplied)
    }

    QuickFixPriceRangeBottomSheet(
        showPriceRangeBottomSheet,
        onApplyClick = { start, end ->
          selectedPriceStart = start
          selectedPriceEnd = end
          lastAppliedPriceStart = start
          lastAppliedPriceEnd = end
          if (priceFilterApplied) {
            reapplyFilters()
          } else {
            filteredWorkerProfiles =
                searchViewModel.filterWorkersByPriceRange(filteredWorkerProfiles, start, end)
          }
          priceFilterApplied = true
        },
        onDismissRequest = { showPriceRangeBottomSheet = false },
        onClearClick = {
          selectedPriceStart = 0
          selectedPriceEnd = 0
          lastAppliedPriceStart = 500
          lastAppliedPriceEnd = 2500
          priceFilterApplied = false
          reapplyFilters()
        },
        clearEnabled = priceFilterApplied,
        start = lastAppliedPriceStart,
        end = lastAppliedPriceEnd)

    userProfile?.let {
      QuickFixLocationFilterBottomSheet(
          showLocationBottomSheet,
          userProfile = it,
          phoneLocation = phoneLocation,
          selectedLocationIndex = selectedLocationIndex,
          onApplyClick = { location, max ->
            selectedLocation = location
            lastAppliedMaxDist = max
            baseLocation = location
            maxDistance = max
            selectedLocationIndex = userProfile!!.locations.indexOf(location) + 1

            if (location == com.arygm.quickfix.model.locations.Location(0.0, 0.0, "Default")) {
              Toast.makeText(context, "Enable Location In Settings", Toast.LENGTH_SHORT).show()
            }
            if (locationFilterApplied) {
              reapplyFilters()
            } else {
              filteredWorkerProfiles =
                  searchViewModel.filterWorkersByDistance(filteredWorkerProfiles, location, max)
            }
            locationFilterApplied = true
          },
          onDismissRequest = { showLocationBottomSheet = false },
          onClearClick = {
            baseLocation = phoneLocation
            lastAppliedMaxDist = 200
            selectedLocation = com.arygm.quickfix.model.locations.Location()
            maxDistance = 0
            selectedLocationIndex = null
            locationFilterApplied = false
            reapplyFilters()
          },
          clearEnabled = locationFilterApplied,
          end = lastAppliedMaxDist)
    }

    if (isWindowVisible) {
      Log.d("saved lists", userProfile?.savedList.toString() + selectedWorker.uid)
      Popup(
          onDismissRequest = { isWindowVisible = false },
          properties = PopupProperties(focusable = true)) {
            QuickFixSlidingWindow(
                isVisible = isWindowVisible, onDismiss = { isWindowVisible = false }) {
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
                                    .height(
                                        screenHeight *
                                            0.23f) // Adjusted height to accommodate profile picture
                                    // overlap
                                    .testTag("sliding_window_top_bar")) {
                              // Banner Image
                              Image(
                                  painter = BitmapPainter(bannerPicture!!.asImageBitmap()),
                                  contentDescription = "Banner",
                                  modifier =
                                      Modifier.fillMaxWidth()
                                          .height(screenHeight * 0.2f)
                                          .testTag("sliding_window_banner_image"),
                                  contentScale = ContentScale.Crop)

                              QuickFixButton(
                                  buttonText =
                                      if (userProfile?.savedList?.contains(selectedWorker.uid) ==
                                          true)
                                          "saved"
                                      else "save",
                                  onClickAction = {
                                    val profile = userProfile
                                    if (profile == null) {
                                      Log.e(
                                          "SlidingWindow",
                                          "Cannot update saved list: userProfile is null")
                                      return@QuickFixButton
                                    }

                                    val isSaved = profile.savedList.contains(selectedWorker.uid)
                                    val updatedList =
                                        if (isSaved) profile.savedList - selectedWorker.uid
                                        else profile.savedList + selectedWorker.uid
                                    val newProfile = profile.copy(savedList = updatedList)

                                    userProfileViewModel.updateProfile(
                                        newProfile,
                                        onSuccess = {
                                          userProfile = newProfile
                                          val message =
                                              if (isSaved) "Removed from saved list" else "Saved"
                                          Toast.makeText(context, message, Toast.LENGTH_SHORT)
                                              .show()
                                        },
                                        onFailure = {
                                          Log.e("SlidingWindow", "Failed to update profile")
                                        })
                                  },
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
                                      if (userProfile?.savedList?.contains(selectedWorker.uid) ==
                                          true)
                                          Icons.Filled.Bookmark
                                      else Icons.Outlined.BookmarkBorder)

                              // Profile picture overlapping the banner image
                              Image(
                                  painter = BitmapPainter(profilePicture!!.asImageBitmap()),
                                  contentDescription = "Profile Picture",
                                  modifier =
                                      Modifier.size(screenHeight * 0.1f)
                                          .align(Alignment.BottomStart)
                                          .offset(x = screenWidth * 0.04f)
                                          .clip(CircleShape)
                                          .testTag("sliding_window_profile_picture"),
                                  // Negative offset to position correctly
                                  contentScale = ContentScale.Crop)
                            }

                        // Worker Field and Address under the profile picture
                        Column(
                            modifier =
                                Modifier.fillMaxWidth()
                                    .padding(horizontal = screenWidth * 0.04f)
                                    .testTag("sliding_window_worker_additional_info")) {
                              Text(
                                  text = selectedWorker.displayName,
                                  style = MaterialTheme.typography.headlineLarge,
                                  color = colorScheme.onBackground,
                                  modifier = Modifier.testTag("sliding_window_worker_category"))
                              selectedCityName?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = colorScheme.onBackground,
                                    modifier = Modifier.testTag("sliding_window_worker_address"))
                              }
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
                              var showFullDescription by remember { mutableStateOf(false) }
                              val descriptionText =
                                  if (showFullDescription ||
                                      selectedWorker.description.length <= 100) {
                                    selectedWorker.description
                                  } else {
                                    selectedWorker.description.take(100) + "..."
                                  }

                              Text(
                                  text = descriptionText,
                                  style = MaterialTheme.typography.bodySmall,
                                  color = colorScheme.onSurface,
                                  modifier =
                                      Modifier.padding(horizontal = screenWidth * 0.04f)
                                          .testTag("sliding_window_description"))

                              if (selectedWorker.description.length > 100) {
                                Text(
                                    text = if (showFullDescription) "Show less" else "Show more",
                                    style =
                                        MaterialTheme.typography.bodySmall.copy(
                                            color = colorScheme.primary),
                                    modifier =
                                        Modifier.padding(horizontal = screenWidth * 0.04f)
                                            .clickable {
                                              showFullDescription = !showFullDescription
                                            }
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
                                                .testTag(
                                                    "sliding_window_included_services_column")) {
                                          Text(
                                              text = "Included Services",
                                              style = MaterialTheme.typography.headlineMedium,
                                              color = colorScheme.onBackground)
                                          Spacer(modifier = Modifier.height(screenHeight * 0.01f))
                                          selectedWorker.includedServices.forEach { service ->
                                            val name = service.name
                                            Text(
                                                text = "• $name",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = colorScheme.onSurface,
                                                modifier =
                                                    Modifier.padding(
                                                        bottom = screenHeight * 0.005f))
                                          }
                                        }

                                    Spacer(modifier = Modifier.width(screenWidth * 0.02f))

                                    // Add-On Services
                                    Column(
                                        modifier =
                                            Modifier.weight(1f)
                                                .testTag("sliding_window_addon_services_column")) {
                                          Text(
                                              text = "Add-On Services",
                                              style = MaterialTheme.typography.headlineMedium,
                                              color = colorScheme.primary)
                                          Spacer(modifier = Modifier.height(screenHeight * 0.01f))
                                          selectedWorker.addOnServices.forEach { service ->
                                            val name = service.name
                                            Text(
                                                text = "• $name",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = colorScheme.primary,
                                                modifier =
                                                    Modifier.padding(
                                                        bottom = screenHeight * 0.005f))
                                          }
                                        }
                                  }

                              Spacer(modifier = Modifier.height(screenHeight * 0.03f))

                              // Continue Button with Rate/HR
                              QuickFixButton(
                                  buttonText = "Continue",
                                  onClickAction = {
                                    quickFixViewModel.setSelectedWorkerProfile(selectedWorker)
                                    navigationActions.navigateTo(UserScreen.QUICKFIX_ONBOARDING)
                                  },
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
                                selectedWorker.tags.forEach { tag ->
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
                                        selectedWorker.rating.toFloat(),
                                        modifier =
                                            Modifier.height(screenHeight * 0.03f)
                                                .testTag("starsRow"))
                                  }
                              Spacer(modifier = Modifier.height(screenHeight * 0.01f))
                              Spacer(modifier = Modifier.height(screenHeight * 0.01f))
                              LazyRow(
                                  modifier =
                                      Modifier.fillMaxWidth()
                                          .padding(horizontal = screenWidth * 0.04f)
                                          .testTag("sliding_window_reviews_row")) {
                                    itemsIndexed(selectedWorker.reviews) { index, review ->
                                      var isExpanded by remember { mutableStateOf(false) }
                                      val displayText =
                                          if (isExpanded || review.review.length <= 100) {
                                            review.review
                                          } else {
                                            review.review.take(100) + "..."
                                          }

                                      Box(
                                          modifier =
                                              Modifier.padding(end = screenWidth * 0.02f)
                                                  .width(screenWidth * 0.6f)
                                                  .clip(RoundedCornerShape(25f))
                                                  .background(colorScheme.background)) {
                                            Column(
                                                modifier = Modifier.padding(screenWidth * 0.02f)) {
                                                  Text(
                                                      text = displayText,
                                                      style = MaterialTheme.typography.bodySmall,
                                                      color = colorScheme.onSurface)
                                                  if (review.review.length > 100) {
                                                    Text(
                                                        text =
                                                            if (isExpanded) "See less"
                                                            else "See more",
                                                        style =
                                                            MaterialTheme.typography.bodySmall.copy(
                                                                color = colorScheme.primary),
                                                        modifier =
                                                            Modifier.clickable {
                                                                  isExpanded = !isExpanded
                                                                }
                                                                .padding(
                                                                    top = screenHeight * 0.01f))
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
    }
  }
}

fun checkIfLoadingComplete(
    profiles: List<WorkerProfile>,
    profileImages: Map<String, Bitmap?>,
    bannerImages: Map<String, Bitmap?>,
    onComplete: () -> Unit
) {
  val allProfilesLoaded =
      profiles.all { profile ->
        profileImages[profile.uid] != null && bannerImages[profile.uid] != null
      }
  if (allProfilesLoaded) onComplete()
}
