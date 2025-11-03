package com.uth.smarttasks.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uth.smarttasks.data.model.Task
import com.uth.smarttasks.data.network.RetrofitClient
import kotlinx.coroutines.launch

// Định nghĩa các trạng thái UI
data class TaskListUiState(
    val isLoading: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val error: String? = null
)

class TaskListViewModel : ViewModel() {
    private val _uiState = mutableStateOf(TaskListUiState())
    val uiState: State<TaskListUiState> = _uiState

    init {
        loadTasks()
    }

    // Tải lại task, ví dụ khi pull-to-refresh
    fun loadTasks() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getTasks()
                if (response.isSuccess) {
                    _uiState.value = TaskListUiState(tasks = response.data)
                } else {
                    _uiState.value = TaskListUiState(error = response.message)
                }
            } catch (e: Exception) {
                _uiState.value = TaskListUiState(error = e.message ?: "Unknown error")
            }
        }
    }

    // === CODE MỚI THÊM VÀO ===

    // Xử lý khi nhấn checkbox
    fun toggleTaskStatus(task: Task) {
        // 1. Cập nhật UI ngay lập tức (để mượt)
        val newStatus = if (task.status.lowercase() == "completed") "in progress" else "completed"
        val updatedTasks = _uiState.value.tasks.map {
            if (it.id == task.id) {
                it.copy(status = newStatus) // Trả về object Task mới
            } else {
                it
            }
        }
        // Cập nhật state với list mới
        _uiState.value = _uiState.value.copy(tasks = updatedTasks)

        // 2. Gọi API trong background
        viewModelScope.launch {
            try {
                val taskToUpdate = task.copy(status = newStatus)
                // Gọi API PUT (Giả định)
                RetrofitClient.apiService.updateTask(task.id, taskToUpdate)
                // API thành công, không cần làm gì vì UI đã update

            } catch (e: Exception) {
                // Nếu API lỗi, rollback lại UI
                _uiState.value = _uiState.value.copy(
                    tasks = _uiState.value.tasks.map {
                        if (it.id == task.id) task else it // Trả lại task gốc
                    },
                    error = "Failed to update task" // Báo lỗi
                )
            }
        }
    }
}