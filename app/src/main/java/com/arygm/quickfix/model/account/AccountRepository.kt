package com.arygm.quickfix.model.account

interface AccountRepository {

  fun init(onSuccess: () -> Unit)

  fun getAccounts(onSuccess: (List<Account>) -> Unit, onFailure: (Exception) -> Unit)

  fun addAccount(account: Account, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  fun updateAccount(account: Account, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  fun deleteAccountById(id: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  fun accountExists(
      email: String,
      onSuccess: (Pair<Boolean, Account?>) -> Unit,
      onFailure: (Exception) -> Unit
  )

  fun getAccountById(uid: String, onSuccess: (Account?) -> Unit, onFailure: (Exception) -> Unit)
}
