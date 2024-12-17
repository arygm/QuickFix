package com.arygm.quickfix.ui.elements

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.arygm.quickfix.model.messaging.*
import com.arygm.quickfix.model.quickfix.*
import com.arygm.quickfix.model.quickfix.QuickFix
import com.arygm.quickfix.ui.navigation.NavigationActions
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*

class QuickFixDisplayImagesScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var chatRepository: ChatRepository
  private lateinit var quickFixRepository: QuickFixRepository
  private lateinit var chatViewModel: ChatViewModel
  private lateinit var quickFixViewModel: QuickFixViewModel

  // Données de test
  private val fakeQuickFixUid = "fake_quick_fix_id"
  private val fakeChat =
      Chat(
          chatId = "chat_id",
          workeruid = "worker_id",
          useruid = "user_id",
          quickFixUid = fakeQuickFixUid,
          messages = emptyList(),
          chatStatus = ChatStatus.ACCEPTED)
  private val fakeQuickFix =
      QuickFix(
          uid = fakeQuickFixUid,
          imageUrl = listOf("https://example.com/image1.jpg", "https://example.com/image2.jpg"),
          title = "Fixing the Sink",
          description = "Fix the kitchen sink",
          status = Status.PENDING)

  @Before
  fun setup() = runBlocking {
    // Mock des dépendances
    navigationActions = mock()
    chatRepository = mock()
    quickFixRepository = mock()

    // Mock ChatRepository: Retourner un chat
    whenever(chatRepository.getChats(any(), any())).thenAnswer {
      val onSuccess = it.getArgument<(List<Chat>) -> Unit>(0)
      onSuccess(listOf(fakeChat))
    }

    // Mock QuickFixRepository: Retourner un QuickFix
    whenever(quickFixRepository.getQuickFixById(eq(fakeQuickFixUid), any(), any())).thenAnswer {
      val onResult = it.getArgument<(QuickFix?) -> Unit>(1)
      onResult(fakeQuickFix)
    }

    // Initialisation des ViewModels
    chatViewModel = ChatViewModel(chatRepository)
    quickFixViewModel = QuickFixViewModel(quickFixRepository)

    chatViewModel.getChats()
    chatViewModel.selectChat(fakeChat)
  }

  @Test
  fun displaysCorrectNumberOfImagesInTitle() {
    composeTestRule.setContent {
      QuickFixDisplayImagesScreen(
          navigationActions = navigationActions,
          chatViewModel = chatViewModel,
          quickFixViewModel = quickFixViewModel)
    }

    // Vérification du titre avec le bon nombre d'images
    composeTestRule.onNodeWithText("2 images").assertIsDisplayed()
  }

  @Test
  fun clickingBackButtonNavigatesBack() {
    composeTestRule.setContent {
      QuickFixDisplayImagesScreen(
          navigationActions = navigationActions,
          chatViewModel = chatViewModel,
          quickFixViewModel = quickFixViewModel)
    }

    // Action : clic sur le bouton "Back"
    composeTestRule.onNodeWithContentDescription("Back").performClick()

    // Vérification : navigation appelée
    verify(navigationActions).goBack()
  }

  @Test
  fun displaysImagesCorrectly() {
    composeTestRule.setContent {
      QuickFixDisplayImagesScreen(
          navigationActions = navigationActions,
          chatViewModel = chatViewModel,
          quickFixViewModel = quickFixViewModel)
    }

    // Vérification : 2 images sont affichées dans la grille
    composeTestRule.onAllNodesWithTag("imageCard").assertCountEquals(2)
  }

  @Test
  fun displaysNoActiveChatMessageWhenNoChatSelected() {
    runBlocking {
      // Supprimer le chat sélectionné
      chatViewModel.clearSelectedChat()

      composeTestRule.setContent {
        QuickFixDisplayImagesScreen(
            navigationActions = navigationActions,
            chatViewModel = chatViewModel,
            quickFixViewModel = quickFixViewModel)
      }

      // Vérification : le message d'absence de chat actif est affiché
      composeTestRule.onNodeWithText("No active chat selected.").assertIsDisplayed()
    }
  }
}
