package com.arygm.quickfix.ui.search

import QuickFixSlidingWindowWorker
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import com.arygm.quickfix.R
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.category.CategoryViewModel
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
import com.arygm.quickfix.ui.userModeUI.navigation.UserTopLevelDestinations

@Composable
fun SearchOnBoarding(
    onSearch: () -> Unit,
    onSearchEmpty: () -> Unit,
    navigationActions: NavigationActions,
    navigationActionsRoot: NavigationActions,
    searchViewModel: SearchViewModel,
    accountViewModel: AccountViewModel,
    categoryViewModel: CategoryViewModel,
    onProfileClick: (WorkerProfile) -> Unit
) {
  val context = LocalContext.current
  val workerProfiles by searchViewModel.subCategoryWorkerProfiles.collectAsState()
  var userProfile = UserProfile(locations = emptyList(), announcements = emptyList(), uid = "0")
  val focusManager = LocalFocusManager.current
  val categories = categoryViewModel.categories.collectAsState().value
  Log.d("SearchOnBoarding", "Categories: $categories")
  val itemCategories = remember { categories }
  val expandedStates = remember {
    mutableStateListOf(*BooleanArray(itemCategories.size) { false }.toTypedArray())
  }
  val listState = rememberLazyListState()

  var searchQuery by remember { mutableStateOf("") }
  val searchSubcategory by searchViewModel.searchSubcategory.collectAsState()

  // Filtering logic
  val filterState = rememberSearchFiltersState()
  var filteredWorkerProfiles by remember { mutableStateOf(workerProfiles) }

  fun updateFilteredProfiles() {
    filteredWorkerProfiles = filterState.reapplyFilters(workerProfiles, searchViewModel)
  }

  var showFilterButtons by remember { mutableStateOf(false) }
  var showAvailabilityBottomSheet by remember { mutableStateOf(false) }
  var showServicesBottomSheet by remember { mutableStateOf(false) }
  var showPriceRangeBottomSheet by remember { mutableStateOf(false) }
  var showLocationBottomSheet by remember { mutableStateOf(false) }
  // Build filter buttons
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

  var isWindowVisible by remember { mutableStateOf(false) }

  // Variables for WorkerSlidingWindowContent
  var bannerImage by remember { mutableIntStateOf(R.drawable.moroccan_flag) }
  var profilePicture by remember { mutableIntStateOf(R.drawable.placeholder_worker) }
  var initialSaved by remember { mutableStateOf(false) }
  var workerCategory by remember { mutableStateOf("Exterior Painter") }
  var workerAddress by remember { mutableStateOf("Ecublens, VD") }
  var description by remember { mutableStateOf("Worker description goes here.") }
  var includedServices by remember { mutableStateOf(listOf("Service 1", "Service 2")) }
  var addonServices by remember { mutableStateOf(listOf("Add-on 1", "Add-on 2")) }
  var workerRating by remember { mutableDoubleStateOf(4.5) }
  var tags by remember { mutableStateOf(listOf("Tag1", "Tag2")) }
  var reviews by remember { mutableStateOf(listOf("Review 1", "Review 2")) }

  BoxWithConstraints {
    val widthRatio = maxWidth.value / 411f
    val heightRatio = maxHeight.value / 860f
    val sizeRatio = minOf(widthRatio, heightRatio)
    val screenHeight = maxHeight
    val screenWidth = maxWidth

    Scaffold(
        containerColor = colorScheme.background,
        content = { padding ->
          Column(
              modifier =
                  Modifier.fillMaxWidth()
                      .padding(padding)
                      .padding(top = 40.dp * heightRatio)
                      .padding(horizontal = 10.dp * widthRatio),
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
                            )
                          },
                          placeHolderText = "Find your perfect fix with QuickFix",
                          value = searchQuery,
                          onValueChange = {
                            searchQuery = it
                            searchViewModel.updateSearchQuery(it)
                            if (it.isEmpty()) {
                              onSearchEmpty()
                              // When search is empty, we can reset filteredWorkerProfiles to
                              // original
                              filteredWorkerProfiles = workerProfiles
                            } else {
                              onSearch()
                              // If needed, reapply filters here if filters are set
                              updateFilteredProfiles()
                            }
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
                                .padding(top = screenHeight * 0.02f, bottom = screenHeight * 0.01f)
                                .padding(horizontal = screenWidth * 0.02f)
                                .testTag("filter_buttons_row"),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                      FilterRow(
                          showFilterButtons = showFilterButtons,
                          toggleFilterButtons = { showFilterButtons = !showFilterButtons },
                          listOfButtons = listOfButtons,
                          modifier = Modifier.padding(bottom = screenHeight * 0.01f))
                    }

                    ProfileResults(
                        profiles = filteredWorkerProfiles,
                        searchViewModel = searchViewModel,
                        accountViewModel = accountViewModel,
                        listState = listState,
                        onBookClick = { selectedProfile -> onProfileClick(selectedProfile) })
                  }
                }
              }
        },
        modifier =
            Modifier.pointerInput(Unit) {
              detectTapGestures(onTap = { focusManager.clearFocus() })
            })

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
        onApplyClick = { location, max ->
          filterState.selectedLocation = location
          if (location == com.arygm.quickfix.model.locations.Location(0.0, 0.0, "Default")) {
            Toast.makeText(context, "Enable Location In Settings", Toast.LENGTH_SHORT).show()
          }
          filterState.baseLocation = location
          filterState.maxDistance = max
          filterState.locationFilterApplied = true
          updateFilteredProfiles()
        },
        onDismissRequest = { showLocationBottomSheet = false },
        onClearClick = {
          filterState.baseLocation = filterState.phoneLocation
          filterState.selectedLocation = com.arygm.quickfix.model.locations.Location()
          filterState.maxDistance = 0
          filterState.locationFilterApplied = false
          updateFilteredProfiles()
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
        screenHeight = screenHeight,
        screenWidth = screenWidth,
        onContinueClick = { /* Handle continue */})
  }
}
