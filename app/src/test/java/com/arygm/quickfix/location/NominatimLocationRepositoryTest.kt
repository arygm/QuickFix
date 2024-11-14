package com.arygm.quickfix.location

import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.locations.NominatimLocationRepository
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.IOException
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

class NominatimLocationRepositoryTest {

    private lateinit var client: OkHttpClient
    private lateinit var repository: NominatimLocationRepository

    @Before
    fun setUp() {
        // Créer un mock pour OkHttpClient
        client = mock(OkHttpClient::class.java)
        repository = NominatimLocationRepository(client)
    }

    @Test
    fun `test search returns locations on success`() {
        // Prepare a mock JSON response body
        val mockResponseBody = """
        [
            {
                "lat": "48.8588443",
                "lon": "2.2943506",
                "display_name": "Eiffel Tower, Paris, France"
            }
        ]
    """.trimIndent().toResponseBody("application/json".toMediaTypeOrNull())

        // Create a mock Response with the mockResponseBody
        val mockResponse = Response.Builder()
            .code(200)
            .message("OK")
            .protocol(Protocol.HTTP_1_1)
            .request(
                Request.Builder()
                    .url("https://nominatim.openstreetmap.org/search?q=Eiffel%20Tower&format=json")
                    .build()
            )
            .body(mockResponseBody)
            .build()

        // Create a mock Call
        val mockCall: Call = mock()

        // Mock OkHttpClient's newCall to return our mockCall
        whenever(client.newCall(any())).thenReturn(mockCall)

        // Simulate a successful response on enqueue
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback>(0)
            callback.onResponse(mockCall, mockResponse)
            null
        }.whenever(mockCall).enqueue(any())

        // Call the `search` method and check the results
        var result: List<Location>? = null
        repository.search(
            "Eiffel Tower",
            onSuccess = { locations ->
                result = locations
            },
            onFailure = {
                fail("Should not fail in this test case.")
            }
        )

        // If needed, wait briefly to ensure the asynchronous callback has time to execute
        Thread.sleep(100)

        // Verify the results
        assertNotNull(result)
        assertEquals(1, result?.size)
        val location = result?.get(0)
        assertEquals("Eiffel Tower, Paris, France", location?.name)
        location?.latitude?.let { assertEquals(48.8588443, it, 0.0001) }
        location?.longitude?.let { assertEquals(2.2943506, it, 0.0001) }
    }

    @Test
    fun `test search handles HTTP failure`() {
        // Simuler une réponse avec un code 500 (erreur serveur)
        val mockResponse =
            Response.Builder()
                .code(500)
                .message("Internal Server Error")
                .protocol(Protocol.HTTP_1_1)
                .request(
                    Request.Builder()
                        .url("https://nominatim.openstreetmap.org/search?q=Eiffel%20Tower&format=json")
                        .build())
                .build()

        val call = mock(Call::class.java)
        `when`(client.newCall(any())).thenReturn(call)

        doAnswer { invocation ->
            val callback = invocation.getArgument(0, Callback::class.java)
            callback.onResponse(call, mockResponse)
            null
        }
            .`when`(call)
            .enqueue(any())

        // Appeler la méthode `search` et vérifier qu'elle gère correctement l'échec
        var exception: Exception? = null
        repository.search(
            "Eiffel Tower",
            onSuccess = { assert(false) }, // Ne devrait pas réussir dans ce cas
            onFailure = { e -> exception = e })

        // Vérifier que l'exception est capturée
        assertTrue(exception is Exception)
        assertEquals("Request failed: Internal Server Error", exception?.message)
    }

    @Test
    fun `test search handles network failure`() {
        // Simuler une défaillance réseau avec une IOException
        val call = mock(Call::class.java)
        `when`(client.newCall(any())).thenReturn(call)

        doAnswer { invocation ->
            val callback = invocation.getArgument(0, Callback::class.java)
            callback.onFailure(call, IOException("Network error"))
            null
        }
            .`when`(call)
            .enqueue(any())

        // Appeler la méthode `search` et vérifier qu'elle gère correctement la défaillance
        var exception: Exception? = null
        repository.search(
            "Eiffel Tower",
            onSuccess = { assert(false) }, // Ne devrait pas réussir dans ce cas
            onFailure = { e -> exception = e })

        // Vérifier que l'exception est de type IOException
        assertTrue(exception is IOException)
        assertEquals("Network error", exception?.message)
    }
}