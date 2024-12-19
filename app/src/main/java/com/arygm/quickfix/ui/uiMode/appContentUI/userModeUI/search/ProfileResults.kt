package com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.search

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.platform.testTag
import com.arygm.quickfix.R
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.utils.GeocoderWrapper
import kotlin.math.roundToInt

@Composable
fun ProfileResults(
    modifier: Modifier = Modifier,
    profiles: List<WorkerProfile>,
    searchViewModel: SearchViewModel,
    listState: LazyListState,
    accountViewModel: AccountViewModel,
    geocoderWrapper: GeocoderWrapper = GeocoderWrapper(LocalContext.current),
    onBookClick: (WorkerProfile) -> Unit,
    baseLocation: Location
) {
  fun getCityNameFromCoordinates(latitude: Double, longitude: Double): String? {
    val addresses = geocoderWrapper.getFromLocation(latitude, longitude, 1)
    return addresses?.firstOrNull()?.locality
        ?: addresses?.firstOrNull()?.subAdminArea
        ?: addresses?.firstOrNull()?.adminArea
  }

  LazyColumn(
      modifier =
          modifier
              .fillMaxWidth()
              .nestedScroll(rememberNestedScrollInteropConnection())
              .testTag("worker_profiles_list"),
      state = listState) {
        items(profiles.size) { index ->
          val profile = profiles[index]
          var account by remember { mutableStateOf<Account?>(null) }
          var distance by remember { mutableStateOf<Int?>(null) }
          var cityName by remember { mutableStateOf<String?>(null) }

          distance =
              profile.location
                  ?.let { workerLocation ->
                    searchViewModel.calculateDistance(
                        workerLocation.latitude,
                        workerLocation.longitude,
                        baseLocation.latitude,
                        baseLocation.longitude)
                  }
                  ?.toInt()

          LaunchedEffect(profile.uid) {
            accountViewModel.fetchUserAccount(profile.uid) { fetchedAccount: Account? ->
              account = fetchedAccount
            }
          }

          account?.let { acc ->
            val locationName =
                if (profile.location?.name.isNullOrEmpty()) "Unknown" else profile.location?.name

            locationName?.let {
              cityName =
                  profile.location?.let { it1 ->
                    getCityNameFromCoordinates(it1.latitude, profile.location.longitude)
                  }
              cityName?.let { it1 ->
                SearchWorkerProfileResult(
                    modifier = Modifier.testTag("worker_profile_result$index"),
                    profileImage = R.drawable.placeholder_worker,
                    name = "${acc.firstName} ${acc.lastName}",
                    category = profile.fieldOfWork,
                    rating = profile.reviews.map { review -> review.rating }.average(),
                    reviewCount = profile.reviews.size,
                    location = it1,
                    price = profile.price.roundToInt().toString(),
                    onBookClick = { onBookClick(profile) },
                    distance = distance,
                )
              }
            }
          }
        }
      }
}
