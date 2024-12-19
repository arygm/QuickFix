@file:OptIn(ExperimentalCoroutinesApi::class)

package com.arygm.quickfix.ui.tools.ai

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arygm.quickfix.model.tools.ai.GeminiMessageModel
import com.arygm.quickfix.model.tools.ai.GeminiViewModel
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.tools.ai.QuickFixAIChatScreen
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuickFixAIChatScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var mockViewModel: GeminiViewModel

  @Before
  fun setup() {
    // In a real scenario, you might want to mock the network calls in GeminiViewModel.
    // For now, we'll just initialize it directly. If needed, you can use a testing double.
    mockViewModel =
        GeminiViewModel().apply {
          // By default, messageList contains only the context message.
          // This means the UI should show "How may I help?" prompt.
        }
  }

  @Test
  fun initialPromptIsDisplayedWhenNoMessages() {
    // Given the initial state with only the context message
    composeTestRule.setContent { QuickFixAIChatScreen(viewModel = mockViewModel) }

    // Then the prompt "How may I help?" should be displayed
    composeTestRule.onNodeWithText("How may I help?").assertIsDisplayed()
  }

  @Test
  fun userCanSendMessageAndSeeItInTheList() = runTest {
    composeTestRule.setContent { QuickFixAIChatScreen(viewModel = mockViewModel) }

    // Initially, "How may I help?" is displayed
    composeTestRule.onNodeWithText("How may I help?").assertIsDisplayed()

    // When user types a message and sends it
    val userMessage = "I need someone to paint my living room."
    composeTestRule.onNodeWithText("Describe your issue").performTextInput(userMessage)

    // Click the send icon (which has contentDescription "Send")
    composeTestRule.onNodeWithTag("sendIcon").performClick()

    // After sending, the user's message should now appear in the conversation.
    // Wait a moment for the coroutine and message update if necessary.
    composeTestRule.onNodeWithText(userMessage).assertIsDisplayed()
  }

  @Test
  fun contextMessageIsNotRepeatedAfterClear() = runTest {
    // Start with a message already sent
    mockViewModel.messageList.add(GeminiMessageModel("Can you help me fix a leaky faucet?", "user"))

    composeTestRule.setContent { QuickFixAIChatScreen(viewModel = mockViewModel) }

    // Check that the user's message is displayed
    composeTestRule.onNodeWithText("Can you help me fix a leaky faucet?").assertIsDisplayed()

    // Clear messages
    mockViewModel.clearMessages()

    // After clearing, we should see the initial prompt again
    composeTestRule.onNodeWithText("How may I help?").assertIsDisplayed()
  }
}
