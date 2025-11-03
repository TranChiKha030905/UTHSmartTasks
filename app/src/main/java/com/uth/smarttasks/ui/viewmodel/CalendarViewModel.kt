package com.uth.smarttasks.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uth.smarttasks.data.model.Task
import com.uth.smarttasks.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeParseException

data class CalendarUiState(
    val isLoading: Boolean = false,
    val scheduledTasks: Map<LocalDate, List<Task>> = emptyMap(),
    val selectedDateTasks: List<Task> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val error: String? = null
)

// Sửa Constructor
class CalendarViewModel(private val repository: TaskRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            // Sửa logic: Lắng nghe từ Repository
            repository.getTasks().collectLatest { tasks ->

                val scheduledTasksMap = tasks
                    .mapNotNull { task ->
                        val date = parseDueDate(task.dueDate)
                        if (date != null) Pair(date, task) else null
                    }
                    .groupBy { it.first }
                    .mapValues { entry -> entry.value.map { it.second } }
                    .toSortedMap(compareBy { it })

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    scheduledTasks = scheduledTasksMap
                )

                // Cập nhật lại list cho ngày đang chọn (phòng trường hợp task bị xóa)
                selectDate(_uiState.value.selectedDate)
            }
        }
    }

    fun selectDate(date: LocalDate) {
        val tasksForDay = _uiState.value.scheduledTasks[date] ?: emptyList()
        _uiState.value = _uiState.value.copy(
            selectedDate = date,
            selectedDateTasks = tasksForDay
        )
    }

    private fun parseDueDate(dateString: String): LocalDate? {
        return try {
            OffsetDateTime.parse(dateString)
                .atZoneSameInstant(ZoneId.systemDefault())
                .toLocalDate()
        } catch (e: DateTimeParseException) {
            null
        }
    }
}