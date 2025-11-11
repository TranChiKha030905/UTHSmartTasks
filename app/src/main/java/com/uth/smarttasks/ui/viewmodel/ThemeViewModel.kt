package com.uth.smarttasks.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uth.smarttasks.data.local.ThemeDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeViewModel(private val dataStore: ThemeDataStore) : ViewModel() {

    // Đọc theme từ DataStore và biến nó thành StateFlow
    val theme: StateFlow<String> = dataStore.themeFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "Light" // Giá trị khởi tạo
        )

    // Hàm để GHI theme (gọi từ UI)
    fun setTheme(themeName: String) {
        viewModelScope.launch {
            dataStore.saveTheme(themeName)
        }
    }
}