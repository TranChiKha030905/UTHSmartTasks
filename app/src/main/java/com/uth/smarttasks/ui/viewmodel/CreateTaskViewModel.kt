package com.uth.smarttasks.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uth.smarttasks.data.model.Task
import com.uth.smarttasks.data.repository.TaskRepository
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.util.UUID

data class CreateTaskUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val creationSuccess: Boolean = false
)

// Sửa Constructor
class CreateTaskViewModel(private val repository: TaskRepository) : ViewModel() {
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

        // Tạo một object Task mới
        val newTask = Task(
            id = UUID.randomUUID().toString(), // Tạo ID ngẫu nhiên
            title = title,
            description = description,
            category = category,
            priority = priority,
            dueDate = dueDate.ifEmpty { OffsetDateTime.now().toString() }, // Ngày tạm
            status = "Pending",
            subtasks = emptyList(),
            attachments = emptyList()
        )

        viewModelScope.launch { // Thêm launch
            repository.addTask(newTask) // Sửa logic
            _uiState.value = CreateTaskUiState(creationSuccess = true)
        }
    }
}