package com.arygm.quickfix.model.profile

import android.graphics.Bitmap
import android.net.Uri
import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.profile.dataFields.AddOnService
import com.arygm.quickfix.model.profile.dataFields.IncludedService
import com.arygm.quickfix.model.profile.dataFields.Review
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
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
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.LocalTime
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.fail
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class UserProfileRepositoryFirestoreTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore

  @Mock private lateinit var mockDocumentReference: DocumentReference

  @Mock private lateinit var mockCollectionReference: CollectionReference

  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot

  @Mock private lateinit var mockQuerySnapshot: QuerySnapshot

  @Mock private lateinit var mockProfileQuerySnapshot: QuerySnapshot

  @Mock private lateinit var mockQuery: Query

  @Mock private lateinit var mockStorage: FirebaseStorage
  @Mock private lateinit var storageRef: StorageReference
  @Mock private lateinit var storageRef1: StorageReference
  @Mock private lateinit var storageRef2: StorageReference
  @Mock private lateinit var workerProfileFolderRef: StorageReference

  private lateinit var mockFirebaseAuth: FirebaseAuth
  private lateinit var firebaseAuthMockedStatic: MockedStatic<FirebaseAuth>

  private lateinit var profileRepositoryFirestore: UserProfileRepositoryFirestore

  private val testLocations =
      listOf(
          Location(latitude = 0.0, longitude = 0.0, name = "Home"),
          Location(latitude = 1.0, longitude = 1.0, name = "Work"))

  private val userProfile =
      UserProfile(uid = "1", locations = testLocations, announcements = emptyList())

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    firebaseAuthMockedStatic = Mockito.mockStatic(FirebaseAuth::class.java)
    mockFirebaseAuth = mock(FirebaseAuth::class.java)

    // Mock FirebaseAuth.getInstance() to return the mockFirebaseAuth
    firebaseAuthMockedStatic
        .`when`<FirebaseAuth> { FirebaseAuth.getInstance() }
        .thenReturn(mockFirebaseAuth)

    whenever(mockStorage.reference).thenReturn(storageRef)
    whenever(storageRef.child(anyString())).thenReturn(storageRef1)
    whenever(storageRef1.child(anyString())).thenReturn(storageRef2)
    whenever(storageRef2.child(anyString())).thenReturn(workerProfileFolderRef)

    profileRepositoryFirestore = UserProfileRepositoryFirestore(mockFirestore, mockStorage)

    whenever(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
    whenever(mockCollectionReference.document(any())).thenReturn(mockDocumentReference)
    whenever(mockCollectionReference.document()).thenReturn(mockDocumentReference)
    whenever(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockProfileQuerySnapshot))
  }

  @After
  fun tearDown() {
    // Close the static mock
    firebaseAuthMockedStatic.close()
  }

  @Test
  fun getProfiles_callsDocuments() {
    whenever(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockProfileQuerySnapshot))
    whenever(mockProfileQuerySnapshot.documents).thenReturn(listOf())

    profileRepositoryFirestore.getProfiles(
        onSuccess = {}, onFailure = { fail("Failure callback should not be called") })

    verify(mockCollectionReference).get()
  }

  @Test
  fun addProfile_shouldCallFirestoreCollection() {
    whenever(mockDocumentReference.set(any())).thenReturn(Tasks.forResult(null))

    profileRepositoryFirestore.addProfile(userProfile, onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).set(any())
  }

  @Test
  fun addProfile_whenSuccess_callsOnSuccess() {
    val taskCompletionSource = TaskCompletionSource<Void>()
    whenever(mockDocumentReference.set(userProfile)).thenReturn(taskCompletionSource.task)

    var callbackCalled = false

    profileRepositoryFirestore.addProfile(
        profile = userProfile,
        onSuccess = { callbackCalled = true },
        onFailure = { fail("Failure callback should not be called") })

    taskCompletionSource.setResult(null)

    shadowOf(Looper.getMainLooper()).idle()

    assert(callbackCalled)
  }

  @Test
  fun addProfile_whenFailure_callsOnFailure() {
    val taskCompletionSource = TaskCompletionSource<Void>()
    whenever(mockDocumentReference.set(userProfile)).thenReturn(taskCompletionSource.task)

    val exception = Exception("Test exception")
    var callbackCalled = false
    var returnedException: Exception? = null

    profileRepositoryFirestore.addProfile(
        profile = userProfile,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { e ->
          callbackCalled = true
          returnedException = e
        })

    taskCompletionSource.setException(exception)

    shadowOf(Looper.getMainLooper()).idle()

    assert(callbackCalled)
    assert(returnedException == exception)
  }

  @Test
  fun updateProfile_shouldCallFirestoreCollection() {
    whenever(mockDocumentReference.set(any())).thenReturn(Tasks.forResult(null))

    profileRepositoryFirestore.updateProfile(userProfile, onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).set(any())
  }

  @Test
  fun updateProfile_whenSuccess_callsOnSuccess() {
    val taskCompletionSource = TaskCompletionSource<Void>()
    whenever(mockDocumentReference.set(userProfile)).thenReturn(taskCompletionSource.task)

    var callbackCalled = false

    profileRepositoryFirestore.updateProfile(
        profile = userProfile,
        onSuccess = { callbackCalled = true },
        onFailure = { fail("Failure callback should not be called") })

    taskCompletionSource.setResult(null)

    shadowOf(Looper.getMainLooper()).idle()

    assert(callbackCalled)
  }

  @Test
  fun updateProfile_whenFailure_callsOnFailure() {
    val taskCompletionSource = TaskCompletionSource<Void>()
    whenever(mockDocumentReference.set(userProfile)).thenReturn(taskCompletionSource.task)

    val exception = Exception("Test exception")
    var callbackCalled = false
    var returnedException: Exception? = null

    profileRepositoryFirestore.updateProfile(
        profile = userProfile,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { e ->
          callbackCalled = true
          returnedException = e
        })

    taskCompletionSource.setException(exception)

    shadowOf(Looper.getMainLooper()).idle()

    assert(callbackCalled)
    assert(returnedException == exception)
  }

  @Test
  fun deleteProfileById_shouldCallDocumentReferenceDelete() {
    whenever(mockDocumentReference.delete()).thenReturn(Tasks.forResult(null))

    profileRepositoryFirestore.deleteProfileById("1", onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle()

    verify(mockDocumentReference).delete()
  }

  @Test
  fun deleteProfileById_whenSuccess_callsOnSuccess() {
    val taskCompletionSource = TaskCompletionSource<Void>()
    whenever(mockDocumentReference.delete()).thenReturn(taskCompletionSource.task)

    var callbackCalled = false

    profileRepositoryFirestore.deleteProfileById(
        id = "1",
        onSuccess = { callbackCalled = true },
        onFailure = { fail("Failure callback should not be called") })

    taskCompletionSource.setResult(null)

    shadowOf(Looper.getMainLooper()).idle()

    assert(callbackCalled)
  }

  @Test
  fun deleteProfileById_whenFailure_callsOnFailure() {
    val taskCompletionSource = TaskCompletionSource<Void>()
    whenever(mockDocumentReference.delete()).thenReturn(taskCompletionSource.task)

    val exception = Exception("Test exception")
    var callbackCalled = false
    var returnedException: Exception? = null

    profileRepositoryFirestore.deleteProfileById(
        id = "1",
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { e ->
          callbackCalled = true
          returnedException = e
        })

    taskCompletionSource.setException(exception)

    shadowOf(Looper.getMainLooper()).idle()

    assert(callbackCalled)
    assert(returnedException == exception)
  }

  @Test
  fun getProfileById_whenDocumentExists_callsOnSuccessWithProfile() {
    val uid = "1"

    whenever(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    whenever(mockDocumentSnapshot.exists()).thenReturn(true)

    // Mocking the data returned from Firestore
    whenever(mockDocumentSnapshot.id).thenReturn(userProfile.uid)
    whenever(mockDocumentSnapshot.get("locations"))
        .thenReturn(
            listOf(
                mapOf("latitude" to 0.0, "longitude" to 0.0, "name" to "Home"),
                mapOf("latitude" to 1.0, "longitude" to 1.0, "name" to "Work")))

    var callbackCalled = false

    profileRepositoryFirestore.getProfileById(
        uid = uid,
        onSuccess = { foundProfile ->
          callbackCalled = true
          assert(foundProfile == userProfile)
        },
        onFailure = { fail("Failure callback should not be called") })

    shadowOf(Looper.getMainLooper()).idle()

    assert(callbackCalled)
  }

  @Test
  fun getProfileById_whenDocumentDoesNotExist_callsOnSuccessWithNull() {
    val uid = "nonexistent"

    whenever(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    whenever(mockDocumentSnapshot.exists()).thenReturn(false)

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

    whenever(mockDocumentReference.get()).thenReturn(Tasks.forException(exception))

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

  @Test
  fun getProfiles_whenSuccess_callsOnSuccessWithProfiles() {
    val taskCompletionSource = TaskCompletionSource<QuerySnapshot>()
    whenever(mockCollectionReference.get()).thenReturn(taskCompletionSource.task)

    val document1 = mock(DocumentSnapshot::class.java)
    val document2 = mock(DocumentSnapshot::class.java)

    val userProfile2 =
        UserProfile(
            uid = "2",
            locations = listOf(Location(latitude = 2.0, longitude = 2.0, name = "Gym")),
            announcements = emptyList())

    val documents = listOf(document1, document2)
    whenever(mockProfileQuerySnapshot.documents).thenReturn(documents)

    whenever(document1.id).thenReturn(userProfile.uid)
    whenever(document1.get("locations"))
        .thenReturn(
            listOf(
                mapOf("latitude" to 0.0, "longitude" to 0.0, "name" to "Home"),
                mapOf("latitude" to 1.0, "longitude" to 1.0, "name" to "Work")))

    whenever(document2.id).thenReturn(userProfile2.uid)
    whenever(document2.get("locations"))
        .thenReturn(listOf(mapOf("latitude" to 2.0, "longitude" to 2.0, "name" to "Gym")))

    var callbackCalled = false
    var returnedProfiles: List<Profile>? = null

    profileRepositoryFirestore.getProfiles(
        onSuccess = { profiles ->
          callbackCalled = true
          returnedProfiles = profiles
        },
        onFailure = { fail("Failure callback should not be called") })

    taskCompletionSource.setResult(mockProfileQuerySnapshot)

    shadowOf(Looper.getMainLooper()).idle()

    assert(callbackCalled)
    assert(returnedProfiles != null)
    assert(returnedProfiles!!.size == 2)
    assert(returnedProfiles!![0] == userProfile)
    assert(returnedProfiles!![1] == userProfile2)
  }

  @Test
  fun getProfiles_whenFailure_callsOnFailure() {
    val taskCompletionSource = TaskCompletionSource<QuerySnapshot>()
    whenever(mockCollectionReference.get()).thenReturn(taskCompletionSource.task)

    val exception = Exception("Test exception")

    var callbackCalled = false
    var returnedException: Exception? = null

    profileRepositoryFirestore.getProfiles(
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { e ->
          callbackCalled = true
          returnedException = e
        })

    taskCompletionSource.setException(exception)

    shadowOf(Looper.getMainLooper()).idle()

    assert(callbackCalled)
    assert(returnedException == exception)
  }

  @Test
  fun init_whenCurrentUserNotNull_callsOnSuccess() {
    val authStateListenerCaptor = argumentCaptor<FirebaseAuth.AuthStateListener>()
    val mockFirebaseUser = mock(FirebaseUser::class.java)

    doNothing().whenever(mockFirebaseAuth).addAuthStateListener(authStateListenerCaptor.capture())
    whenever(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser)

    var callbackCalled = false

    profileRepositoryFirestore.init(onSuccess = { callbackCalled = true })

    authStateListenerCaptor.firstValue.onAuthStateChanged(mockFirebaseAuth)

    shadowOf(Looper.getMainLooper()).idle()

    assert(callbackCalled)
  }

  @Test
  fun init_whenCurrentUserIsNull_doesNotCallOnSuccess() {
    val authStateListenerCaptor = argumentCaptor<FirebaseAuth.AuthStateListener>()

    doNothing().whenever(mockFirebaseAuth).addAuthStateListener(authStateListenerCaptor.capture())
    whenever(mockFirebaseAuth.currentUser).thenReturn(null)

    var callbackCalled = false

    profileRepositoryFirestore.init(onSuccess = { callbackCalled = true })

    authStateListenerCaptor.firstValue.onAuthStateChanged(mockFirebaseAuth)

    shadowOf(Looper.getMainLooper()).idle()

    assert(!callbackCalled)
  }

  @Test
  fun documentToUser_whenAllFieldsArePresent_returnsUserProfile() {
    // Arrange
    val document = mock(DocumentSnapshot::class.java)
    `when`(document.id).thenReturn(userProfile.uid)
    `when`(document.get("locations"))
        .thenReturn(
            listOf(
                mapOf("latitude" to 0.0, "longitude" to 0.0, "name" to "Home"),
                mapOf("latitude" to 1.0, "longitude" to 1.0, "name" to "Work")))

    // Act
    val result = invokeDocumentToUser(document)

    // Assert
    assertNotNull(result)
    assertEquals(userProfile, result)
  }

  @Test
  fun documentToUser_whenLocationsAreMissing_returnsNull() {
    // Arrange
    val document = mock(DocumentSnapshot::class.java)
    `when`(document.id).thenReturn(userProfile.uid)
    // "locations" field is missing
    `when`(document.get("locations")).thenReturn(null)

    // Act
    val result = invokeDocumentToUser(document)

    // Assert
    assertEquals(result!!.locations, emptyList<Location>())
  }

  @Test
  fun documentToUser_whenLocationsHaveInvalidDataType_returnsNull() {
    // Arrange
    val document = mock(DocumentSnapshot::class.java)
    `when`(document.id).thenReturn(userProfile.uid)
    // "locations" field has invalid data type (not a list of maps)
    `when`(document.get("locations")).thenReturn("Invalid data type")

    // Act
    val result = invokeDocumentToUser(document)

    // Assert
    assertEquals(result!!.locations, emptyList<Location>())
  }

  @Test
  fun documentToUser_whenExceptionOccurs_returnsNull() {
    // Arrange
    val document = mock(DocumentSnapshot::class.java)
    `when`(document.id).thenReturn(userProfile.uid)
    // Simulate an exception when accessing the "locations" field
    `when`(document.get("locations")).thenThrow(RuntimeException("Test exception"))

    // Act
    val result = invokeDocumentToUser(document)

    // Assert
    assertNull(result)
  }

  // ----- Helper Method for Testing Private Method -----

  /** Uses reflection to invoke the private `documentToUser` method. */
  private fun invokeDocumentToUser(document: DocumentSnapshot): UserProfile? {
    val method =
        UserProfileRepositoryFirestore::class
            .java
            .getDeclaredMethod("documentToUser", DocumentSnapshot::class.java)
    method.isAccessible = true
    return method.invoke(profileRepositoryFirestore, document) as UserProfile?
  }

  //  @Test
  //  fun filterWorkers_withFieldOfWork_callsOnSuccess() {
  //    val taskCompletionSource = TaskCompletionSource<QuerySnapshot>()
  //    `when`(mockQuery.get()).thenReturn(taskCompletionSource.task)
  //
  //    // Mock query result to return one worker profile
  //    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))
  //    `when`(mockDocumentSnapshot.toObject(Profile::class.java)).thenReturn(profile)
  //
  //    var callbackCalled = false
  //    var returnedProfiles: List<Profile>? = null
  //
  //    profileRepositoryFirestore.filterWorkers(
  //        fieldOfWork = "Plumber",
  //        hourlyRateThreshold = null,
  //        onSuccess = { profiles ->
  //          callbackCalled = true
  //          returnedProfiles = profiles
  //        },
  //        onFailure = { fail("Failure callback should not be called") })
  //
  //    // Simulate Firestore success
  //    taskCompletionSource.setResult(mockQuerySnapshot)
  //    shadowOf(Looper.getMainLooper()).idle()
  //
  //    // Assert that the success callback was called and the profile matches
  //    assert(callbackCalled)
  //    assert(returnedProfiles != null)
  //    assert(returnedProfiles!!.size == 1)
  //    assert(returnedProfiles!![0] == profile)
  //  }
  //
  //  @Test
  //  fun filterWorkers_withHourlyRateThreshold_callsOnSuccess() {
  //    val taskCompletionSource = TaskCompletionSource<QuerySnapshot>()
  //    `when`(mockQuery.get()).thenReturn(taskCompletionSource.task)
  //
  //    // Mock query result to return one worker profile
  //    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))
  //    `when`(mockDocumentSnapshot.toObject(Profile::class.java)).thenReturn(profile)
  //
  //    var callbackCalled = false
  //    var returnedProfiles: List<Profile>? = null
  //
  //    profileRepositoryFirestore.filterWorkers(
  //        hourlyRateThreshold = 30.0,
  //        fieldOfWork = null,
  //        onSuccess = { profiles ->
  //          callbackCalled = true
  //          returnedProfiles = profiles
  //        },
  //        onFailure = { fail("Failure callback should not be called") })
  //
  //    // Simulate Firestore success
  //    taskCompletionSource.setResult(mockQuerySnapshot)
  //    shadowOf(Looper.getMainLooper()).idle()
  //
  //    // Assert that the success callback was called and the profile matches
  //    assert(callbackCalled)
  //    assert(returnedProfiles != null)
  //    assert(returnedProfiles!!.size == 1)
  //    assert(returnedProfiles!![0] == profile)
  //  }
  //
  //  @Test
  //  fun filterWorkers_withFieldOfWorkAndHourlyRateThreshold_callsOnSuccess() {
  //    val taskCompletionSource = TaskCompletionSource<QuerySnapshot>()
  //    `when`(mockQuery.get()).thenReturn(taskCompletionSource.task)
  //
  //    // Mock query result to return one worker profile
  //    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))
  //    `when`(mockDocumentSnapshot.toObject(Profile::class.java)).thenReturn(profile)
  //
  //    var callbackCalled = false
  //    var returnedProfiles: List<Profile>? = null
  //
  //    profileRepositoryFirestore.filterWorkers(
  //        hourlyRateThreshold = 30.0,
  //        fieldOfWork = "Plumber",
  //        onSuccess = { profiles ->
  //          callbackCalled = true
  //          returnedProfiles = profiles
  //        },
  //        onFailure = { fail("Failure callback should not be called") })
  //
  //    // Simulate Firestore success
  //    taskCompletionSource.setResult(mockQuerySnapshot)
  //    shadowOf(Looper.getMainLooper()).idle()
  //
  //    // Assert that the success callback was called and the profile matches
  //    assert(callbackCalled)
  //    assert(returnedProfiles != null)
  //    assert(returnedProfiles!!.size == 1)
  //    assert(returnedProfiles!![0] == profile)
  //  }
  //
  //  @Test
  //  fun filterWorkers_onFailure_callsOnFailure() {
  //    val taskCompletionSource = TaskCompletionSource<QuerySnapshot>()
  //    `when`(mockQuery.get()).thenReturn(taskCompletionSource.task)
  //
  //    val exception = Exception("Test exception")
  //    var callbackCalled = false
  //    var returnedException: Exception? = null
  //
  //    profileRepositoryFirestore.filterWorkers(
  //        hourlyRateThreshold = 30.0,
  //        fieldOfWork = "Plumber",
  //        onSuccess = { fail("Success callback should not be called") },
  //        onFailure = { e ->
  //          callbackCalled = true
  //          returnedException = e
  //        })
  //
  //    // Simulate Firestore failure
  //    taskCompletionSource.setException(exception)
  //    shadowOf(Looper.getMainLooper()).idle()
  //
  //    // Assert that the failure callback was called and the exception matches
  //    assert(callbackCalled)
  //    assert(returnedException == exception)
  //  }
  @Test
  fun uploadWorkerProfileImages_success() {
    val accountId = "userProfileId"
    val bitmaps = listOf(mock(Bitmap::class.java), mock(Bitmap::class.java))
    val expectedUrls = listOf("https://example.com/image1.jpg", "https://example.com/image2.jpg")
    val baos = ByteArrayOutputStream()
    bitmaps.forEach { bitmap -> bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos) }
    val imageData = baos.toByteArray()

    // Mock storageRef.child("workerProfiles/$workerProfileId")
    `when`(storageRef.child("profiles").child(accountId).child("user"))
        .thenReturn(workerProfileFolderRef)

    // For each image, when workerProfileFolderRef.child(anyString()) is called, return a new
    // fileRef
    val fileRef1 = mock(StorageReference::class.java)
    val fileRef2 = mock(StorageReference::class.java)
    val fileRefs = listOf(fileRef1, fileRef2)
    var fileRefIndex = 0
    `when`(workerProfileFolderRef.child(anyString())).thenAnswer { fileRefs[fileRefIndex++] }

    // Mock putBytes
    val mockUploadTask1 = mock(UploadTask::class.java)
    val mockUploadTask2 = mock(UploadTask::class.java)
    val mockUploadTasks = listOf(mockUploadTask1, mockUploadTask2)
    val imageDatas = listOf(imageData, imageData) // Assuming same data for simplicity

    // Mock fileRef.putBytes(imageData)
    `when`(fileRef1.putBytes(imageDatas[0])).thenReturn(mockUploadTask1)
    `when`(fileRef2.putBytes(imageDatas[1])).thenReturn(mockUploadTask2)

    // Mock mockUploadTask.addOnSuccessListener(...)
    mockUploadTasks.forEachIndexed { index, mockUploadTask ->
      `when`(mockUploadTask.addOnSuccessListener(any())).thenAnswer { invocation ->
        val listener = invocation.getArgument<OnSuccessListener<UploadTask.TaskSnapshot>>(0)
        val taskSnapshot = mock(UploadTask.TaskSnapshot::class.java) // Mock the snapshot
        listener.onSuccess(taskSnapshot)
        mockUploadTask // continue the chain
      }
    }

    // Mock fileRef.downloadUrl
    `when`(fileRef1.downloadUrl).thenReturn(Tasks.forResult(Uri.parse(expectedUrls[0])))
    `when`(fileRef2.downloadUrl).thenReturn(Tasks.forResult(Uri.parse(expectedUrls[1])))

    // Act
    var resultUrls = listOf<String>()
    profileRepositoryFirestore.uploadProfileImages(
        accountId,
        bitmaps,
        onSuccess = {
          resultUrls = it
          Assert.assertEquals(expectedUrls, resultUrls)
        },
        onFailure = { fail("onFailure should not be called") })

    // Wait for tasks to complete
    shadowOf(Looper.getMainLooper()).idle()
  }

  @Test
  fun uploadWorkerProfileImages_failure() {
    val accountId = "userProfileId"
    val bitmap1 = mock(Bitmap::class.java)
    val images = listOf(bitmap1)
    val exception = Exception("Upload failed")

    // Mock storageRef.child("workerProfiles/$workerProfileId")
    `when`(storageRef.child("profiles").child(accountId).child("user"))
        .thenReturn(workerProfileFolderRef)
    // Mock fileRef
    val fileRef = mock(StorageReference::class.java)
    whenever(workerProfileFolderRef.child(anyString())).thenReturn(fileRef)

    // Mock putBytes
    val uploadTask = mock(UploadTask::class.java)
    whenever(fileRef.putBytes(any())).thenReturn(uploadTask)

    // Mock uploadTask.addOnSuccessListener and addOnFailureListener
    whenever(uploadTask.addOnSuccessListener(any())).thenReturn(uploadTask)
    whenever(uploadTask.addOnFailureListener(any())).thenAnswer { invocation ->
      val listener = invocation.arguments[0] as OnFailureListener
      listener.onFailure(exception)
      uploadTask
    }

    // Act
    var onFailureCalled = false
    var exceptionReceived: Exception? = null
    profileRepositoryFirestore.uploadProfileImages(
        accountId,
        images,
        onSuccess = { fail("onSuccess should not be called when upload fails") },
        onFailure = { e ->
          onFailureCalled = true
          exceptionReceived = e
        })

    // Wait for tasks to complete
    shadowOf(Looper.getMainLooper()).idle()

    // Assert
    assertTrue(onFailureCalled)
    Assert.assertEquals(exception, exceptionReceived)
  }

  @Test
  fun testEqualsAndHashCode() {
    val location1 = Location(latitude = 10.0, longitude = 20.0, name = "Home")
    val location2 = Location(latitude = 30.0, longitude = 40.0, name = "Work")

    val userProfile1 =
        UserProfile(
            locations = listOf(location1, location2),
            announcements = listOf("announcement1", "announcement2"),
            wallet = 50.0,
            uid = "user123",
            quickFixes = listOf("fix1", "fix2"),
            savedList = listOf("saved1", "saved2"))

    val userProfile2 =
        UserProfile(
            locations = listOf(location1, location2),
            announcements = listOf("announcement1", "announcement2"),
            wallet = 50.0,
            uid = "user123",
            quickFixes = listOf("fix1", "fix2"),
            savedList = listOf("saved1", "saved2"))

    // Another profile with different properties
    val userProfile3 =
        UserProfile(
            locations = listOf(location1),
            announcements = listOf("announcement3"),
            wallet = 100.0,
            uid = "user456",
            quickFixes = listOf("fix3"),
            savedList = listOf("saved3"))

    // Test equals
    assertTrue(userProfile1 == userProfile2)
    assertFalse(userProfile1 == userProfile3)

    // Test hashCode
    assertEquals(userProfile1.hashCode(), userProfile2.hashCode())
    assertNotEquals(userProfile1.hashCode(), userProfile3.hashCode())
  }

  @Test
  fun testCopy() {
    val location1 = Location(latitude = 10.0, longitude = 20.0, name = "Home")
    val location2 = Location(latitude = 30.0, longitude = 40.0, name = "Work")

    val original =
        UserProfile(
            locations = listOf(location1, location2),
            announcements = listOf("announcement1", "announcement2"),
            wallet = 50.0,
            uid = "user123",
            quickFixes = listOf("fix1", "fix2"),
            savedList = listOf("saved1", "saved2"))

    // Copy without changing anything
    val copySame = original.copy()
    assertEquals(original, copySame)

    // Copy with some modifications
    val modified =
        original.copy(locations = listOf(location1), wallet = 100.0, savedList = listOf("newSaved"))

    assertNotEquals(original, modified)
    assertEquals(listOf(location1), modified.locations)
    assertEquals(100.0, modified.wallet, 0.0001)
    assertEquals(listOf("newSaved"), modified.savedList)
    // Check that unchanged fields remain the same
    assertEquals(original.uid, modified.uid)
    assertEquals(original.announcements, modified.announcements)
    assertEquals(original.quickFixes, modified.quickFixes)
  }

  @Test
  fun testEquals() {
    // Set up test data for components
    val location1 = Location(latitude = 10.0, longitude = 20.0, name = "Home")
    val location2 = Location(latitude = 30.0, longitude = 40.0, name = "Work")

    val includedServices = listOf(IncludedService("Service A"), IncludedService("Service B"))

    val addOnServices = listOf(AddOnService("AddOn A"), AddOnService("AddOn B"))

    val reviews =
        ArrayDeque(
            listOf(
                Review(username = "user1", review = "Great work", rating = 4.5),
                Review(username = "user2", review = "Good job", rating = 4.0)))

    val unavailabilityList = listOf(LocalDate.now().plusDays(2), LocalDate.now().plusDays(4))
    val workingHours = Pair(LocalTime.of(8, 0), LocalTime.of(16, 0))

    // Create a base WorkerProfile
    val worker1 =
        WorkerProfile(
            fieldOfWork = "Carpentry",
            description = "Skilled carpenter",
            location = location1,
            quickFixes = listOf("fix1", "fix2"),
            includedServices = includedServices,
            addOnServices = addOnServices,
            reviews = reviews,
            profilePicture = "http://example.com/profile.jpg",
            bannerPicture = "http://example.com/banner.jpg",
            price = 100.0,
            displayName = "John Doe",
            unavailability_list = unavailabilityList,
            workingHours = workingHours,
            uid = "worker123",
            tags = listOf("Professional", "Reliable"),
            rating = reviews.map { it.rating }.average())

    // Create an identical WorkerProfile
    val worker2 =
        WorkerProfile(
            fieldOfWork = "Carpentry",
            description = "Skilled carpenter",
            location = location1,
            quickFixes = listOf("fix1", "fix2"),
            includedServices = includedServices,
            addOnServices = addOnServices,
            reviews = reviews,
            profilePicture = "http://example.com/profile.jpg",
            bannerPicture = "http://example.com/banner.jpg",
            price = 100.0,
            displayName = "John Doe",
            unavailability_list = unavailabilityList,
            workingHours = workingHours,
            uid = "worker123",
            tags = listOf("Professional", "Reliable"),
            rating = reviews.map { it.rating }.average())

    // A WorkerProfile with a different field to ensure not equal
    val worker3 =
        WorkerProfile(
            fieldOfWork = "Plumbing", // changed fieldOfWork
            description = "Skilled carpenter",
            location = location1,
            quickFixes = listOf("fix1", "fix2"),
            includedServices = includedServices,
            addOnServices = addOnServices,
            reviews = reviews,
            profilePicture = "http://example.com/profile.jpg",
            bannerPicture = "http://example.com/banner.jpg",
            price = 100.0,
            displayName = "John Doe",
            unavailability_list = unavailabilityList,
            workingHours = workingHours,
            uid = "worker123",
            tags = listOf("Professional", "Reliable"),
            rating = reviews.map { it.rating }.average())

    // Tests
    assertTrue("worker1 should be equal to worker2", worker1 == worker2)
    assertEquals(
        "worker1 and worker2 should have the same hashCode", worker1.hashCode(), worker2.hashCode())

    assertFalse(
        "worker1 should not be equal to worker3 because fieldOfWork differs", worker1 == worker3)

    // Check self equality
    assertTrue("worker1 should be equal to itself", worker1 == worker1)

    // Check null
    assertFalse("worker1 should not be equal to null", worker1 == null)

    // Check different type
    assertFalse("worker1 should not be equal to a different type", worker1.equals("Some String"))
  }
}
