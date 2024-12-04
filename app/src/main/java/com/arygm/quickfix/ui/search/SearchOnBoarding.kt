package com.arygm.quickfix.ui.search

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.arygm.quickfix.MainActivity
import com.arygm.quickfix.R
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.category.Category
import com.arygm.quickfix.model.category.CategoryViewModel
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.elements.QuickFixTextFieldCustom
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.TopLevelDestinations
import com.arygm.quickfix.ui.theme.poppinsTypography
import com.arygm.quickfix.utils.LocationHelper

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

  BoxWithConstraints {
    val widthRatio = maxWidth.value / 411f
    val heightRatio = maxHeight.value / 860f
    val sizeRatio = minOf(widthRatio, heightRatio)

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
                  ProfileContent(
                      profiles = profiles,
                      searchViewModel = searchViewModel,
                      accountViewModel = accountViewModel,
                      listState = listState,
                      widthRatio = widthRatio,
                      heightRatio = heightRatio,
                  )
                }
              }
        })
  }
}

@Composable
fun CategoryContent(
    navigationActions: NavigationActions,
    searchViewModel: SearchViewModel,
    listState: LazyListState,
    expandedStates: MutableList<Boolean>,
    itemCategories: List<Category>,
    widthRatio: Float,
    heightRatio: Float,
) {
  Column(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp * widthRatio),
      horizontalAlignment = Alignment.Start) {
        Text(
            text = "Categories",
            style = poppinsTypography.labelLarge,
            color = colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(4.dp * heightRatio))
        LazyColumn(modifier = Modifier.fillMaxWidth(), state = listState) {
          itemsIndexed(itemCategories, key = { index, _ -> index }) { index, item ->
            ExpandableCategoryItem(
                item = item,
                isExpanded = expandedStates[index],
                onExpandedChange = { expandedStates[index] = it },
                searchViewModel = searchViewModel,
                navigationActions = navigationActions,
            )
            Spacer(modifier = Modifier.height(10.dp * heightRatio))
          }
        }
      }
}

@Composable
fun ProfileContent(
    profiles: List<WorkerProfile>, // Assuming WorkerProfile is your model class
    listState: LazyListState,
    searchViewModel: SearchViewModel,
    accountViewModel: AccountViewModel,
    widthRatio: Float,
    heightRatio: Float,
) {
  // Column for wrapping the list
  Column(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp * widthRatio),
      horizontalAlignment = Alignment.Start) {
        // Title: "Profiles"
        Text(
            text = "Profiles",
            style = poppinsTypography.labelLarge,
            color = colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(4.dp * heightRatio))

        // LazyColumn for displaying profiles
        LazyColumn(modifier = Modifier.fillMaxWidth(), state = listState) {
          items(profiles.size) { index ->
            val profile = profiles[index]
            var account by remember { mutableStateOf<Account?>(null) }
            var distance by remember { mutableStateOf<Int?>(null) }

            // Get user's current location and calculate distance
            val locationHelper = LocationHelper(LocalContext.current, MainActivity())
            locationHelper.getCurrentLocation { location ->
              location?.let {
                distance =
                    profile.location?.let { workerLocation ->
                      searchViewModel
                          .calculateDistance(
                              workerLocation.latitude,
                              workerLocation.longitude,
                              it.latitude,
                              it.longitude)
                          .toInt()
                    }
              }
            }

            // Fetch user account details
            LaunchedEffect(profile.uid) {
              accountViewModel.fetchUserAccount(profile.uid) { fetchedAccount ->
                account = fetchedAccount
              }
            }

            // Render profile card if account data is available
            account?.let { acc ->
              SearchWorkerProfileResult(
                  modifier =
                      Modifier.padding(vertical = 10.dp * heightRatio)
                          .fillMaxWidth()
                          .testTag("worker_profile_result_$index"),
                  profileImage = R.drawable.placeholder_worker, // Replace with actual image
                  name = "${acc.firstName} ${acc.lastName}",
                  category = profile.fieldOfWork,
                  rating = profile.rating,
                  reviewCount = profile.reviews.size,
                  location = profile.location?.name ?: "Unknown",
                  price = profile.price.toString(),
                  distance = distance,
                  onBookClick = {
                    // Handle book action
                  })
            }

            Spacer(modifier = Modifier.height(10.dp * heightRatio))
          }
        }
      }
}
