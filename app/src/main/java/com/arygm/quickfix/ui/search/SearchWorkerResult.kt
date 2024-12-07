package com.arygm.quickfix.ui.search

import QuickFixSlidingWindowWorker
import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.profile.dataFields.AddOnService
import com.arygm.quickfix.model.profile.dataFields.IncludedService
import com.arygm.quickfix.model.profile.dataFields.Review
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.ui.elements.ChooseServiceTypeSheet
import com.arygm.quickfix.ui.elements.QuickFixAvailabilityBottomSheet
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.elements.QuickFixPriceRangeBottomSheet
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.poppinsTypography
import com.arygm.quickfix.utils.LocationHelper
import java.time.LocalTime

data class SearchFilterButtons(
    val onClick: () -> Unit,
    val text: String,
    val leadingIcon: ImageVector? = null,
    val trailingIcon: ImageVector? = null,
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchWorkerResult(
    navigationActions: NavigationActions,
    searchViewModel: SearchViewModel,
    accountViewModel: AccountViewModel
) {
  var showAvailabilityBottomSheet by remember { mutableStateOf(false) }
  var showServicesBottomSheet by remember { mutableStateOf(false) }
  var showPriceRangeBottomSheet by remember { mutableStateOf(false) }
  val workerProfiles by searchViewModel.workerProfiles.collectAsState()
  var filteredWorkerProfiles by remember { mutableStateOf(workerProfiles) }
  var isWindowVisible by remember { mutableStateOf(false) }
  var saved by remember { mutableStateOf(false) }

  val listOfButtons =
      listOf(
          SearchFilterButtons(
              onClick = { /* Handle click */},
              text = "Location",
          ),
          SearchFilterButtons(
              onClick = { showServicesBottomSheet = true },
              text = "Service Type",
              trailingIcon = Icons.Default.KeyboardArrowDown,
          ),
          SearchFilterButtons(
              onClick = { showAvailabilityBottomSheet = true },
              text = "Availability",
              leadingIcon = Icons.Default.CalendarMonth,
              trailingIcon = Icons.Default.KeyboardArrowDown,
          ),
          SearchFilterButtons(
              onClick = {
                filteredWorkerProfiles = searchViewModel.sortWorkersByRating(filteredWorkerProfiles)
              },
              text = "Highest Rating",
              leadingIcon = Icons.Default.WorkspacePremium,
          ),
          SearchFilterButtons(
              onClick = { showPriceRangeBottomSheet = true },
              text = "Price Range",
          ),
      )

  val searchQuery by searchViewModel.searchQuery.collectAsState()
  val searchSubcategory by searchViewModel.searchSubcategory.collectAsState()
  var currentLocation by remember { mutableStateOf<Location?>(null) }

  val locationHelper: LocationHelper = LocationHelper(LocalContext.current, MainActivity())
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

                LazyRow(
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(top = screenHeight * 0.02f, bottom = screenHeight * 0.01f)
                            .padding(horizontal = screenWidth * 0.02f)
                            .wrapContentHeight()
                            .testTag("filter_buttons_row"),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                  items(1) {
                    IconButton(
                        onClick = { /* Goes to all filter screen */},
                        modifier =
                            Modifier.height(screenHeight * 0.05f)
                                .padding(bottom = screenHeight * 0.01f),
                        content = {
                          Icon(
                              imageVector = Icons.Default.Tune,
                              contentDescription = "Filter",
                              tint = colorScheme.onBackground,
                          )
                        },
                        colors =
                            IconButtonDefaults.iconButtonColors()
                                .copy(containerColor = colorScheme.surface),
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                  }

                  items(listOfButtons.size) { index ->
                    QuickFixButton(
                        buttonText = listOfButtons[index].text,
                        onClickAction = listOfButtons[index].onClick,
                        buttonColor = colorScheme.surface,
                        textColor = colorScheme.onBackground,
                        textStyle =
                            poppinsTypography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        height = screenHeight * 0.05f,
                        leadingIcon = listOfButtons[index].leadingIcon,
                        trailingIcon = listOfButtons[index].trailingIcon,
                        contentPadding =
                            PaddingValues(vertical = 0.dp, horizontal = screenWidth * 0.02f),
                        modifier = Modifier.testTag("filter_button_${listOfButtons[index].text}"))
                    Spacer(modifier = Modifier.width(screenHeight * 0.01f))
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
                                          Review("Great service!", "4"),
                                          Review("Very professional", "3.5"),
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
        showAvailabilityBottomSheet, onDismissRequest = { showAvailabilityBottomSheet = false }) {
            days,
            hour,
            minute ->
          filteredWorkerProfiles =
              searchViewModel.filterWorkersByAvailability(
                  filteredWorkerProfiles, days, hour, minute)
        }

    searchSubcategory?.let {
      ChooseServiceTypeSheet(
          showServicesBottomSheet,
          it.tags,
          onApplyClick = { services ->
            filteredWorkerProfiles =
                searchViewModel.filterWorkersByServices(filteredWorkerProfiles, services)
          },
          onDismissRequest = { showServicesBottomSheet = false })
    }

    QuickFixPriceRangeBottomSheet(
        showPriceRangeBottomSheet,
        onApplyClick = { start, end ->
          filteredWorkerProfiles =
              searchViewModel.filterWorkersByPriceRange(filteredWorkerProfiles, start, end)
        },
        onDismissRequest = { showPriceRangeBottomSheet = false })

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
