package com.arygm.quickfix.ui.dashboard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.arygm.quickfix.R
import org.junit.Rule
import org.junit.Test

class MessageWidgetTests {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun messagesWidget_displaysDefaultNumberOfItems() {
    val testMessages =
        listOf(
            MessageSneakPeak(
                "User 1",
                "Message 1",
                "2023-11-26",
                R.drawable.placeholder_worker,
                false,
                1,
                Icons.Default.Home),
            MessageSneakPeak(
                "User 2",
                "Message 2",
                "2023-11-27",
                R.drawable.placeholder_worker,
                true,
                0,
                Icons.Default.Settings),
            MessageSneakPeak(
                "User 3",
                "Message 3",
                "2023-11-28",
                R.drawable.placeholder_worker,
                true,
                2,
                Icons.Default.Email),
            MessageSneakPeak(
                "User 4",
                "Message 4",
                "2023-11-29",
                R.drawable.placeholder_worker,
                false,
                3,
                Icons.Default.Call))

    composeTestRule.setContent {
      MessagesWidget(
          messageList = testMessages, onShowAllClick = {}, onItemClick = {}, itemsToShowDefault = 3)
    }

    // Verify that only the default number of items (3) are displayed initially
    composeTestRule.onNodeWithTag("MessageItem_User 1").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MessageItem_User 2").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MessageItem_User 3").assertIsDisplayed()

    // Verify that the fourth item is not displayed
    composeTestRule.onNodeWithTag("MessageItem_User 4").assertDoesNotExist()
  }

  @Test
  fun messagesWidget_displaysAllItems_whenShowAllClicked() {
    val testMessages =
        listOf(
            MessageSneakPeak(
                "User 1",
                "Message 1",
                "2023-11-26",
                R.drawable.placeholder_worker,
                false,
                1,
                Icons.Default.Home),
            MessageSneakPeak(
                "User 2",
                "Message 2",
                "2023-11-27",
                R.drawable.placeholder_worker,
                true,
                0,
                Icons.Default.Settings),
            MessageSneakPeak(
                "User 3",
                "Message 3",
                "2023-11-28",
                R.drawable.placeholder_worker,
                true,
                2,
                Icons.Default.Email),
            MessageSneakPeak(
                "User 4",
                "Message 4",
                "2023-11-29",
                R.drawable.placeholder_worker,
                false,
                3,
                Icons.Default.Call))

    composeTestRule.setContent {
      MessagesWidget(
          messageList = testMessages, onShowAllClick = {}, onItemClick = {}, itemsToShowDefault = 3)
    }

    // Click the "Show All" button
    composeTestRule.onNodeWithTag("ShowAllButton").performClick()

    // Verify that all items are displayed
    composeTestRule.onNodeWithTag("MessageItem_User 1").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MessageItem_User 2").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MessageItem_User 3").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MessageItem_User 4").assertIsDisplayed()
  }
}
