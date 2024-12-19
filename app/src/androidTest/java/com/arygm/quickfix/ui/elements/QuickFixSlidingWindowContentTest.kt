package com.arygm.quickfix.ui.elements

import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.account.AccountRepository
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.profile.dataFields.Service
import com.arygm.quickfix.model.quickfix.QuickFix
import com.arygm.quickfix.model.quickfix.QuickFixRepository
import com.arygm.quickfix.model.quickfix.QuickFixViewModel
import com.arygm.quickfix.model.quickfix.Status
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.google.firebase.Timestamp
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class QuickFixSlidingWindowContentTest {

  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var accountRepository: AccountRepository
  private lateinit var accountViewModel: AccountViewModel
  private lateinit var quickFixRepository: QuickFixRepository
  private lateinit var quickFixViewModel: QuickFixViewModel

  private val quickFixMock =
      QuickFix(
          uid = "12345",
          status = Status.PENDING,
          imageUrl = listOf("https://via.placeholder.com/120"),
          date = listOf(Timestamp.now()),
          time = Timestamp.now(),
          includedServices =
              listOf(
                  mock(Service::class.java).apply {
                    `when`(name).thenReturn("Initial Consultation")
                  },
                  mock(Service::class.java).apply {
                    `when`(name).thenReturn("Professional Clean-Up")
                  }),
          addOnServices =
              listOf(
                  mock(Service::class.java).apply {
                    `when`(name).thenReturn("Premium Paint Upgrade")
                  },
                  mock(Service::class.java).apply {
                    `when`(name).thenReturn("Extra Coats for added Durability")
                  }),
          workerId = "Worker Id",
          userId = "User Id",
          chatUid = "chat_12345",
          title = "QuickFix Title",
          description = "QuickFix Description",
          bill = emptyList(),
          location = Location(48.8566, 2.3522, "Paris, France"))
  private lateinit var navigationActions: NavigationActions

  @Before
  fun setup() {
    navigationActions = mock(NavigationActions::class.java)
    accountRepository = mock(AccountRepository::class.java)
    accountViewModel = AccountViewModel(accountRepository)
    quickFixRepository = mock(QuickFixRepository::class.java)
    quickFixViewModel = QuickFixViewModel(quickFixRepository)
    doAnswer { invocation ->
          val onSuccess = invocation.arguments[1] as (Account?) -> Unit
          onSuccess(
              Account(
                  uid = "User Id",
                  firstName = "Name",
                  lastName = "Last Name",
                  email = "email",
                  birthDate = Timestamp.now()))
        }
        .whenever(accountRepository)
        .getAccountById(any(), any(), any())
  }

  @Test
  fun quickFixSlidingWindowContent_displaysTitle() {
    composeTestRule.setContent {
      QuickFixSlidingWindowContent(
          quickFix = quickFixMock,
          onDismiss = {},
          isVisible = true,
          navigationActions,
          accountViewModel,
          quickFixViewModel)
    }

    composeTestRule.onNodeWithText("Name Last Name's QuickFix request").assertIsDisplayed()
  }

  @Test
  fun quickFixSlidingWindowContent_displaysServices() {
    composeTestRule.setContent {
      QuickFixSlidingWindowContent(
          quickFix = quickFixMock,
          onDismiss = {},
          isVisible = true,
          navigationActions,
          accountViewModel,
          quickFixViewModel)
    }

    quickFixMock.includedServices.forEach { service ->
      composeTestRule.onNodeWithText(service.name).assertIsDisplayed()
    }
    quickFixMock.addOnServices.forEach { service ->
      composeTestRule.onNodeWithText(service.name).assertIsDisplayed()
    }
  }

  @Test
  fun quickFixSlidingWindowContent_displaysAppointmentDetails() {
    composeTestRule.setContent {
      QuickFixSlidingWindowContent(
          quickFix = quickFixMock,
          onDismiss = {},
          isVisible = true,
          navigationActions,
          accountViewModel,
          quickFixViewModel)
    }

    val appointmentTime = quickFixMock.time.toDate().toString().split(" ")[3]
    val appointmentDate = quickFixMock.time.toDate().toString().split(" ").take(3).joinToString(" ")

    composeTestRule.onNodeWithText(appointmentTime).assertIsDisplayed()
    composeTestRule.onNodeWithText(appointmentDate).assertIsDisplayed()
    composeTestRule.onNodeWithText(quickFixMock.location.name).assertIsDisplayed()
  }
}
