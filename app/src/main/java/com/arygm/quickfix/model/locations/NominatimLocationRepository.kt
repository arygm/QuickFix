package com.arygm.quickfix.model.locations

import java.io.IOException
import okhttp3.*
import org.json.JSONArray

class NominatimLocationRepository(val client: OkHttpClient) : LocationRepository {
    override fun search(
        query: String,
        onSuccess: (List<Location>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val url = "https://nominatim.openstreetmap.org/search?q=${query}&format=json"

        val request = Request.Builder().url(url).build()

        client
            .newCall(request)
            .enqueue(
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        onFailure(e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        if (response.isSuccessful) {
                            response.body?.let { responseBody ->
                                val jsonResponse = responseBody.string()
                                try {
                                    val locations = parseLocationJson(jsonResponse)
                                    onSuccess(locations)
                                } catch (e: Exception) {
                                    onFailure(e)
                                }
                            } ?: run { onFailure(IOException("Empty response body")) }
                        } else {
                            onFailure(IOException("Error response: ${response.code}"))
                        }
                    }
                })
    }

    private fun parseLocationJson(jsonResponse: String): List<Location> {
        val jsonArray = JSONArray(jsonResponse)
        val locations = mutableListOf<Location>()

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val name = jsonObject.optString("display_name", "Unknown")
            val lat = jsonObject.optDouble("lat", 0.0)
            val lon = jsonObject.optDouble("lon", 0.0)

            locations.add(Location(lat, lon, name))
        }

        return locations
    }
}
