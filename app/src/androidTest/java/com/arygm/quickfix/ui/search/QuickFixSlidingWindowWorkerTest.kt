package com.arygm.quickfix.ui.search

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.profile.dataFields.AddOnService
import com.arygm.quickfix.model.profile.dataFields.IncludedService
import com.arygm.quickfix.model.profile.dataFields.Review
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
            .map { IncludedService(it) }

    val addonServices =
        listOf(
                "Detailed Color Consultation",
                "Premium Paint Upgrade",
                "Extensive Surface Preparation")
            .map { AddOnService(it) }

    val reviews =
        ArrayDeque(
            listOf("Great service!", "Very professional and clean.", "Would highly recommend.")
                .map { Review("bob", it, 4.0) })

    composeTestRule.setContent {
      QuickFixSlidingWindowWorker(
          isVisible = true,
          onDismiss = { /* No-op */},
          screenHeight = 800.dp,
          screenWidth = 400.dp,
          onContinueClick = { /* No-op */},
          workerProfile =
              WorkerProfile(
                  includedServices = includedServices,
                  addOnServices = addonServices,
                  reviews = reviews),
      )
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
          workerProfile =
              WorkerProfile(
                  includedServices = includedServices.map { IncludedService(it) },
                  addOnServices = addOnServices.map { AddOnService(it) },
                  reviews = ArrayDeque(reviews.map { Review("bob", it, 4.0) })),
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
          workerProfile =
              WorkerProfile(
                  includedServices = includedServices.map { IncludedService(it) },
                  addOnServices = addOnServices.map { AddOnService(it) },
                  tags = tags,
                  reviews = ArrayDeque(reviews.map { Review("bob", it, 4.0) })),
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
