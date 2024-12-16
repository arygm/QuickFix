package com.arygm.quickfix.location

import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.locations.NominatimLocationRepository
import java.io.IOException
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*

class NominatimLocationRepositoryTest {

  @Mock private lateinit var mockOkHttpClient: OkHttpClient

  @Mock private lateinit var mockCall: Call

  private lateinit var repository: NominatimLocationRepository

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    repository = NominatimLocationRepository(mockOkHttpClient)
  }

  @Test
  fun `search should call onSuccess when response is successful`() {
    val query = "Lausanne"
    val expectedUrl = "https://nominatim.openstreetmap.org/search?q=$query&format=json"

    // Mock the OkHttp client to return our mock call
    whenever(mockOkHttpClient.newCall(any())).thenReturn(mockCall)

    // Prepare a successful response
    val mockResponseBody =
        """[{"display_name": "Lausanne, Switzerland", "lat": "46.5191", "lon": "6.6323"}]"""
            .toResponseBody("application/json".toMediaTypeOrNull())
    val mockResponse =
        Response.Builder()
            .code(200)
            .protocol(Protocol.HTTP_1_1)
            .request(Request.Builder().url(expectedUrl).build())
            .message("OK")
            .body(mockResponseBody)
            .build()

    // Simulate the enqueue callback
    doAnswer { invocation ->
          val callback = invocation.getArgument<Callback>(0)
          callback.onResponse(mockCall, mockResponse)
        }
        .whenever(mockCall)
        .enqueue(any())

    // Call the method under test
    var locationsResult: List<Location>? = null
    repository.search(
        query,
        onSuccess = { locations -> locationsResult = locations },
        onFailure = {
          // Should not reach here in this test
        })

    // Verify that the correct URL was requested
    argumentCaptor<Request>().apply {
      verify(mockOkHttpClient).newCall(capture())
      assert(firstValue.url.toString() == expectedUrl)
    }

    // Assert that onSuccess was called with the correct data
    assert(locationsResult != null)
    assert(locationsResult!!.size == 1)
    val location = locationsResult!![0]
    assert(location.name == "Lausanne, Switzerland")
    assert(location.latitude == 46.5191)
    assert(location.longitude == 6.6323)
  }

  @Test
  fun `search should call onFailure when request fails`() {
    val query = "Lausanne"
    val expectedUrl = "https://nominatim.openstreetmap.org/search?q=$query&format=json"

    // Mock the OkHttp client to return our mock call
    whenever(mockOkHttpClient.newCall(any())).thenReturn(mockCall)

    // Simulate a network failure
    doAnswer { invocation ->
          val callback = invocation.getArgument<Callback>(0)
          callback.onFailure(mockCall, IOException("Network error"))
        }
        .whenever(mockCall)
        .enqueue(any())

    // Call the method under test
    var exceptionResult: Exception? = null
    repository.search(
        query,
        onSuccess = {
          // Should not reach here in this test
        },
        onFailure = { exception -> exceptionResult = exception })

    // Verify that the correct URL was requested
    argumentCaptor<Request>().apply {
      verify(mockOkHttpClient).newCall(capture())
      assert(firstValue.url.toString() == expectedUrl)
    }

    // Assert that onFailure was called with the correct exception
    assert(exceptionResult is IOException)
    assert(exceptionResult?.message == "Network error")
  }

  @Test
  fun `search should call onSuccess with empty list when no locations are found`() {
    val query = "NonexistentPlace"
    val expectedUrl = "https://nominatim.openstreetmap.org/search?q=$query&format=json"

    // Mock the OkHttp client to return our mock call
    whenever(mockOkHttpClient.newCall(any())).thenReturn(mockCall)

    // Prepare a successful response with empty JSON array
    val mockResponseBody = """[]""".toResponseBody("application/json".toMediaTypeOrNull())
    val mockResponse =
        Response.Builder()
            .code(200)
            .protocol(Protocol.HTTP_1_1)
            .request(Request.Builder().url(expectedUrl).build())
            .message("OK")
            .body(mockResponseBody)
            .build()

    // Simulate the enqueue callback
    doAnswer { invocation ->
          val callback = invocation.getArgument<Callback>(0)
          callback.onResponse(mockCall, mockResponse)
        }
        .whenever(mockCall)
        .enqueue(any())

    // Call the method under test
    var locationsResult: List<Location>? = null
    repository.search(
        query,
        onSuccess = { locations -> locationsResult = locations },
        onFailure = {
          // Should not reach here in this test
        })

    // Verify that the correct URL was requested
    argumentCaptor<Request>().apply {
      verify(mockOkHttpClient).newCall(capture())
      assert(firstValue.url.toString() == expectedUrl)
    }

    // Assert that onSuccess was called with an empty list
    assert(locationsResult != null)
    assert(locationsResult!!.isEmpty())
  }
}
