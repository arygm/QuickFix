package com.arygm.quickfix.location

import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.locations.LocationRepository
import com.arygm.quickfix.model.locations.LocationViewModel
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify

class LocationViewModelTest {
    private lateinit var locationRepository: LocationRepository
    private lateinit var locationViewModel: LocationViewModel

    private val query = "paris"

    @Before
    fun setUp() {
        locationRepository = mock(LocationRepository::class.java)
        locationViewModel = LocationViewModel(locationRepository)
    }

    @Test
    fun setQuery() {
        locationViewModel.setQuery(query)
        assertThat(locationViewModel.query.value, `is`(query))
    }

    @Test
    fun searchCallsRepository() {
        locationViewModel.setQuery(query)
        verify(locationRepository).search(eq(query), any(), any())
    }

    @Test
    fun assertRepoIsSet() {
        assertThat(locationViewModel.repository, `is`(locationRepository))
    }

    @Test
    fun assertTheListIsUpdated() {
        val mockResult =
            listOf(
                Location(48.8534951, 2.3483915, "Paris, Île-de-France, France métropolitaine, France"),
                Location(
                    48.8588897,
                    2.3200410217200766,
                    "Paris, Île-de-France, France métropolitaine, France"),
                Location(
                    48.8588897,
                    2.3200410217200766,
                    "Paris, Île-de-France, France métropolitaine, France")
            )
        locationViewModel.setQuery(query)

        verify(locationRepository).search(eq(query), any(), any())
        val successCallback = argumentCaptor<(List<Location>) -> Unit>()
        verify(locationRepository).search(eq(query), successCallback.capture(), any())

        successCallback.firstValue.invoke(mockResult)

        assertThat(locationViewModel.locationSuggestions.value, `is`(mockResult))
    }
}