package com.arygm.quickfix.model.quickfix

import android.util.Log
import com.arygm.quickfix.model.bill.BillField
import com.arygm.quickfix.model.bill.Units
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.profile.dataFields.AddOnService
import com.arygm.quickfix.model.profile.dataFields.IncludedService
import com.arygm.quickfix.utils.performFirestoreOperation
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

open class QuickFixRepositoryFirestore(private val db: FirebaseFirestore) : QuickFixRepository {

  val collectionPath = "quickFix"

  override fun getRandomUid(): String {
    return db.collection(collectionPath).document().id
  }

  override fun init(onSuccess: () -> Unit) {
    Firebase.auth.addAuthStateListener {
      if (it.currentUser != null) {
        onSuccess()
      }
    }
  }

  override fun getQuickFixes(onSuccess: (List<QuickFix>) -> Unit, onFailure: (Exception) -> Unit) {
    db.collection(collectionPath).get().addOnCompleteListener { task ->
      if (task.isSuccessful) {
        val accounts =
            task.result?.documents?.mapNotNull { document -> documentToQuickFix(document) }
                ?: emptyList()
        onSuccess(accounts)
      } else {
        task.exception?.let { e ->
          Log.e("QuickFixRepositoryFirestore", "Error getting documents", e)
          onFailure(e)
        }
      }
    }
  }

  override fun addQuickFix(
      quickFix: QuickFix,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    performFirestoreOperation(
        db.collection(collectionPath).document(quickFix.uid).set(quickFix), onSuccess, onFailure)
  }

  override fun updateQuickFix(
      quickFix: QuickFix,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    performFirestoreOperation(
        db.collection(collectionPath).document(quickFix.uid).set(quickFix), onSuccess, onFailure)
  }

  override fun deleteQuickFixById(
      id: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    performFirestoreOperation(
        db.collection(collectionPath).document(id).delete(), onSuccess, onFailure)
  }

  override fun getQuickFixById(
      uid: String,
      onSuccess: (QuickFix?) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    db.collection(collectionPath)
        .document(uid)
        .get()
        .addOnSuccessListener { document ->
          if (document.exists()) {
            val account = documentToQuickFix(document)
            onSuccess(account)
          } else {
            onSuccess(null)
          }
        }
        .addOnFailureListener { exception ->
          Log.e("QuickFixRepositoryFirestore", "Error fetching QuickFix", exception)
          onFailure(exception)
        }
  }

  private fun documentToQuickFix(document: DocumentSnapshot): QuickFix? {
    try {
      val uid = document.getString("uid") ?: document.id

      val statusString = document.getString("status") ?: return null
      val status =
          try {
            Status.valueOf(statusString)
          } catch (e: IllegalArgumentException) {
            Log.e("QuickFixRepositoryFirestore", "Unknown status: $statusString", e)
            return null
          }

      val imageUrlAny = document.get("imageUrl") as? List<*>
      val imageUrl = imageUrlAny?.mapNotNull { it as? String } ?: emptyList()

      val dateAnyList = document.get("date") as? List<*>
      val dateList =
          dateAnyList?.mapNotNull {
            when (it) {
              is Timestamp -> it
              is Map<*, *> -> {
                val seconds = (it["seconds"] as? Number)?.toLong()
                val nanoseconds = (it["nanoseconds"] as? Number)?.toInt() ?: 0
                if (seconds != null) Timestamp(seconds, nanoseconds) else null
              }
              else -> null
            }
          } ?: emptyList()

      val time =
          document.getTimestamp("time")
              ?: run {
                val timeMap = document.get("time") as? Map<*, *>
                val seconds = (timeMap?.get("seconds") as? Number)?.toLong()
                val nanoseconds = (timeMap?.get("nanoseconds") as? Number)?.toInt() ?: 0
                if (seconds != null) Timestamp(seconds, nanoseconds) else null
              }
              ?: return null

      val includedServicesAny = document.get("includedServices") as? List<*>
      val includedServices =
          includedServicesAny?.mapNotNull {
            when (it) {
              is String -> IncludedService(it)
              is Map<*, *> -> {
                val name = it["name"] as? String
                name?.let { IncludedService(it) }
              }
              else -> null
            }
          } ?: emptyList()

      val addOnServicesAny = document.get("addOnServices") as? List<*>
      val addOnServices =
          addOnServicesAny?.mapNotNull {
            when (it) {
              is String -> AddOnService(it)
              is Map<*, *> -> {
                val name = it["name"] as? String
                name?.let { AddOnService(it) }
              }
              else -> null
            }
          } ?: emptyList()

      val workerId = document.getString("workerId") ?: ""
      val userId = document.getString("userId") ?: ""
      val chatUid = document.getString("chatUid") ?: ""
      val title = document.getString("title") ?: ""
      val description = document.getString("description") ?: ""

      val billAnyList = document.get("bill") as? List<*>
      val bill =
          billAnyList?.mapNotNull {
            val map = it as? Map<*, *> ?: return@mapNotNull null
            val billDescription = map["description"] as? String ?: return@mapNotNull null
            val unitString = map["unit"] as? String ?: return@mapNotNull null
            val unit =
                try {
                  Units.valueOf(unitString)
                } catch (e: IllegalArgumentException) {
                  Log.e("QuickFixRepositoryFirestore", "Unknown unit: $unitString", e)
                  return@mapNotNull null
                }
            val amount = (map["amount"] as? Number)?.toDouble() ?: return@mapNotNull null
            val unitPrice = (map["unitPrice"] as? Number)?.toDouble() ?: return@mapNotNull null
            val total = (map["total"] as? Number)?.toDouble() ?: return@mapNotNull null
            BillField(billDescription, unit, amount, unitPrice, total)
          } ?: emptyList()

      val locationMap = document.get("location") as? Map<*, *>
      val location =
          locationMap?.let {
            val latitude = (it["latitude"] as? Number)?.toDouble() ?: 0.0
            val longitude = (it["longitude"] as? Number)?.toDouble() ?: 0.0
            val name = it["name"] as? String ?: ""
            Location(latitude, longitude, name)
          } ?: Location()

      return QuickFix(
          uid = uid,
          status = status,
          imageUrl = imageUrl,
          date = dateList,
          time = time,
          includedServices = includedServices,
          addOnServices = addOnServices,
          workerId = workerId,
          userId = userId,
          chatUid = chatUid,
          title = title,
          description = description,
          bill = bill,
          location = location)
    } catch (e: Exception) {
      Log.e("QuickFixRepositoryFirestore", "Error converting document to QuickFix", e)
      return null
    }
  }
}
