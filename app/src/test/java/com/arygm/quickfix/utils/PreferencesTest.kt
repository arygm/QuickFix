package com.arygm.quickfix.utils

import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.google.firebase.Timestamp
import java.util.Calendar
import java.util.GregorianCalendar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class PreferencesTest {

  private lateinit var preferencesViewModel: PreferencesViewModel
  private val testDispatcher = StandardTestDispatcher()

  @Before
  fun setup() {
    Dispatchers.setMain(testDispatcher)
    preferencesViewModel = mock()
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun `test setAccountPreferences saves signOut state`() =
      runTest(testDispatcher) {
        // Arrange
        val account =
            Account(
                uid = "testUid",
                firstName = "John",
                lastName = "Doe",
                email = "john.doe@example.com",
                birthDate = Timestamp(GregorianCalendar(2023, Calendar.JANUARY, 1).time),
                isWorker = false)

        // Act
        setAccountPreferences(preferencesViewModel, account, signIn = false)
        testScheduler.advanceUntilIdle() // Ensure all coroutines are executed

        // Assert
        verify(preferencesViewModel).savePreference(IS_SIGN_IN_KEY, false)
        verify(preferencesViewModel).savePreference(USER_ID_KEY, "testUid")
        verify(preferencesViewModel).savePreference(FIRST_NAME_KEY, "John")
        verify(preferencesViewModel).savePreference(LAST_NAME_KEY, "Doe")
        verify(preferencesViewModel).savePreference(EMAIL_KEY, "john.doe@example.com")
        verify(preferencesViewModel).savePreference(DATE_OF_BIRTH_KEY, "01/01/2023")
        verify(preferencesViewModel).savePreference(IS_WORKER_KEY, false)
      }
}
