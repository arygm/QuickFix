package com.arygm.quickfix.model.profile

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.eq
import org.mockito.ArgumentMatchers.isNull
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.kotlin.anyOrNull

class WorkerListViewModelTest {
  private lateinit var viewModel: WorkerListViewModel
  private lateinit var mockRepository: ProfileRepositoryFirestore

  @Before
  fun setUp() {
    // Mock the repository
    mockRepository = mock(ProfileRepositoryFirestore::class.java)
    // Initialize the ViewModel with the mocked repository
    viewModel = WorkerListViewModel(mockRepository)
  }

  @Test
  fun testFilterWorkerProfilesByHourlyRateSuccess() {
    // Arrange: Prepare expected worker profile and mock repository success behavior
    val expectedProfiles =
        listOf(
            Profile(
                uid = "worker_123",
                firstName = "John",
                lastName = "Doe",
                email = "johnDoes@gmail.com",
                birthDate = Timestamp.now(),
                description = "I'm a professional worker",
                isWorker = true,
                fieldOfWork = "Plumber",
                hourlyRate = 25.0,
                location = GeoPoint(46.0, 6.0) // Add location as GeoPoint
                ))

    // Mock repository behavior for a successful response
    doAnswer { invocation ->
          val onSuccess = invocation.arguments[2] as (List<Profile>) -> Unit
          onSuccess(expectedProfiles) // Simulate success callback
          null
        }
        .`when`(mockRepository)
        .filterWorkers(eq(30.0), isNull(), anyOrNull(), anyOrNull())

    // Act: Call ViewModel's function to filter worker profiles
    viewModel.filterWorkerProfiles(hourlyRateThreshold = 30.0)

    // Assert: Check that the ViewModel state is updated correctly
    assertEquals(expectedProfiles, viewModel.workerProfiles.value)
    assertNull(viewModel.errorMessage.value) // Ensure there's no error message
  }

  @Test
  fun testFilterWorkerProfilesByHourlyRateFailure() {
    // Arrange: Mock an error message and simulate repository failure behavior
    val errorMessage = "Failed to fetch profiles"

    // Mock repository behavior for a failure response
    doAnswer { invocation ->
          val onFailure = invocation.arguments[3] as (Exception) -> Unit
          onFailure(Exception(errorMessage)) // Simulate failure callback
          null
        }
        .`when`(mockRepository)
        .filterWorkers(eq(30.0), isNull(), anyOrNull(), anyOrNull())

    // Act: Call ViewModel's function to filter worker profiles
    viewModel.filterWorkerProfiles(hourlyRateThreshold = 30.0)

    // Assert: Check that the ViewModel state is updated correctly on failure
    assertTrue(viewModel.workerProfiles.value.isEmpty()) // The list should be empty
    assertEquals(errorMessage, viewModel.errorMessage.value) // Ensure error message is set
  }

  @Test
  fun testFilterWorkerProfilesByFieldOfWorkSuccess() {
    // Arrange: Prepare expected worker profiles and mock repository success behavior
    val expectedProfiles =
        listOf(
            Profile(
                uid = "worker_124",
                firstName = "Jane",
                lastName = "Doe",
                email = "janeDoe@gmail.com",
                birthDate = Timestamp.now(),
                description = "Experienced electrician",
                isWorker = true,
                fieldOfWork = "Electrician",
                hourlyRate = 40.0,
                location = GeoPoint(46.0, 7.0) // Add location as GeoPoint
                ))

    // Mock repository behavior for a successful response
    doAnswer { invocation ->
          val onSuccess = invocation.arguments[2] as (List<Profile>) -> Unit
          onSuccess(expectedProfiles) // Simulate success callback
          null
        }
        .`when`(mockRepository)
        .filterWorkers(isNull(), eq("Electrician"), anyOrNull(), anyOrNull())

    // Act: Call ViewModel's function to filter worker profiles by field of work
    viewModel.filterWorkerProfiles(fieldOfWork = "Electrician")

    // Assert: Check that the ViewModel state is updated correctly
    assertEquals(expectedProfiles, viewModel.workerProfiles.value)
    assertNull(viewModel.errorMessage.value)
  }

  @Test
  fun testFilterWorkerProfilesByDistanceSuccess() {
    // Arrange: Prepare expected worker profiles
    val profiles =
        listOf(
            Profile(
                uid = "worker_125",
                firstName = "Alice",
                lastName = "Smith",
                email = "aliceSmith@gmail.com",
                birthDate = Timestamp.now(),
                description = "Handyman",
                isWorker = true,
                fieldOfWork = "Handyman",
                hourlyRate = 30.0,
                location = GeoPoint(46.5, 6.6) // Worker location as GeoPoint
                ))

    // Mock repository behavior
    doAnswer { invocation ->
          val onSuccess = invocation.arguments[2] as (List<Profile>) -> Unit
          onSuccess(profiles) // Simulate success callback
          null
        }
        .`when`(mockRepository)
        .filterWorkers(isNull(), isNull(), anyOrNull(), anyOrNull())

    // Act: Filter profiles by distance (user's location vs worker's location)
    viewModel.filterWorkerProfiles(userLat = 46.0, userLon = 6.0, maxDistanceInKm = 100.0)

    // Assert: Ensure the profiles list contains the correct workers within the distance
    val result = viewModel.workerProfiles.value
    assertEquals(1, result.size)
    assertEquals("Alice", result[0].firstName)
  }

  @Test
  fun testFilterWorkerProfilesWithAllNullParameters() {
    // Arrange: Prepare worker profiles
    val profiles =
        listOf(
            Profile(
                uid = "worker_126",
                firstName = "Bob",
                lastName = "Marley",
                email = "bobMarley@gmail.com",
                birthDate = Timestamp.now(),
                description = "Gardener",
                isWorker = true,
                fieldOfWork = "Gardening",
                hourlyRate = 20.0,
                location = GeoPoint(45.5, 6.0) // Add location as GeoPoint
                ))

    // Mock repository behavior for a successful response
    doAnswer { invocation ->
          val onSuccess = invocation.arguments[2] as (List<Profile>) -> Unit
          onSuccess(profiles) // Simulate success callback
          null
        }
        .`when`(mockRepository)
        .filterWorkers(isNull(), isNull(), anyOrNull(), anyOrNull())

    // Act: Call ViewModel's function with all null parameters
    viewModel.filterWorkerProfiles()

    // Assert: Check that all profiles are returned without any filtering
    assertEquals(profiles, viewModel.workerProfiles.value)
  }

  @Test
  fun testFilterWorkerProfilesWithMultipleFilters() {
    // Arrange: Prepare worker profiles
    val profiles =
        listOf(
            Profile(
                uid = "worker_127",
                firstName = "Clark",
                lastName = "Kent",
                email = "clark.kent@dailyplanet.com",
                birthDate = Timestamp.now(),
                description = "Professional superhero",
                isWorker = true,
                fieldOfWork = "Journalist",
                hourlyRate = 45.0,
                location = GeoPoint(47.0, 8.0) // Add location as GeoPoint
                ),
            Profile(
                uid = "worker_128",
                firstName = "Bruce",
                lastName = "Wayne",
                email = "bruce.wayne@waynecorp.com",
                birthDate = Timestamp.now(),
                description = "Businessman",
                isWorker = true,
                fieldOfWork = "CEO",
                hourlyRate = 80.0,
                location = GeoPoint(40.0, -74.0) // Add location as GeoPoint
                ))

    // Mock repository behavior for a successful response
    doAnswer { invocation ->
          val onSuccess = invocation.arguments[2] as (List<Profile>) -> Unit
          onSuccess(profiles) // Simulate success callback
          null
        }
        .`when`(mockRepository)
        .filterWorkers(eq(50.0), isNull(), anyOrNull(), anyOrNull())

    // Act: Apply multiple filters
    viewModel.filterWorkerProfiles(
        hourlyRateThreshold = 50.0, userLat = 46.0, userLon = 6.0, maxDistanceInKm = 500.0)

    // Assert: Only Clark Kent should match the filters
    val result = viewModel.workerProfiles.value
    assertEquals(1, result.size)
    assertEquals("Clark", result[0].firstName)
  }
}
