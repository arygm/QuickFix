package com.arygm.quickfix.ui.dashboard

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.arygm.quickfix.model.account.AccountRepositoryFirestore
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.messaging.Chat
import com.arygm.quickfix.model.messaging.Message
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.WorkerProfileRepositoryFirestore
import com.google.firebase.Timestamp
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class MessageWidgetTests {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var accountViewModel: AccountViewModel
  private lateinit var accountRepositoryFirestore: AccountRepositoryFirestore
  private lateinit var profileViewModel: ProfileViewModel
  private lateinit var profileRepositoryFirestore: WorkerProfileRepositoryFirestore
  private val testMessages =
      listOf(
          Chat(
              "Id 1",
              "WorkerId 1",
              "UserId 1",
              listOf(Message("MessageId 1", "User 1", "Message 1", Timestamp.now(), false)),
          ),
          Chat(
              "Id 2",
              "WorkerId 2",
              "UserId 2",
              listOf(Message("MessageId 2", "User 2", "Message 2", Timestamp.now(), false)),
          ),
          Chat(
              "Id 3",
              "WorkerId 3",
              "UserId 3",
              listOf(Message("MessageId 3", "User 3", "Message 3", Timestamp.now(), false)),
          ),
          Chat(
              "Id 4",
              "WorkerId 4",
              "UserId 4",
              listOf(Message("MessageId 4", "User 4", "Message 4", Timestamp.now(), false)),
          ))

  @Before
  fun setup() {
    accountRepositoryFirestore = mock(AccountRepositoryFirestore::class.java)
    accountViewModel = AccountViewModel(accountRepositoryFirestore)
    profileRepositoryFirestore = mock(WorkerProfileRepositoryFirestore::class.java)
    profileViewModel = ProfileViewModel(profileRepositoryFirestore)
    composeTestRule.setContent {
      MessagesWidget(
          messageList = testMessages,
          onShowAllClick = {},
          onItemClick = {},
          isUser = true,
          accountViewModel = accountViewModel,
          profileViewModel = profileViewModel,
          itemsToShowDefault = 3)
    }
  }

  @Test
  fun messagesWidget_displaysDefaultNumberOfItems() {

    // Verify that only the default number of items (3) are displayed initially
    composeTestRule.onNodeWithTag("MessageItem_Id 1").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MessageItem_Id 2").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MessageItem_Id 3").assertIsDisplayed()

    // Verify that the fourth item is not displayed
    composeTestRule.onNodeWithTag("MessageItem_Id 4").assertDoesNotExist()
  }

  @Test
  fun messagesWidget_displaysAllItems_whenShowAllClicked() {

    // Click the "Show All" button
    composeTestRule.onNodeWithTag("ShowAllButton").performClick()

    // Verify that all items are displayed
    composeTestRule.onNodeWithTag("MessageItem_Id 1").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MessageItem_Id 2").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MessageItem_Id 3").assertIsDisplayed()
    composeTestRule.onNodeWithTag("MessageItem_Id 4").assertIsDisplayed()
  }
}
