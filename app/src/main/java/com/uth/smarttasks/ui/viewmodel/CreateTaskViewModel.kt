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

// Constructor đã nhận Repository
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

        val newTask = Task(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            category = category,
            priority = priority,
            dueDate = dueDate.ifEmpty { OffsetDateTime.now().toString() },
            status = "Pending",
            subtasks = emptyList(),
            attachments = emptyList()
        )

        viewModelScope.launch {
            // Logic đã trỏ về Repository
            repository.addTask(newTask)
            _uiState.value = CreateTaskUiState(creationSuccess = true)
        }
    }
}