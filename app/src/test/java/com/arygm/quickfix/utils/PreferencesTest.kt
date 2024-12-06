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
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

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
        verify(preferencesViewModel).savePreference(UID_KEY, "user123")
        verify(preferencesViewModel).savePreference(FIRST_NAME_KEY, "Alice")
        verify(preferencesViewModel).savePreference(LAST_NAME_KEY, "Smith")
        verify(preferencesViewModel).savePreference(EMAIL_KEY, "alice.smith@example.com")
        verify(preferencesViewModel)
            .savePreference(
                BIRTH_DATE_KEY,
                "15/05/1990") // Ensure formatting matches your timestampToString implementation
        verify(preferencesViewModel).savePreference(IS_WORKER_KEY, true)
      }

  @Test
  fun setSignInSavesSignInPreference() =
      runTest(testDispatcher) {
        // Act
        setSignIn(preferencesViewModel, true, dispatcher = testDispatcher)
        testScheduler.advanceUntilIdle()

        // Assert
        verify(preferencesViewModel).savePreference(IS_SIGN_IN_KEY, true)
      }

  @Test
  fun loadIsSignInReturnsCorrectValue() =
      runTest(testDispatcher) {
        // Arrange
        whenever(preferencesViewModel.loadPreference(eq(IS_SIGN_IN_KEY), any())).thenAnswer {
            invocation ->
          val callback = invocation.arguments[1] as (Boolean?) -> Unit
          callback(true)
        }

        // Act
        val result = loadIsSignIn(preferencesViewModel)

        // Assert
        assertEquals(true, result)
      }

  @Test
  fun loadFirstNameReturnsCorrectValue() =
      runTest(testDispatcher) {
        // Arrange
        whenever(preferencesViewModel.loadPreference(eq(FIRST_NAME_KEY), any())).thenAnswer {
            invocation ->
          val callback = invocation.arguments[1] as (String?) -> Unit
          callback("Alice")
        }

        // Act
        val result = loadFirstName(preferencesViewModel)

        // Assert
        assertEquals("Alice", result)
      }

  @Test
  fun setFirstNameSavesFirstNamePreference() =
      runTest(testDispatcher) {
        // Act
        setFirstName(preferencesViewModel, "Alice", dispatcher = testDispatcher)
        testScheduler.advanceUntilIdle()

        // Assert
        verify(preferencesViewModel).savePreference(FIRST_NAME_KEY, "Alice")
      }

  @Test
  fun clearAccountPreferencesClearsAllPreferences() =
      runTest(testDispatcher) {
        // Act
        clearAccountPreferences(preferencesViewModel, dispatcher = testDispatcher)
        testScheduler.advanceUntilIdle()

        // Assert
        verify(preferencesViewModel).clearAllPreferences()
      }
}
