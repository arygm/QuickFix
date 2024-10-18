package com.arygm.quickfix.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import junit.framework.TestCase.assertEquals
import org.junit.Test

class BottomNavigationMenuTest {

  @Test
  fun testConvertImageVectorToDrawableId_HomeIcon() {
    // Test Home icon case
    val result = convertImageVectorToDrawableId(Icons.Default.Home)
    assertEquals(com.arygm.quickfix.R.drawable.icon_home_vector, result)
  }

  @Test
  fun testConvertImageVectorToDrawableId_AddCircleIcon() {
    // Test AddCircle icon case
    val result = convertImageVectorToDrawableId(Icons.Default.AddCircle)
    assertEquals(com.arygm.quickfix.R.drawable.icon_annoucement, result)
  }

  @Test
  fun testConvertImageVectorToDrawableId_AccountCircleIcon() {
    // Test AccountCircle icon case
    val result = convertImageVectorToDrawableId(Icons.Default.AccountCircle)
    assertEquals(com.arygm.quickfix.R.drawable.profile, result)
  }

  @Test
  fun testConvertImageVectorToDrawableId_MenuIcon() {
    // Test Menu icon case
    val result = convertImageVectorToDrawableId(Icons.Default.Menu)
    assertEquals(com.arygm.quickfix.R.drawable.dashboard, result)
  }

  @Test
  fun testConvertImageVectorToDrawableId_MoreVertIcon() {
    // Test MoreVert icon case
    val result = convertImageVectorToDrawableId(Icons.Default.MoreVert)
    assertEquals(com.arygm.quickfix.R.drawable.icon_other, result)
  }

  @Test
  fun testConvertImageVectorToDrawableId_SearchIcon() {
    // Test Search icon case
    val result = convertImageVectorToDrawableId(Icons.Default.Search)
    assertEquals(com.arygm.quickfix.R.drawable.logo, result)
  }

  @Test
  fun testConvertImageVectorToDrawableId_DefaultFallback() {
    // Test fallback case for an unsupported icon
    val unsupportedIcon = Icons.Default.Warning // Assuming unsupported icon
    val result = convertImageVectorToDrawableId(unsupportedIcon)
    assertEquals(com.arygm.quickfix.R.drawable.ic_launcher_background, result) // Fallback drawable
  }
}
