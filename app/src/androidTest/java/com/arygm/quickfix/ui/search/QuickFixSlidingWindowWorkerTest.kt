package com.arygm.quickfix.ui.search

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import com.arygm.quickfix.R
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.search.QuickFixSlidingWindowWorker
import org.junit.Rule
import org.junit.Test

class QuickFixSlidingWindowWorkerTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testIncludedServicesAreDisplayed() {
    // Mock data
    val includedServices =
        listOf(
            "Initial Consultation",
            "Basic Surface Preparation",
            "Priming of Surfaces",
            "High-Quality Paint Application",
            "Two Coats of Paint",
            "Professional Cleanup")

    val addonServices =
        listOf(
            "Detailed Color Consultation", "Premium Paint Upgrade", "Extensive Surface Preparation")

    val reviews =
        listOf("Great service!", "Very professional and clean.", "Would highly recommend.")

    composeTestRule.setContent {
      QuickFixSlidingWindowWorker(
          isVisible = true,
          onDismiss = { /* No-op */},
          bannerImage = R.drawable.moroccan_flag,
          profilePicture = R.drawable.placeholder_worker,
          initialSaved = false,
          workerCategory = "Painter",
          workerAddress = "123 Main Street",
          description = "Sample description for the worker.",
          includedServices = includedServices,
          addonServices = addonServices,
          workerRating = 4.5,
          tags = listOf("Exterior Painting", "Interior Painting"),
          reviews = reviews,
          screenHeight = 800.dp,
          screenWidth = 400.dp,
          onContinueClick = { /* No-op */})
    }

    // Verify the included services section is displayed
    composeTestRule
        .onNodeWithTag("sliding_window_included_services_column")
        .assertExists()
        .assertIsDisplayed()

    // Check each included service
    includedServices.forEach { service ->
      composeTestRule.onNodeWithText("• $service").assertExists().assertIsDisplayed()
    }
  }

  @Test
  fun testAddOnServicesAreDisplayed() {
    // Mock data
    val includedServices =
        listOf(
            "Initial Consultation",
            "Basic Surface Preparation",
            "Priming of Surfaces",
            "High-Quality Paint Application",
            "Two Coats of Paint",
            "Professional Cleanup")

    val addOnServices =
        listOf(
            "Detailed Color Consultation",
            "Premium paint Upgrade",
            "Extensive Surface Preparation",
            "Extra Coats for added Durability",
            "Power Washing and Deep Cleaning")

    val reviews =
        listOf("Great service!", "Very professional and clean.", "Would highly recommend.")

    composeTestRule.setContent {
      QuickFixSlidingWindowWorker(
          isVisible = true,
          onDismiss = { /* No-op */},
          bannerImage = R.drawable.moroccan_flag,
          profilePicture = R.drawable.placeholder_worker,
          initialSaved = false,
          workerCategory = "Painter",
          workerAddress = "123 Main Street",
          description = "Sample description for the worker.",
          includedServices = includedServices,
          addonServices = addOnServices,
          workerRating = 4.5,
          tags = listOf("Exterior Painting", "Interior Painting"),
          reviews = reviews,
          screenHeight = 800.dp,
          screenWidth = 400.dp,
          onContinueClick = { /* No-op */})
    }

    // Verify the add-on services section is displayed
    composeTestRule
        .onNodeWithTag("sliding_window_addon_services_column")
        .assertExists()
        .assertIsDisplayed()

    // Check each add-on service
    addOnServices.forEach { service ->
      composeTestRule.onNodeWithText("• $service").assertExists().assertIsDisplayed()
    }
  }

  @Test
  fun testTagsAreDisplayed() {
    // Mock data
    val includedServices =
        listOf(
            "Initial Consultation",
            "Basic Surface Preparation",
            "Priming of Surfaces",
            "High-Quality Paint Application",
            "Two Coats of Paint",
            "Professional Cleanup")

    val addOnServices =
        listOf(
            "Detailed Color Consultation",
            "Premium Paint Upgrade",
            "Extensive Surface Preparation",
            "Extra Coats for Added Durability",
            "Power Washing and Deep Cleaning")

    val tags =
        listOf(
            "Exterior Painting",
            "Interior Painting",
            "Cabinet Painting",
            "Licensed & Insured",
            "Local Worker")

    val reviews =
        listOf("Great service!", "Very professional and clean.", "Would highly recommend.")

    composeTestRule.setContent {
      QuickFixSlidingWindowWorker(
          isVisible = true,
          onDismiss = { /* No-op */},
          bannerImage = R.drawable.moroccan_flag,
          profilePicture = R.drawable.placeholder_worker,
          initialSaved = false,
          workerCategory = "Painter",
          workerAddress = "123 Main Street",
          description = "Sample description for the worker.",
          includedServices = includedServices,
          addonServices = addOnServices,
          workerRating = 4.5,
          tags = tags,
          reviews = reviews,
          screenHeight = 800.dp,
          screenWidth = 400.dp,
          onContinueClick = { /* No-op */})
    }

    // Verify the tags section is displayed
    composeTestRule.onNodeWithTag("sliding_window_tags_flow_row").assertExists().assertIsDisplayed()

    // Check each tag
    tags.forEach { tag -> composeTestRule.onNodeWithText(tag).assertExists().assertIsDisplayed() }
  }
}
