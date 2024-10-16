package com.arygm.quickfix.model.profile

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import junit.framework.TestCase.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.timeout
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class ProfileRepositoryFirestoreTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore
  @Mock private lateinit var mockDocumentReference: DocumentReference
  @Mock private lateinit var mockCollectionReference: CollectionReference
  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot
  @Mock private lateinit var mockProfileQuerySnapshot: QuerySnapshot
  @Mock private lateinit var mockQuery: Query

  private lateinit var profileRepositoryFirestore: ProfileRepositoryFirestore

  private val profile =
      Profile(
          uid = "1",
          firstName = "John",
          lastName = "Doe",
          email = "john.doe@example.com",
          birthDate = Timestamp.now(),
          description = "Sample description")

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    profileRepositoryFirestore = ProfileRepositoryFirestore(mockFirestore)

    `when`(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(any())).thenReturn(mockDocumentReference)
    `when`(mockCollectionReference.document()).thenReturn(mockDocumentReference)
    `when`(mockCollectionReference.whereEqualTo(any<String>(), any())).thenReturn(mockQuery)
  }

  @Test
  fun getProfiles_callsDocuments() {
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockProfileQuerySnapshot))

    `when`(mockProfileQuerySnapshot.documents).thenReturn(listOf())

    profileRepositoryFirestore.getProfiles(
        onSuccess = {}, onFailure = { fail("Failure callback should not be called") })

    verify(timeout(100)) { (mockProfileQuerySnapshot).documents }
  }

  @Test
  fun addProfile_shouldCallFirestoreCollection() {
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forResult(null))

    profileRepositoryFirestore.addProfile(profile, onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).set(any())
  }

  @Test
  fun updateProfile_shouldCallFirestoreCollection() {
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forResult(null))

    profileRepositoryFirestore.updateProfile(profile, onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).set(any())
  }

  @Test
  fun deleteProfileById_shouldCallDocumentReferenceDelete() {
    `when`(mockDocumentReference.delete()).thenReturn(Tasks.forResult(null))

    profileRepositoryFirestore.deleteProfileById("1", onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).delete()
  }

  @Test
  fun profileExists_whenProfileExists_callsOnSuccessWithTrueAndProfile() {
    val email = "john.doe@example.com"

    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockProfileQuerySnapshot))
    `when`(mockProfileQuerySnapshot.isEmpty).thenReturn(false)
    `when`(mockProfileQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))

    `when`(mockDocumentSnapshot.id).thenReturn(profile.uid)
    `when`(mockDocumentSnapshot.getString("firstName")).thenReturn(profile.firstName)
    `when`(mockDocumentSnapshot.getString("lastName")).thenReturn(profile.lastName)
    `when`(mockDocumentSnapshot.getString("email")).thenReturn(profile.email)
    `when`(mockDocumentSnapshot.getTimestamp("birthDate")).thenReturn(profile.birthDate)
    `when`(mockDocumentSnapshot.getString("description")).thenReturn(profile.description)

    var callbackCalled = false

    profileRepositoryFirestore.profileExists(
        email = email,
        onSuccess = { (exists, foundProfile) ->
          callbackCalled = true
          assert(exists)
          assert(foundProfile == profile)
        },
        onFailure = { fail("Failure callback should not be called") })

    shadowOf(Looper.getMainLooper()).idle()

    assert(callbackCalled)
  }

  @Test
  fun profileExists_whenProfileDoesNotExist_callsOnSuccessWithFalseAndNull() {
    val email = "unknown@example.com"

    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockProfileQuerySnapshot))
    `when`(mockProfileQuerySnapshot.isEmpty).thenReturn(true)

    var callbackCalled = false

    profileRepositoryFirestore.profileExists(
        email = email,
        onSuccess = { (exists, foundProfile) ->
          callbackCalled = true
          assert(!exists)
          assert(foundProfile == null)
        },
        onFailure = { fail("Failure callback should not be called") })

    shadowOf(Looper.getMainLooper()).idle()

    assert(callbackCalled)
  }

  @Test
  fun profileExists_whenFailure_callsOnFailure() {
    val email = "john.doe@example.com"
    val exception = Exception("Test exception")

    `when`(mockQuery.get()).thenReturn(Tasks.forException(exception))

    var failureCallbackCalled = false

    profileRepositoryFirestore.profileExists(
        email = email,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { e ->
          failureCallbackCalled = true
          assert(e == exception)
        })

    shadowOf(Looper.getMainLooper()).idle()

    assert(failureCallbackCalled)
  }

  @Test
  fun getProfileById_whenDocumentExists_callsOnSuccessWithProfile() {
    val uid = "1"

    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.exists()).thenReturn(true)
    `when`(mockDocumentSnapshot.id).thenReturn(profile.uid)
    `when`(mockDocumentSnapshot.getString("firstName")).thenReturn(profile.firstName)
    `when`(mockDocumentSnapshot.getString("lastName")).thenReturn(profile.lastName)
    `when`(mockDocumentSnapshot.getString("email")).thenReturn(profile.email)
    `when`(mockDocumentSnapshot.getTimestamp("birthDate")).thenReturn(profile.birthDate)
    `when`(mockDocumentSnapshot.getString("description")).thenReturn(profile.description)

    var callbackCalled = false

    profileRepositoryFirestore.getProfileById(
        uid = uid,
        onSuccess = { foundProfile ->
          callbackCalled = true
          assert(foundProfile == profile)
        },
        onFailure = { fail("Failure callback should not be called") })

    shadowOf(Looper.getMainLooper()).idle()

    assert(callbackCalled)
  }

  @Test
  fun getProfileById_whenDocumentDoesNotExist_callsOnSuccessWithNull() {
    val uid = "nonexistent"

    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.exists()).thenReturn(false)

    var callbackCalled = false

    profileRepositoryFirestore.getProfileById(
        uid = uid,
        onSuccess = { foundProfile ->
          callbackCalled = true
          assert(foundProfile == null)
        },
        onFailure = { fail("Failure callback should not be called") })

    shadowOf(Looper.getMainLooper()).idle()

    assert(callbackCalled)
  }

  @Test
  fun getProfileById_whenFailure_callsOnFailure() {
    val uid = "1"
    val exception = Exception("Test exception")

    `when`(mockDocumentReference.get()).thenReturn(Tasks.forException(exception))

    var failureCallbackCalled = false

    profileRepositoryFirestore.getProfileById(
        uid = uid,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { e ->
          failureCallbackCalled = true
          assert(e == exception)
        })

    shadowOf(Looper.getMainLooper()).idle()

    assert(failureCallbackCalled)
  }
}
