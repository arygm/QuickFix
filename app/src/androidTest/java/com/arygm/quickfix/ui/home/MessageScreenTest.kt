package com.arygm.quickfix.ui.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.arygm.quickfix.model.bill.BillField
import com.arygm.quickfix.model.bill.Unit
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.messaging.*
import com.arygm.quickfix.model.profile.dataFields.Service
import com.arygm.quickfix.model.quickfix.*
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.QuickFixTheme
import com.google.firebase.Timestamp
import java.util.Date
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonNull.content
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class MessageScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var chatRepository: ChatRepository
  private lateinit var quickFixRepository: QuickFixRepository
  private lateinit var chatViewModel: ChatViewModel
  private lateinit var quickFixViewModel: QuickFixViewModel

  // Implémentation de test pour Service
  data class TestService(override val name: String) : Service

  private val yesterday = Timestamp(Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000))
  private val today = Timestamp(Date(System.currentTimeMillis()))

  private val includedServices = listOf(TestService("Included Service 1"))
  private val addOnServices = listOf(TestService("Add-on Service 1"))

  private val billFields =
      listOf(
          BillField(
              description = "Labor", unit = Unit.HOURS, amount = 2, unitPrice = 40.0, total = 80.0))

  private val fakeLocation =
      Location(latitude = 40.7128, longitude = -74.0060, name = "Test Location")

  private val fakeQuickFixUid = "qf_123"
  private val fakeQuickFix =
      QuickFix(
          uid = fakeQuickFixUid,
          status = Status.PENDING,
          imageUrl = listOf("https://example.com/image1.jpg"),
          date = listOf(yesterday, today),
          time = today,
          includedServices = includedServices,
          addOnServices = addOnServices,
          workerName = "John the Worker",
          userName = "Jane the User",
          chatUid = "chat_123",
          title = "Fixing the Kitchen Sink",
          bill = billFields,
          location = fakeLocation)

  private val testUserId = "testUserId"
  private val fakeChat =
      Chat(
          chatId = "chat_123",
          chatStatus = ChatStatus.ACCEPTED,
          quickFixUid = fakeQuickFixUid,
          messages =
              listOf(
                  Message("msg_1", testUserId, "Hello!", Timestamp.now()),
                  Message("msg_2", "otherUserId", "Hi, how can I help you?", Timestamp.now())),
          useruid = "Jane the User",
          workeruid = "John the Worker")

  @Before
  fun setup() {
    navigationActions = mock(NavigationActions::class.java)
    chatRepository = mock(ChatRepository::class.java)
    quickFixRepository = mock(QuickFixRepository::class.java)

    // Mock du init pour quickFixRepository (si nécessaire)
    whenever(quickFixRepository.init(any())).thenAnswer { invocation ->
      val onSuccess = invocation.getArgument<() -> Unit>(0)
      onSuccess()
      null
    }

    // Simule getChats : on retourne toujours notre fakeChat
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<(List<Chat>) -> Unit>(0)
          onSuccess(listOf(fakeChat))
          null
        }
        .whenever(chatRepository)
        .getChats(any(), any())

    // Simule getQuickFixes : on retourne toujours notre fakeQuickFix
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<(List<QuickFix>) -> Unit>(0)
          onSuccess(listOf(fakeQuickFix))
          null
        }
        .whenever(quickFixRepository)
        .getQuickFixes(any(), any())

    // Création des ViewModels réels
    chatViewModel = ChatViewModel(chatRepository)
    quickFixViewModel = QuickFixViewModel(quickFixRepository)

    // Maintenant, on force le chargement des données pour éviter les cas nuls.
    runBlocking {
      chatViewModel.getChats() // Charge le chat
      quickFixViewModel.getQuickFixes() // Charge le QuickFix
    }

    // Sélection du chat si nécessaire
    chatViewModel.selectChat(fakeChat)
  }

  @Test
  fun testQuickFixDetailsAreDisplayed() {
    composeTestRule.setContent {
      QuickFixTheme {
        MessageScreen(
            chatViewModel = chatViewModel,
            navigationActions = navigationActions,
            quickFixViewModel = quickFixViewModel,
            userId = testUserId,
            isUser = true)
      }
    }

    // Vérifier l'affichage de quickFixDetails
    composeTestRule.onNodeWithTag("quickFixDetails").assertIsDisplayed()
  }

  @Test
  fun testMessagesAreDisplayed() {
    composeTestRule.setContent {
      QuickFixTheme {
        MessageScreen(
            chatViewModel = chatViewModel,
            navigationActions = navigationActions,
            quickFixViewModel = quickFixViewModel,
            userId = testUserId,
            isUser = true)
      }
    }

    // Vérifie les messages
    composeTestRule.onNodeWithText("Hello!").assertIsDisplayed()
    composeTestRule.onNodeWithText("Hi, how can I help you?").assertIsDisplayed()
  }

  @Test
  fun testAcceptedStatusShowsActiveConversationText() {
    // Le statut du chat est ACCEPTED par défaut
    composeTestRule.setContent {
      QuickFixTheme {
        MessageScreen(
            chatViewModel = chatViewModel,
            navigationActions = navigationActions,
            quickFixViewModel = quickFixViewModel,
            userId = testUserId,
            isUser = true)
      }
    }

    composeTestRule.onNodeWithText("Conversation is active. Start messaging!").assertIsDisplayed()
  }

  @Test
  fun testGettingSuggestionsShowsSuggestions() {
    // Arrange: On modifie le chat pour qu'il ait le statut GETTING_SUGGESTIONS
    val gettingSuggestionsChat = fakeChat.copy(chatStatus = ChatStatus.GETTING_SUGGESTIONS)
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<(List<Chat>) -> Unit>(0)
          // Retourne ce chat modifié
          onSuccess(listOf(gettingSuggestionsChat))
          null
        }
        .whenever(chatRepository)
        .getChats(any(), any())

    runBlocking {
      chatViewModel.getChats()
      chatViewModel.selectChat(gettingSuggestionsChat)
      quickFixViewModel.getQuickFixes()
    }

    // Act
    composeTestRule.setContent {
      QuickFixTheme {
        MessageScreen(
            chatViewModel = chatViewModel,
            navigationActions = navigationActions,
            quickFixViewModel = quickFixViewModel,
            userId = testUserId,
            isUser = true)
      }
    }

    // Assert : Vérifier que les suggestions sont affichées
    composeTestRule.onNodeWithText("How is it going?").assertIsDisplayed()
    composeTestRule.onNodeWithText("Is the time and day okay for you?").assertIsDisplayed()
    composeTestRule.onNodeWithText("I can’t wait to work with you!").assertIsDisplayed()
  }

  @Test
  fun testWorkerRefusedStatusShowsRefusalMessage() {
    // Arrange: On modifie le chat pour qu'il ait le statut WORKER_REFUSED
    val refusedChat = fakeChat.copy(chatStatus = ChatStatus.WORKER_REFUSED)
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<(List<Chat>) -> Unit>(0)
          // Retourne ce chat refusé
          onSuccess(listOf(refusedChat))
          null
        }
        .whenever(chatRepository)
        .getChats(any(), any())

    runBlocking {
      chatViewModel.getChats()
      chatViewModel.selectChat(refusedChat)
      quickFixViewModel.getQuickFixes()
    }

    // Act
    composeTestRule.setContent {
      QuickFixTheme {
        MessageScreen(
            chatViewModel = chatViewModel,
            navigationActions = navigationActions,
            quickFixViewModel = quickFixViewModel,
            userId = testUserId,
            isUser = true)
      }
    }

    // Assert : Vérifier que le message de refus est affiché
    composeTestRule
        .onNodeWithText(
            "John the Worker has rejected the QuickFix. No big deal! Contact another worker from the search screen! 😊")
        .assertIsDisplayed()
  }

  @Test
  fun testSendingMessageWhenAcceptedWorks() {
    // Arrange: Le statut est ACCEPTED par défaut dans fakeChat
    // On veut vérifier que l'envoi de message fonctionne. On va moquer sendMessage pour vérifier
    // qu'il est appelé.
    doAnswer { invocation ->
          val chat = invocation.getArgument<Chat>(0)
          val message = invocation.getArgument<Message>(1)
          val onSuccess = invocation.getArgument<() -> Unit>(2)
          // Simule le succès
          onSuccess()
          null
        }
        .whenever(chatRepository)
        .sendMessage(any(), any(), any(), any())

    // Act
    composeTestRule.setContent {
      QuickFixTheme {
        MessageScreen(
            chatViewModel = chatViewModel,
            navigationActions = navigationActions,
            quickFixViewModel = quickFixViewModel,
            userId = testUserId,
            isUser = true)
      }
    }

    // On entre un texte dans le champ de message
    composeTestRule.onNodeWithTag("messageTextField").performTextInput("Hello from test!")
    // On clique sur le bouton d'envoi
    composeTestRule.onNodeWithTag("sendButton").performClick()

    // Assert
    // Vérifier que sendMessage a été appelé avec le message "Hello from test!"
    verify(chatRepository)
        .sendMessage(eq(fakeChat), argThat { content == "Hello from test!" }, any(), any())
  }

  @Test
  fun testWaitingForResponseStatusAsUserShowsAwaitingConfirmation() {
    // Arrange: On modifie le chat pour qu'il ait le statut WAITING_FOR_RESPONSE
    val waitingChat = fakeChat.copy(chatStatus = ChatStatus.WAITING_FOR_RESPONSE)
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<(List<Chat>) -> Unit>(0)
          onSuccess(listOf(waitingChat))
          null
        }
        .whenever(chatRepository)
        .getChats(any(), any())

    runBlocking {
      chatViewModel.getChats()
      chatViewModel.selectChat(waitingChat)
      quickFixViewModel.getQuickFixes()
    }

    // Act
    composeTestRule.setContent {
      QuickFixTheme {
        MessageScreen(
            chatViewModel = chatViewModel,
            navigationActions = navigationActions,
            quickFixViewModel = quickFixViewModel,
            userId = testUserId,
            isUser = true)
      }
    }

    // Assert : Vérifier le texte d'attente
    composeTestRule
        .onNodeWithText("Awaiting confirmation from John the Worker...")
        .assertIsDisplayed()
  }

  @Test
  fun testWaitingForResponseStatusAsWorkerShowsAcceptRejectButtons() {
    // Arrange: On modifie le chat pour qu'il ait le statut WAITING_FOR_RESPONSE
    val waitingChat = fakeChat.copy(chatStatus = ChatStatus.WAITING_FOR_RESPONSE)
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<(List<Chat>) -> Unit>(0)
          onSuccess(listOf(waitingChat))
          null
        }
        .whenever(chatRepository)
        .getChats(any(), any())

    runBlocking {
      chatViewModel.getChats()
      chatViewModel.selectChat(waitingChat)
      quickFixViewModel.getQuickFixes()
    }

    // Act
    composeTestRule.setContent {
      QuickFixTheme {
        MessageScreen(
            chatViewModel = chatViewModel,
            navigationActions = navigationActions,
            quickFixViewModel = quickFixViewModel,
            userId = testUserId,
            isUser = false // Le worker
            )
      }
    }

    // Assert : Vérifier les boutons accept/reject
    composeTestRule
        .onNodeWithText("Would you like to accept this QuickFix request?")
        .assertIsDisplayed()
    composeTestRule.onNodeWithTag("acceptButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("refuseButton").assertIsDisplayed()
  }
}
