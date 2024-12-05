package com.arygm.quickfix.ui.elements

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arygm.quickfix.model.categories.WorkerCategory
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.profile.dataFields.AddOnService
import com.arygm.quickfix.model.profile.dataFields.IncludedService
import com.arygm.quickfix.model.quickfix.QuickFix
import com.arygm.quickfix.model.quickfix.Status
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UpcomingQuickFixesTest {

  @get:Rule val composeTestRule = createComposeRule()

  private val sampleData =
      listOf(
          QuickFix(
              uid = "1",
              status = Status.UPCOMING,
              imageUrl = listOf("https://example.com/image1.jpg"),
              date = listOf(Timestamp.now()),
              time = Timestamp.now(),
              category = WorkerCategory.PAINTING,
              includedServices = listOf(IncludedService("Exterior Painting")),
              addOnServices = listOf(AddOnService("Interior Painting")),
              workerName = "Adam Worker",
              userName = "Adam User",
              chatUid = "adam123",
              title = "Painting",
              bill = emptyList(),
              location = Location(123.01, 123.02, "123")))

  @Test
  fun testUpcomingQuickFixesDisplaysItems() {
    val formatter = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
    composeTestRule.setContent {
      QuickFixesWidget(
          status = Status.UPCOMING,
          quickFixList = sampleData,
          onShowAllClick = {},
          onItemClick = {})
    }

    // Verify that the first three items are displayed
    sampleData.forEach {
      composeTestRule.onNodeWithText(it.workerName).assertIsDisplayed()
      composeTestRule.onNodeWithText(it.title).assertIsDisplayed()
      composeTestRule.onNodeWithText(formatter.format(it.date.first().toDate())).assertIsDisplayed()
    }
  }

  @Test
  fun testShowAllButtonTogglesItemCount() {
    val sampleData = List(5) { index -> sampleData[0].copy(uid = index.toString()) }

    composeTestRule.setContent {
      QuickFixesWidget(
          status = Status.UPCOMING,
          quickFixList = sampleData,
          onShowAllClick = {},
          onItemClick = {})
    }

    // Verify initial state shows only first three items
    composeTestRule.onAllNodesWithTagPrefix("QuickFixItem_").assertCountEquals(3)

    // Click on "Show All" button
    composeTestRule.onNodeWithTag("ShowAllButton").performClick()

    // Verify all items are displayed
    composeTestRule.onAllNodesWithTagPrefix("QuickFixItem_").assertCountEquals(5)

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
          status = Status.UPCOMING,
          quickFixList = sampleData,
          onShowAllClick = {},
          onItemClick = { clickedItem = it })
    }

    // Perform click on the item
    composeTestRule.onNodeWithTag("QuickFixItem_Adam Worker").performClick()

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
