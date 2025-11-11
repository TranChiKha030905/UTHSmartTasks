package com.uth.smarttasks.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Khởi tạo DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ThemeDataStore(context: Context) {

    private val dataStore = context.dataStore

    // Khóa (key) để lưu chủ đề
    private object PreferencesKeys {
        val THEME_KEY = stringPreferencesKey("app_theme")
    }

    // Luồng (Flow) để đọc chủ đề. Mặc định là "Light"
    val themeFlow: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.THEME_KEY] ?: "Light"
        }

    // Hàm để LƯU chủ đề
    suspend fun saveTheme(themeName: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_KEY] = themeName
        }
    }
}