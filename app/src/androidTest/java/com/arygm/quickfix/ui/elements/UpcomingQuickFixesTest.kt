package com.arygm.quickfix.ui.elements

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.profile.Profile
import com.arygm.quickfix.model.profile.ProfileRepository
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.model.quickfix.QuickFix
import com.arygm.quickfix.model.quickfix.Status
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class UpcomingQuickFixesTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var workerViewRepository: ProfileRepository
  private lateinit var workerViewModel: ProfileViewModel

  private val sampleData =
      listOf(
          QuickFix(
              uid = "1",
              status = Status.UPCOMING,
              imageUrl = emptyList(),
              date = listOf(Timestamp.now()),
              time = Timestamp.now(),
              includedServices = emptyList(),
              addOnServices = emptyList(),
              workerId = "worker 1",
              userId = "user 1",
              chatUid = "chat 1",
              title = "QuickFix 1",
              description = "Description 1",
              bill = emptyList(),
              location = Location(0.0, 0.0, "Location 1")),
          QuickFix(
              uid = "2",
              status = Status.UPCOMING,
              imageUrl = emptyList(),
              date = listOf(Timestamp.now()),
              time = Timestamp.now(),
              includedServices = emptyList(),
              addOnServices = emptyList(),
              workerId = "worker 2",
              userId = "user 2",
              chatUid = "chat 2",
              title = "QuickFix 2",
              description = "Description 2",
              bill = emptyList(),
              location = Location(0.0, 0.0, "Location 2")),
          QuickFix(
              uid = "3",
              status = Status.UPCOMING,
              imageUrl = emptyList(),
              date = listOf(Timestamp.now()),
              time = Timestamp.now(),
              includedServices = emptyList(),
              addOnServices = emptyList(),
              workerId = "worker 3",
              userId = "user 3",
              chatUid = "chat 3",
              title = "QuickFix 3",
              description = "Description 3",
              bill = emptyList(),
              location = Location(0.0, 0.0, "Location 1")))

  @Before
  fun setUp() {
    workerViewRepository = mock(ProfileRepository::class.java)
    workerViewModel = ProfileViewModel(workerViewRepository)
    doAnswer { invocation ->
          val onSuccess = invocation.arguments[1] as (Profile?) -> Unit
          onSuccess(WorkerProfile(displayName = "Anonymous Worker"))
          null
        }
        .whenever(workerViewRepository)
        .getProfileById(any(), any(), any())
  }

  @Test
  fun testUpcomingQuickFixesDisplaysItems() {
    val formatter = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())

    composeTestRule.setContent {
      QuickFixesWidget(
          quickFixList = sampleData,
          onShowAllClick = {},
          onItemClick = {},
          workerViewModel = workerViewModel,
          itemsToShowDefault = 3)
    }
    // Verify that the first three items are displayed
    composeTestRule.onAllNodesWithText("Anonymous Worker").assertCountEquals(3)
    composeTestRule
        .onAllNodesWithText(formatter.format(sampleData[0].date.first().toDate()))
        .assertCountEquals(3)
    sampleData.forEachIndexed { index, it ->
      composeTestRule.onNodeWithText(it.title).assertIsDisplayed()
    }
  }

  @Test
  fun testShowAllButtonTogglesItemCount() {
    composeTestRule.setContent {
      QuickFixesWidget(
          quickFixList = sampleData + sampleData,
          onShowAllClick = {},
          onItemClick = {},
          workerViewModel = workerViewModel)
    }

    // Verify initial state shows only first three items
    composeTestRule.onAllNodesWithTagPrefix("QuickFixItem_").assertCountEquals(3)

    // Click on "Show All" button
    composeTestRule.onNodeWithTag("ShowAllButton").performClick()

    // Verify all items are displayed
    composeTestRule.onAllNodesWithTagPrefix("QuickFixItem_").assertCountEquals(6)

    // Click on "Show Less" button
    composeTestRule.onNodeWithTag("ShowAllButton").performClick()

    // Verify only three items are displayed again
    composeTestRule.onAllNodesWithTagPrefix("QuickFixItem_").assertCountEquals(3)
  }

  @Test
  fun testItemClick() {
    var clickedItem: QuickFix? = null

    composeTestRule.setContent {
      QuickFixesWidget(
          quickFixList = sampleData,
          onShowAllClick = {},
          onItemClick = { clickedItem = it },
          workerViewModel = workerViewModel)
    }

    // Perform click on the item
    composeTestRule.onNodeWithTag("QuickFixItem_${sampleData.get(0).title}").performClick()

    // Verify that the clicked item is correct
    assert(clickedItem == sampleData[0])
  }

  // Helper function to find nodes with tag prefix
  private fun SemanticsNodeInteractionsProvider.onAllNodesWithTagPrefix(
      tagPrefix: String
  ): SemanticsNodeInteractionCollection {
    return onAllNodes(hasTestTagPrefix(tagPrefix))
  }

  private fun hasTestTagPrefix(prefix: String): SemanticsMatcher {
    return SemanticsMatcher("${SemanticsProperties.TestTag.name} starts with '$prefix'") {
      val testTag = it.config.getOrNull(SemanticsProperties.TestTag)
      testTag != null && testTag.startsWith(prefix)
    }
  }
}
