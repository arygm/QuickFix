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
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.category.CategoryViewModel
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.profile.UserProfile
import com.arygm.quickfix.model.profile.ProfileViewModel
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

@Composable
fun SearchOnBoarding(
    navigationActions: NavigationActions,
    navigationActionsRoot: NavigationActions,
    searchViewModel: SearchViewModel,
    accountViewModel: AccountViewModel,
    categoryViewModel: CategoryViewModel,
    onProfileClick: (WorkerProfile, String) -> Unit,
    workerViewModel: ProfileViewModel
) {
  val (uiState, setUiState) = remember { mutableStateOf(SearchUIState()) }
  val workerProfiles by searchViewModel.workerProfilesSuggestions.collectAsState()
  var filteredWorkerProfiles by remember { mutableStateOf(workerProfiles) }
  val context = LocalContext.current
  val searchSubcategory by searchViewModel.searchSubcategory.collectAsState()
  var locationFilterApplied by remember { mutableStateOf(false) }
  var userProfile by remember { mutableStateOf<UserProfile?>(null) }
  var lastAppliedMaxDist by remember { mutableIntStateOf(200) }
  val focusManager = LocalFocusManager.current
  var selectedLocation by remember { mutableStateOf(Location()) }
  val categories = categoryViewModel.categories.collectAsState().value
  val itemCategories = remember { categories }
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

  fun updateFilteredProfiles() {
    filteredWorkerProfiles = filterState.reapplyFilters(workerProfiles, searchViewModel)
  }

  // Build filter buttons
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

    val profileImagesMap by remember { mutableStateOf(mutableMapOf<String, Bitmap?>()) }
    val bannerImagesMap by remember { mutableStateOf(mutableMapOf<String, Bitmap?>()) }
    var loading by remember { mutableStateOf(true) }
    // Tracks if data is loading
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
  BoxWithConstraints {
    val widthRatio = maxWidth.value / 411f
    val heightRatio = maxHeight.value / 860f
    val sizeRatio = minOf(widthRatio, heightRatio)
    val screenHeight = maxHeight
    val screenWidth = maxWidth

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
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 0.dp * heightRatio),
                        horizontalArrangement = Arrangement.Center
                    ) {
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
                                searchViewModel.searchEngine(it)
                            },
                            shape = CircleShape,
                            textStyle = poppinsTypography.bodyMedium,
                            textColor = colorScheme.onBackground,
                            placeHolderColor = colorScheme.onBackground,
                            leadIconColor = colorScheme.onBackground,
                            widthField = 300.dp * widthRatio,
                            heightField = 40.dp * heightRatio,
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
                                        top = screenHeight * 0.02f,
                                        bottom = screenHeight * 0.01f
                                    )
                                    .padding(horizontal = screenWidth * 0.02f),
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
                                    screenHeight = screenHeight
                                )
                            }
                        }
                    }
                    ProfileResults(
                        profiles = filteredWorkerProfiles,
                        searchViewModel = searchViewModel,
                        accountViewModel = accountViewModel,
                        listState = listState,
                        onBookClick = { selectedProfile, loc ->
                            onProfileClick(
                                selectedProfile,
                                loc
                            )
                        }, workerViewModel = workerViewModel
                    )
                }
            }},
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
    }
  }
}