package com.arygm.quickfix.viewmodel

import com.arygm.quickfix.model.WorkerProfile
import com.arygm.quickfix.repository.WorkerProfileRepository
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.ArgumentMatchers.isNull
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock

class WorkerProfileViewModelTest {
  private lateinit var viewModel: WorkerProfileViewModel
  private lateinit var mockRepository: WorkerProfileRepository

  @Before
  fun setUp() {
    // Mock the repository
    mockRepository = mock(WorkerProfileRepository::class.java)
    // Initialize the ViewModel with the mocked repository
    viewModel = WorkerProfileViewModel(mockRepository)
  }

  @Test
  fun testFilterWorkerProfilesByHourlyRateSuccess() {
    // Arrange: Prepare expected worker profile and mock repository success behavior
    val expectedProfiles =
        listOf(
            WorkerProfile(
                uid = "worker_123", firstName = "John", lastName = "Doe", hourlyRate = 25.0))

    // Mock repository behavior for a successful response
    doAnswer { invocation ->
          val onSuccess = invocation.arguments[3] as (List<WorkerProfile>) -> Unit
          onSuccess(expectedProfiles) // Simulate success callback
          null
        }
        .`when`(mockRepository)
        .filterWorkers(eq(30.0), isNull(), any(), any())

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
          val onFailure = invocation.arguments[4] as (Exception) -> Unit
          onFailure(Exception(errorMessage)) // Simulate failure callback
          null
        }
        .`when`(mockRepository)
        .filterWorkers(eq(30.0), isNull(), any(), any())

    // Act: Call ViewModel's function to filter worker profiles
    viewModel.filterWorkerProfiles(hourlyRateThreshold = 30.0)

    // Assert: Check that the ViewModel state is updated correctly on failure
    assertTrue(viewModel.workerProfiles.value.isEmpty()) // The list should be empty
    assertEquals(errorMessage, viewModel.errorMessage.value) // Ensure error message is set
  }
}
