package com.arygm.quickfix.model.account

import android.util.Log
import com.arygm.quickfix.utils.performFirestoreOperation
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

open class AccountRepositoryFirestore(private val db: FirebaseFirestore) : AccountRepository {

  private val collectionPath = "accounts"

  override fun init(onSuccess: () -> Unit) {
    Firebase.auth.addAuthStateListener {
      if (it.currentUser != null) {
        onSuccess()
      }
    }
  }

  override fun getAccounts(onSuccess: (List<Account>) -> Unit, onFailure: (Exception) -> Unit) {
    Log.d("AccountRepositoryFirestore", "getAccounts")
    db.collection(collectionPath).get().addOnCompleteListener { task ->
      if (task.isSuccessful) {
        val accounts =
            task.result?.documents?.mapNotNull { document -> documentToAccount(document) }
                ?: emptyList()
        onSuccess(accounts)
      } else {
        task.exception?.let { e ->
          Log.e("AccountRepositoryFirestore", "Error getting documents", e)
          onFailure(e)
        }
      }
    }
  }

  override fun addAccount(account: Account, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    performFirestoreOperation(
        db.collection(collectionPath).document(account.uid).set(account), onSuccess, onFailure)
  }

  override fun updateAccount(
      account: Account,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    performFirestoreOperation(
        db.collection(collectionPath).document(account.uid).set(account), onSuccess, onFailure)
  }

  override fun deleteAccountById(
      id: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    performFirestoreOperation(
        db.collection(collectionPath).document(id).delete(), onSuccess, onFailure)
  }

  override fun accountExists(
      email: String,
      onSuccess: (Pair<Boolean, Account?>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    db.collection(collectionPath)
        .whereEqualTo("email", email)
        .get()
        .addOnSuccessListener { querySnapshot ->
          if (!querySnapshot.isEmpty) {
            val document = querySnapshot.documents.first()
            val account = documentToAccount(document)
            onSuccess(Pair(true, account))
          } else {
            Log.d("AccountRepositoryFirestore", "Account does not exist: null")
            onSuccess(Pair(false, null))
          }
        }
        .addOnFailureListener { exception ->
          Log.e("AccountRepositoryFirestore", "Error checking if account exists", exception)
          onFailure(exception)
        }
  }

  private fun documentToAccount(document: DocumentSnapshot): Account? {
    return try {
      val uid = document.id
      val firstName = document.getString("firstName") ?: return null
      val lastName = document.getString("lastName") ?: return null
      Log.d("AccountRepositoryFirestore", "lastName: $lastName")
      val email = document.getString("email") ?: return null
      Log.d("AccountRepositoryFirestore", "email: $email")
      val birthDate = document.getTimestamp("birthDate") ?: return null
      Log.d("AccountRepositoryFirestore", "birthDate: $birthDate")
      val isWorker = document.getBoolean("worker") ?: return null
      Log.d("AccountRepositoryFirestore", "isWorker: $isWorker")

      val account =
          Account(
              uid = uid,
              firstName = firstName,
              lastName = lastName,
              email = email,
              birthDate = birthDate,
              isWorker = isWorker)
      Log.d("AccountRepositoryFirestore", "account: $account")
      account
    } catch (e: Exception) {
      Log.e("AccountRepositoryFirestore", "Error converting document to Account", e)
      null
    }
  }

  override fun getAccountById(
      uid: String,
      onSuccess: (Account?) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    db.collection(collectionPath)
        .document(uid)
        .get()
        .addOnSuccessListener { document ->
          if (document.exists()) {
            val account = documentToAccount(document)
            onSuccess(account)
          } else {
            onSuccess(null)
          }
        }
        .addOnFailureListener { exception ->
          Log.e("AccountRepositoryFirestore", "Error fetching account", exception)
          onFailure(exception)
        }
  }
}
