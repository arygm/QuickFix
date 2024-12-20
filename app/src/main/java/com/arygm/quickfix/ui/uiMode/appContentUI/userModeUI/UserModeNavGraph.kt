package com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.arygm.quickfix.dataStore
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.category.CategoryViewModel
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.locations.LocationViewModel
import com.arygm.quickfix.model.messaging.ChatViewModel
import com.arygm.quickfix.model.offline.small.PreferencesRepositoryDataStore
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.offline.small.PreferencesViewModelUserProfile
import com.arygm.quickfix.model.offline.small.PreferencesViewModelWorkerProfile
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.UserProfileRepositoryFirestore
import com.arygm.quickfix.model.quickfix.QuickFixViewModel
import com.arygm.quickfix.model.search.AnnouncementRepositoryFirestore
import com.arygm.quickfix.model.search.AnnouncementViewModel
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.model.switchModes.ModeViewModel
import com.arygm.quickfix.ui.dashboard.DashboardScreen
import com.arygm.quickfix.ui.elements.LocationSearchCustomScreen
import com.arygm.quickfix.ui.elements.QuickFixDisplayImagesScreen
import com.arygm.quickfix.ui.elements.QuickFixOfflineBar
import com.arygm.quickfix.ui.navigation.BottomNavigationMenu
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.search.QuickFixFinderScreen
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.camera.QuickFixDisplayImages
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.home.HomeScreen
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.home.MessageScreen
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.map.MapScreen
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.navigation.USER_TOP_LEVEL_DESTINATIONS
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.navigation.UserRoute
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.navigation.UserScreen
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.navigation.getBottomBarIdUser
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.profile.AccountConfigurationScreen
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.profile.UserProfileScreen
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.profile.becomeWorker.BusinessScreen
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.quickfix.QuickFixOnBoarding
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.search.AnnouncementDetailScreen
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.search.SearchWorkerResult
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.delay

@Composable
fun UserModeNavHost(
    testBitmapPP: Bitmap?,
    testLocation: Location = Location(),
    modeViewModel: ModeViewModel,
    userViewModel: ProfileViewModel,
    workerViewModel: ProfileViewModel,
    accountViewModel: AccountViewModel,
    categoryViewModel: CategoryViewModel,
    locationViewModel: LocationViewModel,
    preferencesViewModel: PreferencesViewModel,
    rootMainNavigationActions: NavigationActions,
    userPreferencesViewModel: PreferencesViewModelUserProfile,
    workerPreferencesViewModel: PreferencesViewModelWorkerProfile,
    appContentNavigationActions: NavigationActions,
    chatViewModel: ChatViewModel,
    quickFixViewModel: QuickFixViewModel,
    isOffline: Boolean
) {
  val context = LocalContext.current
  val userNavController = rememberNavController()
  val userNavigationActions = remember { NavigationActions(userNavController) }
  val searchViewModel: SearchViewModel = viewModel(factory = SearchViewModel.Factory)

  // Create required repositories
  val announcementRepository =
      AnnouncementRepositoryFirestore(db = Firebase.firestore, storage = Firebase.storage)
  val preferencesRepository = PreferencesRepositoryDataStore(context.dataStore)
  val userProfileRepository =
      UserProfileRepositoryFirestore(db = Firebase.firestore, storage = Firebase.storage)
  val announcementViewModel: AnnouncementViewModel =
      viewModel(
          factory =
              AnnouncementViewModel.userFactory(
                  announcementRepository = announcementRepository,
                  preferencesRepository = preferencesRepository,
                  userProfileRepository = userProfileRepository))

  // Initialized here because needed for the bottom bar
  val startDestination by modeViewModel.onSwitchStartDestUser.collectAsState()
  val isUser = true // TODO: This variable needs to get its value after the authentication
  var currentScreen by remember { mutableStateOf<String?>(null) }
  val shouldShowBottomBar by remember {
    derivedStateOf {
      currentScreen?.let {
        it != UserScreen.DISPLAY_UPLOADED_IMAGES && it != UserScreen.SEARCH_LOCATION
      } ?: true &&
          currentScreen?.let {
            it != UserScreen.ACCOUNT_CONFIGURATION && it != UserScreen.TO_WORKER
          } ?: true &&
          currentScreen?.let {
            it != UserScreen.QUICKFIX_ONBOARDING &&
                it != UserScreen.MESSAGES &&
                it != UserScreen.QUICKFIX_DISPLAY_IMAGES
          } ?: true &&
          currentScreen?.let { it != UserScreen.ANNOUNCEMENT_DETAIL } ?: true
    }
  }

  var showBottomBar by remember { mutableStateOf(false) }

  // Delay the appearance of the bottom bar
  LaunchedEffect(shouldShowBottomBar) {
    if (shouldShowBottomBar) {
      delay(200) // Adjust the delay duration (in milliseconds) as needed
      showBottomBar = true
    } else {
      showBottomBar = false
    }
  }

  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
        topBar = { QuickFixOfflineBar(isVisible = isOffline) },
        modifier = Modifier.fillMaxSize() // Ensure Scaffold fills the parent
        ) { innerPadding ->
          Box(
              modifier =
                  Modifier.fillMaxSize().padding(innerPadding) // Apply the scaffold's padding
              ) {
                // Main app navigation content
                NavHost(
                    navController = userNavigationActions.navController,
                    startDestination = startDestination,
                    enterTransition = { EnterTransition.None },
                    exitTransition = { ExitTransition.None }) {
                      composable(UserRoute.HOME) {
                        HomeNavHost(
                            onScreenChange = { currentScreen = it },
                            chatViewModel,
                            modeViewModel,
                            locationViewModel,
                            accountViewModel,
                            categoryViewModel,
                            preferencesViewModel,
                            userViewModel,
                            workerViewModel,
                            quickFixViewModel,
                            userNavigationActions,
                            searchViewModel,
                            announcementViewModel,
                            isUser)
                      }
                      composable(UserRoute.SEARCH) {
                        SearchNavHost(
                            isUser,
                            userNavigationActions,
                            searchViewModel,
                            userViewModel,
                            workerViewModel,
                            accountViewModel,
                            announcementViewModel,
                            onScreenChange = { currentScreen = it },
                            categoryViewModel,
                            preferencesViewModel,
                            locationViewModel,
                            quickFixViewModel,
                            chatViewModel,
                            modeViewModel)
                      }
                      composable(UserRoute.DASHBOARD) {
                        DashBoardNavHost(
                            onScreenChange = { currentScreen = it },
                            userViewModel,
                            workerViewModel,
                            accountViewModel,
                            modeViewModel,
                            locationViewModel,
                            quickFixViewModel,
                            chatViewModel,
                            preferencesViewModel,
                            announcementViewModel,
                            categoryViewModel,
                            userNavigationActions)
                      }
                      composable(UserRoute.PROFILE) {
                        ProfileNavHost(
                            accountViewModel,
                            workerViewModel,
                            userNavigationActions,
                            onScreenChange = { currentScreen = it },
                            categoryViewModel,
                            preferencesViewModel,
                            locationViewModel,
                            testBitmapPP,
                            testLocation,
                            rootMainNavigationActions,
                            userPreferencesViewModel,
                            appContentNavigationActions,
                            modeViewModel,
                            workerPreferencesViewModel)
                      }
                    }

                // Bottom bar aligned at the bottom of the same parent Box

                AnimatedVisibility(
                    visible = showBottomBar,
                    enter = slideInVertically { fullHeight -> fullHeight }, // Slide in from bottom
                    exit = slideOutVertically { fullHeight -> fullHeight }, // Slide out to bottom
                    modifier =
                        Modifier.align(Alignment.BottomCenter) // Align at bottom of this parent Box
                            .zIndex(1f)
                            .testTag("BNM")) {
                      BottomNavigationMenu(
                          onTabSelect = { selectedDestination ->
                            userNavigationActions.navigateTo(selectedDestination)
                          },
                          navigationActions = userNavigationActions,
                          tabList = USER_TOP_LEVEL_DESTINATIONS,
                          getBottomBarId = getBottomBarIdUser)
                    }
              }
        }
  }
}

@Composable
fun HomeNavHost(
    onScreenChange: (String) -> Unit = {},
    chatViewModel: ChatViewModel,
    modeViewModel: ModeViewModel,
    locationViewModel: LocationViewModel,
    accountViewModel: AccountViewModel,
    categoryViewModel: CategoryViewModel,
    preferencesViewModel: PreferencesViewModel,
    userViewModel: ProfileViewModel,
    workerViewModel: ProfileViewModel,
    quickFixViewModel: QuickFixViewModel,
    navigationActionsRoot: NavigationActions,
    searchViewModel: SearchViewModel,
    announcementViewModel: AnnouncementViewModel,
    isUser: Boolean
) {
  val homeNavController = rememberNavController()
  val navigationActions = remember { NavigationActions(homeNavController) }

  BackHandler { navigationActions.goBack() }

  LaunchedEffect(navigationActions.currentScreen) {
    onScreenChange(navigationActions.currentScreen)
  }
  NavHost(
      navController = homeNavController,
      startDestination = UserScreen.HOME,
      route = UserRoute.HOME,
  ) {
    composable(UserScreen.HOME) {
      HomeScreen(
          navigationActions,
          preferencesViewModel,
          userViewModel,
          workerViewModel,
          quickFixViewModel,
          navigationActionsRoot,
          searchViewModel)
    }

    composable(UserScreen.QUICKFIX_ONBOARDING) {
      QuickFixOnBoarding(
          navigationActionsRoot = navigationActionsRoot,
          navigationActions = navigationActions,
          modeViewModel = modeViewModel,
          quickFixViewModel = quickFixViewModel,
          preferencesViewModel = preferencesViewModel,
          chatViewModel = chatViewModel,
          userViewModel = userViewModel,
          workerViewModel = workerViewModel,
          locationViewModel = locationViewModel,
          accountViewModel = accountViewModel,
          categoryViewModel = categoryViewModel)
    }
    // Add MessageScreen as a nested composable within Home
    composable(UserScreen.MESSAGES) {
      MessageScreen(
          chatViewModel = chatViewModel,
          navigationActions = navigationActions,
          quickFixViewModel = quickFixViewModel,
          preferencesViewModel = preferencesViewModel,
          accountViewModel = accountViewModel,
          workerViewModel = workerViewModel)
    }

    composable(UserScreen.SEARCH_WORKER_RESULT) {
      SearchWorkerResult(
          navigationActions,
          searchViewModel,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          quickFixViewModel,
          workerViewModel = workerViewModel)
    }
    composable(UserScreen.MAP) {
      MapScreen(
          workerViewModel,
          searchViewModel,
          quickFixViewModel,
          navigationActions,
          userViewModel,
          preferencesViewModel)
    }
    composable(UserScreen.SEARCH) {
      QuickFixFinderScreen(
          navigationActions,
          navigationActionsRoot,
          isUser,
          userViewModel,
          accountViewModel,
          searchViewModel,
          announcementViewModel,
          categoryViewModel,
          quickFixViewModel,
          preferencesViewModel,
          workerViewModel = workerViewModel)
    }
  }
}

@Composable
fun ProfileNavHost(
    accountViewModel: AccountViewModel,
    workerViewModel: ProfileViewModel,
    userNavigationActions: NavigationActions,
    onScreenChange: (String) -> Unit,
    categoryViewModel: CategoryViewModel,
    preferencesViewModel: PreferencesViewModel,
    locationViewModel: LocationViewModel,
    testBitmapPP: Bitmap? = null,
    testLocation: Location = Location(),
    rootMainNavigationActions: NavigationActions,
    userPreferencesViewModel: PreferencesViewModelUserProfile,
    appContentNavigationActions: NavigationActions,
    modeViewModel: ModeViewModel,
    workerPreferencesViewModel: PreferencesViewModelWorkerProfile
) {

  val profileNavController = rememberNavController()
  val profileNavigationActions = remember { NavigationActions(profileNavController) }

  BackHandler { profileNavigationActions.goBack() }

  LaunchedEffect(profileNavigationActions.currentScreen) {
    onScreenChange(profileNavigationActions.currentScreen)
  }
  NavHost(navController = profileNavController, startDestination = UserScreen.PROFILE) {
    composable(UserScreen.PROFILE) {
      UserProfileScreen(
          userNavigationActions,
          profileNavigationActions,
          rootMainNavigationActions,
          preferencesViewModel,
          userPreferencesViewModel,
          appContentNavigationActions,
          modeViewModel)
    }
    composable(UserScreen.ACCOUNT_CONFIGURATION) {
      AccountConfigurationScreen(profileNavigationActions, accountViewModel, preferencesViewModel)
    }
    composable(UserScreen.TO_WORKER) {
      BusinessScreen(
          profileNavigationActions,
          accountViewModel,
          workerViewModel,
          preferencesViewModel,
          categoryViewModel,
          locationViewModel,
          testBitmapPP,
          testLocation,
          workerPreferencesViewModel)
    }
  }
}

@Composable
fun DashBoardNavHost(
    onScreenChange: (String) -> Unit,
    userViewModel: ProfileViewModel,
    workerViewModel: ProfileViewModel,
    accountViewModel: AccountViewModel,
    modeViewModel: ModeViewModel,
    locationViewModel: LocationViewModel,
    quickFixViewModel: QuickFixViewModel,
    chatViewModel: ChatViewModel,
    preferencesViewModel: PreferencesViewModel,
    announcementViewModel: AnnouncementViewModel,
    categoryViewModel: CategoryViewModel,
    navigationActionsRoot: NavigationActions
) {
  val dashboardNavController = rememberNavController()
  val navigationActions = remember { NavigationActions(dashboardNavController) }

  BackHandler { navigationActions.goBack() }

  LaunchedEffect(navigationActions.currentScreen) {
    onScreenChange(navigationActions.currentScreen)
  }
  NavHost(navController = dashboardNavController, startDestination = UserScreen.DASHBOARD) {
    composable(UserScreen.DASHBOARD) {
      DashboardScreen(
          navigationActions,
          userViewModel,
          workerViewModel,
          accountViewModel,
          quickFixViewModel,
          chatViewModel,
          preferencesViewModel,
          announcementViewModel,
          categoryViewModel)
    }
    composable(UserScreen.MESSAGES) {
      MessageScreen(
          chatViewModel,
          navigationActions,
          quickFixViewModel,
          preferencesViewModel,
          workerViewModel,
          accountViewModel)
    }

    composable(UserScreen.QUICKFIX_ONBOARDING) {
      QuickFixOnBoarding(
          navigationActionsRoot = navigationActionsRoot,
          navigationActions = navigationActions,
          modeViewModel = modeViewModel,
          quickFixViewModel = quickFixViewModel,
          preferencesViewModel = preferencesViewModel,
          chatViewModel = chatViewModel,
          userViewModel = userViewModel,
          workerViewModel = workerViewModel,
          locationViewModel = locationViewModel,
          accountViewModel = accountViewModel,
          categoryViewModel = categoryViewModel)
    }
    composable(UserScreen.ANNOUNCEMENT_DETAIL) {
      AnnouncementDetailScreen(
          announcementViewModel, categoryViewModel, preferencesViewModel, navigationActions)
    }
    composable(UserScreen.DISPLAY_UPLOADED_IMAGES) {
      QuickFixDisplayImages(navigationActions, preferencesViewModel, announcementViewModel)
    }

    composable(UserScreen.QUICKFIX_DISPLAY_IMAGES) {
      QuickFixDisplayImagesScreen(
          navigationActions = navigationActions,
          chatViewModel = chatViewModel,
          quickFixViewModel = quickFixViewModel)
    }
  }
}

@Composable
fun SearchNavHost(
    isUser: Boolean,
    navigationActionsRoot: NavigationActions,
    searchViewModel: SearchViewModel,
    userViewModel: ProfileViewModel,
    workerViewModel: ProfileViewModel,
    accountViewModel: AccountViewModel,
    announcementViewModel: AnnouncementViewModel,
    onScreenChange: (String) -> Unit,
    categoryViewModel: CategoryViewModel,
    preferencesViewModel: PreferencesViewModel,
    locationViewModel: LocationViewModel,
    quickFixViewModel: QuickFixViewModel,
    chatViewModel: ChatViewModel,
    modeViewModel: ModeViewModel
) {
  val searchNavController = rememberNavController()
  val navigationActions = remember { NavigationActions(searchNavController) }

  BackHandler { navigationActions.goBack() }

  LaunchedEffect(navigationActions.currentScreen) {
    onScreenChange(navigationActions.currentScreen)
  }
  NavHost(
      navController = searchNavController,
      startDestination = UserScreen.SEARCH,
  ) {
    composable(UserScreen.SEARCH) {
      QuickFixFinderScreen(
          navigationActions,
          navigationActionsRoot,
          isUser,
          userViewModel,
          accountViewModel,
          searchViewModel,
          announcementViewModel,
          categoryViewModel,
          quickFixViewModel,
          preferencesViewModel,
          workerViewModel = workerViewModel)
    }
    composable(UserScreen.DISPLAY_UPLOADED_IMAGES) {
      QuickFixDisplayImages(navigationActions, preferencesViewModel, announcementViewModel)
    }
    composable(UserScreen.SEARCH_WORKER_RESULT) {
      SearchWorkerResult(
          navigationActions,
          searchViewModel,
          accountViewModel,
          userViewModel,
          preferencesViewModel,
          quickFixViewModel,
          workerViewModel = workerViewModel)
    }
    composable(UserScreen.SEARCH_LOCATION) {
      LocationSearchCustomScreen(
          navigationActions = navigationActions, locationViewModel = locationViewModel)
    }

    composable(UserScreen.QUICKFIX_ONBOARDING) {
      QuickFixOnBoarding(
          navigationActionsRoot = navigationActionsRoot,
          navigationActions = navigationActions,
          modeViewModel = modeViewModel,
          quickFixViewModel = quickFixViewModel,
          preferencesViewModel = preferencesViewModel,
          chatViewModel = chatViewModel,
          userViewModel = userViewModel,
          workerViewModel = workerViewModel,
          locationViewModel = locationViewModel,
          accountViewModel = accountViewModel,
          categoryViewModel = categoryViewModel)
    }

    composable(UserScreen.MESSAGES) {
      MessageScreen(
          chatViewModel = chatViewModel,
          navigationActions = navigationActions,
          quickFixViewModel = quickFixViewModel,
          preferencesViewModel = preferencesViewModel,
          accountViewModel = accountViewModel,
          workerViewModel = workerViewModel)
    }
    composable(UserScreen.QUICKFIX_DISPLAY_IMAGES) {
      QuickFixDisplayImagesScreen(
          navigationActions = navigationActions,
          chatViewModel = chatViewModel,
          quickFixViewModel = quickFixViewModel)
    }
  }
}
