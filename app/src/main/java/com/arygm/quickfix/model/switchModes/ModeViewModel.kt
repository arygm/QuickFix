package com.arygm.quickfix.model.switchModes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.arygm.quickfix.model.search.AnnouncementRepositoryFirestore
import com.arygm.quickfix.model.search.AnnouncementViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class AppMode {
    USER, WORKER
}

class ModeViewModel : ViewModel() {
    private val _currentMode = MutableStateFlow(AppMode.USER)
    val currentMode: StateFlow<AppMode> = _currentMode

    companion object {
        val Factory: ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ModeViewModel()
                            as T
                }
            }
    }

    fun switchMode(mode: AppMode) {
        _currentMode.value = mode
    }
}