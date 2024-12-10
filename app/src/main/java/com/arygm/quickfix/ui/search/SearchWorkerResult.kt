package com.arygm.quickfix.ui.search

import QuickFixSlidingWindowWorker
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arygm.quickfix.MainActivity
import com.arygm.quickfix.R
import com.arygm.quickfix.model.account.AccountViewModel
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
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.elements.QuickFixLocationFilterBottomSheet
import com.arygm.quickfix.ui.elements.QuickFixPriceRangeBottomSheet
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.poppinsTypography
import com.arygm.quickfix.utils.LocationHelper
import com.arygm.quickfix.utils.loadUserId
import java.time.LocalDate
import java.time.LocalTime

data class SearchFilterButtons(
    val onClick: () -> Unit,
    val text: String,
    val leadingIcon: ImageVector? = null,
    val trailingIcon: ImageVector? = null,
    val applied: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchWorkerResult(
    navigationActions: NavigationActions,
    searchViewModel: SearchViewModel,
    accountViewModel: AccountViewModel,
    userProfileViewModel: ProfileViewModel,
    preferencesViewModel: PreferencesViewModel
) {
  val locationHelper = LocationHelper(LocalContext.current, MainActivity())
  var phoneLocation by remember {
    mutableStateOf(com.arygm.quickfix.model.locations.Location(0.0, 0.0, "Default"))
  }
  var baseLocation by remember { mutableStateOf(phoneLocation) }
  val context = LocalContext.current
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
  }

  var showFilterButtons by remember { mutableStateOf(false) }
  var showAvailabilityBottomSheet by remember { mutableStateOf(false) }
  var showServicesBottomSheet by remember { mutableStateOf(false) }
  var showPriceRangeBottomSheet by remember { mutableStateOf(false) }
  var showLocationBottomSheet by remember { mutableStateOf(false) }
  val workerProfiles by searchViewModel.subCategoryWorkerProfiles.collectAsState()
  var filteredWorkerProfiles by remember { mutableStateOf(workerProfiles) }
  var isWindowVisible by remember { mutableStateOf(false) }
  var saved by remember { mutableStateOf(false) }

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

  fun reapplyFilters() {
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

  val searchQuery by searchViewModel.searchQuery.collectAsState()
  val searchSubcategory by searchViewModel.searchSubcategory.collectAsState()

  var userProfile = UserProfile(locations = emptyList(), announcements = emptyList(), uid = "0")
  var uid by remember { mutableStateOf("Loading...") }

  LaunchedEffect(Unit) { uid = loadUserId(preferencesViewModel) }
  userProfileViewModel.fetchUserProfile(uid) { profile ->
    if (profile is UserProfile) {
      userProfile = profile
    } else {
      Log.e("SearchWorkerResult", "Fetched a worker profile from a user profile repo.")
    }
  }

  // Wrap everything in a Box to allow overlay
  val listState = rememberLazyListState()

  // Variables for Sliding Window
  var bannerImage by remember { mutableStateOf(R.drawable.moroccan_flag) }
  var profilePicture by remember { mutableStateOf(R.drawable.placeholder_worker) }
  var initialSaved by remember { mutableStateOf(false) }
  var workerCategory by remember { mutableStateOf("Exterior Painter") }
  var workerAddress by remember { mutableStateOf("Ecublens, VD") }
  var description by remember { mutableStateOf("Worker description goes here.") }
  var includedServices by remember { mutableStateOf(listOf<String>()) }
  var addonServices by remember { mutableStateOf(listOf<String>()) }
  var workerRating by remember { mutableStateOf(4.5) }
  var tags by remember { mutableStateOf(listOf<String>()) }
  var reviews by remember { mutableStateOf(listOf<String>()) }

  BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
    val screenHeight = maxHeight
    val screenWidth = maxWidth
    Log.d("Screen Dimensions", "Height: $screenHeight, Width: $screenWidth")

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
                      containerColor = colorScheme.background),
          )
        }) { paddingValues ->
          // Main content inside the Scaffold
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
                            .padding(horizontal = screenWidth * 0.02f)
                            .wrapContentHeight()
                            .testTag("filter_buttons_row"),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                  // Tune Icon - fixed, non-scrollable
                  IconButton(
                      onClick = { showFilterButtons = !showFilterButtons },
                      modifier =
                          Modifier.padding(bottom = screenHeight * 0.01f).testTag("tuneButton"),
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
                                    Modifier.testTag("filter_button_${listOfButtons[index].text}"))
                            Spacer(modifier = Modifier.width(screenHeight * 0.01f))
                          }
                        }
                  }
                }

                ProfileResults(
                    modifier = Modifier.testTag("worker_profiles_list"),
                    profiles = filteredWorkerProfiles,
                    listState = listState,
                    searchViewModel = searchViewModel,
                    accountViewModel = accountViewModel,
                    heightRatio = 1f,
                    onBookClick = { selectedProfile ->

                      // TODO when linking the backend remove placeHolder data
                      val profile =
                          WorkerProfile(
                              rating = 4.8,
                              fieldOfWork = "Exterior Painter",
                              description = "Worker description goes here.",
                              location =
                                  com.arygm.quickfix.model.locations.Location(
                                      12.0, 12.0, "Ecublens, VD"),
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

                      // Update variables for Sliding Window
                      bannerImage = R.drawable.moroccan_flag // Replace with actual data
                      profilePicture = R.drawable.placeholder_worker // Replace with actual data
                      initialSaved = false // Replace with actual data
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

    QuickFixAvailabilityBottomSheet(
        showAvailabilityBottomSheet,
        onDismissRequest = { showAvailabilityBottomSheet = false },
        onOkClick = { days, hour, minute ->
          selectedDays = days
          selectedHour = hour
          selectedMinute = minute
          filteredWorkerProfiles =
              searchViewModel.filterWorkersByAvailability(
                  filteredWorkerProfiles, days, hour, minute)
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
            filteredWorkerProfiles =
                searchViewModel.filterWorkersByServices(filteredWorkerProfiles, selectedServices)
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
          filteredWorkerProfiles =
              searchViewModel.filterWorkersByPriceRange(filteredWorkerProfiles, start, end)
          priceFilterApplied = true
        },
        onDismissRequest = { showPriceRangeBottomSheet = false },
        onClearClick = {
          selectedPriceStart = 0
          selectedPriceEnd = 0
          priceFilterApplied = false
          reapplyFilters()
        },
        clearEnabled = priceFilterApplied)

    QuickFixLocationFilterBottomSheet(
        showLocationBottomSheet,
        userProfile = userProfile,
        phoneLocation = phoneLocation,
        onApplyClick = { location, max ->
          selectedLocation = location
          if (location == com.arygm.quickfix.model.locations.Location(0.0, 0.0, "Default")) {
            Toast.makeText(context, "Enable Location In Settings", Toast.LENGTH_SHORT).show()
          }
          baseLocation = location
          maxDistance = max
          filteredWorkerProfiles =
              searchViewModel.filterWorkersByDistance(filteredWorkerProfiles, location, max)
          locationFilterApplied = true
        },
        onDismissRequest = { showLocationBottomSheet = false },
        onClearClick = {
          baseLocation = phoneLocation
          selectedLocation = com.arygm.quickfix.model.locations.Location()
          maxDistance = 0
          locationFilterApplied = false
          reapplyFilters()
        },
        clearEnabled = locationFilterApplied)

    QuickFixSlidingWindowWorker(
        isVisible = isWindowVisible,
        onDismiss = { isWindowVisible = false },
        bannerImage = bannerImage,
        profilePicture = profilePicture,
        initialSaved = saved,
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
