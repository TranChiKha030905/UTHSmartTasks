package com.uth.smarttasks.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uth.smarttasks.data.model.Task
import com.uth.smarttasks.data.network.RetrofitClient
import kotlinx.coroutines.launch

data class TaskDetailUiState(
    val isLoading: Boolean = false,
    val task: Task? = null,
    val error: String? = null,
    val deletionSuccess: Boolean = false
)

class TaskDetailViewModel : ViewModel() {
    private val _uiState = mutableStateOf(TaskDetailUiState())
    val uiState: State<TaskDetailUiState> = _uiState

    fun loadTaskDetail(taskId: String) {
        _uiState.value = TaskDetailUiState(isLoading = true)
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getTaskDetail(taskId)
                if (response.isSuccess) {
                    _uiState.value = TaskDetailUiState(task = response.data)
                } else {
                    _uiState.value = TaskDetailUiState(error = response.message)
                }
            } catch (e: Exception) {
                _uiState.value = TaskDetailUiState(error = e.message ?: "Unknown error")
            }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.deleteTask(taskId)
                if (response.isSuccess) {
                    // Cập nhật state để UI biết là đã xóa thành công
                    _uiState.value = _uiState.value.copy(deletionSuccess = true)
                } else {
                    // Cập nhật state với thông báo lỗi
                    _uiState.value = _uiState.value.copy(error = response.message)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message ?: "Unknown error")
            }
        }
    }
}