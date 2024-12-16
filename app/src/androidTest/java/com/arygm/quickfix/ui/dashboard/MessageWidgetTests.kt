package com.arygm.quickfix.ui.dashboard

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.arygm.quickfix.model.messaging.Chat
import com.arygm.quickfix.model.messaging.Message
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.dashboard.ChatWidget
import org.junit.Rule
import org.junit.Test

class MessageWidgetTests {

  @get:Rule val composeTestRule = createComposeRule()

  private val testChat =
      Chat(
          chatId = "1",
          workeruid = "Worker 1",
          useruid = "User 1",
          quickFixUid = "1",
          listOf(
              Message("Message 1", "User 1", "Content 1"),
              Message("Message 2", "Worker 1", "Content 2"),
              Message("Message 3", "User 1", "Content 3")))

  @Test
  fun messagesWidget_displaysDefaultNumberOfItems() {

    composeTestRule.setContent {
      ChatWidget(
          chatList = List(3) { testChat },
          onShowAllClick = {},
          onItemClick = {},
          itemsToShowDefault = 3,
          uid = "User 1")
    }

    // Verify that only the default number of items (3) are displayed initially
    composeTestRule.onAllNodesWithTag("MessageItem_${testChat.chatId}").assertCountEquals(3)
  }

  @Test
  fun messagesWidget_displaysAllItems_whenShowAllClicked() {
    composeTestRule.setContent {
      ChatWidget(
          chatList = List(4) { testChat },
          onShowAllClick = {},
          onItemClick = {},
          itemsToShowDefault = 3,
          uid = "User 1")
    }

    // Click the "Show All" button
    composeTestRule.onNodeWithTag("ShowAllButton").performClick()

    // Verify that all items are displayed
    composeTestRule.onAllNodesWithTag("MessageItem_${testChat.chatId}").assertCountEquals(4)
  }
}
