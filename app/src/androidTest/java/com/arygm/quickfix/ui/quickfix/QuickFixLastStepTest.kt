package com.arygm.quickfix.ui.quickfix

import android.graphics.Bitmap
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
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
import com.arygm.quickfix.model.quickfix.QuickFixRepository
import com.arygm.quickfix.model.quickfix.QuickFixViewModel
import com.arygm.quickfix.model.quickfix.Status
import com.arygm.quickfix.model.switchModes.AppMode
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.navigation.USER_TOP_LEVEL_DESTINATIONS
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.quickfix.QuickFixLastStep
import com.google.firebase.Timestamp
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDate
import java.time.LocalTime
import java.util.Date
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class QuickFixLastStepTest {

  @get:Rule val composeTestRule = createComposeRule()

  // Mock ViewModels
  private val categoryViewModel: CategoryViewModel = mockk(relaxed = true)
  private lateinit var quickFixRepository: QuickFixRepository
  private lateinit var quickFixViewModel: QuickFixViewModel
  private val profileViewModel: ProfileViewModel = mockk(relaxed = true)
  private val navigationActions: NavigationActions = mockk(relaxed = true)

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
                      Review(username = "User1", review = "Great service!", rating = 5.0),
                      Review(username = "User2", review = "Very satisfied", rating = 4.5))),
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

  @Before
  fun setUp() {
    quickFixRepository = mock(QuickFixRepository::class.java)

    // Mock the QuickFixViewModel
    quickFixViewModel = QuickFixViewModel(quickFixRepository)
    doAnswer { invocation ->
          val onSuccess = invocation.arguments[1] as (List<Pair<String, Bitmap>>) -> Unit
          onSuccess(
              listOf(
                  Pair(
                      "https://example.com/image1.jpg",
                      Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888))))
        }
        .whenever(quickFixRepository)
        .fetchQuickFixAsBitmaps(any(), any(), any())

    doAnswer { invocation ->
          val onSuccess = invocation.arguments[1] as () -> Unit
          onSuccess
        }
        .whenever(quickFixRepository)
        .updateQuickFix(any(), any(), any())
  }

  @Test
  fun testQuickFixLastStepDisplaysWorkerOverview() {
    // Arrange
    every { runBlocking { categoryViewModel.getCategoryBySubcategoryId(any(), any()) } } answers
        {
          secondArg<(Category?) -> Unit>().invoke(sampleCategory)
        }
    quickFixViewModel.setUpdateQuickFix(sampleQuickFix)
    // Act
    composeTestRule.setContent {
      QuickFixLastStep(
          workerProfile = sampleWorkerProfile,
          categoryViewModel = categoryViewModel,
          quickFixViewModel = quickFixViewModel,
          workerViewModel = profileViewModel,
          onQuickFixChange = { _ -> },
          mode = AppMode.USER,
          navigationActionsRoot = navigationActions)
    }

    // Assert
    composeTestRule.onNodeWithTag("WorkerOverview").assertIsDisplayed()
  }

  @Test
  fun testQuickFixLastStepDisplaysButtonsWhenUpcoming() {
    // Arrange
    every { runBlocking { categoryViewModel.getCategoryBySubcategoryId(any(), any()) } } answers
        {
          secondArg<(Category?) -> Unit>().invoke(sampleCategory)
        }
    quickFixViewModel.setUpdateQuickFix(sampleQuickFix)

    // Act
    composeTestRule.setContent {
      QuickFixLastStep(
          workerProfile = sampleWorkerProfile,
          categoryViewModel = categoryViewModel,
          quickFixViewModel = quickFixViewModel,
          workerViewModel = profileViewModel,
          onQuickFixChange = { _ -> },
          mode = AppMode.USER,
          navigationActionsRoot = navigationActions)
    }

    // Assert
    composeTestRule
        .onNodeWithTag("ConsultDiscussionButton")
        .assertIsDisplayed()
        .assertHasClickAction()
    composeTestRule.onNodeWithTag("CancelButton").assertIsDisplayed().assertHasClickAction()
  }

  @Test
  fun testQuickFixLastStepDisplaysGoBackButton() {
    // Arrange
    every { runBlocking { categoryViewModel.getCategoryBySubcategoryId(any(), any()) } } answers
        {
          secondArg<(Category?) -> Unit>().invoke(sampleCategory)
        }
    quickFixViewModel.setUpdateQuickFix(sampleQuickFix)

    // Act
    composeTestRule.setContent {
      QuickFixLastStep(
          workerProfile = sampleWorkerProfile,
          categoryViewModel = categoryViewModel,
          quickFixViewModel = quickFixViewModel,
          workerViewModel = profileViewModel,
          onQuickFixChange = { _ -> },
          mode = AppMode.USER,
          navigationActionsRoot = navigationActions)
    }

    composeTestRule.onNodeWithTag("QuickFixLastStep_LazyColumn").performScrollToIndex(3)

    // Assert
    composeTestRule
        .onNodeWithTag("GoBackHomeButton")
        .assertIsDisplayed()
        .assertHasClickAction()
        .performClick()

    verify { navigationActions.navigateTo(USER_TOP_LEVEL_DESTINATIONS[0].route) }
  }

  @Test
  fun testCancelButtonUpdatesQuickFixStatus() {
    // Arrange
    every { runBlocking { categoryViewModel.getCategoryBySubcategoryId(any(), any()) } } answers
        {
          secondArg<(Category?) -> Unit>().invoke(sampleCategory)
        }

    quickFixViewModel.setUpdateQuickFix(sampleQuickFix)
    // Act
    composeTestRule.setContent {
      QuickFixLastStep(
          workerProfile = sampleWorkerProfile,
          categoryViewModel = categoryViewModel,
          quickFixViewModel = quickFixViewModel,
          workerViewModel = profileViewModel,
          onQuickFixChange = { _ -> },
          mode = AppMode.USER,
          navigationActionsRoot = navigationActions)
    }

    // Find and click the Cancel button
    composeTestRule.onNodeWithTag("CancelButton").performClick()
  }

  @Test
  fun testReviewSectionDisplayedWhenQuickFixCompleted() {
    // Arrange
    val completedQuickFix = sampleQuickFix.copy(status = Status.COMPLETED)
    every { runBlocking { categoryViewModel.getCategoryBySubcategoryId(any(), any()) } } answers
        {
          secondArg<(Category?) -> Unit>().invoke(sampleCategory)
        }

    quickFixViewModel.setUpdateQuickFix(completedQuickFix)

    // Act
    composeTestRule.setContent {
      QuickFixLastStep(
          workerProfile = sampleWorkerProfile,
          categoryViewModel = categoryViewModel,
          quickFixViewModel = quickFixViewModel,
          workerViewModel = profileViewModel,
          onQuickFixChange = { _ -> },
          mode = AppMode.USER,
          navigationActionsRoot = navigationActions)
    }

    // Assert
    composeTestRule.onNodeWithTag("ReviewSection").assertIsDisplayed()
    composeTestRule.onNodeWithTag("FinishButton").assertIsDisplayed()
  }

  @Test
  fun testUserCanEnterFeedbackAndRating() {
    // Arrange
    val completedQuickFix = sampleQuickFix.copy(status = Status.COMPLETED)
    every { runBlocking { categoryViewModel.getCategoryBySubcategoryId(any(), any()) } } answers
        {
          secondArg<(Category?) -> Unit>().invoke(sampleCategory)
        }
    every { profileViewModel.updateProfile(any(), any(), any()) } just Runs

    quickFixViewModel.setUpdateQuickFix(completedQuickFix)

    // Act
    // Act
    composeTestRule.setContent {
      QuickFixLastStep(
          workerProfile = sampleWorkerProfile,
          categoryViewModel = categoryViewModel,
          quickFixViewModel = quickFixViewModel,
          workerViewModel = profileViewModel,
          onQuickFixChange = { _ -> },
          mode = AppMode.USER,
          navigationActionsRoot = navigationActions)
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
    every { runBlocking { categoryViewModel.getCategoryBySubcategoryId(any(), any()) } } answers
        {
          secondArg<(Category?) -> Unit>().invoke(sampleCategory)
        }

    quickFixViewModel.setUpdateQuickFix(completedQuickFix)
    // Act
    composeTestRule.setContent {
      QuickFixLastStep(
          workerProfile = sampleWorkerProfile,
          categoryViewModel = categoryViewModel,
          quickFixViewModel = quickFixViewModel,
          workerViewModel = profileViewModel,
          onQuickFixChange = { _ -> },
          mode = AppMode.USER,
          navigationActionsRoot = navigationActions)
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
    every { runBlocking { categoryViewModel.getCategoryBySubcategoryId(any(), any()) } } answers
        {
          secondArg<(Category?) -> Unit>().invoke(sampleCategory)
        }

    quickFixViewModel.setUpdateQuickFix(sampleQuickFix)

    // Act
    composeTestRule.setContent {
      QuickFixLastStep(
          workerProfile = sampleWorkerProfile,
          categoryViewModel = categoryViewModel,
          quickFixViewModel = quickFixViewModel,
          workerViewModel = profileViewModel,
          onQuickFixChange = { _ -> },
          mode = AppMode.USER,
          navigationActionsRoot = navigationActions)
    }

    // Click the Consult Discussion button
    composeTestRule.onNodeWithTag("ConsultDiscussionButton").performClick().assertHasClickAction()
  }

  // Additional Tests based on the updated QuickFix class

  @Test
  fun testQuickFixDetailsDisplayedCorrectly() {
    // Arrange
    every { runBlocking { categoryViewModel.getCategoryBySubcategoryId(any(), any()) } } answers
        {
          secondArg<(Category?) -> Unit>().invoke(sampleCategory)
        }

    quickFixViewModel.setUpdateQuickFix(sampleQuickFix)
    // Act
    composeTestRule.setContent {
      QuickFixLastStep(
          workerProfile = sampleWorkerProfile,
          categoryViewModel = categoryViewModel,
          quickFixViewModel = quickFixViewModel,
          workerViewModel = profileViewModel,
          onQuickFixChange = { _ -> },
          mode = AppMode.USER,
          navigationActionsRoot = navigationActions)
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
    every { runBlocking { categoryViewModel.getCategoryBySubcategoryId(any(), any()) } } answers
        {
          secondArg<(Category?) -> Unit>().invoke(sampleCategory)
        }

    quickFixViewModel.setUpdateQuickFix(sampleQuickFix)
    // Act
    composeTestRule.setContent {
      QuickFixLastStep(
          workerProfile = sampleWorkerProfile,
          categoryViewModel = categoryViewModel,
          quickFixViewModel = quickFixViewModel,
          workerViewModel = profileViewModel,
          onQuickFixChange = { _ -> },
          mode = AppMode.USER,
          navigationActionsRoot = navigationActions)
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
    every { runBlocking { categoryViewModel.getCategoryBySubcategoryId(any(), any()) } } answers
        {
          secondArg<(Category?) -> Unit>().invoke(sampleCategory)
        }

    quickFixViewModel.setUpdateQuickFix(completedQuickFix)
    // Act
    composeTestRule.setContent {
      QuickFixLastStep(
          workerProfile = sampleWorkerProfile,
          categoryViewModel = categoryViewModel,
          quickFixViewModel = quickFixViewModel,
          workerViewModel = profileViewModel,
          onQuickFixChange = { _ -> },
          mode = AppMode.USER,
          navigationActionsRoot = navigationActions)
    }

    // Set rating to 0
    composeTestRule.onNode(hasTestTag("RatingBar")).performTouchInput {
      click(percentOffset(0f, .5f))
    }

    // Assert that the Finish button is not enabled
    composeTestRule.onNodeWithTag("FinishButton").assertIsNotEnabled()
  }
}
