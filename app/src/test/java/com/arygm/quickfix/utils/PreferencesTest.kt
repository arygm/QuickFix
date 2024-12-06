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
  fun setAccountPreferencesSavesAllPreferences() =
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
        testScheduler.advanceUntilIdle()

        // Assert
        verify(preferencesViewModel).savePreference(IS_SIGN_IN_KEY, true)
        verify(preferencesViewModel).savePreference(UID_KEY, "user123")
        verify(preferencesViewModel).savePreference(FIRST_NAME_KEY, "Alice")
        verify(preferencesViewModel).savePreference(LAST_NAME_KEY, "Smith")
        verify(preferencesViewModel).savePreference(EMAIL_KEY, "alice.smith@example.com")
        verify(preferencesViewModel).savePreference(BIRTH_DATE_KEY, "15/05/1990")
        verify(preferencesViewModel).savePreference(IS_WORKER_KEY, true)
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

  @Test
  fun setSignInSavesCorrectValue() =
      runTest(testDispatcher) {
        // Act
        setSignIn(preferencesViewModel, true, dispatcher = testDispatcher)
        testScheduler.advanceUntilIdle()

        // Assert
        verify(preferencesViewModel).savePreference(IS_SIGN_IN_KEY, true)
      }

  @Test
  fun setUserIdSavesCorrectValue() =
      runTest(testDispatcher) {
        // Act
        setUserId(preferencesViewModel, "user123", dispatcher = testDispatcher)
        testScheduler.advanceUntilIdle()

        // Assert
        verify(preferencesViewModel).savePreference(UID_KEY, "user123")
      }

  @Test
  fun setFirstNameSavesCorrectValue() =
      runTest(testDispatcher) {
        // Act
        setFirstName(preferencesViewModel, "Alice", dispatcher = testDispatcher)
        testScheduler.advanceUntilIdle()

        // Assert
        verify(preferencesViewModel).savePreference(FIRST_NAME_KEY, "Alice")
      }

  @Test
  fun setLastNameSavesCorrectValue() =
      runTest(testDispatcher) {
        // Act
        setLastName(preferencesViewModel, "Smith", dispatcher = testDispatcher)
        testScheduler.advanceUntilIdle()

        // Assert
        verify(preferencesViewModel).savePreference(LAST_NAME_KEY, "Smith")
      }

  @Test
  fun setEmailSavesCorrectValue() =
      runTest(testDispatcher) {
        // Act
        setEmail(preferencesViewModel, "alice.smith@example.com", dispatcher = testDispatcher)
        testScheduler.advanceUntilIdle()

        // Assert
        verify(preferencesViewModel).savePreference(EMAIL_KEY, "alice.smith@example.com")
      }

  @Test
  fun setBirthDateSavesCorrectValue() =
      runTest(testDispatcher) {
        // Act
        setBirthDate(preferencesViewModel, "15/05/1990", dispatcher = testDispatcher)
        testScheduler.advanceUntilIdle()

        // Assert
        verify(preferencesViewModel).savePreference(BIRTH_DATE_KEY, "15/05/1990")
      }

  @Test
  fun setIsWorkerSavesCorrectValue() =
      runTest(testDispatcher) {
        // Act
        setIsWorker(preferencesViewModel, true, dispatcher = testDispatcher)
        testScheduler.advanceUntilIdle()

        // Assert
        verify(preferencesViewModel).savePreference(IS_WORKER_KEY, true)
      }

  @Test
  fun loadIsSignInReturnsCorrectValue() =
      runTest(testDispatcher) {
        // Arrange
        whenever(preferencesViewModel.loadPreference(eq(IS_SIGN_IN_KEY), any())).thenAnswer {
          val callback = it.getArgument<(Boolean?) -> Unit>(1)
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
          val callback = it.getArgument<(String?) -> Unit>(1)
          callback("Alice")
        }

        // Act
        val result = loadFirstName(preferencesViewModel)

        // Assert
        assertEquals("Alice", result)
      }

  @Test
  fun loadLastNameReturnsCorrectValue() =
      runTest(testDispatcher) {
        // Arrange
        whenever(preferencesViewModel.loadPreference(eq(LAST_NAME_KEY), any())).thenAnswer {
          val callback = it.getArgument<(String?) -> Unit>(1)
          callback("Smith")
        }

        // Act
        val result = loadLastName(preferencesViewModel)

        // Assert
        assertEquals("Smith", result)
      }

  @Test
  fun loadEmailReturnsCorrectValue() =
      runTest(testDispatcher) {
        // Arrange
        whenever(preferencesViewModel.loadPreference(eq(EMAIL_KEY), any())).thenAnswer {
          val callback = it.getArgument<(String?) -> Unit>(1)
          callback("alice.smith@example.com")
        }

        // Act
        val result = loadEmail(preferencesViewModel)

        // Assert
        assertEquals("alice.smith@example.com", result)
      }

  @Test
  fun loadBirthDateReturnsCorrectValue() =
      runTest(testDispatcher) {
        // Arrange
        whenever(preferencesViewModel.loadPreference(eq(BIRTH_DATE_KEY), any())).thenAnswer {
          val callback = it.getArgument<(String?) -> Unit>(1)
          callback("15/05/1990")
        }

        // Act
        val result = loadBirthDate(preferencesViewModel)

        // Assert
        assertEquals("15/05/1990", result)
      }

  @Test
  fun loadIsWorkerReturnsCorrectValue() =
      runTest(testDispatcher) {
        // Arrange
        whenever(preferencesViewModel.loadPreference(eq(IS_WORKER_KEY), any())).thenAnswer {
          val callback = it.getArgument<(Boolean?) -> Unit>(1)
          callback(true)
        }

        // Act
        val result = loadIsWorker(preferencesViewModel)

        // Assert
        assertEquals(true, result)
      }
}
