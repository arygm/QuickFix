package com.arygm.quickfix.ui.search

import android.location.Location
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.poppinsTypography
import com.arygm.quickfix.utils.LocationHelper

data class SearchFilterButtons(
    val onClick: () -> Unit,
    val text: String,
    val leadingIcon: ImageVector? = null,
    val trailingIcon: ImageVector? = null,
)

val listOfButtons =
    listOf(
        SearchFilterButtons(
            onClick = { /* Handle click */},
            text = "Location",
        ),
        SearchFilterButtons(
            onClick = { /* Handle click */},
            text = "Service Type",
            trailingIcon = Icons.Default.KeyboardArrowDown,
        ),
        SearchFilterButtons(
            onClick = { /* Handle click */},
            text = "Availability",
            leadingIcon = Icons.Default.CalendarMonth,
            trailingIcon = Icons.Default.KeyboardArrowDown,
        ),
        SearchFilterButtons(
            onClick = { /* Handle click */},
            text = "Highest Rating",
            leadingIcon = Icons.Default.WorkspacePremium,
        ),
        SearchFilterButtons(
            onClick = { /* Handle click */},
            text = "Price Range",
        ),
    )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchWorkerResult(
    navigationActions: NavigationActions,
    searchViewModel: SearchViewModel,
    accountViewModel: AccountViewModel
) {

  val searchQuery by searchViewModel.searchQuery.collectAsState()
  val workerProfiles by searchViewModel.workerProfiles.collectAsState()
  var currentLocation by remember { mutableStateOf<Location?>(null) }

  var locationHelper: LocationHelper = LocationHelper(LocalContext.current, MainActivity())

  Scaffold(
      topBar = {
        CenterAlignedTopAppBar(
            title = {},
            navigationIcon = {
              IconButton(onClick = { navigationActions.goBack() }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                          .padding(top = 20.dp, bottom = 10.dp)
                          .padding(horizontal = 10.dp)
                          .wrapContentHeight()
                          .testTag("filter_buttons_row"),
                  verticalAlignment = Alignment.CenterVertically,
              ) {
                items(1) {
                  Box(modifier = Modifier.height(40.dp)) {
                    IconButton(
                        onClick = { /* Goes to all filter screen */},
                        modifier = Modifier.padding(bottom = 8.dp),
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
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                }

                items(listOfButtons.size) { index ->
                  QuickFixButton(
                      buttonText = listOfButtons[index].text,
                      onClickAction = listOfButtons[index].onClick,
                      buttonColor = colorScheme.surface,
                      textColor = colorScheme.onBackground,
                      textStyle = poppinsTypography.labelSmall.copy(fontWeight = FontWeight.Medium),
                      height = 40.dp,
                      leadingIcon = listOfButtons[index].leadingIcon,
                      trailingIcon = listOfButtons[index].trailingIcon,
                      contentPadding = PaddingValues(vertical = 0.dp, horizontal = 10.dp),
                      modifier = Modifier.testTag("filter_button_${listOfButtons[index].text}"))
                  Spacer(modifier = Modifier.width(10.dp))
                }
              }
              LazyColumn(modifier = Modifier.fillMaxWidth().testTag("worker_profiles_list")) {
                items(workerProfiles.size) { index ->
                  val profile = workerProfiles[index]
                  var account by remember { mutableStateOf<Account?>(null) }
                  var distance by remember { mutableStateOf<Int?>(null) }

                  locationHelper.getCurrentLocation { location ->
                    currentLocation = location
                    location?.let {
                      distance =
                          profile.location
                              ?.let { workerLocation ->
                                searchViewModel.calculateDistance(
                                    workerLocation.latitude,
                                    workerLocation.longitude,
                                    it.latitude,
                                    it.longitude)
                              }
                              ?.toInt()
                    }
                  }

                  LaunchedEffect(profile.uid) {
                    accountViewModel.fetchUserAccount(profile.uid) { fetchedAccount: Account? ->
                      account = fetchedAccount
                    }
                  }

                  account?.let { acc ->
                    (if (profile.location?.name.isNullOrEmpty()) "Unknown"
                        else profile.location?.name)
                        ?.let {
                          SearchWorkerProfileResult(
                              modifier = Modifier.testTag("worker_profile_result$index"),
                              profileImage = R.drawable.placeholder_worker,
                              name = "${acc.firstName} ${acc.lastName}",
                              category = profile.fieldOfWork,
                              rating = profile.rating,
                              reviewCount = profile.reviews.size,
                              location = it,
                              price = profile.price.toString(),
                              onBookClick = { /* Handle book click in preview */},
                              distance = distance,
                          )
                        }
                  }
                  Spacer(modifier = Modifier.height(3.dp))
                }
              }
            }
      }
}
