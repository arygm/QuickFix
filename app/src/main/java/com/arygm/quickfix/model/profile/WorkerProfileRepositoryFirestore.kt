package com.arygm.quickfix.model.profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.util.Log
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.profile.dataFields.AddOnService
import com.arygm.quickfix.model.profile.dataFields.IncludedService
import com.arygm.quickfix.model.profile.dataFields.Review
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.LocalTime
import kotlin.math.cos

open class WorkerProfileRepositoryFirestore(
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) : ProfileRepository {

  private val collectionPath = "workers"
  private val storageRef = storage.reference
  private val compressionQuality = 50

  override fun init(onSuccess: () -> Unit) {
    Firebase.auth.addAuthStateListener {
      if (it.currentUser != null) {
        onSuccess()
      }
    }
  }

  override fun getProfiles(onSuccess: (List<Profile>) -> Unit, onFailure: (Exception) -> Unit) {
    Log.d("ProfileRepositoryFirestore", "getProfiles")
    db.collection(collectionPath).get().addOnCompleteListener { task ->
      if (task.isSuccessful) {
        val workerProfiles =
            task.result?.documents?.mapNotNull { document -> documentToWorker(document) }
                ?: emptyList<WorkerProfile>()
        onSuccess(workerProfiles)
      } else {
        task.exception?.let { e ->
          Log.e("WorkerProfileRepositoryFirestore", "Error getting documents", e)
          onFailure(e)
        }
      }
    }
  }

  override fun addProfile(profile: Profile, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val workerProfile = profile as WorkerProfile
    val data = workerProfile.toFirestoreMap()
    performFirestoreOperation(
        db.collection(collectionPath).document(profile.uid).set(data), onSuccess, onFailure)
  }

  override fun updateProfile(
      profile: Profile,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val workerProfile = profile as WorkerProfile
    val data = workerProfile.toFirestoreMap()

    performFirestoreOperation(
        db.collection(collectionPath).document(profile.uid).set(data), onSuccess, onFailure)
  }

  override fun deleteProfileById(
      id: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    performFirestoreOperation(
        db.collection(collectionPath).document(id).delete(), onSuccess, onFailure)
  }

  private fun performFirestoreOperation(
      task: Task<Void>,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    task.addOnCompleteListener { result ->
      if (result.isSuccessful) {
        onSuccess()
      } else {
        result.exception?.let { e ->
          Log.e("WorkerProfileRepositoryFirestore", "Error performing Firestore operation", e)
          onFailure(e)
        }
      }
    }
  }

  override fun uploadProfileImages(
      accountId: String,
      images: List<Bitmap>,
      onSuccess: (List<String>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val workerFolderRef = storageRef.child("profiles").child(accountId).child("worker")
    val uploadedImageUrls = mutableListOf<String>()
    var uploadCount = 0

    images.forEach { bitmap ->
      val fileRef = workerFolderRef.child("image_${System.currentTimeMillis()}.jpg")
      Log.d("WorkerProfileRepositoryFirestore", "Uploading to path: ${fileRef.path}")

      val baos = ByteArrayOutputStream()
      bitmap.compress(Bitmap.CompressFormat.JPEG, compressionQuality, baos) // Compress the image
      val byteArray = baos.toByteArray()

      fileRef
          .putBytes(byteArray)
          .addOnSuccessListener {
            Log.d(
                "WorkerProfileRepositoryFirestore", "Image uploaded successfully: ${fileRef.name}")
            fileRef.downloadUrl
                .addOnSuccessListener { uri ->
                  uploadedImageUrls.add(uri.toString())
                  uploadCount++
                  if (uploadCount == images.size) {
                    onSuccess(uploadedImageUrls)
                  }
                }
                .addOnFailureListener { exception ->
                  Log.e(
                      "WorkerProfileRepositoryFirestore",
                      "Failed to get download URL for ${fileRef.name}",
                      exception)
                  onFailure(exception)
                }
          }
          .addOnFailureListener { exception ->
            Log.e(
                "WorkerProfileRepositoryFirestore",
                "Failed to upload image: ${fileRef.name}",
                exception)
            onFailure(exception)
          }
    }
  }

  private fun documentToWorker(document: DocumentSnapshot): WorkerProfile? {
    return try {
      val uid = document.id
      val description = document.getString("description") ?: return null
      val fieldOfWork = document.getString("fieldOfWork") ?: return null
      val locationData = document.get("location") as? Map<String, Any> ?: return null
      val location =
          locationData.let {
            Location(
                latitude = it["latitude"] as? Double ?: 0.0,
                longitude = it["longitude"] as? Double ?: 0.0,
                name = it["name"] as? String ?: "")
          }
      val price = document.getDouble("price") ?: 0.0
      val displayName = document.getString("display_name") ?: ""
      val includedServicesData =
          document.get("included_services") as? List<Map<String, Any>> ?: emptyList()
      val includedServices =
          includedServicesData.mapNotNull { serviceMap ->
            try {
              IncludedService(
                  name = serviceMap["name"] as? String ?: "",
              )
            } catch (e: Exception) {
              Log.e("Firestore", "Error parsing included service: $serviceMap", e)
              null // Skip invalid entries
            }
          }
      val addOnServicesData =
          document.get("addOnServices") as? List<Map<String, Any>> ?: emptyList()
      val addOnServices =
          addOnServicesData.mapNotNull { serviceMap ->
            try {
              AddOnService(
                  name = serviceMap["name"] as? String ?: "",
              )
            } catch (e: Exception) {
              Log.e("Firestore", "Error parsing add-on service: $serviceMap", e)
              null // Skip invalid entries
            }
          }

      val workingHoursMap = document.get("workingHours") as? Map<String, String>
      val workingHours =
          if (workingHoursMap != null) {
            Pair(
                LocalTime.parse(workingHoursMap["start"] ?: LocalTime.now().toString()),
                LocalTime.parse(workingHoursMap["end"] ?: LocalTime.now().toString()))
          } else {
            Pair(LocalTime.now(), LocalTime.now()) // Default value
          }

      val unavailabilityListData =
          document.get("unavailability_list") as? List<String> ?: emptyList()
      val unavailabilityList =
          unavailabilityListData.mapNotNull { dateString ->
            try {
              LocalDate.parse(dateString) // Parses the String into LocalDate
            } catch (e: Exception) {
              null // If parsing fails, skip this entry
            }
          }

      val reviewsData =
          document.get("reviews") as? List<Map<String, Any>> // Ensure type is Map<String, Any>
          ?: emptyList() // Fallback to an empty list if `reviews` is null or not a list

      val reviews =
          reviewsData.mapNotNull { reviewMap ->
            try {
              Review(
                  username =
                      reviewMap["username"] as? String
                          ?: "", // Safely cast and fallback to an empty string
                  review =
                      reviewMap["review"] as? String
                          ?: "", // Safely cast and fallback to an empty string
                  rating = (reviewMap["rating"] as? Double) ?: 0.0 // Safely cast rating to Double
                  )
            } catch (e: Exception) {
              Log.e("Firestore", "Error parsing review: $reviewMap", e)
              null // Skip invalid entries
            }
          }
      val tags = document.get("tags") as? List<String> ?: emptyList()
      val profilePicture = document.getString("profileImageUrl") ?: ""
      val bannerPicture = document.getString("bannerImageUrl") ?: ""
      val quickFixes = document.get("quickFixes") as? List<String> ?: emptyList()

      WorkerProfile(
          uid = uid,
          price = price,
          description = description,
          fieldOfWork = fieldOfWork,
          location = location,
          unavailability_list = unavailabilityList,
          workingHours = workingHours,
          reviews = reviews.toCollection(ArrayDeque()),
          includedServices = includedServices,
          addOnServices = addOnServices,
          profilePicture = profilePicture,
          bannerPicture = bannerPicture,
          displayName = displayName,
          tags = tags,
          quickFixes = quickFixes)
    } catch (e: Exception) {
      Log.e("WorkerProfileRepositoryFirestore", "Error converting document to WorkerProfile", e)
      null
    }
  }

  override fun getProfileById(
      uid: String,
      onSuccess: (Profile?) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    db.collection(collectionPath)
        .document(uid)
        .get()
        .addOnSuccessListener { document ->
          if (document.exists()) {
            val profile = documentToWorker(document)
            onSuccess(profile)
          } else {
            onSuccess(null)
          }
        }
        .addOnFailureListener { exception ->
          Log.e("WorkerProfileRepositoryFirestore", "Error fetching profile", exception)
          onFailure(exception)
        }
  }

  fun filterWorkers(
      rating: Double?,
      reviews: List<String>?,
      price: Double?,
      fieldOfWork: String?,
      location: Location?,
      radiusInKm: Double?,
      onSuccess: (List<WorkerProfile>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    var query: Query = db.collection(collectionPath)

    rating?.let { query = query.whereEqualTo("rating", it) }
    reviews?.takeIf { it.isNotEmpty() }?.let { query = query.whereArrayContainsAny("reviews", it) }

    fieldOfWork?.let { query = query.whereEqualTo("fieldOfWork", it) }

    price?.let { query = query.whereLessThan("price", it) }

    if (location != null && radiusInKm != null) {
      val earthRadius = 6371.0
      val lat = location.latitude
      val lon = location.longitude
      val latDelta = radiusInKm / earthRadius
      val lonDelta = radiusInKm / (earthRadius * cos(Math.toRadians(lat)))

      val minLat = lat - Math.toDegrees(latDelta)
      val maxLat = lat + Math.toDegrees(latDelta)
      val minLon = lon - Math.toDegrees(lonDelta)
      val maxLon = lon + Math.toDegrees(lonDelta)

      // Add range filters for latitude and longitude
      query =
          query
              .whereGreaterThanOrEqualTo("location.latitude", minLat)
              .whereLessThanOrEqualTo("location.latitude", maxLat)
              .whereGreaterThanOrEqualTo("location.longitude", minLon)
              .whereLessThanOrEqualTo("location.longitude", maxLon)
    }
    query
        .get()
        .addOnSuccessListener { querySnapshot ->
          Log.d(
              "WorkerProfileRepositoryFirestore",
              "Successfully fetched worker profiles : ${querySnapshot.documents.size}")
          val workerProfiles = querySnapshot.documents.mapNotNull { documentToWorker(it) }
          onSuccess(workerProfiles)
        }
        .addOnFailureListener { exception -> onFailure(exception) }
  }

  private fun fetchProfileImageUrl(
      accountId: String,
      onSuccess: (String) -> Unit,
      onFailure: (Exception) -> Unit,
      documentId: String
  ) {
    val firestore = db
    val collection = firestore.collection(collectionPath)
    collection
        .document(accountId)
        .get()
        .addOnSuccessListener { document ->
          val imageUrl = document[documentId] as? String ?: ""
          onSuccess(imageUrl)
        }
        .addOnFailureListener { onFailure(it) }
  }

  override fun fetchProfileImageAsBitmap(
      accountId: String,
      onSuccess: (Bitmap) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    fetchProfileImageUrl(
        accountId,
        { url ->
          if (url.isEmpty()) {
            Log.d(
                "WorkerProfileRepositoryFirestore",
                "No profile image found for account ID: $accountId")
            val defaultProfileBitmap =
                createSolidColorBitmap(
                    width = 200, // Adjust the width in pixels
                    height = 200, // Adjust the height in pixels
                    color = 0xFF66001A.toInt())
            onSuccess(defaultProfileBitmap)
          } else {
            val imageRef = storage.getReferenceFromUrl(url)
            Log.d("WorkerProfileRepositoryFirestore", "Fetching profile image from URL: $url")
            imageRef
                .getBytes(Long.MAX_VALUE)
                .addOnSuccessListener { bytes ->
                  val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                  onSuccess(bitmap)
                }
                .addOnFailureListener {
                  Log.e("WorkerProfileRepositoryFirestore", "Failed to fetch profile image", it)
                  onFailure(it)
                }
          }
        },
        onFailure,
        "profileImageUrl")
  }

  override fun fetchBannerImageAsBitmap(
      accountId: String,
      onSuccess: (Bitmap) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    fetchProfileImageUrl(
        accountId,
        { url ->
          if (url.isEmpty()) {
            val defaultBannerBitmap =
                createSolidColorBitmap(
                    width = 800, // Adjust the width in pixels
                    height = 400, // Adjust the height in pixels
                    color = 0xFF66001A.toInt())
            onSuccess(defaultBannerBitmap)
          } else {
            val imageRef = storage.getReferenceFromUrl(url)
            imageRef
                .getBytes(Long.MAX_VALUE)
                .addOnSuccessListener { bytes ->
                  val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                  onSuccess(bitmap)
                }
                .addOnFailureListener { onFailure(it) }
          }
        },
        onFailure,
        "bannerImageUrl")
  }
}

fun createSolidColorBitmap(width: Int, height: Int, color: Int): Bitmap {
  val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
  val canvas = Canvas(bitmap)
  val paint = Paint().apply { this.color = color }
  canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
  return bitmap
}
