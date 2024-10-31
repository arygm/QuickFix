package com.arygm.quickfix.model.account

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

open class AccountViewModel(private val repository: AccountRepository) : ViewModel() {

  private val accounts_ = MutableStateFlow<List<Account>>(emptyList())
  val accounts: StateFlow<List<Account>> = accounts_.asStateFlow()

  init {
    repository.init { getAccounts() }
  }

  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AccountViewModel(AccountRepositoryFirestore(Firebase.firestore)) as T
          }
        }
  }

  fun getAccounts() {
    repository.getAccounts(
        onSuccess = { accounts_.value = it },
        onFailure = { e -> Log.e("AccountViewModel", "Failed to fetch accounts: ${e.message}") })
  }

  fun addAccount(account: Account, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    repository.addAccount(
        account = account,
        onSuccess = {
          getAccounts()
          onSuccess()
        },
        onFailure = { e ->
          Log.e("AccountViewModel", "Failed to add account: ${e.message}")
          onFailure(e)
        })
  }

  fun updateAccount(account: Account, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    repository.updateAccount(
        account = account,
        onSuccess = {
          getAccounts()
          onSuccess()
          // fetchUserAccount(account.uid) { loggedInAccountViewModel.setLoggedInAccount(account) }
        },
        onFailure = { e ->
          Log.e("AccountViewModel", "Failed to update account: ${e.message}")
          onFailure(e)
        })
  }

  fun deleteAccountById(id: String) {
    repository.deleteAccountById(
        id = id,
        onSuccess = { getAccounts() },
        onFailure = { e -> Log.e("AccountViewModel", "Failed to delete account: ${e.message}") })
  }

  fun accountExists(email: String, onResult: (Boolean, Account?) -> Unit) {
    repository.accountExists(
        email,
        onSuccess = { (exists, account) ->
          if (exists) {
            Log.d("AccountCheck", "Account with this email exists.")
            onResult(true, account)
          } else {
            Log.d("AccountCheck", "No account found with this email.")
            onResult(false, null)
          }
        },
        onFailure = { exception ->
          Log.e("AccountCheck", "Error checking account existence", exception)
          onResult(false, null)
        })
  }

  fun fetchUserAccount(uid: String, onResult: (Account?) -> Unit) {
    repository.getAccountById(
        uid,
        onSuccess = { account ->
          if (account != null) {
            onResult(account)
          } else {
            Log.e("AccountViewModel", "No account found for user with UID: $uid")
            onResult(null)
          }
        },
        onFailure = { e ->
          Log.e("AccountViewModel", "Error fetching account: ${e.message}")
          onResult(null)
        })
  }
}
