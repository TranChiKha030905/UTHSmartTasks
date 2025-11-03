package com.uth.smarttasks.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uth.smarttasks.data.model.Task
import com.uth.smarttasks.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Data class cho TOÀN BỘ số liệu
data class StatsData(
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val inProgressTasks: Int = 0,
    val pendingTasks: Int = 0,
    val byPriority: Map<String, Int> = emptyMap(),
    val byCategory: Map<String, Int> = emptyMap()
)

// State cho UI
data class StatsUiState(
    val isLoading: Boolean = false,
    val stats: StatsData = StatsData(),
    val error: String? = null
)

class StatisticsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadStats()
    }

    fun loadStats() {
        _uiState.value = StatsUiState(isLoading = true)

        viewModelScope.launch {
            try {
                // 1. Gọi API y hệt như TaskListViewModel
                val response = RetrofitClient.apiService.getTasks()
                if (response.isSuccess) {
                    // 2. Phân tích data phía client
                    val tasks = response.data
                    val stats = processTasks(tasks)
                    _uiState.value = StatsUiState(stats = stats)
                } else {
                    _uiState.value = StatsUiState(error = response.message)
                }
            } catch (e: Exception) {
                _uiState.value = StatsUiState(error = e.message ?: "Unknown error")
            }
        }
    }

    // Hàm phân tích "ra ngô ra khoai"
    private fun processTasks(tasks: List<Task>): StatsData {
        // Đếm theo Status
        val total = tasks.size
        val completed = tasks.count { it.status.equals("completed", ignoreCase = true) }
        val inProgress = tasks.count { it.status.equals("in progress", ignoreCase = true) }
        val pending = tasks.count { it.status.equals("pending", ignoreCase = true) }

        // Nhóm theo Priority (Ưu tiên)
        val byPriority = tasks
            .groupBy { it.priority.ifEmpty { "N/A" } } // Nhóm theo key
            .mapValues { it.value.size } // Đếm số lượng

        // Nhóm theo Category (Danh mục)
        val byCategory = tasks
            .groupBy { it.category.ifEmpty { "N/A" } }
            .mapValues { it.value.size }

        return StatsData(
            totalTasks = total,
            completedTasks = completed,
            inProgressTasks = inProgress,
            pendingTasks = pending,
            byPriority = byPriority,
            byCategory = byCategory
        )
    }
}