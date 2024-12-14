package com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arygm.quickfix.R
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.account.LoggedInAccountViewModel
import com.arygm.quickfix.model.category.CategoryViewModel
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.profile.dataFields.AddOnService
import com.arygm.quickfix.model.profile.dataFields.IncludedService
import com.arygm.quickfix.model.profile.dataFields.Review
import com.arygm.quickfix.model.search.AnnouncementViewModel
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import java.time.LocalTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickFixFinderScreen(
    navigationActions: NavigationActions,
    navigationActionsRoot: NavigationActions,
    isUser: Boolean = true,
    profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.UserFactory),
    loggedInAccountViewModel: LoggedInAccountViewModel =
        viewModel(factory = LoggedInAccountViewModel.Factory),
    searchViewModel: SearchViewModel = viewModel(factory = SearchViewModel.Factory),
    accountViewModel: AccountViewModel = viewModel(factory = AccountViewModel.Factory),
    announcementViewModel: AnnouncementViewModel =
        viewModel(factory = AnnouncementViewModel.Factory),
    categoryViewModel: CategoryViewModel = viewModel(factory = CategoryViewModel.Factory)
) {
  var isWindowVisible by remember { mutableStateOf(false) }
  var pager by remember { mutableStateOf(true) }
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
  val pagerState = rememberPagerState(pageCount = { 2 })
  val colorBackground =
      if (pagerState.currentPage == 0) colorScheme.background else colorScheme.surface
  val colorButton = if (pagerState.currentPage == 1) colorScheme.background else colorScheme.surface

  BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
    val screenHeight = maxHeight
    val screenWidth = maxWidth

    Scaffold(
        containerColor = colorBackground,
        topBar = {
          TopAppBar(
              title = {
                Text(
                    text = "Quickfix",
                    color = colorScheme.primary,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.testTag("QuickFixFinderTopBarTitle"))
              },
              colors = TopAppBarDefaults.topAppBarColors(containerColor = colorBackground),
              modifier = Modifier.testTag("QuickFixFinderTopBar"))
        },
        content = { padding ->
          Column(
              modifier = Modifier.fillMaxSize().testTag("QuickFixFinderContent").padding(padding),
              verticalArrangement = Arrangement.Center,
              horizontalAlignment = Alignment.CenterHorizontally) {
                val coroutineScope = rememberCoroutineScope()

                if (pager) {
                  Surface(
                      color = colorButton,
                      shape = RoundedCornerShape(screenWidth * 0.05f),
                      modifier =
                          Modifier.padding(horizontal = screenWidth * 0.1f)
                              .clip(RoundedCornerShape(screenWidth * 0.05f))) {
                        TabRow(
                            selectedTabIndex = pagerState.currentPage,
                            containerColor = Color.Transparent,
                            divider = {},
                            indicator = {},
                            modifier =
                                Modifier.padding(
                                        horizontal = screenWidth * 0.0025f,
                                        vertical = screenWidth * 0.0025f)
                                    .align(Alignment.CenterHorizontally)
                                    .testTag("quickFixSearchTabRow")) {
                              QuickFixScreenTab(
                                  pagerState, coroutineScope, 0, "Search", screenWidth)
                              QuickFixScreenTab(
                                  pagerState, coroutineScope, 1, "Announce", screenWidth)
                            }
                      }
                }

                HorizontalPager(
                    state = pagerState,
                    userScrollEnabled = false,
                    modifier = Modifier.testTag("quickFixSearchPager")) { page ->
                      when (page) {
                        0 -> {
                          SearchOnBoarding(
                              onSearch = { pager = false },
                              onSearchEmpty = { pager = true },
                              navigationActions,
                              navigationActionsRoot,
                              searchViewModel,
                              accountViewModel,
                              categoryViewModel) { profiles ->
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
                              }
                        }
                        1 -> {
                          AnnouncementScreen(
                              announcementViewModel,
                              loggedInAccountViewModel,
                              profileViewModel,
                              accountViewModel,
                              categoryViewModel,
                              navigationActions = navigationActions,
                              isUser = isUser)
                        }
                        else -> Text("Should never happen !")
                      }
                    }
              }
        })

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

@Composable
fun QuickFixScreenTab(
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    currentPage: Int,
    title: String,
    screenWidth: Dp
) {
  Tab(
      selected = pagerState.currentPage == currentPage,
      onClick = { coroutineScope.launch { pagerState.scrollToPage(currentPage) } },
      modifier =
          Modifier.padding(horizontal = screenWidth * 0.01f, vertical = screenWidth * 0.01f)
              .clip(RoundedCornerShape(screenWidth * 0.0325f))
              .background(
                  if (pagerState.currentPage == currentPage) colorScheme.primary
                  else Color.Transparent)
              .testTag("tab$title")) {
        Text(
            title,
            color =
                if (pagerState.currentPage == currentPage) colorScheme.background
                else colorScheme.tertiaryContainer,
            style = MaterialTheme.typography.titleMedium,
            modifier =
                Modifier.padding(horizontal = screenWidth * 0.04f, vertical = screenWidth * 0.02f)
                    .testTag("tabText$title"))
      }
}
