package com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.search

import android.annotation.SuppressLint
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
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.arygm.quickfix.MainActivity
import com.arygm.quickfix.R
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.UserProfile
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.profile.dataFields.AddOnService
import com.arygm.quickfix.model.profile.dataFields.IncludedService
import com.arygm.quickfix.model.profile.dataFields.Review
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
import kotlin.math.roundToInt
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchWorkerResult(
    navigationActions: NavigationActions,
    searchViewModel: SearchViewModel,
    accountViewModel: AccountViewModel,
    userProfileViewModel: ProfileViewModel,
    preferencesViewModel: PreferencesViewModel,
    quickFixViewModel: QuickFixViewModel,
    geocoderWrapper: GeocoderWrapper = GeocoderWrapper(LocalContext.current),
    locationHelper: LocationHelper = LocationHelper(LocalContext.current, MainActivity())
) {
  fun getCityNameFromCoordinates(latitude: Double, longitude: Double): String? {
    val addresses = geocoderWrapper.getFromLocation(latitude, longitude, 1)
    return addresses?.firstOrNull()?.locality
        ?: addresses?.firstOrNull()?.subAdminArea
        ?: addresses?.firstOrNull()?.adminArea
  }
  var phoneLocation by remember {
    mutableStateOf(com.arygm.quickfix.model.locations.Location(0.0, 0.0, "Default"))
  }
  var baseLocation by remember { mutableStateOf(phoneLocation) }
  val context = LocalContext.current
  val locationHelper = LocationHelper(context, MainActivity())

  // State that manages all filters and their applied logic
  val filterState = rememberSearchFiltersState()

  val workerProfiles by searchViewModel.subCategoryWorkerProfiles.collectAsState()
  var filteredWorkerProfiles by remember { mutableStateOf(workerProfiles) }

  var showFilterButtons by remember { mutableStateOf(false) }
  var showAvailabilityBottomSheet by remember { mutableStateOf(false) }
  var showServicesBottomSheet by remember { mutableStateOf(false) }
  var showPriceRangeBottomSheet by remember { mutableStateOf(false) }
  var showLocationBottomSheet by remember { mutableStateOf(false) }
  var selectedLocationIndex by remember { mutableStateOf<Int?>(null) }

  var isWindowVisible by remember { mutableStateOf(false) }

  var bannerImage by remember { mutableIntStateOf(R.drawable.moroccan_flag) }
  var profilePicture by remember { mutableIntStateOf(R.drawable.placeholder_worker) }
  var initialSaved by remember { mutableStateOf(false) }
  var workerCategory by remember { mutableStateOf("Exterior Painter") }
  var workerAddress by remember { mutableStateOf("Ecublens, VD") }
  var description by remember { mutableStateOf("Worker description goes here.") }
  var includedServices by remember { mutableStateOf(listOf<String>()) }
  var addonServices by remember { mutableStateOf(listOf<String>()) }
  var workerRating by remember { mutableDoubleStateOf(4.5) }
  var tags by remember { mutableStateOf(listOf<String>()) }
  var reviews by remember { mutableStateOf(listOf<String>()) }

  var userProfile = UserProfile(locations = emptyList(), announcements = emptyList(), uid = "0")
  var uid by remember { mutableStateOf("Loading...") }

  val searchQuery by searchViewModel.searchQuery.collectAsState()
  val searchSubcategory by searchViewModel.searchSubcategory.collectAsState()

  // Fetch user and set base location
  LaunchedEffect(Unit) {
    uid = loadUserId(preferencesViewModel)
    userProfileViewModel.fetchUserProfile(uid) { profile ->
      if (profile is UserProfile) {
        userProfile = profile
      } else {
        Log.e("SearchWorkerResult", "Fetched a worker profile from a user profile repo.")
      }
    }
  }

  LaunchedEffect(Unit) {
    if (locationHelper.checkPermissions()) {
      locationHelper.getCurrentLocation { location ->
        if (location != null) {
          val userLoc = Location(location.latitude, location.longitude, "Phone Location")
          filterState.phoneLocation = userLoc
          filterState.baseLocation = userLoc
        } else {
          Toast.makeText(context, "Unable to fetch location", Toast.LENGTH_SHORT).show()
        }
      }
    } else {
      Toast.makeText(context, "Enable Location In Settings", Toast.LENGTH_SHORT).show()
    }
  }

  var selectedWorker by remember { mutableStateOf(WorkerProfile()) }
  var selectedCityName by remember { mutableStateOf<String?>(null) }
  var showFilterButtons by remember { mutableStateOf(false) }
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
  var emergencyFilterApplied by remember { mutableStateOf(false) }

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

    if (emergencyFilterApplied) {
      updatedProfiles = searchViewModel.emergencyFilter(updatedProfiles, baseLocation)
    }

    Log.d("Chill guy", updatedProfiles.size.toString())
    filteredWorkerProfiles = updatedProfiles
  }
    val listState = rememberLazyListState()
    fun updateFilteredProfiles() {
        filteredWorkerProfiles = filterState.reapplyFilters(workerProfiles, searchViewModel)
    }

    val listOfButtons =
      filterState.getFilterButtons(
          workerProfiles = workerProfiles,
          filteredProfiles = filteredWorkerProfiles,
          searchViewModel = searchViewModel,
          onProfilesUpdated = { updated -> filteredWorkerProfiles = updated },
          onShowAvailabilityBottomSheet = { showAvailabilityBottomSheet = true },
          onShowServicesBottomSheet = { showServicesBottomSheet = true },
          onShowPriceRangeBottomSheet = { showPriceRangeBottomSheet = true },
          onShowLocationBottomSheet = { showLocationBottomSheet = true },
      )

  // ==========================================================================//
  // ============ TODO: REMOVE NO-DATA WHEN BACKEND IS IMPLEMENTED ============//
  // ==========================================================================//

  val bannerImage = R.drawable.moroccan_flag
  val profilePicture = R.drawable.placeholder_worker

  // ==========================================================================//
  // ==========================================================================//
  // ==========================================================================//

  var isWindowVisible by remember { mutableStateOf(false) }
  var saved by remember { mutableStateOf(false) }

  // Wrap everything in a Box to allow overlay
  BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
    val screenHeight = maxHeight
    val screenWidth = maxWidth

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
                IconButton(onClick = {}) {
                  Icon(
                      imageVector = Icons.Default.Search,
                      contentDescription = "Search",
                      tint = colorScheme.onBackground)
                }
              },
              colors =
                  TopAppBarDefaults.centerAlignedTopAppBarColors(
                      containerColor = colorScheme.background),
          )
        }) { paddingValues ->
          Column(
              modifier = Modifier.fillMaxWidth().padding(paddingValues),
              horizontalAlignment = Alignment.CenterHorizontally) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
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
                  FilterRow(
                      showFilterButtons = showFilterButtons,
                      toggleFilterButtons = { showFilterButtons = !showFilterButtons },
                      listOfButtons = listOfButtons,
                      modifier = Modifier.padding(bottom = screenHeight * 0.01f),
                      screenWidth = screenWidth,
                      screenHeight = screenHeight)
                }

                ProfileResults(
                    modifier = Modifier.testTag("worker_profiles_list"),
                    profiles = filteredWorkerProfiles,
                    listState = listState,
                    searchViewModel = searchViewModel,
                    accountViewModel = accountViewModel,
                    onBookClick = { selectedProfile ->
                      // Mock data for demonstration
                      val profile =
                          WorkerProfile(
                              rating = 4.8,
                              fieldOfWork = "Exterior Painter",
                              description = "Worker description goes here.",
                              location = Location(12.0, 12.0, "Ecublens, VD"),
                              quickFixes = listOf("Painting", "Gardening"),
                              includedServices =
                                  listOf(
                                      IncludedService("Painting"),
                                      IncludedService("Gardening"),
                                  ),
                              addOnServices =
                                  listOf(
                                      AddOnService("Furniture Assembly"),
                                      AddOnService("Window Cleaning"),
                                  ),
                              reviews =
                                  ArrayDeque(
                                      listOf(
                                          Review("Bob", "nice work", 4.0),
                                          Review("Alice", "bad work", 3.5),
                                      )),
                              profilePicture = "placeholder_worker",
                              price = 130.0,
                              displayName = "John Doe",
                              unavailability_list = emptyList(),
                              workingHours = Pair(LocalTime.now(), LocalTime.now()),
                              uid = "1234",
                              tags = listOf("Painter", "Gardener"),
                          )

                      bannerImage = R.drawable.moroccan_flag
                      profilePicture = R.drawable.placeholder_worker
                      initialSaved = false
                      workerCategory = profile.fieldOfWork
                      workerAddress = profile.location?.name ?: "Unknown"
                      description = profile.description
                      includedServices = profile.includedServices.map { it.name }
                      addonServices = profile.addOnServices.map { it.name }
                      workerRating = profile.rating
                      tags = profile.tags
                      reviews = profile.reviews.map { it.review }

                      isWindowVisible = true
                    })
              }
        }

    // Bottom sheets for filters
    QuickFixAvailabilityBottomSheet(
        showAvailabilityBottomSheet,
        onDismissRequest = { showAvailabilityBottomSheet = false },
        onOkClick = { days, hour, minute ->
          filterState.selectedDays = days
          filterState.selectedHour = hour
          filterState.selectedMinute = minute
          filterState.availabilityFilterApplied = true
          updateFilteredProfiles()
        },
        onClearClick = {
          filterState.availabilityFilterApplied = false
          filterState.selectedDays = emptyList()
          filterState.selectedHour = 0
          filterState.selectedMinute = 0
          updateFilteredProfiles()
        },
        clearEnabled = filterState.availabilityFilterApplied)

    searchSubcategory?.let {
      ChooseServiceTypeSheet(
          showServicesBottomSheet,
          it.tags,
          selectedServices = filterState.selectedServices,
          onApplyClick = { services ->
            filterState.selectedServices = services
            filterState.servicesFilterApplied = true
            updateFilteredProfiles()
          },
          onDismissRequest = { showServicesBottomSheet = false },
          onClearClick = {
            filterState.selectedServices = emptyList()
            filterState.servicesFilterApplied = false
            updateFilteredProfiles()
          },
          clearEnabled = filterState.servicesFilterApplied)
    }

    QuickFixPriceRangeBottomSheet(
        showPriceRangeBottomSheet,
        onApplyClick = { start, end ->
          filterState.selectedPriceStart = start
          filterState.selectedPriceEnd = end
          filterState.priceFilterApplied = true
          updateFilteredProfiles()
        },
        onDismissRequest = { showPriceRangeBottomSheet = false },
        onClearClick = {
          filterState.selectedPriceStart = 0
          filterState.selectedPriceEnd = 0
          filterState.priceFilterApplied = false
          updateFilteredProfiles()
        },
        clearEnabled = filterState.priceFilterApplied)

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
    QuickFixSlidingWindowWorker(
        isVisible = isWindowVisible,
        onDismiss = { isWindowVisible = false },
        bannerImage = bannerImage,
        profilePicture = profilePicture,
        initialSaved = initialSaved,
        workerCategory = workerCategory,
        workerAddress = workerAddress,
        description = description,
        includedServices = includedServices,
        addonServices = addonServices,
        workerRating = workerRating,
        tags = tags,
        reviews = reviews,
        screenHeight = maxHeight,
        screenWidth = maxWidth,
        onContinueClick = {
                  quickFixViewModel.setSelectedWorkerProfile(selectedWorker)
                  navigationActions.navigateTo(UserScreen.QUICKFIX_ONBOARDING)})
  }
}
