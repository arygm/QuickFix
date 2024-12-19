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
    preferencesViewModel: PreferencesViewModel
) {
  var isWindowVisible by remember { mutableStateOf(false) }
  var pager by remember { mutableStateOf(true) }
  var selectedWorker by remember { mutableStateOf(WorkerProfile()) }
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
                    style = typography.headlineLarge,
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
                              navigationActions,
                              navigationActionsRoot,
                              searchViewModel,
                              accountViewModel,
                              categoryViewModel,
                              onProfileClick = { profile -> selectedWorker = profile })
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
        workerProfile = selectedWorker,
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
