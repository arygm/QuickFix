package com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.search

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.utils.GeocoderWrapper

@Composable
fun ProfileResults(
    modifier: Modifier = Modifier,
    profiles: List<WorkerProfile>,
    searchViewModel: SearchViewModel,
    accountViewModel: AccountViewModel,
    baseLocation: Location,
    profileImagesMap: Map<String, Bitmap?>,
    bannerImagesMap: Map<String, Bitmap?>,
    screenHeight: Dp,
    geocoderWrapper: GeocoderWrapper = GeocoderWrapper(LocalContext.current),
    onBookClick: (WorkerProfile, String, Bitmap, Bitmap) -> Unit,
) {
  fun getCityNameFromCoordinates(latitude: Double, longitude: Double): String? {
    val addresses = geocoderWrapper.getFromLocation(latitude, longitude, 1)
    return addresses?.firstOrNull()?.locality
        ?: addresses?.firstOrNull()?.subAdminArea
        ?: addresses?.firstOrNull()?.adminArea
  }

  LazyColumn(modifier = modifier.fillMaxWidth().testTag("worker_profiles_list")) {
    items(profiles.size) { index ->
      val profile = profiles[index]
      var account by remember { mutableStateOf<Account?>(null) }
      var distance by remember { mutableStateOf<Int?>(null) }
      var cityName by remember { mutableStateOf<String?>(null) }
      val profileImage = profileImagesMap[profile.uid]
      val bannerImage = bannerImagesMap[profile.uid]
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
            profileImage?.let { it2 ->
              SearchWorkerProfileResult(
                  modifier = Modifier.testTag("worker_profile_result$index"),
                  profileImage = it2,
                  name = profile.displayName,
                  category = profile.fieldOfWork,
                  rating = profile.reviews.map { review -> review.rating }.average(),
                  reviewCount = profile.reviews.size,
                  location = it1,
                  price = profile.price.toString(),
                  onBookClick = { onBookClick(profile, it1, it2, bannerImage!!) },
                  distance = distance,
              )
            }
          }
        }
      }
      Spacer(modifier = Modifier.height(screenHeight * 0.004f))
    }
  }
}
