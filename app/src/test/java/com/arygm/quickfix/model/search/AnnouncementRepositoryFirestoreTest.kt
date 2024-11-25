package com.arygm.quickfix.model.search

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.arygm.quickfix.model.locations.Location
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.time.LocalDateTime
import junit.framework.TestCase.fail
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.timeout
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

  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot

  private lateinit var announcementRepositoryFirestore: AnnouncementRepositoryFirestore

  private val announcement1 =
      Announcement(
          announcementId = "announcement1",
          userId = "user1",
          title = "Test Announcement",
          category = "Test Category",
          description = "Test Description",
          location = Location(37.7749, -122.4194, "San Francisco"),
          availability =
              listOf(
                  AvailabilitySlot(
                      start = LocalDateTime.parse("2024-11-24T10:00:00"),
                      end = LocalDateTime.parse("2024-11-24T14:00:00"))),
          quickFixImages = listOf("image1.jpg", "image2.jpg"))
  private val announcement2 =
      Announcement(
          announcementId = "announcement2",
          userId = "user2",
          title = "Test Announcement",
          category = "Test Category",
          description = "Test Description",
          location = Location(37.7749, -122.4194, "San Francisco"),
          availability =
              listOf(
                  AvailabilitySlot(
                      start = LocalDateTime.parse("2024-11-24T10:00:00"),
                      end = LocalDateTime.parse("2024-11-24T14:00:00"))),
          quickFixImages = listOf("image1.jpg", "image2.jpg"))

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    // Ensure FirebaseApp is initialized
    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    // Initialize the repository
    announcementRepositoryFirestore = AnnouncementRepositoryFirestore(mockFirestore)

    // Mock the Firestore structure
    `when`(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(any())).thenReturn(mockDocumentReference)
    `when`(mockCollectionReference.document()).thenReturn(mockDocumentReference)
  }

  @Test
  fun verifyMocks() {
    // Check that the collection method is returning a mock
    assertNotNull(mockFirestore.collection("announcements"))

    // Ensure that document calls return proper mocks
    assertNotNull(mockCollectionReference.document("announcement1"))
  }

  // ----- Get New UID Test -----
  @Test
  fun getNewUid() {
    `when`(mockCollectionReference.document().id).thenReturn("1")
    val uid = announcementRepositoryFirestore.getNewUid()
    assertEquals("1", uid)
  }

  // ----- Get Announcements Tests -----
  @Test
  fun getAnnouncements_onSuccess_callsDocuments() {
    // Ensure that mockToDoQuerySnapshot is properly initialized and mocked
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))

    // Ensure the QuerySnapshot returns a list of mock DocumentSnapshots
    `when`(mockQuerySnapshot.documents).thenReturn(listOf())

    // Call the method under test
    announcementRepositoryFirestore.getAnnouncements(
        onSuccess = {

          // Do nothing; we just want to verify that the 'documents' field was accessed
        },
        onFailure = { fail("Failure callback should not be called") })

    // Verify that the 'documents' field was accessed
    verify(timeout(100)) { (mockQuerySnapshot).documents }
  }

  @Test
  fun getAnnouncementsOnFailureTest() {
    val exception = Exception("Firestore error")
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forException(exception))

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
    whenever(mockDocument1.get("title")).thenReturn("Title 1")
    whenever(mockDocument2.get("title")).thenReturn("Title 2")

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
    `when`(mockCollectionReference.whereIn(FieldPath.documentId(), listOf("id1", "id2")))
        .thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.get()).thenReturn(taskCompletionSource.task)

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
    `when`(mockAnnouncementDocument.set(any<Announcement>())).thenReturn(Tasks.forResult(null))

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
    val userId = "user1"
    val announcementId = "id1"
    whenever(mockCollectionReference.document(announcementId)).thenReturn(mockDocumentReference)
    whenever(mockDocumentReference.delete()).thenReturn(Tasks.forResult(null))

    var callbackCalled = false

    announcementRepositoryFirestore.deleteAnnouncementById(
        userId = userId,
        announcementId = announcementId,
        onSuccess = { callbackCalled = true },
        onFailure = { fail("Failure callback should not be called") })
    shadowOf(Looper.getMainLooper()).idle()
    assert(callbackCalled)
  }

  @Test
  fun deleteAnnouncementById_onFailure_callsOnFailure() {
    val userId = "user1"
    val announcementId = "id1"
    val exception = Exception("Test exception")
    whenever(mockCollectionReference.document(announcementId)).thenReturn(mockDocumentReference)
    whenever(mockDocumentReference.delete()).thenReturn(Tasks.forException(exception))

    var failureCallbackCalled = false

    announcementRepositoryFirestore.deleteAnnouncementById(
        userId = userId,
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
    val document = Mockito.mock(DocumentSnapshot::class.java)
    whenever(document.id).thenReturn("id1")
    whenever(document.getString("userId")).thenReturn("user1")
    whenever(document.getString("title")).thenReturn("Test Title")
    whenever(document.getString("category")).thenReturn("Category")
    whenever(document.getString("description")).thenReturn("Test Description")
    whenever(document.get("location"))
        .thenReturn(mapOf("latitude" to 0.0, "longitude" to 0.0, "name" to "Test Location"))
    whenever(document.get("availability"))
        .thenReturn(listOf(mapOf("start" to "2024-11-25T00:00", "end" to "2024-11-25T01:00")))
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
    assertEquals("2024-11-25T00:00", result.availability[0].start.toString())
    assertEquals("2024-11-25T01:00", result.availability[0].end.toString())
    assertEquals(listOf("image1", "image2"), result.quickFixImages)
  }

  @Test
  fun documentToAnnouncement_invalidDocument_returnsNull() {
    val document = Mockito.mock(DocumentSnapshot::class.java)
    whenever(document.getString("userId")).thenReturn(null) // Missing required field

    val result = invokeDocumentToAnnouncement(document)

    assertNull(result)
  }
}
