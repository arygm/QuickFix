package com.arygm.quickfix.ui.elements

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arygm.quickfix.MainActivity
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.locations.LocationViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.utils.LocationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSearchCustomScreen(
    navigationActions: NavigationActions,
    locationViewModel: LocationViewModel,
) {
  val locationQuery by locationViewModel.query.collectAsState()
  val locationSuggestions by locationViewModel.locationSuggestions.collectAsState()
  val error by locationViewModel.error.collectAsState()
  val locationHelper: LocationHelper = LocationHelper(LocalContext.current, MainActivity())
  val screenWidth = LocalConfiguration.current.screenWidthDp.dp

  Scaffold(
      topBar = {
        TopAppBar(
            title = {
              Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                QuickFixTextFieldCustom(
                    value = locationQuery,
                    onValueChange = { locationViewModel.setQuery(it) },
                    placeHolderText = "Entrer une adresse ou un lieu",
                    showLeadingIcon = { true },
                    leadingIcon = Icons.Default.ArrowBack,
                    descriptionLeadIcon = "Retour",
                    onTextFieldClick = { navigationActions.goBack() },
                    shape = RoundedCornerShape(20.dp),
                    widthField = screenWidth * 0.9f,
                    heightField = screenWidth * 0.1f,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    textColor = MaterialTheme.colorScheme.onBackground,
                    moveContentHorizontal = 8.dp,
                    moveContentTop = 0.dp,
                    leadIconColor = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.testTag("input_search_field"),
                    clickableTestTag = "input_search_field_clickable")
              }
            },
            colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background))
      },
      content = { padding ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background)) {
              Divider(thickness = 1.dp)

              LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                if (locationSuggestions.isNotEmpty()) {
                  items(locationSuggestions) { location ->
                    Row(
                        modifier =
                            Modifier.clickable {
                                  navigationActions.saveToBackStack("selectedLocation", location)
                                  navigationActions.goBack()
                                }
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                                .testTag("location_suggestion_${location.name}"),
                        verticalAlignment = Alignment.CenterVertically) {
                          MyIcon(screenWidth)

                          Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                            Text(
                                text = location.name,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground)
                          }

                          Icon(
                              imageVector = Icons.Default.ArrowForward,
                              contentDescription = "Naviguer",
                              tint = MaterialTheme.colorScheme.onBackground,
                              modifier = Modifier.size(screenWidth * 0.06f))
                        }
                    Divider(color = MaterialTheme.colorScheme.secondary)
                  }
                } else if (locationQuery.isEmpty()) {
                  item {
                    Row(
                        modifier =
                            Modifier.fillMaxWidth()
                                .clickable {
                                  Log.e("LocationSearch", "Position actuelle: haaaaaaaamid")

                                  locationHelper.getCurrentLocation { currentLocation ->
                                    Log.e(
                                        "LocationSearch",
                                        "kabbour Position actuelle: $currentLocation")

                                    if (currentLocation != null) {
                                      val userLocation =
                                          Location(
                                              name = "Ma position actuelle",
                                              latitude = currentLocation.latitude,
                                              longitude = currentLocation.longitude)
                                      Log.e(
                                          "LocationSearch",
                                          "kchacjcnnv r Position actuelle: $userLocation")

                                      navigationActions.saveToBackStack(
                                          "selectedLocation", userLocation)
                                      navigationActions.goBack()
                                    }
                                  }
                                }
                                .padding(vertical = 12.dp)
                                .testTag("use_current_location"),
                        verticalAlignment = Alignment.CenterVertically) {
                          MyIcon(screenWidth)

                          Text(
                              text = "Utiliser ma position actuelle",
                              style = MaterialTheme.typography.bodyLarge,
                              color = MaterialTheme.colorScheme.onBackground,
                              modifier = Modifier.padding(start = 8.dp))
                        }
                  }
                } else {
                  item {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        contentAlignment = Alignment.Center) {
                          Text(
                              text = "Aucun résultat trouvé",
                              style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
                              color = MaterialTheme.colorScheme.onBackground,
                              modifier = Modifier.testTag("no_results_message"))
                        }
                  }
                }
              }

              if (error != null) {
                Text(
                    text = error!!.message ?: "Une erreur est survenue",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp).testTag("error_message"))
              }
            }
      })
}

@Composable
fun MyIcon(screenWidth: Dp) {
  Box(
      modifier =
          Modifier.size(screenWidth * 0.1f)
              .background(color = MaterialTheme.colorScheme.secondary, shape = CircleShape)
              .padding(8.dp),
      contentAlignment = Alignment.Center) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Localisation",
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(screenWidth * 0.06f))
      }
}
