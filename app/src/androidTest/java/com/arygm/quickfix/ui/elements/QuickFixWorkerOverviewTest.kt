package com.arygm.quickfix.ui.elements

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.profile.dataFields.Review
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuickFixWorkerOverviewTest {

  @get:Rule val composeTestRule = createComposeRule()

  // Sample WorkerProfile data for testing
  private var sampleWorkerProfile =
      WorkerProfile(
          displayName = "Jane Smith",
          fieldOfWork = "Electrician",
          includedServices =
              listOf(
                  // Add included services if needed
                  ),
          addOnServices =
              listOf(
                  // Add add-on services if needed
                  ),
          reviews =
              ArrayDeque(
                  listOf(
                      Review(username = "User1", review = "Excellent work!", rating = 5.0f),
                      Review(username = "User2", review = "Very professional.", rating = 4.5f))),
          profilePicture = "https://example.com/jane-smith.jpg",
          bannerPicture = "https://example.com/jane-smith-banner.jpg",
          price = 80.0,
          unavailability_list =
              listOf(
                  // Add unavailability dates if needed
                  ),
          workingHours = Pair(java.time.LocalTime.of(8, 0), java.time.LocalTime.of(16, 0)),
          tags = listOf("Reliable", "Skilled", "Friendly"),
          uid = "worker123")

  @Test
  fun workerOverview_displaysProfilePicture() {
    // Arrange & Act
    composeTestRule.setContent {
      QuickFixWorkerOverview(workerProfile = sampleWorkerProfile, modifier = Modifier)
    }

    // Assert
    composeTestRule.onNodeWithTag("WorkerProfilePicture_Image").assertIsDisplayed()
  }

  @Test
  fun workerOverview_displaysDisplayName() {
    // Arrange & Act
    composeTestRule.setContent {
      QuickFixWorkerOverview(workerProfile = sampleWorkerProfile, modifier = Modifier)
    }

    // Assert
    composeTestRule
        .onNodeWithTag("WorkerDisplayName_Text")
        .assertIsDisplayed()
        .assertTextEquals("Jane Smith")
  }

  @Test
  fun workerOverview_displaysRating() {
    // Arrange & Act
    composeTestRule.setContent {
      QuickFixWorkerOverview(workerProfile = sampleWorkerProfile, modifier = Modifier)
    }

    // Assert
    composeTestRule
        .onNodeWithTag("WorkerRating_Text")
        .assertIsDisplayed()
        .assertTextEquals("4.75 ★") // Average of 5.0 and 4.5 is 4.75
  }

  @Test
  fun workerOverview_displaysReviewsCount() {
    // Arrange & Act
    composeTestRule.setContent {
      QuickFixWorkerOverview(workerProfile = sampleWorkerProfile, modifier = Modifier)
    }

    // Assert
    composeTestRule
        .onNodeWithTag("WorkerReviewsCount_Text")
        .assertIsDisplayed()
        .assertTextEquals("(2+)")
  }

  @Test
  fun workerOverview_displaysLocation() {
    val workerProfile =
        WorkerProfile(
            sampleWorkerProfile.displayName,
            location = Location(latitude = 34.0522, longitude = -118.2437, name = "Los Angeles"),
        )
    // Arrange & Act
    composeTestRule.setContent {
      QuickFixWorkerOverview(workerProfile = workerProfile, modifier = Modifier)
    }

    // Assert
    composeTestRule
        .onNodeWithTag("WorkerLocation_Text")
        .assertIsDisplayed()
        .assertTextEquals("Los Angeles")
  }

  @Test
  fun workerOverview_displaysFieldOfWork() {
    // Arrange & Act
    composeTestRule.setContent {
      QuickFixWorkerOverview(workerProfile = sampleWorkerProfile, modifier = Modifier)
    }

    // Assert
    composeTestRule
        .onNodeWithTag("WorkerFieldOfWork_Text")
        .assertIsDisplayed()
        .assertTextEquals("Electrician")
  }

  @Test
  fun workerOverview_layout_isCorrectlyArranged() {
    // Arrange & Act
    composeTestRule.setContent {
      QuickFixWorkerOverview(workerProfile = sampleWorkerProfile, modifier = Modifier)
    }

    // Assert: Ensure that all major components are present
    composeTestRule.onNodeWithTag("WorkerProfilePicture_Column").assertIsDisplayed()
    composeTestRule.onNodeWithTag("WorkerDetails_Column").assertIsDisplayed()
    composeTestRule.onNodeWithTag("WorkerLocationFieldWork_Column").assertIsDisplayed()
  }

  @Test
  fun workerOverview_handlesUnknownLocation() {
    // Act
    composeTestRule.setContent {
      QuickFixWorkerOverview(workerProfile = sampleWorkerProfile, modifier = Modifier)
    }

    // Assert
    composeTestRule
        .onNodeWithTag("WorkerLocation_Text")
        .assertIsDisplayed()
        .assertTextEquals("Unknown Location")
  }

  @Test
  fun workerOverview_displaysNoReviewsCorrectly() {
    // Arrange
    val workerProfileNoReviews =
        WorkerProfile(
            displayName = "John Doe",
            fieldOfWork = "Plumber",
            reviews = ArrayDeque(),
        )
    // Act
    composeTestRule.setContent {
      QuickFixWorkerOverview(workerProfile = workerProfileNoReviews, modifier = Modifier)
    }

    // Assert
    composeTestRule
        .onNodeWithTag("WorkerRating_Text")
        .assertIsDisplayed()
        .assertTextEquals("No rating") // Average of no reviews can be handled as 0.0
    composeTestRule
        .onNodeWithTag("WorkerReviewsCount_Text")
        .assertIsDisplayed()
        .assertTextEquals("(0+)")
  }
}
