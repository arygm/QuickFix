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
import androidx.compose.foundation.layout.wrapContentHeight
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
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.UserProfile
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.quickfix.QuickFixViewModel
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.ui.elements.ChooseServiceTypeSheet
import com.arygm.quickfix.ui.elements.QuickFixAvailabilityBottomSheet
import com.arygm.quickfix.ui.elements.QuickFixLocationFilterBottomSheet
import com.arygm.quickfix.ui.elements.QuickFixPriceRangeBottomSheet
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.poppinsTypography
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.navigation.UserScreen
import com.arygm.quickfix.utils.LocationHelper
import com.arygm.quickfix.utils.loadUserId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchWorkerResult(
    navigationActions: NavigationActions,
    searchViewModel: SearchViewModel,
    accountViewModel: AccountViewModel,
    userProfileViewModel: ProfileViewModel,
    quickFixViewModel: QuickFixViewModel,
    preferencesViewModel: PreferencesViewModel,
) {
  val (uiState, setUiState) = remember { mutableStateOf(SearchUIState()) }
  var isWindowVisible by remember { mutableStateOf(false) }
  val context = LocalContext.current
  val locationHelper = LocationHelper(context, MainActivity())
  var selectedWorkerProfile by remember { mutableStateOf(WorkerProfile()) }
  val filterState = rememberSearchFiltersState()
  var baseLocation by remember { mutableStateOf(filterState.phoneLocation) }
  var userProfile = UserProfile(locations = emptyList(), announcements = emptyList(), uid = "0")
  var uid by remember { mutableStateOf("Loading...") }
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
        } else {
          Toast.makeText(context, "Unable to fetch location", Toast.LENGTH_SHORT).show()
        }
      }
    } else {
      Toast.makeText(context, "Enable Location In Settings", Toast.LENGTH_SHORT).show()
    }
  }

  val workerProfiles by searchViewModel.subCategoryWorkerProfiles.collectAsState()
  var filteredWorkerProfiles by remember { mutableStateOf(workerProfiles) }
  val searchCategory by searchViewModel.searchCategory.collectAsState()

  var locationFilterApplied by remember { mutableStateOf(false) }
  var selectedLocation by remember { mutableStateOf(Location()) }
  var maxDistance by remember { mutableIntStateOf(0) }
  var selectedLocationIndex by remember { mutableStateOf<Int?>(null) }

  var lastAppliedMaxDist by remember { mutableIntStateOf(200) }

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
          onShowAvailabilityBottomSheet = {
            setUiState(uiState.copy(showAvailabilityBottomSheet = true))
          },
          onShowServicesBottomSheet = { setUiState(uiState.copy(showServicesBottomSheet = true)) },
          onShowPriceRangeBottomSheet = {
            setUiState(uiState.copy(showPriceRangeBottomSheet = true))
          },
          onShowLocationBottomSheet = { setUiState(uiState.copy(showLocationBottomSheet = true)) })
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
                      showFilterButtons = uiState.showFilterButtons,
                      toggleFilterButtons = {
                        setUiState(uiState.copy(showFilterButtons = !uiState.showFilterButtons))
                      },
                      listOfButtons = listOfButtons,
                      modifier = Modifier.padding(bottom = screenHeight * 0.01f),
                      screenWidth = screenWidth,
                      screenHeight = screenHeight)
                }
                ProfileResults(
                    profiles = filteredWorkerProfiles,
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    searchViewModel = searchViewModel,
                    accountViewModel = accountViewModel,
                    listState = listState,
                    onBookClick = { selectedProfile, _ ->
                      isWindowVisible = true
                      selectedWorkerProfile = selectedProfile
                    },
                )
              }
        }

    // Bottom sheets for filters
    QuickFixAvailabilityBottomSheet(
        uiState.showAvailabilityBottomSheet,
        onDismissRequest = { setUiState(uiState.copy(showAvailabilityBottomSheet = false)) },
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
          uiState.showServicesBottomSheet,
          it.tags,
          selectedServices = filterState.selectedServices,
          onApplyClick = { services ->
            filterState.selectedServices = services
            filterState.servicesFilterApplied = true
            updateFilteredProfiles()
          },
          onDismissRequest = { setUiState(uiState.copy(showServicesBottomSheet = false)) },
          onClearClick = {
            filterState.selectedServices = emptyList()
            filterState.servicesFilterApplied = false
            updateFilteredProfiles()
          },
          clearEnabled = filterState.servicesFilterApplied)
    }

    QuickFixPriceRangeBottomSheet(
        uiState.showPriceRangeBottomSheet,
        onApplyClick = { start, end ->
          filterState.selectedPriceStart = start
          filterState.selectedPriceEnd = end
          filterState.priceFilterApplied = true
          updateFilteredProfiles()
        },
        onDismissRequest = { setUiState(uiState.copy(showPriceRangeBottomSheet = false)) },
        onClearClick = {
          filterState.selectedPriceStart = 0
          filterState.selectedPriceEnd = 0
          filterState.priceFilterApplied = false
          updateFilteredProfiles()
        },
        clearEnabled = filterState.priceFilterApplied)

    QuickFixLocationFilterBottomSheet(
        uiState.showLocationBottomSheet,
        userProfile = userProfile,
        phoneLocation = filterState.phoneLocation,
        selectedLocationIndex = selectedLocationIndex,
        onApplyClick = { location, max ->
          selectedLocation = location
          lastAppliedMaxDist = max
          baseLocation = location
          maxDistance = max
          selectedLocationIndex = userProfile.locations.indexOf(location) + 1

          if (location == Location(0.0, 0.0, "Default")) {
            Toast.makeText(context, "Enable Location In Settings", Toast.LENGTH_SHORT).show()
          }
          if (locationFilterApplied) {
            updateFilteredProfiles()
          } else {
            filteredWorkerProfiles =
                searchViewModel.filterWorkersByDistance(filteredWorkerProfiles, location, max)
          }
          locationFilterApplied = true
        },
        onDismissRequest = { setUiState(uiState.copy(showLocationBottomSheet = false)) },
        onClearClick = {
          baseLocation = filterState.phoneLocation
          lastAppliedMaxDist = 200
          selectedLocation = Location()
          maxDistance = 0
          selectedLocationIndex = null
          locationFilterApplied = false
          updateFilteredProfiles()
        },
        clearEnabled = locationFilterApplied,
        end = lastAppliedMaxDist)
    QuickFixSlidingWindowWorker(
        isVisible = isWindowVisible,
        onDismiss = { isWindowVisible = false },
        screenHeight = maxHeight,
        screenWidth = maxWidth,
        onContinueClick = {
          quickFixViewModel.setSelectedWorkerProfile(selectedWorkerProfile)
          navigationActions.navigateTo(UserScreen.QUICKFIX_ONBOARDING)
        },
        workerProfile = selectedWorkerProfile,
    )
  }
}
