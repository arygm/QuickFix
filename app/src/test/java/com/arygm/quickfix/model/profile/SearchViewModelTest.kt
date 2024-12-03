package com.arygm.quickfix.model.profile

import com.arygm.quickfix.model.category.CategoryRepositoryFirestore
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.search.SearchViewModel
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

class SearchViewModelTest {
  private lateinit var viewModel: SearchViewModel
  private lateinit var mockRepository: WorkerProfileRepositoryFirestore
  private lateinit var cateRepository: CategoryRepositoryFirestore

  @Before
  fun setUp() {
    // Mock the repository
    mockRepository = mock(WorkerProfileRepositoryFirestore::class.java)
    cateRepository = mock(CategoryRepositoryFirestore::class.java)
    // Initialize the ViewModel with the mocked repository
    viewModel = SearchViewModel(mockRepository)
  }

  @Test
  fun testFilterWorkerProfilesByHourlyRateSuccess() {
    // Arrange: Prepare expected worker profile and mock repository success behavior
    val expectedProfiles =
        listOf(
            WorkerProfile(
                uid = "worker_123",
                price = 25.0,
                fieldOfWork = "Plumber",
                location = Location(46.0, 6.0, "Test Location")))

    // Mock repository behavior for a successful response
    doAnswer { invocation ->
          val onSuccess = invocation.arguments[6] as (List<WorkerProfile>) -> Unit
          onSuccess(expectedProfiles) // Simulate success callback
          null
        }
        .`when`(mockRepository)
        .filterWorkers(
            anyOrNull(),
            anyOrNull(),
            eq(30.0),
            isNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull())

    // Act: Call ViewModel's function to filter worker profiles
    viewModel.filterWorkerProfiles(price = 30.0)

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
          val onFailure = invocation.arguments[7] as (Exception) -> Unit
          onFailure(Exception(errorMessage)) // Simulate failure callback
          null
        }
        .`when`(mockRepository)
        .filterWorkers(
            anyOrNull(),
            anyOrNull(),
            eq(30.0),
            isNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull())

    // Act: Call ViewModel's function to filter worker profiles
    viewModel.filterWorkerProfiles(price = 30.0)

    // Assert: Check that the ViewModel state is updated correctly on failure
    assertTrue(viewModel.workerProfiles.value.isEmpty()) // The list should be empty
    assertEquals(errorMessage, viewModel.errorMessage.value) // Ensure error message is set
  }

  @Test
  fun testFilterWorkerProfilesByFieldOfWorkSuccess() {
    // Arrange: Prepare expected worker profiles and mock repository success behavior
    val expectedProfiles =
        listOf(
            WorkerProfile(
                uid = "worker_124",
                price = 40.0,
                fieldOfWork = "Electrician",
                location = Location(46.0, 7.0, "Another Test Location")))

    // Mock repository behavior for a successful response
    doAnswer { invocation ->
          val onSuccess = invocation.arguments[6] as (List<WorkerProfile>) -> Unit
          onSuccess(expectedProfiles) // Simulate success callback
          null
        }
        .`when`(mockRepository)
        .filterWorkers(
            anyOrNull(),
            anyOrNull(),
            isNull(),
            eq("Electrician"),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull())

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
            WorkerProfile(
                uid = "worker_125",
                price = 30.0,
                fieldOfWork = "Handyman",
                location = Location(46.5, 6.6, "Nearby Location")))

    // Mock repository behavior
    doAnswer { invocation ->
          val onSuccess = invocation.arguments[6] as (List<WorkerProfile>) -> Unit
          onSuccess(profiles) // Simulate success callback
          null
        }
        .`when`(mockRepository)
        .filterWorkers(
            isNull(),
            anyOrNull(),
            isNull(),
            isNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull())

    // Act: Filter profiles by distance (user's location vs worker's location)
    viewModel.filterWorkerProfiles(
        location = Location(46.0, 6.0, "User Location"), maxDistanceInKm = 100.0)

    // Assert: Ensure the profiles list contains the correct workers within the distance
    val result = viewModel.workerProfiles.value
    assertEquals(1, result.size)
    assertEquals("worker_125", result[0].uid)
  }

  @Test
  fun testFilterWorkerProfilesWithAllNullParameters() {
    // Arrange: Prepare worker profiles
    val profiles =
        listOf(
            WorkerProfile(
                uid = "worker_126",
                price = 20.0,
                fieldOfWork = "Gardening",
                location = Location(45.5, 6.0, "Far Away Location")))

    // Mock repository behavior for a successful response
    doAnswer { invocation ->
          val onSuccess = invocation.arguments[6] as (List<WorkerProfile>) -> Unit
          onSuccess(profiles) // Simulate success callback
          null
        }
        .`when`(mockRepository)
        .filterWorkers(
            isNull(),
            anyOrNull(),
            isNull(),
            isNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull())

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
            WorkerProfile(
                uid = "worker_127",
                price = 45.0,
                fieldOfWork = "Journalist",
                location = Location(47.0, 8.0, "Metropolis")),
            WorkerProfile(
                uid = "worker_128",
                price = 80.0,
                fieldOfWork = "CEO",
                location = Location(40.0, -74.0, "Gotham City")))

    // Mock repository behavior for a successful response
    doAnswer { invocation ->
          val onSuccess = invocation.arguments[6] as (List<WorkerProfile>) -> Unit
          onSuccess(profiles) // Simulate success callback
          null
        }
        .`when`(mockRepository)
        .filterWorkers(
            isNull(),
            anyOrNull(),
            eq(50.0),
            isNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull())

    // Act: Apply multiple filters
    viewModel.filterWorkerProfiles(
        price = 50.0, location = Location(46.0, 6.0, "User Location"), maxDistanceInKm = 500.0)

    // Assert: Only "worker_127" should match the filters
    val result = viewModel.workerProfiles.value
    assertEquals(1, result.size)
    assertEquals("worker_127", result[0].uid)
  }
}
