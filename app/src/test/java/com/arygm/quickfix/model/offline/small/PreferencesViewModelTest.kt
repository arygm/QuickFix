package com.arygm.quickfix.model.offline.small

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*
import org.junit.rules.TemporaryFolder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule

@OptIn(ExperimentalCoroutinesApi::class)
class PreferencesViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private lateinit var testDataStore: DataStore<Preferences>
    private lateinit var repository: PreferencesRepositoryDataStore
    private lateinit var viewModel: PreferencesViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        initializeViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadPreference invokes onLoaded with correct value`() = runTest {
        val mockKey = stringPreferencesKey("test_key")
        val mockValue = "test_value"

        // Set the value in the DataStore
        testDataStore.edit { preferences ->
            preferences[mockKey] = mockValue
        }

        var loadedValue: String? = null

        // Call the method under test
        viewModel.loadPreference(mockKey) { value ->
            loadedValue = value
        }

        // Since using UnconfinedTestDispatcher, no need to advance time
        // Assert that the loaded value is correct
        assertEquals(mockValue, loadedValue)
    }

    // Other tests...

    private fun initializeViewModel() {
        testDataStore = PreferenceDataStoreFactory.create(
            scope = CoroutineScope(testDispatcher),
            produceFile = { temporaryFolder.newFile("test.preferences_pb") }
        )
        repository = PreferencesRepositoryDataStore(testDataStore)
        viewModel = PreferencesViewModel(repository)
    }


    @Test
    fun `savePreference saves the value correctly`() = runTest {
        val mockKey = stringPreferencesKey("test_key")
        val mockValue = "test_value"

        // Call the method under test
        viewModel.savePreference(mockKey, mockValue)

        // Advance the dispatcher to process pending coroutines
        advanceUntilIdle()

        // Read the value from the DataStore directly
        val result = testDataStore.data.first()[mockKey]

        assertEquals(mockValue, result)
    }

    @Test
    fun `clearAllPreferences clears all preferences`() = runTest {
        // Set some values in the DataStore
        testDataStore.edit { preferences ->
            preferences[stringPreferencesKey("key1")] = "value1"
            preferences[intPreferencesKey("key2")] = 42
        }

        // Ensure all coroutines have completed
        advanceUntilIdle()

        // Call the method under test
        viewModel.clearAllPreferences()

        // Advance the dispatcher to ensure clear operation completes
        advanceUntilIdle()

        // Verify that preferences are empty
        val preferences = testDataStore.data.first()
        assertTrue(preferences.asMap().isEmpty())
    }

}
