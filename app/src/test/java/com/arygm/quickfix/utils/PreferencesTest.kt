package com.arygm.quickfix.utils

import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.google.firebase.Timestamp
import java.util.Calendar
import java.util.GregorianCalendar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class PreferencesTest {

  private lateinit var preferencesViewModel: PreferencesViewModel

  @Before
  fun setup() {
    Dispatchers.setMain(Dispatchers.Unconfined)
    preferencesViewModel = mock()
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun `test setAccountPreferences saves all preferences`() = runTest {
    // Arrange
    val account =
        Account(
            uid = "testUid",
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            birthDate = Timestamp(GregorianCalendar(2023, Calendar.JANUARY, 1).time),
            isWorker = true)

    // Act
    setAccountPreferences(preferencesViewModel, account, signIn = true)

    // Assert
    verify(preferencesViewModel).savePreference(IS_SIGN_IN_KEY, true)
    verify(preferencesViewModel).savePreference(USER_ID_KEY, "testUid")
    verify(preferencesViewModel).savePreference(FIRST_NAME_KEY, "John")
    verify(preferencesViewModel).savePreference(LAST_NAME_KEY, "Doe")
    verify(preferencesViewModel).savePreference(EMAIL_KEY, "john.doe@example.com")
    verify(preferencesViewModel).savePreference(DATE_OF_BIRTH_KEY, "01/01/2023")
    verify(preferencesViewModel).savePreference(IS_WORKER_KEY, true)
  }

  @Test
  fun `test setAccountPreferences saves signOut state`() = runTest {
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
