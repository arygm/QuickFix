package com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.search

import android.util.Log
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.arygm.quickfix.R
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.category.CategoryViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.quickfix.QuickFixViewModel
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.elements.QuickFixTextFieldCustom
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.poppinsTypography
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.navigation.UserScreen
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.navigation.UserTopLevelDestinations

@Composable
fun SearchOnBoarding(
    navigationActions: NavigationActions,
    navigationActionsRoot: NavigationActions,
    searchViewModel: SearchViewModel,
    accountViewModel: AccountViewModel,
    categoryViewModel: CategoryViewModel,
    quickFixViewModel: QuickFixViewModel,
    workerViewModel: ProfileViewModel
) {
  val profiles = searchViewModel.workerProfilesSuggestions.collectAsState()
  val focusManager = LocalFocusManager.current
  val categories = categoryViewModel.categories.collectAsState().value
  Log.d("SearchOnBoarding", "Categories: $categories")
  val itemCategories = remember { categories }
  val expandedStates = remember {
    mutableStateListOf(*BooleanArray(itemCategories.size) { false }.toTypedArray())
  }
  val listState = rememberLazyListState()

  var searchQuery by remember { mutableStateOf("") }
  var isWindowVisible by remember { mutableStateOf(false) }
  var selectedWorker by remember { mutableStateOf<WorkerProfile?>(null) }

  // Variables for WorkerSlidingWindowContent
  // These will be set when a worker profile is selected
  var bannerImage by remember { mutableStateOf(R.drawable.moroccan_flag) }
  var profilePicture by remember { mutableStateOf(R.drawable.placeholder_worker) }
  var initialSaved by remember { mutableStateOf(false) }
  var workerAddress by remember { mutableStateOf("") }

  BoxWithConstraints {
    val widthRatio = maxWidth.value / 411f
    val heightRatio = maxHeight.value / 860f
    val sizeRatio = minOf(widthRatio, heightRatio)
    val screenHeight = maxHeight.value
    val screenWidth = maxWidth.value

    // Use Scaffold for the layout structure
    Scaffold(
        containerColor = colorScheme.background,
        content = { padding ->
          Column(
              modifier =
                  Modifier.fillMaxWidth().padding(padding).padding(top = 40.dp * heightRatio),
              horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp * heightRatio),
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
                            searchViewModel.searchEngine(it)
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
                  ProfileResults(
                      profiles = profiles.value,
                      searchViewModel = searchViewModel,
                      accountViewModel = accountViewModel,
                      listState = listState,
                      heightRatio = heightRatio,
                      onBookClick = { selectedProfile, locName ->
                        selectedWorker = selectedProfile as WorkerProfile
                        // Set up variables for WorkerSlidingWindowContent
                        bannerImage = R.drawable.moroccan_flag
                        profilePicture = R.drawable.placeholder_worker
                        initialSaved = false
                        workerAddress = locName
                        isWindowVisible = true
                      },
                      workerViewModel = workerViewModel)

                  if (isWindowVisible) {
                    Popup(
                        onDismissRequest = { isWindowVisible = false },
                        alignment = Alignment.Center) {
                          selectedWorker?.let {
                            QuickFixSlidingWindowWorker(
                                isVisible = isWindowVisible,
                                onDismiss = { isWindowVisible = false },
                                bannerImage = bannerImage,
                                profilePicture = profilePicture,
                                initialSaved = initialSaved,
                                workerCategory = it.fieldOfWork,
                                workerAddress = workerAddress,
                                description = it.description,
                                includedServices = it.includedServices.map { it.name },
                                addonServices = it.addOnServices.map { it.name },
                                workerRating = it.reviews.map { it1 -> it1.rating }.average(),
                                tags = it.tags,
                                reviews = it.reviews.map { it.review },
                                screenHeight = screenHeight.dp,
                                screenWidth = screenWidth.dp,
                                onContinueClick = {
                                  quickFixViewModel.setSelectedWorkerProfile(it)
                                  navigationActions.navigateTo(UserScreen.QUICKFIX_ONBOARDING)
                                })
                          }
                        }
                  }
                }
              }
        },
        modifier =
            Modifier.pointerInput(Unit) {
              detectTapGestures(onTap = { focusManager.clearFocus() })
            })
  }
}
