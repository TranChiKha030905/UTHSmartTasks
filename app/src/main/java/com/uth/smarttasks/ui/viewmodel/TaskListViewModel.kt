package com.uth.smarttasks.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uth.smarttasks.data.model.Task
import com.uth.smarttasks.data.repository.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// State mới, không có 'error'
data class TaskListUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = true // Mặc định là true
)

// Sửa Constructor
class TaskListViewModel(private val repository: TaskRepository) : ViewModel() {

    // Lấy Flow từ Repository và chuyển thành StateFlow
    val uiState: StateFlow<TaskListUiState> = repository.getTasks()
        .map { TaskListUiState(tasks = it, isLoading = false) } // Khi data về, isLoading = false
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TaskListUiState(isLoading = true) // Ban đầu là loading
        )

    // Hàm Refresh 🔄
    fun refreshTasks() {
        viewModelScope.launch {
            repository.refreshTasks() // Gọi "Bộ não"
        }
    }

    // Hàm Checkbox
    fun toggleTaskStatus(task: Task) {
        viewModelScope.launch {
            val newStatus = if (task.status.lowercase() == "completed") "in progress" else "completed"
            repository.updateTask(task.copy(status = newStatus)) // Gọi "Bộ não"
        }
    }
}