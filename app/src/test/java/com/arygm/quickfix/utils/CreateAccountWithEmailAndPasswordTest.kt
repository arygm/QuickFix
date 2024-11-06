package com.arygm.quickfix.utils

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.account.AccountRepositoryFirestore
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.account.LoggedInAccountViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.UserProfile
import com.arygm.quickfix.model.profile.UserProfileRepositoryFirestore
import com.arygm.quickfix.model.profile.WorkerProfileRepositoryFirestore
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class CreateAccountWithEmailAndPasswordTest {

  private lateinit var firebaseAuth: FirebaseAuth

  @Mock private lateinit var firebaseUser: FirebaseUser

  @Mock private lateinit var authResult: AuthResult

  @Mock private lateinit var accountRepository: AccountRepositoryFirestore

  @Mock private lateinit var userProfileRepository: UserProfileRepositoryFirestore

  @Mock private lateinit var workerProfileRepository: WorkerProfileRepositoryFirestore

  private lateinit var accountViewModel: AccountViewModel
  private lateinit var profileViewModel: ProfileViewModel
  private lateinit var loggedInAccountViewModel: LoggedInAccountViewModel

  private lateinit var firebaseAuthMockedStatic: MockedStatic<FirebaseAuth>

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    // Initialize FirebaseApp if necessary
    val context = ApplicationProvider.getApplicationContext<android.content.Context>()
    if (FirebaseApp.getApps(context).isEmpty()) {
      FirebaseApp.initializeApp(context)
    }

    // Mock FirebaseAuth.getInstance()
    firebaseAuthMockedStatic = Mockito.mockStatic(FirebaseAuth::class.java)
    firebaseAuth = Mockito.mock(FirebaseAuth::class.java)

    firebaseAuthMockedStatic
        .`when`<FirebaseAuth> { FirebaseAuth.getInstance() }
        .thenReturn(firebaseAuth)

    // Initialize accountViewModel and profileViewModel with the mocked repositories
    accountViewModel = AccountViewModel(accountRepository)
    profileViewModel = ProfileViewModel(userProfileRepository)

    // Initialize loggedInAccountViewModel with the mocked repositories
    loggedInAccountViewModel =
        LoggedInAccountViewModel(userProfileRepository, workerProfileRepository)

    // Mock FirebaseAuth.getInstance().currentUser
    whenever(firebaseAuth.currentUser).thenReturn(firebaseUser)
  }

  @After
  fun tearDown() {
    // Close the static mock
    firebaseAuthMockedStatic.close()
  }

  @Test
  fun testCreateAccountSuccess() {
    // Prepare test data
    val firstName = "John"
    val lastName = "Doe"
    val email = "john.doe@example.com"
    val password = "Password1"
    val birthDate = "01/01/1990"
    val uid = "testUid"

    // Mock FirebaseAuth behavior
    val authTaskCompletionSource = TaskCompletionSource<AuthResult>()
    whenever(firebaseAuth.createUserWithEmailAndPassword(any(), any()))
        .thenReturn(authTaskCompletionSource.task)
    whenever(authResult.user).thenReturn(firebaseUser)
    whenever(firebaseUser.uid).thenReturn(uid)
    whenever(firebaseAuth.currentUser).thenReturn(firebaseUser)

    // Mock UserProfileRepository behavior to succeed
    whenever(userProfileRepository.addProfile(any(), any(), any())).thenAnswer { invocation ->
      val onSuccessCallback = invocation.getArgument<() -> Unit>(1)
      onSuccessCallback()
      null
    }

    // Mock AccountRepository behavior to succeed
    whenever(accountRepository.addAccount(any(), any(), any())).thenAnswer { invocation ->
      val onSuccessCallback = invocation.getArgument<() -> Unit>(1)
      onSuccessCallback()
      null
    }

    // Flags to check callbacks
    var successCalled = false
    var failureCalled = false

    // Invoke the function under test
    createAccountWithEmailAndPassword(
        firebaseAuth = firebaseAuth,
        firstName = firstName,
        lastName = lastName,
        email = email,
        password = password,
        birthDate = birthDate,
        accountViewModel = accountViewModel,
        loggedInAccountViewModel = loggedInAccountViewModel,
        userViewModel = profileViewModel,
        onSuccess = { successCalled = true },
        onFailure = { failureCalled = true })

    // Simulate task completion
    authTaskCompletionSource.setResult(authResult)

    // Allow background tasks to complete
    shadowOf(Looper.getMainLooper()).idle()

    // Assertions
    assertTrue(successCalled)
    assertFalse(failureCalled)

    // Verify that the user profile was added correctly
    val userProfileCaptor = argumentCaptor<UserProfile>()
    verify(userProfileRepository).addProfile(userProfileCaptor.capture(), any(), any())
    val capturedUserProfile = userProfileCaptor.firstValue
    assertEquals(uid, capturedUserProfile.uid)
    // Additional checks can be added if necessary

    // Verify that the account was added correctly
    val accountCaptor = argumentCaptor<Account>()
    verify(accountRepository).addAccount(accountCaptor.capture(), any(), any())
    val capturedAccount = accountCaptor.firstValue
    assertEquals(uid, capturedAccount.uid)
    assertEquals(firstName, capturedAccount.firstName)
    assertEquals(lastName, capturedAccount.lastName)
    assertEquals(email, capturedAccount.email)

    // Verify that the loggedInAccount was set
    assertEquals(capturedAccount, loggedInAccountViewModel.loggedInAccount.value)
  }

  @Test
  fun testCreateAccountFirebaseFailure() {
    // Prepare test data
    val firstName = "John"
    val lastName = "Doe"
    val email = "john.doe@example.com"
    val password = "Password1"
    val birthDate = "01/01/1990"
    val exception = Exception("Firebase error")

    // Mock FirebaseAuth failure
    val authTaskCompletionSource = TaskCompletionSource<AuthResult>()
    whenever(firebaseAuth.createUserWithEmailAndPassword(any(), any()))
        .thenReturn(authTaskCompletionSource.task)

    // Flags to check callbacks
    var successCalled = false
    var failureCalled = false

    // Invoke the function under test
    createAccountWithEmailAndPassword(
        firebaseAuth = firebaseAuth,
        firstName = firstName,
        lastName = lastName,
        email = email,
        password = password,
        birthDate = birthDate,
        accountViewModel = accountViewModel,
        loggedInAccountViewModel = loggedInAccountViewModel,
        userViewModel = profileViewModel,
        onSuccess = { successCalled = true },
        onFailure = { failureCalled = true })

    // Simulate task failure
    authTaskCompletionSource.setException(exception)

    // Allow background tasks to complete
    shadowOf(Looper.getMainLooper()).idle()

    // Assertions
    assertFalse(successCalled)
    assertTrue(failureCalled)
  }

  @Test
  fun testCreateAccountProfileFailure() {
    // Prepare test data
    val firstName = "John"
    val lastName = "Doe"
    val email = "john.doe@example.com"
    val password = "Password1"
    val birthDate = "01/01/1990"
    val uid = "testUid"
    val exception = Exception("Profile creation failed")

    // Mock FirebaseAuth success
    val authTaskCompletionSource = TaskCompletionSource<AuthResult>()
    whenever(firebaseAuth.createUserWithEmailAndPassword(any(), any()))
        .thenReturn(authTaskCompletionSource.task)
    whenever(authResult.user).thenReturn(firebaseUser)
    whenever(firebaseUser.uid).thenReturn(uid)
    whenever(firebaseAuth.currentUser).thenReturn(firebaseUser)

    // Mock UserProfileRepository behavior to fail
    whenever(userProfileRepository.addProfile(any(), any(), any())).thenAnswer { invocation ->
      val onFailureCallback = invocation.getArgument<(Exception) -> Unit>(2)
      onFailureCallback(exception)
      null
    }

    // Flags to check callbacks
    var successCalled = false
    var failureCalled = false

    // Invoke the function under test
    createAccountWithEmailAndPassword(
        firebaseAuth = firebaseAuth,
        firstName = firstName,
        lastName = lastName,
        email = email,
        password = password,
        birthDate = birthDate,
        accountViewModel = accountViewModel,
        loggedInAccountViewModel = loggedInAccountViewModel,
        userViewModel = profileViewModel,
        onSuccess = { successCalled = true },
        onFailure = { failureCalled = true })

    // Simulate task completion
    authTaskCompletionSource.setResult(authResult)

    // Allow background tasks to complete
    shadowOf(Looper.getMainLooper()).idle()

    // Assertions
    assertFalse(successCalled)
    assertTrue(failureCalled)
  }

  @Test
  fun testCreateAccountWithInvalidBirthDate() {
    // Prepare test data
    val firstName = "Tom"
    val lastName = "Brown"
    val email = "tom.brown@example.com"
    val password = "Password456"
    val birthDate = "invalid date"
    val uid = "testUid"

    // Mock FirebaseAuth behavior
    val authTaskCompletionSource = TaskCompletionSource<AuthResult>()
    whenever(firebaseAuth.createUserWithEmailAndPassword(any(), any()))
        .thenReturn(authTaskCompletionSource.task)
    whenever(authResult.user).thenReturn(firebaseUser)
    whenever(firebaseUser.uid).thenReturn(uid)
    whenever(firebaseAuth.currentUser).thenReturn(firebaseUser)

    // Flags to check callbacks
    var successCalled = false
    var failureCalled = false

    // Invoke the function under test
    createAccountWithEmailAndPassword(
        firebaseAuth = firebaseAuth,
        firstName = firstName,
        lastName = lastName,
        email = email,
        password = password,
        birthDate = birthDate,
        accountViewModel = accountViewModel,
        loggedInAccountViewModel = loggedInAccountViewModel,
        userViewModel = profileViewModel,
        onSuccess = { successCalled = true },
        onFailure = { failureCalled = true })

    // Simulate task completion
    authTaskCompletionSource.setResult(authResult)

    // Allow background tasks to complete
    shadowOf(Looper.getMainLooper()).idle()

    // Assertions
    assertFalse(successCalled)
    assertTrue(failureCalled)
  }

  @Test
  fun testCreateAccountWithNullCallbacks() {
    // Prepare test data
    val firstName = "Alice"
    val lastName = "Johnson"
    val email = "alice.johnson@example.com"
    val password = "Password789"
    val birthDate = "03/03/1993"
    val uid = "testUid"

    // Mock FirebaseAuth behavior
    val authTaskCompletionSource = TaskCompletionSource<AuthResult>()
    whenever(firebaseAuth.createUserWithEmailAndPassword(any(), any()))
        .thenReturn(authTaskCompletionSource.task)
    whenever(authResult.user).thenReturn(firebaseUser)
    whenever(firebaseUser.uid).thenReturn(uid)
    whenever(firebaseAuth.currentUser).thenReturn(firebaseUser)

    // Mock UserProfileRepository behavior to succeed
    whenever(userProfileRepository.addProfile(any(), any(), any())).thenAnswer { invocation ->
      val onSuccessCallback = invocation.getArgument<() -> Unit>(1)
      onSuccessCallback()
      null
    }

    // Mock AccountRepository behavior to succeed
    whenever(accountRepository.addAccount(any(), any(), any())).thenAnswer { invocation ->
      val onSuccessCallback = invocation.getArgument<() -> Unit>(1)
      onSuccessCallback()
      null
    }

    // Invoke the function under test without callbacks
    createAccountWithEmailAndPassword(
        firebaseAuth = firebaseAuth,
        firstName = firstName,
        lastName = lastName,
        email = email,
        password = password,
        birthDate = birthDate,
        accountViewModel = accountViewModel,
        loggedInAccountViewModel = loggedInAccountViewModel,
        userViewModel = profileViewModel,
        onSuccess = {},
        onFailure = {})

    // Simulate task completion
    authTaskCompletionSource.setResult(authResult)

    // Allow background tasks to complete
    shadowOf(Looper.getMainLooper()).idle()

    // Since callbacks are empty, we just verify that no exceptions are thrown
  }

  @Test
  fun testCreateAccountWithInvalidEmail() {
    // Prepare test data
    val firstName = "Noah"
    val lastName = "Anderson"
    val email = "invalid-email"
    val password = "Password444"
    val birthDate = "08/08/1998"
    val exception = Exception("Invalid email format")

    // Mock FirebaseAuth failure due to invalid email
    val authTaskCompletionSource = TaskCompletionSource<AuthResult>()
    whenever(firebaseAuth.createUserWithEmailAndPassword(any(), any()))
        .thenReturn(authTaskCompletionSource.task)

    // Flags to check callbacks
    var successCalled = false
    var failureCalled = false

    // Invoke the function under test
    createAccountWithEmailAndPassword(
        firebaseAuth = firebaseAuth,
        firstName = firstName,
        lastName = lastName,
        email = email,
        password = password,
        birthDate = birthDate,
        accountViewModel = accountViewModel,
        loggedInAccountViewModel = loggedInAccountViewModel,
        userViewModel = profileViewModel,
        onSuccess = { successCalled = true },
        onFailure = { failureCalled = true })

    // Simulate task failure
    authTaskCompletionSource.setException(exception)

    // Allow background tasks to complete
    shadowOf(Looper.getMainLooper()).idle()

    // Assertions
    assertFalse(successCalled)
    assertTrue(failureCalled)
  }

  @Test
  fun testCreateAccountWhenUserAlreadyExists() {
    // Prepare test data
    val firstName = "Olivia"
    val lastName = "Thomas"
    val email = "olivia.thomas@example.com"
    val password = "Password555"
    val birthDate = "09/09/1999"
    val exception = Exception("User already exists")

    // Mock FirebaseAuth failure due to user already existing
    val authTaskCompletionSource = TaskCompletionSource<AuthResult>()
    whenever(firebaseAuth.createUserWithEmailAndPassword(any(), any()))
        .thenReturn(authTaskCompletionSource.task)

    // Flags to check callbacks
    var successCalled = false
    var failureCalled = false

    // Invoke the function under test
    createAccountWithEmailAndPassword(
        firebaseAuth = firebaseAuth,
        firstName = firstName,
        lastName = lastName,
        email = email,
        password = password,
        birthDate = birthDate,
        accountViewModel = accountViewModel,
        loggedInAccountViewModel = loggedInAccountViewModel,
        userViewModel = profileViewModel,
        onSuccess = { successCalled = true },
        onFailure = { failureCalled = true })

    // Simulate task failure
    authTaskCompletionSource.setException(exception)

    // Allow background tasks to complete
    shadowOf(Looper.getMainLooper()).idle()

    // Assertions
    assertFalse(successCalled)
    assertTrue(failureCalled)
  }
}
