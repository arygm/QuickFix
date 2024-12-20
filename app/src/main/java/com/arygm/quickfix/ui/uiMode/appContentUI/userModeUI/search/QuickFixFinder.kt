package com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.category.CategoryViewModel
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.quickfix.QuickFixViewModel
import com.arygm.quickfix.model.search.AnnouncementViewModel
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.ui.elements.defaultBitmap
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.navigation.UserScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickFixFinderScreen(
    navigationActions: NavigationActions,
    navigationActionsRoot: NavigationActions,
    isUser: Boolean = true,
    profileViewModel: ProfileViewModel,
    accountViewModel: AccountViewModel,
    searchViewModel: SearchViewModel,
    announcementViewModel: AnnouncementViewModel,
    categoryViewModel: CategoryViewModel =
        viewModel(factory = CategoryViewModel.Factory(LocalContext.current)),
    quickFixViewModel: QuickFixViewModel,
    preferencesViewModel: PreferencesViewModel,
    workerViewModel: ProfileViewModel
) {
  var isWindowVisible by remember { mutableStateOf(false) }
  var selectedCityName by remember { mutableStateOf<String?>(null) }

  var selectedWorker by remember { mutableStateOf(WorkerProfile()) }
  val pagerState = rememberPagerState(pageCount = { 2 })
  val colorBackground =
      if (pagerState.currentPage == 0) colorScheme.background else colorScheme.surface
  val colorButton = if (pagerState.currentPage == 1) colorScheme.background else colorScheme.surface

  var profilePicture by remember { mutableStateOf(defaultBitmap) }
  var bannerPicture by remember { mutableStateOf(defaultBitmap) }

  var initialSaved by remember { mutableStateOf(false) }
  BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
    val screenHeight = maxHeight
    val screenWidth = maxWidth

    Scaffold(
        containerColor = colorBackground,
        topBar = {
          TopAppBar(
              title = {
                val coroutineScope = rememberCoroutineScope()
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxSize().padding(end = screenWidth * 0.05f)) {
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
                                            horizontal = screenWidth * 0.01f,
                                            vertical = screenWidth * 0.01f)
                                        .testTag("quickFixSearchTabRow")) {
                                  QuickFixScreenTab(
                                      pagerState, coroutineScope, 0, "Search", screenWidth)
                                  QuickFixScreenTab(
                                      pagerState, coroutineScope, 1, "Announce", screenWidth)
                                }
                          }
                    }
              },
              colors = TopAppBarDefaults.topAppBarColors(containerColor = colorBackground),
              modifier = Modifier.testTag("QuickFixFinderTopBar"))
        },
        content = { padding ->
          Column(
              modifier = Modifier.fillMaxSize().testTag("QuickFixFinderContent").padding(padding),
              verticalArrangement = Arrangement.Center,
              horizontalAlignment = Alignment.CenterHorizontally) {
                HorizontalPager(
                    state = pagerState,
                    userScrollEnabled = false,
                    modifier = Modifier.testTag("quickFixSearchPager")) { page ->
                      when (page) {
                        0 -> {
                          SearchOnBoarding(
                              navigationActions,
                              navigationActionsRoot,
                              searchViewModel,
                              accountViewModel,
                              preferencesViewModel,
                              profileViewModel,
                              categoryViewModel,
                              onBookClick = { selectedProfile, locName, profile, banner ->
                                bannerPicture = banner
                                profilePicture = profile
                                initialSaved = false
                                selectedCityName = locName
                                isWindowVisible = true
                                selectedWorker = selectedProfile
                              },
                              workerViewModel = workerViewModel)
                        }
                        1 -> {
                          AnnouncementScreen(
                              announcementViewModel,
                              profileViewModel,
                              accountViewModel,
                              preferencesViewModel,
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
        screenHeight = screenHeight,
        screenWidth = screenWidth,
        onContinueClick = {
          quickFixViewModel.setSelectedWorkerProfile(selectedWorker)
          navigationActions.navigateTo(UserScreen.QUICKFIX_ONBOARDING)
        },
        bannerImage = bannerPicture,
        profilePicture = profilePicture,
        initialSaved = initialSaved,
        workerCategory = selectedWorker.fieldOfWork,
        selectedCityName = selectedCityName,
        description = selectedWorker.description,
        includedServices = selectedWorker.includedServices.map { it.name },
        addonServices = selectedWorker.addOnServices.map { it.name },
        workerRating = selectedWorker.reviews.map { it1 -> it1.rating }.average(),
        tags = selectedWorker.tags,
        reviews = selectedWorker.reviews.map { it.review },
    )
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
            style = typography.titleMedium,
            modifier =
                Modifier.padding(horizontal = screenWidth * 0.04f, vertical = screenWidth * 0.02f)
                    .testTag("tabText$title"))
      }
}
