package com.arygm.quickfix.ui.dashboard

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.arygm.quickfix.model.bill.BillField
import com.arygm.quickfix.model.bill.Units
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.quickfix.QuickFix
import com.arygm.quickfix.model.quickfix.Status
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.dashboard.BillsWidget
import com.google.firebase.Timestamp
import org.junit.Rule
import org.junit.Test

class BillsWidgetTests {

  @get:Rule val composeTestRule = createComposeRule()

  private val testBills =
      listOf(
          BillField("Bill 1", Units.M2, 100.0, 25.0),
          BillField("Bill 2", Units.H, 100.0, 25.0),
          BillField("Bill 3", Units.M, 100.0, 25.0),
          BillField("Bill 4", Units.U, 100.0, 25.0))

  private val fakeQuickFix =
      QuickFix(
          uid = "1",
          title = "Fake QuickFix",
          description = "This is a fake QuickFix for testing",
          imageUrl = listOf("https://example.com/image.jpg"),
          date = listOf(Timestamp.now()), // Example timestamp
          time = Timestamp.now(),
          includedServices = emptyList(),
          addOnServices = emptyList(),
          workerId = "1",
          userId = "1",
          chatUid = "1",
          status = Status.UPCOMING,
          bill = testBills,
          location = Location(0.0, 0.0, "Fake Location"))

  @Test
  fun billSample_displaysDefaultNumberOfItems() {
    composeTestRule.setContent {
      BillsWidget(
          quickFixes = listOf(fakeQuickFix),
          onShowAllClick = {},
          onItemClick = {},
          itemsToShowDefault = 3)
    }

    composeTestRule.onNodeWithTag("BillItem_${fakeQuickFix.title}").assertIsDisplayed()

    // Verify that the fourth item is not displayed
    composeTestRule.onNodeWithTag("BillItem_Bill 4").assertDoesNotExist()
  }

  @Test
  fun billSample_displaysAllItems_whenShowAllClicked() {
    composeTestRule.setContent {
      BillsWidget(
          quickFixes = List(4) { fakeQuickFix },
          onShowAllClick = {},
          onItemClick = {},
          itemsToShowDefault = 3)
    }

    // Click the "Show All" button
    composeTestRule.onNodeWithTag("ShowAllButton").performClick()

    // Verify that all items are displayed
    composeTestRule.onAllNodesWithTag("BillItem_${fakeQuickFix.title}").assertCountEquals(4)
  }
}
