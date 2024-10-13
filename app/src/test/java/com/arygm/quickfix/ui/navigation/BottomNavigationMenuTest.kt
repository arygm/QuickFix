package com.arygm.quickfix.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Warning
import junit.framework.TestCase.assertEquals
import org.junit.Test

class BottomNavigationMenuTest {

  @Test
  fun testConvertImageVectorToDrawableId_HomeIcon() {
    // Test Home icon case
    val result = convertImageVectorToDrawableId(Icons.Default.Home)
    assertEquals(com.arygm.quickfix.R.drawable.icon_home, result)
  }

  @Test
  fun testConvertImageVectorToDrawableId_AddCircleIcon() {
    // Test AddCircle icon case
    val result = convertImageVectorToDrawableId(Icons.Default.AddCircle)
    assertEquals(com.arygm.quickfix.R.drawable.icon_annoucement, result)
  }

  @Test
  fun testConvertImageVectorToDrawableId_MenuIcon() {
    // Test Menu icon case
    val result = convertImageVectorToDrawableId(Icons.Default.Menu)
    assertEquals(com.arygm.quickfix.R.drawable.icon_menu, result)
  }

  @Test
  fun testConvertImageVectorToDrawableId_MoreVertIcon() {
    // Test MoreVert icon case
    val result = convertImageVectorToDrawableId(Icons.Default.MoreVert)
    assertEquals(com.arygm.quickfix.R.drawable.icon_other, result)
  }

  @Test
  fun testConvertImageVectorToDrawableId_PlaceIcon() {
    // Test Place icon case
    val result = convertImageVectorToDrawableId(Icons.Default.Place)
    assertEquals(com.arygm.quickfix.R.drawable.icon_map, result)
  }

  @Test
  fun testConvertImageVectorToDrawableId_DateRangeIcon() {
    // Test DateRange icon case
    val result = convertImageVectorToDrawableId(Icons.Default.DateRange)
    assertEquals(com.arygm.quickfix.R.drawable.icon_calendar, result)
  }

  @Test
  fun testConvertImageVectorToDrawableId_DefaultFallback() {
    // Test fallback case for an unsupported icon
    val unsupportedIcon = Icons.Default.Warning // Assuming unsupported icon
    val result = convertImageVectorToDrawableId(unsupportedIcon)
    assertEquals(com.arygm.quickfix.R.drawable.ic_launcher_background, result) // Fallback drawable
  }
}
