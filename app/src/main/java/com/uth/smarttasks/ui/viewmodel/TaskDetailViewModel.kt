package com.uth.smarttasks.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uth.smarttasks.data.model.Task
import com.uth.smarttasks.data.repository.TaskRepository
import kotlinx.coroutines.launch

data class TaskDetailUiState(
    val isLoading: Boolean = false,
    val task: Task? = null,
    val error: String? = null,
    val deletionSuccess: Boolean = false
)

// Constructor đã nhận Repository
class TaskDetailViewModel(private val repository: TaskRepository) : ViewModel() {
    private val _uiState = mutableStateOf(TaskDetailUiState())
    val uiState: State<TaskDetailUiState> = _uiState

    fun loadTaskDetail(taskId: String) {
        _uiState.value = TaskDetailUiState(isLoading = true)
        viewModelScope.launch {
            // Logic đã trỏ về Repository
            val task = repository.getTaskById(taskId)
            if (task != null) {
                _uiState.value = TaskDetailUiState(task = task)
            } else {
                _uiState.value = TaskDetailUiState(error = "Task not found")
            }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            // Logic đã trỏ về Repository
            repository.deleteTask(taskId)
            _uiState.value = _uiState.value.copy(deletionSuccess = true)
        }
    }
}