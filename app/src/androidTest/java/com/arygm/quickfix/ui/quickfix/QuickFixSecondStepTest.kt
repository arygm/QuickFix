package com.arygm.quickfix.ui.quickfix

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.profile.dataFields.AddOnService
import com.arygm.quickfix.model.profile.dataFields.IncludedService
import com.arygm.quickfix.model.quickfix.QuickFix
import com.arygm.quickfix.model.quickfix.QuickFixRepository
import com.arygm.quickfix.model.quickfix.QuickFixViewModel
import com.arygm.quickfix.model.quickfix.Status
import com.google.firebase.Timestamp
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class QuickFixSecondStepTest {

  private lateinit var quickFixRepository: QuickFixRepository
  private lateinit var quickFixViewModel: QuickFixViewModel

  @Before
  fun setUp() {
    quickFixRepository = mock(QuickFixRepository::class.java)
    quickFixViewModel = QuickFixViewModel(quickFixRepository)
    val fakeQuickFix =
        QuickFix(
            "",
            Status.PENDING,
            listOf("Image 1 URL", "Image 2 URL"),
            listOf(Timestamp.now(), Timestamp.now()),
            Timestamp.now(),
            listOf(IncludedService("Service 1"), IncludedService("Service 2")),
            listOf(AddOnService("Service 1"), AddOnService("Service 2")),
            "",
            "",
            "",
            "",
            "",
            emptyList(),
            Location(0.0, 0.0, "Fake Location"))
    quickFixViewModel.setQuickFixes(listOf(fakeQuickFix))
    composeTestRule.setContent { QuickFixSecondStep(quickFixViewModel, fakeQuickFix) }
  }

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testHeaderIsDisplayed() {
    // Check if the header text is displayed
    composeTestRule.onNodeWithTag("HeaderText").assertIsDisplayed()
  }

  @Test
  fun testButtonIsDisplayed() {
    // Check if the button is displayed
    composeTestRule.onNodeWithTag("ConsultDiscussionButton").assertIsDisplayed()
  }

  @Test
  fun testButtonClick() {
    // Perform click on the button
    composeTestRule.onNodeWithTag("ConsultDiscussionButton").performClick()

    // (Optional) Check for any post-click effects
  }

  @Test
  fun testDatesAndImagesAreDisplayed() {

    // Check if the dates row is displayed
    composeTestRule.onNodeWithTag("DatesRow").assertIsDisplayed()

    // Check if the day and time headers are displayed
    composeTestRule.onNodeWithTag("DayHeader").assertIsDisplayed()
    composeTestRule.onNodeWithTag("TimeHeader").assertIsDisplayed()

    composeTestRule.onNodeWithTag("DatesLazyColumn").assertIsDisplayed()

    quickFixViewModel.quickFixes.value[0].date.forEachIndexed() { index, date ->
      composeTestRule.onNodeWithTag("DateText_$index").assertIsDisplayed()
      composeTestRule.onNodeWithTag("TimeText_$index").assertIsDisplayed()
    }

    quickFixViewModel.quickFixes.value[0].imageUrl.forEachIndexed() { index, date ->
      composeTestRule.onNodeWithTag("DateText_$index").assertIsDisplayed()
      composeTestRule.onNodeWithTag("TimeText_$index").assertIsDisplayed()
    }
  }
}
