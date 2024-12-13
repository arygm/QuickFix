package com.arygm.quickfix.utils

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.account.AccountRepositoryFirestore
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.offline.small.PreferencesRepository
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.UserProfileRepositoryFirestore
import com.arygm.quickfix.model.profile.WorkerProfileRepositoryFirestore
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.FirebaseApp
import com.google.firebase.Timestamp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class SignInWithEmailAndPasswordTest {

  private lateinit var firebaseAuth: FirebaseAuth

  @Mock private lateinit var firebaseUser: FirebaseUser

  @Mock private lateinit var authResult: AuthResult

  @Mock private lateinit var accountRepository: AccountRepositoryFirestore

  @Mock private lateinit var userProfileRepo: UserProfileRepositoryFirestore

  @Mock private lateinit var workerProfileRepo: WorkerProfileRepositoryFirestore

  @Mock private lateinit var preferencesRepository: PreferencesRepository

  @Mock private lateinit var userPreferencesViewModel: PreferencesViewModel

  @Mock private lateinit var userViewModel: ProfileViewModel
  @Mock private lateinit var userProfileRepository: UserProfileRepositoryFirestore

  private lateinit var accountViewModel: AccountViewModel

  private lateinit var preferencesViewModel: PreferencesViewModel

  private lateinit var firebaseAuthMockedStatic: MockedStatic<FirebaseAuth>

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    // Initialize FirebaseApp if necessary
    val context = ApplicationProvider.getApplicationContext<android.content.Context>()
    if (FirebaseApp.getApps(context).isEmpty()) {
      FirebaseApp.initializeApp(context)
    }
    userProfileRepository = mock()
    // Mock FirebaseAuth.getInstance()
    firebaseAuthMockedStatic = Mockito.mockStatic(FirebaseAuth::class.java)
    firebaseAuth = Mockito.mock(FirebaseAuth::class.java)

    firebaseAuthMockedStatic
        .`when`<FirebaseAuth> { FirebaseAuth.getInstance() }
        .thenReturn(firebaseAuth)

    // Initialize accountViewModel with the mocked repository
    accountViewModel = AccountViewModel(accountRepository)
    preferencesViewModel = PreferencesViewModel(preferencesRepository)
    userPreferencesViewModel = PreferencesViewModel(preferencesRepository)
    userViewModel = ProfileViewModel(userProfileRepository)

    // Mock FirebaseAuth.getInstance().currentUser
    whenever(firebaseAuth.currentUser).thenReturn(firebaseUser)
  }

  @After
  fun tearDown() {
    firebaseAuthMockedStatic.close()
  }

  @Test
  fun testSignInSuccessAndAccountFetchSuccess() {
    // Prepare test data
    val email = "john.doe@example.com"
    val password = "password123"
    val uid = "testUid"

    val account =
        Account(
            uid = uid,
            firstName = "John",
            lastName = "Doe",
            email = email,
            birthDate = Timestamp.now(),
            isWorker = false)

    // Mock FirebaseAuth behavior
    val signInTaskCompletionSource = TaskCompletionSource<AuthResult>()
    whenever(firebaseAuth.signInWithEmailAndPassword(any(), any()))
        .thenReturn(signInTaskCompletionSource.task)
    whenever(authResult.user).thenReturn(firebaseUser)
    whenever(firebaseUser.uid).thenReturn(uid)
    whenever(firebaseAuth.currentUser).thenReturn(firebaseUser)

    // Mock accountRepository.getAccountById to return the account
    whenever(accountRepository.getAccountById(eq(uid), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.getArgument<(Account?) -> Unit>(1)
      onSuccess(account)
      null
    }

    // Flags to check callbacks
    var onResultCalled = false
    var resultValue: Boolean? = null

    // Invoke the function under test
    signInWithEmailAndFetchAccount(
        email = email,
        password = password,
        accountViewModel = accountViewModel,
        preferencesViewModel = preferencesViewModel,
        onResult = { result ->
          onResultCalled = true
          resultValue = result
        },
        userPreferencesViewModel,
        userViewModel)

    // Simulate task completion
    signInTaskCompletionSource.setResult(authResult)

    // Allow background tasks to complete
    shadowOf(Looper.getMainLooper()).idle()

    // Assertions
    assertTrue(onResultCalled)
    assertTrue(resultValue == true)

    // Verify that the account was fetched
    verify(accountRepository).getAccountById(eq(uid), any(), any())
  }

  @Test
  fun testSignInFailure() {
    // Prepare test data
    val email = "john.doe@example.com"
    val password = "wrongPassword"
    val exception = Exception("Authentication failed")

    // Mock FirebaseAuth behavior
    val signInTaskCompletionSource = TaskCompletionSource<AuthResult>()
    whenever(firebaseAuth.signInWithEmailAndPassword(any(), any()))
        .thenReturn(signInTaskCompletionSource.task)

    // Flags to check callbacks
    var onResultCalled = false
    var resultValue: Boolean? = null

    // Invoke the function under test
    signInWithEmailAndFetchAccount(
        email = email,
        password = password,
        accountViewModel = accountViewModel,
        preferencesViewModel = preferencesViewModel,
        onResult = { result ->
          onResultCalled = true
          resultValue = result
        },
        userPreferencesViewModel,
        userViewModel)

    // Simulate task failure
    signInTaskCompletionSource.setException(exception)

    // Allow background tasks to complete
    shadowOf(Looper.getMainLooper()).idle()

    // Assertions
    assertTrue(onResultCalled)
    assertFalse(resultValue == true)

    // Verify that getAccountById was never called
    verify(accountRepository, never()).getAccountById(any(), any(), any())
  }

  @Test
  fun testSignInSuccessButFetchAccountReturnsNull() {
    // Prepare test data
    val email = "john.doe@example.com"
    val password = "password123"
    val uid = "testUid"

    // Mock FirebaseAuth behavior
    val signInTaskCompletionSource = TaskCompletionSource<AuthResult>()
    whenever(firebaseAuth.signInWithEmailAndPassword(any(), any()))
        .thenReturn(signInTaskCompletionSource.task)
    whenever(authResult.user).thenReturn(firebaseUser)
    whenever(firebaseUser.uid).thenReturn(uid)
    whenever(firebaseAuth.currentUser).thenReturn(firebaseUser)

    // Mock accountRepository.getAccountById to return null
    whenever(accountRepository.getAccountById(eq(uid), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.getArgument<(Account?) -> Unit>(1)
      onSuccess(null)
      null
    }

    // Flags to check callbacks
    var onResultCalled = false
    var resultValue: Boolean? = null

    // Invoke the function under test
    signInWithEmailAndFetchAccount(
        email = email,
        password = password,
        accountViewModel = accountViewModel,
        preferencesViewModel = preferencesViewModel,
        onResult = { result ->
          onResultCalled = true
          resultValue = result
        },
        userPreferencesViewModel,
        userViewModel)

    // Simulate task completion
    signInTaskCompletionSource.setResult(authResult)

    // Allow background tasks to complete
    shadowOf(Looper.getMainLooper()).idle()

    // Assertions
    assertTrue(onResultCalled)
    assertFalse(resultValue == true)
  }

  @Test
  fun testSignInSuccessButNoCurrentUser() {
    // Prepare test data
    val email = "john.doe@example.com"
    val password = "password123"

    // Mock FirebaseAuth behavior
    val signInTaskCompletionSource = TaskCompletionSource<AuthResult>()
    whenever(firebaseAuth.signInWithEmailAndPassword(any(), any()))
        .thenReturn(signInTaskCompletionSource.task)

    // Simulate that currentUser is null
    whenever(firebaseAuth.currentUser).thenReturn(null)

    // Flags to check callbacks
    var onResultCalled = false
    var resultValue: Boolean? = null

    // Invoke the function under test
    signInWithEmailAndFetchAccount(
        email = email,
        password = password,
        accountViewModel = accountViewModel,
        preferencesViewModel = preferencesViewModel,
        onResult = { result ->
          onResultCalled = true
          resultValue = result
        },
        userPreferencesViewModel,
        userViewModel)

    // Simulate task completion
    signInTaskCompletionSource.setResult(authResult)

    // Allow background tasks to complete
    shadowOf(Looper.getMainLooper()).idle()

    // Assertions
    assertTrue(onResultCalled)
    assertFalse(resultValue == true)

    // Verify that getAccountById was not called
    verify(accountRepository, never()).getAccountById(any(), any(), any())
  }

  @Test
  fun testFetchUserAccountFails() {
    // Prepare test data
    val email = "john.doe@example.com"
    val password = "password123"
    val uid = "testUid"
    val exception = Exception("Account fetch failed")

    // Mock FirebaseAuth behavior
    val signInTaskCompletionSource = TaskCompletionSource<AuthResult>()
    whenever(firebaseAuth.signInWithEmailAndPassword(any(), any()))
        .thenReturn(signInTaskCompletionSource.task)
    whenever(authResult.user).thenReturn(firebaseUser)
    whenever(firebaseUser.uid).thenReturn(uid)
    whenever(firebaseAuth.currentUser).thenReturn(firebaseUser)

    // Mock accountRepository.getAccountById to call onFailure
    whenever(accountRepository.getAccountById(eq(uid), any(), any())).thenAnswer { invocation ->
      val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
      onFailure(exception)
      null
    }

    // Flags to check callbacks
    var onResultCalled = false
    var resultValue: Boolean? = null

    // Invoke the function under test
    signInWithEmailAndFetchAccount(
        email = email,
        password = password,
        accountViewModel = accountViewModel,
        preferencesViewModel = preferencesViewModel,
        onResult = { result ->
          onResultCalled = true
          resultValue = result
        },
        userPreferencesViewModel,
        userViewModel)

    // Simulate task completion
    signInTaskCompletionSource.setResult(authResult)

    // Allow background tasks to complete
    shadowOf(Looper.getMainLooper()).idle()

    // Assertions
    assertTrue(onResultCalled)
    assertFalse(resultValue == true)
  }

  @Test
  fun testSignInWithEmptyEmail() {
    // Prepare test data
    val email = ""
    val password = "password123"
    val exception = Exception("Email cannot be empty")

    // Mock FirebaseAuth behavior to fail
    val signInTaskCompletionSource = TaskCompletionSource<AuthResult>()
    whenever(firebaseAuth.signInWithEmailAndPassword(eq(email), any()))
        .thenReturn(signInTaskCompletionSource.task)

    // Flags to check callbacks
    var onResultCalled = false
    var resultValue: Boolean? = null

    // Invoke the function under test
    signInWithEmailAndFetchAccount(
        email = email,
        password = password,
        accountViewModel = accountViewModel,
        preferencesViewModel = preferencesViewModel,
        onResult = { result ->
          onResultCalled = true
          resultValue = result
        },
        userPreferencesViewModel,
        userViewModel)

    // Simulate task failure
    signInTaskCompletionSource.setException(exception)

    // Allow background tasks to complete
    shadowOf(Looper.getMainLooper()).idle()

    // Assertions
    assertTrue(onResultCalled)
    assertFalse(resultValue == true)

    // Verify that getAccountById was never called
    verify(accountRepository, never()).getAccountById(any(), any(), any())
  }

  @Test
  fun testSignInWithEmptyPassword() {
    // Prepare test data
    val email = "john.doe@example.com"
    val password = ""
    val exception = Exception("Password cannot be empty")

    // Mock FirebaseAuth behavior to fail
    val signInTaskCompletionSource = TaskCompletionSource<AuthResult>()
    whenever(firebaseAuth.signInWithEmailAndPassword(any(), eq(password)))
        .thenReturn(signInTaskCompletionSource.task)

    // Flags to check callbacks
    var onResultCalled = false
    var resultValue: Boolean? = null

    // Invoke the function under test
    signInWithEmailAndFetchAccount(
        email = email,
        password = password,
        accountViewModel = accountViewModel,
        preferencesViewModel = preferencesViewModel,
        onResult = { result ->
          onResultCalled = true
          resultValue = result
        },
        userPreferencesViewModel,
        userViewModel)

    // Simulate task failure
    signInTaskCompletionSource.setException(exception)

    // Allow background tasks to complete
    shadowOf(Looper.getMainLooper()).idle()

    // Assertions
    assertTrue(onResultCalled)
    assertFalse(resultValue == true)

    // Verify that getAccountById was never called
    verify(accountRepository, never()).getAccountById(any(), any(), any())
  }

  @Test
  fun testSignInWithInvalidEmailFormat() {
    // Prepare test data
    val email = "invalid-email"
    val password = "password123"
    val exception = Exception("Invalid email format")

    // Mock FirebaseAuth behavior to fail
    val signInTaskCompletionSource = TaskCompletionSource<AuthResult>()
    whenever(firebaseAuth.signInWithEmailAndPassword(eq(email), any()))
        .thenReturn(signInTaskCompletionSource.task)

    // Flags to check callbacks
    var onResultCalled = false
    var resultValue: Boolean? = null

    // Invoke the function under test
    signInWithEmailAndFetchAccount(
        email = email,
        password = password,
        accountViewModel = accountViewModel,
        preferencesViewModel = preferencesViewModel,
        onResult = { result ->
          onResultCalled = true
          resultValue = result
        },
        userPreferencesViewModel,
        userViewModel)

    // Simulate task failure
    signInTaskCompletionSource.setException(exception)

    // Allow background tasks to complete
    shadowOf(Looper.getMainLooper()).idle()

    // Assertions
    assertTrue(onResultCalled)
    assertFalse(resultValue == true)

    // Verify that getAccountById was never called
    verify(accountRepository, never()).getAccountById(any(), any(), any())
  }
}
