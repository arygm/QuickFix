package com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.search

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.arygm.quickfix.MainActivity
import com.arygm.quickfix.R
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.UserProfile
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.profile.dataFields.AddOnService
import com.arygm.quickfix.model.profile.dataFields.IncludedService
import com.arygm.quickfix.model.profile.dataFields.Review
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.ui.elements.ChooseServiceTypeSheet
import com.arygm.quickfix.ui.elements.QuickFixAvailabilityBottomSheet
import com.arygm.quickfix.ui.elements.QuickFixLocationFilterBottomSheet
import com.arygm.quickfix.ui.elements.QuickFixPriceRangeBottomSheet
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.poppinsTypography
import com.arygm.quickfix.utils.LocationHelper
import com.arygm.quickfix.utils.loadUserId
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchWorkerResult(
    navigationActions: NavigationActions,
    searchViewModel: SearchViewModel,
    accountViewModel: AccountViewModel,
    userProfileViewModel: ProfileViewModel,
    preferencesViewModel: PreferencesViewModel
) {
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

  val listState = rememberLazyListState()

  // Update the displayed profiles after filters have changed
  fun updateFilteredProfiles() {
    filteredWorkerProfiles = filterState.reapplyFilters(workerProfiles, searchViewModel)
  }

  // Build the list of filter buttons through the filter state
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
                          text = searchQuery,
                          style = poppinsTypography.labelMedium,
                          fontSize = 24.sp,
                          fontWeight = FontWeight.SemiBold,
                          textAlign = TextAlign.Center,
                      )
                      Text(
                          text = "This is a sample description for the $searchQuery result",
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
                            .padding(horizontal = screenWidth * 0.02f),
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

    QuickFixLocationFilterBottomSheet(
        showLocationBottomSheet,
        userProfile = userProfile,
        phoneLocation = filterState.phoneLocation,
        selectedLocationIndex = selectedLocationIndex,
        onApplyClick = { location, max ->
          selectedLocationIndex = userProfile.locations.indexOf(location) + 1
          filterState.selectedLocation = location
          filterState.baseLocation = location
          filterState.maxDistance = max
          filterState.locationFilterApplied = true
          updateFilteredProfiles()
        },
        onDismissRequest = { showLocationBottomSheet = false },
        onClearClick = {
          filterState.baseLocation = filterState.phoneLocation
          filterState.selectedLocation = Location()
          filterState.maxDistance = 0
          filterState.locationFilterApplied = false
          updateFilteredProfiles()
          selectedLocationIndex = null
        },
        clearEnabled = filterState.locationFilterApplied)

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
        onContinueClick = {})
  }
}
