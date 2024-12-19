package com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.search

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.arygm.quickfix.MainActivity
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.utils.GeocoderWrapper
import com.arygm.quickfix.utils.LocationHelper
import kotlin.math.roundToInt

@Composable
fun ProfileResults(
    modifier: Modifier = Modifier,
    profiles: List<WorkerProfile>,
    listState: LazyListState,
    searchViewModel: SearchViewModel,
    accountViewModel: AccountViewModel,
    heightRatio: Float,
    workerViewModel: ProfileViewModel,
    geocoderWrapper: GeocoderWrapper = GeocoderWrapper(LocalContext.current),
    onBookClick: (WorkerProfile, String) -> Unit
) {
    fun getCityNameFromCoordinates(latitude: Double, longitude: Double): String? {
        val addresses = geocoderWrapper.getFromLocation(latitude, longitude, 1)
        return addresses?.firstOrNull()?.locality
            ?: addresses?.firstOrNull()?.subAdminArea
            ?: addresses?.firstOrNull()?.adminArea
    }

    val context = LocalContext.current
    val locationHelper = remember { LocationHelper(context, MainActivity()) }

    LazyColumn(modifier = modifier.fillMaxWidth(), state = listState) {
        items(profiles.size) { index ->
            val profile = profiles[index]

            var account by remember { mutableStateOf<Account?>(null) }
            var distance by remember { mutableStateOf<Int?>(null) }
            var profileImage by remember { mutableStateOf<Bitmap?>(null) }
            var cityName by remember { mutableStateOf<String?>(null) }

            // Fetch data once using LaunchedEffect, keyed by profile.uid
            LaunchedEffect(profile.uid) {
                // Fetch profile image once
                workerViewModel.fetchProfileImageAsBitmap(
                    profile.uid,
                    onSuccess = { profileImage = it },
                    onFailure = { Log.e("ProfileResults", "Failed to fetch profile image: $it") }
                )

                // Fetch account data once
                accountViewModel.fetchUserAccount(profile.uid) { fetchedAccount ->
                    account = fetchedAccount
                }

                // Get current location once
                locationHelper.getCurrentLocation { location ->
                    location?.let {
                        distance = profile.location?.let { workerLocation ->
                            searchViewModel.calculateDistance(
                                workerLocation.latitude,
                                workerLocation.longitude,
                                it.latitude,
                                it.longitude
                            ).toInt()
                        }
                    }

                    // Compute city name once
                    cityName = profile.location?.let { loc ->
                        getCityNameFromCoordinates(loc.latitude, loc.longitude)
                    } ?: "Unknown"
                }
            }

            // Only show the result once all required data is available
            val displayLoc = cityName ?: "Unknown"
            if (account != null && profileImage != null) {
                SearchWorkerProfileResult(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("worker_profile_result_$index")
                        .clickable { },
                    profileImage = profileImage!!,
                    name = "${account!!.firstName} ${account!!.lastName}",
                    category = profile.fieldOfWork,
                    rating = profile.rating,
                    reviewCount = profile.reviews.size,
                    location = displayLoc,
                    price = profile.price.roundToInt().toString(),
                    distance = distance,
                    onBookClick = { onBookClick(profile, displayLoc) }
                )
            }
        }
    }
}