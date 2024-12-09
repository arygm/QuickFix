package com.arygm.quickfix.model.profile

import android.graphics.Bitmap
import android.util.Log
import com.arygm.quickfix.model.locations.Location
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class UserProfileRepositoryFirestore(
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) : ProfileRepository {

  private val collectionPath = "users"
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
    Log.d("UserProfileRepositoryFirestore", "getProfiles")
    db.collection(collectionPath).get().addOnCompleteListener { task ->
      if (task.isSuccessful) {
        val profiles =
            task.result?.documents?.mapNotNull { document -> documentToUser(document) }
                ?: emptyList()
        onSuccess(profiles)
      } else {
        task.exception?.let { e ->
          Log.e("UserProfileRepositoryFirestore", "Error getting documents", e)
          onFailure(e)
        }
      }
    }
  }

  override fun addProfile(profile: Profile, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    performFirestoreOperation(
        db.collection(collectionPath).document(profile.uid).set(profile), onSuccess, onFailure)
  }

  override fun updateProfile(
      profile: Profile,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    performFirestoreOperation(
        db.collection(collectionPath).document(profile.uid).set(profile), onSuccess, onFailure)
  }

  override fun deleteProfileById(
      id: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    performFirestoreOperation(
        db.collection(collectionPath).document(id).delete(), onSuccess, onFailure)
  }

  override fun uploadProfileImages(
      accountId: String,
      images: List<Bitmap>,
      onSuccess: (List<String>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val workerFolderRef = storageRef.child("profiles").child(accountId).child("user")
    val uploadedImageUrls = mutableListOf<String>()
    var uploadCount = 0

    images.forEach { bitmap ->
      val fileRef = workerFolderRef.child("image_${System.currentTimeMillis()}.jpg")

      val baos = ByteArrayOutputStream()
      bitmap.compress(Bitmap.CompressFormat.JPEG, compressionQuality, baos) // Compress the image
      val byteArray = baos.toByteArray()

      fileRef
          .putBytes(byteArray)
          .addOnSuccessListener {
            fileRef.downloadUrl
                .addOnSuccessListener { uri ->
                  uploadedImageUrls.add(uri.toString())
                  uploadCount++
                  if (uploadCount == images.size) {
                    onSuccess(uploadedImageUrls)
                  }
                }
                .addOnFailureListener { exception -> onFailure(exception) }
          }
          .addOnFailureListener { exception -> onFailure(exception) }
    }
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
          Log.e("UserProfileRepositoryFirestore", "Error performing Firestore operation", e)
          onFailure(e)
        }
      }
    }
  }

  private fun documentToUser(document: DocumentSnapshot): UserProfile? {
    return try {
      val uid = document.id
      val locationsData = document.get("locations") as? List<Map<String, Any>> ?: emptyList()
      val locations =
          locationsData.map { map ->
            Location(
                latitude = map["latitude"] as? Double ?: 0.0,
                longitude = map["longitude"] as? Double ?: 0.0,
                name = map["name"] as? String ?: "")
          }
      val announcements = document.get("announcements") as? List<String> ?: emptyList()
      UserProfile(uid = uid, locations = locations, announcements = announcements)
    } catch (e: Exception) {
      Log.e("UserProfileRepositoryFirestore", "Error converting document to UserProfile", e)
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
            val profile = documentToUser(document)
            onSuccess(profile)
          } else {
            onSuccess(null)
          }
        }
        .addOnFailureListener { exception ->
          Log.e("UserProfileRepositoryFirestore", "Error fetching profile", exception)
          onFailure(exception)
        }
  }
}
