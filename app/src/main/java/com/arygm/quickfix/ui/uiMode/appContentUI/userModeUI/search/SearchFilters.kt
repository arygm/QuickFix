package com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.theme.poppinsTypography
import java.time.LocalDate

data class SearchFilterButtons(
    val onClick: () -> Unit,
    val text: String,
    val leadingIcon: ImageVector? = null,
    val trailingIcon: ImageVector? = null,
    val applied: Boolean = false
)

data class SearchFiltersState(
    var availabilityFilterApplied: Boolean = false,
    var servicesFilterApplied: Boolean = false,
    var priceFilterApplied: Boolean = false,
    var locationFilterApplied: Boolean = false,
    var ratingFilterApplied: Boolean = false,
    var selectedDays: List<LocalDate> = emptyList(),
    var selectedHour: Int = 0,
    var selectedMinute: Int = 0,
    var selectedServices: List<String> = emptyList(),
    var selectedPriceStart: Int = 0,
    var selectedPriceEnd: Int = 0,
    var selectedLocation: Location = Location(),
    var maxDistance: Int = 0,
    var baseLocation: Location = Location(),
    var phoneLocation: Location = Location(0.0, 0.0, "Default"),
)

@Composable
fun rememberSearchFiltersState(): SearchFiltersState {
  return remember { SearchFiltersState() }
}

/** Applies all active filters to [workerProfiles]. */
fun SearchFiltersState.reapplyFilters(
    workerProfiles: List<WorkerProfile>,
    searchViewModel: SearchViewModel
): List<WorkerProfile> {
  var updatedProfiles = workerProfiles

  if (availabilityFilterApplied) {
    updatedProfiles =
        searchViewModel.filterWorkersByAvailability(
            updatedProfiles, selectedDays, selectedHour, selectedMinute)
  }

  if (servicesFilterApplied) {
    updatedProfiles = searchViewModel.filterWorkersByServices(updatedProfiles, selectedServices)
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

  if (ratingFilterApplied) {
    updatedProfiles = searchViewModel.sortWorkersByRating(updatedProfiles)
  }

  return updatedProfiles
}

/** Clears all active filters and returns the original [workerProfiles]. */
fun SearchFiltersState.clearFilters(workerProfiles: List<WorkerProfile>): List<WorkerProfile> {
  availabilityFilterApplied = false
  priceFilterApplied = false
  locationFilterApplied = false
  ratingFilterApplied = false
  servicesFilterApplied = false
  selectedServices = emptyList()
  baseLocation = phoneLocation
  return workerProfiles
}

/**
 * Creates the list of filter buttons.
 *
 * Each button's action updates the filter state and calls [onProfilesUpdated] to update the
 * displayed profiles.
 */
fun SearchFiltersState.getFilterButtons(
    workerProfiles: List<WorkerProfile>,
    filteredProfiles: List<WorkerProfile>,
    searchViewModel: SearchViewModel,
    onProfilesUpdated: (List<WorkerProfile>) -> Unit,
    onShowAvailabilityBottomSheet: () -> Unit,
    onShowServicesBottomSheet: () -> Unit,
    onShowPriceRangeBottomSheet: () -> Unit,
    onShowLocationBottomSheet: () -> Unit
): List<SearchFilterButtons> {

  return listOf(
      SearchFilterButtons(
          onClick = {
            val cleared = clearFilters(workerProfiles)
            onProfilesUpdated(cleared)
          },
          text = "Clear",
          leadingIcon = Icons.Default.Clear),
      SearchFilterButtons(
          onClick = { onShowLocationBottomSheet() },
          text = "Location",
          leadingIcon = Icons.Default.LocationSearching,
          trailingIcon = Icons.Default.KeyboardArrowDown,
          applied = locationFilterApplied),
      SearchFilterButtons(
          onClick = { onShowServicesBottomSheet() },
          text = "Service Type",
          leadingIcon = Icons.Default.Handyman,
          trailingIcon = Icons.Default.KeyboardArrowDown,
          applied = servicesFilterApplied),
      SearchFilterButtons(
          onClick = { onShowAvailabilityBottomSheet() },
          text = "Availability",
          leadingIcon = Icons.Default.CalendarMonth,
          trailingIcon = Icons.Default.KeyboardArrowDown,
          applied = availabilityFilterApplied),
      SearchFilterButtons(
          onClick = {
            if (ratingFilterApplied) {
              ratingFilterApplied = false
              onProfilesUpdated(reapplyFilters(workerProfiles, searchViewModel))
            } else {
              val rated = searchViewModel.sortWorkersByRating(filteredProfiles)
              ratingFilterApplied = true
              onProfilesUpdated(rated)
            }
          },
          text = "Highest Rating",
          leadingIcon = Icons.Default.WorkspacePremium,
          trailingIcon = if (ratingFilterApplied) Icons.Default.Clear else null,
          applied = ratingFilterApplied),
      SearchFilterButtons(
          onClick = { onShowPriceRangeBottomSheet() },
          text = "Price Range",
          leadingIcon = Icons.Default.MonetizationOn,
          trailingIcon = Icons.Default.KeyboardArrowDown,
          applied = priceFilterApplied))
}

@Composable
fun FilterRow(
    showFilterButtons: Boolean,
    toggleFilterButtons: () -> Unit,
    listOfButtons: List<SearchFilterButtons>,
    modifier: Modifier = Modifier
) {
  val screenHeight = 800.dp // These could be replaced with actual dimension calculations
  val screenWidth = 400.dp

  IconButton(
      onClick = { toggleFilterButtons() },
      modifier = modifier.testTag("tuneButton"),
      colors =
          IconButtonDefaults.iconButtonColors(
              containerColor =
                  if (showFilterButtons) colorScheme.primary else colorScheme.surface)) {
        Icon(
            imageVector = Icons.Default.Tune,
            contentDescription = "Filter",
            tint = if (showFilterButtons) colorScheme.onPrimary else colorScheme.onBackground,
        )
      }

  Spacer(modifier = Modifier.width(10.dp))

  AnimatedVisibility(visible = showFilterButtons) {
    LazyRow(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
      items(listOfButtons.size) { index ->
        val button = listOfButtons[index]
        QuickFixButton(
            buttonText = button.text,
            onClickAction = button.onClick,
            buttonColor = if (button.applied) colorScheme.primary else colorScheme.surface,
            textColor = if (button.applied) colorScheme.onPrimary else colorScheme.onBackground,
            textStyle = poppinsTypography.labelSmall.copy(fontWeight = FontWeight.Medium),
            height = screenHeight * 0.05f,
            leadingIcon = button.leadingIcon,
            trailingIcon = button.trailingIcon,
            leadingIconTint =
                if (button.applied) colorScheme.onPrimary else colorScheme.onBackground,
            trailingIconTint =
                if (button.applied) colorScheme.onPrimary else colorScheme.onBackground,
            contentPadding = PaddingValues(vertical = 0.dp, horizontal = screenWidth * 0.02f),
            modifier = Modifier.testTag("filter_button_${button.text}"))
        Spacer(modifier = Modifier.width(screenHeight * 0.01f))
      }
    }
  }
}
