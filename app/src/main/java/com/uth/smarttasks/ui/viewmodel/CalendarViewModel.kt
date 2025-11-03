package com.uth.smarttasks.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uth.smarttasks.data.model.Task
import com.uth.smarttasks.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeParseException

// State cho UI Calendar (ĐÃ SỬA LẠI)
data class CalendarUiState(
    val isLoading: Boolean = false,
    // Key: Ngày, Value: List Task của ngày đó
    val scheduledTasks: Map<LocalDate, List<Task>> = emptyMap(),
    // List Task cho ngày ĐÃ CHỌN
    val selectedDateTasks: List<Task> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(), // Ngày đang chọn
    val error: String? = null
)

class CalendarViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        _uiState.value = CalendarUiState(isLoading = true)
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getTasks()
                if (response.isSuccess) {
                    val tasks = response.data

                    // Tự xử lý: Nhóm task theo ngày
                    val scheduledTasksMap = tasks
                        .mapNotNull { task ->
                            val date = parseDueDate(task.dueDate)
                            if (date != null) Pair(date, task) else null
                        }
                        .groupBy { it.first } // Nhóm theo Ngày
                        .mapValues { entry -> entry.value.map { it.second } } // Lấy List<Task>
                        .toSortedMap(compareBy { it }) // Sắp xếp

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        scheduledTasks = scheduledTasksMap
                    )

                    // Tải xong, tự động chọn ngày hôm nay
                    selectDate(LocalDate.now())

                } else {
                    _uiState.value = CalendarUiState(error = response.message)
                }
            } catch (e: Exception) {
                _uiState.value = CalendarUiState(error = e.message ?: "Unknown error")
            }
        }
    }

    // Hàm này được gọi từ UI khi user chọn 1 ngày
    fun selectDate(date: LocalDate) {
        // Lấy list task cho ngày đó từ cái map đã xử lý
        val tasksForDay = _uiState.value.scheduledTasks[date] ?: emptyList()

        _uiState.value = _uiState.value.copy(
            selectedDate = date,
            selectedDateTasks = tasksForDay
        )
    }

    // Hàm chuyển đổi String (2024-03-26T09:00:00Z) sang LocalDate
    private fun parseDueDate(dateString: String): LocalDate? {
        return try {
            OffsetDateTime.parse(dateString)
                .atZoneSameInstant(ZoneId.systemDefault()) // Chuyển sang múi giờ của máy
                .toLocalDate() // Lấy ngày
        } catch (e: DateTimeParseException) {
            null // Nếu parse lỗi
        }
    }
}