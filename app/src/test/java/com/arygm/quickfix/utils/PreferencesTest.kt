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
  fun setAccountPreferencesSavesUserPreferences() =
      runTest(testDispatcher) {
        // Arrange
        val account =
            Account(
                uid = "user123",
                firstName = "Alice",
                lastName = "Smith",
                email = "alice.smith@example.com",
                birthDate = Timestamp(GregorianCalendar(1990, Calendar.MAY, 15).time),
                isWorker = true)

        // Act
        setAccountPreferences(
            preferencesViewModel, account, signIn = true, dispatcher = testDispatcher)
        testScheduler.advanceUntilIdle() // Ensures all coroutines finish

        // Assert
        verify(preferencesViewModel).savePreference(IS_SIGN_IN_KEY, true)
        verify(preferencesViewModel).savePreference(USER_ID_KEY, "user123")
        verify(preferencesViewModel).savePreference(FIRST_NAME_KEY, "Alice")
        verify(preferencesViewModel).savePreference(LAST_NAME_KEY, "Smith")
        verify(preferencesViewModel).savePreference(EMAIL_KEY, "alice.smith@example.com")
        // Adjust date string as per your timestampToString implementation
        verify(preferencesViewModel).savePreference(DATE_OF_BIRTH_KEY, "15/05/1990")
        verify(preferencesViewModel).savePreference(IS_WORKER_KEY, true)
      }
}
