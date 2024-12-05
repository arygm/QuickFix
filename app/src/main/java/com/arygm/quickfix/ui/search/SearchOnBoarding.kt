// SearchOnBoarding.kt

package com.arygm.quickfix.ui.search

import QuickFixSlidingWindowWorker
import android.util.Log
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
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.arygm.quickfix.R
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.category.CategoryViewModel
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.elements.QuickFixTextFieldCustom
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.TopLevelDestinations
import com.arygm.quickfix.ui.theme.poppinsTypography

@Composable
fun SearchOnBoarding(
    navigationActions: NavigationActions,
    navigationActionsRoot: NavigationActions,
    searchViewModel: SearchViewModel,
    accountViewModel: AccountViewModel,
    categoryViewModel: CategoryViewModel
) {
  val profiles = searchViewModel.workerProfiles.collectAsState().value
  val categories = categoryViewModel.categories.collectAsState().value
  Log.d("SearchOnBoarding", "Categories: $categories")
  val itemCategories = remember { categories }
  val expandedStates = remember {
    mutableStateListOf(*BooleanArray(itemCategories.size) { false }.toTypedArray())
  }
  val listState = rememberLazyListState()

  var searchQuery by remember { mutableStateOf("") }
  var isWindowVisible by remember { mutableStateOf(false) }

  // Variables for WorkerSlidingWindowContent
  // These will be set when a worker profile is selected
  var bannerImage by remember { mutableStateOf(R.drawable.moroccan_flag) }
  var profilePicture by remember { mutableStateOf(R.drawable.placeholder_worker) }
  var initialSaved by remember { mutableStateOf(false) }
  var workerCategory by remember { mutableStateOf("Exterior Painter") }
  var workerAddress by remember { mutableStateOf("Ecublens, VD") }
  var description by remember { mutableStateOf("Worker description goes here.") }
  var includedServices by remember { mutableStateOf(listOf("Service 1", "Service 2")) }
  var addonServices by remember { mutableStateOf(listOf("Add-on 1", "Add-on 2")) }
  var workerRating by remember { mutableStateOf(4.5) }
  var tags by remember { mutableStateOf(listOf("Tag1", "Tag2")) }
  var reviews by remember { mutableStateOf(listOf("Review 1", "Review 2")) }

  BoxWithConstraints {
    val widthRatio = maxWidth.value / 411f
    val heightRatio = maxHeight.value / 860f
    val sizeRatio = minOf(widthRatio, heightRatio)
    val screenHeight = maxHeight
    val screenWidth = maxWidth

    // Use Scaffold for the layout structure
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
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp * heightRatio),
                    horizontalArrangement = Arrangement.Center) {
                      QuickFixTextFieldCustom(
                          modifier = Modifier.testTag("searchContent"),
                          showLeadingIcon = { true },
                          leadingIcon = Icons.Outlined.Search,
                          showTrailingIcon = { searchQuery.isNotEmpty() },
                          trailingIcon = {
                            IconButton(
                                onClick = { searchQuery = "" },
                                modifier = Modifier.testTag("clearSearchQueryIcon")) {
                                  Icon(
                                      imageVector = Icons.Filled.Clear,
                                      contentDescription = "Clear search query",
                                      tint = colorScheme.onBackground,
                                  )
                                }
                          },
                          placeHolderText = "Find your perfect fix with QuickFix",
                          value = searchQuery, // Search query
                          onValueChange = {
                            searchQuery = it
                            searchViewModel.updateSearchQuery(it)
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
                          textStyle = poppinsTypography.bodyMedium,
                          onClickAction = {
                            navigationActionsRoot.navigateTo(TopLevelDestinations.HOME)
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
                      profiles = profiles,
                      searchViewModel = searchViewModel,
                      accountViewModel = accountViewModel,
                      listState = listState,
                      widthRatio = widthRatio,
                      heightRatio = heightRatio,
                      onBookClick = { selectedProfile ->
                        // Handle profile click
                        // Set up variables for WorkerSlidingWindowContent
                        bannerImage = R.drawable.moroccan_flag // Replace with actual data
                        profilePicture = R.drawable.placeholder_worker // Replace with actual data
                        initialSaved = false // Replace with actual data
                        workerCategory = selectedProfile.fieldOfWork
                        workerAddress = selectedProfile.location?.name ?: "Unknown"
                        description = selectedProfile.description
                        includedServices = selectedProfile.includedServices.map { it.name }
                        addonServices = selectedProfile.addOnServices.map { it.name }
                        workerRating = selectedProfile.rating
                        tags = selectedProfile.tags
                        reviews =
                            selectedProfile.reviews.map {
                              it.review
                            } // Replace 'text' with the correct property
                        isWindowVisible = true
                      })
                }
              }
        })

    // Call to WorkerSlidingWindowContent
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
