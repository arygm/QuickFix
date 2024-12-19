package com.arygm.quickfix.model.tools.ai

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GeminiViewModelTest {

  @get:Rule val instantExecutorRule = InstantTaskExecutorRule()

  private val testDispatcher = StandardTestDispatcher()

  private lateinit var generativeModel: GenerativeModel
  private lateinit var viewModel: GeminiViewModel

  @Before
  fun setup() {
    Dispatchers.setMain(testDispatcher)
    generativeModel = mockk()
    viewModel = GeminiViewModel(generativeModel)
  }

  @Test
  fun `sendMessage handles errors gracefully`() = runTest {
    val userQuestion = "I need an electrician."
    val mockChat = mockk<Chat>(relaxed = true)

    every { generativeModel.startChat(any()) } returns mockChat
    every { runBlocking { mockChat.sendMessage(userQuestion) } } throws Exception("API error")

    viewModel.sendMessage(userQuestion)

    advanceUntilIdle()

    assertEquals(3, viewModel.messageList.size)
    assertEquals(userQuestion, viewModel.messageList[1].message)
    assertEquals("Error : API error", viewModel.messageList[2].message)

    verify { runBlocking { mockChat.sendMessage(userQuestion) } }
  }

  @Test
  fun `clearMessages resets message list to context message`() {
    viewModel.messageList.add(GeminiMessageModel("Test message", "user"))

    viewModel.clearMessages()

    assertEquals(1, viewModel.messageList.size)
    assertEquals(viewModel.contextMessage, viewModel.messageList[0].message)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }
}
