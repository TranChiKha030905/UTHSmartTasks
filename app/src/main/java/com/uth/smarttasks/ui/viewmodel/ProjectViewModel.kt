package com.uth.smarttasks.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uth.smarttasks.data.model.Task
import com.uth.smarttasks.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// State cho UI Projects
data class ProjectUiState(
    val isLoading: Boolean = false,
    // Dùng Map để nhóm: Key là "Category", Value là List<Task>
    val projects: Map<String, List<Task>> = emptyMap(),
    val error: String? = null
)

class ProjectViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProjectUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadProjects()
    }

    fun loadProjects() {
        _uiState.value = ProjectUiState(isLoading = true)
        viewModelScope.launch {
            try {
                // 1. Gọi API y hệt
                val response = RetrofitClient.apiService.getTasks()
                if (response.isSuccess) {
                    // 2. Tự nhóm (group)
                    val tasks = response.data
                    val groupedProjects = tasks
                        .filter { it.category.isNotBlank() } // Bỏ qua task không có category
                        .groupBy { it.category } // Nhóm theo "Work", "Personal" ...

                    _uiState.value = ProjectUiState(projects = groupedProjects)
                } else {
                    _uiState.value = ProjectUiState(error = response.message)
                }
            } catch (e: Exception) {
                _uiState.value = ProjectUiState(error = e.message ?: "Unknown error")
            }
        }
    }
}