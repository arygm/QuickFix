package com.arygm.quickfix.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.ui.theme.poppinsTypography
import com.arygm.quickfix.utils.LocationHelper

@Composable
fun ProfileResults(
    profiles: List<WorkerProfile>,
    listState: LazyListState,
    searchViewModel: SearchViewModel,
    accountViewModel: AccountViewModel,
    widthRatio: Float,
    heightRatio: Float,
    onBookClick: (WorkerProfile) -> Unit
) {
  // Column for wrapping the list
  Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
    Text(
        text = "Profiles",
        style = poppinsTypography.labelLarge,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(horizontal = 10.dp * widthRatio),
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
                      .testTag("worker_profile_result_$index")
                      .clickable {},
              profileImage = R.drawable.placeholder_worker,
              name = "${acc.firstName} ${acc.lastName}",
              category = profile.fieldOfWork,
              rating = profile.rating,
              reviewCount = profile.reviews.size,
              location = profile.location?.name ?: "Unknown",
              price = profile.price.toString(),
              distance = distance,
              onBookClick = { onBookClick(profile) })
        }

        Spacer(modifier = Modifier.height(10.dp * heightRatio))
      }
    }
  }
}
