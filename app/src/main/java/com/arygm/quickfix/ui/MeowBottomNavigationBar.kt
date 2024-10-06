package com.arygm.quickfix.ui

import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.graphics.vector.ImageVector
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.arygm.quickfix.R
import com.arygm.quickfix.ui.navigation.TopLevelDestination
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.arygm.quickfix.ui.navigation.USER_TOP_LEVEL_DESTINATIONS
import com.arygm.quickfix.ui.navigation.WORKER_TOP_LEVEL_DESTINATIONS

@Composable
fun MeowBottomNavigationMenu(
    selectedItem: String,                // To track the selected item
    onTabSelect: (TopLevelDestination) -> Unit,
    isUser: Boolean                      // Boolean flag to determine the user type
) {
    // Determine the tab list based on the user type
    val tabList = if (isUser) USER_TOP_LEVEL_DESTINATIONS else WORKER_TOP_LEVEL_DESTINATIONS

    // Use AndroidView to integrate MeowBottomNavigation
    AndroidView(
        factory = { ctx ->
            MeowBottomNavigation(ctx).apply {
                // Add menu items using the tabList
                tabList.forEachIndexed { index, tab ->
                    // Get the drawable resource from the ImageVector
                    val drawableId = convertImageVectorToDrawableId(tab.icon)
                    add(MeowBottomNavigation.Model(index + 1, drawableId))
                }

                // Set design colors
                setCircleColor(Color.parseColor("#731734")) // Central button color
                setBackgroundBottomColor(Color.parseColor("#F16138")) // Orange background color
                setDefaultIconColor(Color.WHITE) // Default icon color (unselected)
                setSelectedIconColor(Color.WHITE) // Selected icon color

                // Define a listener for item show events
                setOnShowListener { model ->
                    // Handle item show event
                    Log.d("MeowBottomNavigation", "Item shown: ${model.id}")
                }
                // Define actions on selecting a menu item
                setOnClickMenuListener { model ->
                    val selectedTab = tabList.getOrNull(model.id - 1) // Find the corresponding tab
                    if (selectedTab != null) {
                        onTabSelect(selectedTab)
                    }
                }
                // Handle reselect event to avoid crash
                setOnReselectListener { model ->
                    // Handle the event when the user clicks on the currently selected item
                    Log.d("MeowBottomNavigation", "Reselected tab id: ${model.id}")
                    // You can add a behavior here or just ignore the reselect event
                }

                // Attempt to show the default selected item
                try {
                    val defaultItemIndex = tabList.indexOfFirst { it.route == selectedItem }
                    show(defaultItemIndex + 1, true)
                } catch (e: Exception) {
                    Log.e("MeowBottomNavigation", "Failed to call show(): ${e.message}")
                }
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

// Helper function to convert ImageVector to drawable resource ID
fun convertImageVectorToDrawableId(imageVector: ImageVector): Int {
    return when (imageVector) {
        Icons.Default.Home -> R.drawable.icon_home
        Icons.Default.AddCircle -> R.drawable.icon_annoucement
        Icons.Default.Menu -> R.drawable.icon_menu
        Icons.Default.MoreVert -> R.drawable.icon_other
        Icons.Default.Place -> R.drawable.icon_map
        Icons.Default.DateRange -> R.drawable.icon_calendar
        else -> R.drawable.ic_launcher_background // Default fallback icon
    }
}