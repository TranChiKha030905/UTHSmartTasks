package com.uth.smarttasks.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uth.smarttasks.data.network.RetrofitClient
import kotlinx.coroutines.launch

// State cho màn hình Create Task
data class CreateTaskUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val creationSuccess: Boolean = false
)

class CreateTaskViewModel : ViewModel() {
    private val _uiState = mutableStateOf(CreateTaskUiState())
    val uiState: State<CreateTaskUiState> = _uiState

    fun createTask(
        title: String,
        description: String,
        category: String,
        priority: String,
        dueDate: String
    ) {
        _uiState.value = CreateTaskUiState(isLoading = true)

        // Tạo 1 Map (hoặc 1 data class) để gửi đi
        // Giả định API chỉ cần nhiêu đây, và status mặc định là "Pending"
        val taskData = mapOf(
            "title" to title,
            "description" to description,
            "category" to category,
            "priority" to priority,
            "dueDate" to dueDate,
            "status" to "Pending"
        )

        viewModelScope.launch {
            try {
                // Gọi API POST (Giả định)
                val response = RetrofitClient.apiService.createTask(taskData)
                if(response.isSuccess) {
                    _uiState.value = CreateTaskUiState(creationSuccess = true)
                } else {
                    _uiState.value = CreateTaskUiState(error = response.message)
                }
            } catch (e: Exception) {
                _uiState.value = CreateTaskUiState(error = e.message ?: "Unknown error")
            }
        }
    }
}