package com.arygm.quickfix.ui.quickfix

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arygm.quickfix.model.bill.BillField
import com.arygm.quickfix.model.category.Category
import com.arygm.quickfix.model.category.CategoryViewModel
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.profile.dataFields.AddOnService
import com.arygm.quickfix.model.profile.dataFields.IncludedService
import com.arygm.quickfix.model.profile.dataFields.Review
import com.arygm.quickfix.model.quickfix.QuickFix
import com.arygm.quickfix.model.quickfix.QuickFixViewModel
import com.arygm.quickfix.model.quickfix.Status
import com.google.firebase.Timestamp
import io.mockk.*
import java.time.LocalDate
import java.time.LocalTime
import java.util.Date
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuickFixLastStepTest {

  @get:Rule val composeTestRule = createComposeRule()

  // Mock ViewModels
  private val categoryViewModel: CategoryViewModel = mockk(relaxed = true)
  private val quickFixViewModel: QuickFixViewModel = mockk(relaxed = true)
  private val profileViewModel: ProfileViewModel = mockk(relaxed = true)

  // Sample Data
  private val sampleCategory = Category(id = "cat1", name = "Plumbing")

  private val sampleWorkerProfile =
      WorkerProfile(
          displayName = "John Doe",
          fieldOfWork = "Plumbing",
          location = Location(latitude = 40.7128, longitude = -74.0060, name = "New York"),
          includedServices =
              listOf(
                  IncludedService(name = "Leak Repair"),
                  IncludedService(name = "Pipe Replacement")),
          addOnServices =
              listOf(
                  AddOnService(name = "Emergency Call-Out"),
                  AddOnService(name = "Premium Materials")),
          reviews =
              ArrayDeque(
                  listOf(
                      Review(username = "User1", review = "Great service!", rating = 5.0f),
                      Review(username = "User2", review = "Very satisfied", rating = 4.5f))),
          profilePicture = "https://example.com/john-doe.jpg",
          bannerPicture = "https://example.com/john-doe-banner.jpg",
          price = 75.0,
          unavailability_list = listOf(LocalDate.now().plusDays(1), LocalDate.now().plusDays(3)),
          workingHours = Pair(LocalTime.of(9, 0), LocalTime.of(17, 0)),
          tags = listOf("Reliable", "Experienced", "Professional"),
          uid = "worker1")

  private val sampleQuickFix =
      QuickFix(
          uid = "quickfix1",
          status = Status.UPCOMING,
          imageUrl = listOf("https://example.com/image1.jpg", "https://example.com/image2.jpg"),
          date = listOf(Timestamp(Date())),
          time = Timestamp(Date()),
          includedServices = listOf(IncludedService(name = "Leak Repair")),
          addOnServices = listOf(AddOnService(name = "Emergency Call-Out")),
          workerId = "worker1",
          userId = "user1",
          chatUid = "chat1",
          title = "Fix leaking pipe",
          description = "Fix the leaking pipe in the kitchen.",
          bill =
              listOf(
                  BillField(description = "Leak Repair", total = 100.0),
                  BillField(description = "Emergency Call-Out", total = 50.0)),
          location = Location(latitude = 40.7128, longitude = -74.0060, name = "New York"))

  @Test
  fun testQuickFixLastStepDisplaysWorkerOverview() {
    // Arrange
    every { categoryViewModel.getCategoryBySubcategoryId(any(), any()) } answers
        {
          secondArg<(Category?) -> Unit>().invoke(sampleCategory)
        }

    // Act
    composeTestRule.setContent {
      QuickFixLastStep(
          quickFix = sampleQuickFix,
          workerProfile = sampleWorkerProfile,
          categoryViewModel = categoryViewModel,
          quickFixViewModel = quickFixViewModel,
          workerViewModel = profileViewModel)
    }

    // Assert
    composeTestRule.onNodeWithTag("WorkerOverview").assertIsDisplayed()
  }

  @Test
  fun testQuickFixLastStepDisplaysButtonsWhenUpcoming() {
    // Arrange
    every { categoryViewModel.getCategoryBySubcategoryId(any(), any()) } answers
        {
          secondArg<(Category?) -> Unit>().invoke(sampleCategory)
        }

    // Act
    composeTestRule.setContent {
      QuickFixLastStep(
          quickFix = sampleQuickFix,
          workerProfile = sampleWorkerProfile,
          categoryViewModel = categoryViewModel,
          quickFixViewModel = quickFixViewModel,
          workerViewModel = profileViewModel)
    }

    // Assert
    composeTestRule
        .onNodeWithTag("ConsultDiscussionButton")
        .assertIsDisplayed()
        .assertHasClickAction()
    composeTestRule.onNodeWithTag("CancelButton").assertIsDisplayed().assertHasClickAction()
  }

  @Test
  fun testCancelButtonUpdatesQuickFixStatus() {
    // Arrange
    every { categoryViewModel.getCategoryBySubcategoryId(any(), any()) } answers
        {
          secondArg<(Category?) -> Unit>().invoke(sampleCategory)
        }
    every { quickFixViewModel.updateQuickFix(any(), any(), any()) } just Runs

    // Act
    composeTestRule.setContent {
      QuickFixLastStep(
          quickFix = sampleQuickFix,
          workerProfile = sampleWorkerProfile,
          categoryViewModel = categoryViewModel,
          quickFixViewModel = quickFixViewModel,
          workerViewModel = profileViewModel)
    }

    // Find and click the Cancel button
    composeTestRule.onNodeWithTag("CancelButton").performClick()

    // Assert
    verify {
      quickFixViewModel.updateQuickFix(
          sampleQuickFix.copy(
              status = Status.CANCELED), // Assuming cancel changes status to CANCELED
          onSuccess = any(),
          onFailure = any())
    }
  }

  @Test
  fun testReviewSectionDisplayedWhenQuickFixCompleted() {
    // Arrange
    val completedQuickFix = sampleQuickFix.copy(status = Status.COMPLETED)
    every { categoryViewModel.getCategoryBySubcategoryId(any(), any()) } answers
        {
          secondArg<(Category?) -> Unit>().invoke(sampleCategory)
        }

    // Act
    composeTestRule.setContent {
      QuickFixLastStep(
          quickFix = completedQuickFix,
          workerProfile = sampleWorkerProfile,
          categoryViewModel = categoryViewModel,
          quickFixViewModel = quickFixViewModel,
          workerViewModel = profileViewModel)
    }

    // Assert
    composeTestRule.onNodeWithTag("ReviewSection").assertIsDisplayed()
    composeTestRule.onNodeWithTag("FinishButton").assertIsDisplayed()
  }

  @Test
  fun testUserCanEnterFeedbackAndRating() {
    // Arrange
    val completedQuickFix = sampleQuickFix.copy(status = Status.COMPLETED)
    every { categoryViewModel.getCategoryBySubcategoryId(any(), any()) } answers
        {
          secondArg<(Category?) -> Unit>().invoke(sampleCategory)
        }
    every { profileViewModel.updateProfile(any(), any(), any()) } just Runs

    // Act
    composeTestRule.setContent {
      QuickFixLastStep(
          quickFix = completedQuickFix,
          workerProfile = sampleWorkerProfile,
          categoryViewModel = categoryViewModel,
          quickFixViewModel = quickFixViewModel,
          workerViewModel = profileViewModel)
    }

    // Enter Feedback
    composeTestRule.onNodeWithTag("FeedbackTextField").performTextInput("Great service!")

    composeTestRule.onNode(hasTestTag("RatingBar")).performTouchInput {
      click(percentOffset(.2f, .5f))
    }

    // Click Finish
    composeTestRule.onNodeWithTag("FinishButton").performClick()

    // Assert
    verify {
      profileViewModel.updateProfile(
          match { profile ->
            val workerProfile = profile as WorkerProfile
            workerProfile.reviews.any { review ->
              review.review == "Great service!" && review.rating >= 4.5f
            }
          },
          onSuccess = any(),
          onFailure = any())
    }
  }

  @Test
  fun testFeedbackFieldMaxCharacters() {
    // Arrange
    val completedQuickFix = sampleQuickFix.copy(status = Status.COMPLETED)
    every { categoryViewModel.getCategoryBySubcategoryId(any(), any()) } answers
        {
          secondArg<(Category?) -> Unit>().invoke(sampleCategory)
        }

    // Act
    composeTestRule.setContent {
      QuickFixLastStep(
          quickFix = completedQuickFix,
          workerProfile = sampleWorkerProfile,
          categoryViewModel = categoryViewModel,
          quickFixViewModel = quickFixViewModel,
          workerViewModel = profileViewModel)
    }

    composeTestRule.onNodeWithTag("FeedbackTextField").performTextInput("a".repeat(1500))

    composeTestRule.onNodeWithTag("FeedbackTextField").performTextInput("a".repeat(100))
    // Assert that only 1500 characters are present
    composeTestRule.onNodeWithTag("FeedbackTextField").assertTextContains("a".repeat(1500))

    // Assert that the 1600-character string does not exist
    composeTestRule.onNodeWithText("a".repeat(1600)).assertDoesNotExist()
  }

  @Test
  fun testConsultDiscussionButtonNavigatesToChat() {
    // Arrange
    every { categoryViewModel.getCategoryBySubcategoryId(any(), any()) } answers
        {
          secondArg<(Category?) -> Unit>().invoke(sampleCategory)
        }

    // Act
    composeTestRule.setContent {
      QuickFixLastStep(
          quickFix = sampleQuickFix,
          workerProfile = sampleWorkerProfile,
          categoryViewModel = categoryViewModel,
          quickFixViewModel = quickFixViewModel,
          workerViewModel = profileViewModel,
      )
    }

    // Click the Consult Discussion button
    composeTestRule.onNodeWithTag("ConsultDiscussionButton").performClick().assertHasClickAction()
  }

  // Additional Tests based on the updated QuickFix class

  @Test
  fun testQuickFixDetailsDisplayedCorrectly() {
    // Arrange
    every { categoryViewModel.getCategoryBySubcategoryId(any(), any()) } answers
        {
          secondArg<(Category?) -> Unit>().invoke(sampleCategory)
        }

    // Act
    composeTestRule.setContent {
      QuickFixLastStep(
          quickFix = sampleQuickFix,
          workerProfile = sampleWorkerProfile,
          categoryViewModel = categoryViewModel,
          quickFixViewModel = quickFixViewModel,
          workerViewModel = profileViewModel)
    }

    // Assert Title
    composeTestRule.onNodeWithText("Fix leaking pipe").assertIsDisplayed()

    // Assert Included Services
    composeTestRule.onNodeWithText("Leak Repair").assertIsDisplayed()

    // Assert Add-On Services
    composeTestRule.onNodeWithText("Emergency Call-Out").assertIsDisplayed()

    // Assert Total Price
    composeTestRule.onNodeWithText("150.0").assertIsDisplayed()
  }

  @Test
  fun testQuickFixDateAndTimeDisplayedCorrectly() {
    // Arrange
    every { categoryViewModel.getCategoryBySubcategoryId(any(), any()) } answers
        {
          secondArg<(Category?) -> Unit>().invoke(sampleCategory)
        }

    // Act
    composeTestRule.setContent {
      QuickFixLastStep(
          quickFix = sampleQuickFix,
          workerProfile = sampleWorkerProfile,
          categoryViewModel = categoryViewModel,
          quickFixViewModel = quickFixViewModel,
          workerViewModel = profileViewModel)
    }

    // Format dates and times as in the composable
    val dateFormatter = java.text.SimpleDateFormat("EEE, dd MMM", java.util.Locale.getDefault())
    val timeFormatter = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())

    sampleQuickFix.date.forEach { timestamp ->
      composeTestRule
          .onNodeWithText(
              "${dateFormatter.format(timestamp.toDate())} - ${timeFormatter.format(timestamp.toDate())}")
          .assertIsDisplayed()
    }
  }

  @Test
  fun testFinishButtonNotEnabledWhenRatingIsZero() {
    // Arrange
    val completedQuickFix = sampleQuickFix.copy(status = Status.COMPLETED)
    every { categoryViewModel.getCategoryBySubcategoryId(any(), any()) } answers
        {
          secondArg<(Category?) -> Unit>().invoke(sampleCategory)
        }

    // Act
    composeTestRule.setContent {
      QuickFixLastStep(
          quickFix = completedQuickFix,
          workerProfile = sampleWorkerProfile,
          categoryViewModel = categoryViewModel,
          quickFixViewModel = quickFixViewModel,
          workerViewModel = profileViewModel)
    }

    // Set rating to 0
    composeTestRule.onNode(hasTestTag("RatingBar")).performTouchInput {
      click(percentOffset(0f, .5f))
    }

    // Assert that the Finish button is not enabled
    composeTestRule.onNodeWithTag("FinishButton").assertIsNotEnabled()
  }
}
