package com.arygm.quickfix.model.quickfix

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.arygm.quickfix.model.bill.BillField
import com.arygm.quickfix.model.bill.Units
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.profile.dataFields.AddOnService
import com.arygm.quickfix.model.profile.dataFields.IncludedService
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
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class QuickFixRepositoryFirestoreTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore

  @Mock private lateinit var mockDocumentReference: DocumentReference

  @Mock private lateinit var mockCollectionReference: CollectionReference

  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot

  @Mock private lateinit var mockQuerySnapshot: QuerySnapshot

  @Mock private lateinit var mockQuickFixQuerySnapshot: QuerySnapshot

  @Mock private lateinit var mockQuery: Query

  private lateinit var mockFirebaseAuth: FirebaseAuth
  private lateinit var firebaseAuthMockedStatic: MockedStatic<FirebaseAuth>

  private lateinit var quickFixRepositoryFirestore: QuickFixRepositoryFirestore

  private val testTimestamp = Timestamp.now()
  private val testLocation = Location(latitude = 1.0, longitude = 2.0, name = "Test Location")
  private val testBillField =
      BillField(
          description = "Test Service",
          unit = Units.H,
          amount = 2.0,
          unitPrice = 50.0,
          total = 100.0)
  private val testQuickFix =
      QuickFix(
          uid = "1",
          status = Status.PENDING,
          imageUrl = listOf("http://example.com/image1.jpg"),
          date = listOf(testTimestamp),
          time = testTimestamp,
          includedServices = listOf(IncludedService("Painting")),
          addOnServices = listOf(AddOnService("Wall Repair")),
          workerId = "Worker Id A",
          userId = "User Id B",
          chatUid = "chat123",
          title = "Fix My Wall",
          description = "I need help fixing my wall",
          bill = listOf(testBillField),
          location = testLocation)

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    firebaseAuthMockedStatic = Mockito.mockStatic(FirebaseAuth::class.java)
    mockFirebaseAuth = Mockito.mock(FirebaseAuth::class.java)

    firebaseAuthMockedStatic
        .`when`<FirebaseAuth> { FirebaseAuth.getInstance() }
        .thenReturn(mockFirebaseAuth)

    quickFixRepositoryFirestore = QuickFixRepositoryFirestore(mockFirestore)

    whenever(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
    whenever(mockCollectionReference.document(any())).thenReturn(mockDocumentReference)
    whenever(mockCollectionReference.document()).thenReturn(mockDocumentReference)
    whenever(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockQuickFixQuerySnapshot))
  }

  @After
  fun tearDown() {
    firebaseAuthMockedStatic.close()
  }

  // ----- CRUD Operation Tests -----

  @Test
  fun getQuickFixes_callsDocuments() {
    whenever(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockQuickFixQuerySnapshot))
    whenever(mockQuickFixQuerySnapshot.documents).thenReturn(listOf())

    quickFixRepositoryFirestore.getQuickFixes(
        onSuccess = {}, onFailure = { fail("Failure callback should not be called") })

    verify(mockCollectionReference).get()
  }

  @Test
  fun addQuickFix_shouldCallFirestoreCollection() {
    whenever(mockDocumentReference.set(any<QuickFix>())).thenReturn(Tasks.forResult(null))

    quickFixRepositoryFirestore.addQuickFix(testQuickFix, onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).set(any())
  }

  @Test
  fun addQuickFix_whenSuccess_callsOnSuccess() {
    val taskCompletionSource = TaskCompletionSource<Void>()
    whenever(mockDocumentReference.set(any<QuickFix>())).thenReturn(taskCompletionSource.task)

    var callbackCalled = false

    quickFixRepositoryFirestore.addQuickFix(
        quickFix = testQuickFix,
        onSuccess = { callbackCalled = true },
        onFailure = { fail("Failure callback should not be called") })

    taskCompletionSource.setResult(null)

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(callbackCalled)
  }

  @Test
  fun addQuickFix_whenFailure_callsOnFailure() {
    val taskCompletionSource = TaskCompletionSource<Void>()
    whenever(mockDocumentReference.set(any<QuickFix>())).thenReturn(taskCompletionSource.task)

    val exception = Exception("Test exception")
    var callbackCalled = false
    var returnedException: Exception? = null

    quickFixRepositoryFirestore.addQuickFix(
        quickFix = testQuickFix,
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
  fun updateQuickFix_shouldCallFirestoreCollection() {
    whenever(mockDocumentReference.set(any<QuickFix>())).thenReturn(Tasks.forResult(null))

    quickFixRepositoryFirestore.updateQuickFix(testQuickFix, onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).set(any())
  }

  @Test
  fun updateQuickFix_whenSuccess_callsOnSuccess() {
    val taskCompletionSource = TaskCompletionSource<Void>()
    whenever(mockDocumentReference.set(any<QuickFix>())).thenReturn(taskCompletionSource.task)

    var callbackCalled = false

    quickFixRepositoryFirestore.updateQuickFix(
        quickFix = testQuickFix,
        onSuccess = { callbackCalled = true },
        onFailure = { fail("Failure callback should not be called") })

    taskCompletionSource.setResult(null)

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(callbackCalled)
  }

  @Test
  fun updateQuickFix_whenFailure_callsOnFailure() {
    val taskCompletionSource = TaskCompletionSource<Void>()
    whenever(mockDocumentReference.set(any<QuickFix>())).thenReturn(taskCompletionSource.task)

    val exception = Exception("Test exception")
    var callbackCalled = false
    var returnedException: Exception? = null

    quickFixRepositoryFirestore.updateQuickFix(
        quickFix = testQuickFix,
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
  fun deleteQuickFixById_shouldCallDocumentReferenceDelete() {
    whenever(mockDocumentReference.delete()).thenReturn(Tasks.forResult(null))

    quickFixRepositoryFirestore.deleteQuickFixById("1", onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).delete()
  }

  @Test
  fun deleteQuickFixById_whenSuccess_callsOnSuccess() {
    val taskCompletionSource = TaskCompletionSource<Void>()
    whenever(mockDocumentReference.delete()).thenReturn(taskCompletionSource.task)

    var callbackCalled = false

    quickFixRepositoryFirestore.deleteQuickFixById(
        id = "1",
        onSuccess = { callbackCalled = true },
        onFailure = { fail("Failure callback should not be called") })

    taskCompletionSource.setResult(null)

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(callbackCalled)
  }

  @Test
  fun deleteQuickFixById_whenFailure_callsOnFailure() {
    val taskCompletionSource = TaskCompletionSource<Void>()
    whenever(mockDocumentReference.delete()).thenReturn(taskCompletionSource.task)

    val exception = Exception("Test exception")
    var callbackCalled = false
    var returnedException: Exception? = null

    quickFixRepositoryFirestore.deleteQuickFixById(
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

  // ----- getQuickFixById Tests -----

  @Test
  fun getQuickFixById_whenDocumentExists_callsOnSuccessWithQuickFix() {
    val uid = "1"

    whenever(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    whenever(mockDocumentSnapshot.exists()).thenReturn(true)

    mockDocumentSnapshotData(mockDocumentSnapshot, testQuickFix)

    var callbackCalled = false

    quickFixRepositoryFirestore.getQuickFixById(
        uid = uid,
        onSuccess = { foundQuickFix ->
          callbackCalled = true
          assertEquals(testQuickFix, foundQuickFix)
        },
        onFailure = { fail("Failure callback should not be called") })

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(callbackCalled)
  }

  @Test
  fun getQuickFixById_whenDocumentDoesNotExist_callsOnSuccessWithNull() {
    val uid = "nonexistent"

    whenever(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    whenever(mockDocumentSnapshot.exists()).thenReturn(false)

    var callbackCalled = false

    quickFixRepositoryFirestore.getQuickFixById(
        uid = uid,
        onSuccess = { foundQuickFix ->
          callbackCalled = true
          assertNull(foundQuickFix)
        },
        onFailure = { fail("Failure callback should not be called") })

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(callbackCalled)
  }

  @Test
  fun getQuickFixById_whenFailure_callsOnFailure() {
    val uid = "1"
    val exception = Exception("Test exception")

    whenever(mockDocumentReference.get()).thenReturn(Tasks.forException(exception))

    var failureCallbackCalled = false

    quickFixRepositoryFirestore.getQuickFixById(
        uid = uid,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { e ->
          failureCallbackCalled = true
          assertEquals(exception, e)
        })

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(failureCallbackCalled)
  }

  // ----- getQuickFixes Tests -----

  @Test
  fun getQuickFixes_whenSuccess_callsOnSuccessWithQuickFixes() {
    val taskCompletionSource = TaskCompletionSource<QuerySnapshot>()
    whenever(mockCollectionReference.get()).thenReturn(taskCompletionSource.task)

    val document1 = Mockito.mock(DocumentSnapshot::class.java)
    val document2 = Mockito.mock(DocumentSnapshot::class.java)

    val testQuickFix2 = testQuickFix.copy(uid = "2", title = "Fix My Door")

    val documents = listOf(document1, document2)
    whenever(mockQuickFixQuerySnapshot.documents).thenReturn(documents)

    mockDocumentSnapshotData(document1, testQuickFix)
    mockDocumentSnapshotData(document2, testQuickFix2)

    var callbackCalled = false
    var returnedQuickFixes: List<QuickFix>? = null

    quickFixRepositoryFirestore.getQuickFixes(
        onSuccess = { quickFixes ->
          callbackCalled = true
          returnedQuickFixes = quickFixes
        },
        onFailure = { fail("Failure callback should not be called") })

    taskCompletionSource.setResult(mockQuickFixQuerySnapshot)

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(callbackCalled)
    assertNotNull(returnedQuickFixes)
    assertEquals(2, returnedQuickFixes!!.size)
    assertEquals(testQuickFix, returnedQuickFixes!![0])
    assertEquals(testQuickFix2, returnedQuickFixes!![1])
  }

  @Test
  fun getQuickFixes_whenFailure_callsOnFailure() {
    val taskCompletionSource = TaskCompletionSource<QuerySnapshot>()
    whenever(mockCollectionReference.get()).thenReturn(taskCompletionSource.task)

    val exception = Exception("Test exception")

    var callbackCalled = false
    var returnedException: Exception? = null

    quickFixRepositoryFirestore.getQuickFixes(
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

  // ----- documentToQuickFix Tests -----

  @Test
  fun documentToQuickFix_whenAllFieldsArePresent_returnsQuickFix() {
    // Arrange
    val document = Mockito.mock(DocumentSnapshot::class.java)
    mockDocumentSnapshotData(document, testQuickFix)

    // Act
    val result = invokeDocumentToQuickFix(document)

    // Assert
    assertNotNull(result)
    assertEquals(testQuickFix, result)
  }

  @Test
  fun documentToQuickFix_whenEssentialFieldsAreMissing_returnsNull() {
    // Arrange
    val document = Mockito.mock(DocumentSnapshot::class.java)
    whenever(document.id).thenReturn(testQuickFix.uid)
    // Missing "status", which is essential
    whenever(document.getString("status")).thenReturn(null)

    // Act
    val result = invokeDocumentToQuickFix(document)

    // Assert
    assertNull(result)
  }

  @Test
  fun documentToQuickFix_whenExceptionOccurs_returnsNull() {
    // Arrange
    val document = Mockito.mock(DocumentSnapshot::class.java)
    whenever(document.id).thenReturn(testQuickFix.uid)
    // Simulate an exception when accessing the "status" field
    whenever(document.getString("status")).thenThrow(RuntimeException("Test exception"))

    // Act
    val result = invokeDocumentToQuickFix(document)

    // Assert
    assertNull(result)
  }

  // ----- Helper Methods -----

  private fun mockDocumentSnapshotData(document: DocumentSnapshot, quickFix: QuickFix) {
    whenever(document.id).thenReturn(quickFix.uid)
    whenever(document.getString("uid")).thenReturn(quickFix.uid)
    whenever(document.getString("status")).thenReturn(quickFix.status.name)
    whenever(document.get("imageUrl")).thenReturn(quickFix.imageUrl)
    whenever(document.get("date")).thenReturn(quickFix.date)
    whenever(document.getTimestamp("time")).thenReturn(quickFix.time)
    whenever(document.get("includedServices"))
        .thenReturn(quickFix.includedServices.map { mapOf("name" to it.name) })
    whenever(document.get("addOnServices"))
        .thenReturn(quickFix.addOnServices.map { mapOf("name" to it.name) })
    whenever(document.getString("workerId")).thenReturn(quickFix.workerId)
    whenever(document.getString("userId")).thenReturn(quickFix.userId)
    whenever(document.getString("chatUid")).thenReturn(quickFix.chatUid)
    whenever(document.getString("title")).thenReturn(quickFix.title)
    whenever(document.get("bill"))
        .thenReturn(
            quickFix.bill.map {
              mapOf(
                  "description" to it.description,
                  "unit" to it.unit.name,
                  "amount" to it.amount,
                  "unitPrice" to it.unitPrice,
                  "total" to it.total)
            })
    whenever(document.get("location"))
        .thenReturn(
            mapOf(
                "latitude" to quickFix.location.latitude,
                "longitude" to quickFix.location.longitude,
                "name" to quickFix.location.name))
  }

  /** Uses reflection to invoke the private `documentToQuickFix` method. */
  private fun invokeDocumentToQuickFix(document: DocumentSnapshot): QuickFix? {
    val method =
        QuickFixRepositoryFirestore::class
            .java
            .getDeclaredMethod("documentToQuickFix", DocumentSnapshot::class.java)
    method.isAccessible = true
    return method.invoke(quickFixRepositoryFirestore, document) as QuickFix?
  }

  // ----- Init Method Tests -----

  @Test
  fun init_whenCurrentUserNotNull_callsOnSuccess() {
    val authStateListenerCaptor = argumentCaptor<FirebaseAuth.AuthStateListener>()
    val mockFirebaseUser = Mockito.mock(FirebaseUser::class.java)

    doNothing().whenever(mockFirebaseAuth).addAuthStateListener(authStateListenerCaptor.capture())
    whenever(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser)

    var callbackCalled = false

    quickFixRepositoryFirestore.init(onSuccess = { callbackCalled = true })

    // Simulate auth state change
    authStateListenerCaptor.firstValue.onAuthStateChanged(mockFirebaseAuth)

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(callbackCalled)
  }

  @Test
  fun init_whenCurrentUserIsNull_doesNotCallOnSuccess() {
    val authStateListenerCaptor = argumentCaptor<FirebaseAuth.AuthStateListener>()

    doNothing().whenever(mockFirebaseAuth).addAuthStateListener(authStateListenerCaptor.capture())
    whenever(mockFirebaseAuth.currentUser).thenReturn(null)

    var callbackCalled = false

    quickFixRepositoryFirestore.init(onSuccess = { callbackCalled = true })

    // Simulate auth state change
    authStateListenerCaptor.firstValue.onAuthStateChanged(mockFirebaseAuth)

    shadowOf(Looper.getMainLooper()).idle()

    assertFalse(callbackCalled)
  }
}
