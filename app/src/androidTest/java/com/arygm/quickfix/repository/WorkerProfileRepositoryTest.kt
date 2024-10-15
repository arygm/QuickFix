package com.arygm.quickfix.repository

import com.arygm.quickfix.model.WorkerProfile
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.fail
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class WorkerProfileRepositoryTest {
  private lateinit var repository: WorkerProfileRepository
  private lateinit var mockFirestore: FirebaseFirestore
  private lateinit var mockCollection: CollectionReference

  @Before
  fun setUp() {
    // Mock Firestore
    mockFirestore = mock(FirebaseFirestore::class.java)
    // Mock Firestore collection reference
    mockCollection = mock(CollectionReference::class.java)
    // Mock collection("worker_profiles") to return the mocked collection reference
    `when`(mockFirestore.collection("worker_profiles")).thenReturn(mockCollection)
    // Initialize the repository
    repository = WorkerProfileRepository()
  }

  @Test
  fun testFilterWorkersSuccess() {
    val expectedProfiles =
        listOf(
            WorkerProfile(
                uid = "worker_123", firstName = "John", lastName = "Doe", hourlyRate = 20.0))

    // Mock DocumentSnapshot and QuerySnapshot
    val mockDocumentSnapshot = mock(DocumentSnapshot::class.java)
    `when`(mockDocumentSnapshot.toObject(WorkerProfile::class.java)).thenReturn(expectedProfiles[0])

    val mockQuerySnapshot = mock(QuerySnapshot::class.java)
    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))

    // Mock Task<QuerySnapshot> and its behavior
    val mockTask = mock(Task::class.java) as Task<QuerySnapshot>
    `when`(mockTask.isSuccessful).thenReturn(true)
    `when`(mockTask.result).thenReturn(mockQuerySnapshot)

    // Mock whereLessThan and get calls in the collection reference
    `when`(mockCollection.whereLessThan("hourlyRate", 30.0)).thenReturn(mockCollection)
    `when`(mockCollection.get()).thenReturn(mockTask)

    // Call the repository function and verify behavior
    repository.filterWorkers(
        hourlyRateThreshold = 30.0,
        onSuccess = { profiles -> assertEquals(expectedProfiles, profiles) },
        onFailure = { fail("Should not reach failure callback") })
  }

  @Test
  fun testFilterWorkersFailure() {
    val mockException = Exception("Firestore error")

    // Mock the Task<QuerySnapshot> and its failure behavior
    val mockTask = mock(Task::class.java) as Task<QuerySnapshot>
    `when`(mockTask.isSuccessful).thenReturn(false)
    `when`(mockTask.exception).thenReturn(mockException)

    // Mock Firestore query to return the mocked Task
    val mockCollection = mockFirestore.collection("worker_profiles")
    `when`(mockCollection.whereLessThan("hourlyRate", 30.0)).thenReturn(mockCollection)
    `when`(mockCollection.get()).thenReturn(mockTask)

    // Call the repository's function and verify the error handling
    repository.filterWorkers(
        hourlyRateThreshold = 30.0,
        onSuccess = { fail("Should not reach success callback") },
        onFailure = { error -> assertEquals("Firestore error", error.message) })
  }
}
