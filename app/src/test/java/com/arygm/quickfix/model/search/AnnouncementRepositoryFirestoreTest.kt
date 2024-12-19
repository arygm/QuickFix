package com.arygm.quickfix.model.search

import android.graphics.Bitmap
import android.net.Uri
import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.arygm.quickfix.model.locations.Location
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import junit.framework.TestCase.fail
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class AnnouncementRepositoryFirestoreTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore
  @Mock private lateinit var mockCollectionReference: CollectionReference
  @Mock private lateinit var mockDocumentReference: DocumentReference
  @Mock private lateinit var mockQuerySnapshot: QuerySnapshot
  @Mock private lateinit var mockQuery: Query

  // Mocks for FirebaseStorage
  @Mock private lateinit var mockStorage: FirebaseStorage
  @Mock private lateinit var storageRef: StorageReference
  @Mock private lateinit var announcementFolderRef: StorageReference

  private lateinit var announcementRepositoryFirestore: AnnouncementRepositoryFirestore

  private val timestamp = Timestamp.now()

  private val announcement1 =
      Announcement(
          announcementId = "announcement1",
          userId = "user1",
          title = "Test Announcement",
          category = "Test Category",
          description = "Test Description",
          location = Location(37.7749, -122.4194, "San Francisco"),
          availability = listOf(AvailabilitySlot(start = timestamp, end = timestamp)),
          quickFixImages = listOf("image1.jpg", "image2.jpg"))

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    // Ensure FirebaseApp is initialized
    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    // Mock storage
    whenever(mockStorage.reference).thenReturn(storageRef)
    whenever(storageRef.child(anyString())).thenReturn(announcementFolderRef)
    announcementRepositoryFirestore = AnnouncementRepositoryFirestore(mockFirestore, mockStorage)

    // Mock the Firestore structure
    whenever(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
    whenever(mockCollectionReference.document(any())).thenReturn(mockDocumentReference)
    whenever(mockCollectionReference.document()).thenReturn(mockDocumentReference)
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))

    `when`(mockCollectionReference.whereEqualTo(eq("category"), any<String>()))
        .thenReturn(mockQuery)
    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))
  }

  @Test
  fun verifyMocks() {
    // Check that the collection method is returning a mock
    assertNotNull(mockFirestore.collection("announcements"))

    // Ensure that document calls return proper mocks
    assertNotNull(mockCollectionReference.document("announcement1"))
  }

  // Init test
  @Test
  fun init_callsOnSuccess() {
    var onSuccessCalled = false
    announcementRepositoryFirestore.init { onSuccessCalled = true }
    assertTrue(onSuccessCalled)
  }

  // ----- Get New UID Test -----
  @Test
  fun getNewUid() {
    whenever(mockCollectionReference.document().id).thenReturn("1")
    val uid = announcementRepositoryFirestore.getNewUid()
    assertEquals("1", uid)
  }

  // ----- Get Announcements Tests -----
  @Test
  fun getAnnouncements_onSuccess_callsDocuments() {
    // Ensure that mockQuerySnapshot is properly initialized and mocked
    whenever(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))

    // Ensure the QuerySnapshot returns a list of mock DocumentSnapshots
    whenever(mockQuerySnapshot.documents).thenReturn(listOf())

    // Call the method under test
    announcementRepositoryFirestore.getAnnouncements(
        onSuccess = {
          // Do nothing; we just want to verify that the 'documents' field was accessed
        },
        onFailure = { fail("Failure callback should not be called") })

    verify(mockCollectionReference).get()
  }

  @Test
  fun getAnnouncementsOnFailureTest() {
    val exception = Exception("Firestore error")
    whenever(mockCollectionReference.get()).thenReturn(Tasks.forException(exception))

    var onFailureCalled = false
    announcementRepositoryFirestore.getAnnouncements(
        onSuccess = {}, onFailure = { onFailureCalled = true })

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(onFailureCalled)
  }

  @Test
  fun getAnnouncementsForUser_emptyIdsList_callsOnSuccessWithEmptyList() {
    var callbackCalled = false
    var returnedAnnouncements: List<Announcement>? = null

    announcementRepositoryFirestore.getAnnouncementsForUser(
        announcements = emptyList(),
        onSuccess = { announcements ->
          callbackCalled = true
          returnedAnnouncements = announcements
        },
        onFailure = { fail("Failure callback should not be called") })

    assertTrue(callbackCalled)
    assertNotNull(returnedAnnouncements)
    assertTrue(returnedAnnouncements!!.isEmpty())
  }

  @Test
  fun getAnnouncementsForUser_mapsDocumentsToAnnouncements() {
    val announcementIds = listOf("id1", "id2")
    val mockDocument1 = mock(DocumentSnapshot::class.java)
    val mockDocument2 = mock(DocumentSnapshot::class.java)

    // Mock Firestore behavior
    whenever(mockCollectionReference.whereIn(FieldPath.documentId(), announcementIds))
        .thenReturn(mockCollectionReference)
    whenever(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))
    whenever(mockQuerySnapshot.documents).thenReturn(listOf(mockDocument1, mockDocument2))

    // Mock the document-to-announcement mapping
    whenever(mockDocument1.id).thenReturn("id1")
    whenever(mockDocument2.id).thenReturn("id2")
    whenever(mockDocument1.getString("title")).thenReturn("Title 1")
    whenever(mockDocument2.getString("title")).thenReturn("Title 2")
    whenever(mockDocument1.getString("userId")).thenReturn("user1")
    whenever(mockDocument2.getString("userId")).thenReturn("user2")
    whenever(mockDocument1.getString("category")).thenReturn("Category")
    whenever(mockDocument2.getString("category")).thenReturn("Category")
    whenever(mockDocument1.getString("description")).thenReturn("Description")
    whenever(mockDocument2.getString("description")).thenReturn("Description")

    // Call the method
    announcementRepositoryFirestore.getAnnouncementsForUser(
        announcements = announcementIds,
        onSuccess = { announcements ->
          assertEquals(2, announcements.size)
          assertEquals("Title 1", announcements[0].title)
          assertEquals("Title 2", announcements[1].title)
        },
        onFailure = { fail("Failure callback should not be called") })
  }

  @Test
  fun getAnnouncementsForUser_onFailure_callsOnFailure() {
    val taskCompletionSource = TaskCompletionSource<QuerySnapshot>()
    val exception = Exception("Test exception")
    whenever(mockCollectionReference.whereIn(FieldPath.documentId(), listOf("id1", "id2")))
        .thenReturn(mockCollectionReference)
    whenever(mockCollectionReference.get()).thenReturn(taskCompletionSource.task)

    var callbackCalled = false
    var returnedException: Exception? = null

    announcementRepositoryFirestore.getAnnouncementsForUser(
        announcements = listOf("id1", "id2"),
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

  // ----- Announce Tests -----
  @Test
  fun announce_callsSetOnAnnouncementDocument() {
    val announcementId = announcement1.announcementId
    val mockAnnouncementDocument = mockCollectionReference.document(announcementId)
    whenever(mockAnnouncementDocument.set(any<Announcement>())).thenReturn(Tasks.forResult(null))

    announcementRepositoryFirestore.announce(
        announcement = announcement1,
        onSuccess = {},
        onFailure = { fail("Failure callback should not be called") })

    shadowOf(Looper.getMainLooper()).idle()
    verify(mockAnnouncementDocument).set(eq(announcement1))
  }

  @Test
  fun updateAnnouncement_onSuccess_callsOnSuccess() {
    whenever(mockCollectionReference.document(announcement1.announcementId))
        .thenReturn(mockDocumentReference)
    whenever(mockDocumentReference.set(announcement1)).thenReturn(Tasks.forResult(null))

    var callbackCalled = false

    announcementRepositoryFirestore.updateAnnouncement(
        announcement = announcement1,
        onSuccess = { callbackCalled = true },
        onFailure = { fail("Failure callback should not be called") })
    shadowOf(Looper.getMainLooper()).idle()
    assert(callbackCalled)
  }

  @Test
  fun updateAnnouncement_onFailure_callsOnFailure() {
    val exception = Exception("Test exception")
    whenever(mockCollectionReference.document(announcement1.announcementId))
        .thenReturn(mockDocumentReference)
    whenever(mockDocumentReference.set(announcement1)).thenReturn(Tasks.forException(exception))

    var failureCallbackCalled = false

    announcementRepositoryFirestore.updateAnnouncement(
        announcement = announcement1,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { e ->
          failureCallbackCalled = true
          assertEquals(exception, e)
        })
    shadowOf(Looper.getMainLooper()).idle()
    assert(failureCallbackCalled)
  }

  @Test
  fun deleteAnnouncementById_onSuccess_callsOnSuccess() {
    val announcementId = "id1"
    whenever(mockCollectionReference.document(announcementId)).thenReturn(mockDocumentReference)
    whenever(mockDocumentReference.delete()).thenReturn(Tasks.forResult(null))

    var callbackCalled = false

    announcementRepositoryFirestore.deleteAnnouncementById(
        announcementId = announcementId,
        onSuccess = { callbackCalled = true },
        onFailure = { fail("Failure callback should not be called") })
    shadowOf(Looper.getMainLooper()).idle()
    assert(callbackCalled)
  }

  @Test
  fun deleteAnnouncementById_onFailure_callsOnFailure() {
    val announcementId = "id1"
    val exception = Exception("Test exception")
    whenever(mockCollectionReference.document(announcementId)).thenReturn(mockDocumentReference)
    whenever(mockDocumentReference.delete()).thenReturn(Tasks.forException(exception))

    var failureCallbackCalled = false

    announcementRepositoryFirestore.deleteAnnouncementById(
        announcementId = announcementId,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { e ->
          failureCallbackCalled = true
          assertEquals(exception, e)
        })
    shadowOf(Looper.getMainLooper()).idle()
    assert(failureCallbackCalled)
  }

  private fun invokeDocumentToAnnouncement(document: DocumentSnapshot): Announcement? {
    val method =
        AnnouncementRepositoryFirestore::class
            .java
            .getDeclaredMethod("documentToAnnouncement", DocumentSnapshot::class.java)
    method.isAccessible = true
    return method.invoke(announcementRepositoryFirestore, document) as Announcement?
  }

  @Test
  fun documentToAnnouncement_validDocument_returnsAnnouncement() {
    val document = mock(DocumentSnapshot::class.java)
    whenever(document.id).thenReturn("id1")
    whenever(document.getString("userId")).thenReturn("user1")
    whenever(document.getString("title")).thenReturn("Test Title")
    whenever(document.getString("category")).thenReturn("Category")
    whenever(document.getString("description")).thenReturn("Test Description")
    whenever(document.get("location"))
        .thenReturn(mapOf("latitude" to 0.0, "longitude" to 0.0, "name" to "Test Location"))

    val startTimestamp = Timestamp.now() // Mock a valid Timestamp
    val endTimestamp = Timestamp.now()
    whenever(document.get("availability"))
        .thenReturn(listOf(mapOf("start" to startTimestamp, "end" to endTimestamp)))
    whenever(document.get("quickFixImages")).thenReturn(listOf("image1", "image2"))

    val result = invokeDocumentToAnnouncement(document)

    assertNotNull(result)
    assertEquals("id1", result!!.announcementId)
    assertEquals("user1", result.userId)
    assertEquals("Test Title", result.title)
    assertEquals("Category", result.category)
    assertEquals("Test Description", result.description)
    assertEquals(Location(0.0, 0.0, "Test Location"), result.location)
    assertEquals(1, result.availability.size)
    assertEquals(startTimestamp, result.availability[0].start)
    assertEquals(endTimestamp, result.availability[0].end)
    assertEquals(listOf("image1", "image2"), result.quickFixImages)
  }

  @Test
  fun documentToAnnouncement_invalidDocument_returnsNull() {
    val document = mock(DocumentSnapshot::class.java)
    whenever(document.getString("userId")).thenReturn(null) // Missing required field

    val result = invokeDocumentToAnnouncement(document)

    assertNull(result)
  }

  // ----- Tests for uploadAnnouncementImages -----
  @Test
  fun uploadAnnouncementImages_success() {
    val announcementId = "announcementId"
    val bitmaps = listOf(mock(Bitmap::class.java), mock(Bitmap::class.java))
    val expectedUrls = listOf("https://example.com/image1.jpg", "https://example.com/image2.jpg")
    val baos = ByteArrayOutputStream()
    bitmaps.forEach { bitmap -> bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos) }
    val imageData = baos.toByteArray()

    // Mock storageRef.child("announcements/$announcementId")
    Mockito.`when`(storageRef.child("announcements/$announcementId"))
        .thenReturn(announcementFolderRef)

    // For each image, when announcementFolderRef.child(anyString()) is called, return a new fileRef
    val fileRef1 = mock(StorageReference::class.java)
    val fileRef2 = mock(StorageReference::class.java)
    val fileRefs = listOf(fileRef1, fileRef2)
    var fileRefIndex = 0
    Mockito.`when`(announcementFolderRef.child(anyString())).thenAnswer { fileRefs[fileRefIndex++] }

    // Mock putBytes
    val mockUploadTask1 = mock(UploadTask::class.java)
    val mockUploadTask2 = mock(UploadTask::class.java)
    val mockUploadTasks = listOf(mockUploadTask1, mockUploadTask2)
    val imageDatas = listOf(imageData, imageData) // Assuming same data for simplicity

    // Mock fileRef.putBytes(imageData)
    Mockito.`when`(fileRef1.putBytes(imageDatas[0])).thenReturn(mockUploadTask1)
    Mockito.`when`(fileRef2.putBytes(imageDatas[1])).thenReturn(mockUploadTask2)

    // Mock mockUploadTask.addOnSuccessListener(...)
    mockUploadTasks.forEachIndexed { index, mockUploadTask ->
      Mockito.`when`(mockUploadTask.addOnSuccessListener(any())).thenAnswer { invocation ->
        val listener = invocation.getArgument<OnSuccessListener<UploadTask.TaskSnapshot>>(0)
        val taskSnapshot = mock(UploadTask.TaskSnapshot::class.java) // Mock the snapshot
        listener.onSuccess(taskSnapshot)
        mockUploadTask // continue the chain
      }
    }

    // Mock fileRef.downloadUrl
    Mockito.`when`(fileRef1.downloadUrl).thenReturn(Tasks.forResult(Uri.parse(expectedUrls[0])))
    Mockito.`when`(fileRef2.downloadUrl).thenReturn(Tasks.forResult(Uri.parse(expectedUrls[1])))

    // Act
    var resultUrls = listOf<String>()
    announcementRepositoryFirestore.uploadAnnouncementImages(
        announcementId,
        bitmaps,
        onSuccess = {
          resultUrls = it
          assertEquals(expectedUrls, resultUrls)
        },
        onFailure = { fail("onFailure should not be called") })

    // Wait for tasks to complete
    shadowOf(Looper.getMainLooper()).idle()
  }

  @Test
  fun uploadAnnouncementImages_failure() {
    val announcementId = "testAnnouncementId"
    val bitmap1 = mock(Bitmap::class.java)
    val images = listOf(bitmap1)
    val exception = Exception("Upload failed")

    // Mock storageRef.child("announcements/$announcementId")
    whenever(storageRef.child("announcements/$announcementId")).thenReturn(announcementFolderRef)

    // Mock fileRef
    val fileRef = mock(StorageReference::class.java)
    whenever(announcementFolderRef.child(anyString())).thenReturn(fileRef)

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
    announcementRepositoryFirestore.uploadAnnouncementImages(
        announcementId,
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
    assertEquals(exception, exceptionReceived)
  }

  // fetchAnnouncementsImageUrls tests
  @Test
  fun fetchAnnouncementsImageUrls_onSuccess_returnsUrls() {
    val announcementId = "announcement123"
    val mockDocRef = mock(DocumentReference::class.java)
    val mockSnapshot = mock(DocumentSnapshot::class.java)
    whenever(mockCollectionReference.document(announcementId)).thenReturn(mockDocRef)
    whenever(mockDocRef.get()).thenReturn(Tasks.forResult(mockSnapshot))
    whenever(mockSnapshot.get("quickFixImages")).thenReturn(listOf("url1", "url2"))

    var returnedUrls: List<String>? = null
    var failureCalled = false
    announcementRepositoryFirestore.fetchAnnouncementsImageUrls(
        announcementId = announcementId,
        onSuccess = { urls -> returnedUrls = urls },
        onFailure = { failureCalled = true })

    shadowOf(Looper.getMainLooper()).idle()

    assertFalse(failureCalled)
    assertEquals(listOf("url1", "url2"), returnedUrls)
  }

  @Test
  fun fetchAnnouncementsImageUrls_onFailure_callsOnFailure() {
    val announcementId = "announcementFail"
    val mockDocRef = mock(DocumentReference::class.java)
    whenever(mockCollectionReference.document(announcementId)).thenReturn(mockDocRef)
    val exception = Exception("Fetch URLs failed")
    whenever(mockDocRef.get()).thenReturn(Tasks.forException(exception))

    var failureCalled = false
    var returnedException: Exception? = null

    announcementRepositoryFirestore.fetchAnnouncementsImageUrls(
        announcementId = announcementId,
        onSuccess = { fail("Should not succeed") },
        onFailure = { e ->
          failureCalled = true
          returnedException = e
        })

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(failureCalled)
    assertEquals(exception, returnedException)
  }

  // fetchAnnouncementsImagesAsBitmaps tests
  @Test
  fun fetchAnnouncementsImageUrls_emptyList() {
    val announcementId = "announcementEmptyUrls"

    // Mock Firestore to return an empty list of quickFixImages
    val mockDocRef = mock(DocumentReference::class.java)
    val mockSnapshot = mock(DocumentSnapshot::class.java)
    whenever(mockCollectionReference.document(announcementId)).thenReturn(mockDocRef)
    whenever(mockDocRef.get()).thenReturn(Tasks.forResult(mockSnapshot))
    whenever(mockSnapshot.get("quickFixImages")).thenReturn(emptyList<String>())

    var onSuccessCalled = false
    var returnedUrls: List<String>? = null
    var onFailureCalled = false

    announcementRepositoryFirestore.fetchAnnouncementsImageUrls(
        announcementId = announcementId,
        onSuccess = { urls ->
          onSuccessCalled = true
          returnedUrls = urls
        },
        onFailure = { onFailureCalled = true })

    shadowOf(Looper.getMainLooper()).runToEndOfTasks()

    // Assertions: onSuccess should be called with an empty list
    assertTrue("onSuccess should be called", onSuccessCalled)
    assertFalse("onFailure should not be called", onFailureCalled)
    assertNotNull("URLs should not be null", returnedUrls)
    assertTrue("URLs should be empty", returnedUrls!!.isEmpty())
  }

  @Test
  fun fetchAnnouncementsImagesAsBitmaps_emptyUrls() {
    val announcementId = "announcementEmpty"
    // Mock fetchAnnouncementsImageUrls returning empty list
    val mockDocRef = mock(DocumentReference::class.java)
    val mockSnapshot = mock(DocumentSnapshot::class.java)
    whenever(mockCollectionReference.document(announcementId)).thenReturn(mockDocRef)
    whenever(mockDocRef.get()).thenReturn(Tasks.forResult(mockSnapshot))
    whenever(mockSnapshot.get("quickFixImages")).thenReturn(emptyList<String>())

    var successCalled = false
    var resultList: List<Pair<String, Bitmap>>? = null

    // No need for FirebaseStorage mocks since no URLs to fetch
    announcementRepositoryFirestore.fetchAnnouncementsImagesAsBitmaps(
        announcementId,
        onSuccess = {
          successCalled = true
          resultList = it
        },
        onFailure = { fail("Should not fail with empty list") })

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(successCalled)
    assertNotNull(resultList)
    assertTrue(resultList!!.isEmpty())
  }

  @Test
  fun fetchAnnouncementsImagesAsBitmaps_failureFetchingUrls() {
    val announcementId = "announcementFailUrls"
    val exception = Exception("Failed to fetch URLs")

    // Mock fetchAnnouncementsImageUrls failure
    val mockDocRef = mock(DocumentReference::class.java)
    whenever(mockCollectionReference.document(announcementId)).thenReturn(mockDocRef)
    whenever(mockDocRef.get()).thenReturn(Tasks.forException(exception))

    var failureCalled = false
    var exceptionReceived: Exception? = null

    // Since URLs fail to fetch, onFailure should be called directly
    announcementRepositoryFirestore.fetchAnnouncementsImagesAsBitmaps(
        announcementId,
        onSuccess = { fail("Should not succeed") },
        onFailure = {
          failureCalled = true
          exceptionReceived = it
        })

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(failureCalled)
    assertEquals(exception, exceptionReceived)
  }

  @Test
  fun getAnnouncementsByCategory_whenNonEmpty_callsOnSuccessWithAnnouncements() {
    val category = "Plumbing"

    // Set up mock documents
    val mockDoc1 = mock(DocumentSnapshot::class.java)
    val mockDoc2 = mock(DocumentSnapshot::class.java)

    // Mock Firestore behavior for a non-empty result
    `when`(mockCollectionReference.whereEqualTo("category", category)).thenReturn(mockQuery)
    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))
    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDoc1, mockDoc2))

    // Mock document fields
    `when`(mockDoc1.id).thenReturn("doc1")
    `when`(mockDoc1.getString("userId")).thenReturn("userId1")
    `when`(mockDoc1.getString("title")).thenReturn("Title 1")
    `when`(mockDoc1.getString("category")).thenReturn("Plumbing")
    `when`(mockDoc1.getString("description")).thenReturn("Desc 1")
    `when`(mockDoc1.get("location"))
        .thenReturn(mapOf("latitude" to 1.0, "longitude" to 2.0, "name" to "Location1"))
    `when`(mockDoc1.get("availability")).thenReturn(emptyList<Map<String, Any>>())
    `when`(mockDoc1.get("quickFixImages")).thenReturn(emptyList<String>())

    `when`(mockDoc2.id).thenReturn("doc2")
    `when`(mockDoc2.getString("userId")).thenReturn("userId2")
    `when`(mockDoc2.getString("title")).thenReturn("Title 2")
    `when`(mockDoc2.getString("category")).thenReturn("Plumbing")
    `when`(mockDoc2.getString("description")).thenReturn("Desc 2")
    `when`(mockDoc2.get("location"))
        .thenReturn(mapOf("latitude" to 3.0, "longitude" to 4.0, "name" to "Location2"))
    `when`(mockDoc2.get("availability")).thenReturn(emptyList<Map<String, Any>>())
    `when`(mockDoc2.get("quickFixImages")).thenReturn(emptyList<String>())

    var successCalled = false
    var announcementsReceived: List<Announcement>? = null

    announcementRepositoryFirestore.getAnnouncementsByCategory(
        category,
        onSuccess = {
          successCalled = true
          announcementsReceived = it
        },
        onFailure = { fail("Failure should not be called") })

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(successCalled)
    assertNotNull(announcementsReceived)
    assertEquals(2, announcementsReceived!!.size)
    assertEquals("Title 1", announcementsReceived!![0].title)
    assertEquals("Title 2", announcementsReceived!![1].title)
  }

  @Test
  fun getAnnouncementsByCategory_whenEmpty_callsOnSuccessWithEmptyList() {
    val category = "Gardening"

    // Mock Firestore behavior for empty result
    `when`(mockCollectionReference.whereEqualTo("category", category)).thenReturn(mockQuery)
    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))
    `when`(mockQuerySnapshot.isEmpty).thenReturn(true)
    `when`(mockQuerySnapshot.documents).thenReturn(emptyList())

    var successCalled = false
    var announcementsReceived: List<Announcement>? = null

    announcementRepositoryFirestore.getAnnouncementsByCategory(
        category,
        onSuccess = {
          successCalled = true
          announcementsReceived = it
        },
        onFailure = { fail("Failure should not be called") })

    shadowOf(Looper.getMainLooper()).idle()

    assertTrue(successCalled)
    assertNotNull(announcementsReceived)
    assertTrue(announcementsReceived!!.isEmpty())
  }

  @Test
  fun getAnnouncementsByCategory_whenFailure_callsOnFailure() {
    val category = "Plumbing"
    val exception = Exception("Firestore error")

    // Mock Firestore query failure
    `when`(mockQuery.get()).thenReturn(Tasks.forException(exception))

    var callbackCalled = false
    var receivedException: Exception? = null

    // Act
    announcementRepositoryFirestore.getAnnouncementsByCategory(
        category = category,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { e ->
          callbackCalled = true
          receivedException = e
        })

    shadowOf(Looper.getMainLooper()).idle()

    // Assert
    assertTrue(callbackCalled)
    assertEquals(exception, receivedException)
  }
}
