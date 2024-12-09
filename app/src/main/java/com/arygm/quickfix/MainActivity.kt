package com.arygm.quickfix

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.switchModes.AppMode
import com.arygm.quickfix.model.switchModes.ModeViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.RootRoute
import com.arygm.quickfix.ui.noModeUI.NoModeNavHost
import com.arygm.quickfix.ui.theme.QuickFixTheme
import com.arygm.quickfix.ui.uiMode.appContentUI.AppContentNavGraph
import com.arygm.quickfix.ui.uiMode.appContentUI.navigation.AppContentRoute
import com.arygm.quickfix.utils.LocationHelper
import com.arygm.quickfix.utils.loadAppMode
import kotlinx.coroutines.delay

val Context.dataStore by preferencesDataStore(name = "quickfix_preferences")

class MainActivity : ComponentActivity() {

  private lateinit var locationHelper: LocationHelper
  private var testBitmapPP = mutableStateOf<Bitmap?>(null)
  private var testLocation = mutableStateOf<Location>(Location())

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    locationHelper = LocationHelper(this, this)

    setContent {
      QuickFixTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          QuickFixApp(testBitmapPP.value, testLocation.value)
        }
      }
    }

    // Check permissions and get location
    if (locationHelper.checkPermissions()) {
      locationHelper.getCurrentLocation { location ->
        location?.let {
          // Handle location (e.g., update UI, save location data)
          Log.d("MainActivity", "Latitude: ${it.latitude}, Longitude: ${it.longitude}")
        }
      }
    } else {
      locationHelper.requestPermissions()
    }
  }

  fun setTestBitmap(bitmap: Bitmap) {
    testBitmapPP.value = bitmap
  }

  fun setTestLocation(location: Location) {
    testLocation.value = location
  }

  override fun onRequestPermissionsResult(
      requestCode: Int,
      permissions: Array<String>,
      grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == LocationHelper.PERMISSION_REQUEST_ACCESS_LOCATION &&
        grantResults.isNotEmpty() &&
        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      locationHelper.getCurrentLocation { location ->
        location?.let {
          Log.d("MainActivity", "Latitude: ${it.latitude}, Longitude: ${it.longitude}")
        }
      }
    }
  }
}

@Composable
fun QuickFixApp(testBitmapPP: Bitmap?, testLocation: Location = Location()) {

  val context = LocalContext.current
  val rootNavController = rememberNavController()
  val navigationActionsRoot = remember { NavigationActions(rootNavController) }

  val modeViewModel: ModeViewModel = viewModel(factory = ModeViewModel.Factory)

  val userViewModel: ProfileViewModel =
      viewModel(key = "userViewModel", factory = ProfileViewModel.UserFactory)

  val accountViewModel: AccountViewModel = viewModel(factory = AccountViewModel.Factory)

  val preferencesViewModel: PreferencesViewModel =
      viewModel(factory = PreferencesViewModel.Factory(LocalContext.current.dataStore))

  var isOffline by remember { mutableStateOf(!isConnectedToInternet(context)) }

  var currentAppMode by remember { mutableStateOf<String?>(null) }
  Log.d("userContent", "Current App Mode is empty: $currentAppMode")
  LaunchedEffect(Unit) {
    Log.d("userContent", "Loading App Mode")
    currentAppMode =
        when (loadAppMode(preferencesViewModel)) {
          "User" -> AppContentRoute.USER_MODE
          "Worker" -> AppContentRoute.WORKER_MODE
          else -> {
            AppContentRoute.USER_MODE
          }
        }
  }

  modeViewModel.switchMode(
      when (currentAppMode) {
        AppContentRoute.USER_MODE -> AppMode.USER
        AppContentRoute.WORKER_MODE -> AppMode.WORKER
        else -> AppMode.USER
      })

  // Simulate monitoring connectivity (replace this with actual monitoring in production)
  LaunchedEffect(Unit) {
    while (true) {
      isOffline = !isConnectedToInternet(context)
      delay(3000) // Poll every 3 seconds
    }
  }
  NavHost(
      navController = rootNavController,
      startDestination = RootRoute.NO_MODE,
      enterTransition = {
        // You can change whatever you want for transitions
        EnterTransition.None
      },
      exitTransition = {
        // You can change whatever you want for transitions
        ExitTransition.None
      }) {
        composable(RootRoute.NO_MODE) {
          NoModeNavHost(
              navigationActionsRoot,
              accountViewModel,
              preferencesViewModel,
              userViewModel,
              isOffline = isOffline)
        }

        composable(RootRoute.APP_CONTENT) {
          AppContentNavGraph(
              testBitmapPP,
              testLocation,
              preferencesViewModel,
              userViewModel,
              accountViewModel,
              isOffline,
              navigationActionsRoot,
              modeViewModel)
        }
      }
}

fun isConnectedToInternet(context: Context): Boolean {
  val connectivityManager =
      context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
  val network = connectivityManager.activeNetwork ?: return false
  val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
  return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}
