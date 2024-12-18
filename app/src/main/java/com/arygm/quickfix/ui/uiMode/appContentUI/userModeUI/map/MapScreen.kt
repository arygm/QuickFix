package com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.map

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.window.Popup
import com.arygm.quickfix.MainActivity
import com.arygm.quickfix.R
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.UserProfile
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.quickfix.QuickFixViewModel
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.navigation.UserScreen
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.search.QuickFixSlidingWindowWorker
import com.arygm.quickfix.utils.GeocoderWrapper
import com.arygm.quickfix.utils.LocationHelper
import com.arygm.quickfix.utils.loadUserId
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberMarkerState


@Composable
@GoogleMapComposable
fun MapScreen(workerViewModel: ProfileViewModel, searchViewModel: SearchViewModel, quickFixViewModel: QuickFixViewModel, navigationActions: NavigationActions, geocoderWrapper: GeocoderWrapper = GeocoderWrapper(LocalContext.current)) {

    fun getCityNameFromCoordinates(latitude: Double, longitude: Double): String? {
        val addresses = geocoderWrapper.getFromLocation(latitude, longitude, 1)
        return addresses?.firstOrNull()?.locality
            ?: addresses?.firstOrNull()?.subAdminArea
            ?: addresses?.firstOrNull()?.adminArea
    }

    val context = LocalContext.current
    var isWindowVisible by remember { mutableStateOf(false) }
    var selectedWorker by remember { mutableStateOf<WorkerProfile?>(null) }
    val locationHelper = LocationHelper(context, MainActivity())

    var bannerImage by remember { mutableStateOf(R.drawable.moroccan_flag) }
    var profilePicture by remember { mutableStateOf(R.drawable.placeholder_worker) }
    var initialSaved by remember { mutableStateOf(false) }
    var workerAddress by remember { mutableStateOf("") }


    var phoneLocation by remember {
        mutableStateOf<Location?>(null)
    }
    val cameraPositionState = remember {
        com.google.maps.android.compose.CameraPositionState()
    }
    LaunchedEffect(Unit) {
        if (locationHelper.checkPermissions()) {
            locationHelper.getCurrentLocation { location ->
                if (location != null) {
                    phoneLocation =
                        Location(
                            location.latitude, location.longitude, "Phone Location")
                    cameraPositionState.position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(
                        LatLng(location.latitude, location.longitude),
                        15f
                    )
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
    BoxWithConstraints {
        val widthRatio = maxWidth.value / 411f
        val heightRatio = maxHeight.value / 860f
        val sizeRatio = minOf(widthRatio, heightRatio)
        val screenHeight = maxHeight
        val screenWidth = maxWidth

        GoogleMap(
            modifier = Modifier.fillMaxSize().testTag("mapScreen"),
            cameraPositionState = cameraPositionState,
            onMapLoaded = {  isMapLoaded = true }) {
            filteredWorkers?.forEach { profile ->
                profile.location?.let { val cityName = getCityNameFromCoordinates(it.latitude, profile.location.longitude)
                if(cityName != null){
                    workerAddress = cityName
                }else{
                    workerAddress = profile.location.name
                }}
                profile.location?.let { LatLng(it.latitude, it.longitude) }?.let {
                    rememberMarkerState(
                        position = it
                    )
                }?.let {
                    Marker(
                        state =
                        it,
                        title = profile.displayName,
                        onClick = {
                            selectedWorker = profile
                            isWindowVisible = true
                            true
                        }
                    )
                }

            }
        }

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
                        screenHeight = screenHeight,
                        screenWidth = screenWidth,
                        onContinueClick = {
                            quickFixViewModel.setSelectedWorkerProfile(it)
                            navigationActions.navigateTo(UserScreen.QUICKFIX_ONBOARDING)
                        })
                }
            }
        }
    }
}