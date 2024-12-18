package com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.map

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.UserProfile
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.utils.LocationHelper
import com.arygm.quickfix.utils.loadUserId
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberMarkerState


@Composable
@GoogleMapComposable
fun MapScreen(workerViewModel: ProfileViewModel, searchViewModel: SearchViewModel) {
    val context = LocalContext.current
    val locationHelper = LocationHelper(context, MainActivity())
    var phoneLocation by remember {
        mutableStateOf<Location?>(null)
    }
    LaunchedEffect(Unit) {
        if (locationHelper.checkPermissions()) {
            locationHelper.getCurrentLocation { location ->
                if (location != null) {
                    phoneLocation =
                        Location(
                            location.latitude, location.longitude, "Phone Location")
                } else {
                    Toast.makeText(context, "Unable to fetch location", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "Enable Location In Settings", Toast.LENGTH_SHORT).show()
        }
    }
    val profiles by workerViewModel.profiles.collectAsState()
    val workerProfiles = profiles.filterIsInstance<WorkerProfile>()
    val filteredWorkers by remember {mutableStateOf(phoneLocation?.let {
        searchViewModel.filterWorkersByDistance(workerProfiles, it,30)
    })}
    var isMapLoaded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize().testTag("mapScreen"),
            onMapLoaded = {  isMapLoaded = true }) {
            filteredWorkers?.forEach { profile ->
                profile.location?.let { LatLng(it.latitude, it.longitude) }?.let {
                    rememberMarkerState(
                        position = it
                    )
                }?.let {
                    Marker(
                        state =
                        it,
                        title = profile.displayName
                    )
                }

            }
        }
    }
}