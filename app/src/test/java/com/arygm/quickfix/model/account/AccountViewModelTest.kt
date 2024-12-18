package com.arygm.quickfix.model.account

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import junit.framework.TestCase.fail
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class AccountViewModelTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore

  @Mock private lateinit var mockDocumentReference: DocumentReference

  @Mock private lateinit var mockCollectionReference: CollectionReference

  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot

  @Mock private lateinit var mockQuerySnapshot: QuerySnapshot

  @Mock private lateinit var mockAccountQuerySnapshot: QuerySnapshot

  @Mock private lateinit var mockQuery: Query
  @Mock private lateinit var mockStorage: FirebaseStorage
  @Mock private lateinit var storageRef: StorageReference
  @Mock private lateinit var storageRef1: StorageReference
  @Mock private lateinit var storageRef2: StorageReference
  @Mock private lateinit var accountFolderRef: StorageReference

  private lateinit var mockFirebaseAuth: FirebaseAuth
  private lateinit var firebaseAuthMockedStatic: MockedStatic<FirebaseAuth>

  private lateinit var accountRepositoryFirestore: AccountRepositoryFirestore

  private val account =
      Account(
          uid = "1",
          firstName = "John",
          lastName = "Doe",
          email = "john.doe@example.com",
          birthDate = Timestamp.now(),
          isWorker = false,
          profilePicture = "https://example.com/profile.jpg")

  private val account2 =
      Account(
          uid = "2",
          firstName = "Jane",
          lastName = "Smith",
          email = "jane.smith@example.com",
          birthDate = Timestamp.now(),
          isWorker = true,
          profilePicture = "https://example.com/profile2.jpg")

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    firebaseAuthMockedStatic = Mockito.mockStatic(FirebaseAuth::class.java)
    mockFirebaseAuth = Mockito.mock(FirebaseAuth::class.java)

    // Mock FirebaseAuth.getInstance() to return the mockFirebaseAuth
    firebaseAuthMockedStatic
        .`when`<FirebaseAuth> { FirebaseAuth.getInstance() }
        .thenReturn(mockFirebaseAuth)
    `when`(mockStorage.reference).thenReturn(storageRef)

    accountRepositoryFirestore = AccountRepositoryFirestore(mockFirestore, mockStorage)

    // Mocking the collection reference
    `when`(mockFirestore.collection(eq("accounts"))).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(any())).thenReturn(mockDocumentReference)
    `when`(mockCollectionReference.document()).thenReturn(mockDocumentReference)

    // Mocking whereEqualTo with specific arguments
    `when`(mockCollectionReference.whereEqualTo(eq("email"), eq("john.doe@example.com")))
        .thenReturn(mockQuery)

    // Mock Query.get() to return the desired Task
    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))
  }

  @After
  fun tearDown() {
    // Close the static mock
    firebaseAuthMockedStatic.close()
  }

  // ----- CRUD Operation Tests -----

  @Test
  fun getAccounts_callsDocuments() {
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockAccountQuerySnapshot))
    `when`(mockAccountQuerySnapshot.documents).thenReturn(listOf())

    accountRepositoryFirestore.getAccounts(
        onSuccess = {}, onFailure = { fail("Failure callback should not be called") })

    verify(mockCollectionReference).get()
  }

  @Test
  fun addAccount_shouldCallFirestoreCollection() {
    `when`(mockDocumentReference.set(any<Account>())).thenReturn(Tasks.forResult(null))

    accountRepositoryFirestore.addAccount(account, onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).set(any())
  }

  @Test
  fun addAccount_whenSuccess_callsOnSuccess() {
    val taskCompletionSource = TaskCompletionSource<Void>()
    `when`(mockDocumentReference.set(any<Account>())).thenReturn(taskCompletionSource.task)

    var callbackCalled = false

    accountRepositoryFirestore.addAccount(
        account = account,
        onSuccess = { callbackCalled = true },
        onFailure = { fail("Failure callback should not be called") })

    taskCompletionSource.setResult(null)

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(callbackCalled)
  }

  @Test
  fun addAccount_whenFailure_callsOnFailure() {
    val taskCompletionSource = TaskCompletionSource<Void>()
    `when`(mockDocumentReference.set(any<Account>())).thenReturn(taskCompletionSource.task)

    val exception = Exception("Test exception")
    var callbackCalled = false
    var returnedException: Exception? = null

    accountRepositoryFirestore.addAccount(
        account = account,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { e ->
          callbackCalled = true
          returnedException = e
        })

    taskCompletionSource.setException(exception)

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(callbackCalled)
    assertEquals(exception, returnedException)
  }

  @Test
  fun updateAccount_shouldCallFirestoreCollection() {
    `when`(mockDocumentReference.set(any<Account>())).thenReturn(Tasks.forResult(null))

    accountRepositoryFirestore.updateAccount(account, onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).set(any())
  }

  @Test
  fun updateAccount_whenSuccess_callsOnSuccess() {
    val taskCompletionSource = TaskCompletionSource<Void>()
    `when`(mockDocumentReference.set(any<Account>())).thenReturn(taskCompletionSource.task)

    var callbackCalled = false

    accountRepositoryFirestore.updateAccount(
        account = account,
        onSuccess = { callbackCalled = true },
        onFailure = { fail("Failure callback should not be called") })

    taskCompletionSource.setResult(null)

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(callbackCalled)
  }

  @Test
  fun updateAccount_whenFailure_callsOnFailure() {
    val taskCompletionSource = TaskCompletionSource<Void>()
    `when`(mockDocumentReference.set(any<Account>())).thenReturn(taskCompletionSource.task)

    val exception = Exception("Test exception")
    var callbackCalled = false
    var returnedException: Exception? = null

    accountRepositoryFirestore.updateAccount(
        account = account,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { e ->
          callbackCalled = true
          returnedException = e
        })

    taskCompletionSource.setException(exception)

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(callbackCalled)
    assertEquals(exception, returnedException)
  }

  @Test
  fun deleteAccountById_shouldCallDocumentReferenceDelete() {
    `when`(mockDocumentReference.delete()).thenReturn(Tasks.forResult(null))

    accountRepositoryFirestore.deleteAccountById("1", onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).delete()
  }

  @Test
  fun deleteAccountById_whenSuccess_callsOnSuccess() {
    val taskCompletionSource = TaskCompletionSource<Void>()
    `when`(mockDocumentReference.delete()).thenReturn(taskCompletionSource.task)

    var callbackCalled = false

    accountRepositoryFirestore.deleteAccountById(
        id = "1",
        onSuccess = { callbackCalled = true },
        onFailure = { fail("Failure callback should not be called") })

    taskCompletionSource.setResult(null)

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(callbackCalled)
  }

  @Test
  fun deleteAccountById_whenFailure_callsOnFailure() {
    val taskCompletionSource = TaskCompletionSource<Void>()
    `when`(mockDocumentReference.delete()).thenReturn(taskCompletionSource.task)

    val exception = Exception("Test exception")
    var callbackCalled = false
    var returnedException: Exception? = null

    accountRepositoryFirestore.deleteAccountById(
        id = "1",
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { e ->
          callbackCalled = true
          returnedException = e
        })

    taskCompletionSource.setException(exception)

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(callbackCalled)
    assertEquals(exception, returnedException)
  }

  // ----- getAccountById Tests -----

  @Test
  fun getAccountById_whenDocumentExists_callsOnSuccessWithAccount() {
    val uid = "1"

    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.exists()).thenReturn(true)

    // Mocking the data returned from Firestore
    `when`(mockDocumentSnapshot.id).thenReturn(account.uid)
    `when`(mockDocumentSnapshot.getString("firstName")).thenReturn(account.firstName)
    `when`(mockDocumentSnapshot.getString("lastName")).thenReturn(account.lastName)
    `when`(mockDocumentSnapshot.getString("email")).thenReturn(account.email)
    `when`(mockDocumentSnapshot.getTimestamp("birthDate")).thenReturn(account.birthDate)
    `when`(mockDocumentSnapshot.getBoolean("worker")).thenReturn(account.isWorker)
    `when`(mockDocumentSnapshot.getString("profilePicture")).thenReturn(account.profilePicture)

    var callbackCalled = false

    accountRepositoryFirestore.getAccountById(
        uid = uid,
        onSuccess = { foundAccount ->
          callbackCalled = true
          assertEquals(account, foundAccount)
        },
        onFailure = { fail("Failure callback should not be called") })

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(callbackCalled)
  }

  @Test
  fun getAccountById_whenDocumentDoesNotExist_callsOnSuccessWithNull() {
    val uid = "nonexistent"

    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.exists()).thenReturn(false)

    var callbackCalled = false

    accountRepositoryFirestore.getAccountById(
        uid = uid,
        onSuccess = { foundAccount ->
          callbackCalled = true
          assertNull(foundAccount)
        },
        onFailure = { fail("Failure callback should not be called") })

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(callbackCalled)
  }

  @Test
  fun getAccountById_whenFailure_callsOnFailure() {
    val uid = "1"
    val exception = Exception("Test exception")

    `when`(mockDocumentReference.get()).thenReturn(Tasks.forException(exception))

    var failureCallbackCalled = false

    accountRepositoryFirestore.getAccountById(
        uid = uid,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { e ->
          failureCallbackCalled = true
          assertEquals(exception, e)
        })

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(failureCallbackCalled)
  }

  // ----- accountExists Tests -----

  @Test
  fun accountExists_whenAccountExists_callsOnSuccessWithTrueAndAccount() {
    val email = "john.doe@example.com"

    // **Mock the specific whereEqualTo call**
    `when`(mockCollectionReference.whereEqualTo(eq("email"), eq(email))).thenReturn(mockQuery)

    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockAccountQuerySnapshot))
    `when`(mockAccountQuerySnapshot.isEmpty).thenReturn(false)
    `when`(mockAccountQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))

    `when`(mockDocumentSnapshot.id).thenReturn(account.uid)
    `when`(mockDocumentSnapshot.getString("firstName")).thenReturn(account.firstName)
    `when`(mockDocumentSnapshot.getString("lastName")).thenReturn(account.lastName)
    `when`(mockDocumentSnapshot.getString("email")).thenReturn(account.email)
    `when`(mockDocumentSnapshot.getTimestamp("birthDate")).thenReturn(account.birthDate)
    `when`(mockDocumentSnapshot.getBoolean("worker")).thenReturn(account.isWorker)
    `when`(mockDocumentSnapshot.getString("profilePicture")).thenReturn(account.profilePicture)

    var callbackCalled = false

    accountRepositoryFirestore.accountExists(
        email = email,
        onSuccess = { (exists, foundAccount) ->
          callbackCalled = true
          assertTrue(exists)
          assertEquals(account, foundAccount)
        },
        onFailure = { fail("Failure callback should not be called") })

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(callbackCalled)
  }

  @Test
  fun accountExists_whenAccountDoesNotExist_callsOnSuccessWithFalseAndNull() {
    val email = "unknown@example.com"

    // **Mock the specific whereEqualTo call**
    `when`(mockCollectionReference.whereEqualTo(eq("email"), eq(email))).thenReturn(mockQuery)

    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockAccountQuerySnapshot))
    `when`(mockAccountQuerySnapshot.isEmpty).thenReturn(true)

    var callbackCalled = false

    accountRepositoryFirestore.accountExists(
        email = email,
        onSuccess = { (exists, foundAccount) ->
          callbackCalled = true
          assertFalse(exists)
          assertNull(foundAccount)
        },
        onFailure = { fail("Failure callback should not be called") })

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(callbackCalled)
  }

  @Test
  fun accountExists_whenFailure_callsOnFailure() {
    val email = "john.doe@example.com"
    val exception = Exception("Test exception")

    // **Mock the specific whereEqualTo call**
    `when`(mockCollectionReference.whereEqualTo(eq("email"), eq(email))).thenReturn(mockQuery)

    `when`(mockQuery.get()).thenReturn(Tasks.forException(exception))

    var failureCallbackCalled = false

    accountRepositoryFirestore.accountExists(
        email = email,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { e ->
          failureCallbackCalled = true
          assertEquals(exception, e)
        })

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(failureCallbackCalled)
  }

  // ----- getAccounts Tests -----

  @Test
  fun getAccounts_whenSuccess_callsOnSuccessWithAccounts() {
    val taskCompletionSource = TaskCompletionSource<QuerySnapshot>()
    `when`(mockCollectionReference.get()).thenReturn(taskCompletionSource.task)

    val document1 = Mockito.mock(DocumentSnapshot::class.java)
    val document2 = Mockito.mock(DocumentSnapshot::class.java)

    `when`(mockAccountQuerySnapshot.documents).thenReturn(listOf(document1, document2))

    // Mock data for first document
    `when`(document1.id).thenReturn(account.uid)
    `when`(document1.getString("firstName")).thenReturn(account.firstName)
    `when`(document1.getString("lastName")).thenReturn(account.lastName)
    `when`(document1.getString("email")).thenReturn(account.email)
    `when`(document1.getTimestamp("birthDate")).thenReturn(account.birthDate)
    `when`(document1.getBoolean("worker")).thenReturn(account.isWorker)
    `when`(document1.getString("profilePicture")).thenReturn(account.profilePicture)

    // Mock data for second document
    `when`(document2.id).thenReturn(account2.uid)
    `when`(document2.getString("firstName")).thenReturn(account2.firstName)
    `when`(document2.getString("lastName")).thenReturn(account2.lastName)
    `when`(document2.getString("email")).thenReturn(account2.email)
    `when`(document2.getTimestamp("birthDate")).thenReturn(account2.birthDate)
    `when`(document2.getBoolean("worker")).thenReturn(account2.isWorker)
    `when`(document2.getString("profilePicture")).thenReturn(account2.profilePicture)

    var callbackCalled = false
    var returnedAccounts: List<Account>? = null

    accountRepositoryFirestore.getAccounts(
        onSuccess = { fetchedAccounts ->
          callbackCalled = true
          returnedAccounts = fetchedAccounts
        },
        onFailure = { fail("Failure callback should not be called") })

    taskCompletionSource.setResult(mockAccountQuerySnapshot)

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(callbackCalled)
    assertNotNull(returnedAccounts)
    assertEquals(2, returnedAccounts!!.size)
    assertEquals(account, returnedAccounts!![0])
    assertEquals(account2, returnedAccounts!![1])
  }

  @Test
  fun getAccounts_whenFailure_callsOnFailure() {
    val taskCompletionSource = TaskCompletionSource<QuerySnapshot>()
    `when`(mockCollectionReference.get()).thenReturn(taskCompletionSource.task)

    val exception = Exception("Test exception")

    var callbackCalled = false
    var returnedException: Exception? = null

    accountRepositoryFirestore.getAccounts(
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { e ->
          callbackCalled = true
          returnedException = e
        })

    taskCompletionSource.setException(exception)

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(callbackCalled)
    assertEquals(exception, returnedException)
  }

  // ----- documentToAccount Tests -----

  @Test
  fun documentToAccount_whenAllFieldsArePresent_returnsAccount() {
    // Arrange
    val document = Mockito.mock(DocumentSnapshot::class.java)
    `when`(document.id).thenReturn(account.uid)
    `when`(document.getString("firstName")).thenReturn(account.firstName)
    `when`(document.getString("lastName")).thenReturn(account.lastName)
    `when`(document.getString("email")).thenReturn(account.email)
    `when`(document.getTimestamp("birthDate")).thenReturn(account.birthDate)
    `when`(document.getBoolean("worker")).thenReturn(account.isWorker)
    `when`(document.getString("profilePicture")).thenReturn(account.profilePicture)

    // Act
    val result = invokeDocumentToAccount(document)

    // Assert
    assertNotNull(result)
    assertEquals(account, result)
  }

  @Test
  fun documentToAccount_whenEssentialFieldsAreMissing_returnsNull() {
    // Arrange
    val document = Mockito.mock(DocumentSnapshot::class.java)
    `when`(document.id).thenReturn(account.uid)
    // Missing "firstName", "lastName", "email", "birthDate"
    `when`(document.getString("firstName")).thenReturn(null)
    `when`(document.getString("lastName")).thenReturn(null)
    `when`(document.getString("email")).thenReturn(null)
    `when`(document.getTimestamp("birthDate")).thenReturn(null)

    // Act
    val result = invokeDocumentToAccount(document)

    // Assert
    assertNull(result)
  }

  @Test
  fun documentToAccount_whenInvalidDataType_returnsNull() {
    // Arrange
    val document = Mockito.mock(DocumentSnapshot::class.java)
    `when`(document.id).thenReturn(account.uid)
    `when`(document.getString("firstName")).thenReturn(account.firstName)
    `when`(document.getString("lastName")).thenReturn(account.lastName)
    `when`(document.getString("email")).thenReturn(account.email)
    // "birthDate" field has invalid data type (e.g., String instead of Timestamp)
    `when`(document.getTimestamp("birthDate")).thenReturn(null)

    // Act
    val result = invokeDocumentToAccount(document)

    // Assert
    assertNull(result)
  }

  @Test
  fun documentToAccount_whenExceptionOccurs_returnsNull() {
    // Arrange
    val document = Mockito.mock(DocumentSnapshot::class.java)
    `when`(document.id).thenReturn(account.uid)
    // Simulate an exception when accessing the "firstName" field
    `when`(document.getString("firstName")).thenThrow(RuntimeException("Test exception"))

    // Act
    val result = invokeDocumentToAccount(document)

    // Assert
    assertNull(result)
  }

  @Test
  fun documentToAccount_whenExtraFieldsPresent_returnsAccount() {
    // Arrange
    val document = Mockito.mock(DocumentSnapshot::class.java)
    `when`(document.id).thenReturn(account.uid)
    `when`(document.getString("firstName")).thenReturn(account.firstName)
    `when`(document.getString("lastName")).thenReturn(account.lastName)
    `when`(document.getString("email")).thenReturn(account.email)
    `when`(document.getTimestamp("birthDate")).thenReturn(account.birthDate)
    // Extra field that is not used by the repository
    `when`(document.getString("isWorker")).thenReturn("false")
    `when`(document.getString("profilePicture")).thenReturn(account.profilePicture)

    // Act
    val result = invokeDocumentToAccount(document)

    // Assert
    assertNotNull(result)
    assertEquals(account, result)
  }

  // ----- Helper Method for Testing Private Method -----

  /** Uses reflection to invoke the private `documentToAccount` method. */
  private fun invokeDocumentToAccount(document: DocumentSnapshot): Account? {
    val method =
        AccountRepositoryFirestore::class
            .java
            .getDeclaredMethod("documentToAccount", DocumentSnapshot::class.java)
    method.isAccessible = true
    return method.invoke(accountRepositoryFirestore, document) as Account?
  }

  // ----- Init Method Tests -----

  @Test
  fun init_whenCurrentUserNotNull_callsOnSuccess() {
    val authStateListenerCaptor = argumentCaptor<FirebaseAuth.AuthStateListener>()
    val mockFirebaseUser = Mockito.mock(FirebaseUser::class.java)

    doNothing().`when`(mockFirebaseAuth).addAuthStateListener(authStateListenerCaptor.capture())
    `when`(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser)

    var callbackCalled = false

    accountRepositoryFirestore.init(onSuccess = { callbackCalled = true })

    // Simulate auth state change
    authStateListenerCaptor.firstValue.onAuthStateChanged(mockFirebaseAuth)

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(callbackCalled)
  }

  @Test
  fun init_whenCurrentUserIsNull_doesNotCallOnSuccess() {
    val authStateListenerCaptor = argumentCaptor<FirebaseAuth.AuthStateListener>()

    doNothing().`when`(mockFirebaseAuth).addAuthStateListener(authStateListenerCaptor.capture())
    `when`(mockFirebaseAuth.currentUser).thenReturn(null)

    var callbackCalled = false

    accountRepositoryFirestore.init(onSuccess = { callbackCalled = true })

    // Simulate auth state change
    authStateListenerCaptor.firstValue.onAuthStateChanged(mockFirebaseAuth)

    shadowOf(Looper.getMainLooper()).idle()

    assertFalse(callbackCalled)
  }
}
