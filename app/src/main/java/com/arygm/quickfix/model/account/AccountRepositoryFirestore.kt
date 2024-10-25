package com.arygm.quickfix.model.account

import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.account.AccountRepository
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class AccountRepositoryFirestore(private val db: FirebaseFirestore) : AccountRepository {

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
                    onSuccess(Pair(false, null))
                }
            }
            .addOnFailureListener { exception ->
                Log.e("AccountRepositoryFirestore", "Error checking if account exists", exception)
                onFailure(exception)
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
                    Log.e("AccountRepositoryFirestore", "Error performing Firestore operation", e)
                    onFailure(e)
                }
            }
        }
    }

    private fun documentToAccount(document: DocumentSnapshot): Account? {
        return try {
            val uid = document.id
            val firstName = document.getString("firstName") ?: return null
            val lastName = document.getString("lastName") ?: return null
            val email = document.getString("email") ?: return null
            val birthDate = document.getTimestamp("birthDate") ?: return null

            Account(
                uid = uid,
                firstName = firstName,
                lastName = lastName,
                email = email,
                birthDate = birthDate)
        } catch (e: Exception) {
            Log.e("TodosRepositoryFirestore", "Error converting document to ToDo", e)
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
