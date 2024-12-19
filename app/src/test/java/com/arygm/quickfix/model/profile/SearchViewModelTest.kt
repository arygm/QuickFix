package com.arygm.quickfix.model.profile

import com.arygm.quickfix.model.category.CategoryRepositoryFirestore
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.profile.dataFields.AddOnService
import com.arygm.quickfix.model.profile.dataFields.IncludedService
import com.arygm.quickfix.model.profile.dataFields.Review
import com.arygm.quickfix.model.search.SearchViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.kotlin.any

class SearchViewModelTest {
  private lateinit var viewModel: SearchViewModel
  private lateinit var mockRepository: WorkerProfileRepositoryFirestore
  private lateinit var cateRepository: CategoryRepositoryFirestore
  private val testDispatcher = StandardTestDispatcher()

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setUp() {
    // Mock the repository
    mockRepository = mock(WorkerProfileRepositoryFirestore::class.java)
    cateRepository = mock(CategoryRepositoryFirestore::class.java)
    // Initialize the ViewModel with the mocked repository
    viewModel = SearchViewModel(mockRepository)
    Dispatchers.setMain(testDispatcher)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @After
  fun tearDown() {
    // Reset the main dispatcher to the original Main dispatcher
    Dispatchers.resetMain()
  }

  @Test
  fun testFilterWorkersBySubcategorySuccess() {
    // Arrange: Prepare expected worker profiles
    val workers =
        listOf(
            WorkerProfile(uid = "worker_1", fieldOfWork = "Plumbing", displayName = "Worker 1"),
            WorkerProfile(uid = "worker_2", fieldOfWork = "Electrician", displayName = "Worker 2"),
            WorkerProfile(uid = "worker_3", fieldOfWork = "Plumbing", displayName = "Worker 3"))

    // Mock repository behavior to call onSuccess with the workers list
    doAnswer { invocation ->
          val onSuccess = invocation.arguments[0] as (List<WorkerProfile>) -> Unit
          onSuccess(workers)
          null
        }
        .`when`(mockRepository)
        .getProfiles(any(), any())

    // Act: Call ViewModel's function to filter workers by subcategory
    viewModel.filterWorkersBySubcategory("Plumbing") {
      // Callback can be used to perform additional assertions if needed
    }

    // Assert: Check that the ViewModel state is updated correctly
    val result = viewModel.subCategoryWorkerProfiles.value
    assertEquals(2, result.size)
    assertTrue(result.any { it.uid == "worker_1" })
    assertTrue(result.any { it.uid == "worker_3" })
    assertFalse(result.any { it.uid == "worker_2" })
  }

  @Test
  fun testFilterWorkersBySubcategoryNoMatch() {
    // Arrange: Prepare worker profiles with no matching fieldOfWork
    val workers =
        listOf(
            WorkerProfile(uid = "worker_1", fieldOfWork = "Electrician", displayName = "Worker 1"),
            WorkerProfile(uid = "worker_2", fieldOfWork = "Carpentry", displayName = "Worker 2"))

    // Mock repository behavior to call onSuccess with the workers list
    doAnswer { invocation ->
          val onSuccess = invocation.arguments[0] as (List<WorkerProfile>) -> Unit
          onSuccess(workers)
          null
        }
        .`when`(mockRepository)
        .getProfiles(any(), any())

    // Act: Call ViewModel's function to filter workers by subcategory with no matches
    viewModel.filterWorkersBySubcategory("Plumbing") {
      // Callback can be used to perform additional assertions if needed
    }

    // Assert: Ensure the result list is empty
    val result = viewModel.subCategoryWorkerProfiles.value
    assertTrue(result.isEmpty())
  }

  @Test
  fun testFilterWorkersBySubcategoryEmptyList() {
    // Arrange: Simulate repository returning an empty list
    doAnswer { invocation ->
          val onSuccess = invocation.arguments[0] as (List<WorkerProfile>) -> Unit
          onSuccess(emptyList())
          null
        }
        .`when`(mockRepository)
        .getProfiles(any(), any())

    // Act: Call ViewModel's function to filter workers by subcategory
    viewModel.filterWorkersBySubcategory("Plumbing") {
      // Callback can be used to perform additional assertions if needed
    }

    // Assert: Ensure the result list is empty
    val result = viewModel.subCategoryWorkerProfiles.value
    assertTrue(result.isEmpty())
  }

  @Test
  fun testFilterWorkersBySubcategoryFailure() {
    // Arrange: Simulate repository failure with an exception
    val errorMessage = "Failed to fetch profiles"

    doAnswer { invocation ->
          val onFailure = invocation.arguments[1] as (Exception) -> Unit
          onFailure(Exception(errorMessage))
          null
        }
        .`when`(mockRepository)
        .getProfiles(any(), any())

    // Act: Call ViewModel's function to filter workers by subcategory
    viewModel.filterWorkersBySubcategory("Plumbing") {
      // Callback can be used to perform additional assertions if needed
    }

    // Assert: Ensure no profiles are returned and error message is set
    val result = viewModel.subCategoryWorkerProfiles.value
    assertTrue(result.isEmpty())
  }

  @Test
  fun testFilterWorkersBySubcategorySuccess_CallsOnComplete() = runTest {
    // Arrange: Prepare expected worker profiles
    val workers =
        listOf(
            WorkerProfile(
                uid = "worker_1",
                fieldOfWork = "Plumbing",
                description = "Expert in residential plumbing.",
                displayName = "John Doe",
                tags = listOf("Reliable", "Experienced"),
                includedServices = listOf(),
                addOnServices = listOf(),
                reviews =
                    ArrayDeque(
                        listOf(
                            Review(username = "User1", review = "Great service!", rating = 5.0))),
                price = 30.0,
                location = Location(46.0, 6.0, "Location A"),
                rating = 4.5),
            WorkerProfile(
                uid = "worker_2",
                fieldOfWork = "Electrician",
                description = "Certified electrician with 10 years of experience.",
                displayName = "Jane Smith",
                tags = listOf("Certified", "Prompt"),
                includedServices = listOf(),
                addOnServices = listOf(),
                reviews =
                    ArrayDeque(
                        listOf(
                            Review(
                                username = "User2", review = "Very professional.", rating = 4.8))),
                price = 40.0,
                location = Location(46.5, 6.5, "Location B"),
                rating = 4.8),
            WorkerProfile(
                uid = "worker_3",
                fieldOfWork = "Plumbing",
                description = "Affordable plumbing services for all your needs.",
                displayName = "Bob Johnson",
                tags = listOf("Affordable", "Quick"),
                includedServices = listOf(),
                addOnServices = listOf(),
                reviews =
                    ArrayDeque(
                        listOf(
                            Review(
                                username = "User3",
                                review = "Quick and efficient.",
                                rating = 4.2))),
                price = 25.0,
                location = Location(47.0, 7.0, "Location C"),
                rating = 4.2))

    // Mock repository behavior to call onSuccess with the workers list
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<(List<WorkerProfile>) -> Unit>(0)
          onSuccess(workers)
          null
        }
        .`when`(mockRepository)
        .getProfiles(any(), any())

    // Set up a flag to verify if onComplete is called
    var onCompleteCalled = false
    val onComplete: () -> Unit = { onCompleteCalled = true }

    // Act: Call ViewModel's function to filter workers by subcategory
    viewModel.filterWorkersBySubcategory("Plumbing", onComplete)

    // Advance coroutine until idle to ensure callbacks are executed
    testDispatcher.scheduler.advanceUntilIdle()

    // Assert: Check that the ViewModel state is updated correctly
    val result = viewModel.subCategoryWorkerProfiles.value
    assertEquals(2, result.size)
    assertTrue(result.any { it.uid == "worker_1" })
    assertTrue(result.any { it.uid == "worker_3" })
    assertFalse(result.any { it.uid == "worker_2" })

    // Verify that onComplete was called
    assertTrue(onCompleteCalled)
  }

  @Test
  fun testSearchEngineSuccess() = runTest {
    // Arrange: Prepare worker profiles
    val workers =
        listOf(
            WorkerProfile(
                uid = "worker_1",
                fieldOfWork = "Plumbing",
                description = "Certified expert in residential plumbing.",
                displayName = "John Doe",
                tags = listOf("Reliable", "Experienced"),
                includedServices = listOf(),
                addOnServices = listOf(),
                reviews =
                    ArrayDeque(
                        listOf(
                            Review(username = "User1", review = "Great service!", rating = 5.0))),
                price = 30.0,
                location = Location(46.0, 6.0, "Location A"),
                rating = 4.5),
            WorkerProfile(
                uid = "worker_2",
                fieldOfWork = "Electrician",
                description = "Certified electrician with 10 years of experience.",
                displayName = "Jane Smith",
                tags = listOf("Certified", "Prompt"),
                includedServices = listOf(),
                addOnServices = listOf(),
                reviews =
                    ArrayDeque(
                        listOf(
                            Review(
                                username = "User2", review = "Very professional.", rating = 4.8))),
                price = 40.0,
                location = Location(46.5, 6.5, "Location B"),
                rating = 4.8),
            WorkerProfile(
                uid = "worker_3",
                fieldOfWork = "Plumbing",
                description = "Affordable plumbing services for all your needs.",
                displayName = "Bob Johnson",
                tags = listOf("Affordable", "Quick"),
                includedServices = listOf(),
                addOnServices = listOf(),
                reviews =
                    ArrayDeque(
                        listOf(
                            Review(
                                username = "User3",
                                review = "Quick and efficient.",
                                rating = 4.2))),
                price = 25.0,
                location = Location(47.0, 7.0, "Location C"),
                rating = 4.2))

    // Mock repository behavior to call onSuccess with the workers list
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<(List<WorkerProfile>) -> Unit>(0)
          onSuccess(workers)
          null
        }
        .`when`(mockRepository)
        .getProfiles(any(), any())

    // Act: Call ViewModel's searchEngine function with a query
    val query = "plumbing certified"
    viewModel.searchEngine(query)

    // Advance coroutine until idle to ensure callbacks are executed
    testDispatcher.scheduler.advanceUntilIdle()

    // Assert: Check that the workerProfilesSuggestions state is updated correctly
    val expectedFiltered =
        listOf(workers[0]) // Only worker_1 matches both "plumbing" and "certified"

    assertEquals(expectedFiltered, viewModel.workerProfilesSuggestions.value)
  }

  @Test
  fun testSearchEnginePartialMatch() = runTest {
    // Arrange: Prepare worker profiles
    val workers =
        listOf(
            WorkerProfile(
                uid = "worker_1",
                fieldOfWork = "Plumbing",
                description = "Expert in residential plumbing.",
                displayName = "John Doe",
                tags = listOf("Reliable", "Experienced"),
                includedServices = listOf(),
                addOnServices = listOf(),
                reviews =
                    ArrayDeque(
                        listOf(
                            Review(username = "User1", review = "Great service!", rating = 5.0))),
                price = 30.0,
                location = Location(46.0, 6.0, "Location A"),
                rating = 4.5),
            WorkerProfile(
                uid = "worker_2",
                fieldOfWork = "Electrician",
                description = "Certified electrician with 10 years of experience.",
                displayName = "Jane Smith",
                tags = listOf("Certified", "Prompt"),
                includedServices = listOf(),
                addOnServices = listOf(),
                reviews =
                    ArrayDeque(
                        listOf(
                            Review(
                                username = "User2", review = "Very professional.", rating = 4.8))),
                price = 40.0,
                location = Location(46.5, 6.5, "Location B"),
                rating = 4.8))

    // Mock repository behavior to call onSuccess with the workers list
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<(List<WorkerProfile>) -> Unit>(0)
          onSuccess(workers)
          null
        }
        .`when`(mockRepository)
        .getProfiles(any(), any())

    // Act: Call ViewModel's searchEngine function with a partial match query
    val query = "certified plumber"
    viewModel.searchEngine(query)

    // Advance coroutine until idle to ensure callbacks are executed
    testDispatcher.scheduler.advanceUntilIdle()

    // Assert: Check that the workerProfilesSuggestions state is updated correctly
    val expectedFiltered =
        emptyList<WorkerProfile>() // No worker matches both "certified" and "plumber"

    assertEquals(expectedFiltered, viewModel.workerProfilesSuggestions.value)
  }

  @Test
  fun testSearchEngineCaseInsensitive() = runTest {
    // Arrange: Prepare worker profiles
    val workers =
        listOf(
            WorkerProfile(
                uid = "worker_1",
                fieldOfWork = "Plumbing",
                description = "Expert in residential plumbing.",
                displayName = "John Doe",
                tags = listOf("Reliable", "Experienced"),
                includedServices = listOf(),
                addOnServices = listOf(),
                reviews =
                    ArrayDeque(
                        listOf(
                            Review(username = "User1", review = "Great service!", rating = 5.0))),
                price = 30.0,
                location = Location(46.0, 6.0, "Location A"),
                rating = 4.5))

    // Mock repository behavior to call onSuccess with the workers list
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<(List<WorkerProfile>) -> Unit>(0)
          onSuccess(workers)
          null
        }
        .`when`(mockRepository)
        .getProfiles(any(), any())

    // Act: Call ViewModel's searchEngine function with different casing
    val query = "PlUmBiNg"
    viewModel.searchEngine(query)

    // Advance coroutine until idle to ensure callbacks are executed
    testDispatcher.scheduler.advanceUntilIdle()

    // Assert: Check that the workerProfilesSuggestions state is updated correctly
    val expectedFiltered = listOf(workers[0])

    assertEquals(expectedFiltered, viewModel.workerProfilesSuggestions.value)
  }

  @Test
  fun testSearchEngineFailure() = runTest {
    // Arrange: Simulate repository failure with an exception
    val errorMessage = "Failed to fetch profiles"

    doAnswer { invocation ->
          val onFailure = invocation.getArgument<(Exception) -> Unit>(1)
          onFailure(Exception(errorMessage))
          null
        }
        .`when`(mockRepository)
        .getProfiles(any(), any())

    // Act: Call ViewModel's searchEngine function
    val query = "plumbing"
    viewModel.searchEngine(query)

    // Advance coroutine until idle to ensure callbacks are executed
    testDispatcher.scheduler.advanceUntilIdle()

    // Assert: Ensure that workerProfilesSuggestions is empty
    assertTrue(viewModel.workerProfilesSuggestions.value.isEmpty())
  }

  @Test
  fun testSearchEngineMultipleMatches() = runTest {
    // Arrange: Prepare worker profiles with multiple matches
    val workers =
        listOf(
            WorkerProfile(
                uid = "worker_1",
                fieldOfWork = "Plumbing",
                description = "Expert in residential plumbing.",
                displayName = "John Doe",
                tags = listOf("Reliable", "Experienced"),
                includedServices = listOf(IncludedService(name = "Basic Consultation")),
                addOnServices = listOf(AddOnService(name = "Express Delivery")),
                reviews =
                    ArrayDeque(
                        listOf(
                            Review(username = "User1", review = "Great service!", rating = 5.0))),
                price = 30.0,
                location = Location(46.0, 6.0, "Location A"),
                rating = 4.5),
            WorkerProfile(
                uid = "worker_2",
                fieldOfWork = "Electrician",
                description = "Certified electrician with 10 years of experience.",
                displayName = "Jane Smith",
                tags = listOf("Certified", "Prompt"),
                includedServices = listOf(IncludedService(name = "Service Inspection")),
                addOnServices = listOf(AddOnService(name = "Premium Materials")),
                reviews =
                    ArrayDeque(
                        listOf(
                            Review(
                                username = "User2", review = "Very professional.", rating = 4.8))),
                price = 40.0,
                location = Location(46.5, 6.5, "Location B"),
                rating = 4.8),
            WorkerProfile(
                uid = "worker_3",
                fieldOfWork = "Plumbing",
                description = "Affordable plumbing services for all your needs.",
                displayName = "Bob Johnson",
                tags = listOf("Affordable", "Quick"),
                includedServices = listOf(IncludedService(name = "Basic Consultation")),
                addOnServices = listOf(AddOnService(name = "Express Delivery")),
                reviews =
                    ArrayDeque(
                        listOf(
                            Review(
                                username = "User3",
                                review = "Quick and efficient.",
                                rating = 4.2))),
                price = 25.0,
                location = Location(47.0, 7.0, "Location C"),
                rating = 4.2))

    // Mock repository behavior to call onSuccess with the workers list
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<(List<WorkerProfile>) -> Unit>(0)
          onSuccess(workers)
          null
        }
        .`when`(mockRepository)
        .getProfiles(any(), any())

    // Act: Call ViewModel's searchEngine function with a query that matches multiple fields
    val query = "plumbing express delivery"
    viewModel.searchEngine(query)

    // Advance coroutine until idle to ensure callbacks are executed
    testDispatcher.scheduler.advanceUntilIdle()

    // Assert: Only worker_1 and worker_3 match "plumbing" and have "express delivery" as an add-on
    // service
    val expectedFiltered = listOf(workers[0], workers[2])

    assertEquals(expectedFiltered, viewModel.workerProfilesSuggestions.value)
  }
}
