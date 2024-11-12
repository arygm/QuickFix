package com.arygm.quickfix.utils

import android.util.Log
import com.google.android.gms.tasks.Task

fun performFirestoreOperation(
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
