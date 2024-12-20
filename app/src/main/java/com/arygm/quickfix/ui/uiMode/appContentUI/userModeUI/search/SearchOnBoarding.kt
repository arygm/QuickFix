package com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.search

import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.arygm.quickfix.MainActivity
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.category.CategoryViewModel
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.UserProfile
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.ui.elements.ChooseServiceTypeSheet
import com.arygm.quickfix.ui.elements.QuickFixAvailabilityBottomSheet
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.elements.QuickFixLocationFilterBottomSheet
import com.arygm.quickfix.ui.elements.QuickFixPriceRangeBottomSheet
import com.arygm.quickfix.ui.elements.QuickFixTextFieldCustom
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.poppinsTypography
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.navigation.UserTopLevelDestinations
import com.arygm.quickfix.utils.LocationHelper
import com.arygm.quickfix.utils.loadUserId

@Composable
fun SearchOnBoarding(
    navigationActions: NavigationActions,
    navigationActionsRoot: NavigationActions,
    searchViewModel: SearchViewModel,
    accountViewModel: AccountViewModel,
    preferencesViewModel: PreferencesViewModel,
    userProfileViewModel: ProfileViewModel,
    categoryViewModel: CategoryViewModel,
    onBookClick: (WorkerProfile, String, Bitmap, Bitmap) -> Unit,
    workerViewModel: ProfileViewModel,
    locationHelper: LocationHelper = LocationHelper(LocalContext.current, MainActivity())
) {
  val (uiState, setUiState) = remember { mutableStateOf(SearchUIState()) }
  val context = LocalContext.current
  val searchSubcategory by searchViewModel.searchSubcategory.collectAsState()
  var locationFilterApplied by remember { mutableStateOf(false) }
  var userProfile by remember { mutableStateOf<UserProfile?>(null) }
  var lastAppliedMaxDist by remember { mutableIntStateOf(200) }
  val profiles by workerViewModel.profiles.collectAsState()
  var searchedWorkers by remember { mutableStateOf(profiles as List<WorkerProfile>) }
  val focusManager = LocalFocusManager.current
  var selectedLocation by remember { mutableStateOf(Location()) }
  val categories = categoryViewModel.categories.collectAsState().value
  val itemCategories = remember { categories }
  var uid by remember { mutableStateOf("Loading...") }

  val expandedStates = remember {
    mutableStateListOf(*BooleanArray(itemCategories.size) { false }.toTypedArray())
  }
  val listState = rememberLazyListState()
  var selectedLocationIndex by remember { mutableStateOf<Int?>(null) }

  var searchQuery by remember { mutableStateOf("") }

  // Filtering logic
  val filterState = rememberSearchFiltersState()
  var baseLocation by remember { mutableStateOf(filterState.phoneLocation) }
  var maxDistance by remember { mutableIntStateOf(0) }

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
    uid = loadUserId(preferencesViewModel)
    userProfileViewModel.fetchUserProfile(uid) { profile -> userProfile = profile as UserProfile }
  }
  fun updateFilteredProfiles() {
    searchedWorkers = filterState.reapplyFilters(profiles as List<WorkerProfile>, searchViewModel)
  }

  // Build filter buttons
  val listOfButtons =
      filterState.getFilterButtons(
          workerProfiles = profiles as List<WorkerProfile>,
          filteredProfiles = searchedWorkers,
          searchViewModel = searchViewModel,
          onProfilesUpdated = { updated -> searchedWorkers = updated },
          onShowAvailabilityBottomSheet = {
            setUiState(uiState.copy(showAvailabilityBottomSheet = true))
          },
          onShowServicesBottomSheet = { setUiState(uiState.copy(showServicesBottomSheet = true)) },
          onShowPriceRangeBottomSheet = {
            setUiState(uiState.copy(showPriceRangeBottomSheet = true))
          },
          onShowLocationBottomSheet = { setUiState(uiState.copy(showLocationBottomSheet = true)) })

  val profileImagesMap by remember { mutableStateOf(mutableMapOf<String, Bitmap?>()) }
  val bannerImagesMap by remember { mutableStateOf(mutableMapOf<String, Bitmap?>()) }
  var loading by remember { mutableStateOf(true) }
  // Tracks if data is loading
  LaunchedEffect(profiles) {
    if (profiles.isNotEmpty()) {
      searchedWorkers.forEach { profile ->
        // Fetch profile images
        workerViewModel.fetchProfileImageAsBitmap(
            profile.uid,
            onSuccess = { bitmap ->
              profileImagesMap[profile.uid] = bitmap
              checkIfLoadingComplete(
                  profiles as List<WorkerProfile>, profileImagesMap, bannerImagesMap) {
                    loading = false
                  }
            },
            onFailure = { Log.e("ProfileResults", "Failed to fetch profile image") })

        // Fetch banner images
        workerViewModel.fetchBannerImageAsBitmap(
            profile.uid,
            onSuccess = { bitmap ->
              bannerImagesMap[profile.uid] = bitmap
              checkIfLoadingComplete(
                  profiles as List<WorkerProfile>, profileImagesMap, bannerImagesMap) {
                    loading = false
                  }
            },
            onFailure = { Log.e("ProfileResults", "Failed to fetch banner image") })
      }
    } else {
      loading = false // No profiles to load
    }
  }
  BoxWithConstraints {
    val widthRatio = maxWidth.value / 411f
    val heightRatio = maxHeight.value / 860f
    val sizeRatio = minOf(widthRatio, heightRatio)
    val screenHeight = maxHeight.value
    val screenWidth = maxWidth.value

    Scaffold(
        containerColor = colorScheme.background,
        content = { padding ->
          if (loading) {
            // Display a loader
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
              CircularProgressIndicator(
                  color = colorScheme.primary, modifier = Modifier.size(64.dp))
            }
          } else {
            Column(
                modifier =
                    Modifier.fillMaxWidth().padding(padding).padding(top = 40.dp * heightRatio),
                horizontalAlignment = Alignment.CenterHorizontally) {
                  Row(
                      modifier = Modifier.fillMaxWidth().padding(bottom = 0.dp * heightRatio),
                      horizontalArrangement = Arrangement.Center) {
                        QuickFixTextFieldCustom(
                            modifier = Modifier.testTag("searchContent"),
                            showLeadingIcon = { true },
                            leadingIcon = Icons.Outlined.Search,
                            showTrailingIcon = { searchQuery.isNotEmpty() },
                            trailingIcon = {
                              Icon(
                                  imageVector = Icons.Filled.Clear,
                                  contentDescription = "Clear search query",
                                  tint = colorScheme.onBackground,
                                  modifier = Modifier.testTag("clearSearchIcon"),
                              )
                            },
                            placeHolderText = "Find your perfect fix with QuickFix",
                            value = searchQuery,
                            onValueChange = {
                              searchQuery = it
                              searchViewModel.searchEngine(it, profiles as List<WorkerProfile>)
                            },
                            shape = CircleShape,
                            textStyle = poppinsTypography.bodyMedium,
                            textColor = colorScheme.onBackground,
                            placeHolderColor = colorScheme.onBackground,
                            leadIconColor = colorScheme.onBackground,
                            widthField = (screenWidth * 0.8).dp,
                            heightField = (screenHeight * 0.045).dp,
                            moveContentHorizontal = 10.dp * widthRatio,
                            moveContentBottom = 0.dp,
                            moveContentTop = 0.dp,
                            sizeIconGroup = 30.dp * sizeRatio,
                            spaceBetweenLeadIconText = 0.dp,
                            onClick = true,
                        )
                        Spacer(modifier = Modifier.width(10.dp * widthRatio))
                        QuickFixButton(
                            buttonText = "Cancel",
                            textColor = colorScheme.onBackground,
                            buttonColor = colorScheme.background,
                            buttonOpacity = 1f,
                            textStyle = poppinsTypography.labelSmall,
                            onClickAction = {
                              navigationActionsRoot.navigateTo(UserTopLevelDestinations.HOME)
                            },
                            contentPadding = PaddingValues(0.dp),
                        )
                      }
                  if (searchQuery.isEmpty()) {
                    // Show Categories
                    CategoryContent(
                        navigationActions = navigationActions,
                        searchViewModel = searchViewModel,
                        listState = listState,
                        expandedStates = expandedStates,
                        itemCategories = itemCategories,
                        widthRatio = widthRatio,
                        heightRatio = heightRatio,
                    )
                  } else {
                    // Show Profiles
                    // Insert filter buttons here (only when searchQuery is not empty)
                    Column {
                      Row(
                          modifier =
                              Modifier.fillMaxWidth()
                                  .padding(
                                      top = screenHeight.dp * 0.02f,
                                      bottom = screenHeight.dp * 0.01f)
                                  .padding(horizontal = screenWidth.dp * 0.02f),
                          verticalAlignment = Alignment.CenterVertically,
                      ) {
                        FilterRow(
                            showFilterButtons = uiState.showFilterButtons,
                            toggleFilterButtons = {
                              setUiState(
                                  uiState.copy(showFilterButtons = !uiState.showFilterButtons))
                            },
                            listOfButtons = listOfButtons,
                            modifier = Modifier.padding(bottom = screenHeight.dp * 0.01f),
                            screenWidth = screenWidth.dp,
                            screenHeight = screenHeight.dp)
                      }
                    }
                  }
                  ProfileResults(
                      profiles = searchedWorkers,
                      searchViewModel = searchViewModel,
                      accountViewModel = accountViewModel,
                      onBookClick = { selectedProfile, loc, profile, banner ->
                        onBookClick(selectedProfile, loc, profile, banner)
                      },
                      profileImagesMap = profileImagesMap,
                      bannerImagesMap = bannerImagesMap,
                      baseLocation = baseLocation,
                      screenHeight = screenHeight.dp)
                }
          }
        },
        modifier =
            Modifier.pointerInput(Unit) {
              detectTapGestures(onTap = { focusManager.clearFocus() })
            })

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

    userProfile?.let {
      QuickFixLocationFilterBottomSheet(
          uiState.showLocationBottomSheet,
          profile = it,
          phoneLocation = filterState.phoneLocation,
          selectedLocationIndex = selectedLocationIndex,
          onApplyClick = { location, max ->
            selectedLocation = location
            lastAppliedMaxDist = max
            baseLocation = location
            maxDistance = max
            selectedLocationIndex = it.locations.indexOf(location) + 1

            if (location == Location(0.0, 0.0, "Default")) {
              Toast.makeText(context, "Enable Location In Settings", Toast.LENGTH_SHORT).show()
            }
            if (locationFilterApplied) {
              updateFilteredProfiles()
            } else {
              searchedWorkers =
                  searchViewModel.filterWorkersByDistance(searchedWorkers, location, max)
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
    }
  }
}
