package com.uth.smarttasks.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uth.smarttasks.data.model.Task
import com.uth.smarttasks.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class ProjectUiState(
    val isLoading: Boolean = false,
    val projects: Map<String, List<Task>> = emptyMap(),
    val error: String? = null
)

// Sửa Constructor
class ProjectViewModel(private val repository: TaskRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(ProjectUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadProjects()
    }

    fun loadProjects() {
        _uiState.value = ProjectUiState(isLoading = true)
        viewModelScope.launch {
            // Sửa logic: Lắng nghe từ Repository
            repository.getTasks().collectLatest { tasks ->
                val groupedProjects = tasks
                    .filter { it.category.isNotBlank() }
                    .groupBy { it.category }

                _uiState.value = ProjectUiState(projects = groupedProjects)
            }
        }
    }
}