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

    val request =
        Request.Builder()
            .url(url)
            .header(
                "User-Agent", "QuickFix/1.0 (daferyassine52@gmail.com)") // Set a proper User-Agent
            .header("Referer", "https://quickfix.com") // Optionally add a Referer
            .build()
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
                      val locations = parseBody(jsonResponse)
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

  fun parseBody(body: String): List<Location> {
    val jsonArray = JSONArray(body)

    return List(jsonArray.length()) { i ->
      val jsonObject = jsonArray.getJSONObject(i)

      val lat = jsonObject.getDouble("lat")
      val lon = jsonObject.getDouble("lon")
      val name = jsonObject.getString("display_name")

      Location(lat, lon, name)
    }
  }
}
