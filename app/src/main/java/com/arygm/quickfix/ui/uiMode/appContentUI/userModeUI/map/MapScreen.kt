package com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.map

import QuickFixToolboxFloatingButton
import android.graphics.Bitmap
import android.graphics.Canvas
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.arygm.quickfix.MainActivity
import com.arygm.quickfix.R
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.UserProfile
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.quickfix.QuickFixViewModel
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.ui.elements.QuickFixAvailabilityBottomSheet
import com.arygm.quickfix.ui.elements.QuickFixLocationFilterBottomSheet
import com.arygm.quickfix.ui.elements.QuickFixPriceRangeBottomSheet
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.navigation.UserScreen
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.search.QuickFixSlidingWindowWorker
import com.arygm.quickfix.utils.GeocoderWrapper
import com.arygm.quickfix.utils.LocationHelper
import com.arygm.quickfix.utils.loadUserId
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberMarkerState
import java.time.LocalDate

@Composable
@GoogleMapComposable
fun MapScreen(
    workerViewModel: ProfileViewModel,
    searchViewModel: SearchViewModel,
    quickFixViewModel: QuickFixViewModel,
    navigationActions: NavigationActions,
    userProfileViewModel: ProfileViewModel,
    preferencesViewModel: PreferencesViewModel,
    geocoderWrapper: GeocoderWrapper = GeocoderWrapper(LocalContext.current)
) {

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

  var availabilityFilterApplied by remember { mutableStateOf(false) }
  var priceFilterApplied by remember { mutableStateOf(false) }
  var locationFilterApplied by remember { mutableStateOf(false) }
  var emergencyFilterApplied by remember { mutableStateOf(false) }

  var showAvailabilityBottomSheet by remember { mutableStateOf(false) }
  var showPriceRangeBottomSheet by remember { mutableStateOf(false) }
  var showLocationBottomSheet by remember { mutableStateOf(false) }

  var selectedDays by remember { mutableStateOf(emptyList<LocalDate>()) }
  var selectedHour by remember { mutableStateOf(0) }
  var selectedMinute by remember { mutableStateOf(0) }
  var selectedPriceStart by remember { mutableStateOf(0) }
  var selectedPriceEnd by remember { mutableStateOf(0) }
  var selectedLocation by remember { mutableStateOf(Location()) }
  var maxDistance by remember { mutableStateOf(0) }
  var selectedLocationIndex by remember { mutableStateOf<Int?>(null) }

  var lastAppliedPriceStart by remember { mutableStateOf(500) }
  var lastAppliedPriceEnd by remember { mutableStateOf(2500) }
  var lastAppliedMaxDist by remember { mutableStateOf(200) }

  var phoneLocation by remember { mutableStateOf<Location?>(null) }
  val cameraPositionState = remember { com.google.maps.android.compose.CameraPositionState() }

  var userProfile by remember { mutableStateOf<UserProfile?>(null) }
  var uid by remember { mutableStateOf("Loading...") }

  LaunchedEffect(Unit) {
    if (locationHelper.checkPermissions()) {
      locationHelper.getCurrentLocation { location ->
        if (location != null) {
          phoneLocation = Location(location.latitude, location.longitude, "Phone Location")
          cameraPositionState.position =
              com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(
                  LatLng(location.latitude, location.longitude), 5f)
        } else {
          Toast.makeText(context, "Unable to fetch location", Toast.LENGTH_SHORT).show()
        }
      }
    } else {
      Toast.makeText(context, "Enable Location In Settings", Toast.LENGTH_SHORT).show()
    }
    uid = loadUserId(preferencesViewModel)
    userProfileViewModel.fetchUserProfile(uid) { profile -> userProfile = profile as UserProfile }
  }
  var baseLocation by remember { mutableStateOf(phoneLocation) }
  val profiles by workerViewModel.profiles.collectAsState()
  val workerProfiles = profiles.filterIsInstance<WorkerProfile>()
  var filteredWorkers by remember { mutableStateOf(workerProfiles) }
  var isMapLoaded by remember { mutableStateOf(false) }

  fun reapplyFilters() {
    var updatedProfiles = workerProfiles

    if (availabilityFilterApplied) {
      updatedProfiles =
          searchViewModel.filterWorkersByAvailability(
              updatedProfiles, selectedDays, selectedHour, selectedMinute)
    }

    if (priceFilterApplied) {
      updatedProfiles =
          searchViewModel.filterWorkersByPriceRange(
              updatedProfiles, selectedPriceStart, selectedPriceEnd)
    }

    if (locationFilterApplied) {
      updatedProfiles =
          searchViewModel.filterWorkersByDistance(updatedProfiles, selectedLocation, maxDistance)
    }

    if (emergencyFilterApplied) {
      updatedProfiles = searchViewModel.emergencyFilter(updatedProfiles, baseLocation!!)
    }

    filteredWorkers = updatedProfiles
  }

  BoxWithConstraints {
    val widthRatio = maxWidth.value / 411f
    val heightRatio = maxHeight.value / 860f
    val sizeRatio = minOf(widthRatio, heightRatio)
    val screenHeight = maxHeight
    val screenWidth = maxWidth

    GoogleMap(
        modifier = Modifier.fillMaxSize().testTag("mapScreen"),
        cameraPositionState = cameraPositionState,
        onMapLoaded = { isMapLoaded = true }) {
          filteredWorkers.forEach { profile ->
            profile.location?.let {
              val cityName = getCityNameFromCoordinates(it.latitude, profile.location.longitude)
              if (cityName != null) {
                workerAddress = cityName
              } else {
                workerAddress = profile.location.name
              }
            }
            profile.location
                ?.let { LatLng(it.latitude, it.longitude) }
                ?.let { rememberMarkerState(position = it) }
                ?.let {
                  Marker(
                      state = it,
                      title = profile.displayName,
                      onClick = {
                        selectedWorker = profile
                        isWindowVisible = true
                        true
                      },
                      icon =
                          customMarkerBitmapDescriptor(
                              iconRes = R.drawable.accountsettingsvector,
                              iconSizeDp = 24,
                              iconTintColor = android.graphics.Color.parseColor("#66001A"),
                              backgroundColor = android.graphics.Color.TRANSPARENT,
                              backgroundSizeDp = 48))
                }
          }
        }
    val maxHeightValue = maxHeight.value
    Box(modifier = Modifier.padding(16.dp)) {
      Surface(
          shape = RoundedCornerShape(20.dp),
          modifier =
              Modifier.size((maxHeightValue * 0.07).dp)
                  .clickable { navigationActions.goBack() }
                  .testTag("HomeButton"),
          color = colorScheme.surface,
          shadowElevation = 5.dp) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Home Icon",
                modifier = Modifier.padding((maxHeightValue * 0.01).dp),
                tint = colorScheme.primary,
            )
          }
    }

    if (isWindowVisible) {
      Popup(onDismissRequest = { isWindowVisible = false }, alignment = Alignment.Center) {
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

    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.BottomStart,
    ) {
      QuickFixToolboxFloatingButton(
          modifier = Modifier.testTag("FilterButton"),
          mainIcon = Icons.Default.FilterAlt,
          iconList =
              listOf(
                  Icons.Default.Clear,
                  Icons.Default.LocationSearching,
                  Icons.Default.MonetizationOn,
                  Icons.Default.CalendarMonth,
                  Icons.Default.Warning),
          onIconClick = { index ->
            when (index) {
              0 -> {
                filteredWorkers = workerProfiles
                availabilityFilterApplied = false
                priceFilterApplied = false
                locationFilterApplied = false
                emergencyFilterApplied = false
                lastAppliedMaxDist = 200
                lastAppliedPriceStart = 500
                lastAppliedPriceEnd = 2500
                selectedLocationIndex = null
                baseLocation = phoneLocation
              }
              1 -> showLocationBottomSheet = true
              2 -> showPriceRangeBottomSheet = true
              3 -> showAvailabilityBottomSheet = true
              4 -> {
                if (emergencyFilterApplied) {
                  emergencyFilterApplied = false
                  reapplyFilters()
                } else {
                  lastAppliedMaxDist = 200
                  lastAppliedPriceStart = 500
                  lastAppliedPriceEnd = 2500
                  selectedLocationIndex = null
                  availabilityFilterApplied = false
                  priceFilterApplied = false
                  locationFilterApplied = false
                  baseLocation = phoneLocation
                  filteredWorkers = workerProfiles
                  filteredWorkers =
                      baseLocation?.let { searchViewModel.emergencyFilter(filteredWorkers, it) }!!
                  emergencyFilterApplied = true
                }
              }
            }
          })
    }

    QuickFixAvailabilityBottomSheet(
        showAvailabilityBottomSheet,
        onDismissRequest = { showAvailabilityBottomSheet = false },
        onOkClick = { days, hour, minute ->
          selectedDays = days
          selectedHour = hour
          selectedMinute = minute
          if (availabilityFilterApplied) {
            reapplyFilters()
          } else {
            filteredWorkers =
                searchViewModel.filterWorkersByAvailability(filteredWorkers, days, hour, minute)
          }
          availabilityFilterApplied = true
        },
        onClearClick = {
          availabilityFilterApplied = false
          selectedDays = emptyList()
          selectedHour = 0
          selectedMinute = 0
          reapplyFilters()
        },
        clearEnabled = availabilityFilterApplied)

    QuickFixPriceRangeBottomSheet(
        showPriceRangeBottomSheet,
        onApplyClick = { start, end ->
          selectedPriceStart = start
          selectedPriceEnd = end
          lastAppliedPriceStart = start
          lastAppliedPriceEnd = end
          if (priceFilterApplied) {
            reapplyFilters()
          } else {
            filteredWorkers = searchViewModel.filterWorkersByPriceRange(filteredWorkers, start, end)
          }
          priceFilterApplied = true
        },
        onDismissRequest = { showPriceRangeBottomSheet = false },
        onClearClick = {
          selectedPriceStart = 0
          selectedPriceEnd = 0
          lastAppliedPriceStart = 500
          lastAppliedPriceEnd = 2500
          priceFilterApplied = false
          reapplyFilters()
        },
        clearEnabled = priceFilterApplied,
        start = lastAppliedPriceStart,
        end = lastAppliedPriceEnd)

    userProfile?.let {
      phoneLocation?.let { it1 ->
        QuickFixLocationFilterBottomSheet(
            showLocationBottomSheet,
            profile = it,
            phoneLocation = it1,
            selectedLocationIndex = selectedLocationIndex,
            onApplyClick = { location, max ->
              selectedLocation = location
              lastAppliedMaxDist = max
              baseLocation = location
              maxDistance = max
              selectedLocationIndex = userProfile!!.locations.indexOf(location) + 1

              if (location == com.arygm.quickfix.model.locations.Location(0.0, 0.0, "Default")) {
                Toast.makeText(context, "Enable Location In Settings", Toast.LENGTH_SHORT).show()
              }
              if (locationFilterApplied) {
                reapplyFilters()
              } else {
                filteredWorkers =
                    searchViewModel.filterWorkersByDistance(filteredWorkers, location, max)
              }
              cameraPositionState.position =
                  com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(
                      LatLng(baseLocation!!.latitude, baseLocation!!.longitude), 5f)
              locationFilterApplied = true
            },
            onDismissRequest = { showLocationBottomSheet = false },
            onClearClick = {
              baseLocation = phoneLocation
              lastAppliedMaxDist = 200
              selectedLocation = com.arygm.quickfix.model.locations.Location()
              maxDistance = 0
              selectedLocationIndex = null
              locationFilterApplied = false
              cameraPositionState.position =
                  com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(
                      LatLng(phoneLocation!!.latitude, phoneLocation!!.longitude), 5f)
              reapplyFilters()
            },
            clearEnabled = locationFilterApplied,
            end = lastAppliedMaxDist)
      }
    }
  }
}

@Composable
fun customMarkerBitmapDescriptor(
    @DrawableRes iconRes: Int,
    iconSizeDp: Int = 24,
    iconTintColor: Int? = null,
    backgroundColor: Int? = null,
    backgroundSizeDp: Int = 48 // Adjust this if you need a bigger background
): BitmapDescriptor {
  val context = LocalContext.current
  val drawable =
      AppCompatResources.getDrawable(context, iconRes)
          ?: return BitmapDescriptorFactory.defaultMarker()

  val density = context.resources.displayMetrics.density
  val iconSizePx = (iconSizeDp * density).toInt()
  val bgSizePx = (backgroundSizeDp * density).toInt()

  // Tint the icon if a color is provided
  if (iconTintColor != null) {
    drawable.mutate()
    drawable.setTint(iconTintColor)
  }

  val bitmap = Bitmap.createBitmap(bgSizePx, bgSizePx, Bitmap.Config.ARGB_8888)
  val canvas = Canvas(bitmap)

  // Draw a background shape (circle) if a backgroundColor is provided
  backgroundColor?.let { bgColor ->
    val paint =
        android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
          color = bgColor
          style = android.graphics.Paint.Style.FILL
        }
    val radius = bgSizePx / 2f
    canvas.drawCircle(radius, radius, radius, paint)
  }

  // Center the icon drawable on the background
  val left = (bgSizePx - iconSizePx) / 2
  val top = (bgSizePx - iconSizePx) / 2
  drawable.setBounds(left, top, left + iconSizePx, top + iconSizePx)
  drawable.draw(canvas)

  return BitmapDescriptorFactory.fromBitmap(bitmap)
}
