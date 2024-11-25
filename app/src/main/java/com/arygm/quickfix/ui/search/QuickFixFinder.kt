package com.arygm.quickfix.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.arygm.quickfix.model.account.LoggedInAccountViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.search.AnnouncementViewModel
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickFixFinderScreen(
    navigationActions: NavigationActions,
    navigationActionsRoot: NavigationActions,
    isUser: Boolean = true,
    profileViewModel: ProfileViewModel,
    loggedInAccountViewModel: LoggedInAccountViewModel,
    searchViewModel: SearchViewModel,
    announcementViewModel: AnnouncementViewModel
) {
  Scaffold(
      containerColor = colorScheme.background,
      topBar = {
        TopAppBar(
            title = {
              Text(
                  text = "Quickfix",
                  color = colorScheme.primary,
                  style = MaterialTheme.typography.headlineLarge,
                  modifier = Modifier.testTag("QuickFixFinderTopBarTitle"))
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = colorScheme.background),
            modifier = Modifier.testTag("QuickFixFinderTopBar"))
      },
      content = { padding ->
        Column(
            modifier = Modifier.fillMaxSize().testTag("QuickFixFinderContent").padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
              val pagerState = rememberPagerState(pageCount = { 2 })
              val coroutineScope = rememberCoroutineScope()

              Surface(
                  color = colorScheme.surface,
                  shape = RoundedCornerShape(20.dp),
                  modifier = Modifier.padding(horizontal = 40.dp).clip(RoundedCornerShape(20.dp))) {
                    TabRow(
                        selectedTabIndex = pagerState.currentPage,
                        containerColor = Color.Transparent,
                        divider = {},
                        indicator = {},
                        modifier =
                            Modifier.padding(horizontal = 1.dp, vertical = 1.dp)
                                .align(Alignment.CenterHorizontally)
                                .testTag("quickFixSearchTabRow")) {
                          QuickFixScreenTab(pagerState, coroutineScope, 0, "Search")
                          QuickFixScreenTab(pagerState, coroutineScope, 1, "Announce")
                        }
                  }

              HorizontalPager(
                  state = pagerState,
                  userScrollEnabled = false,
                  modifier = Modifier.testTag("quickFixSearchPager")) { page ->
                    when (page) {
                      0 ->
                          SearchOnBoarding(
                              navigationActions, navigationActionsRoot, isUser, searchViewModel)
                      1 ->
                          AnnouncementScreen(
                              announcementViewModel,
                              loggedInAccountViewModel,
                              profileViewModel,
                              navigationActions,
                              navigationActionsRoot,
                              isUser)
                      else -> Text("Should never happen !")
                    }
                  }
            }
      })
}

@Composable
fun QuickFixScreenTab(
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    currentPage: Int,
    title: String
) {
  Tab(
      selected = pagerState.currentPage == currentPage,
      onClick = { coroutineScope.launch { pagerState.scrollToPage(currentPage) } },
      modifier =
          Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
              .clip(RoundedCornerShape(13.dp))
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
                Modifier.padding(horizontal = 16.dp, vertical = 8.dp).testTag("tabText$title"))
      }
}
