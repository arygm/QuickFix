package com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.WorkerProfile
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberMarkerState


@Composable
@GoogleMapComposable
fun MapScreen(workerViewModel: ProfileViewModel, accountViewModel: AccountViewModel) {
    val workers by workerViewModel.profiles.collectAsState()
    var isMapLoaded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize().testTag("mapScreen"),
            onMapLoaded = {  isMapLoaded = true }) {
            workers.forEach { profile ->
                val worker = profile as WorkerProfile
                var account: Account? = null
                accountViewModel.fetchUserAccount(worker.uid, onResult = {
                    if (it != null) {
                        account = it
                    }
                })
                worker.location?.let { LatLng(it.latitude, it.longitude) }?.let {
                    rememberMarkerState(
                        position = it
                    )
                }?.let {
                    Marker(
                        state =
                        it,
                        title = account?.firstName + " " + account?.lastName
                    )
                }

            }
        }
    }
}