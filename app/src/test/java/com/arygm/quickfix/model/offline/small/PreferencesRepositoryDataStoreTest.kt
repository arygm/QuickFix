package com.arygm.quickfix.model.offline.small

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class PreferencesRepositoryDataStoreTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private lateinit var testDataStore: DataStore<Preferences>
    private lateinit var repository: PreferencesRepositoryDataStore

    @Test
    fun `getPreferenceByKey returns correct value`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        initializeDataStore(testDispatcher)

        val mockKey = stringPreferencesKey("test_key")
        val mockValue = "test_value"

        // Set the value in the DataStore
        testDataStore.edit { preferences ->
            preferences[mockKey] = mockValue
        }

        // Advance the dispatcher to process pending coroutines
        testDispatcher.scheduler.advanceUntilIdle()

        val result = repository.getPreferenceByKey(mockKey).first()

        assertEquals(mockValue, result)
    }


    private fun initializeDataStore(testDispatcher: TestDispatcher) {
        testDataStore = PreferenceDataStoreFactory.create(
            scope = CoroutineScope(testDispatcher),
            produceFile = { temporaryFolder.newFile("test.preferences_pb") }
        )
        repository = PreferencesRepositoryDataStore(testDataStore)
    }


    @Test
    fun `setPreferenceByKey sets correct value`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        initializeDataStore(testDispatcher)

        val mockKey = stringPreferencesKey("test_key")
        val mockValue = "test_value"

        // Call the method under test
        repository.setPreferenceByKey(mockKey, mockValue)

        testDispatcher.scheduler.advanceUntilIdle()

        // Read the value from the DataStore directly
        val result = testDataStore.data.first()[mockKey]

        assertEquals(mockValue, result)
    }

    @Test
    fun `clearPreferences clears all values`() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        initializeDataStore(testDispatcher)

        // Set some values
        testDataStore.edit { preferences ->
            preferences[stringPreferencesKey("key1")] = "value1"
            preferences[intPreferencesKey("key2")] = 42
        }

        // Clear preferences
        repository.clearPreferences()

        testDispatcher.scheduler.advanceUntilIdle()

        // Verify that preferences are empty
        val preferences = testDataStore.data.first()
        assertTrue(preferences.asMap().isEmpty())
    }

}
